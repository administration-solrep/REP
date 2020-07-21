import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Status;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.transaction.TransactionHelper;

import fr.dila.reponses.api.service.ProfilUtilisateurService;
import fr.dila.reponses.api.user.ProfilUtilisateur;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.core.util.ExcelUtil;

import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.service.STServiceLocator;

/**
 * script groovy d'initialisation des dates de changement de mot de passe
 * 
 */

    
print "Début du script groovy d'initialisation des dates de changement de mot de passe";
print "-------------------------------------------------------------------------------";

    final UserManager userManager = STServiceLocator.getUserManager();
    final STMailService mailService = STServiceLocator.getSTMailService();
    final ProfilUtilisateurService profilUtilisateurService = ReponsesServiceLocator.getProfilUtilisateurService();
    
    // On récupère tous les ids par ordre alphabétique
    Map<String, Serializable> filter = new HashMap<String, Serializable>();
    final DocumentModelList userModelList = userManager.searchUsers(filter, null);
    final List<ProfilUtilisateur> allUsersWithProfilList = new ArrayList<ProfilUtilisateur>();
    
    for (DocumentModel userDocModel : userModelList) {
    	STUser user = userDocModel.getAdapter(STUser.class);
    	if (user.isActive()) {
    		try {
        		DocumentModel profilModel = profilUtilisateurService.getOrInitUserProfilFromId(Session,user.getUsername());
        		if (profilModel != null) {
        			ProfilUtilisateur profilUtilisateur =  profilModel.getAdapter(ProfilUtilisateur.class);
    	    		if (profilUtilisateur != null) {
    	        		allUsersWithProfilList.add(profilUtilisateur);
    	        	}
        		}
        	} catch (ClientException e) {
        		print "ATTENTION : Impossible de récupérer le profil de " + user.getUsername();
        		print e.getMessage();
        	}
    	}
    	TransactionHelper.commitOrRollbackTransaction();
    	TransactionHelper.startTransaction();
    }
    
    print allUsersWithProfilList.size() + " profils récupérés."
    
    if (TransactionHelper.lookupUserTransaction().getStatus() == Status.STATUS_NO_TRANSACTION) {
    	TransactionHelper.startTransaction();
    }
    
    static final int NB_GROUPE = 4;
    final int TAILLE_GROUPE = allUsersWithProfilList.size()/NB_GROUPE;
	Calendar calendar = Calendar.getInstance();
	calendar.set(2014,Calendar.SEPTEMBER,25,0,0,0);
    for ( int i=0 ; i<allUsersWithProfilList.size() ; i++ ) {
    	ProfilUtilisateur profilUtilisateur = allUsersWithProfilList.get(i);
		if (i == TAILLE_GROUPE-1) {
    		calendar.add(Calendar.DATE, 7);
    	} else if (i == 2*TAILLE_GROUPE-1) {
    		calendar.add(Calendar.DATE, 7);
    	} else if (i == 3*TAILLE_GROUPE-1) {
    		calendar.add(Calendar.DATE, 7);
    	}
    	profilUtilisateur.setDernierChangementMotDePasse(calendar);
    	Session.saveDocument(profilUtilisateur.getDocument());
    	Session.save();
    	TransactionHelper.commitOrRollbackTransaction();
    	TransactionHelper.startTransaction();
    }
    
    
print "-------------------------------------------------------------------------------";
print "Fin du script groovy d'initialisation des dates de changement de mot de passe";
return "Fin du script groovy";
