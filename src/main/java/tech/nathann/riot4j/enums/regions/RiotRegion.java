package tech.nathann.riot4j.enums.regions;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RiotRegion implements Region {
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
