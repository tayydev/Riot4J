package io.github.nathannorth.riot4j.queues;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

/**
 * a generic queue defines a sink, and a response system to return hotsources that will evaluate (from some input type
 * to a Mono of some output type) using the strategy defined in the evaluate method
 */
public abstract class GenericQueue<Input, Response> {
    private final Sinks.Many<Request> in = Sinks.many().multicast().onBackpressureBuffer(1024, false);

    public GenericQueue() {
        in.asFlux()
                .flatMap(request -> evaluate(request.input) //evaluate requests
                        .doOnNext(response -> //todo how scuffed is doOnNext
                                request.response.emitValue(response, Sinks.EmitFailureHandler.FAIL_FAST) //publish to individual request's hotsources
                        ), 1)
                .subscribe();
    }

    //push new item to the queue, return a reference to the newly generated Request object's hotsource
    public Mono<Response> push(Input input) {
        Request r = new Request(input);
        in.emitNext(r, Sinks.EmitFailureHandler.FAIL_FAST);
        return r.response.asMono();
    }

    //processes a request of our generic type. can pause the entire queue by waiting
     abstract Mono<Response> evaluate(Input input);

    private class Request {
        final Input input;
        final Sinks.One<Response> response = Sinks.one();
        public Request(Input input) {
            this.input = input;
        }
    }
}
