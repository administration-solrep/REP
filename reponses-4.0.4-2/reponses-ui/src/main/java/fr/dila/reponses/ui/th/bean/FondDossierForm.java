package fr.dila.reponses.ui.th.bean;

import fr.dila.st.ui.annot.SwBean;
import fr.dila.st.ui.th.bean.PaginationForm;
import javax.ws.rs.QueryParam;

@SwBean(keepdefaultValue = true)
public class FondDossierForm extends PaginationForm {
    @QueryParam("fichiers")
    private String fichiers;

    @QueryParam("entite")
    private String entite;

    @QueryParam("auteur")
    private String auteur;

    @QueryParam("date")
    private String date;

    @QueryParam("version")
    private String version;

    public FondDossierForm() {
        super();
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getFichiers() {
        return fichiers;
    }

    public void setFichiers(String fichiers) {
        this.fichiers = fichiers;
    }

    public String getEntite() {
        return entite;
    }

    public void setEntite(String entite) {
        this.entite = entite;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
