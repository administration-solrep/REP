package fr.dila.reponses.api.service;

import fr.dila.st.api.service.STUserManager;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface ReponsesUserManager extends STUserManager {
    /**
     * Création d'un utilisateur occasionnel
     *
     * @param session
     * @param newUser
     * @return
     */
    DocumentModel createUserOccasional(CoreSession session, DocumentModel newUser);

    DocumentModel createUser(DocumentModel userModel);
}
