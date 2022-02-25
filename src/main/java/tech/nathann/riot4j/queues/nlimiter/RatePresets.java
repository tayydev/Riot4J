package tech.nathann.riot4j.queues.nlimiter;

import tech.nathann.riot4j.queues.RateLimits;

import java.time.Duration;
import java.util.Map;

public class RatePresets {
    private static final Map<RateLimits, Dispenser> buckets = Map.of(
            RateLimits.ACCOUNT_BY_RIOT_ID, new Dispenser(RateLimits.ACCOUNT_BY_RIOT_ID, Duration.ofMinutes(1), 1000),
            RateLimits.ACCOUNT_BY_PUUID, new Dispenser(RateLimits.ACCOUNT_BY_PUUID, Duration.ofMinutes(1), 1000),
            RateLimits.ACTIVE_SHARDS, new Dispenser(RateLimits.ACTIVE_SHARDS, Duration.ofSeconds(10), 20000),

            RateLimits.VAL_RANKED, new Dispenser(RateLimits.VAL_RANKED, Duration.ofSeconds(10), 10),
            RateLimits.VAL_STATUS, new Dispenser(RateLimits.VAL_STATUS, Duration.ofSeconds(10), 20000),
            RateLimits.VAL_CONTENT, new Dispenser(RateLimits.VAL_CONTENT, Duration.ofMinutes(1), 60),

            RateLimits.VAL_MATCH, new Dispenser(RateLimits.VAL_MATCH, Duration.ofMinutes(1), 60),
            RateLimits.VAL_MATCHLIST, new Dispenser(RateLimits.VAL_MATCHLIST, Duration.ofMinutes(1), 120),
            RateLimits.VAL_RECENT_MATCHES, new Dispenser(RateLimits.VAL_RECENT_MATCHES, Duration.ofMinutes(1), 60)
    );

    public static final ProactiveRatelimiter DEV_CLIENT = new ProactiveRatelimiter(
            new Dispenser(RateLimits.RIOT4J_MASTER, Duration.ofSeconds(1), 20),
            new Dispenser(RateLimits.RIOT4J_SECONDARY, Duration.ofMinutes(2), 100),
            buckets
    );

    //todo this is specific to my limits
    public static final ProactiveRatelimiter PROD_CLIENT = new ProactiveRatelimiter(
            new Dispenser(RateLimits.RIOT4J_MASTER, Duration.ofSeconds(10), 500),
            new Dispenser(RateLimits.RIOT4J_SECONDARY, Duration.ofMinutes(10), 30000),
            buckets
    );
}
