package io.github.nathannorth.riot4j.queues;

import io.github.nathannorth.riot4j.util.Exceptions;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class QueueOfQueues extends GenericQueue<Mono<String>, String> {

    @Override
    Mono<String> evaluate(Mono<String> input) {
        return input
                .onErrorResume(error -> {
                    if(error instanceof Exceptions.RateLimitedException) {
                        System.out.println("Hit rate limit... delaying: " + ((Exceptions.RateLimitedException) error).getSecs() + " seconds");
                        return Mono.delay(Duration.ofSeconds(((Exceptions.RateLimitedException) error).getSecs()))
                                .flatMap(finished -> evaluate(input)); //try again todo this isnt recursive
                    }
                    else return Mono.error(error);
                });
    }
}
