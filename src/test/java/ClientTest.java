import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import tech.nathann.riot4j.api.account.RiotAccount;
import tech.nathann.riot4j.api.content.ValContent;
import tech.nathann.riot4j.api.match.ValMatch;
import tech.nathann.riot4j.api.match.ValMatchlist;
import tech.nathann.riot4j.clients.RiotClientBuilder;
import tech.nathann.riot4j.clients.RiotDevelopmentAPIClient;
import tech.nathann.riot4j.clients.RiotProductionAPIClient;
import tech.nathann.riot4j.enums.ValLocale;
import tech.nathann.riot4j.enums.ValQueueId;
import tech.nathann.riot4j.enums.ValRecentQueue;
import tech.nathann.riot4j.enums.regions.ValRegion;
import tech.nathann.riot4j.json.riotAccount.ActiveShardData;
import tech.nathann.riot4j.json.valMatch.RecentMatchesData;
import tech.nathann.riot4j.json.valPlatform.PlatformStatusData;
import tech.nathann.riot4j.objects.ValActId;

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

        client.getStatusUpdates(ValRegion.NORTH_AMERICA, Duration.ofSeconds(1)).blockFirst();

        PlatformStatusData data = client.getValStatus(ValRegion.NORTH_AMERICA).block();

        ValContent content = client.getValContent(ValRegion.NORTH_AMERICA, ValLocale.US_ENGLISH).block();

        client.getValLeaderboards(ValRegion.NORTH_AMERICA, content.getLatestAct(), 0)
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

        ValMatchlist matchList = nate.getMatchList().block();

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
