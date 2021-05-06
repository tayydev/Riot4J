package io.github.nathannorth.riotWrapper.util;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class RateLimiter {
    public static void main(String[] args) {
        RateLimiter r = new RateLimiter(Duration.ofSeconds(10), 10);

        Flux.range(0, 100000)
                //todo the problem with this is that pretty much every item gets triggered by the same dispenser
                .delayUntil(e -> r.dispenser.next())
                .doOnNext(e -> System.out.println(e))
                .subscribe();
        //r.dispenser.doOnNext(e -> System.out.println(e)).subscribe();
        Mono.never().block();
    }

    private final Duration dur;
    private final int requests;
    private final Flux<Long> dispenser;

    public RateLimiter(Duration dur, int requests) {
        this.dur = dur;
        this.requests = requests;

        dispenser = Flux.interval(dur.dividedBy(requests)).cache(requests);
    }

    //todo figure out a proper abstraction
    public Mono<Integer> limit() {
        return Mono.just(0).delayUntil(e -> dispenser.next());
    }
}
