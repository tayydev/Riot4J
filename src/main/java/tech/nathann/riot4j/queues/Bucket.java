package tech.nathann.riot4j.queues;

import tech.nathann.riot4j.exceptions.RateLimitedException;
import tech.nathann.riot4j.exceptions.RetryableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * A bucket represents a per-method (per-endpoint) rate limit that can be hit without clogging up the global limit.
 * Every bucket must be tied to a master bucket that prevents concurrent 429s from being created
 */
public class Bucket {
    //per-bucket queue
    private final Sinks.Many<Retryable> in = Sinks.many().multicast().onBackpressureBuffer(1024, false);

    //ref to master
    private final BucketManager master;

    //enum for logging
    private final RateLimits limit;

    private Logger log = LoggerFactory.getLogger(Bucket.class);

    public Bucket(BucketManager master, RateLimits limit) {
        this.master = master;
        this.limit = limit;

        //we just use tries (with concurrency of 1) until the day we die
        in.asFlux()
                .flatMap(retryable -> useATry(retryable), 1)
                .subscribe();

        log.info("Bucket of limit " + limit.name() + " subscription started");
    }

    //a recursive function that pushes a retryable to the master queue, then waits to see if there is issue. If master
    // queue emits error then we wait and re-push to master
    private Mono<Boolean> useATry(Retryable retryable) {
        return master.push(retryable)
                .onErrorResume(RateLimitedException.class, error -> {
                    log.warn("Bucket " + limit.name() + " got METHOD rate limit from master! Delaying " + error.getSecs() + " seconds");
                    return Mono.delay(Duration.ofSeconds(error.getSecs()))
                            .flatMap(completion -> useATry(retryable));
                })
                .onErrorResume(RetryableException.class, error -> {
                    int time = retryable.makeRetry();
                    if(time == -1) {
                        log.error("Retried MAX amount " + retryable.getRetryCount() + " of times for " + limit.name() + ". Dropping...");
                        return Mono.empty();
                    }
                    log.warn("Bucket " + limit.name() + " got a retryable error! Delaying " + time + " seconds. This is attempt " + retryable.getRetryCount() +  " for this request");
                    return Mono.delay(Duration.ofSeconds(time))
                            .flatMap(completion -> useATry(retryable));
                });
    }

    //push something to this bucket. generates a new retryable, emits it internally, and returns a mono that will eventually fulfill request
    Mono<String> push(HttpClient.ResponseReceiver<?> input) {
        log.debug("Bucket " + limit + " got input");
        Retryable r = new Retryable(input, limit);
        in.emitNext(r, FailureStrategies.RETRY_ON_SERIALIZED);
        return r.getResultHandle().asMono();
    }
}
