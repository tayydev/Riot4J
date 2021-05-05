package io.github.nathannorth.riotWrapper.json.platform;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutableStatusData.class)
@JsonDeserialize(as = ImmutableStatusData.class)
public interface StatusData {
    int id();
    String maintenance_status();
    String incident_severity();
    List<ContentData> titles();
    List<UpdateData> updates();
    String created_at();
    String archive_at();
    String updated_at();
    List<String> platforms();
}
