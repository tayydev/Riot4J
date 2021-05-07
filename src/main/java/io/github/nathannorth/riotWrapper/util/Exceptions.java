package io.github.nathannorth.riotWrapper.util;

import reactor.netty.http.client.HttpClientResponse;

public class Exceptions {
    public static class JsonProblem extends RuntimeException {
        public JsonProblem(String s) {
            super(s);
        }
    }

    public static class IncompleteBuilderException extends RuntimeException {
        public IncompleteBuilderException(String s) {
            super(s);
        }
    }
    public static class InvalidTokenException extends RuntimeException {
        public InvalidTokenException(String s) {
            super(s);
        }
    }

    public static class WebFailure extends RuntimeException {
        private final HttpClientResponse response;

        public WebFailure(String in, HttpClientResponse response) {
            super("Error code: " + in);
            this.response = response;
        }

        public HttpClientResponse getResponse() {
            return response;
        }
    }
    public static class RateLimitedException extends WebFailure {
        private final int secs;
        public RateLimitedException(HttpClientResponse response, int secs) {
            super("Rate limit error", response);
            this.secs = secs;
        }

        public int getSecs() {
            return secs;
        }
    }
}
