package fr.dila.reponses.ui.bean;

import fr.dila.st.ui.annot.NxProp;
import fr.dila.st.ui.annot.SwBean;
import java.util.ArrayList;
import javax.ws.rs.FormParam;

@SwBean
public class DossierSaveForm {
    @NxProp(xpath = "qu:idMinistereRattachement", docType = "Question")
    @FormParam("ministere-rattachement-key")
    private String ministereRattachement;

    @NxProp(xpath = "qu:idDirectionPilote", docType = "Question")
    @FormParam("direction-pilote-key")
    private String directionPilote;

    @FormParam("AN_rubrique-comp")
    private ArrayList<String> anRubriqueComp = new ArrayList<>();

    @FormParam("AN_analyse-comp")
    private ArrayList<String> anAnalyseComp = new ArrayList<>();

    @FormParam("TA_analyse-comp")
    private ArrayList<String> anTAnalyseComp = new ArrayList<>();

    @FormParam("SE_theme-comp")
    private ArrayList<String> seThemeComp = new ArrayList<>();

    @FormParam("SE_renvoi-comp")
    private ArrayList<String> seRenvoiComp = new ArrayList<>();

    @FormParam("SE_rubrique-comp")
    private ArrayList<String> seRubriqueComp = new ArrayList<>();

    @FormParam("motclef_ministere-comp")
    private ArrayList<String> motsClesMinisteres = new ArrayList<>();

    public String getMinistereRattachement() {
        return ministereRattachement;
    }

    public void setMinistereRattachement(String ministereRattachement) {
        this.ministereRattachement = ministereRattachement;
    }

    public String getDirectionPilote() {
        return directionPilote;
    }

    public void setDirectionPilote(String directionPilote) {
        this.directionPilote = directionPilote;
    }

    public ArrayList<String> getAnRubriqueComp() {
        return anRubriqueComp;
    }

    public void setAnRubriqueComp(ArrayList<String> anRubriqueComp) {
        this.anRubriqueComp = anRubriqueComp;
    }

    public ArrayList<String> getAnAnalyseComp() {
        return anAnalyseComp;
    }

    public void setAnAnalyseComp(ArrayList<String> anAnalyseComp) {
        this.anAnalyseComp = anAnalyseComp;
    }

    public ArrayList<String> getAnTAnalyseComp() {
        return anTAnalyseComp;
    }

    public void setAnTAnalyseComp(ArrayList<String> anTAnalyseComp) {
        this.anTAnalyseComp = anTAnalyseComp;
    }

    public ArrayList<String> getSeThemeComp() {
        return seThemeComp;
    }

    public void setSeThemeComp(ArrayList<String> seThemeComp) {
        this.seThemeComp = seThemeComp;
    }

    public ArrayList<String> getSeRenvoiComp() {
        return seRenvoiComp;
    }

    public void setSeRenvoiComp(ArrayList<String> seRenvoiComp) {
        this.seRenvoiComp = seRenvoiComp;
    }

    public ArrayList<String> getSeRubriqueComp() {
        return seRubriqueComp;
    }

    public void setSeRubriqueComp(ArrayList<String> seRubriqueComp) {
        this.seRubriqueComp = seRubriqueComp;
    }

    public ArrayList<String> getMotsClesMinisteres() {
        return motsClesMinisteres;
    }

    public void setMotsClesMinisteres(ArrayList<String> motsClesMinisteres) {
        this.motsClesMinisteres = motsClesMinisteres;
    }
}
