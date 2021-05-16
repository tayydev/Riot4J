package io.github.nathannorth.riot4j;

import io.github.nathannorth.riot4j.clients.RiotDevelopmentAPIClient;
import io.github.nathannorth.riot4j.objects.ValActId;
import io.github.nathannorth.riot4j.objects.ValRegion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TesterClass {
    public static void main(String[] args) {

        RiotDevelopmentAPIClient client = RiotDevelopmentAPIClient.builder()
                .addKey(getKeys().get(0))
                .build()
                .block();

        //client.getStatusUpdates(ValRegion.NORTH_AMERICA, Duration.ofSeconds(5)).doOnNext($ -> System.out.println($)).blockLast();

        client.getValLeaderboards(ValRegion.NORTH_AMERICA, ValActId.EPISODE_ONE_ACT_ONE, 0, 2000)
                .doOnNext(p -> System.out.println(p)).blockLast();



        //client.getUpdates(ValRegion.NORTH_AMERICA, Duration.ofSeconds(5)).doOnNext(thing -> System.out.println(thing)).blockLast();

//        PlatformStatusData blocked = client.getValStatus(ValRegion.NORTH_AMERICA).doOnNext(status -> System.out.println(status)).block();
//
//        System.out.println(client.getValUpdatePretty(ValRegion.NORTH_AMERICA, ValLocale.US_ENGLISH).block());
//
//        Mono.never().block();
//
//        client.getValLeaderboards(ValRegion.NORTH_AMERICA, ValActId.EPISODE_ONE_ACT_THREE, 0, 2000)
//                .map(playerData -> "Rank: #" + playerData.leaderboardRank() + " - Player: " + playerData.gameName().orElse("anonymous"))
//                .doOnNext(info -> System.out.println(info))
//                .blockLast();


//        client.getValStatus(ValRegion.ASIA_PACIFIC)
//                .doOnNext(status -> System.out.println(status))
//                .block();
//        //client.getValContent(ValRegion.NORTH_AMERICA, RiotLocale.US_ENGLISH).doOnNext(contentData -> System.out.println(contentData)).block();
//        client.getValContent(ValRegion.NORTH_AMERICA, ValLocale.US_ENGLISH)
//                .map(contentData -> TestingHelper.getAct(2, 2, contentData).id())
//                .doOnNext(id -> System.out.println("Id found: " + id))
//                .block();
//
//        ContentData data = client.getValContent(ValRegion.NORTH_AMERICA, ValLocale.FR_FRENCH).block();
//        for(int i = 1; i <= 2; i++) {
//            for(int ii = 1; ii <= 3; ii++) {
//                System.out.println("Id for " + i + ", " + ii);
//                System.out.println(TestingHelper.getAct(i, ii, data));
//            }
//        }
//
//        List<LeaderboardPlayerData> players = client.getValLeaderboards(ValRegion.NORTH_AMERICA, ValActId.EPISODE_TWO_ACT_THREE, 0, 10)
//                .map(stuff -> stuff.players()).block();
//        for(LeaderboardPlayerData player: players) {
//            System.out.println("Place " + player.leaderboardRank());
//            System.out.println("Name " + player.gameName());
//            System.out.println("RR " + player.rankedRating());
//        }
//        client.getLeaderboardFlux(ValRegion.NORTH_AMERICA, ValActId.EPISODE_TWO_ACT_TWO, 1, 634)
//                .doOnNext(data -> System.out.println("Rank #" + data.leaderboardRank() + ", Player: " + data.gameName().orElse("Anonymous")))
//                .subscribe();
//        Mono.never().block();
    }

    private static void demo() {
        final RiotDevelopmentAPIClient client = RiotDevelopmentAPIClient.builder()
                .addKey("TOKEN")
                .build()
                .block();

        client.getValLeaderboards(ValRegion.NORTH_AMERICA, ValActId.EPISODE_ONE_ACT_ONE, 0, 2000)
                .map(playerData -> "Rank: #" + playerData.leaderboardRank() + " Player: " + playerData.gameName().orElse("anonymous"))
                .doOnNext(info -> System.out.println(info))
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
