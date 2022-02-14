package tech.nathann.riot4j.json.riotAccount;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import tech.nathann.riot4j.enums.RiotGame;
import tech.nathann.riot4j.enums.ValRegion;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableActiveShardData.class)
@JsonDeserialize(as = ImmutableActiveShardData.class)
public interface ActiveShardData {
    String puuid();
    RiotGame game();
    ValRegion activeShard();
}
