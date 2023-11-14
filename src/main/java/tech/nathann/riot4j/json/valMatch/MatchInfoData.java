package tech.nathann.riot4j.json.valMatch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import tech.nathann.riot4j.enums.ValGameMode;
import tech.nathann.riot4j.enums.ValQueueId;
import tech.nathann.riot4j.objects.ValActId;

import java.util.Optional;

@Value.Immutable
@JsonSerialize(as = ImmutableMatchInfoData.class)
@JsonDeserialize(as = ImmutableMatchInfoData.class)
@JsonIgnoreProperties(ignoreUnknown = true) //TODO read premier info
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
    Optional<String> gameVersion();
    Optional<String> region();
    Optional<String> gameLoopZone();
}
