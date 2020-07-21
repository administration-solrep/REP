package fr.dila.reponses.core.service;

import java.util.Calendar;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.reponses.api.service.ProfilUtilisateurService;
import fr.dila.reponses.api.service.ReponsesLoginManager;
import fr.dila.reponses.api.user.ProfilUtilisateur;

/**
 * Gestionnaires de login de l'application Réponses.
 * 
 * @author asatre
 */
public class ReponsesLoginManagerImpl implements ReponsesLoginManager {
    
	private static final long serialVersionUID = -2127267288938409203L;
	
	/**
	 * Default constructor
	 */
	public ReponsesLoginManagerImpl(){
		// do nothing
	}
	
	@Override
    public void updateUserAfterLogin(CoreSession session, String username) throws ClientException {
	    
        // on recupere son profil utilisateur
        final ProfilUtilisateurService profilUtilisateurService = ReponsesServiceLocator.getProfilUtilisateurService();
        ProfilUtilisateur profilUtilisateur = (ProfilUtilisateur) profilUtilisateurService.getProfilUtilisateur(session, username);

        if(profilUtilisateur != null){
            if ( profilUtilisateur.getDernierChangementMotDePasse() == null ) {
    			// Il s'agit de la première connexion
    			profilUtilisateur.setDernierChangementMotDePasse(Calendar.getInstance());
    		}
            session.saveDocument(profilUtilisateur.getDocument());
            session.save();
        }        
    }
}
