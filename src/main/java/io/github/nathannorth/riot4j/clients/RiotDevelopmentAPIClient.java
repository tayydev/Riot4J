package io.github.nathannorth.riot4j.clients;

import io.github.nathannorth.riot4j.json.Mapping;
import io.github.nathannorth.riot4j.json.valContent.ContentData;
import io.github.nathannorth.riot4j.json.valLeaderboard.LeaderboardData;
import io.github.nathannorth.riot4j.json.valLeaderboard.LeaderboardPlayerData;
import io.github.nathannorth.riot4j.json.valPlatform.PlatformStatusData;
import io.github.nathannorth.riot4j.objects.ValActId;
import io.github.nathannorth.riot4j.objects.ValLocale;
import io.github.nathannorth.riot4j.queues.CleanLimitedQueue;
import io.github.nathannorth.riot4j.objects.ValRegion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

/**
 * A DevelopmentClient is the core of this library. The client contains logic for rate limiting and mapping requests.
 * If there were to be a time when I got access to a production key, a sister client, the RiotProductionClient with
 * more complete access to the API would be written.
 */
public class RiotDevelopmentAPIClient extends RiotAPIClient {

    private final CleanLimitedQueue rateLimiter = new CleanLimitedQueue();
    RiotDevelopmentAPIClient(String token) {
        super(token);
    }

    /**
     * Initialize a new builder.
     * @return A builder object
     */
    public static RiotDevelopmentAPIClientBuilder builder() {
        return new RiotDevelopmentAPIClientBuilder();
    }

    /**
     * Gets VALORANT's status for a given region.
     * @param region Which VALORANT region to get data from
     * @return a PlatformStatusData containing any incidents / maintenance.
     */
    public Mono<PlatformStatusData> getValStatus(ValRegion region) {
        return rateLimiter.push(getValStatusRaw(token, region.getValue()))
                .map(Mapping.map(PlatformStatusData.class));
    }

    /**
     * Gets a ton of data from the game. Notably used to get ActIDs and game updates.
     * @param region Which VALORANT region to get data from
     * @param locale What language to return data in - technically optional on an API level but its just a headache to deal with every language at once
     * @return a content object containing all characters, maps, chromas, skin(Levels)s, equips, gameModes, spray(Levels)s, charm(Levels)s, playerCards, playerTitles, and acts.
     */
    public Mono<ContentData> getValContent(ValRegion region, ValLocale locale) {
        return rateLimiter.push(getValContentRaw(token, region.getValue(), locale.getValue()))
                .map(Mapping.map(ContentData.class));
    }

    /**
     * Gets a chunk of the leaderboards and surrounds it with some other helpful data. See also: {@link #getValLeaderboards(ValRegion, ValActId, int, long)} for a easier to use method.
     * @param region Which VALORANT region to get data from
     * @param actId Which act to get data from - will 404 from any act before Episode Two
     * @param startIndex Where in the leaderboard get players from
     * @param size How many players to get
     * @return a leaderboard object with a list of leaderboardplayers
     */
    public Mono<LeaderboardData> getValLeaderboardChunk(ValRegion region, ValActId actId, int startIndex, int size) {
        if(startIndex < 0) throw new IndexOutOfBoundsException("Start cannot be negative!");
        if(size > 200) throw new IndexOutOfBoundsException("Size cannot be greater than 200!");
        return rateLimiter.push(getValLeaderboardRaw(token, region.getValue(), actId.getValue(), size + "", startIndex + ""))
                .map(Mapping.map(LeaderboardData.class));
    }

    /**
     * Returns a flux of players in between two positions on the leaderboard. See also: {@link #getValLeaderboardChunk(ValRegion, ValActId, int, int)} if you need to get additional data besides just player objects (eg total players).
     * @param region Which VALORANT region to get data from
     * @param id Which act to get data from - will 404 from any act before Episode Two
     * @param startIndex Index for the start of the range you want data from (inclusive)
     * @param endIndex Index of the end of the range you want data from (noninclusive)
     * @return a flux of player objects from startIndex to endIndex
     */
    public Flux<LeaderboardPlayerData> getValLeaderboards(ValRegion region, ValActId id, int startIndex, long endIndex) {
        if(startIndex < 0) throw new IndexOutOfBoundsException("Start cannot be negative!");
        if(startIndex >= endIndex) throw new IndexOutOfBoundsException("Invalid range!");
        return getValLeaderboardChunk(region, id, 0, 1)
                //todo make more efficient
                .flatMapMany(data -> {
                    ArrayList<Integer> temp = new ArrayList<>();
                    for(int i = startIndex; i < (int) Math.min(endIndex, data.totalPlayers() - 1); i += 200) {
                        temp.add(i);
                    }
                    return Flux.fromIterable(temp);
                })
                        .flatMap(num -> getValLeaderboardChunk(region, id, num, (int) Math.min(endIndex - num, 200))
                                .flatMapMany(result -> Flux.fromIterable(result.players())), 1);
    }
}
