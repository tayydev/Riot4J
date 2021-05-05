package io.github.nathannorth.riotWrapper.json;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableFailureData.class)
@JsonDeserialize(as = ImmutableFailureData.class)
public interface FailureData {
    Data status();

    @Value.Immutable
    @JsonSerialize(as = ImmutableData.class)
    @JsonDeserialize(as = ImmutableData.class)
    public static interface Data {
        String message();
        int status_code();
    }
}
