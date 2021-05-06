package io.github.nathannorth.riotWrapper.queues;

import reactor.core.publisher.Flux;

/**
 * Abstraction for a REST request queue.
 *
 * @param <T> the type of queue elements materializing the requests.
 */
public interface RequestQueue<T> {

    /**
     * Pushes a new request to the queue.
     *
     * @param request the request to push.
     * @return {@code true} if the request was submitted successfully, {@code false} otherwise
     */
    boolean push(T request);

    /**
     * Exposes a Flux that continuously emits requests available in queue.
     *
     * @return a Flux of requests.
     */
    Flux<T> requests();
}
