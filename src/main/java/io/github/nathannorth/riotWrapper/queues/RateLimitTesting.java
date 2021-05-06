package io.github.nathannorth.riotWrapper.queues;

import io.github.nathannorth.riotWrapper.TesterClass;
import io.github.nathannorth.riotWrapper.clients.RiotAPIClient;
import io.github.nathannorth.riotWrapper.clients.RiotDevelopmentAPIClient;
import io.github.nathannorth.riotWrapper.clients.RiotDevelopmentAPIClientBuilder;
import io.github.nathannorth.riotWrapper.objects.ValRegion;
import io.github.nathannorth.riotWrapper.util.Exceptions;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

import java.beans.Expression;
import java.time.Duration;
import java.util.function.Function;

public class RateLimitTesting {

    public static void main(String[] args) {
        RateLimiter b = new RateLimiter();
        RequestQueue<HttpClient.ResponseReceiver<?>> httpQueue = b.create();

        RiotDevelopmentAPIClient client = RiotDevelopmentAPIClient.builder()
                .addToken(TesterClass.getKeys().get(0))
                .build()
                .block();
        for(int i = 0; i < 102; i++) {
            httpQueue.push(client.getPartialRequest(ValRegion.ASIA_PACIFIC));
        }

        httpQueue.requests() //flux of httpResponseReceiver
                .flatMap(responseReceiver ->
                        theThing.apply(responseReceiver).onErrorResume(error -> {
                            if(error instanceof Exceptions.RateLimitedException) {
                                System.out.println("Got delay, about to retry!");
                                return Mono.delay(Duration.ofSeconds(((Exceptions.RateLimitedException) error).getSecs()))
                                        .flatMap(useless -> theThing.apply(responseReceiver));
                            }
                            return Mono.error(error);
                        }), 1) //concurrency is one so that we execute in order
                .doOnNext(s -> System.out.println(s)) //print to console
                .subscribe();

        Mono.never().block();
    }

    private static final Function<HttpClient.ResponseReceiver<?>, Mono<String>> theThing =
            receiver -> receiver
                    .responseSingle(((response, byteBufMono) -> {
                        if (response.status().code() / 100 == 2) return byteBufMono.asString();
                        //rate limiting
                        if (response.status().code() == 429) {
                            int secs = Integer.parseInt(response.responseHeaders().get("Retry-After")); //get retry-after
                            System.out.println("Retrying after " + secs + " seconds.");
                            throw new Exceptions.RateLimitedException(secs); //throw associated exception
                        }
                        throw new RuntimeException("Bad error type"); //temporarily scuffed
                    }));



//            .onErrorResume(e -> {
//                if(e instanceof Exceptions.RateLimitedException) {
//                    return receiver.responseSingle(((response, byteBufMono) -> byteBufMono.asString()))
//                            .delayElement(Duration.ofSeconds(((Exceptions.RateLimitedException) e).getSecs()));
//                }
//                else return Mono.error(e);
//            });
}