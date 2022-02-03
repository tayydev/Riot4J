package io.github.nathannorth.riot4j.clients;

import io.github.nathannorth.riot4j.enums.RiotRegion;
import io.github.nathannorth.riot4j.enums.ValRegion;
import io.github.nathannorth.riot4j.exceptions.InvalidTokenException;
import reactor.core.publisher.Mono;

public class RiotClientBuilder {
    public static RiotClientBuilder create() {
        return new RiotClientBuilder();
    }

    protected RiotClientBuilder() {
    }

    protected final ImmutableClientConfig.Builder builder = ImmutableClientConfig.builder();

    public RiotClientBuilder token(String token) {
        builder.token(token);
        return this;
    }

    public RiotClientBuilder riotRegion(RiotRegion region) {
        builder.riotRegion(region);
        return this;
    }

    public RiotClientBuilder valRegion(ValRegion region) {
        builder.valRegion(region);
        return this;
    }

    public Mono<RiotDevelopmentAPIClient> buildDevClient() {
        ClientConfig config = builder.build();

        return new RiotDevelopmentAPIClient(config).test()
                .onErrorResume(e -> Mono.error(new InvalidTokenException("The token specified is not valid")))
                .ofType(RiotDevelopmentAPIClient.class);
    }

    public Mono<RiotProductionAPIClient> buildProductionClient() {
        ClientConfig config = builder.build();

        return new RiotProductionAPIClient(config).test()
                .onErrorResume(e -> Mono.error(new InvalidTokenException("The token specified is not valid")))
                .ofType(RiotProductionAPIClient.class);
    }
}
