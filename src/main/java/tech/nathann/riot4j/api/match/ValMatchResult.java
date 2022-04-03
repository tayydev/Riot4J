package tech.nathann.riot4j.api.match;

import java.util.Locale;

public enum ValMatchResult {
    WON,
    LOST,
    TIED;

    @Override
    public String toString() {
        return name().toLowerCase(Locale.ROOT);
    }
}
