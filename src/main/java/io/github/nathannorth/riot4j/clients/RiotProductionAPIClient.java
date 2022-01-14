package io.github.nathannorth.riot4j.clients;

import io.github.nathannorth.riot4j.enums.ValQueue;
import io.github.nathannorth.riot4j.enums.ValRegion;
import io.github.nathannorth.riot4j.exceptions.IncompleteBuilderException;
import io.github.nathannorth.riot4j.json.Mapping;
import io.github.nathannorth.riot4j.json.valMatch.MatchData;
import io.github.nathannorth.riot4j.json.valMatch.MatchlistData;
import io.github.nathannorth.riot4j.json.valMatch.RecentMatchesData;
import io.github.nathannorth.riot4j.objects.ValMatch;
import io.github.nathannorth.riot4j.queues.RateLimits;
import reactor.core.publisher.Mono;

public class RiotProductionAPIClient extends RiotDevelopmentAPIClient {
    protected RiotProductionAPIClient(String token) {
        super(token);
    }

    public static RiotProductionAPIClientBuilder getProdBuilder() {
        return new RiotProductionAPIClientBuilder();
    }

    public Mono<RecentMatchesData> getRecentMatches(ValRegion region, ValQueue queue) {
        return buckets.pushToBucket(RateLimits.VAL_RECENT_MATCHES, getRecentMatchesRaw(token, region.toString(), queue.toString()))
                .map(Mapping.map(RecentMatchesData.class));
    }

    public Mono<ValMatch> getMatch(ValRegion region, String matchId) {
        return buckets.pushToBucket(RateLimits.VAL_MATCH, getMatchRaw(token, region.toString(), matchId))
                .map(Mapping.map(MatchData.class))
                .map(matchData -> new ValMatch(matchData));
    }

    public Mono<MatchlistData> getMatchList(ValRegion region, String puuid) {
        return buckets.pushToBucket(RateLimits.VAL_MATCHLIST, getMatchListRaw(token, region.toString(), puuid))
                .map(Mapping.map(MatchlistData.class));
    }

    public static class RiotProductionAPIClientBuilder {
        private String key = null;

        /**
         * Gives a builder object an API key
         * @param key your API key
         * @return your builder with an updated API key
         */
        public RiotProductionAPIClientBuilder addKey(String key) {
            this.key = key;
            return this;
        }

        /**
         * Returns a mono of your client that when evaluated tests your api key and returns a completed RiotDevelopmentAPIClient
         * @return a RiotDevelopmentAPIClient
         */
        public Mono<RiotProductionAPIClient> build() {
            if (key == null) return Mono.error(new IncompleteBuilderException("Did not specify token."));
            RiotProductionAPIClient temp = new RiotProductionAPIClient(key);
            return temp.getRecentMatches(ValRegion.NORTH_AMERICA, ValQueue.UNRATED)
                    .then(Mono.just(temp));
        }
    }
}
