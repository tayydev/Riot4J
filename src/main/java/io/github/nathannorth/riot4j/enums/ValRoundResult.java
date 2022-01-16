package io.github.nathannorth.riot4j.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ValRoundResult {
    BOMB_DEFUSED("Defuse"),
    BOMB_DETONATED("Detonate"),
    TEAM_ELIMINATED("Elimination"),
    SURRENDERED("Surrendered"),
    OTHER(""); //todo test if this is time

    private final String value;

    ValRoundResult(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return value;
    }
}
