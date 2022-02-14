package tech.nathann.riot4j.clients;

import tech.nathann.riot4j.enums.RiotGame;
import tech.nathann.riot4j.enums.RiotRegion;
import tech.nathann.riot4j.enums.ValRegion;
import tech.nathann.riot4j.json.Mapping;
import tech.nathann.riot4j.json.riotAccount.ActiveShardData;
import tech.nathann.riot4j.json.riotAccount.RiotAccountData;
import tech.nathann.riot4j.json.valLeaderboard.LeaderboardData;
import tech.nathann.riot4j.objects.ValActId;
import tech.nathann.riot4j.queues.BucketManager;
import tech.nathann.riot4j.queues.RateLimits;
import reactor.core.publisher.Mono;

/**
 * A RiotAPIClient is a generic class that holds a token
 */
public abstract class RiotAPIClient extends RawAPIInterface {
    protected final BucketManager buckets = new BucketManager();
    protected final String token;
    protected final RiotRegion riotRegion;
    protected final ValRegion valRegion;

    protected RiotAPIClient(ClientConfig config) {
        this.token = config.token();
        this.riotRegion = config.riotRegion();
        this.valRegion = config.valRegion();
    }

    protected Mono<RiotAccountData> getRiotAccountData(RiotRegion region, String name, String tagline) {
        return buckets.pushToBucket(RateLimits.ACCOUNT_BY_RIOT_ID, getAccountByNameRaw(token, region.toString(), name, tagline))
                .map(Mapping.map(RiotAccountData.class));
    }

    protected Mono<RiotAccountData> getRiotAccountData(RiotRegion region, String puuid) {
        return buckets.pushToBucket(RateLimits.ACCOUNT_BY_PUUID, getAccountByPuuidRaw(token, region.toString(), puuid))
                .map(Mapping.map(RiotAccountData.class));
    }

    protected Mono<ActiveShardData> getActiveShardData(RiotRegion region, RiotGame game, String puuid) {
        return buckets.pushToBucket(RateLimits.ACTIVE_SHARDS, getActiveShardsByGameRaw(token, region.toString(), game.toString(), puuid))
                .map(Mapping.map(ActiveShardData.class));
    }

    protected Mono<LeaderboardData> getLeaderboardData(ValRegion region, ValActId act, Long start, Long size) {
        return buckets.pushToBucket(RateLimits.VAL_RANKED, getValLeaderboardRaw(token, region.toString(), act.toString(), size.toString(), start.toString()))
                .map(Mapping.map(LeaderboardData.class));
    }

    //todo still missing some methods that are only in riotdev/prod clients

    public abstract Mono<RiotAPIClient> test();
}
