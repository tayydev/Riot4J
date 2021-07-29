package io.github.nathannorth.riot4j.enums;

public enum ValQueue {
    COMPETITIVE("competitive"),
    UNRATED("unrated"),
    SPIKE_RUSH("spikerush"),
    TOURNAMENTS("tournamentmode"); //todo test this
    //todo other modes

    private final String value;
    ValQueue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
