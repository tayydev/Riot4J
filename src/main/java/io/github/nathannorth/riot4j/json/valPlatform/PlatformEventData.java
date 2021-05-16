package io.github.nathannorth.riot4j.json.valPlatform;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

/**
 * wraps a StatusDto
 */
@Value.Immutable
@JsonSerialize(as = ImmutablePlatformEventData.class)
@JsonDeserialize(as = ImmutablePlatformEventData.class)
public interface PlatformEventData {
    int id();
    Optional<String> maintenance_status(); //if its a patch, this exists
    Optional<String> incident_severity(); //if its an incident, this exists
    List<LocalizedContentData> titles();
    List<UpdateData> updates();
    String created_at();
    Optional<String> archive_at();
    Optional<String> updated_at();
    List<String> platforms();
}
