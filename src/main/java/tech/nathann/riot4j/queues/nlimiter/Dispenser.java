package tech.nathann.riot4j.queues.nlimiter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import tech.nathann.riot4j.queues.RateLimits;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

public class Dispenser {
    private static final Logger log = LoggerFactory.getLogger(Dispenser.class);

    private final RateLimits limit;
    private final Duration reset;
    private final Mono<Instant>[] tickets;

    private final Sinks.Many<Wrap> queue = Sinks.many().unicast().onBackpressureBuffer();

    public Dispenser(RateLimits limit, Duration reset, int count) {
        this.limit = limit;
        this.reset = reset;

        tickets = new Mono[count];
        Arrays.fill(tickets, Mono.just(Instant.EPOCH));

        queue.asFlux()
                .concatMap(request ->
                        getTicket(request.request)
                                .doOnNext(fin -> request.response.emitValue(fin, Sinks.EmitFailureHandler.FAIL_FAST))
                ).subscribe();
    }

    private int position = 0;
    private Mono<TicketedRequest> getTicket(TicketedRequest request) {
        log.trace("Ticket requested from " + limit);

        int pos = position % tickets.length;
        Mono<Instant> lockMono = tickets[pos];

        return lockMono.flatMap(lock -> {
            log.trace("Lock aquired");

            Duration timePassed = Duration.between(lock, Instant.now());
            boolean isFree = reset.compareTo(timePassed) < 0;
            if(isFree) {
                log.trace("Ticket at position " + pos + " is free!");
                tickets[pos] = request.getLock();
                position++;
                return Mono.just(request);
            } else {
                Duration delay = reset.minus(timePassed);
                log.warn("Ticket at pos " + pos + " for bucket " + limit + " isn't free, delaying " + delay);
                return Mono.delay(delay)
                        .flatMap(fin -> getTicket(request));
            }
        });
    }

    public Mono<TicketedRequest> pushTicket(TicketedRequest request) {
        Wrap temp = new Wrap(request);
        queue.emitNext(temp, Sinks.EmitFailureHandler.FAIL_FAST);
        return temp.response.asMono();
    }

    private static class Wrap {
        private final Sinks.One<TicketedRequest> response = Sinks.one();
        private final TicketedRequest request;

        public Wrap(TicketedRequest request) {
            this.request = request;
        }
    }
}