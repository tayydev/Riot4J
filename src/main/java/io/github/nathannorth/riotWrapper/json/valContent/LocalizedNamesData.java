package io.github.nathannorth.riotWrapper.json.valContent;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableLocalizedNamesData.class)
@JsonDeserialize(as = ImmutableLocalizedNamesData.class)
public interface LocalizedNamesData {
    //todo if i hate myself
}
