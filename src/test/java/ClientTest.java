import io.github.nathannorth.riot4j.clients.RiotDevelopmentAPIClient;
import io.github.nathannorth.riot4j.clients.RiotProductionAPIClient;
import io.github.nathannorth.riot4j.enums.ValQueue;
import io.github.nathannorth.riot4j.json.riotAccount.RiotAccountData;
import io.github.nathannorth.riot4j.json.valMatch.MatchData;
import io.github.nathannorth.riot4j.json.valMatch.MatchlistData;
import io.github.nathannorth.riot4j.json.valMatch.RecentMatchesData;
import io.github.nathannorth.riot4j.objects.ValActId;
import io.github.nathannorth.riot4j.enums.ValLocale;
import io.github.nathannorth.riot4j.enums.ValRegion;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

public class ClientTest {
    @Test
    public void testDevClient() {
        final RiotDevelopmentAPIClient client = RiotDevelopmentAPIClient.builder()
                .addKey(getKeys().get(0))
                .getDevBuilder()
                .block();
        assert client != null;

        client.getStatusUpdates(ValRegion.NORTH_AMERICA, Duration.ofSeconds(1)).blockFirst();

        client.getValContent(ValRegion.NORTH_AMERICA, ValLocale.US_ENGLISH).block();

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

        MatchlistData matchList = client.getMatchList(ValRegion.NORTH_AMERICA, "6kUT4ZLSpWUc0FG2zzfQFbNOHxV2_m55JVJQbTqRUolkNuCaAVP5WqDsir4s4BLBQrwuFpZebarWLQ").block();

        System.out.println(matchList);

        MatchData match = client.getMatch(ValRegion.NORTH_AMERICA, "f960e3ef-388f-4847-8dea-daf299768cad").block();

        System.out.println(match);

        RiotAccountData nate = client.getRiotAccount("nate", "asdf").block();

        RecentMatchesData matches = client.getRecentMatches(ValRegion.NORTH_AMERICA, ValQueue.COMPETITIVE).block();

        System.out.println(nate + "\n" + matches);
    }

    //@Test
    public void speedTest() {
        final RiotDevelopmentAPIClient client = RiotDevelopmentAPIClient.builder()
                .addKey(getKeys().get(0))
                .getDevBuilder()
                .block();
        assert client != null;

        client.getValLeaderboards(ValRegion.NORTH_AMERICA, ValActId.EPISODE_TWO_ACT_THREE, 0, 2000)
                .doOnNext(e -> System.out.println(e))
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
