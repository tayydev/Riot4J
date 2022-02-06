package io.github.nathannorth.riot4j.exceptions;

import reactor.netty.http.client.HttpClientResponse;

import java.util.Optional;

public class RetryableException extends RuntimeException {

    //todo this is really stupid but makes it easier to instantiate

    private final Optional<HttpClientResponse> response;
    private final Optional<String> content;

    public RetryableException(HttpClientResponse response, String content) {
        this.response = Optional.of(response);
        this.content = Optional.of(content);
    }

    public RetryableException() {
        response = Optional.empty();
        content = Optional.empty();
    }

}
