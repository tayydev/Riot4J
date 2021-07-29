package io.github.nathannorth.riot4j.json.riotAccount;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableRiotAccountData.class)
@JsonDeserialize(as = ImmutableRiotAccountData.class)
public interface RiotAccountData {
    String puuid();
    String gameName();
    String tagLine();
}
