package tech.nathann.riot4j.queues.old;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.netty.http.client.HttpClient;
import tech.nathann.riot4j.exceptions.RateLimitedException;
import tech.nathann.riot4j.exceptions.RetryableException;
import tech.nathann.riot4j.queues.FailureStrategies;
import tech.nathann.riot4j.queues.RateLimits;
import tech.nathann.riot4j.queues.Ratelimiter;

import java.time.Duration;
import java.util.HashMap;

/**
 * A bucket manager handles all the actual web requests of its owned buckets, and provides a convenience map so outside methods can push to buckets
 */
public class BucketManager implements Ratelimiter {
    //map using enum for organization
    private final HashMap<RateLimits, Bucket> buckets = new HashMap<>();
    private final Sinks.Many<Retryable> in = Sinks.many().unicast().onBackpressureBuffer();

    private final Logger log = LoggerFactory.getLogger(BucketManager.class);

    public BucketManager() {
        //use tries till the day we die. We attach two emissions to successful tries
        in.asFlux()
                .doOnNext(retryable -> log.debug("Retryable of limit " + retryable.getRateLimit() + " passing through BucketManager"))
                .flatMap(retryable -> useATry(retryable)
                                //whenever a try makes it out of the method, we emit the result to the output mono and also tell the queue that it can start sending stuff again
                                .doOnNext(e -> log.debug("Retryable completed in BucketManager"))
                                .doOnNext(result -> retryable.getResultHandle().emitValue(result, FailureStrategies.RETRY_ON_SERIALIZED))
                                .doOnNext(result -> retryable.getBucketHandle().emitValue(true, FailureStrategies.RETRY_ON_SERIALIZED))
                                //handle errors
                                .onErrorResume(throwable -> {
                                    log.debug("Error passing through bucket " + throwable.toString());
                                    retryable.getResultHandle().emitError(throwable, FailureStrategies.RETRY_ON_SERIALIZED); //output error to client
                                    retryable.getBucketHandle().emitValue(true, FailureStrategies.RETRY_ON_SERIALIZED); //an error is a success from the perspective of a bucket
                                    return Mono.empty();
                                })
                        , 1)
                .subscribe();

        log.info("BucketManager subscription started");
    }

    //recursive function to try making http requests
    private Mono<String> useATry(Retryable retryable) {
        return retryable.getTry() //try http request
                .onErrorResume(RateLimitedException.class, error -> {
                    //global rate limit
                    if(!error.isMethod()) {
                        log.warn("BucketManager hit GLOBAL rate limit! Delaying " + error.getSecs() + " seconds");
                        return Mono.delay(Duration.ofSeconds(error.getSecs()))
                                .flatMap(finished -> useATry(retryable)); //try again after failure
                    }
                    //method rate limit
                    else {
                        log.info("BucketManager hit METHOD rate limit! Telling bucket to wait");
                        retryable.getBucketHandle().emitError(error, FailureStrategies.RETRY_ON_SERIALIZED);
                        return Mono.empty(); //go on with our lives
                    }
                })
                //500 series error
                .onErrorResume(RetryableException.class, error -> {
                    log.warn("BucketManager hit RETRYABLE error! Telling bucket to retry.");
                    retryable.getBucketHandle().emitError(error, FailureStrategies.RETRY_ON_SERIALIZED);
                    return Mono.empty();
                })
                .doOnError(error -> log.warn("Error passed through BucketMaster: " + error.toString()));
    }

    //when a bucket pushes to the manager we need to reset the handle it waits on, and then emit to our queue
    protected Mono<Boolean> push(Retryable r) {
        r.setBucketHandle(Sinks.one()); //reset bucket handle todo flux instead?
        in.emitNext(r, FailureStrategies.RETRY_ON_SERIALIZED);
        return r.getBucketHandle().asMono();
    }

    //public method to access teh manager. Allows a user to push to a bucket given a key enum. Creates buckets as necessary.
    public Mono<String> push(RateLimits limit, HttpClient.ResponseReceiver<?> input) {
        return Mono.defer(() -> { //defer to prevent accidental early subscription
            log.debug("Input pushed to bucket " + limit);
            Bucket bucket = buckets.computeIfAbsent(limit,
                    key -> new Bucket(this, key)
            );
            return bucket.push(input);
        });
    }
}
