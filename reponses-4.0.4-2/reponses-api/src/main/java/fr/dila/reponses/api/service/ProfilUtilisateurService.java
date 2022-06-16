package fr.dila.reponses.api.service;

import fr.dila.reponses.api.user.ProfilUtilisateur;
import fr.dila.st.api.service.STProfilUtilisateurService;
import fr.dila.st.api.user.STUser;
import fr.sword.naiad.nuxeo.commons.core.helper.VocabularyHelper;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;

public interface ProfilUtilisateurService extends STProfilUtilisateurService<ProfilUtilisateur> {
    /**
     * Retourne la liste filtrée des utilisateurs souhaitant recevoir des mails
     * @param session CoreSession
     * @param userList List<STUser>
     * @return liste filtrée
     *
     */
    List<STUser> getFilteredUserList(CoreSession session, List<STUser> userList);

    /**
     * Retourne la liste filtrée des utilisateurs selon un paramètre d'envoi de mails
     * @param session
     * @param userList
     * @param mailMode
     * @return
     *
     */
    List<STUser> getFilteredUserList(CoreSession session, List<STUser> userList, String mailMode);

    List<VocabularyHelper.Entry> getVocEntryAllowedColumn(CoreSession session);

    List<VocabularyHelper.Entry> getVocEntryUserColumn(CoreSession session);

    List<String> getUserColumn(CoreSession session);
}
