package io.github.nathannorth.riot4j.enums;

public enum ValMatchTeam { //todo use this
    BLUE("Blue") , RED("Red");

    private final String value;

    ValMatchTeam(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
