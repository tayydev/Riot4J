package tech.nathann.riot4j.api.match;

import tech.nathann.riot4j.enums.ValTeamId;
import tech.nathann.riot4j.json.valMatch.PlayerData;
import tech.nathann.riot4j.json.valMatch.PlayerStatsData;

import java.util.Optional;

public class StatisticalValPlayer implements PlayerData, Comparable<StatisticalValPlayer> {
    private final PlayerData data;

    private final int combatScore;
    private final int totalShots;
    private final int headshots;

    public StatisticalValPlayer(PlayerData data, int combatScore, int totalShots, int headshots) {
        this.data = data;
        this.combatScore = combatScore;
        this.totalShots = totalShots;
        this.headshots = headshots;
    }

    public int getCombatScore() {
        return combatScore;
    }

    public float getHeadShotPercentage() {
        return (float) headshots / totalShots;
    }

    public String getKDA() {
        PlayerStatsData stats = data.stats().get();
        return stats.kills() + "/" + stats.deaths() + "/" + stats.assists();
    }

    public int getTotalShots() {
        return totalShots;
    }

    public int getHeadshots() {
        return headshots;
    }

    @Override
    public int compareTo(StatisticalValPlayer o) {
        return o.combatScore - this.combatScore;
    }

    @Override
    public String puuid() {
        return data.puuid();
    }

    @Override
    public String gameName() {
        return data.gameName();
    }

    @Override
    public String tagLine() {
        return data.tagLine();
    }

    @Override
    public ValTeamId teamId() {
        return data.teamId();
    }

    @Override
    public String partyId() {
        return data.partyId();
    }

    @Override
    public Optional<String> characterId() { //todo character id should be a Character object (linked to translator)
        return data.characterId();
    }

    @Override
    public Optional<PlayerStatsData> stats() {
        return data.stats();
    }

    @Override
    public int competitiveTier() {
        return data.competitiveTier();
    }

    @Override
    public String playerCard() {
        return data.playerCard();
    }

    @Override
    public String playerTitle() {
        return data.playerTitle();
    }
}
