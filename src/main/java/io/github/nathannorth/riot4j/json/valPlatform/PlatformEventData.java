package io.github.nathannorth.riot4j.json.valPlatform;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

//wraps a StatusDto

@Value.Immutable
@JsonSerialize(as = ImmutablePlatformEventData.class)
@JsonDeserialize(as = ImmutablePlatformEventData.class)
public interface PlatformEventData {
    int id();
    String maintenance_status();
    String incident_severity();
    List<LocalizedContentData> titles();
    List<UpdateData> updates();
    String created_at();
    String archive_at();
    String updated_at();
    List<String> platforms();
}
