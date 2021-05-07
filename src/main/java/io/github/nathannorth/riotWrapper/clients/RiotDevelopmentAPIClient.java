package io.github.nathannorth.riotWrapper.clients;

import io.github.nathannorth.riotWrapper.json.Mapping;
import io.github.nathannorth.riotWrapper.json.valContent.ContentData;
import io.github.nathannorth.riotWrapper.json.valPlatform.PlatformStatusData;
import io.github.nathannorth.riotWrapper.objects.ValLocale;
import io.github.nathannorth.riotWrapper.queues.CleanLimitedQueue;
import io.github.nathannorth.riotWrapper.objects.ValRegion;
import reactor.core.publisher.Mono;

public class RiotDevelopmentAPIClient extends RiotAPIClient {

    private final CleanLimitedQueue rateLimiter = new CleanLimitedQueue();

    public RiotDevelopmentAPIClient(String token) {
        super(token);
    }

    //todo this should have a default locale and region with overrides(?)


    //todo better builder structure
    public static RiotDevelopmentAPIClientBuilder builder() {
        return new RiotDevelopmentAPIClientBuilder();
    }

    public Mono<PlatformStatusData> getValStatus(ValRegion region) {
        return rateLimiter.push(getValStatusRaw(token, region.getValue()))
                .map(Mapping.map(PlatformStatusData.class));
    }

    //we only support getting by local because anything else is stupid
    public Mono<ContentData> getValContent(ValRegion region, ValLocale locale) {
        return rateLimiter.push(getValContentRaw(token, region.getValue(), locale.getValue()))
                .map(Mapping.map(ContentData.class));
    }
}
