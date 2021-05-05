package io.github.nathannorth.riotWrapper.util;

import io.github.nathannorth.riotWrapper.json.FailureData;

public class Exceptions {
    public static class JsonProblem extends RuntimeException {
        public JsonProblem(String s) {
            super(s);
        }
    }
    public static class WebFailure extends RuntimeException {
        private final FailureData data;

        public WebFailure(FailureData f) {
            data = f;
        }

        public FailureData getData() {
            return data;
        }
    }
}
