package fr.dila.reponses.api.enumeration;

import java.util.Objects;
import java.util.stream.Stream;

public enum IndexationTypeEnum {
    SE_THEME("SE_theme", "Th"),
    SE_RUBRIQUE("SE_rubrique", "Ru"),
    SE_RENVOI("SE_renvoi", "Re"),
    AN_RUBRIQUE("AN_rubrique", "Ru"),
    TA_RUBRIQUE("TA_rubrique", "TA"),
    AN_ANALYSE("AN_analyse", "Ti"),
    MOTSCLEF_MINISTERE("motclef_ministere");

    private final String value;
    private final String prefix;

    IndexationTypeEnum(String value) {
        this(value, null);
    }

    IndexationTypeEnum(String value, String prefix) {
        this.value = value;
        this.prefix = prefix;
    }

    public String getValue() {
        return value;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getLabelKey() {
        return "label.vocabulary." + value;
    }

    public static IndexationTypeEnum fromString(String value) {
        return Stream
            .of(values())
            .filter(indexationType -> Objects.equals(indexationType.getValue(), value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Valeur inattendue '" + value + "'"));
    }
}
