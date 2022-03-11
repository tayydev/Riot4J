package tech.nathann.riot4j.clients;

import reactor.core.publisher.Mono;
import tech.nathann.riot4j.enums.regions.RiotRegion;
import tech.nathann.riot4j.enums.regions.ValRegion;
import tech.nathann.riot4j.exceptions.InvalidTokenException;
import tech.nathann.riot4j.queues.nlimiter.RatePresets;

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

        return new RiotDevelopmentAPIClient(config, RatePresets.DEV_CLIENT).test()
                .onErrorResume(e -> Mono.error(new InvalidTokenException("The token specified is not valid")))
                .ofType(RiotDevelopmentAPIClient.class);
    }

    public Mono<RiotProductionAPIClient> buildProductionClient() {
        ClientConfig config = builder.build();

        return new RiotProductionAPIClient(config, RatePresets.PROD_CLIENT).test()
                .onErrorResume(e -> Mono.error(new InvalidTokenException("The token specified is not valid")))
                .ofType(RiotProductionAPIClient.class);
    }
}
