package io.github.nathannorth.riot4j.api.account;

import io.github.nathannorth.riot4j.api.match.ValMatchlist;
import io.github.nathannorth.riot4j.clients.RiotDevelopmentAPIClient;
import io.github.nathannorth.riot4j.clients.RiotProductionAPIClient;
import io.github.nathannorth.riot4j.enums.ValRegion;
import io.github.nathannorth.riot4j.json.riotAccount.ActiveShardData;
import io.github.nathannorth.riot4j.json.riotAccount.RiotAccountData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class RiotAccount { //todo this implements is really only a depreciation feature, should be removed
    private static final Logger log = LoggerFactory.getLogger(RiotAccount.class);

    private final RiotDevelopmentAPIClient parent;
    private final RiotAccountData data;
    private final Mono<ActiveShardData> shard;

    public RiotAccount(RiotDevelopmentAPIClient parent, RiotAccountData data) {
        this.parent = parent;
        this.data = data;

        shard = parent.getActiveShardsVal(puuid())
                .doOnNext(e -> log.debug("Un-MEMORY-cached value for region cached"))
                .cache();
    }

    /**
     * @return the matchlist for a given player. Will return empty if the parent of this object does not inherit {@link RiotProductionAPIClient}
     */
    public Mono<ValMatchlist> getMatchList() {
        if(parent instanceof RiotProductionAPIClient) {
            return ((RiotProductionAPIClient) parent).getMatchList(this);
        }
        return Mono.empty();
    }

    public RiotDevelopmentAPIClient getParent() {
        return parent; //todo should really be client not parent
    }

    public RiotAccountData getData() {
        return data;
    }

    public Mono<ValRegion> getRegion() {
        return shard.map(it -> it.activeShard());
    }

    public String puuid() {
        return data.puuid();
    }

    public String gameName() {
        return data.gameName();
    }

    public String tagLine() {
        return data.tagLine();
    }

    public String getCombinedName() {
        return gameName() + "#" + tagLine();
    }

    @Override
    public String toString() {
        return "RiotAccount{" +
                "parent=" + parent +
                ", data=" + data +
                ", shard=" + shard +
                '}';
    }
}
