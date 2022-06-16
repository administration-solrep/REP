package fr.dila.reponses.api.service;

import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Service permettant de déléguer ses droits à d'autres utilisateurs.
 *
 * @author jtremeaux
 */
public interface DelegationService {
    /**
     * Retourne la racine des délégations de l'utilisateur en cours.
     *
     * @param session
     *            Session
     * @return Racine des délégations
     */
    DocumentModel getDelegationRoot(CoreSession session);

    /**
     * Retourne la liste des délégations actives dont l'utilisateur est destinataire.
     *
     * @param session
     *            Session
     * @param userId
     *            Utilisateur destinataire
     */
    List<DocumentModel> findActiveDelegationForUser(CoreSession session, String userId);

    /**
     * Envoir d'un email à la création / modification d'une délégation.
     *
     * @param session
     *            Session
     * @param delegationDoc
     *            Document délégation
     */
    void sendDelegationEmail(CoreSession session, DocumentModel delegationDoc);
}
