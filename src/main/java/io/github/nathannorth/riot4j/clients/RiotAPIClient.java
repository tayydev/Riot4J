package io.github.nathannorth.riot4j.clients;

/**
 * A RiotAPIClient is a generic class that holds a token
 */
public abstract class RiotAPIClient extends RawAPIInterface {
    final String token;

    RiotAPIClient(String token) {
        this.token = token;
    }
}
