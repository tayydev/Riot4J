package io.github.nathannorth.riot4j.objects;

public enum ValLocale {
    AE_ARABIC("ae-AE"),
    DE_GERMAN("de-DE"),
    US_ENGLISH("en-US"),
    ES_SPANISH("es-ES"),
    MX_SPANISH("es-MX"),
    FR_FRENCH("fr-FR"),
    ID_INDONESIAN("id-ID"),
    IT_ITALIAN("it-IT"),
    JP_JAPANESE("ja-JP"),
    KO_KOREAN("ko-KR"),
    PL_POLISH("pl-PL"),
    BR_PORTUGUESE("pt-BR"),
    RU_RUSSIAN("ru-RU"),
    TR_TURKISH("tr-TR"),
    TH_THAI("th-TH"),
    VN_VIETNAMESE("vi-VN"),
    TW_CHINESE("zh-TW");

    String value;
    ValLocale(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}