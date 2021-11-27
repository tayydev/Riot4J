package io.github.nathannorth.riot4j.util;

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
        private final String content;

        public WebFailure(HttpClientResponse response, String content) {
            super("Error code: " + response.status().code());
            this.response = response;
            this.content = content;
        }

        public HttpClientResponse getResponse() {
            return response;
        }
        public String getContent() {
            return content;
        }

        @Override
        public String toString() {
            return "WebFailure{" +
                    "response=" + response +
                    ", content='" + content + '\'' +
                    "} " + super.toString();
        }
    }
    public static class RateLimitedException extends WebFailure {
        private final int secs;
        public RateLimitedException(HttpClientResponse response, String content, int secs) {
            super(response, content);
            this.secs = secs;
        }

        public int getSecs() {
            return secs;
        }
    }
}
