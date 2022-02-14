package tech.nathann.riot4j.json.valMatch;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutableMatchData.class)
@JsonDeserialize(as = ImmutableMatchData.class)
public interface MatchData {
    MatchInfoData matchInfo();
    List<PlayerData> players();
    List<CoachData> coaches();
    List<TeamData> teams();
    List<RoundResultData> roundResults();
}
