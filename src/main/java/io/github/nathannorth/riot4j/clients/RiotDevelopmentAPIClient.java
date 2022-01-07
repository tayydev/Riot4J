package io.github.nathannorth.riot4j.clients;

import io.github.nathannorth.riot4j.enums.ValLocale;
import io.github.nathannorth.riot4j.enums.ValRegion;
import io.github.nathannorth.riot4j.exceptions.IncompleteBuilderException;
import io.github.nathannorth.riot4j.exceptions.InvalidTokenException;
import io.github.nathannorth.riot4j.exceptions.WebFailure;
import io.github.nathannorth.riot4j.json.Mapping;
import io.github.nathannorth.riot4j.json.riotAccount.ActiveShardData;
import io.github.nathannorth.riot4j.json.riotAccount.RiotAccountData;
import io.github.nathannorth.riot4j.json.valContent.ContentData;
import io.github.nathannorth.riot4j.json.valLeaderboard.LeaderboardData;
import io.github.nathannorth.riot4j.json.valLeaderboard.LeaderboardPlayerData;
import io.github.nathannorth.riot4j.json.valPlatform.PlatformStatusData;
import io.github.nathannorth.riot4j.objects.Translator;
import io.github.nathannorth.riot4j.objects.ValActId;
import io.github.nathannorth.riot4j.objects.ValActIdGroup;
import io.github.nathannorth.riot4j.objects.ValStatusUpdateEvent;
import io.github.nathannorth.riot4j.queues.BucketManager;
import io.github.nathannorth.riot4j.queues.RateLimits;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;

/**
 * A DevelopmentClient is the core of this library. The client contains logic for rate limiting and mapping requests.
 * If there were to be a time when I got access to a production key, a sister client, the RiotProductionClient with
 * more complete access to the API would be written.
 */
public class RiotDevelopmentAPIClient extends RiotAPIClient {

    protected final BucketManager buckets = new BucketManager();

    protected RiotDevelopmentAPIClient(String token) {
        super(token);
    }


    /**
     * Initialize a new builder.
     * @return A builder object
     */
    public static RiotDevelopmentAPIClientBuilder getDevBuilder() {
        return new RiotDevelopmentAPIClientBuilder();
    }

    public Mono<ActiveShardData> getActiveShardsVal(String puuid) {
        return getActiveShardsByGame("americas", "val", puuid);
    }

    public Mono<ActiveShardData> getActiveShardsByGame(String riotRegion, String game, String puuid) {
        return buckets.pushToBucket(RateLimits.ACCOUNT_ACTIVE_SHARD_BY_GAME, getActiveShardsByGame(token, riotRegion, game, puuid))
                .map(Mapping.map(ActiveShardData.class));
    }

    /**
     *
     * @param name
     * @param tagLine
     * @return
     */
    public Mono<RiotAccountData> getRiotAccountByName(String name, String tagLine) {
        return getRiotAccountByName("americas", name, tagLine);
    }

    /**
     * Gets a riot account from a name/tagline. note the parameter is a RIOT REGION, not a VAL REGION. There is no enum
     * class for Riot Regions at this time. See also: {@link #getRiotAccountByName(String, String)} for an alternative that
     * defaults to americas endpoint
     * @param riotRegion a string representation of the desired RIOT region (eg 'americas' instead of 'na')
     * @param name username to search for
     * @param tagLine tag to search for
     * @return returns a {@link WebFailure} with error code 404 if user not found
     */
    public Mono<RiotAccountData> getRiotAccountByName(String riotRegion, String name, String tagLine) {
        return buckets.pushToBucket(RateLimits.ACCOUNT_BY_RIOT_ID, getAccountByNameRaw(token, riotRegion, name, tagLine))
                .map(Mapping.map(RiotAccountData.class));
    }

    //todo javadoc these and maybe address riot region vs other region
    public Mono<RiotAccountData> getRiotAccountByPuuid(String puuid) {
        return getRiotAccountByPuuid("americas", puuid);
    }
    public Mono<RiotAccountData> getRiotAccountByPuuid(String riotRegion, String puuid) {
        return buckets.pushToBucket(RateLimits.ACCOUNT_BY_PUUID, getAccountByPuuidRaw(token, riotRegion, puuid))
                .map(Mapping.map(RiotAccountData.class));
    }

    /**
     * Gets VALORANT's status for a given region.
     * @param region Which VALORANT region to get data from
     * @return a PlatformStatusData containing any incidents / maintenance.
     */
    public Mono<PlatformStatusData> getValStatus(ValRegion region) {
        return buckets.pushToBucket(RateLimits.VAL_STATUS, getValStatusRaw(token, region.toString()))
                .map(Mapping.map(PlatformStatusData.class));
    }

    private PlatformStatusData lastData = null;

    /**
     * Gets a flux that emits statusUpdateEvents whenever riots status for a given region changes. Checks are made on a given duration.
     * @param region Region to get status from
     * @param duration Duration to check for updates, not recommended to be set to anything under 1 second to avoid overflowing the ratelimit sink
     * @return
     */
    public Flux<ValStatusUpdateEvent> getStatusUpdates(ValRegion region, Duration duration) { //todo test or remove this
        return Flux.interval(duration).flatMap(num -> getValStatus(region)
                .filter(status -> !status.equals(lastData)) //data must be changed
                .map(newStatus -> {
                    PlatformStatusData oldStatus = lastData;
                    lastData = newStatus;
                    return new ValStatusUpdateEvent(oldStatus, newStatus);
                }));
    }

    /**
     * Gets a ton of data from the game. Notably used to get ActIDs and game updates.
     * @param region Which VALORANT region to get data from
     * @param locale What language to return data in - technically optional on an API level but its just a headache to deal with every language at once
     * @return a content object containing all characters, maps, chromas, skin(Levels)s, equips, gameModes, spray(Levels)s, charm(Levels)s, playerCards, playerTitles, and acts.
     */
    public Mono<ContentData> getValContent(ValRegion region, ValLocale locale) {
        return buckets.pushToBucket(RateLimits.VAL_CONTENT, getValContentRaw(token, region.toString(), locale.toString()))
                .map(Mapping.map(ContentData.class));
    }

    /**
     * Get all ValActIds in an organized object
     * @return a mono that evaluates to a ValActIdSet
     */
    public Mono<ValActIdGroup> getActs() {
        return getValContent(ValRegion.NORTH_AMERICA, ValLocale.US_ENGLISH)
                .map(contentData -> new ValActIdGroup(contentData.acts()));
    }

    /**
     * Get a new translator object based on latest data
     * @return a translator object up do date as of when the mono evaluates
     */
    public Mono<Translator> getTranslator() {
        return getValContent(ValRegion.NORTH_AMERICA, ValLocale.US_ENGLISH)
                .map(contentData -> new Translator(contentData));
    }

    /**
     * Gets a chunk of the leaderboards and surrounds it with some other helpful data. See also: {@link #getValLeaderboards(ValRegion, ValActId, long, long)} for a user friendly method.
     * @param region Which VALORANT region to get data from
     * @param actId Which act to get data from - will 404 from any act before Episode Two
     * @param startIndex Where in the leaderboard get players from
     * @param size How many players to get
     * @return a leaderboard object with a list of LeaderboardPlayerData
     */
    public Mono<LeaderboardData> getValLeaderboardChunk(ValRegion region, ValActId actId, long startIndex, long size) {
        if(startIndex < 0) return Mono.error(new IndexOutOfBoundsException("Start cannot be negative!"));
        if(size > 200) return Mono.error(new IndexOutOfBoundsException("Size cannot be greater than 200!"));
        //todo more robust checks
        return buckets.pushToBucket(RateLimits.VAL_RANKED, getValLeaderboardRaw(token, region.toString(), actId.toString(), size + "", startIndex + ""))
                .map(Mapping.map(LeaderboardData.class));
    }

    /**
     * Returns a flux of players in between two positions on the leaderboard. See also: {@link #getValLeaderboardChunk(ValRegion, ValActId, long, long)} if you need to get additional data besides just player objects (eg total players).
     * @param region Which VALORANT region to get data from
     * @param id Which act to get data from - will 404 from any act before Episode Two
     * @param startIndex Index for the start of the range you want data from (inclusive)
     * @param endIndex Index of the end of the range you want data from (noninclusive)
     * @return a flux of player objects from startIndex to endIndex
     */
    public Flux<LeaderboardPlayerData> getValLeaderboards(ValRegion region, ValActId id, long startIndex, long endIndex) {
        if(startIndex < 0) return Flux.error(new IndexOutOfBoundsException("Start cannot be negative!"));
        if(startIndex >= endIndex) return Flux.error(new IndexOutOfBoundsException("Invalid range!"));
        return getValLeaderboardChunk(region, id, 0, 1)
                .flatMapMany(data -> {
                    ArrayList<Long> temp = new ArrayList<>();
                    for(long i = startIndex; i < Math.min(endIndex, data.totalPlayers() - 1); i += 200) {
                        temp.add(i);
                    }
                    if(temp.size() == 0) return Flux.error(new IndexOutOfBoundsException("Outside of leaderboard range!"));
                    return Flux.fromIterable(temp);
                })
                        .flatMap(num -> getValLeaderboardChunk(region, id, num, Math.min(endIndex - num, 200))
                                .flatMapMany(result -> Flux.fromIterable(result.players())), 1);
    }

    public static class RiotDevelopmentAPIClientBuilder {
        private String key = null;

        /**
         * Gives a builder object an API key
         * @param key your API key
         * @return your builder with an updated API key
         */
        public RiotDevelopmentAPIClientBuilder addKey(String key) {
                this.key = key;
                return this;
            }

        /**
         * Returns a mono of your client that when evaluated tests your api key and returns a completed RiotDevelopmentAPIClient
         * @return a RiotDevelopmentAPIClient
         */
        public Mono<RiotDevelopmentAPIClient> build() {
                if (key == null) return Mono.error(new IncompleteBuilderException("Did not specify token."));
                RiotDevelopmentAPIClient temp = new RiotDevelopmentAPIClient(key);
                return temp.getValStatus(ValRegion.NORTH_AMERICA)
                        .onErrorResume(e -> {
                            if (e instanceof WebFailure) {
                                if(((WebFailure) e).getResponse().status().code() == 403)
                                    return Mono.error(new InvalidTokenException("The token specified is not valid."));
                            }
                            return Mono.error(e);
                        })
                        .then(Mono.just(temp));
        }

        /**
         * if you like to live life on the edge (or want to save resources) this method is for you
         * @return a RiotDevelopmentAPIClient WITHOUT testing its API key.
         */
        public RiotDevelopmentAPIClient buildUnsafe() {
            if (key == null) throw new IncompleteBuilderException("Did not specify token.");
            return new RiotDevelopmentAPIClient(key);
        }
    }
}
