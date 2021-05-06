package io.github.nathannorth.riotWrapper.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufMono;
import reactor.netty.http.client.HttpClientResponse;

import java.util.function.BiFunction;

public class ErrorMapping {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> BiFunction<HttpClientResponse, ByteBufMono, Mono<T>> map(Class<T> c) {
        return (response, body) ->
        {
            if(response.status().code() == 403) return Mono.error(new Exceptions.WebFailure(403));
            else return body.asString().map(str -> {
                try {
                    return mapper.readValue(str, c);
                } catch (JsonProcessingException e) {
                    throw new Exceptions.JsonProblem(e.getMessage());
                }
            });
        };
    }
}
