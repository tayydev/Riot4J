package tech.nathann.riot4j.api.match;

import java.util.Locale;

/**
 * Represents the win result of a VALORANT match
 */
public enum ValMatchResult {
    WON,
    LOST,
    TIED;

    @Override
    public String toString() {
        return name().toLowerCase(Locale.ROOT);
    }
}
