package io.github.nathannorth.riot4j.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RiotRegion {
    AMERICAS("americas"),
    ASIA("asia"),
    EUROPE("europe"),
    E_SPORTS("esports");

    private final String value;
    RiotRegion(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return value;
    }
}
