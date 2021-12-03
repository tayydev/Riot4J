package io.github.nathannorth.riot4j.exceptions;

import reactor.netty.http.client.HttpClientResponse;

public class Exceptions {

    public static WebFailure ofWebFailure(HttpClientResponse response, String content) {
        if(response.status().code() == 429) {
            return new RateLimitedException(response, content);
        }
        return new WebFailure(response, content);
    }

}
