package io.github.nathannorth.riotWrapper.clients;

import io.github.nathannorth.riotWrapper.json.Mapping;
import io.github.nathannorth.riotWrapper.json.valContent.ContentData;
import io.github.nathannorth.riotWrapper.json.valPlatform.PlatformStatusData;
import io.github.nathannorth.riotWrapper.objects.RiotLocale;
import io.github.nathannorth.riotWrapper.queues.CleanLimitedQueue;
import io.github.nathannorth.riotWrapper.objects.ValRegion;
import reactor.core.publisher.Mono;

public class RiotDevelopmentAPIClient extends RiotAPIClient {

    private final CleanLimitedQueue queue = new CleanLimitedQueue();

    protected RiotDevelopmentAPIClient(String token) {
        super(token);
    }

    public static RiotDevelopmentAPIClientBuilder builder() {
        return new RiotDevelopmentAPIClientBuilder();
    }

    public Mono<PlatformStatusData> getValStatus(ValRegion region) {
        return queue.push(webClient
                .headers(head -> head.add("X-Riot-Token", token))
                .get()
                .uri("https://" + region.getValue() + ".api.riotgames.com/val/status/v1/platform-data")
        ).map(Mapping.map(PlatformStatusData.class));
    }

    public Mono<ContentData> getValContent(ValRegion region, RiotLocale locale) {
        return queue.push(webClient
                .headers(head -> head.add("X-Riot-Token", token))
                .get()
                .uri("https://" + region.getValue() + ".api.riotgames.com/val/content/v1/contents?locale=" + locale.getContent())
        ).map(Mapping.map(ContentData.class));
    }
}
