package tech.nathann.riot4j.api.match;

import tech.nathann.riot4j.enums.ValGameMode;
import tech.nathann.riot4j.enums.ValQueueId;
import tech.nathann.riot4j.enums.ValRoundResult;
import tech.nathann.riot4j.enums.ValTeamId;
import tech.nathann.riot4j.exceptions.MatchParseException;
import tech.nathann.riot4j.json.valMatch.*;

import java.util.*;

public class ValMatch implements Comparable<ValMatch> {

    private final MatchData data;

    public ValMatch(MatchData data) {
        this.data = data;
    }

    public MatchData getData() {
        return data;
    }

    public MatchInfoData matchInfo() {
        return data.matchInfo();
    }

    public List<PlayerData> players() {
        return data.players();
    }

    public List<CoachData> coaches() {
        return data.coaches();
    }

    public List<TeamData> teams() {
        return data.teams();
    }

    public List<RoundResultData> roundResults() {
        return data.roundResults();
    }

    public int numDefuses(ValTeamId team) {
        int returnable = 0;
        for(RoundResultData round: roundResults()) {
            if(round.winningTeam().equals(team) && round.roundResultCode().equals(ValRoundResult.BOMB_DEFUSED)) returnable ++;
        }
        return returnable;
    }

    public int numPlants(ValTeamId team) {
        int returnable = 0;
        for(RoundResultData round: roundResults()) {
            if(round.bombPlanter().isPresent() && getPlayer(round.bombPlanter().get()).teamId().equals(team)) returnable++;
        }
        return returnable;
    }

    //number of round excluding surrenders
    public int numRoundsNoSurrenders() {
        int returnable = 0;
        for(RoundResultData round: roundResults()) {
            if(!round.roundResultCode().equals(ValRoundResult.SURRENDERED)) {
                returnable++;
            }
        }
        return Math.max(returnable, 1); //avoid divide by zero by returning 1
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

    public Optional<ValTeamId> winningTeam() {
        for(TeamData t: teams()) {
            if(t.won()) {
                return Optional.of(t.teamId());
            }
        }
        return Optional.empty();
    }

    public boolean isWinFor(String puuid) {
        if(winningTeam().isEmpty()) return false; // it isn't a win if you tie
        return getPlayer(puuid).teamId().equals(winningTeam().get());
    }

    public PlayerData getPlayer(String puuid) {
        PlayerData returnable = null;
        for(PlayerData player: players()) {
            if(player.puuid().equals(puuid)) returnable = player;
        }
        if(returnable == null) throw new MatchParseException("Player not found!");
        return returnable;
    }

    //todo maybe make optional and remove public access to the playerData object? maybe make a coach object and have this return either or?
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

        return new StatisticalValPlayer(player, player.stats().get().score() / numRoundsNoSurrenders(), totalShots, headShots);
    }

    private static PlayerRoundStatsData getPlayerRound(RoundResultData round, String puuid) {
        for(PlayerRoundStatsData player: round.playerStats()) {
            if(player.puuid().equals(puuid)) return player;
        }
        throw new MatchParseException("Player not found!");
    }

    //returns a ValQueueId that represents what GAME MODE is being played eg. if it's a custom deathmatch then it returns deathmatch *instead* of custom
    public ValQueueId gameModeAsQueue() { //todo gamemode should be an object also double check this can't be handled api side
        String mode = matchInfo().gameMode().toString();
        if(mode.equals("/Game/GameModes/Bomb/BombGameMode.BombGameMode_C")) return ValQueueId.UNRATED;
        if(mode.equals("/Game/GameModes/Deathmatch/DeathmatchGameMode.DeathmatchGameMode_C")) return ValQueueId.DEATHMATCH;
        if(mode.equals("/Game/GameModes/GunGame/GunGameTeamsGameMode.GunGameTeamsGameMode_C")) return ValQueueId.ESCALATION;
        if(mode.equals("/Game/GameModes/OneForAll/OneForAll_GameMode.OneForAll_GameMode_C")) return ValQueueId.REPLICATION;
        if(mode.equals("/Game/GameModes/QuickBomb/QuickBombGameMode.QuickBombGameMode_C")) return ValQueueId.SPIKE_RUSH;
        if(mode.equals("/Game/GameModes/SnowballFight/SnowballFightGameMode.SnowballFightGameMode_C")) return ValQueueId.SNOWBALL_FIGHT;
        throw new MatchParseException("No matching game mode found!");
    }

    public String getGameTypeHuman() {
        StringJoiner joiner = new StringJoiner(" ");

        if(matchInfo().queueId().equals(ValQueueId.CUSTOM))
            joiner.add("Custom");

        if(matchInfo().queueId().equals(ValQueueId.UNRATED)) joiner.add("Unrated");
        if(matchInfo().queueId().equals(ValQueueId.COMPETITIVE)) joiner.add("Competitive");

        ValGameMode mode = matchInfo().gameMode();
        if(!mode.equals(ValGameMode.BOMB)) {
            String modeString = mode.name().toUpperCase(Locale.ROOT).substring(0, 1) + mode.name().toLowerCase(Locale.ROOT).substring(1);
            joiner.add(modeString);
        }

        return joiner.toString();
    }


    //for use in conjunction with Translator#gameModeAsQueue
    public String scoreLine(PlayerData playerData) {
        //funny deathmatch scoreline based on kills
        if(matchInfo().gameMode().equals(ValGameMode.DEATHMATCH)) { //todo this might crash with custom deathmatches with 1 player
            //get list of players sorted by kills
            List<PlayerData> players = new ArrayList<>(players()); //arraylist so modifiable
            players.sort((a, b) -> { //todo players should implement comparable (by kills and/or by combat score), could also help with matchmvp methods
                if(a.stats().get().kills() > b.stats().get().kills()) {
                    return -1;
                }
                else {
                    return 1;
                }
            });

            //if you win the deathmatch [yourScore]:[secondPlace]
            if(players.get(0).puuid().equals(playerData.puuid())) {
                return playerData.stats().get().kills() + ":" + players.get(1).stats().get().kills();
            }
            //if you lose the deathmatch [winnerScore]:[yourScore]
            else {
                return playerData.stats().get().kills() + ":" + players.get(0).stats().get().kills();
            }
        }
        List<TeamData> teams = new ArrayList<>(teams());
        teams.sort((a, b) -> {
            if(a.teamId().equals(playerData.teamId())) {
                return -1;
            }
            else {
                return 1;
            }
        });


        if(matchInfo().queueId().equals(ValQueueId.ESCALATION) || matchInfo().queueId().equals(ValQueueId.SNOWBALL_FIGHT)) {
            return teams.get(0).numPoints() + ":" + teams.get(1).numPoints(); //escalation/snowball uses points
        } else {
            return teams.get(0).roundsWon() + ":" + teams.get(1).roundsWon(); //everything else uses rounds
        }
    }

    /**
     * Sorts matches descending by time
     * @param o match to compare to
     * @return
     */
    @Override
    public int compareTo(ValMatch o) {
        if(matchInfo().gameStartMillis() > o.matchInfo().gameStartMillis()) {
            return -1;
        }
        else {
            return 1;
        }
    }
}
