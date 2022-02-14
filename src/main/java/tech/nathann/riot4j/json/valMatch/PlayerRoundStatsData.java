package tech.nathann.riot4j.json.valMatch;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutablePlayerRoundStatsData.class)
@JsonDeserialize(as = ImmutablePlayerRoundStatsData.class)
public interface PlayerRoundStatsData {
    String puuid();
    List<KillData> kills();
    List<DamageData> damage();
    int score();
    EconomyData economy();
    AbilityData ability();
}
