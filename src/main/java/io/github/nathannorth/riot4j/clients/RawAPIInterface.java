package io.github.nathannorth.riot4j.clients;

import reactor.netty.http.client.HttpClient;

/**
 * The RawAPIInterface contains partial methods for web requests. It does not contain rate limiting or mapping logic.
 */
public abstract class RawAPIInterface {
    final HttpClient webClient = HttpClient.create();;

    HttpClient.ResponseReceiver<?> getValStatusRaw(String token, String region) {
        return webClient
                .headers(head -> head.add("X-Riot-Token", token))
                .get()
                .uri("https://" + region + ".api.riotgames.com/val/status/v1/platform-data");
    }

    HttpClient.ResponseReceiver<?> getValContentRaw(String token, String region, String locale) {
        return webClient
                .headers(head -> head.add("X-Riot-Token", token))
                .get()
                .uri("https://" + region + ".api.riotgames.com/val/content/v1/contents?locale=" + locale);
    }

    HttpClient.ResponseReceiver<?> getValLeaderboardRaw(String token, String region, String actId, String size, String startIndex) {
        return webClient
                .headers(head -> head.add("X-Riot-Token", token))
                .get()
                .uri("https://" + region + ".api.riotgames.com/val/ranked/v1/leaderboards/by-act/" + actId + "?size=" + size + "&startIndex=" + startIndex);
    }
}
