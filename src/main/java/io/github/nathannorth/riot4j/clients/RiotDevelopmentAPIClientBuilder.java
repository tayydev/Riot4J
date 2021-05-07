package io.github.nathannorth.riot4j.clients;

import io.github.nathannorth.riot4j.objects.ValRegion;
import io.github.nathannorth.riot4j.util.Exceptions;
import reactor.core.publisher.Mono;

public class RiotDevelopmentAPIClientBuilder {
        private String key = null;

        public RiotDevelopmentAPIClientBuilder addKey(String key) {
            this.key = key;
            return this;
        }

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
}
