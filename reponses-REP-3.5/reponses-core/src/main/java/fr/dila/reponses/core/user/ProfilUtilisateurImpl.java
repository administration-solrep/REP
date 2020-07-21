package fr.dila.reponses.core.user;

import java.util.Calendar;
import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.constant.ProfilUtilisateurConstants;
import fr.dila.reponses.api.user.ProfilUtilisateur;
import fr.dila.st.api.constant.STProfilUtilisateurConstants;
import fr.dila.st.core.domain.STDomainObjectImpl;
import fr.dila.st.core.util.PropertyUtil;

public class ProfilUtilisateurImpl extends STDomainObjectImpl implements ProfilUtilisateur {

    /**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -7088436799261749114L;

	public ProfilUtilisateurImpl(DocumentModel document) {
        super(document);
    }
    
    @Override
    public String getParametreMail(){
        return PropertyUtil.getStringProperty(document, 
                ProfilUtilisateurConstants.PROFIL_UTILISATEUR_SCHEMA, 
                ProfilUtilisateurConstants.PROFIL_UTILISATEUR_PARAMETRE_MAIL_PROPERTY);
    }
    
    @Override
    public void setParametreMail(String parametreMail){
        PropertyUtil.setProperty(document, 
                ProfilUtilisateurConstants.PROFIL_UTILISATEUR_SCHEMA, 
                ProfilUtilisateurConstants.PROFIL_UTILISATEUR_PARAMETRE_MAIL_PROPERTY,
                parametreMail);
    }
    
    @Override
    public Calendar getDernierChangementMotDePasse() {
    	return PropertyUtil.getCalendarProperty(document, 
                ProfilUtilisateurConstants.PROFIL_UTILISATEUR_SCHEMA, 
                STProfilUtilisateurConstants.PROFIL_UTILISATEUR_DERNIER_CHANGEMENT_MDP_PROPERTY);
    }
    
    @Override
    public void setDernierChangementMotDePasse(Calendar dernierChangementMotDePasse) {
    	PropertyUtil.setProperty(document, 
                ProfilUtilisateurConstants.PROFIL_UTILISATEUR_SCHEMA, 
                STProfilUtilisateurConstants.PROFIL_UTILISATEUR_DERNIER_CHANGEMENT_MDP_PROPERTY,
                dernierChangementMotDePasse);
    }

    @Override
    public List<String> getUserColumns() {
        return PropertyUtil.getStringListProperty(document, 
                ProfilUtilisateurConstants.PROFIL_UTILISATEUR_SCHEMA, 
                ProfilUtilisateurConstants.PROFIL_UTILISATEUR_COLUMNS);
    }

    @Override
    public void setUserColumns(List<String> userColumns) {
        PropertyUtil.setProperty(document, 
                ProfilUtilisateurConstants.PROFIL_UTILISATEUR_SCHEMA, 
                ProfilUtilisateurConstants.PROFIL_UTILISATEUR_COLUMNS,
                userColumns);
    }

}
