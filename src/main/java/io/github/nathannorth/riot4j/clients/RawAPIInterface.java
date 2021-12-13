package io.github.nathannorth.riot4j.clients;

import reactor.netty.http.client.HttpClient;

/**
 * The RawAPIInterface contains partial methods for web requests. It does not contain rate limiting, mapping logic, or convenience methods.
 */
public abstract class RawAPIInterface {
    final HttpClient webClient = HttpClient.create();

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

    HttpClient.ResponseReceiver<?> getRecentMatchesRaw(String token, String region, String queue) {
        return webClient
                .headers(head -> head.add("X-Riot-Token", token))
                .get()
                .uri("https://" + region + ".api.riotgames.com/val/match/v1/recent-matches/by-queue/" + queue);
    }

    HttpClient.ResponseReceiver<?> getMatchListRaw(String token, String region, String puuid) {
        return webClient
                .headers(head -> head.add("X-Riot-Token", token))
                .get()
                .uri("https://" + region + ".api.riotgames.com/val/match/v1/matchlists/by-puuid/" + puuid);
    }

    HttpClient.ResponseReceiver<?> getMatchRaw(String token, String region, String matchId) {
        return webClient
                .headers(head -> head.add("X-Riot-Token", token))
                .get()
                .uri("https://" + region + ".api.riotgames.com/val/match/v1/matches/" + matchId);
    }

    HttpClient.ResponseReceiver<?> getAccountByNameRaw(String token, String region, String name, String tagLine) {
        return webClient
                .headers(head -> head.add("X-Riot-Token", token))
                .get()
                .uri("https://" + region + ".api.riotgames.com/riot/account/v1/accounts/by-riot-id/" + name + "/" + tagLine);
    }
}
