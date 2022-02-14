package tech.nathann.riot4j.json.valMatch;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import tech.nathann.riot4j.enums.ValTeamId;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableCoachData.class)
@JsonDeserialize(as = ImmutableCoachData.class)
public interface CoachData {
    String puuid();
    ValTeamId teamId();
}
