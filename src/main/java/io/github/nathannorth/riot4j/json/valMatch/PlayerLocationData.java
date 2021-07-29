package io.github.nathannorth.riot4j.json.valMatch;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutablePlayerLocationData.class)
@JsonDeserialize(as = ImmutablePlayerLocationData.class)
public interface PlayerLocationData {
    String puuid();
    Float viewRadians();
    LocationData location();
}
