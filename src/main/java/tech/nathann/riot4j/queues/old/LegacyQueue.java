package tech.nathann.riot4j.queues.old;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.netty.http.client.HttpClient;
import tech.nathann.riot4j.exceptions.RateLimitedException;
import tech.nathann.riot4j.exceptions.RetryableException;
import tech.nathann.riot4j.queues.FailureStrategies;
import tech.nathann.riot4j.queues.nlimiter.Request;

import java.time.Duration;

public class LegacyQueue {
    private final Sinks.Many<Request> in = Sinks.many().multicast().onBackpressureBuffer(1024, false);

    private Logger log = LoggerFactory.getLogger(LegacyQueue.class);

    public LegacyQueue() {
        in.asFlux()
                .flatMap(request -> evaluate(request), 1)
                .subscribe();

        log.info("Legacy queue subscription started");
    }

    //push new item to the queue
    public Mono<String> push(HttpClient.ResponseReceiver<?> input) {
        return Mono.defer(() -> {
                    Request r = new Request(input);
                    in.emitNext(r, FailureStrategies.RETRY_ON_SERIALIZED);
                    return r.getCallback().asMono();
                });
    }

    private Mono<String> evaluate(Request r, int attempt) {
        return evaluate(r, 0);
    }

    //processes a request. May create/handle errors
    private Mono<String> evaluate(Request r, int attempt) {
        return r.getRequest()
                .doOnNext(result -> r.getCallback().emitValue(result, FailureStrategies.RETRY_ON_SERIALIZED))
                .onErrorResume(RateLimitedException.class, ratelimit -> {
                    log.warn("Hit rate limit... delaying: " + ratelimit.getSecs() + " seconds");
                    return Mono.delay(Duration.ofSeconds(ratelimit.getSecs()))
                            .flatMap(finished -> evaluate(r)); //try again
                })
                .onErrorResume(RetryableException.class, retry -> {
                    log.warn("Hit retryable request... delaying: 1 second. This is attempt " + attempt);

                    if(attempt > 9) {
                        log.error("Hit retryable max amount of times: " + retry);
                        r.getCallback().emitError(retry);
                        return Mono.empty();
                    }

                    return Mono.delay(Duration.ofSeconds(1))
                            .flatMap(finished -> evaluate(r, attempt + 1)); //try again
                })
                .onErrorResume(other -> {
                    log.error("Error passing through legacy queue " + other);
                    r.getCallback().emitError(other, FailureStrategies.RETRY_ON_SERIALIZED);
                    return Mono.empty();
                });
    }
}
