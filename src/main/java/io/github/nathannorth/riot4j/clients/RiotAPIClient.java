package io.github.nathannorth.riot4j.clients;

public class RiotAPIClient extends RawAPIInterface {
    final String token;

    RiotAPIClient(String token) {
        this.token = token;
    }
}
