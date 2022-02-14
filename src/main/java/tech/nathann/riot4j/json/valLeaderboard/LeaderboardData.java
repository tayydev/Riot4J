package tech.nathann.riot4j.json.valLeaderboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

/**
 * wraps a LeaderboardDto
 */
@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true) //todo figure out what "TierDetails" are
@JsonSerialize(as = ImmutableLeaderboardData.class)
@JsonDeserialize(as = ImmutableLeaderboardData.class)
public interface LeaderboardData {
    String actId();
    List<LeaderboardPlayerData> players();
    long totalPlayers();
    int immortalStartingIndex();
    int immortalStartingPage();
    int topTierRRThreshold();
    String shard();
}
