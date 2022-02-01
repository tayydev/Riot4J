package io.github.nathannorth.riot4j.exceptions;

import reactor.netty.http.client.HttpClientResponse;

public class RetryableException extends WebFailure {

    public RetryableException(HttpClientResponse response, String content) {
        super(response, content);
    }

}
