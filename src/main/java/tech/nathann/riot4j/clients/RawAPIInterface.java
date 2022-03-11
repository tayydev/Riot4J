package tech.nathann.riot4j.clients;

import reactor.netty.http.client.HttpClient;

/**
 * The RawAPIInterface contains partial methods for web requests. It does not contain rate limiting, mapping logic, or convenience methods.
 */
public abstract class RawAPIInterface {

    private final HttpClient webClient;

    public RawAPIInterface(HttpClient webClient) {
        this.webClient = webClient;
    }

    protected HttpClient.ResponseReceiver<?> getValStatusRaw(String token, String region) {
        return webClient
                .headers(head -> head.add("X-Riot-Token", token))
                .get()
                .uri("https://" + region + ".api.riotgames.com/val/status/v1/platform-data");
    }

    protected HttpClient.ResponseReceiver<?> getValContentRaw(String token, String region, String locale) {
        return webClient
                .headers(head -> head.add("X-Riot-Token", token))
                .get()
                .uri("https://" + region + ".api.riotgames.com/val/content/v1/contents?locale=" + locale);
    }

    protected HttpClient.ResponseReceiver<?> getValLeaderboardRaw(String token, String region, String actId, String size, String startIndex) {
        return webClient
                .headers(head -> head.add("X-Riot-Token", token))
                .get()
                .uri("https://" + region + ".api.riotgames.com/val/ranked/v1/leaderboards/by-act/" + actId + "?size=" + size + "&startIndex=" + startIndex);
    }

    protected HttpClient.ResponseReceiver<?> getRecentMatchesRaw(String token, String region, String queue) {
        return webClient
                .headers(head -> head.add("X-Riot-Token", token))
                .get()
                .uri("https://" + region + ".api.riotgames.com/val/match/v1/recent-matches/by-queue/" + queue);
    }

    protected HttpClient.ResponseReceiver<?> getMatchListRaw(String token, String region, String puuid) {
        return webClient
                .headers(head -> head.add("X-Riot-Token", token))
                .get()
                .uri("https://" + region + ".api.riotgames.com/val/match/v1/matchlists/by-puuid/" + puuid);
    }

    protected HttpClient.ResponseReceiver<?> getMatchRaw(String token, String region, String matchId) {
        return webClient
                .headers(head -> head.add("X-Riot-Token", token))
                .get()
                .uri("https://" + region + ".api.riotgames.com/val/match/v1/matches/" + matchId);
    }

    protected HttpClient.ResponseReceiver<?> getAccountByNameRaw(String token, String endpoint, String name, String tagLine) {
        return webClient
                .headers(head -> head.add("X-Riot-Token", token))
                .get()
                .uri("https://" + endpoint + ".api.riotgames.com/riot/account/v1/accounts/by-riot-id/" + name + "/" + tagLine);
    }

    protected HttpClient.ResponseReceiver<?> getAccountByPuuidRaw(String token, String endpoint, String puuid) {
        return webClient
                .headers(head -> head.add("X-Riot-Token", token))
                .get()
                .uri("https://" + endpoint + ".api.riotgames.com/riot/account/v1/accounts/by-puuid/" + puuid);
    }

    protected HttpClient.ResponseReceiver<?> getActiveShardsByGameRaw(String token, String endpoint, String game, String puuid) {
        return webClient
                .headers(head -> head.add("X-Riot-Token", token))
                .get()
                .uri("https://" + endpoint + ".api.riotgames.com/riot/account/v1/active-shards/by-game/" + game +"/by-puuid/" + puuid);
    }
}
