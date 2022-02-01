package io.github.nathannorth.riot4j.queues;

import io.github.nathannorth.riot4j.exceptions.WebFailure;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.netty.http.client.HttpClient;

import java.nio.charset.StandardCharsets;

public class Retryable {

    private final HttpClient.ResponseReceiver<?> httpRequest;
    private final Sinks.One<String> resultHandle = Sinks.one();
    private Sinks.One<Boolean> bucketHandle = Sinks.one();

    private int retryCount = 0;
    private final RateLimits rateLimit;

    public Retryable(HttpClient.ResponseReceiver<?> httpRequest, RateLimits rateLimit) {
        this.httpRequest = httpRequest;
        this.rateLimit = rateLimit;
    }

    public RateLimits getRateLimit() {
        return rateLimit;
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

    public int getRetryCount() {
        return retryCount;
    }

    public int makeRetry() { //todo this is very stateful which is maybe cringe?
        retryCount++;
        if(retryCount > 10) return -1; //cap of ten retries
        else return (int) Math.pow(retryCount, 2); //exponential backoff
    }

    //get an actionable http request for this retryable
    public Mono<String> getTry() {
        return httpRequest.responseSingle(((response, byteBufMono) -> {
            Mono<String> contentMono = byteBufMono.asString(StandardCharsets.UTF_8); //manually set charset just in case
            //no errors
            if(response.status().code() / 100 == 2) return contentMono;
            //yes errors
            return contentMono
                    .switchIfEmpty(Mono.just("")) //make sure we don't eat errors w/out body
                    .flatMap(content -> { //save body of the error
                        return Mono.error(WebFailure.of(response, content));
                    });
        }));
    }
}
