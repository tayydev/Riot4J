package tech.nathann.riot4j.queues;

import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import tech.nathann.riot4j.enums.regions.Region;

public interface Ratelimiter {
    Mono<String> push(RateLimits limit, Region region, HttpClient.ResponseReceiver<?> input);
}
