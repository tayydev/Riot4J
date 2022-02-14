package tech.nathann.riot4j.json.valMatch;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import tech.nathann.riot4j.enums.ValTeamId;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableTeamData.class)
@JsonDeserialize(as = ImmutableTeamData.class)
public interface TeamData {
    ValTeamId teamId();
    boolean won();
    int roundsPlayed();
    int roundsWon();
    int numPoints();
}
