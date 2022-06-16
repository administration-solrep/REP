package fr.dila.reponses.api.enumeration;

import static java.util.stream.Collectors.toMap;

import fr.dila.reponses.api.constant.RequeteConstants;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Une énumération pour donner les différents mode d'indexation possible :
 * INDEX_ORIG : indexation d'origine des dossiers.
 * INDEX_COMPL : l'indexation utilisateur
 * TOUS : les 2 modes d'indexation
 */
public enum IndexModeEnum {
    /* Indexation Ministères */
    INDEX_COMPL("indexmode.compl", RequeteConstants.PART_REQUETE_INDEX_COMPL),
    /* Indexation Parlement */
    INDEX_ORIG("indexmode.orig", RequeteConstants.PART_REQUETE_INDEX_ORIGINE),
    TOUS("indexmode.tous", RequeteConstants.PART_REQUETE_INDEX_TOUS);

    private final String labelKey;
    private final String queryModelName;

    IndexModeEnum(String labelKey, String queryModelName) {
        this.labelKey = labelKey;
        this.queryModelName = queryModelName;
    }

    public String getLabelKey() {
        return labelKey;
    }

    public String getQueryModelName() {
        return queryModelName;
    }

    /**
     * Récupère une Map contenant toutes les valeurs de l'enum avec en clé le nom de l'enum et en valeur la clé du label.
     *
     * @return une Map contenant toutes les valeurs de l'enum avec en clé le nom de l'enum et en valeur la clé du label
     */
    public static Map<String, String> getLabelKeys() {
        return Stream
            .of(values())
            .collect(
                toMap(
                    IndexModeEnum::name,
                    IndexModeEnum::getLabelKey,
                    (existing, replacement) -> existing,
                    LinkedHashMap::new
                )
            );
    }

    public static IndexModeEnum fromValue(String value) {
        return Stream
            .of(values())
            .filter(indexMode -> Objects.equals(indexMode.name(), value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Valeur inattendue '" + value + "'"));
    }
}
