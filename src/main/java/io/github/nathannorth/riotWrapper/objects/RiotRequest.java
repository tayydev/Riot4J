package io.github.nathannorth.riotWrapper.objects;

import io.github.nathannorth.riotWrapper.util.ErrorMapping;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

public class RiotRequest {

    private HttpClient.ResponseReceiver<?> receiver;

    private RiotRequest(HttpClient.ResponseReceiver<?> receiver) {
        this.receiver = receiver;
    }
    public static RiotRequest create(HttpClient.ResponseReceiver<?> receiver) {
        return new RiotRequest(receiver);
    }
    public <T> Mono<T> get(Class<T> c) {
        return receiver
                .responseSingle(ErrorMapping.map(c))
                //.applyDelay()
                ;
    }
}
