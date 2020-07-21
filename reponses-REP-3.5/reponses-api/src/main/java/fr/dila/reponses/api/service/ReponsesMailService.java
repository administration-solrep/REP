package fr.dila.reponses.api.service;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.cases.Question;
import fr.dila.st.api.service.STMailService;

public interface ReponsesMailService extends STMailService {

    /**
     * Envoi du mail de nouveau mot de passe.
     */
    void sendMailUserPasswordCreation(CoreSession session, String userId, String password) throws ClientException;

    /**
     * Envoi un mail à l'administrateur fonctionnel après l'échec de validation de la signature d'un dossier.
     * @param documentManager
     * @param dossierDoc
     * @throws ClientException 
     */
    void sendMailAfterSignatureError(CoreSession session, DocumentModel dossierDoc) throws ClientException;

    /**
     * Envoi un mail à l'administrateur fonctionnel lorsque qu'aucune feuille de route n'est trouvée pour un dossier.
     * @param session
     * @param question
     * @throws ClientException 
     */
    void sendMailNoRouteFound(CoreSession session, Question question) throws ClientException;
    
    /**
     * Envoi un mail aux utilisateurs du poste de l'étape en cours, aux utilisateurs du DBC et au utilisateurs du poste Superviseur SGG
     * @param session
     * @param dossierDoc
     * @throws ClientException
     */
    void sendMailAfterStateChangedQuestion (CoreSession session, DocumentModel dossierDoc, String nouvelEtat) throws ClientException;

	/**
	 * Envoi une alerte contenant la liste des question ayant fait l'objet d'un retrait
	 * 
	 * @param session
	 * @throws ClientException 
	 */
	void sendDailyRetiredMail(CoreSession session) throws ClientException;
}
