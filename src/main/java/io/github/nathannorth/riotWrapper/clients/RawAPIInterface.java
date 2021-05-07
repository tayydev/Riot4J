package io.github.nathannorth.riotWrapper.clients;

import reactor.netty.http.client.HttpClient;

//todo make sure package private does what i think it does
public class RawAPIInterface {
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
}
