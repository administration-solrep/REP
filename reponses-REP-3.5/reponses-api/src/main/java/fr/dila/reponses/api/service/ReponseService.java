package fr.dila.reponses.api.service;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;

import fr.dila.reponses.api.cases.Reponse;

public interface ReponseService {
    /**
     * get Reponse major version number.
     * 
     * @return Numéro de version majeur
     * @throws ClientException
     */
    int getReponseMajorVersionNumber(CoreSession session, DocumentModel reponse) throws ClientException;

    /**
     * get Reponse old Version Document.
     * 
     * @return old Version Document
     * @throws ClientException
     */
    DocumentModel getReponseOldVersionDocument(CoreSession session, DocumentModel reponse, int versionNumber) throws ClientException;

    /**
     * get Reponse Version Document List.
     * 
     * @return Reponse Version Document List.
     * @throws ClientException
     */
    List<DocumentModel> getReponseVersionDocumentList(CoreSession session, DocumentModel reponse) throws ClientException;

    /**
     * get Reponse From Dossier.
     * 
     * @return Reponse
     */
    DocumentModel getReponseFromDossier(CoreSession session, DocumentModel dossier);
    
    /**
     * Modification de la réponse.
     * 
     * @param session Session
     * @param reponseDoc Réponse
     * @param dossier
     * @return
     * @throws ClientException
     */
    DocumentModel saveReponse(CoreSession session, DocumentModel reponseDoc, DocumentModel dossier) throws ClientException;
    
    /**
     * Modification de la réponse et de l'erratum éventuel.
     * 
     * @param session Session
     * @param reponseDoc Réponse
     * @param dossier
     * @return
     * @throws ClientException
     */
    DocumentModel saveReponseAndErratum(CoreSession documentManager, DocumentModel reponse, DocumentModel currentDocument) throws ClientException;
    
    /**
     * create a Reponse version,increment Reponse and save the new Reponse
     * Document from Ministere, calling by WebService
     *  
     * @param session Session
     * @param reponse Réponse
     * @return
     * @throws ClientException
     */
    DocumentModel saveReponseFromMinistere(CoreSession session, DocumentModel reponse) throws ClientException;
    
    /**
     * Increments the version of the reponse.
     * 
     * @param session
     * @param reponse
     * @throws ClientException
     */
    DocumentModel incrementReponseVersion(CoreSession session, DocumentModel reponse) throws ClientException;
    
    /**
     * Return Dossier from Reponse.
     * 
     * @param session
     * @param reponse
     * @return
     * @throws ClientException
     */
    DocumentModel getDossierFromReponse(CoreSession session, DocumentModel reponse) throws ClientException;
    
    /**
     * 
     * @param session
     * @param reponse 
     * @return liste des contributeurs de chaque version, par ordre de version
     * @throws PropertyException
     * @throws ClientException
     */
    List<String> getVersionsContributorsFromReponse(CoreSession session, DocumentModel reponse) throws PropertyException, ClientException;

    /**
     * brise la signature sur action utilisateur
     * @param session
     * @param reponse
     * @param dossier
     * @return
     * @throws ClientException
     */
	DocumentModel briserSignatureReponse(CoreSession session, Reponse reponse, DocumentModel dossier) throws ClientException;

	/**
	 * Indique si la réponse est signée ou non
	 * @param dossier
	 * @return
	 */
	boolean isReponseSignee(CoreSession session, DocumentModel dossierDoc);
	

	/**
	 * Indique si la réponse est publié ou non
	 * @param dossier
	 * @return
	 */
	boolean isReponsePublished(CoreSession session, DocumentModel dossierDoc);

}
