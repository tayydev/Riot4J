package tech.nathann.riot4j.json.valMatch;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableAbilityCastsData.class)
@JsonDeserialize(as = ImmutableAbilityCastsData.class)
public interface AbilityCastsData {
    int grenadeCasts();
    int ability1Casts();
    int ability2Casts();
    int ultimateCasts();
}
