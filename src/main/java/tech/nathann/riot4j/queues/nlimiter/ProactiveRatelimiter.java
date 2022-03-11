package tech.nathann.riot4j.queues.nlimiter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.netty.http.client.HttpClient;
import tech.nathann.riot4j.enums.regions.Region;
import tech.nathann.riot4j.queues.FailureStrategies;
import tech.nathann.riot4j.queues.RateLimits;
import tech.nathann.riot4j.queues.Ratelimiter;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProactiveRatelimiter implements Ratelimiter {
    private static final Logger log = LoggerFactory.getLogger(ProactiveRatelimiter.class);

    private final Map<RateLimits, Map<Region, Dispenser>> buckets;
    private final Sinks.Many<TicketedRequest> ingest = Sinks.many().unicast().onBackpressureBuffer();

    public ProactiveRatelimiter(RateLimits masterLimit, RateLimits secondaryLimit, List<RateLimits> respectedLimits) {
        Dispenser master = new Dispenser(masterLimit);
        Dispenser secondary = new Dispenser(secondaryLimit);

        this.buckets = new HashMap<>();
        for(RateLimits limit: respectedLimits) {
            buckets.put(limit, new HashMap<>());
        }

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

    public Mono<String> push(RateLimits limit, Region region, HttpClient.ResponseReceiver<?> input) {
        return Mono.defer(() -> {
                    Dispenser bucket = buckets.get(limit) //get map<region, bucket>
                            .computeIfAbsent(region, key -> new Dispenser(limit)); //get actual bucket
                    Request request = new Request(input);
                    TicketedRequest ticketed = new TicketedRequest(request, this, bucket);
                    ingest.emitNext(ticketed, FailureStrategies.RETRY_ON_SERIALIZED);
                    return request.getResponse();
                }
        );
    }

    //when a bucket needs to retry it needs the SAME ticketed request to go through so that the delay is respected
    public Mono<String> pushRetry(TicketedRequest retry) {
        return Mono.defer(() -> {
            ingest.emitNext(retry, FailureStrategies.RETRY_ON_SERIALIZED);
            return retry.getRequest().getResponse();
        });
    }

    private Instant future = Instant.EPOCH;
    public void limit(Duration time) {
        log.info("Ratelimiter got limit " + time);
        future = Instant.now().plus(time);
    }
}
