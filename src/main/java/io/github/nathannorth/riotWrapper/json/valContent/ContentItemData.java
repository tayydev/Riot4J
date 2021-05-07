package io.github.nathannorth.riotWrapper.json.valContent;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@JsonSerialize(as = ImmutableContentItemData.class)
@JsonDeserialize(as = ImmutableContentItemData.class)
public interface ContentItemData {
    String name();
    String id();
    String assetName();
    Optional<String> assetPath();
}
