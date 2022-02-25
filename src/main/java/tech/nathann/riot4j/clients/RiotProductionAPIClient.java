package tech.nathann.riot4j.clients;

import reactor.core.publisher.Mono;
import tech.nathann.riot4j.api.match.ValMatch;
import tech.nathann.riot4j.api.match.ValMatchlist;
import tech.nathann.riot4j.enums.ValRecentQueue;
import tech.nathann.riot4j.enums.ValRegion;
import tech.nathann.riot4j.json.valMatch.RecentMatchesData;
import tech.nathann.riot4j.queues.Ratelimiter;

public class RiotProductionAPIClient extends RiotDevelopmentAPIClient {
    protected RiotProductionAPIClient(ClientConfig config, Ratelimiter limiter) {
        super(config, limiter);
    }

    public Mono<RecentMatchesData> getRecentMatches(ValRegion region, ValRecentQueue queue) {
        return getRecentMatchesData(region, queue);
    }

    public Mono<ValMatch> getMatch(ValRegion region, String matchId) {
        return getMatchData(region, matchId)
                .map(data -> new ValMatch(data));
    }

    public Mono<ValMatchlist> getMatchList(ValRegion region, String puuid) {
        return getMatchListData(region, puuid)
                .map(data -> new ValMatchlist(this, data, region));
    }

    @Override
    public Mono<RiotAPIClient> test() {
        return getRecentMatches(valRegion, ValRecentQueue.UNRATED)
                .thenReturn(this);
    }
}
