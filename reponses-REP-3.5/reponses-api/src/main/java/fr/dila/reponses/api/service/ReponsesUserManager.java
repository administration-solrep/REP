package fr.dila.reponses.api.service;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.service.STUserManager;

public interface ReponsesUserManager extends STUserManager {

    /**
     * Création d'un utilisateur occasionnel
     * 
     * @param session
     * @param newUser
     * @return
     * @throws ClientException
     */
    DocumentModel createUserOccasional(CoreSession session, DocumentModel newUser) throws ClientException;
    
    DocumentModel createUser(DocumentModel userModel) throws ClientException;

}
