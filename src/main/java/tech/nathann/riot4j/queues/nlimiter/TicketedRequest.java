package tech.nathann.riot4j.queues.nlimiter;

import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import tech.nathann.riot4j.exceptions.RateLimitedException;
import tech.nathann.riot4j.exceptions.RetryableException;
import tech.nathann.riot4j.queues.FailureStrategies;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeoutException;

public class TicketedRequest {
    private static final Logger log = LoggerFactory.getLogger(TicketedRequest.class);

    private final Request request;
    private final ProactiveRatelimiter master;
    private final Dispenser bucket;
    private final Sinks.One<Instant> lock = Sinks.one();
    private final int retries;

    public TicketedRequest(Request request, ProactiveRatelimiter master, Dispenser bucket, int retries) {
        this.request = request;
        this.master = master;
        this.bucket = bucket;
        this.retries = retries;
    }

    public TicketedRequest(Request request, ProactiveRatelimiter master, Dispenser bucket) {
        this(request, master, bucket, 0);
    }

    /**
     * We release the rate limit immediately after the web request, this has the benefit of being more aggressive, but
     * the drawback that future tries have to get their own rate-limit ticket
     */
    public Mono<String> getTry() {
        if(isDispose) {
            log.info("Pre-disposing value.");
            request.getCallback().emitError(new TimeoutException(), FailureStrategies.RETRY_ON_SERIALIZED);
            return Mono.empty();
        }

        return request.getRequest()
                .doOnCancel(() -> {
                    log.debug("Cancelled request in bucket " + bucket);
                })
                .doOnSubscribe(sub -> subscription = sub) //todo cringe
                .doOnEach(any -> lock.emitValue(Instant.now(), FailureStrategies.RETRY_ON_SERIALIZED)) //no matter what we release lock AFTER value emitted
                .onErrorResume(RateLimitedException.class, rate -> {
                    Duration length = Duration.ofSeconds(rate.getSecs());
                    log.error(bucket.getLimit() + " GOT RATE LIMIT, DELAYING " + length);
                    master.limit(length);
                    return Mono.delay(length) //todo technically unnecessary since we lock master but ehhhh
                            .flatMap(fin -> getRetry()); //delay then retry todo   this is sus
                })
                .onErrorResume(RetryableException.class, retry -> {
                    if(retries > 7) { //give up
                        log.error("Retried MAX amount " + retries + " of times in: " + bucket);
                        return Mono.error(retry);
                    }
                    Duration length = Duration.ofSeconds(retryTime()); //should be 1 2 4 8 16 32 64 etc
                    log.warn("Bucket got a retryable error! Delaying " + length + ". This is attempt " + retries +  " for this request");
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

    private Subscription subscription = null;
    private boolean isDispose = false;

    public void dispose() {
        log.debug("Disposing subscription!");
        lock.emitValue(Instant.now(), FailureStrategies.RETRY_ON_SERIALIZED);
        isDispose = true;
        if(subscription == null) {
            log.warn("No subscription found!");
        } else {
            subscription.cancel();
        }
    }

    public Mono<String> getResponse() {
        return request.getCallback().asMono();
    }

    private Mono<String> getRetry() {
        return master.pushTicket(new TicketedRequest(request, master, bucket, retries + 1));
    }

    public Mono<Instant> getLock() {
        return Mono.firstWithValue(
                lock.asMono(),
                Mono.delay(Duration.ofMinutes(5)).map(fin -> {
                    log.error("LOCK TIMED OUT");
                    return Instant.now();
                })
        );
    }

    private int retryTime() {
        return (int) Math.pow(2, retries);
    }

    public Dispenser getBucket() {
        return bucket;
    }

    public Request getRequest() {
        return request;
    }

    @Override
    public String toString() {
        return "TicketedRequest{" +
                "request=" + request +
                ", bucket=" + bucket +
                ", retries=" + retries +
                ", subscription=" + subscription +
                ", isDispose=" + isDispose +
                '}';
    }
}
