package io.github.nathannorth.riotWrapper.objects;

//todo is this riot locale or is this val locale?
public class ValLocale {
    private final String value;

    private ValLocale(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static final ValLocale US_ENGLISH = new ValLocale("en-US");
    public static final ValLocale FR_FRENCH = new ValLocale("fr-FR");
}
