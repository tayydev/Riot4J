package tech.nathann.riot4j.json.valMatch;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableLocationData.class)
@JsonDeserialize(as = ImmutableLocationData.class)
public interface LocationData {
    int x();
    int y();
}
