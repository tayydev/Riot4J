package io.github.nathannorth.riotWrapper.util;

import io.github.nathannorth.riotWrapper.json.ResponseCode;

public class Exceptions {
    public static class JsonProblem extends RuntimeException {
        public JsonProblem(String s) {
            super(s);
        }
    }
    public static class WebFailure extends RuntimeException {
        private final ResponseCode data;

        public WebFailure(ResponseCode f) {
            super("Web related exception with error code: " + f.status().status_code());
            data = f;
        }

        public ResponseCode getData() {
            return data;
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
}
