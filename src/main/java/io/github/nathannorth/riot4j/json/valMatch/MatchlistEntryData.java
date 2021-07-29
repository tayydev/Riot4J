package io.github.nathannorth.riot4j.json.valMatch;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableMatchlistEntryData.class)
@JsonDeserialize(as = ImmutableMatchlistEntryData.class)
public interface MatchlistEntryData {
    String matchId();
    long gameStartTimeMillis();
    String teamId();
}
