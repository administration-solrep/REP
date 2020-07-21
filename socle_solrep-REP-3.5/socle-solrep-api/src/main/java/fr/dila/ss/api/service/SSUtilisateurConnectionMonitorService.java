package fr.dila.ss.api.service;

import java.util.Date;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.ss.api.documentmodel.SSInfoUtilisateurConnection;
import fr.dila.st.api.user.STUser;

public interface SSUtilisateurConnectionMonitorService {

    /**
     * creer ou update les infos de la connection d'un utilisateur pour les statistiques
     * 
     * @param session
     * @param user le user concerne
     * @throws ClientException
     */
    void createOrUpdateInfoUtilisateurConnection(CoreSession session, STUser user) throws ClientException;

    /**
     * supprimer les infos de la connection d'un utilisateur pour les statistiques
     * 
     * @param session
     * @param user le user concerne
     * @throws ClientException
     */
    void removeInfoUtilisateurConnection(CoreSession session, STUser user) throws ClientException;

    /**
     * retourner tous les infos de la connections des utilisateurs
     * 
     * @param session
     * @return
     * @throws ClientException
     */
    List<DocumentModel> getAllInfoUtilisateurConnection(CoreSession session) throws ClientException;

    void updateInfoUtilisateurConnection(CoreSession session, STUser user, boolean islogout) throws ClientException;

    List<DocumentModel> getAllInfoUtilisateurConnection(CoreSession session, boolean isLogout) throws ClientException;

    SSInfoUtilisateurConnection getInfoUtilisateurConnection(CoreSession session, String userName) throws ClientException;
    
    List<String> getListInfoUtilisateurConnection(final CoreSession session, Date dateDeConnexion) throws ClientException;

	List<String> getListInfoUtilisateurConnectionNotConnectedSince(final CoreSession session, Date dateDeConnexion) throws ClientException;
}
