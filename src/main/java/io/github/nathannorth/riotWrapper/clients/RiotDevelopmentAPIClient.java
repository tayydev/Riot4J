package io.github.nathannorth.riotWrapper.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.nathannorth.riotWrapper.queues.CleanLimitedQueue;
import io.github.nathannorth.riotWrapper.json.platform.PlatformData;
import io.github.nathannorth.riotWrapper.objects.ValRegion;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.io.IOException;

public class RiotDevelopmentAPIClient extends RiotAPIClient {

    private final CleanLimitedQueue queue = new CleanLimitedQueue();

    protected RiotDevelopmentAPIClient(String token) {
        super(token);
    }

    public static RiotDevelopmentAPIClientBuilder builder() {
        return new RiotDevelopmentAPIClientBuilder();
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    public Mono<PlatformData> getValStatus(ValRegion region) {
        return queue.push(webClient
                .headers(head -> head.add("X-Riot-Token", token))
                .get()
                .uri("https://" + region.getValue() + ".api.riotgames.com/val/status/v1/platform-data")
        ).map(string -> {
            try {
                return mapper.readValue(string, PlatformData.class);
            } catch (IOException exception) {
                throw new RuntimeException("test");
            }
        });
    }

    //for debug
    public HttpClient.ResponseReceiver<?> getPartialRequest(ValRegion region) {
        return webClient
                .headers(head -> head.add("X-Riot-Token", token))
                .get()
                .uri("https://" + region.getValue() + ".api.riotgames.com/val/status/v1/platform-data");
    }
}
