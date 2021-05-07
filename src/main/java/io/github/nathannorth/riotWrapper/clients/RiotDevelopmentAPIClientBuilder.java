package io.github.nathannorth.riotWrapper.clients;

import io.github.nathannorth.riotWrapper.objects.ValRegion;
import io.github.nathannorth.riotWrapper.util.Exceptions;
import reactor.core.publisher.Mono;

public class RiotDevelopmentAPIClientBuilder {
        private String token = null;

        public RiotDevelopmentAPIClientBuilder addToken(String token) {
            this.token = token;
            return this;
        }

        public Mono<RiotDevelopmentAPIClient> build() {
            if (token == null) return Mono.error(new Exceptions.IncompleteBuilderException("Did not specify token."));
            RiotDevelopmentAPIClient temp = new RiotDevelopmentAPIClient(token);
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
