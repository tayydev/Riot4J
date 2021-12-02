package io.github.nathannorth.riot4j.queues;

import io.github.nathannorth.riot4j.util.Exceptions;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientResponse;

import java.util.Optional;

public class Retryable {
    private final HttpClient.ResponseReceiver<?> httpRequest;
    private Mono<String> result = Mono.empty();

    public Retryable(HttpClient.ResponseReceiver<?> httpRequest) {
        this.httpRequest = httpRequest;
    }

    public Mono<String> getTry() {
        return httpRequest.responseSingle(((response, byteBufMono) ->
                handleResponseCodes(response, byteBufMono.asString())));
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
