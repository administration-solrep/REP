package fr.dila.reponses.api.service;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.logging.ReponsesLoggingLine;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Service permettant de gérer un catalogue de modèles de feuille de route de l'application Réponses.
 *
 * @author jtremeaux
 */
public interface FeuilleRouteModelService extends fr.dila.ss.api.service.FeuilleRouteModelService {
    /**
     * Recherche et retourne la route par défaut pour les questions.
     * Utiliser uniquement si le dossier ne contient pas le ministère interpelé
     * @param session Session
     * @return Route par défaut pour les questions
     *
     */
    SSFeuilleRoute getDefaultRouteQuestion(CoreSession session);

    /**
     * Selectionne la feuille de route
     * @param session Session
     * @param dossier
     * @return feuille de route
     *
     */
    SSFeuilleRoute selectRouteForDossier(CoreSession session, Dossier dossier);

    /**
     * Recherche et retourne la route par défaut pour les errata.
     *
     * @param session Session
     * @return Route par défaut pour les errata
     *
     */
    SSFeuilleRoute getDefaultRouteErrata(CoreSession session);

    /**
     * Migre les modèles de feuille de route d'un ministère à un autre.
     *
     * @param session
     * @param oldMinistereId
     * @param newMinistereId
     * @param reponsesLoggingLine
     * @return
     *
     */
    void migrateMinistereFeuilleRouteModel(
        CoreSession session,
        String oldMinistereId,
        String newMinistereId,
        String oldMailboxId,
        String newMailboxId,
        ReponsesLoggingLine reponsesLoggingLine
    );

    /**
     * Retourne tous les steps d'une feuille de route, y compris ceux dans les conteneurs, sans ordre
     *
     * @param feuilleRouteId id de la feuille de route
     * @param session session
     * @return liste de step
     *
     */
    List<DocumentModel> getAllRouteElement(String feuilleRouteId, CoreSession session);

    /**
     * Retourne tous les steps d'une feuille de route non 'done', y compris ceux dans les conteneurs, sans ordre,
     * @param feuilleRouteId
     * @param session
     * @return
     *
     */
    List<DocumentModel> getAllReadyOrRunningRouteElement(String feuilleRouteId, CoreSession session);
}
