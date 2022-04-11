package tech.nathann.riot4j.api.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import tech.nathann.riot4j.api.match.ValMatchlist;
import tech.nathann.riot4j.clients.RiotDevelopmentAPIClient;
import tech.nathann.riot4j.clients.RiotProductionAPIClient;
import tech.nathann.riot4j.enums.regions.ValRegion;
import tech.nathann.riot4j.json.riotAccount.ActiveShardData;
import tech.nathann.riot4j.json.riotAccount.RiotAccountData;

/**
 * Holds data about a Riot Account and provides access to {@link ValRegion} and {@link ValMatchlist}
 */
public class RiotAccount implements Puuid {
    private static final Logger log = LoggerFactory.getLogger(RiotAccount.class);

    private final RiotDevelopmentAPIClient client;
    private final RiotAccountData data;
    private final Mono<ActiveShardData> shard;

    public RiotAccount(RiotDevelopmentAPIClient client, RiotAccountData data) {
        this.client = client;
        this.data = data;

        shard = client.getActiveShardsVal(puuid())
                .doOnNext(e -> log.debug("Un-MEMORY-cached value for region cached"))
                .cache();
    }

    /**
     * @return the matchlist for a given player. Will return empty if the parent of this object does not inherit {@link RiotProductionAPIClient}
     */
    public Mono<ValMatchlist> getMatchList() {
        if(client instanceof RiotProductionAPIClient) {
            RiotProductionAPIClient prodParent = (RiotProductionAPIClient) client;
            return getRegion().flatMap(region -> prodParent.getMatchList(region, puuid()));
        }
        return Mono.empty();
    }

    public RiotDevelopmentAPIClient getClient() {
        return client;
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
                "parent=" + client +
                ", data=" + data +
                ", shard=" + shard +
                '}';
    }
}
