package io.github.nathannorth.riot4j.objects;

import io.github.nathannorth.riot4j.enums.ValMatchType;
import io.github.nathannorth.riot4j.json.valContent.ContentData;
import io.github.nathannorth.riot4j.json.valContent.ContentItemData;
import io.github.nathannorth.riot4j.json.valMatch.MatchInfoData;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;

/**
 * A translator parses data in the content endpoint to help turn outputs from other endpoints like the match endpoint into human-readable information
 */
public class Translator {
    private final ContentData data;
    private final HashMap<String, String> maps = new HashMap<>();
    private final HashMap<String, String> gameModes = new HashMap<>();

    /**
     * Instantiate a new Translator. A  translator may become out of date given a multi-week lifespan as it does not make any api call itself
     * @param data
     */
    public Translator(ContentData data) {
        this.data = data;

        for(ContentItemData map: data.maps()) {
            if(map.assetPath().isPresent()) { //ignore random api noise
                maps.put(map.assetPath().get(), map.name());
            }
        }
        for(ContentItemData mode: data.gameModes()) {
            gameModes.put(mode.assetPath().get(), mode.name());
        }
    }

    public String getMapName(String assetName) {
        return maps.get(assetName);
    }

    /**
     * A game TYPE is not the same as a game MODE or a game QUEUE. The queue represents the distinction between rated and unrated modes. The mode represents whether it is bomb mode or deathmatch, escalation, etc. The type is human-readable description that provides only non-implied information.
     * @param matchInfoData
     * @return What a user would expect a description for a mode to be.
     */
    public String getGameTypeHuman(MatchInfoData matchInfoData) {
        StringJoiner joiner = new StringJoiner(" ");

        if(matchInfoData.queueId().equals(""))
            joiner.add("Custom");

        if(matchInfoData.queueId().equals("unrated")) joiner.add("Unrated");
        if(matchInfoData.queueId().equals("competitive")) joiner.add("Competitive");

        String gameMode = gameModes.get(matchInfoData.gameMode());
        if(!gameMode.equals("Standard"))
            joiner.add(gameMode);

        return joiner.toString();
    }

    //todo add unranked
    private final Map<Integer, String> base = Map.of(
            1, "Iron",
            2, "Bronze",
            3, "Silver",
            4, "Gold",
            5, "Platinum",
            6, "Diamond",
            7, "Immortal",
            8, "Radiant"
    );
    public String getRankHuman(int competitiveTier) {
        return base.get(competitiveTier / 3) + " " + (competitiveTier % 3 + 1);
    }
    public final ValMatchType getType(MatchInfoData info) {
        if(info.isRanked()) return ValMatchType.COMPETITIVE;
        if(info.queueId().equals("")) return ValMatchType.CUSTOM;
        String guess = gameModes.get(info.gameMode()).toUpperCase(Locale.ROOT).replace(" ", "_");
        if(guess.equals("STANDARD")) return ValMatchType.UNRATED;
        else return ValMatchType.valueOf(guess);
    }
}
