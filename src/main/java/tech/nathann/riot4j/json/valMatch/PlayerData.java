package tech.nathann.riot4j.json.valMatch;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import tech.nathann.riot4j.objects.ValTeamId;

import java.util.Optional;

@Value.Immutable
@JsonSerialize(as = ImmutablePlayerData.class)
@JsonDeserialize(as = ImmutablePlayerData.class)
public interface PlayerData {
    String puuid();
    String gameName();
    String tagLine();
    ValTeamId teamId();
    String partyId();
    Optional<String> characterId(); //not provided for spectators
    Optional<PlayerStatsData> stats(); //not provided for spectators
    int competitiveTier();
    String playerCard();
    String playerTitle();

    Optional<Boolean> isObserver();
}
