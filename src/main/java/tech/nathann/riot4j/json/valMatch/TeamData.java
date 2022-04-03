package tech.nathann.riot4j.json.valMatch;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import tech.nathann.riot4j.objects.ValTeamId;

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
