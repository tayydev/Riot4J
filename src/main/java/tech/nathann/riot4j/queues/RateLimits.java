package tech.nathann.riot4j.queues;

import java.time.Duration;

public enum RateLimits {
    /**
     * i dont like having separate enums hardcoded for different client types but I also want ratelimits to be a true
     * enum. Maybe in the long run ratelimits will be an object so that rate limits can self-adjust on the fly, but for
     * the moment everything is static because of issues with restructuring Dispenser objects on the fly
     */
    RIOT4J_DEV_MASTER(20, Duration.ofSeconds(1)),
    RIOT4J_DEV_SECONDARY(100, Duration.ofMinutes(2)),
    RIOT4J_PROD_MASTER(500, Duration.ofSeconds(10)),
    RIOT4J_PROD_SECONDARY(30000, Duration.ofMinutes(10)),

    ACCOUNT_BY_RIOT_ID(1000, Duration.ofMinutes(1)),
    ACCOUNT_BY_PUUID(1000, Duration.ofMinutes(1)),
    ACTIVE_SHARDS(20000, Duration.ofSeconds(10)),

    VAL_CONTENT(60, Duration.ofMinutes(1)),
    VAL_RANKED(10, Duration.ofSeconds(10)),
    VAL_STATUS(20000, Duration.ofSeconds(10)),

    VAL_MATCH(60, Duration.ofMinutes(1)),
    VAL_MATCHLIST(120, Duration.ofMinutes(1)),
    VAL_RECENT_MATCHES(60, Duration.ofMinutes(1));


    private final int count;
    private final Duration length;

    RateLimits(int count, Duration length) {
        this.count = count;
        this.length = length;
    }

    public int getCount() {
        return count;
    }

    public Duration getLength() {
        return length;
    }
}
