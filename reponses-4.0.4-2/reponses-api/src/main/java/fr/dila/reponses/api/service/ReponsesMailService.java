package fr.dila.reponses.api.service;

import fr.dila.reponses.api.cases.Question;
import fr.dila.st.api.service.STMailService;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface ReponsesMailService extends STMailService {
    /**
     * Envoi du mail de nouveau mot de passe.
     */
    void sendMailUserPasswordCreation(CoreSession session, String userId, String password);

    /**
     * Envoi un mail à l'administrateur fonctionnel après l'échec de validation de la signature d'un dossier.
     * @param documentManager
     * @param dossierDoc
     *
     */
    void sendMailAfterSignatureError(CoreSession session, DocumentModel dossierDoc);

    /**
     * Envoi un mail à l'administrateur fonctionnel lorsque qu'aucune feuille de route n'est trouvée pour un dossier.
     * @param session
     * @param question
     *
     */
    void sendMailNoRouteFound(CoreSession session, Question question);

    /**
     * Envoi un mail aux utilisateurs du poste de l'étape en cours, aux utilisateurs du DBC et au utilisateurs du poste Superviseur SGG
     * @param session
     * @param dossierDoc
     *
     */
    void sendMailAfterStateChangedQuestion(CoreSession session, DocumentModel dossierDoc, String nouvelEtat);

    /**
     * Envoi une alerte contenant la liste des question ayant fait l'objet d'un retrait
     *
     * @param session
     *
     */
    void sendDailyRetiredMail(CoreSession session);
}
