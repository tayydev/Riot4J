package tech.nathann.riot4j.json.riotAccount;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@JsonSerialize(as = ImmutableIncompleteRiotAccountData.class)
@JsonDeserialize(as = ImmutableIncompleteRiotAccountData.class)
public interface IncompleteRiotAccountData {
    Optional<String> puuid();
    Optional<String> gameName();
    Optional<String> tagLine();
}
