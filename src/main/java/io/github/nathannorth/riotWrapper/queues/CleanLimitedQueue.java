package io.github.nathannorth.riotWrapper.queues;

import io.github.nathannorth.riotWrapper.util.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.netty.http.client.HttpClient;
import reactor.util.concurrent.Queues;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class CleanLimitedQueue {
    private static final AtomicInteger count = new AtomicInteger(0);
    private final Sinks.Many<Request> in = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);

    public Mono<String> push(HttpClient.ResponseReceiver<?> r) {
        Request request = new Request(r);
        in.tryEmitNext(request);
        return out()
                .filter(completed -> completed.id == request.id)
                .next()
                .map(completed -> completed.result);
    }

    private Flux<Completed> out() {
        return in.asFlux().flatMap(request -> conversion.apply(request)
                        .onErrorResume(error -> {
                            if(error instanceof Exceptions.RateLimitedException) {
                                System.out.println("Hit rate limit... delaying: " + ((Exceptions.RateLimitedException) error).getSecs() + " seconds.");
                                return Mono.delay(Duration.ofSeconds(((Exceptions.RateLimitedException) error).getSecs()))
                                        .flatMap(finished -> conversion.apply(request));
                            }
                            else return Mono.error(error);
                        }), 1);
    }

    private final Function<Request, Mono<Completed>> conversion = request ->
        request.response.responseSingle(((response, byteBufMono) -> {
            if(response.status().code() / 100 == 2)
                return byteBufMono.asString();
            if(response.status().code() == 429)
                throw new Exceptions.RateLimitedException(
                        Integer.parseInt(response.responseHeaders().get("Retry-After"))
                );
            else throw new RuntimeException("tbd");
        })).map(str -> new Completed(request.id, str));

    private static class Request {
        private final HttpClient.ResponseReceiver<?> response;
        private final int id = count.getAndIncrement();
        public Request(HttpClient.ResponseReceiver<?> response) {
            this.response = response;
        }
    }
    private static class Completed {
        private final int id;
        private final String result;

        public Completed(int id, String result) {
            this.id = id;
            this.result = result;
        }

        public String getResult() {
            return result;
        }
    }
}
