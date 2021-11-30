package io.github.nathannorth.riot4j.queues;

import io.github.nathannorth.riot4j.util.Exceptions;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientResponse;

import java.time.Duration;

public class ProactiveQueue {
    private final Sinks.Many<Request> in = Sinks.many().multicast().onBackpressureBuffer(1024, false);

    public ProactiveQueue() {
        in.asFlux().flatMap(request ->
                evaluate(request)
                        .onErrorResume(error -> {
                            if(error instanceof Exceptions.RateLimitedException) {
                                System.out.println("Hit rate limit... delaying: " + ((Exceptions.RateLimitedException) error).getSecs() + " seconds");
                                return Mono.delay(Duration.ofSeconds(((Exceptions.RateLimitedException) error).getSecs()))
                                        .flatMap(finished -> evaluate(request)); //try again
                            }
                            else return Mono.error(error);
                        }), 1).subscribe();
    }

    //push new item to the queue
    public Mono<String> push(HttpClient.ResponseReceiver<?> input) {
        Request r = new Request(input);
        in.emitNext(r, Sinks.EmitFailureHandler.FAIL_FAST);
        return r.response.asMono();
    }

    //processes a request. May create, but does not handle, errors
    private Mono<Void> evaluate(Request r) {
        return r.input.responseSingle(((response, byteBufMono) ->
                        handleResponseCodes(response, byteBufMono.asString())))
                .doOnNext(result -> r.response.emitValue(result, Sinks.EmitFailureHandler.FAIL_FAST))
                .then();
    }

    //turns response codes into appropriate errors
    private Mono<String> handleResponseCodes(HttpClientResponse response, Mono<String> contentMono) {
        //no errors
        if(response.status().code() / 100 == 2) return contentMono;
        //yes errors
        return contentMono
                .switchIfEmpty(Mono.just("")) //make sure we don't eat errors w/out body
                .flatMap(content -> { //save body of the error
                    if(response.status().code() ==  429) { //rate limited error
                        return Mono.error(new Exceptions.RateLimitedException(response, content,
                                Integer.parseInt(response.responseHeaders().get("Retry-After"))));
                    }
                    else { //some other error
                        return Mono.error(new Exceptions.WebFailure(response, content));
                    }
                });
    }
}
class Request {
    final HttpClient.ResponseReceiver<?> input;
    final Sinks.One<String> response = Sinks.one();

    public Request(HttpClient.ResponseReceiver<?> input) {
        this.input = input;
    }
}