package io.github.nathannorth.riot4j.objects;

import io.github.nathannorth.riot4j.enums.ValTeamId;
import io.github.nathannorth.riot4j.exceptions.MatchParseException;
import io.github.nathannorth.riot4j.json.valMatch.*;

import java.util.List;

public class ValMatch implements MatchData, Comparable<ValMatch> {
    private final MatchData data;

    public ValMatch(MatchData data) {
        this.data = data;
    }

    @Override
    public MatchInfoData matchInfo() {
        return data.matchInfo();
    }

    @Override
    public List<PlayerData> players() {
        return data.players();
    }

    @Override
    public List<CoachData> coaches() {
        return data.coaches();
    }

    @Override
    public List<TeamData> teams() {
        return data.teams();
    }

    @Override
    public List<RoundResultData> roundResults() {
        return data.roundResults();
    }

    public PlayerData getMatchMVP() {
        PlayerData mvp = null;
        int score = 0;
        for(PlayerData player: players()) {
            if(player.stats().isEmpty()) continue;
            if(player.stats().get().score() > score) {
                mvp = player;
                score = player.stats().get().score();
            }
        }
        if(mvp == null) throw new MatchParseException("No MVP found!");
        return mvp;
    }

    public PlayerData getTeamMVP(ValTeamId teamId) {
        PlayerData mvp = null;
        int score = 0;
        for(PlayerData player: players()) {
            if(player.stats().isEmpty() || !player.teamId().equals(teamId)) continue;
            if(player.stats().get().score() > score) {
                mvp = player;
                score = player.stats().get().score();
            }
        }
        if(mvp == null) throw new MatchParseException("No MVP found!");
        return mvp;
    }

    public ValTeamId winningTeam() {
        for(TeamData t: teams()) {
            if(t.won()) {
                return t.teamId();
            }
        }
        throw new MatchParseException("No winning team!");
    }

    public boolean isWinFor(String puuid) {
        return getPlayer(puuid).teamId().equals(winningTeam());
    }

    public PlayerData getPlayer(String puuid) {
        PlayerData returnable = null;
        for(PlayerData player: players()) {
            if(player.puuid().equals(puuid)) returnable = player;
        }
        if(returnable == null) throw new MatchParseException("Player not found!");
        return returnable;
    }

    public StatisticalValPlayer getStatisticalPlayer(String puuid) {
        PlayerData player = getPlayer(puuid);

        if(player.stats().isEmpty()) throw new MatchParseException("Could not create StatisticalValPlayer. PlayerData has no stats.");

        /**
         * Counts headshots. Note: we do not filter shotguns at this time because there isn't a definite way to
         * know, per-shot, whether it was done with a shotgun. Kill objects make the distinction between guns
         * but *damage* objects, which are a more accurate measure of headshots, do not specify guns
         */
        int headShots = 0;
        int totalShots = 0;
        for(RoundResultData round: roundResults()) {
            PlayerRoundStatsData stats = getPlayerRound(round, puuid);
            for(DamageData fight: stats.damage()) {
                headShots += fight.headshots();
                totalShots += fight.headshots() + fight.bodyshots() + fight.legshots();
            }
        }

        return new StatisticalValPlayer(player, player.stats().get().score() / roundResults().size(), (float) headShots / totalShots);
    }

    private static PlayerRoundStatsData getPlayerRound(RoundResultData round, String puuid) {
        for(PlayerRoundStatsData player: round.playerStats()) {
            if(player.puuid().equals(puuid)) return player;
        }
        throw new MatchParseException("Player not found!");
    }

    public String scoreLine(ValTeamId teamId) {
        int rounds = 0;
        for(RoundResultData round: roundResults()) {
            if(round.winningTeam().equals(teamId)) rounds++;
        }
        return rounds + ":" + (roundResults().size() - rounds);
    }

    /**
     * Sorts matches descending by time
     * @param o match to compare to
     * @return
     */
    @Override
    public int compareTo(ValMatch o) {
        if(o.matchInfo().gameStartMillis() > matchInfo().gameLengthMillis()) {
            return -1;
        }
        else {
            return 1;
        }
    }
}