package io.github.nathannorth.riot4j.json.valMatch;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutablePlayerData.class)
@JsonDeserialize(as = ImmutablePlayerData.class)
public interface PlayerData {
    String puuid();
    String gameName();
    String tagLine();
    String teamId();
    String partyId();
    String characterId();
    PlayerStatsData stats();
    int competitiveTier();
    String playerCard();
    String playerTitle();
}
