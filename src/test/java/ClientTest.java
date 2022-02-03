import io.github.nathannorth.riot4j.api.account.RiotAccount;
import io.github.nathannorth.riot4j.api.match.ValMatch;
import io.github.nathannorth.riot4j.api.match.ValMatchlist;
import io.github.nathannorth.riot4j.clients.RiotClientBuilder;
import io.github.nathannorth.riot4j.clients.RiotDevelopmentAPIClient;
import io.github.nathannorth.riot4j.clients.RiotProductionAPIClient;
import io.github.nathannorth.riot4j.enums.ValLocale;
import io.github.nathannorth.riot4j.enums.ValQueueId;
import io.github.nathannorth.riot4j.enums.ValRecentQueue;
import io.github.nathannorth.riot4j.enums.ValRegion;
import io.github.nathannorth.riot4j.json.riotAccount.ActiveShardData;
import io.github.nathannorth.riot4j.json.valContent.ContentData;
import io.github.nathannorth.riot4j.json.valLeaderboard.LeaderboardData;
import io.github.nathannorth.riot4j.json.valMatch.RecentMatchesData;
import io.github.nathannorth.riot4j.json.valPlatform.PlatformStatusData;
import io.github.nathannorth.riot4j.objects.ValActId;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

public class ClientTest {
    @Test
    public void testDevClient() {
        final RiotDevelopmentAPIClient client = RiotClientBuilder.create()
                .token(getKeys().get(0))
                .buildDevClient()
                .block();
        assert client != null;

        client.getValLeaderboards(ValRegion.NORTH_AMERICA, ValActId.EPISODE_TWO_ACT_THREE, 12370)
                .take(600)
                .doOnNext(e -> System.out.println(e))
                .blockLast();

        LeaderboardData leaderboard = client.getValLeaderboardChunk(ValRegion.NORTH_AMERICA, ValActId.EPISODE_TWO_ACT_THREE, 100, 100).block();

        client.getStatusUpdates(ValRegion.NORTH_AMERICA, Duration.ofSeconds(1)).blockFirst();

        PlatformStatusData data = client.getValStatus(ValRegion.NORTH_AMERICA).block();

        ContentData content = client.getValContent(ValRegion.NORTH_AMERICA, ValLocale.US_ENGLISH).block();

        client.getActs().map(acts -> acts.getActId(3, 1)).flatMapMany(actId ->
                client.getValLeaderboards(ValRegion.NORTH_AMERICA, actId, 0))
                .take(1000)
                .blockLast();
    }

    @Test
    public void testProductionClient() {
        final RiotProductionAPIClient client = RiotClientBuilder.create()
                .token(getKeys().get(1))
                .valRegion(ValRegion.BRAZIL)
                .buildProductionClient()
                .block();
        assert client != null;

        RiotAccount nate = client.getRiotAccountByName("nate", "asdf").block();

        ActiveShardData loc = client.getActiveShardsVal(nate.puuid()).block();

        ValMatchlist matchList = client.getMatchList(loc.activeShard(), nate.puuid()).block();

        RecentMatchesData recentMatchesData = client.getRecentMatches(ValRegion.NORTH_AMERICA, ValRecentQueue.UNRATED).block();

        ValMatch match = client.getMatch(loc.activeShard(), matchList.history().get(0).matchId()).block();

        ValMatch dm = client.getMatch(loc.activeShard(), matchList.matches().filter(e -> e.queueId().equals(ValQueueId.DEATHMATCH)).map(e -> e.matchId()).next().block()).block();

        client.getRecentMatches(ValRegion.NORTH_AMERICA, ValRecentQueue.COMPETITIVE).block();
    }

    @Test
    public void cacheTest() {
        final RiotProductionAPIClient client = RiotClientBuilder.create()
                .token(getKeys().get(1))
                .buildProductionClient()
                .block();
        assert client != null;

        RiotAccount nate = client.getRiotAccountByName("nate", "asdf").block();

        nate.getRegion().block();

        System.out.println("First region got.");

        nate.getRegion().block();

        System.out.println("Second region got.");

        ValMatchlist matchList = client.getMatchList(nate).block();

        matchList.history().get(0).getValMatch().block();

        System.out.println("First match got.");

        matchList.history().get(0).getValMatch().block();

        System.out.println("Second match got.");
    }

    @Test
    public void speedTest() {
        final RiotDevelopmentAPIClient client = RiotClientBuilder.create()
                .token(getKeys().get(0))
                .buildDevClient()
                .block();
        assert client != null;

        System.out.println("Testing rate limiting:");

        Flux.interval(Duration.ofSeconds(5)).flatMap(
                sec -> client.getValContent(ValRegion.NORTH_AMERICA, ValLocale.US_ENGLISH))
                .doOnNext(e -> System.out.println("This message should play at regular 5 second intervals, regardless of rate limiting for the ranked endpoint..."))
                .subscribe();

        client.getValLeaderboards(ValRegion.NORTH_AMERICA, ValActId.EPISODE_TWO_ACT_THREE, 0)
                .take(2000)
                .blockLast();
    }

    //keys.txt is stored in root dir and holds instance-specific data (eg. bot token)
    private static List<String> keys = null;
    public static List<String> getKeys() {
        if (keys == null) {
            try {
                keys = Files.readAllLines(Paths.get("./keys.txt"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //filter out things we commented out in our keys
            for (int i = keys.size() - 1; i >= 0; i--) {
                if (keys.get(i).indexOf('#') == 0) keys.remove(i);
            }
        }
        return keys;
    }
}
