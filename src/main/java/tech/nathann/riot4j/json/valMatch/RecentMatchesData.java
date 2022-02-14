package tech.nathann.riot4j.json.valMatch;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutableRecentMatchesData.class)
@JsonDeserialize(as = ImmutableRecentMatchesData.class)
public interface RecentMatchesData {
    long currentTime();
    List<String> matchIds();
}
