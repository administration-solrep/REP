package fr.dila.reponses.api.service;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.ecm.platform.routing.api.DocumentRoute;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.logging.ReponsesLoggingLine;

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
     * @throws ClientException
     */
    DocumentRoute getDefaultRouteQuestion(CoreSession session) throws ClientException;

    /**
     * Selectionne la feuille de route
     * @param session Session
     * @param dossier
     * @return feuille de route
     * @throws ClientException
     */
    DocumentRoute selectRouteForDossier(CoreSession session, Dossier dossier) throws ClientException;
    
    /**
     * Recherche et retourne la route par défaut pour les errata.
     * 
     * @param session Session
     * @return Route par défaut pour les errata
     * @throws ClientException
     */
    DocumentRoute getDefaultRouteErrata(CoreSession session) throws ClientException;

    /**
     * Migre les modèles de feuille de route d'un ministère à un autre.
     * 
     * @param session
     * @param oldMinistereId
     * @param newMinistereId
     * @param reponsesLoggingLine 
     * @return 
     * @throws ClientException
     */
    void migrateMinistereFeuilleRouteModel(CoreSession session, String oldMinistereId, String newMinistereId, String oldMailboxId, String newMailboxId, ReponsesLoggingLine reponsesLoggingLine) throws ClientException;
    
    /**
     * Retourne tous les steps d'une feuille de route, y compris ceux dans les conteneurs, sans ordre
     * 
     * @param feuilleRouteId id de la feuille de route
     * @param session session
     * @return liste de step
     * @throws ClientException
     */
    List<DocumentModel> getAllRouteElement(String feuilleRouteId, CoreSession session) throws ClientException;

    /**
     * Retourne tous les steps d'une feuille de route non 'done', y compris ceux dans les conteneurs, sans ordre,
     * @param feuilleRouteId
     * @param session
     * @return
     * @throws ClientException 
     */
	List<DocumentModel> getAllReadyOrRunningRouteElement(String feuilleRouteId, CoreSession session) throws ClientException;

}
