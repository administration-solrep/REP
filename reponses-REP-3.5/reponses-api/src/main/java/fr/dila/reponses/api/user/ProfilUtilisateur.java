package fr.dila.reponses.api.user;

import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.user.STProfilUtilisateur;

public interface ProfilUtilisateur extends STProfilUtilisateur{

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
    
    
}
