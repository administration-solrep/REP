package fr.dila.reponses.api.service;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.ui.web.directory.VocabularyEntry;

import fr.dila.st.api.service.STProfilUtilisateurService;
import fr.dila.st.api.user.STUser;

public interface ProfilUtilisateurService extends STProfilUtilisateurService {

    /**
     * Retourne la liste filtrée des utilisateurs souhaitant recevoir des mails
     * @param session CoreSession
     * @param userList List<STUser>
     * @return liste filtrée
     * @throws ClientException 
     */
    List<STUser> getFilteredUserList(CoreSession session, List<STUser> userList) throws ClientException;

    /**
     * Retourne la liste filtrée des utilisateurs selon un paramètre d'envoi de mails
     * @param session
     * @param userList
     * @param mailMode
     * @return
     * @throws ClientException
     */
    List<STUser> getFilteredUserList(CoreSession session, List<STUser> userList, String mailMode) throws ClientException;
    
    List<VocabularyEntry> getVocEntryAllowedColumn(CoreSession session) throws ClientException;

    List<VocabularyEntry> getVocEntryUserColumn(CoreSession session) throws ClientException;

    List<String> getUserColumn(CoreSession session) throws ClientException;
	
}
