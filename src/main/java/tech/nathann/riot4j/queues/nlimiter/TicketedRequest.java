package tech.nathann.riot4j.queues.nlimiter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import tech.nathann.riot4j.exceptions.RateLimitedException;
import tech.nathann.riot4j.exceptions.RetryableException;
import tech.nathann.riot4j.queues.FailureStrategies;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class TicketedRequest {
    private static final Logger log = LoggerFactory.getLogger(TicketedRequest.class);

    private final Request request;
    private final ProactiveRatelimiter master;
    private final Dispenser bucket;
    private final Sinks.One<Instant> lock = Sinks.one();

    public TicketedRequest(Request request, ProactiveRatelimiter master, Dispenser bucket) {
        this.request = request;
        this.master = master;
        this.bucket = bucket;
    }

    /**
     * We release the rate limit immediately before the web request, this has the benefit of being more agressive, but
     * the drawback that future tries have to get their own ratelimit ticket
     */
    public Mono<String> getTry() {
        lock.emitValue(Instant.now(), FailureStrategies.RETRY_ON_SERIALIZED); //release rate limit
        return request.getRequest()
                .onErrorResume(RateLimitedException.class, rate -> {
                    Duration length = Duration.ofSeconds(rate.getSecs());
                    log.error(bucket.getLimit() + " GOT RATE LIMIT, DELAYING " + length);
                    master.limit(length);
                    return Mono.delay(length)
                            .flatMap(fin -> getRetry()); //delay then retry
                })
                .onErrorResume(RetryableException.class, retry -> {
                    int count = retryCount.incrementAndGet();
                    if(count > 10) { //give up
                        log.error("Retried MAX amount " + count + " of times. Dropping...");
                        return Mono.error(retry);
                    }
                    Duration length = Duration.ofSeconds(retryTime(count));
                    log.warn("Bucket got a retryable error! Delaying " + length + ". This is attempt " + count +  " for this request");
                    return Mono.delay(length)
                            .flatMap(fin -> getRetry());
                })
                .doOnNext(fin -> request.getCallback().emitValue(fin, FailureStrategies.RETRY_ON_SERIALIZED))
                .onErrorResume(throwable -> {
                    log.warn("Error passing through " + bucket.getLimit() +  " bucket: " + throwable.toString());
                    request.getCallback().emitError(throwable, FailureStrategies.RETRY_ON_SERIALIZED);
                    return Mono.empty();
                });
    }

    private Mono<String> getRetry() {
        return master.push(bucket.getLimit(), request.getRaw());
    }

    public Mono<Instant> getLock() {
        return lock.asMono();
    }

    private final AtomicInteger retryCount = new AtomicInteger(0);
    private int retryTime(int source) {
        return (int) Math.pow(source, 2);
    }

    public Dispenser getBucket() {
        return bucket;
    }
}
