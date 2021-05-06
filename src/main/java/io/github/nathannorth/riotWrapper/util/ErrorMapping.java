package io.github.nathannorth.riotWrapper.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.nathannorth.riotWrapper.json.ResponseCode;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public class ErrorMapping {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> Function<String, Mono<T>> map(Class<T> c) {
        return data -> {
            try {
                return Mono.just(mapper.readValue(data, c));
            } catch (JsonProcessingException e) {
                try {
                    ResponseCode err = mapper.readValue(data, ResponseCode.class);
                    return Mono.error(new Exceptions.WebFailure(err));
                } catch (JsonProcessingException ee) {
                    throw new Exceptions.JsonProblem("No parsable error");
                }
            }
        };
    }
}
