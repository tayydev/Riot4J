package io.github.nathannorth.riot4j.json.valPlatform;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

//wraps a PlatformDataDto

@Value.Immutable
@JsonSerialize(as = ImmutablePlatformStatusData.class)
@JsonDeserialize(as = ImmutablePlatformStatusData.class)
public interface PlatformStatusData {
    String id();
    String name();
    List<String> locales();
    List<PlatformEventData> maintenances();
    List<PlatformEventData> incidents();
}
