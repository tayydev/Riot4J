import io.github.nathannorth.riot4j.clients.RiotDevelopmentAPIClient;
import io.github.nathannorth.riot4j.clients.RiotProductionAPIClient;
import io.github.nathannorth.riot4j.enums.ValLocale;
import io.github.nathannorth.riot4j.enums.ValQueue;
import io.github.nathannorth.riot4j.enums.ValRegion;
import io.github.nathannorth.riot4j.json.riotAccount.RiotAccountData;
import io.github.nathannorth.riot4j.json.valContent.ContentData;
import io.github.nathannorth.riot4j.json.valMatch.MatchData;
import io.github.nathannorth.riot4j.json.valMatch.MatchlistData;
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
        final RiotDevelopmentAPIClient client = RiotDevelopmentAPIClient.getDevBuilder()
                .addKey(getKeys().get(0))
                .build()
                .block();
        assert client != null;

        client.getStatusUpdates(ValRegion.NORTH_AMERICA, Duration.ofSeconds(1)).blockFirst();

        ContentData content = client.getValContent(ValRegion.NORTH_AMERICA, ValLocale.US_ENGLISH).block();

        client.getActs().map(acts -> acts.getActId(3, 1)).flatMapMany(actId ->
                client.getValLeaderboards(ValRegion.NORTH_AMERICA, actId, 0, 1000))
                .blockLast();
    }

    @Test
    public void testProductionClient() {
        final RiotProductionAPIClient client = RiotProductionAPIClient.getProdBuilder()
                .addKey(getKeys().get(1))
                .build()
                .block();
        assert client != null;

        RiotAccountData nate = client.getRiotAccount("nate", "asdf").block();

        MatchlistData matchList = client.getMatchList(ValRegion.NORTH_AMERICA, nate.puuid()).block();

        MatchData match = client.getMatch(ValRegion.NORTH_AMERICA, matchList.history().get(0).matchId()).block();

        client.getRecentMatches(ValRegion.NORTH_AMERICA, ValQueue.COMPETITIVE).block();
    }

    @Test
    public void speedTest() {
        final RiotDevelopmentAPIClient client = RiotDevelopmentAPIClient.getDevBuilder()
                .addKey(getKeys().get(0))
                .build()
                .block();
        assert client != null;

        System.out.println("Testing rate limiting:");

        Flux.interval(Duration.ofSeconds(5)).flatMap(
                sec -> client.getValContent(ValRegion.NORTH_AMERICA, ValLocale.US_ENGLISH))
                .doOnNext(e -> System.out.println("This message should play at regular 5 second intervals, regardless of rate limiting for the ranked endpoint..."))
                .subscribe();

        client.getValLeaderboards(ValRegion.NORTH_AMERICA, ValActId.EPISODE_TWO_ACT_THREE, 0, 2000)
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
