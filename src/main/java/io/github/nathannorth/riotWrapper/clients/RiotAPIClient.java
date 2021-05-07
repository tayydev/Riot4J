package io.github.nathannorth.riotWrapper.clients;

public class RiotAPIClient extends RawAPIInterface {
    final String token;

    RiotAPIClient(String token) {
        this.token = token;
    }
}
