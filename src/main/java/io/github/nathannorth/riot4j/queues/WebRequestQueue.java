package io.github.nathannorth.riot4j.queues;

import io.github.nathannorth.riot4j.util.Exceptions;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientResponse;

import java.time.Duration;

public class WebRequestQueue extends GenericQueue<HttpClient.ResponseReceiver<?>, String> {

    @Override
    Mono<String> evaluate(HttpClient.ResponseReceiver<?> input) {
        return input.responseSingle(((response, byteBufMono) ->
                        handleResponseCodes(response, byteBufMono.asString())))
                .onErrorResume(error -> {
                    if(error instanceof Exceptions.RateLimitedException) {
                        System.out.println("Hit rate limit... delaying: " + ((Exceptions.RateLimitedException) error).getSecs() + " seconds");
                        return Mono.delay(Duration.ofSeconds(((Exceptions.RateLimitedException) error).getSecs()))
                                .flatMap(finished -> evaluate(input)); //try again
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
