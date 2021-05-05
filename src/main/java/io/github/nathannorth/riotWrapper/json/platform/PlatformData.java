package io.github.nathannorth.riotWrapper.json.platform;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutablePlatformData.class)
@JsonDeserialize(as = ImmutablePlatformData.class)
public interface PlatformData {
    String id();
    String name();
    List<String> locales();
    List<StatusData> maintenances();
    List<StatusData> incidents();
}
