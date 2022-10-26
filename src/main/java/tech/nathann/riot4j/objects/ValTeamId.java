package tech.nathann.riot4j.objects;

import com.fasterxml.jackson.annotation.JsonValue;

public class ValTeamId {
    public static final ValTeamId BLUE = new ValTeamId("Blue");
    public static final ValTeamId RED = new ValTeamId("Red");
    public static final ValTeamId NEUTRAL = new ValTeamId("Neutral");

    private final String value;

    public ValTeamId(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValTeamId valTeamId = (ValTeamId) o;

        return value.equals(valTeamId.value);
    }
}
