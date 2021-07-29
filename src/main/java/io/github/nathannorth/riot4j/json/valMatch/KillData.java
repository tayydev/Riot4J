package io.github.nathannorth.riot4j.json.valMatch;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutableKillData.class)
@JsonDeserialize(as = ImmutableKillData.class)
public interface KillData {
    int timeSinceGameStartMillis();
    int timeSinceRoundStartMillis();
    String killer();
    String victim();
    LocationData victimLocation();
    List<String> assistants();
    List<PlayerLocationData> playerLocations();
    FinishingDamageData finishingDamage();
}
