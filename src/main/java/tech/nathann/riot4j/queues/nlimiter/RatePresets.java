package tech.nathann.riot4j.queues.nlimiter;

import tech.nathann.riot4j.queues.RateLimits;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RatePresets {

    private static final List<RateLimits> respected = Arrays.stream(RateLimits.values())
            .filter(lim -> !lim.equals(RateLimits.RIOT4J_DEV_MASTER)) //this is scuffed lol
            .filter(lim -> !lim.equals(RateLimits.RIOT4J_DEV_SECONDARY))
            .filter(lim -> !lim.equals(RateLimits.RIOT4J_PROD_MASTER))
            .filter(lim -> !lim.equals(RateLimits.RIOT4J_PROD_SECONDARY))
            .collect(Collectors.toList());

    public static final ProactiveRatelimiter DEV_CLIENT = new ProactiveRatelimiter(
            RateLimits.RIOT4J_DEV_MASTER,
            RateLimits.RIOT4J_DEV_SECONDARY,
            respected
    );

    //todo this is specific to my limits
    public static final ProactiveRatelimiter PROD_CLIENT = new ProactiveRatelimiter(
            RateLimits.RIOT4J_PROD_MASTER,
            RateLimits.RIOT4J_PROD_SECONDARY,
            respected
    );
}
