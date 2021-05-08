package io.github.nathannorth.riot4j.clients;

import io.github.nathannorth.riot4j.objects.ValRegion;
import io.github.nathannorth.riot4j.util.Exceptions;
import reactor.core.publisher.Mono;

public class RiotDevelopmentAPIClientBuilder {
        private String key = null;

    /**
     * Gives a builder object an API key
     * @param key your API key
     * @return your builder with an updated API key
     */
    public RiotDevelopmentAPIClientBuilder addKey(String key) {
            this.key = key;
            return this;
        }

    /**
     * Returns a mono of your builder that when evaluated tests your api key and returns a completed RiotDevelopmentAPIClient
     * @return a RiotDevelopmentAPIClient
     */
    public Mono<RiotDevelopmentAPIClient> build() {
            if (key == null) return Mono.error(new Exceptions.IncompleteBuilderException("Did not specify token."));
            RiotDevelopmentAPIClient temp = new RiotDevelopmentAPIClient(key);
            return temp.getValStatus(ValRegion.NORTH_AMERICA) //todo find a better way of validating tokens
                    .onErrorResume(e -> {
                        if (e instanceof Exceptions.WebFailure) {
                            if(((Exceptions.WebFailure) e).getResponse().status().code() == 403)
                                return Mono.error(new Exceptions.InvalidTokenException("The token specified is not valid."));
                        }
                        return Mono.error(e);
                    })
                    .then(Mono.just(temp));
    }

    /**
     * if you like to live life on the edge (or want to save resources) this method is for you
     * @return a RiotDevelopmentAPIClient WITHOUT testing its API key.
     */
    public RiotDevelopmentAPIClient buildUnsafe() {
        return new RiotDevelopmentAPIClient(key);
    }
}
