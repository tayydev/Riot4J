package tech.nathann.riot4j.json.valMatch;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@JsonSerialize(as = ImmutableAbilityData.class)
@JsonDeserialize(as = ImmutableAbilityData.class)
public interface AbilityData {
    Optional<String> grenadeEffects();
    Optional<String> ability1Effects();
    Optional<String> ability2Effects();
    Optional<String> ultimateEffects();
}
