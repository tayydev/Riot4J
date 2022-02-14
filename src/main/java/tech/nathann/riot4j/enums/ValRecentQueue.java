package tech.nathann.riot4j.enums;

public enum ValRecentQueue {
    COMPETITIVE("competitive"),
    UNRATED("unrated"),
    SPIKE_RUSH("spikerush"),
    TOURNAMENTS("tournamentmode"); //non-functional

    private final String value;
    ValRecentQueue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
