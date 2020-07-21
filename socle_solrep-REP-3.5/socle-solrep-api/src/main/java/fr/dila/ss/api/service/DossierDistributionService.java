package fr.dila.ss.api.service;

import java.io.Serializable;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Service de distribution des dossiers du socle SOLREP.
 * 
 * @author jtremeaux
 */
public interface DossierDistributionService extends Serializable {

    /**
     * Initialise les ACL du dossier lors de sa distribution.
     * 
     * @param session Session
     * @param dossierDoc Dossier
     * @param mailboxIdList Mailbox destinataires
     */
    void initDossierDistributionAcl(CoreSession session, final DocumentModel dossierDoc, List<String> mailboxIdList) throws ClientException;
    
    /**
     * Retourne la dernière instnace de feuille de route lancée sur le document.
     * 
     * @param session Session
     * @param dossierDoc Dossier
     * @return Instance de feuille de route
     * @throws ClientException
     */
    DocumentModel getLastDocumentRouteForDossier(CoreSession session, DocumentModel dossierDoc) throws ClientException;
    
    /**
     * Retourne la liste des instances de feuilles de route démarrées sur un d'un dossier.
     * 
     * @param session Session
     * @param dossierDoc Dossier
     * @return Liste des instances de feuilles de route
     * @throws ClientException 
     */
    List<DocumentModel> getDossierRoutes(CoreSession session, DocumentModel dossierDoc) throws ClientException;

    /**
     * Annule la feuille de route actuelle, instancie une nouvelle feuille de route et lève un événement pour
     * déparrer la nouvelle instance sur le cas.
     * 
     * @param session Session
     * @param caseDoc Cas lié à la feuille de route
     * @param oldRouteInstanceDoc Ancienne instance de feuille de route
     * @param newRouteModelDoc Nouveau modèle de feuille de route
     * @return Nouvelle instance de feuille de route
     * @throws ClientException
     */
    DocumentModel substituerFeuilleRoute(CoreSession session, DocumentModel dossierDoc, DocumentModel oldRouteInstanceDoc, DocumentModel newRouteModelDoc,  String typeCreation) throws ClientException;
   
    /**
     * Donne un avis favorable pour l'étape de feuille de route lié au dossier du dossier link.
     * 
     * @param session Session
     * @param dossierDoc Dossier
     * @param dossierLinkDoc dossierLink à valider
     * @throws ClientException
     */
    void validerEtape(CoreSession session, DocumentModel dossierDoc, DocumentModel dossierLinkDoc) throws ClientException;
    
    /**
     * Donne un avis défavorable pour l'étape de feuille de route lié au dossier du dossier link.
     * 
     * @param session Session
     * @param dossierDoc Dossier
     * @param dossierLinkDoc dossierLink à valider
     * @throws ClientException
     */
    void validerEtapeRefus(CoreSession session, DocumentModel dossierDoc, DocumentModel dossierLinkDoc) throws ClientException;
    
    /**
     * Donne un avis 'non concerné' pour l'étape de feuille de route lié au dossier du dossier link.
     * 
     * @param session Session
     * @param dossierDoc Dossier
     * @param dossierLinkDoc dossierLink à valider
     * @throws ClientException
     */
    void validerEtapeNonConcerne(CoreSession session, DocumentModel dossierDoc, DocumentModel dossierLinkDoc) throws ClientException;
    
    /**
     * Démarre une instance de feuille de route (qui a déjà été créée) sur un dossier pour la substitution.
     * 
     * @param session Session
     * @param dossierDoc Dossier
     * @param oldFeuilleRouteDoc Ancienne instance de feuille de route
     * @param newFeuilleRouteDoc Nouvelle instance de feuille de route
     * @param typeCreation Type de création de feuille de route (substitution ou réattribution)
     * @throws ClientException
     */
    void startRouteAfterSubstitution(CoreSession session, DocumentModel oldFeuilleRouteDoc, DocumentModel newFeuilleRouteDoc, String typeCreation) throws ClientException;
    
    /**
     * Redémarre la feuille de route d'un dossier dont la feuille de route a été terminée précédemment.
     * 
     * @param session Session
     * @param dossierDoc Dossier
     * @throws ClientException
     */
    void restartDossier(CoreSession session, DocumentModel dossierDoc) throws ClientException;
    
    /**
     * Rejette le DossierLink passé en paramètre.
     * voir  methode nuxeo 'rejectTask' de la classe 'ActionableCaseLinkActionsBean'
     * 
     * @param dossierLinkDoc Document DossierLink
     * @throws ClientException
     */
    void rejeterDossierLink(CoreSession session, DocumentModel dossierDoc, DocumentModel dossierLinkDoc) throws ClientException;
    
    /**
     * valider une etape pour la reprise
     * @param session session 
     * @param dossierLinkDoc 
     * @throws ClientException
     */
    void validerEtapePourReprise(CoreSession session, DocumentModel dossierLinkDoc) throws ClientException ;
    
    /**
     * Renseigne le status de validation l'étape avec le status passé en parametre
     * 
     * @param session
     * @param dossierLinkDoc
     * @param validationStatus
     * @throws ClientException
     */
    void updateStepValidationStatus(CoreSession session, DocumentModel etapeDoc, String validationStatus, DocumentModel dossierDoc) throws ClientException;
}
