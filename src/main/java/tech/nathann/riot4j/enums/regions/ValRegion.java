package tech.nathann.riot4j.enums.regions;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Val regions represent valid regions to execute api calls against
 */
public enum ValRegion implements Region {
    NORTH_AMERICA("na"),
    BRAZIL("br"),
    EUROPE("eu"),
    LATIN_AMERICA("latam"),
    ASIA_PACIFIC("ap"),
    KOREA("kr"),
    E_SPORTS("esports"); //note: NOT VALID for ranked endpoint

    private final String value;
    ValRegion(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return value;
    }
}
