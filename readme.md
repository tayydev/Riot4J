# Riot4J
Riot4J is a work-in-progress reactive Java wrapper for the Riot API with a specific focus on the VALORANT APIs.
Riot4J is built with [Project Reactor's](https://projectreactor.io/) [Netty](https://github.com/reactor/reactor-netty) and
[Immutable](https://immutables.github.io/) [Jackson](https://github.com/FasterXML/jackson-databind) Objects.
# Example
This example gets the top 2000 ranked VALORANT players in North America during Episode 2 Act 3 and prints their name and rank.
```java
public final class Example {
    public static void main(String[] args) {
        final RiotDevelopmentAPIClient client = RiotDevelopmentAPIClient.builder()
                .addKey(args[0])
                .build()
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
- **Reactive** - I'm fairly confident Riot4J follows the [reactive-streams](http://www.reactive-streams.org/) protocol (I haven't actually read the whole thing so who knows)
- **Automatic Rate Limiting** - Riot4J automatically handles ratelimiting, so you shouldn't have to worry about those pesky 429 errors.
- **Other Great Features** - It exists?
# Installation
tbd
# Disclaimer
Riot4J isn't endorsed by Riot Games and doesn't reflect the views or opinions of Riot Games or anyone officially involved in producing or managing Riot Games properties. Riot Games, and all associated properties are trademarks or registered trademarks of Riot Games, Inc.