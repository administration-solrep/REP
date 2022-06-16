package fr.dila.reponses.api.flux;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Une interface pour gérer les informations de flux de type signalement, renouvellement, priorité.
 *
 * @author jgomez
 */
public interface HasInfoFlux {
    void setDocument(DocumentModel doc);

    /**
     * Retourne vrai si la priorité est urgente.
     *
     * @return Vrai si la priorité est urgente
     */
    Boolean isUrgent();

    /**
     * Retourne vraie si la question est signalée.
     *
     * @return Vraie si signalée
     */
    Boolean isSignale();

    /**
     *
     * Retourne vrai si la question est renouvellée.
     *
     * @return Vrai si renouvellée
     */
    Boolean isRenouvelle();

    /**
     * Retourne le delai d'expiration d'une feuille de route à partir d'une référence de question.
     *
     * @return Une string représentant le délai
     */
    String getDelaiExpirationFdr(CoreSession coreSession);

    /**
     * calcul le champ EtatsQuestion de denormalisation de la question
     * @return
     */
    String computeEtatsQuestion();
}
