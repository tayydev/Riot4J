package io.github.nathannorth.riot4j.api.account;

import io.github.nathannorth.riot4j.clients.RiotDevelopmentAPIClient;
import io.github.nathannorth.riot4j.enums.ValRegion;
import io.github.nathannorth.riot4j.json.riotAccount.ActiveShardData;
import io.github.nathannorth.riot4j.json.riotAccount.RiotAccountData;
import reactor.core.publisher.Mono;

public class RiotAccount { //todo this implements is really only a depreciation feature, should be removed
    private final RiotDevelopmentAPIClient parent;
    private final RiotAccountData data;
    private ActiveShardData shard = null;

    public RiotAccount(RiotDevelopmentAPIClient parent, RiotAccountData data) {
        this.parent = parent;
        this.data = data;
    }

    public RiotDevelopmentAPIClient getParent() {
        return parent; //todo should really be client not parent
    }

    public RiotAccountData getData() {
        return data;
    }

    public Mono<ValRegion> getRegion() {
        if(shard != null) return Mono.just(shard.activeShard());
        return fetchShard().flatMap(r -> getRegion());
    }

    private Mono<ActiveShardData> fetchShard() {
        return parent.getActiveShardsVal(puuid())
                .doOnNext(newShard -> shard = newShard);
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
