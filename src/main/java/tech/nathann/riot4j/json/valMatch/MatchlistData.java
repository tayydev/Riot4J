package tech.nathann.riot4j.json.valMatch;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutableMatchlistData.class)
@JsonDeserialize(as = ImmutableMatchlistData.class)
public interface MatchlistData {
    String puuid();
    List<MatchlistEntryData> history();
}
