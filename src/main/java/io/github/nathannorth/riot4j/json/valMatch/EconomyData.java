package io.github.nathannorth.riot4j.json.valMatch;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableEconomyData.class)
@JsonDeserialize(as = ImmutableEconomyData.class)
public interface EconomyData {
    int loadoutValue();
    String weapon();
    String armor();
    int remaining();
    int spent();
}
