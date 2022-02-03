# Riot4J
Riot4J is a work-in-progress reactive Java wrapper for the Riot API with a specific focus on the VALORANT APIs.
Riot4J is built with [Project Reactor's](https://projectreactor.io/) [Netty](https://github.com/reactor/reactor-netty) and
[Immutable](https://immutables.github.io/) [Jackson](https://github.com/FasterXML/jackson-databind) Objects.
# Examples
This example finds the average headshot percentage for a player in Competitive queue:
```java
public class Example1 {
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
    }
}

```
This example gets the top 2000 ranked VALORANT players in North America during Episode 2 Act 3 and prints their name and rank:
```java
public class Example2 {
    public static void main(String[] args) {
        final RiotDevelopmentAPIClient client = RiotClientBuilder.create()
                .token(args[0])
                .buildDevClient()
                .block();

        client.getValLeaderboards(ValRegion.NORTH_AMERICA, ValActId.EPISODE_TWO_ACT_THREE, 0, 2000)
                .map(player -> 
                        "Rank: #" + player.leaderboardRank() + 
                        " - Name: " + player.gameName().orElse("Anonymous")
                )
                .doOnNext(info -> System.out.println(info))
                .blockLast();
    }
}
``` 
# Features
- **Reactive** - Riot4J follows the [reactive-streams](http://www.reactive-streams.org/) protocol
- **Automatic Rate Limiting** - Riot4J automatically handles rate limiting *per bucket* so you shouldn't have to worry about those pesky 429 errors
- **Exponential Backoff** - Riot4J automatically retries when Riot's API is returning 500s 
- **Convenience Methods** - Riot4J extends and connects raw data mappings in intuitive, useful ways
# Build
The only current supported method of implementing Riot4J is through Gradle's [composite builds](https://docs.gradle.org/current/userguide/composite_builds.html). Riot4J is still receiving breaking API changes regularly, and should be used for experimentation only. Please get in touch if you are interested in using this in a more official capacity.
# Disclaimer
Riot4J isn't endorsed by Riot Games and doesn't reflect the views or opinions of Riot Games or anyone officially involved in producing or managing Riot Games properties. Riot Games, and all associated properties are trademarks or registered trademarks of Riot Games, Inc.