package fr.dila.reponses.ui.enums;

import fr.dila.ss.ui.enums.EtapeValidationStatus;
import java.util.Objects;
import java.util.stream.Stream;

public enum RepEtapeValidationStatusEnum implements EtapeValidationStatus {
    VALIDMANUEL(
        "1",
        "label.reponses.feuilleRoute.etape.valide.manuellement",
        "icon--check-circle base-paragraph--success"
    ),
    REFUS("2", "label.reponses.feuilleRoute.etape.valide.refusValidation", "icon--times-circle base-paragraph--danger"),
    VALIDAUTO(
        "3",
        "label.reponses.feuilleRoute.etape.valide.automatiquement",
        "icon--check-bubble-cog base-paragraph--success"
    ),
    NONCONCERNE(
        "4",
        "label.reponses.feuilleRoute.etape.nonConcerne",
        "icon--user-bubble-times base-paragraph--default"
    );

    private final String key;
    private final String typeEtape;
    private final String labelKey;
    private final String icon;

    RepEtapeValidationStatusEnum(String key, String labelKey, String icon) {
        this(key, null, labelKey, icon);
    }

    RepEtapeValidationStatusEnum(String key, String typeEtape, String labelKey, String icon) {
        this.key = key;
        this.typeEtape = typeEtape;
        this.labelKey = labelKey;
        this.icon = icon;
    }

    public static RepEtapeValidationStatusEnum getEnumFromKey(String key, String typeEtape) {
        return Stream
            .of(values())
            .filter(elem -> Objects.equals(elem.getKey(), key) && Objects.equals(elem.getTypeEtape(), typeEtape))
            .findFirst()
            .orElse(
                Stream
                    .of(values())
                    .filter(elem -> Objects.equals(elem.getKey(), key))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Valeur inattendue '" + key + "'"))
            );
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getTypeEtape() {
        return typeEtape;
    }

    @Override
    public String getLabelKey() {
        return labelKey;
    }

    @Override
    public String getIcon() {
        return icon;
    }
}
