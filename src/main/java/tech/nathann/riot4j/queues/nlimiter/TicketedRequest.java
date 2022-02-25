package tech.nathann.riot4j.queues.nlimiter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import tech.nathann.riot4j.exceptions.RateLimitedException;
import tech.nathann.riot4j.exceptions.RetryableException;

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

    public Mono<String> getTry() {
        return request.getRequest()
                .onErrorResume(RateLimitedException.class, rate -> {
                    Duration length = Duration.ofSeconds(rate.getSecs());
                    log.error("GOT RATE LIMIT, DELAYING " + length);
                    master.limit(length);
                    return Mono.delay(length).flatMap(fin -> getTry()); //delay then retry
                })
                .onErrorResume(RetryableException.class, retry -> {
                    retryCount++;
                    if(retryCount > 10) { //give up
                        log.error("Retried MAX amount " + retryCount + " of times. Dropping...");
                        return Mono.error(retry);
                    }
                    Duration length = Duration.ofSeconds(retryTime());
                    log.warn("Bucket got a retryable error! Delaying " + length + ". This is attempt " + retryCount +  " for this request");
                    return Mono.delay(length)
                            .flatMap(fin -> getTry());
                })
                .doOnEach(any -> lock.emitValue(Instant.now(), Sinks.EmitFailureHandler.FAIL_FAST)) //release rate limit
                .doOnNext(fin -> request.getCallback().emitValue(fin, Sinks.EmitFailureHandler.FAIL_FAST))
                .doOnError(err -> request.getCallback().emitError(err, Sinks.EmitFailureHandler.FAIL_FAST));
    }

    public Mono<Instant> getLock() {
        return lock.asMono();
    }

    private int retryCount = 0;
    private int retryTime() {
        return (int) Math.pow(retryCount, 2);
    }

    public Dispenser getBucket() {
        return bucket;
    }
}
