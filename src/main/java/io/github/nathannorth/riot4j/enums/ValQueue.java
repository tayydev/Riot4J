package io.github.nathannorth.riot4j.enums;

public enum ValQueue {
    COMPETITIVE("competitive"),
    UNRATED("unrated"),
    SPIKE_RUSH("spikerush"),
    TOURNAMENTS("tournamentmode"); //todo test this
    //todo how do other modes work

    private final String value;
    ValQueue(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }
}
