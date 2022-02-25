package tech.nathann.riot4j.queues;

import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

public interface Ratelimiter {
    Mono<String> push(RateLimits limit, HttpClient.ResponseReceiver<?> input);
}
