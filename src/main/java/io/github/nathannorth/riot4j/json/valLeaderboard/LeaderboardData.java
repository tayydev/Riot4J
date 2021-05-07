package io.github.nathannorth.riot4j.json.valLeaderboard;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

//wraps a LeaderboardDto

@Value.Immutable
@JsonSerialize(as = ImmutableLeaderboardData.class)
@JsonDeserialize(as = ImmutableLeaderboardData.class)
public interface LeaderboardData {
    String actId();
    List<LeaderboardPlayerData> players();
    long totalPlayers();
    int immortalStartingPage();
    int topTierRRThreshold();
    String shard();
}
