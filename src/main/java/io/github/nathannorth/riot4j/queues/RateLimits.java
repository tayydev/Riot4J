package io.github.nathannorth.riot4j.queues;

public enum RateLimits {
    VAL_MATCH("match"),
    VAL_MATCHLIST("matchlists"),
    VAL_RECENT_MATCHES("recent"),
    VAL_CONTENT("content"),
    ACCOUNT_BY_RIOT_ID("accountRiotId"),
    ACCOUNT_BY_PUUID("accountPuuid"),
    ACTIVE_SHARDS("shards"),
    VAL_RANKED("ranked"),
    VAL_STATUS("status");


    private final String val;
    RateLimits(String val) {
        this.val = val;
    }
}
