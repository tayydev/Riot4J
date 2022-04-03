package tech.nathann.riot4j.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.StringJoiner;

public enum ValQueueId {
    UNRATED("unrated"),
    COMPETITIVE("competitive"),
    DEATHMATCH("deathmatch"),
    SPIKE_RUSH("spikerush"),
    ESCALATION("ggteam"),
    REPLICATION("onefa"),
    SNOWBALL_FIGHT("snowball"),
    CUSTOM(""); //todo default val?

    private final String value;

    ValQueueId(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return value;
    }

    public String prettyName() {
        StringJoiner returnable = new StringJoiner(" ");
        String[] words = name().split("_");
        for(String word: words) {
            returnable.add(word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase());
        }
        return returnable.toString();
    }
}
