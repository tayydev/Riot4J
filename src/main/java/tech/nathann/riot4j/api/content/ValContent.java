package tech.nathann.riot4j.api.content;

import tech.nathann.riot4j.json.valContent.ActData;
import tech.nathann.riot4j.json.valContent.ContentData;
import tech.nathann.riot4j.json.valContent.ContentItemData;
import tech.nathann.riot4j.objects.ValActId;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Holds information from /val/content endpoint. Useful for transforming api values into human-readable names.
 */
public class ValContent {

    private final ContentData data;

    public ValContent(ContentData data) {
        this.data = data;

        for(ContentItemData character: data.characters()) {
            characters.put(character.id().toUpperCase(Locale.ROOT), new ValCharacter(character.id(), character.name()));
        }
        for(ContentItemData map: data.maps()) {
            if(map.assetPath().isPresent()) { //ignore random api noise
                maps.put(map.assetPath().get(), map.name());
            }
        }
    }

    public ValActId getLatestAct() {
        for(ActData act: data.acts()) {
            if(act.isActive()) return act.id();
        }
        throw new RuntimeException("No active acts!");
    }

    private final Map<String, ValCharacter> characters = new HashMap<>();
    public ValCharacter characterOf(String id) {
        return characters.get(id.toUpperCase(Locale.ROOT));
    }

    private final Map<String, String> maps = new HashMap<>();
    public String getMapName(String mapPath) {
        return maps.get(mapPath);
    }

    public static String getCharacterURL(String id) {
        id = id.toUpperCase(Locale.ROOT);
        return "https://github.com/NathanNorth/ValorantAssets/raw/main/icons/characters/" + id + ".png";
    }
    public static String getRankURL(int rank) {
        return "https://github.com/NathanNorth/ValorantAssets/raw/main/icons/ranked/" + rank + ".png";
    }

    private static final Map<Integer, String> base = Map.of(
            1, "Iron",
            2, "Bronze",
            3, "Silver",
            4, "Gold",
            5, "Platinum",
            6, "Diamond",
            7, "Immortal",
            8, "Radiant"
    );
    public static String getRankHuman(int competitiveTier) {
        if(competitiveTier < 1) return "Unranked";
        return base.get(competitiveTier / 3) + " " + (competitiveTier % 3 + 1);
    }
}
