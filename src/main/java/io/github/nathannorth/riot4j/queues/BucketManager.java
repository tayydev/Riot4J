package io.github.nathannorth.riot4j.queues;

import io.github.nathannorth.riot4j.exceptions.RateLimitedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.HashMap;

/**
 * A bucket manager handles all the actual web requests of its owned buckets, and provides a convenience map so outside methods can push to buckets
 */
public class BucketManager {
    //map using enum for organization
    private final HashMap<RateLimits, Bucket> buckets = new HashMap<>();
    private final Sinks.Many<Retryable> in = Sinks.many().multicast().onBackpressureBuffer(1024, true);

    private Logger log = LoggerFactory.getLogger(BucketManager.class);

    public BucketManager() {
        //use tries till the day we die. We attach two emissions to successful tries
        in.asFlux()
                .doOnNext(retryable -> log.debug("Retryable of limit " + retryable.getRateLimit() + " passing through BucketManager"))
                .flatMap(retryable -> useATry(retryable)
                                //whenever a try makes it out of the method, we emit the result to the output mono and also tell the queue that it can start sending stuff again
                                .doOnNext(e -> log.debug("Retryable completed in BucketManager"))
                                .doOnNext(result -> retryable.getResultHandle().emitValue(result, Sinks.EmitFailureHandler.FAIL_FAST))
                                .doOnNext(result -> retryable.getBucketHandle().emitValue(true, Sinks.EmitFailureHandler.FAIL_FAST))
                                //handle errors
                                .onErrorResume(throwable -> {
                                    log.debug("Error passing through bucket " + throwable.toString());
                                    retryable.getResultHandle().emitError(throwable, Sinks.EmitFailureHandler.FAIL_FAST); //output error to client
                                    retryable.getBucketHandle().emitValue(true, Sinks.EmitFailureHandler.FAIL_FAST); //an error is a success from the perspective of a bucket
                                    return Mono.empty();
                                })
                        , 1)
                .subscribe();

        log.info("BucketManager subscription started");
    }

    //recursive function to try making http requests
    private Mono<String> useATry(Retryable retryable) {
        return retryable.getTry() //try http request
                .onErrorResume(error -> {
                    //catch rate limits
                    if(error instanceof RateLimitedException) {
                        RateLimitedException rateLimitedError = (RateLimitedException) error;

                        //global rate limit
                        if(!rateLimitedError.isMethod()) {
                            log.warn("BucketManager hit GLOBAL rate limit! Delaying " + rateLimitedError.getSecs() + " seconds");
                            return Mono.delay(Duration.ofSeconds(rateLimitedError.getSecs()))
                                    .flatMap(finished -> useATry(retryable)); //try again after failure
                        }
                        //method rate limit
                        else {
                            log.info("BucketManager hit METHOD rate limit! Telling bucket to wait");
                            retryable.getBucketHandle().emitError(rateLimitedError, Sinks.EmitFailureHandler.FAIL_FAST);
                            return Mono.empty(); //go on with our lives
                        }
                    }
                    log.warn("Error passed through BucketMaster: " + error.toString());
                    return Mono.error(error); //some other error
                });
    }

    //when a bucket pushes to the manager we need to reset the handle it waits on, and then emit to our queue
    protected Mono<Boolean> push(Retryable r) {
        r.setBucketHandle(Sinks.one()); //reset bucket handle
        in.emitNext(r, Sinks.EmitFailureHandler.FAIL_FAST);
        return r.getBucketHandle().asMono();
    }

    /**
     * i have no clue what im doing but i think that having a single very boring scheduler means that random bucket
     * actions don't get different threads. I want to avoid this because sinks don't like to recieve input from multiple places
     * but they're kind of a tsundere about it because sometimes they are ok with it and if you define a custom emit strategy
     * they might just be chilling but everyone talking about custom emit strategies is all like 'well if everyone behaves this will work'
     * and im like bro how do i know if my code is going to behave its a bunch of letters not a dog
     */
    private final Scheduler scheduler = Schedulers.newSingle("Buckets");

    //public method to access teh manager. Allows a user to push to a bucket given a key enum. Creates buckets as necessary.
    public Mono<String> pushToBucket(RateLimits limit, HttpClient.ResponseReceiver<?> input) {
        return Mono.defer(() -> { //defer to prevent accidental early subscription
            log.debug("Input pushed to bucket " + limit);
            Bucket bucket = buckets.computeIfAbsent(limit,
                    key -> new Bucket(this, key)
            );
            return bucket.push(input);
        }).subscribeOn(scheduler);
    }
}
