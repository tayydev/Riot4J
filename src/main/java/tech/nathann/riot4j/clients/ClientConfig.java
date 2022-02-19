package tech.nathann.riot4j.clients;

import org.immutables.value.Value;
import tech.nathann.riot4j.enums.RiotRegion;
import tech.nathann.riot4j.enums.ValRegion;

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

    //todo should have a default locale
}
