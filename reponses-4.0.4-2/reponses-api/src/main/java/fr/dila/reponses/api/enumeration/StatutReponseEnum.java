package fr.dila.reponses.api.enumeration;

import static java.util.stream.Collectors.toMap;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

public enum StatutReponseEnum {
    EN_ATTENTE("statutreponse.enattente", "q.qu:etatQuestion = 'en cours'"),
    REPONDU("statutreponse.repondu", "q.qu:etatQuestion = 'repondu'"),
    PUBLIE("statutreponse.publie", "(q.qu:etatQuestion = 'repondu' AND r.rep:datePublicationJOReponse IS NOT NULL)");

    private final String labelKey;
    private final String whereClause;

    StatutReponseEnum(String labelKey, String whereClause) {
        this.labelKey = labelKey;
        this.whereClause = whereClause;
    }

    public String getLabelKey() {
        return labelKey;
    }

    public String getWhereClause() {
        return whereClause;
    }

    /**
     * Récupère une Map contenant toutes les valeurs de l'enum avec en clé le nom de l'enum et en valeur la clé du label.
     * @return une Map contenant toutes les valeurs de l'enum avec en clé le nom de l'enum et en valeur la clé du label
     */
    public static Map<String, String> getLabelKeys() {
        return Stream
            .of(values())
            .collect(
                toMap(
                    StatutReponseEnum::name,
                    StatutReponseEnum::getLabelKey,
                    (existing, replacement) -> existing,
                    LinkedHashMap::new
                )
            );
    }
}
