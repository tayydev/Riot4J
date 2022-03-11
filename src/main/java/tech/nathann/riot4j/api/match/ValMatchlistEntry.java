package tech.nathann.riot4j.api.match;

import reactor.core.publisher.Mono;
import tech.nathann.riot4j.clients.RiotProductionAPIClient;
import tech.nathann.riot4j.enums.ValQueueId;
import tech.nathann.riot4j.enums.regions.ValRegion;
import tech.nathann.riot4j.enums.ValTeamId;
import tech.nathann.riot4j.json.valMatch.MatchlistEntryData;

import java.util.Optional;

public class ValMatchlistEntry {
    private final RiotProductionAPIClient parent;
    private final MatchlistEntryData data;
    private final ValRegion region;

    public ValMatchlistEntry(RiotProductionAPIClient parent, MatchlistEntryData data, ValRegion region) {
        this.parent = parent;
        this.data = data;
        this.region = region;
    }

    public Mono<ValMatch> getValMatch() {
        return parent.getMatch(region, data.matchId());
    }

    public String matchId() {
        return data.matchId();
    }

    public ValQueueId queueId() {
        return data.queueId();
    }

    public long gameStartTimeMillis() {
        return data.gameStartTimeMillis();
    }

    public Optional<ValTeamId> teamId() {
        return data.teamId();
    }

    public ValRegion region() {
        return region;
    }
}
