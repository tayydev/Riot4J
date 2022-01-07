package io.github.nathannorth.riot4j.json.riotAccount;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.nathannorth.riot4j.enums.ValRegion;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableActiveShardData.class)
@JsonDeserialize(as = ImmutableActiveShardData.class)
public interface ActiveShardData {
    String puuid();
    String game();
    ValRegion activeShard();
}
