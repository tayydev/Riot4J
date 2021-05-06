package io.github.nathannorth.riotWrapper.queues;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

import java.util.function.Function;

public class RateLimiter {

    private final Function<Sinks.ManySpec, Sinks.Many<Object>> requestSinkFactory =
            spec -> spec.multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);

    public <T> RequestQueue<T> create() {
        return new RequestQueue<T>() {

            private final Sinks.Many<Object> sink = requestSinkFactory.apply(Sinks.many());

            @Override
            public boolean push(T request) {
                Sinks.EmitResult res = sink.tryEmitNext(request);

                if(res.isSuccess()) return true;
                else throw new RuntimeException("Something broke!"); //todo clean this up
            }

            @SuppressWarnings("unchecked")
            @Override
            public Flux<T> requests() {
                return (Flux<T>) sink.asFlux();
            }
        };
    }
}
