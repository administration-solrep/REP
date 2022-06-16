package fr.dila.reponses.ui.bean;

import fr.dila.st.ui.annot.SwBean;
import fr.dila.st.ui.validators.annot.SwLength;
import fr.dila.st.ui.validators.annot.SwPathRefName;
import fr.dila.st.ui.validators.annot.SwRequired;
import javax.ws.rs.FormParam;

@SwBean
public class RequetePersoForm {

    public RequetePersoForm() {
        super();
    }

    @SwRequired
    @SwLength(min = 3)
    @SwPathRefName
    @FormParam("titre")
    private String titre;

    @FormParam("commentaire")
    private String commentaire;

    @FormParam("requete")
    private String requete;

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public String getRequete() {
        return requete;
    }

    public void setRequete(String requete) {
        this.requete = requete;
    }
}
