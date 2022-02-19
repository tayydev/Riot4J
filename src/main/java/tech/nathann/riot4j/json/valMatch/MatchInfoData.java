package tech.nathann.riot4j.json.valMatch;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import tech.nathann.riot4j.enums.ValGameMode;
import tech.nathann.riot4j.enums.ValQueueId;
import tech.nathann.riot4j.objects.ValActId;

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
    ValQueueId queueId(); //empty is custom
    ValGameMode gameMode();
    boolean isRanked();
    ValActId seasonId();
}