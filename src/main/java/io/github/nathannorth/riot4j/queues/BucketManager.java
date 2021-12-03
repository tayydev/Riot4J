package io.github.nathannorth.riot4j.queues;

import io.github.nathannorth.riot4j.exceptions.RateLimitedException;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.HashMap;

public class BucketManager {
    private final HashMap<RateLimits, Bucket> buckets = new HashMap<>();
    private final Sinks.Many<Retryable> in = Sinks.many().multicast().onBackpressureBuffer(1024, true);

    public BucketManager() {
        in.asFlux()
                .flatMap(retryable -> useATry(retryable)
                                .doOnNext(result -> retryable.getResultHandle().emitValue(result, Sinks.EmitFailureHandler.FAIL_FAST))
                                .doOnNext(result -> retryable.getBucketHandle().emitValue(true, Sinks.EmitFailureHandler.FAIL_FAST))
                        , 1)
                .subscribe();
    }

    private Mono<String> useATry(Retryable retryable) {
        return retryable.getTry()
                .onErrorResume(error -> {
                    if(error instanceof RateLimitedException) {
                        RateLimitedException rateLimitedError = (RateLimitedException) error;

                        //global rate limit
                        if(!rateLimitedError.isMethod()) {
                            System.out.println("Hit GLOBAL rate limit! Delaying " + rateLimitedError.getSecs() + " seconds...");
                            return Mono.delay(Duration.ofSeconds(rateLimitedError.getSecs()))
                                    .flatMap(finished -> useATry(retryable));
                        }
                        //method rate limit
                        else {
                            System.out.println("Hit METHOD rate limit! Telling bucket to wait!");
                            retryable.getBucketHandle().emitError(rateLimitedError, Sinks.EmitFailureHandler.FAIL_FAST);
                            return Mono.empty(); //go on with our lives
                        }
                    }
                    return Mono.error(error); //some other error
                });
    }

    protected Mono<Boolean> push(Retryable r) {
        r.setBucketHandle(Sinks.one()); //reset bucket handle
        in.emitNext(r, Sinks.EmitFailureHandler.FAIL_FAST);
        return r.getBucketHandle().asMono();
    }

    public Mono<String> pushToBucket(RateLimits limit, HttpClient.ResponseReceiver<?> input) {
        Bucket bucket = buckets.computeIfAbsent(limit,
                key -> new Bucket(this, key)
        );
        return bucket.push(input);
    }
}
