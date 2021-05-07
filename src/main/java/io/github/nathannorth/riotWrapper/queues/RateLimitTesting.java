package io.github.nathannorth.riotWrapper.queues;

import io.github.nathannorth.riotWrapper.TesterClass;
import io.github.nathannorth.riotWrapper.clients.RiotDevelopmentAPIClient;
import io.github.nathannorth.riotWrapper.objects.ValRegion;
import io.github.nathannorth.riotWrapper.util.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.function.Function;

public class RateLimitTesting {

    public static void main(String[] args) {
        RiotDevelopmentAPIClient client = RiotDevelopmentAPIClient.builder()
                .addToken(TesterClass.getKeys().get(0))
                .build()
                .block();

//        System.out.println(Integer.parseInt(System.getProperty("reactor.bufferSize.small", "256")));
//
//        Mono.never().block();

        //todo this errors
        Flux<Object> stupid = Flux.interval(Duration.ofMillis(100))
                .flatMap(thing -> client.getValStatus(ValRegion.BRAZIL).doOnNext(status -> System.out.println("A - STATUS #" + thing + " " + status)));

//        Flux<Object> alsoStupid = Flux.interval(Duration.ofSeconds(2))
//                .flatMap(thing -> client.getValStatus(ValRegion.BRAZIL).doOnNext(status -> System.out.println("B - STATUS #" + thing + " " + status)));

        Flux.merge(stupid).subscribe();

        Mono.never().block();
//        for(int i = 0; i < 102; i++) {
//            httpQueue.push(client.getPartialRequest(ValRegion.ASIA_PACIFIC));
//        }
//
//        httpQueue.requests() //flux of httpResponseReceiver
//                .flatMap(responseReceiver ->
//                        theThing.apply(responseReceiver).onErrorResume(error -> {
//                            if(error instanceof Exceptions.RateLimitedException) {
//                                System.out.println("Got delay, about to retry!");
//                                return Mono.delay(Duration.ofSeconds(((Exceptions.RateLimitedException) error).getSecs()))
//                                        .flatMap(useless -> theThing.apply(responseReceiver));
//                            }
//                            return Mono.error(error);
//                        }), 1) //concurrency is one so that we execute in order
//                .doOnNext(s -> System.out.println(s)) //print to console
//                .subscribe();

        Mono.never().block();
    }
}
