package io.github.nathannorth.riot4j.queues;

import io.github.nathannorth.riot4j.exceptions.RateLimitedException;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

public class Bucket {
    private final Sinks.Many<Retryable> in = Sinks.many().multicast().onBackpressureBuffer(1024, false);

    private final BucketManager master;

    private final RateLimits limit;

    public Bucket(BucketManager master, RateLimits limit) {
        this.master = master;
        this.limit = limit;

        in.asFlux()
                .flatMap(retryable -> useATry(retryable), 1)
                .subscribe();
    }

    private Mono<Boolean> useATry(Retryable retryable) {
        return master.push(retryable)
                .onErrorResume(e -> {
                    RateLimitedException rateLimitedException = (RateLimitedException) e; //we will only ever get this exception out of this sink
                    System.out.println("Got METHOD rate limit in bucket " + limit.name() + " from master! Delaying " + rateLimitedException.getSecs() + " seconds!");
                    return Mono.delay(Duration.ofSeconds(rateLimitedException.getSecs()))
                            .flatMap(finished -> useATry(retryable));
                });
    }

    public Mono<String> push(HttpClient.ResponseReceiver<?> input) {
        Retryable r = new Retryable(input);
        in.emitNext(r, Sinks.EmitFailureHandler.FAIL_FAST);
        return r.getResultHandle().asMono();
    }
}
