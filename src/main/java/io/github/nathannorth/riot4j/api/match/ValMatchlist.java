package io.github.nathannorth.riot4j.api.match;

import io.github.nathannorth.riot4j.clients.RiotProductionAPIClient;
import io.github.nathannorth.riot4j.enums.ValRegion;
import io.github.nathannorth.riot4j.json.valMatch.MatchlistData;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

public class ValMatchlist {
    private final RiotProductionAPIClient parent;
    private final MatchlistData data;
    private final ValRegion region;

    private final List<MatchlistEntry> history;

    public ValMatchlist(RiotProductionAPIClient parent, MatchlistData data, ValRegion region) {
        this.parent = parent;
        this.data = data;
        this.region = region;

        history = data.history()
                .stream()
                .map(entry -> new MatchlistEntry(parent, entry, region))
                .collect(Collectors.toList());
    }

    public String puuid() {
        return data.puuid();
    }

    public List<MatchlistEntry> history() {
        return history;
    }

    public Flux<MatchlistEntry> matches() {
        return Flux.fromIterable(history);
    }

    public MatchlistData getData() {
        return data;
    }

    public ValRegion getRegion() {
        return region;
    }
}
