package tech.nathann.riot4j.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;

public class JsonException extends RuntimeException {
    public JsonException(JsonProcessingException source) {
        super(source);
    }
}
