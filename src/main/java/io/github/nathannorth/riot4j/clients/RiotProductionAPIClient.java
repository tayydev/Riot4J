package io.github.nathannorth.riot4j.clients;

import io.github.nathannorth.riot4j.api.account.RiotAccount;
import io.github.nathannorth.riot4j.api.match.MatchlistEntry;
import io.github.nathannorth.riot4j.api.match.ValMatch;
import io.github.nathannorth.riot4j.api.match.ValMatchlist;
import io.github.nathannorth.riot4j.enums.ValRecentQueue;
import io.github.nathannorth.riot4j.enums.ValRegion;
import io.github.nathannorth.riot4j.exceptions.IncompleteBuilderException;
import io.github.nathannorth.riot4j.json.Mapping;
import io.github.nathannorth.riot4j.json.valMatch.MatchData;
import io.github.nathannorth.riot4j.json.valMatch.MatchlistData;
import io.github.nathannorth.riot4j.json.valMatch.RecentMatchesData;
import io.github.nathannorth.riot4j.queues.RateLimits;
import reactor.core.publisher.Mono;

public class RiotProductionAPIClient extends RiotDevelopmentAPIClient {
    protected RiotProductionAPIClient(String token) {
        super(token);
    }

    public static RiotProductionAPIClientBuilder getProdBuilder() {
        return new RiotProductionAPIClientBuilder();
    }

    public Mono<RecentMatchesData> getRecentMatches(ValRegion region, ValRecentQueue queue) {
        return buckets.pushToBucket(RateLimits.VAL_RECENT_MATCHES, getRecentMatchesRaw(token, region.toString(), queue.toString()))
                .map(Mapping.map(RecentMatchesData.class));
    }

    public Mono<ValMatch> getMatch(ValRegion region, String matchId) {
        return buckets.pushToBucket(RateLimits.VAL_MATCH, getMatchRaw(token, region.toString(), matchId))
                .map(Mapping.map(MatchData.class))
                .map(matchData -> new ValMatch(matchData));
    }

    public Mono<ValMatch> getMatch(MatchlistEntry entry) {
        return getMatch(entry.region(), entry.matchId());
    }

    public Mono<ValMatchlist> getMatchList(ValRegion region, String puuid) {
        return buckets.pushToBucket(RateLimits.VAL_MATCHLIST, getMatchListRaw(token, region.toString(), puuid))
                .map(Mapping.map(MatchlistData.class))
                .map(data -> new ValMatchlist(this, data, region));
    }

    public Mono<ValMatchlist> getMatchList(RiotAccount account) {
        return account.getRegion()
                .flatMap(valRegion -> getMatchList(valRegion, account.puuid()));
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
            return temp.getRecentMatches(ValRegion.NORTH_AMERICA, ValRecentQueue.UNRATED)
                    .then(Mono.just(temp));
        }
    }
}
