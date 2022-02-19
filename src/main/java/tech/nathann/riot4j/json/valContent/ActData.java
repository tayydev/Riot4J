package tech.nathann.riot4j.json.valContent;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import tech.nathann.riot4j.objects.ValActId;

/**
 * wraps an ActDto
 */
@Value.Immutable
@JsonSerialize(as = ImmutableActData.class)
@JsonDeserialize(as = ImmutableActData.class)
public interface ActData {
    String name();
    String type(); //undocumented
    ValActId id();
    ValActId parentId(); //undocumented
    Boolean isActive();
}