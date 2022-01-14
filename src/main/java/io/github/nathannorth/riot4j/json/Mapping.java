package io.github.nathannorth.riot4j.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.github.nathannorth.riot4j.exceptions.JsonException;

import java.util.function.Function;

public class Mapping {
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new Jdk8Module())
            .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);
    public static <T> Function<String, T> map(Class<T> tClass) {
        return string -> {
            try {
                return mapper.readValue(string, tClass);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                throw new JsonException("Json didn't map correctly!");
            }
        };
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }
}
