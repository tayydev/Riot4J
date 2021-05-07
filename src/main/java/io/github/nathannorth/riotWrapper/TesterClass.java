package io.github.nathannorth.riotWrapper;

import io.github.nathannorth.riotWrapper.clients.RiotDevelopmentAPIClient;
import io.github.nathannorth.riotWrapper.json.valContent.ContentData;
import io.github.nathannorth.riotWrapper.json.valLeaderboard.LeaderboardPlayerData;
import io.github.nathannorth.riotWrapper.objects.ValActId;
import io.github.nathannorth.riotWrapper.objects.ValLocale;
import io.github.nathannorth.riotWrapper.objects.ValRegion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TesterClass {
    public static void main(String[] args) {

        //todo note when i rename something

        RiotDevelopmentAPIClient client = RiotDevelopmentAPIClient.builder()
                .addToken(getKeys().get(0))
                .build()
                .block();

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
        client.getLeaderboardInfinite(ValRegion.NORTH_AMERICA, ValActId.EPISODE_TWO_ACT_ONE, 234, 698)
                .doOnNext(data -> System.out.println("Rank #" + data.leaderboardRank() + ", Player: " + data.gameName().orElse("Anonymous")))
                .subscribe();
        Mono.never().block();
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
