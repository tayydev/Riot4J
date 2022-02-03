package io.github.nathannorth.riot4j.api.match;

import io.github.nathannorth.riot4j.clients.RiotProductionAPIClient;
import io.github.nathannorth.riot4j.enums.ValQueueId;
import io.github.nathannorth.riot4j.enums.ValRegion;
import io.github.nathannorth.riot4j.enums.ValTeamId;
import io.github.nathannorth.riot4j.json.valMatch.MatchlistEntryData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Optional;

public class MatchlistEntry {
    private final Logger log = LoggerFactory.getLogger(MatchlistEntry.class);

    private final RiotProductionAPIClient parent;
    private final MatchlistEntryData data;
    private final ValRegion region;

    public MatchlistEntry(RiotProductionAPIClient parent, MatchlistEntryData data, ValRegion region) {
        this.parent = parent;
        this.data = data;
        this.region = region;
    }

    public Mono<ValMatch> getValMatch() {
        return parent.getMatch(this);
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
