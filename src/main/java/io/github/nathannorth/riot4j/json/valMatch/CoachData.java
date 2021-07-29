package io.github.nathannorth.riot4j.json.valMatch;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableCoachData.class)
@JsonDeserialize(as = ImmutableCoachData.class)
public interface CoachData {
    String puuid();
    String teamId();
}
