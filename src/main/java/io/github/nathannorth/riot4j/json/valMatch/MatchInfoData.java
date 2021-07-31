package io.github.nathannorth.riot4j.json.valMatch;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableMatchInfoData.class)
@JsonDeserialize(as = ImmutableMatchInfoData.class)
public interface MatchInfoData {
    String matchId();
    String mapId();
    int gameLengthMillis();
    long gameStartMillis();
    String provisioningFlowId();
    boolean isCompleted();
    String customGameName();
    String queueId(); //empty is custom
    String gameMode();
    boolean isRanked();
    String seasonId();
}
