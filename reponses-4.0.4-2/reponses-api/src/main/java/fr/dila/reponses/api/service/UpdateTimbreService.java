package fr.dila.reponses.api.service;

import fr.dila.reponses.api.logging.ReponsesLogging;
import fr.dila.reponses.api.logging.ReponsesLoggingLine;
import fr.dila.reponses.api.logging.ReponsesLoggingLineDetail;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Service de calcul pour les mise a jours des timbre.
 *
 * @author asatre
 */
public interface UpdateTimbreService {
    /**
     * Requête le nombre de questions closes par ministere
     *
     * @param session
     * @return
     *
     */
    Map<String, Long> getCloseCount(CoreSession session);

    /**
     * Requête le nombre de questions signées par ministere
     *
     * @param session
     * @return
     *
     */
    Map<String, Long> getSigneCount(CoreSession session);

    /**
     * Retourne la liste des dossierDocs signés pour l'id de ministère passé en paramètre.
     *
     * @param session
     *            Doit être une session unrestricted pour récupérer tous les dossiers (ce qui est le cas normalement
     *            dans la migration)
     * @param idMinistere
     * @return
     *
     */
    List<DocumentModel> getSignedDossiersForMinistere(CoreSession session, String idMinistere);

    /**
     * Requête le nombre de question à migrer par ministere
     *
     * @param session
     * @return
     *
     */
    Map<String, Long> getMigrableCount(CoreSession session);

    /**
     * Retourne la liste des dossierDocs à migrer pour l'id de ministère passé en paramètre.
     *
     * @param session
     *            Doit être une session unrestricted pour récupérer tous les dossiers (ce qui est le cas normalement
     *            dans la migration)
     * @param idMinistere
     * @return
     *
     */
    List<DocumentModel> getMigrableDossiersForMinistere(CoreSession session, String idMinistere);

    /**
     * Requête le nombre de modèle de feuille de route par ministere
     *
     * @param session
     * @return
     *
     */
    Map<String, Long> getModelFDRCount(CoreSession session);

    /**
     * Détermine si une migration est en cours
     *
     * @param session
     * @return
     *
     */
    Boolean isMigrationEnCours(CoreSession session);

    /**
     * Récupère la migration en cours
     *
     * @param session
     * @return
     *
     */
    ReponsesLogging getMigrationEnCours(CoreSession session);

    /**
     * Creation d'un document {@link ReponsesLogging} a la date du jour
     *
     * @param session
     * @param previsionnalCount
     * @return
     *
     */
    String createLogging(
        final CoreSession session,
        final Long previsionnalCount,
        final Long closePrevisionalCount,
        Map<String, String> timbre,
        String currentGouvernement,
        String nextGouvernement
    );

    List<ReponsesLogging> getAllReponsesLogging(CoreSession session);

    List<ReponsesLoggingLine> getAllReponsesLoggingLine(CoreSession session, String reponsesLoggingId);

    List<ReponsesLoggingLineDetail> getAllReponsesLoggingLineDetail(CoreSession session, String reponsesLoggingLineId);

    Map<String, String> getReponsesLoggingTimbre(final CoreSession session, final String reponsesLoggingId);

    /**
     * Calcul les questions closes pour un ministere donné
     *
     * @param coreSession
     * @param currentMin
     * @return
     *
     */
    Long getCloseCountForMinistere(CoreSession coreSession, String currentMin);

    /**
     * Génère la requête de récupération des ids des questions closes pour un ministère donné
     *
     * @param currentMin
     * @return
     */
    String getQueryClosedQuestionsForMinistere(String currentMin);
}
