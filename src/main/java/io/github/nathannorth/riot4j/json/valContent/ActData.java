package io.github.nathannorth.riot4j.json.valContent;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

//wraps a ActDto

@Value.Immutable
@JsonSerialize(as = ImmutableActData.class)
@JsonDeserialize(as = ImmutableActData.class)
public interface ActData {
    String name();
    String type(); //undocumented
    String id();
    String parentId(); //undocumented
    Boolean isActive();
}