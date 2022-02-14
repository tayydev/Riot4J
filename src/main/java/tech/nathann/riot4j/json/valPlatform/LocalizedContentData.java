package tech.nathann.riot4j.json.valPlatform;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * wraps a ContentDto
 */
@Value.Immutable
@JsonSerialize(as = ImmutableLocalizedContentData.class)
@JsonDeserialize(as = ImmutableLocalizedContentData.class)
public interface LocalizedContentData {
    String locale();
    String content();
}
