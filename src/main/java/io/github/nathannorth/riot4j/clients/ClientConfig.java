package io.github.nathannorth.riot4j.clients;

import io.github.nathannorth.riot4j.enums.RiotRegion;
import io.github.nathannorth.riot4j.enums.ValRegion;
import org.immutables.value.Value;

@Value.Immutable
public abstract class ClientConfig {
    public abstract String token();

    @Value.Default
    public RiotRegion riotRegion() {
        return RiotRegion.AMERICAS;
    }

    @Value.Default
    public ValRegion valRegion() {
        return ValRegion.NORTH_AMERICA;
    }
}
