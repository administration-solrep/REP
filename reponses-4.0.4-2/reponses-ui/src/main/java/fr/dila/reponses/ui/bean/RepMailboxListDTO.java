package fr.dila.reponses.ui.bean;

import fr.dila.reponses.api.constant.ProfilUtilisateurConstants;
import fr.dila.ss.api.constant.SSProfilUtilisateurConstants;
import fr.dila.ss.api.enums.TypeRegroupement;
import fr.dila.ss.ui.bean.MailboxListDTO;
import fr.dila.st.ui.annot.NxProp;

public class RepMailboxListDTO extends MailboxListDTO {
    @NxProp(
        xpath = ProfilUtilisateurConstants.PROFIL_UTILISATEUR_MASQUER_CORBEILLES_XPATH,
        docType = SSProfilUtilisateurConstants.WORKSPACE_DOCUMENT_TYPE
    )
    private Boolean masquerCorbeilles = false;

    public RepMailboxListDTO() {
        super();
        setModeTri(TypeRegroupement.PAR_MINISTERE);
    }

    public Boolean getMasquerCorbeilles() {
        return masquerCorbeilles;
    }

    public void setMasquerCorbeilles(Boolean masquerCorbeilles) {
        this.masquerCorbeilles = masquerCorbeilles;
    }
}
