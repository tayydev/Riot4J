package io.github.nathannorth.riotWrapper.clients;

import io.github.nathannorth.riotWrapper.util.ErrorMapping;
import io.github.nathannorth.riotWrapper.json.platform.PlatformData;
import io.github.nathannorth.riotWrapper.objects.ValRegion;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

public class RiotDevelopmentAPIClient extends RiotAPIClient {

    protected RiotDevelopmentAPIClient(String token) {
        super(token);
    }

    public static RiotDevelopmentAPIClientBuilder builder() {
        return new RiotDevelopmentAPIClientBuilder();
    }

    public Mono<PlatformData> getValStatus(ValRegion region) {
        return webClient
                .headers(head -> head.add("X-Riot-Token", token))
                .get()
                .uri("https://" + region.getValue() + ".api.riotgames.com/val/status/v1/platform-data")
                .responseSingle(ErrorMapping.map(PlatformData.class));
    }

    //for debug
    public HttpClient.ResponseReceiver<?> getPartialRequest(ValRegion region) {
        return webClient
                .headers(head -> head.add("X-Riot-Token", token))
                .get()
                .uri("https://" + region.getValue() + ".api.riotgames.com/val/status/v1/platform-data");
    }
}
