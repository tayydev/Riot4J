package tech.nathann.riot4j.clients;

import reactor.core.publisher.Mono;
import tech.nathann.riot4j.enums.*;
import tech.nathann.riot4j.json.Mapping;
import tech.nathann.riot4j.json.riotAccount.ActiveShardData;
import tech.nathann.riot4j.json.riotAccount.RiotAccountData;
import tech.nathann.riot4j.json.valContent.ContentData;
import tech.nathann.riot4j.json.valLeaderboard.LeaderboardData;
import tech.nathann.riot4j.json.valMatch.MatchData;
import tech.nathann.riot4j.json.valMatch.MatchlistData;
import tech.nathann.riot4j.json.valMatch.RecentMatchesData;
import tech.nathann.riot4j.json.valPlatform.PlatformStatusData;
import tech.nathann.riot4j.objects.ValActId;
import tech.nathann.riot4j.queues.RateLimits;
import tech.nathann.riot4j.queues.Ratelimiter;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * A RiotAPIClient is a generic class that holds a token
 */
public abstract class RiotAPIClient extends RawAPIInterface {
    protected final String token;
    protected final RiotRegion riotRegion;
    protected final ValRegion valRegion;

    protected final Ratelimiter limiter;

    protected RiotAPIClient(ClientConfig config, Ratelimiter limiter) {
        this.token = config.token();
        this.riotRegion = config.riotRegion();
        this.valRegion = config.valRegion();

        this.limiter = limiter;
    }

    /**
     * Development methods:
     */

    protected Mono<RiotAccountData> getRiotAccountData(RiotRegion region, String name, String tagline) {
        String nameSanitized = URLEncoder.encode(name, StandardCharsets.UTF_8);
        String taglineSanitized = URLEncoder.encode(tagline, StandardCharsets.UTF_8);

        return limiter.push(RateLimits.ACCOUNT_BY_RIOT_ID, getAccountByNameRaw(token, region.toString(), nameSanitized, taglineSanitized))
                .map(Mapping.map(RiotAccountData.class));
    }

    protected Mono<RiotAccountData> getRiotAccountData(RiotRegion region, String puuid) {
        return limiter.push(RateLimits.ACCOUNT_BY_PUUID, getAccountByPuuidRaw(token, region.toString(), puuid))
                .map(Mapping.map(RiotAccountData.class));
    }

    protected Mono<ActiveShardData> getActiveShardData(RiotRegion region, RiotGame game, String puuid) {
        return limiter.push(RateLimits.ACTIVE_SHARDS, getActiveShardsByGameRaw(token, region.toString(), game.toString(), puuid))
                .map(Mapping.map(ActiveShardData.class));
    }

    protected Mono<LeaderboardData> getLeaderboardData(ValRegion region, ValActId act, Long start, Long size) {
        return limiter.push(RateLimits.VAL_RANKED, getValLeaderboardRaw(token, region.toString(), act.toString(), size.toString(), start.toString()))
                .map(Mapping.map(LeaderboardData.class));
    }

    protected Mono<PlatformStatusData> getPlatformStatusData(ValRegion region) {
        return limiter.push(RateLimits.VAL_STATUS, getValStatusRaw(token, region.toString()))
                .map(Mapping.map(PlatformStatusData.class));
    }

    protected Mono<ContentData> getContentData(ValRegion region, ValLocale locale) {
        return limiter.push(RateLimits.VAL_CONTENT, getValContentRaw(token, region.toString(), locale.toString()))
                .map(Mapping.map(ContentData.class));
    }

    /**
     *  Production methods:
     */

    protected Mono<RecentMatchesData> getRecentMatchesData(ValRegion region, ValRecentQueue queue) {
        return limiter.push(RateLimits.VAL_RECENT_MATCHES, getRecentMatchesRaw(token, region.toString(), queue.toString()))
                .map(Mapping.map(RecentMatchesData.class));
    }

    protected Mono<MatchData> getMatchData(ValRegion region, String id) {
        return limiter.push(RateLimits.VAL_MATCH, getMatchRaw(token, region.toString(), id))
                .map(Mapping.map(MatchData.class));
    }

    protected Mono<MatchlistData> getMatchListData(ValRegion region, String puuid) {
        return limiter.push(RateLimits.VAL_MATCHLIST, getMatchListRaw(token, region.toString(), puuid))
                .map(Mapping.map(MatchlistData.class));
    }

    //todo still missing some methods that are only in riotdev/prod clients

    public abstract Mono<RiotAPIClient> test();
}
