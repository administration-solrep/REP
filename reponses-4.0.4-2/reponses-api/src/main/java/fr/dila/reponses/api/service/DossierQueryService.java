package fr.dila.reponses.api.service;

import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Service pour récupérer des dossiers
 *
 * @author SCE
 *
 */
public interface DossierQueryService {
    /**
     * Retoune une map de numéro de dossier et l'origine de la question en
     * recherchant avec le numéro d'une question
     *
     * @param session
     *            CoreSession
     * @param numeroQuestion
     *            numéro de la question
     * @return map de numéro de dossier et origine de la question
     */
    Map<String, String> getMapDossierOrigineIdsFromNumero(CoreSession session, String numeroQuestion);

    /**
     * Retoune une liste de numéro de dossier associé à un numéro de question
     *
     * @param session
     * @param numeroQuestion
     * @param origineQuestion
     *            origine de la question (AN ou SENAT)
     * @return list d'ids de dossiers
     */
    List<String> getDossierIdsFromNumeroAndOrigine(CoreSession session, String numeroQuestion, String origineQuestion);
}
