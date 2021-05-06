package io.github.nathannorth.riotWrapper.clients;

import reactor.netty.http.client.HttpClient;

public class RiotAPIClient {
    final String token;
    final HttpClient webClient = HttpClient.create();;

    //todo make sure this is private
    protected RiotAPIClient(String token) {
        this.token = token;
    }
}
