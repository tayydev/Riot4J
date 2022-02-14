package tech.nathann.riot4j;

import tech.nathann.riot4j.api.match.MatchlistEntry;
import tech.nathann.riot4j.api.match.ValMatchlist;
import tech.nathann.riot4j.clients.RiotClientBuilder;
import tech.nathann.riot4j.clients.RiotProductionAPIClient;
import tech.nathann.riot4j.enums.ValQueueId;
import tech.nathann.riot4j.enums.ValRegion;
import tech.nathann.riot4j.objects.ValActId;

import java.util.stream.Collectors;

public class Example {
    public static void main(String[] args) {
        final RiotProductionAPIClient client = RiotClientBuilder.create()
                .token(args[0])
                .buildProductionClient()
                .block();

        client.getRiotAccountByName("nate", "asdf")
                .flatMap(account ->
                        account.getMatchList().flatMapMany(ValMatchlist::matches)
                                .filter(entry -> entry.queueId().equals(ValQueueId.COMPETITIVE))
                                .take(10)
                                .flatMap(MatchlistEntry::getValMatch)
                                .map(match -> match.getStatisticalPlayer(account.puuid()).getHeadShotPercentage())
                                .collect(Collectors.averagingDouble(Float::doubleValue))
                                .map(result -> Math.round(result * 100))
                )
                .doOnNext(percent -> System.out.println("Average headshot percentage: " + percent + "%"))
                .block();

        client.getValLeaderboards(ValRegion.NORTH_AMERICA, ValActId.EPISODE_TWO_ACT_THREE, 0)
                .take(2000)
                .map(player ->
                        "Rank: #" + player.leaderboardRank() +
                                " - Name: " + player.gameName().orElse("Anonymous")
                )
                .doOnNext(System.out::println)
                .blockLast();
    }
}
