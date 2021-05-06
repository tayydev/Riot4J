package io.github.nathannorth.riotWrapper.clients;

import io.github.nathannorth.riotWrapper.objects.ValRegion;
import io.github.nathannorth.riotWrapper.util.Exceptions;
import reactor.core.publisher.Mono;

public class RiotDevelopmentAPIBuilder {
        private String token = null;
        public static RiotDevelopmentAPIBuilder builder() {
            return new RiotDevelopmentAPIBuilder();
        }
        public RiotDevelopmentAPIBuilder addToken(String token) {
            this.token = token;
            return this;
        }
        public Mono<RiotDevelopmentAPIClient> build() {
            if (token == null) throw new Exceptions.IncompleteClientException("Did not specify token.");
            RiotDevelopmentAPIClient temp = new RiotDevelopmentAPIClient(token);
            return temp.getValStatus(ValRegion.NORTH_AMERICA) //todo find a better way of validating keys
                    .onErrorResume(e -> {
                        if (e instanceof Exceptions.WebFailure) {
                            //handle different types of errors
                            int code = ((Exceptions.WebFailure) e).getData().status().status_code();
                            if (code == 403)
                                return Mono.error(new Exceptions.InvalidTokenException("The token specified is not valid."));
                        }
                        return Mono.error(e);
                    })
                    .then(Mono.just(temp));
        }
}
