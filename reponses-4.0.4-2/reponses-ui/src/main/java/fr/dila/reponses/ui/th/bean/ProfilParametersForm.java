package fr.dila.reponses.ui.th.bean;

import static fr.dila.reponses.api.constant.ProfilUtilisateurConstants.PROFIL_UTILISATEUR_COLUMNS_XPATH;
import static fr.dila.reponses.api.constant.ProfilUtilisateurConstants.PROFIL_UTILISATEUR_PARAMETRE_MAIL_XPATH;
import static fr.dila.st.api.constant.STProfilUtilisateurConstants.WORKSPACE_DOCUMENT_TYPE;

import fr.dila.st.ui.annot.NxProp;
import fr.dila.st.ui.annot.SwBean;
import java.util.ArrayList;
import javax.ws.rs.FormParam;

@SwBean
public class ProfilParametersForm {
    @NxProp(xpath = PROFIL_UTILISATEUR_COLUMNS_XPATH, docType = WORKSPACE_DOCUMENT_TYPE)
    @FormParam("metadatas")
    private ArrayList<String> userMetadatas = new ArrayList<>();

    @NxProp(xpath = PROFIL_UTILISATEUR_PARAMETRE_MAIL_XPATH, docType = WORKSPACE_DOCUMENT_TYPE)
    @FormParam("userEmail")
    private String userEmail;

    public ProfilParametersForm() {
        super();
    }

    public ArrayList<String> getUserMetadatas() {
        return userMetadatas;
    }

    public void setUserMetadatas(ArrayList<String> userMetadatas) {
        this.userMetadatas = userMetadatas;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
