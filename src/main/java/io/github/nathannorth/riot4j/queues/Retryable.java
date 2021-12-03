package io.github.nathannorth.riot4j.queues;

import io.github.nathannorth.riot4j.exceptions.Exceptions;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.netty.http.client.HttpClient;

public class Retryable {
    private final HttpClient.ResponseReceiver<?> httpRequest;
    private final Sinks.One<String> resultHandle = Sinks.one();
    private Sinks.One<Boolean> bucketHandle = Sinks.one();

    public Retryable(HttpClient.ResponseReceiver<?> httpRequest) {
        this.httpRequest = httpRequest;
    }

    public Sinks.One<String> getResultHandle() {
        return resultHandle;
    }

    public Sinks.One<Boolean> getBucketHandle() {
        return bucketHandle;
    }

    public void setBucketHandle(Sinks.One<Boolean> bucketHandle) {
        this.bucketHandle = bucketHandle;
    }

    //get an actionable http request for this retryable
    public Mono<String> getTry() {
        return httpRequest.responseSingle(((response, byteBufMono) -> {
            Mono<String> contentMono = byteBufMono.asString();
            //no errors
            if(response.status().code() / 100 == 2) return contentMono;
            //yes errors
            return contentMono
                    .switchIfEmpty(Mono.just("")) //make sure we don't eat errors w/out body
                    .flatMap(content -> { //save body of the error
                        return Mono.error(Exceptions.ofWebFailure(response, content));
                    });
        }));
    }
}
