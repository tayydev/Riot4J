package io.github.nathannorth.riotWrapper.objects;

public class ValRegion {
    private final String value;

    private ValRegion(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static final ValRegion NORTH_AMERICA = new ValRegion("na");
    public static final ValRegion BRAZIL = new ValRegion("br");
    public static final ValRegion EUROPE = new ValRegion("eu");
    public static final ValRegion LATIN_AMERICA = new ValRegion("latam");
    public static final ValRegion ASIA_PACIFIC = new ValRegion("ap");
    public static final ValRegion KOREA = new ValRegion("kr");
}
