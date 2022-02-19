package tech.nathann.riot4j.api.content;

import java.util.Locale;

public class ValCharacter {
    private final String id;
    private final String name;

    public ValCharacter(String id, String name) {
        this.id = id.toUpperCase(Locale.ROOT);
        this.name = name;
    }

    //todo use this somehow
    public String getURL() {
        return "https://github.com/NathanNorth/ValorantAssets/raw/main/icons/characters/" + id + ".png";
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
