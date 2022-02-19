package tech.nathann.riot4j.api.match;

import reactor.core.publisher.Flux;
import tech.nathann.riot4j.clients.RiotProductionAPIClient;
import tech.nathann.riot4j.enums.ValRegion;
import tech.nathann.riot4j.json.valMatch.MatchlistData;

import java.util.List;
import java.util.stream.Collectors;

public class ValMatchlist {
    private final RiotProductionAPIClient parent;
    private final MatchlistData data;
    private final ValRegion region;

    private final List<ValMatchlistEntry> history;

    public ValMatchlist(RiotProductionAPIClient parent, MatchlistData data, ValRegion region) {
        this.parent = parent;
        this.data = data;
        this.region = region;

        history = data.history()
                .stream()
                .map(entry -> new ValMatchlistEntry(parent, entry, region))
                .collect(Collectors.toList());
    }

    public String puuid() {
        return data.puuid();
    }

    public List<ValMatchlistEntry> history() {
        return history;
    }

    public Flux<ValMatchlistEntry> matches() {
        return Flux.fromIterable(history);
    }

    public MatchlistData getData() {
        return data;
    }

    public ValRegion getRegion() {
        return region;
    }
}
