package tech.nathann.riot4j.json.valMatch;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import tech.nathann.riot4j.enums.ValQueueId;
import tech.nathann.riot4j.enums.ValTeamId;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@JsonSerialize(as = ImmutableMatchlistEntryData.class)
@JsonDeserialize(as = ImmutableMatchlistEntryData.class)
public interface MatchlistEntryData {
    String matchId();
    ValQueueId queueId();
    long gameStartTimeMillis();
    Optional<ValTeamId> teamId();
}
