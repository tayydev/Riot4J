package io.github.nathannorth.riot4j.json.valMatch;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableDamageData.class)
@JsonDeserialize(as = ImmutableDamageData.class)
public interface DamageData {
    String receiver();
    int damage();
    int legshots();
    int bodyshots();
    int headshots();
}
