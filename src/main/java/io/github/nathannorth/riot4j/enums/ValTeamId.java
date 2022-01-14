package io.github.nathannorth.riot4j.enums;

public enum ValTeamId { //todo use this
    BLUE("Blue") , RED("Red");

    private final String value;

    ValTeamId(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
