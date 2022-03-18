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
        return request.getRequest()
                .doOnEach(any -> lock.emitValue(Instant.now(), FailureStrategies.RETRY_ON_SERIALIZED)) //no matter what we release lock AFTER value emitted
                .onErrorResume(RateLimitedException.class, rate -> {
                    Duration length = Duration.ofSeconds(rate.getSecs());
                    log.error(bucket.getLimit() + " GOT RATE LIMIT, DELAYING " + length);
                    master.limit(length);
                    return Mono.delay(length) //todo technically unnecessary since we lock master but ehhhh
                            .flatMap(fin -> getRetry()); //delay then retry
                })
                .onErrorResume(RetryableException.class, retry -> {
                    if(retryCount > 7) { //give up
                        log.error("Retried MAX amount " + retryCount + " of times. Dropping...");
                        return Mono.error(retry);
                    }
                    Duration length = Duration.ofSeconds(retryTime()); //should be 1 2 4 8 16 32 64 etc
                    retryCount++;
                    log.warn("Bucket got a retryable error! Delaying " + length + ". This is attempt " + retryCount +  " for this request");
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
        return master.pushRetry(this);
    }

    public Mono<Instant> getLock() {
        return lock.asMono();
    }

    private int retryCount = 0;
    private int retryTime() {
        return (int) Math.pow(2, retryCount);
    }

    public Dispenser getBucket() {
        return bucket;
    }

    public Request getRequest() {
        return request;
    }
}
