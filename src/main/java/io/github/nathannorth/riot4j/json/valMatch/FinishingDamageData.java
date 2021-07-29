package io.github.nathannorth.riot4j.json.valMatch;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableFinishingDamageData.class)
@JsonDeserialize(as = ImmutableFinishingDamageData.class)
public interface FinishingDamageData {
    String damageType();
    String damageItem();
    boolean isSecondaryFireMode();
}
