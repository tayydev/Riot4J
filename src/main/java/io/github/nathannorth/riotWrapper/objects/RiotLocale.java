package io.github.nathannorth.riotWrapper.objects;

public class RiotLocale {
    private final String locale;

    private RiotLocale(String locale) {
        this.locale = locale;
    }

    public String getContent() {
        return locale;
    }

    public static final RiotLocale US_ENGLISH = new RiotLocale("en-US");
}
