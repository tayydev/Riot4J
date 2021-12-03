package io.github.nathannorth.riot4j.queues;

import io.github.nathannorth.riot4j.exceptions.Exceptions;
import io.github.nathannorth.riot4j.exceptions.RateLimitedException;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientResponse;

import java.time.Duration;

public class LegacyQueue {
    private final Sinks.Many<Request> in = Sinks.many().multicast().onBackpressureBuffer(1024, false);

    public LegacyQueue() {
        in.asFlux()
                .flatMap(request -> evaluate(request), 1)
                .subscribe();
    }

    //push new item to the queue
    public Mono<String> push(HttpClient.ResponseReceiver<?> input) {
        Request r = new Request(input);
        in.emitNext(r, Sinks.EmitFailureHandler.FAIL_FAST);
        return r.response.asMono();
    }

    //processes a request. May create/handle errors
    private Mono<Void> evaluate(Request r) {
        return r.input.responseSingle(((response, byteBufMono) ->
                        handleResponseCodes(response, byteBufMono.asString())))
                .doOnNext(result -> r.response.emitValue(result, Sinks.EmitFailureHandler.FAIL_FAST))
                .then()
                .onErrorResume(error -> {
                    if(error instanceof RateLimitedException) {
                        System.out.println("Hit rate limit... delaying: " + ((RateLimitedException) error).getSecs() + " seconds");
                        System.out.println(((RateLimitedException) error).getResponse().responseHeaders());
                        return Mono.delay(Duration.ofSeconds(((RateLimitedException) error).getSecs()))
                                .flatMap(finished -> evaluate(r)); //try again
                    }
                    else return Mono.error(error);
                });
    }

    //turns response codes into appropriate errors
    private Mono<String> handleResponseCodes(HttpClientResponse response, Mono<String> contentMono) {
        //no errors
        if(response.status().code() / 100 == 2) return contentMono;
        //yes errors
        return contentMono
                .switchIfEmpty(Mono.just("")) //make sure we don't eat errors w/out body
                .flatMap(content -> Mono.error(Exceptions.ofWebFailure(response, content)));
    }

    public static class Request {
        final HttpClient.ResponseReceiver<?> input;
        final Sinks.One<String> response = Sinks.one();
        public Request(HttpClient.ResponseReceiver<?> input) {
            this.input = input;
        }
    }
}
