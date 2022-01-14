package io.github.nathannorth.riot4j.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ValQueueId {
    UNRATED("unrated"),
    COMPETITIVE("competitive"),
    DEATHMATCH("deathmatch"),
    SPIKE_RUSH("spikerush"),
    ESCALATION("ggteam"),
    REPLICATION("onefa"),
    SNOWBALL_FIGHT("snowball"),
    CUSTOM("");

    private final String value;

    ValQueueId(String value) {
        this.value = value;
    }


    @Override
    @JsonValue
    public String toString() {
        return value;
    }
}
