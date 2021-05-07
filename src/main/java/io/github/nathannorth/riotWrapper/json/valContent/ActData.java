package io.github.nathannorth.riotWrapper.json.valContent;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@JsonSerialize(as = ImmutableActData.class)
@JsonDeserialize(as = ImmutableActData.class)
public interface ActData {
    String name();
    String type(); //undocumented
    Optional<LocalizedNamesData> localizedNames();
    String id();
    String parentId(); //undocumented
    Boolean isActive();
}