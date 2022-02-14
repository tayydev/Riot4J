package tech.nathann.riot4j.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RiotGame {
    VALORANT("val");
    //todo others

    private final String value;
    RiotGame(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return value;
    }
}
