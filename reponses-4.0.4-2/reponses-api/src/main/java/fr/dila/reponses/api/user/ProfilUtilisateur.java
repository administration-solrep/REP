package fr.dila.reponses.api.user;

import fr.dila.st.api.user.STProfilUtilisateur;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface ProfilUtilisateur extends STProfilUtilisateur {
    /**
     * retourne le documentModel associé
     */
    DocumentModel getDocument();

    /**
     * retourne le choix de parametrage d'envoi de mail de l'utilisateur
     * @return
     */
    String getParametreMail();

    /**
     *
     */
    void setParametreMail(String parametreMail);

    List<String> getUserColumns();

    void setUserColumns(List<String> userColumns);

    /**
     * retourne le choix de masquage des corbeilles vides de l'utilisateur
     *
     * @return
     */
    Boolean getMasquerCorbeilles();

    void setMasquerCorbeilles(Boolean masquerCorbeilles);
}
