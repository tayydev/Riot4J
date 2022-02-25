package tech.nathann.riot4j.queues.nlimiter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.netty.http.client.HttpClient;
import tech.nathann.riot4j.queues.FailureStrategies;
import tech.nathann.riot4j.queues.RateLimits;
import tech.nathann.riot4j.queues.Ratelimiter;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public class ProactiveRatelimiter implements Ratelimiter {
    private static final Logger log = LoggerFactory.getLogger(ProactiveRatelimiter.class);

    private final Dispenser master;
    private final Dispenser secondary;
    private final Map<RateLimits, Dispenser> buckets;
    private final Sinks.Many<TicketedRequest> ingest = Sinks.many().unicast().onBackpressureBuffer();

    public ProactiveRatelimiter(Dispenser master, Dispenser secondary, Map<RateLimits, Dispenser> buckets) {
        this.master = master;
        this.secondary = secondary;
        this.buckets = buckets;

        ingest.asFlux()
                .flatMap(request -> request.getBucket().pushTicket(request))//buckets
                .flatMap(request -> master.pushTicket(request)) //masterA
                .concatMap(request -> secondary.pushTicket(request)) //masterB
                .concatMap(request -> delayRecur(request)) //stopper when we hit real ratelimit
                .flatMap(request -> request.getTry()) //evaluate values
                .subscribe();
    }

    //block all requests on ratelimit
    private Mono<TicketedRequest> delayRecur(TicketedRequest request) {
        if(Instant.now().isBefore(future)) {
            log.info("Delaying requests in ratelimiter");
            return Mono.delay(Duration.between(Instant.now(), future))
                    .flatMap(fin -> delayRecur(request));
        }
        return Mono.just(request);
    }

    public Mono<String> push(RateLimits limit, HttpClient.ResponseReceiver<?> input) {
        Dispenser bucket = buckets.get(limit);
        Request request = new Request(input);
        TicketedRequest ticketed = new TicketedRequest(request, this, bucket);
        ingest.emitNext(ticketed, FailureStrategies.RETRY_ON_SERIALIZED);
        return request.getResponse();
    }

    private Instant future = Instant.EPOCH;
    public void limit(Duration time) {
        log.info("Ratelimiter got limit " + time);
        future = Instant.now().plus(time);
    }
}
