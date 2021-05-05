package io.github.nathannorth.riotWrapper.json.platform;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableContentData.class)
@JsonDeserialize(as = ImmutableContentData.class)
public interface ContentData {
    String locale();
    String content();
}
