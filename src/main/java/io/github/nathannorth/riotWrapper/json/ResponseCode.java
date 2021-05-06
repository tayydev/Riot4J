package io.github.nathannorth.riotWrapper.json;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableResponseCode.class)
@JsonDeserialize(as = ImmutableResponseCode.class)
public interface ResponseCode {
    Data status();

    @Value.Immutable
    @JsonSerialize(as = ImmutableData.class)
    @JsonDeserialize(as = ImmutableData.class)
    public static interface Data {
        String message();
        int status_code();
    }
}
