package tech.nathann.riot4j.queues.nlimiter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import tech.nathann.riot4j.enums.regions.Region;
import tech.nathann.riot4j.queues.FailureStrategies;
import tech.nathann.riot4j.queues.RateLimits;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

public class Dispenser {
    private static final Logger log = LoggerFactory.getLogger(Dispenser.class);

    private final RateLimits limit;
    private final Region region;

    private final Duration reset;
    private final Mono<Instant>[] tickets;

    private final Sinks.Many<Wrap> queue = Sinks.many().unicast().onBackpressureBuffer();

    public Dispenser(RateLimits limit, Region region) {
        this.limit = limit;
        this.region = region;
        this.reset = limit.getLength();

        tickets = new Mono[limit.getCount()];
        Arrays.fill(tickets, Mono.just(Instant.EPOCH));

        queue.asFlux()
                .concatMap(wrap ->
                        getTicket(wrap)
                                .doOnNext(fin -> wrap.response.emitValue(fin, FailureStrategies.RETRY_ON_SERIALIZED))
                ).subscribe();

        log.info("Created bucket: " + this);
    }

    private int position = 0;
    private Mono<TicketedRequest> getTicket(Wrap wrap) {
        log.debug("Ticket requested from " + limit);

        int pos = position % tickets.length;
        Mono<Instant> lockMono = tickets[pos];
        TicketedRequest request = wrap.request;

        return lockMono
                .flatMap(lock -> {
                    log.trace("Lock acquired");

                    Duration timePassed = Duration.between(lock, Instant.now());
                    boolean isFree = reset.compareTo(timePassed) < 0;
                    if(isFree) {
                        log.trace("Ticket at position " + pos + " is free!");
                        tickets[pos] = request.getLock();
                        position++;
                        return Mono.just(request);
                    } else {
                        Duration delay = reset.minus(timePassed);
                        if(delay.compareTo(Duration.ofSeconds(1)) > 0) //we only care if delay is over 1 second
                            log.warn("Ticket at pos " + pos + " for  " + this + " isn't free, delaying " + delay);
                        return Mono.delay(delay)
                                .flatMap(fin -> getTicket(wrap));
                    }
                });
    }

    public Mono<TicketedRequest> pushTicket(TicketedRequest request) {
        Wrap temp = new Wrap(request);
        queue.emitNext(temp, FailureStrategies.RETRY_ON_SERIALIZED);
        return temp.response.asMono();
    }

    public RateLimits getLimit() {
        return limit;
    }

    private static class Wrap {
        private final Sinks.One<TicketedRequest> response = Sinks.one();
        private final TicketedRequest request;

        public Wrap(TicketedRequest request) {
            this.request = request;
        }
    }

    @Override
    public String toString() {
        return "Dispenser{" +
                "limit=" + limit +
                ", region=" + region +
                '}';
    }
}