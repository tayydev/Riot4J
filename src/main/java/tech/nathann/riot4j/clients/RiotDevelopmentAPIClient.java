package tech.nathann.riot4j.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import tech.nathann.riot4j.api.account.RiotAccount;
import tech.nathann.riot4j.api.content.ValContent;
import tech.nathann.riot4j.enums.RiotGame;
import tech.nathann.riot4j.enums.regions.RiotRegion;
import tech.nathann.riot4j.enums.ValLocale;
import tech.nathann.riot4j.enums.regions.ValRegion;
import tech.nathann.riot4j.exceptions.WebException;
import tech.nathann.riot4j.json.riotAccount.ActiveShardData;
import tech.nathann.riot4j.json.valLeaderboard.LeaderboardPlayerData;
import tech.nathann.riot4j.json.valPlatform.PlatformStatusData;
import tech.nathann.riot4j.objects.ValActId;
import tech.nathann.riot4j.objects.ValStatusUpdateEvent;
import tech.nathann.riot4j.queues.Ratelimiter;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * The main method of interfacing with the Riot API. Provides rate-limiting, exposes API objects. Only exposes methods
 * that can be used with a Development API Key
 */
public class RiotDevelopmentAPIClient extends RiotAPIClient {

    private static final Logger log = LoggerFactory.getLogger(RiotDevelopmentAPIClient.class);

    protected RiotDevelopmentAPIClient(ClientConfig config, Ratelimiter limiter) {
        super(config, limiter);
    }

    /**
     * Find a {@link RiotAccount} by name + tagline
     * @param riotRegion specify account's *{@link RiotRegion}* (not a {@link ValRegion})
     * @param name username
     * @param tagLine tagline (eg. for 'nate#asdf' the tagline is 'asdf')
     * @return a valid {@link RiotAccount}
     */
    public Mono<RiotAccount> getRiotAccountByName(RiotRegion riotRegion, String name, String tagLine) {
        return getRiotAccountData(riotRegion, name, tagLine)
                .map(data -> new RiotAccount(this, data));
    }

    /**
     * Find a {@link RiotAccount} by name + tagline. Assumes {@link RiotRegion} from this client's {@link ClientConfig}
     * @param name username
     * @param tagLine tagline (eg. for 'nate#asdf' the tagline is 'asdf')
     * @return a valid {@link RiotAccount}
     */
    public Mono<RiotAccount> getRiotAccountByName(String name, String tagLine) {
        return getRiotAccountByName(riotRegion, name, tagLine);
    }


    /**
     * Find a {@link RiotAccount} by puuid
     * @param riotRegion specify account's *{@link RiotRegion}* (not a {@link ValRegion})
     * @param puuid player id
     * @return a valid {@link RiotAccount}
     */
    public Mono<RiotAccount> getRiotAccountByPuuid(RiotRegion riotRegion, String puuid) {
        return getRiotAccountData(riotRegion, puuid)
                .map(data -> new RiotAccount(this, data));
    }

    /**
     * Find a {@link RiotAccount} by puuid. Assumes {@link RiotRegion} from this client's {@link ClientConfig}
     * @param puuid player id
     * @return a valid {@link RiotAccount}
     */
    public Mono<RiotAccount> getRiotAccountByPuuid(String puuid) {
        return getRiotAccountByPuuid(riotRegion, puuid);
    }

    /**
     * Used to get region for an account
     * @param region provide *any* {@link RiotRegion}
     * @param game provide any game user has played
     * @param puuid player id
     * @return region data for user
     */
    public Mono<ActiveShardData> getActiveShardsByGame(RiotRegion region, RiotGame game, String puuid) {
        return getActiveShardData(region, game, puuid);
    }

    /**
     * Used to get region for an account. Assumes that the account has played VALORANT
     * @param region provide *any* {@link RiotRegion}
     * @param puuid player id
     * @return region data for user
     */
    public Mono<ActiveShardData> getActiveShardsVal(RiotRegion region, String puuid) {
        return getActiveShardsByGame(region, RiotGame.VALORANT, puuid);
    }

    /**
     * Used to get region for an account. Assumes that account has played VALORANT. Assumes {@link RiotRegion} from this
     * client's {@link ClientConfig}
     * @param puuid player id
     * @return region data for user
     */
    public Mono<ActiveShardData> getActiveShardsVal(String puuid) {
        return getActiveShardsByGame(riotRegion, RiotGame.VALORANT, puuid);
    }

    /**
     * Get VALORANT status
     * @param region region to get status from
     * @return status information
     */
    public Mono<PlatformStatusData> getValStatus(ValRegion region) {
        return getPlatformStatusData(region);
    }

    private PlatformStatusData lastData = null;

    /**
     * Gets a flux that emits statusUpdateEvents whenever riots status for a given region changes. Checks are made on a given duration.
     * @param region Region to get status from
     * @param duration Duration to check for updates, not recommended to be set to anything under 1 second to avoid overflowing the ratelimit sink
     * @return
     */
    public Flux<ValStatusUpdateEvent> getStatusUpdates(ValRegion region, Duration duration) { //todo test or remove this
        return Flux.interval(duration)
                .doOnNext(e -> log.debug("Trying to get a new status update"))
                .flatMap(num -> getValStatus(region))
                .doOnNext(e -> log.debug("Succeeded in getting new status update"))
                .filter(status -> !status.equals(lastData)) //data must be changed
                .map(newStatus -> {
                    PlatformStatusData oldStatus = lastData;
                    lastData = newStatus;
                    return new ValStatusUpdateEvent(oldStatus, newStatus);
                });
    }

    /**
     * Get information about VALORANT content
     * @param region specify region for content (think when a region gets a patch before another)
     * @param locale language to get content in. Not all regions are translated into every locale
     * @return ValContent
     */
    public Mono<ValContent> getValContent(ValRegion region, ValLocale locale) {
        return getContentData(region, locale)
                .map(data -> new ValContent(data));
    }

    private final Map<Tuple2<ValRegion, ValLocale>, Mono<ValContent>> cache = new HashMap<>();
    public Mono<ValContent> getValContentCached(ValRegion region, ValLocale locale) { //todo cut?
        Tuple2<ValRegion, ValLocale> key = Tuples.of(region, locale);
        return cache.computeIfAbsent(key, newKey ->
                getValContent(region, locale)
                        .doOnNext(e -> log.debug("Caching content!"))
                        .cache(Duration.ofMinutes(5))
        );
    }

    /**
     * Get a flux that emits players indefinitely
     * @param region
     * @param act
     * @param start what position in the leaderboard to start grabbing data from
     * @return a flux that will honor *infinite* demand. {@link Flux#take(long)} is a must-have. See
     * {@link RiotDevelopmentAPIClient#getValLeaderboards(ValRegion, ValActId, long, long)} for a capped method
     */
    public Flux<LeaderboardPlayerData> getValLeaderboards(ValRegion region, ValActId act, long start) {
        return getValLeaderboards(region, act, start, Long.MAX_VALUE);
    }

    /**
     * Get a flux that emits player data from a start index until the end
     * @param region
     * @param act
     * @param start start position
     * @param cap final index (exclusive)
     * @return a flux that will emit leaderboard information until reaching the cap index
     */
    public Flux<LeaderboardPlayerData> getValLeaderboards(ValRegion region, ValActId act, long start, long cap) {
        if(start < 0) return Flux.error(new IndexOutOfBoundsException("Start cannot be negative!"));
        //there is technically the possibility of the leaderboards shrinking while you process chunks causing you to hit an invalid index.
        return getLeaderboardData(region, act, 0L, 1L).flatMapMany(it ->
                recurValLeaderboards(region, act, start, Math.min(it.totalPlayers(), cap))
        );
    }

    private Flux<LeaderboardPlayerData> recurValLeaderboards(ValRegion region, ValActId act, long start, long cap) {
        long size = 200;
        if(start + 200 >= cap) { //if we are getting players at the very end of the leaderboards
            size = cap - start;
        }
        if(size <= 0) return Flux.empty();
        return Flux.concat(
                getLeaderboardData(region, act, start, size).flatMapMany(it -> Flux.fromIterable(it.players())),
                Flux.defer(() -> recurValLeaderboards(region, act, start + 200, cap))
        );
    }

    /**
     * Tests the API key of this client. Used in construction
     * @return
     */
    @Override
    public Mono<RiotAPIClient> test() { //todo make protected?
        return this.getValStatus(valRegion).thenReturn(this);
    }
}
