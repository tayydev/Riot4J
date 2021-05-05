package io.github.nathannorth.riotWrapper.clients;

import io.github.nathannorth.riotWrapper.util.SafeMap;
import io.github.nathannorth.riotWrapper.json.platform.PlatformData;
import io.github.nathannorth.riotWrapper.objects.ValRegion;
import reactor.core.publisher.Mono;

public class RiotDevelopmentAPIClient extends RiotAPIClient {

    public RiotDevelopmentAPIClient(String token) {
        super(token);
    }

    public Mono<PlatformData> getValStatus(ValRegion region) {
        return webClient
                .headers(head -> head.add("X-Riot-Token", token))
                .get()
                .uri("https://" + region.getValue() + ".api.riotgames.com/val/status/v1/platform-data")
                .responseSingle((res, contentMono) -> contentMono.asString())
                .flatMap(SafeMap.map(PlatformData.class));
    }
}
