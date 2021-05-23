import io.github.nathannorth.riot4j.clients.RiotDevelopmentAPIClient;
import io.github.nathannorth.riot4j.objects.ValActId;
import io.github.nathannorth.riot4j.objects.ValLocale;
import io.github.nathannorth.riot4j.objects.ValRegion;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

public class ClientTest {
    @Test
    public void testApis() {
        final RiotDevelopmentAPIClient client = RiotDevelopmentAPIClient.builder()
                .addKey(getKeys().get(0))
                .build()
                .block();
        assert client != null;

        client.getStatusUpdates(ValRegion.NORTH_AMERICA, Duration.ofSeconds(1)).blockFirst();

        client.getValContent(ValRegion.NORTH_AMERICA, ValLocale.US_ENGLISH).block();

        client.getActs().map(acts -> acts.getLatestActId()).flatMapMany(actId ->
                client.getValLeaderboards(ValRegion.NORTH_AMERICA, actId, 0, 1000))
                .blockLast();
    }

    //@Test
    public void speedTest() {
        final RiotDevelopmentAPIClient client = RiotDevelopmentAPIClient.builder()
                .addKey(getKeys().get(0))
                .build()
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
