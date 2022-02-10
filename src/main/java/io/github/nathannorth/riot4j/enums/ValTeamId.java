package io.github.nathannorth.riot4j.enums;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ValTeamId {
    BLUE("Blue") ,
    RED("Red"),
    @JsonEnumDefaultValue
    DEATHMATCH("Deathmatch");

    private final String value;

    ValTeamId(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return value;
    }
}
