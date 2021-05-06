package io.github.nathannorth.riotWrapper.util;

public class Exceptions {
    public static class JsonProblem extends RuntimeException {
        public JsonProblem(String s) {
            super(s);
        }
    }
    public static class WebFailure extends RuntimeException {
        private final int code;

        public WebFailure(int code) {
            super("Error code: " + code);
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    public static class IncompleteClientException extends RuntimeException {
        public IncompleteClientException(String s) {
            super(s);
        }
    }

    public static class InvalidTokenException extends RuntimeException {
        public InvalidTokenException(String s) {
            super(s);
        }
    }

    public static class RateLimitedException extends RuntimeException {
        private final int secs;
        public RateLimitedException(int secs) {
            this.secs = secs;
        }

        public int getSecs() {
            return secs;
        }
    }
}
