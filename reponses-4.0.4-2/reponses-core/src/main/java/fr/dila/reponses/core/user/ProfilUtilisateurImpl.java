package fr.dila.reponses.core.user;

import fr.dila.reponses.api.constant.ProfilUtilisateurConstants;
import fr.dila.reponses.api.user.ProfilUtilisateur;
import fr.dila.st.api.constant.STProfilUtilisateurConstants;
import fr.dila.st.core.domain.STDomainObjectImpl;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.Calendar;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;

public class ProfilUtilisateurImpl extends STDomainObjectImpl implements ProfilUtilisateur {
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -7088436799261749114L;

    public ProfilUtilisateurImpl(DocumentModel document) {
        super(document);
    }

    @Override
    public String getParametreMail() {
        return PropertyUtil.getStringProperty(
            document,
            ProfilUtilisateurConstants.PROFIL_UTILISATEUR_SCHEMA,
            ProfilUtilisateurConstants.PROFIL_UTILISATEUR_PARAMETRE_MAIL_PROPERTY
        );
    }

    @Override
    public void setParametreMail(String parametreMail) {
        PropertyUtil.setProperty(
            document,
            ProfilUtilisateurConstants.PROFIL_UTILISATEUR_SCHEMA,
            ProfilUtilisateurConstants.PROFIL_UTILISATEUR_PARAMETRE_MAIL_PROPERTY,
            parametreMail
        );
    }

    @Override
    public Calendar getDernierChangementMotDePasse() {
        return PropertyUtil.getCalendarProperty(
            document,
            ProfilUtilisateurConstants.PROFIL_UTILISATEUR_SCHEMA,
            STProfilUtilisateurConstants.PROFIL_UTILISATEUR_DERNIER_CHANGEMENT_MDP_PROPERTY
        );
    }

    @Override
    public void setDernierChangementMotDePasse(Calendar dernierChangementMotDePasse) {
        PropertyUtil.setProperty(
            document,
            ProfilUtilisateurConstants.PROFIL_UTILISATEUR_SCHEMA,
            STProfilUtilisateurConstants.PROFIL_UTILISATEUR_DERNIER_CHANGEMENT_MDP_PROPERTY,
            dernierChangementMotDePasse
        );
    }

    @Override
    public List<String> getUserColumns() {
        return PropertyUtil.getStringListProperty(
            document,
            ProfilUtilisateurConstants.PROFIL_UTILISATEUR_SCHEMA,
            ProfilUtilisateurConstants.PROFIL_UTILISATEUR_COLUMNS
        );
    }

    @Override
    public void setUserColumns(List<String> userColumns) {
        PropertyUtil.setProperty(
            document,
            ProfilUtilisateurConstants.PROFIL_UTILISATEUR_SCHEMA,
            ProfilUtilisateurConstants.PROFIL_UTILISATEUR_COLUMNS,
            userColumns
        );
    }

    @Override
    public Boolean getMasquerCorbeilles() {
        return PropertyUtil.getBooleanProperty(
            document,
            ProfilUtilisateurConstants.PROFIL_UTILISATEUR_SCHEMA,
            ProfilUtilisateurConstants.PROFIL_UTILISATEUR_MASQUER_CORBEILLES_PROPERTY
        );
    }

    @Override
    public void setMasquerCorbeilles(Boolean masquerCorbeilles) {
        PropertyUtil.setProperty(
            document,
            ProfilUtilisateurConstants.PROFIL_UTILISATEUR_SCHEMA,
            ProfilUtilisateurConstants.PROFIL_UTILISATEUR_MASQUER_CORBEILLES_PROPERTY,
            masquerCorbeilles
        );
    }

    @Override
    public String getDerniersDossiersIntervention() {
        return getStringProperty(
            ProfilUtilisateurConstants.PROFIL_UTILISATEUR_SCHEMA,
            ProfilUtilisateurConstants.PROFIL_UTILISATEUR_LISTE_DERNIERS_DOSSIERS_INTERVENTION
        );
    }

    @Override
    public void setDerniersDossiersIntervention(String listeIdsDerniersDossierIntervention) {
        setProperty(
            ProfilUtilisateurConstants.PROFIL_UTILISATEUR_SCHEMA,
            ProfilUtilisateurConstants.PROFIL_UTILISATEUR_LISTE_DERNIERS_DOSSIERS_INTERVENTION,
            listeIdsDerniersDossierIntervention
        );
    }
}
