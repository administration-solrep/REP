package fr.dila.reponses.ui.th.bean;

import fr.dila.reponses.ui.bean.IndexationDTO;
import fr.dila.ss.ui.th.bean.ModeleFdrForm;
import fr.dila.st.ui.annot.SwBean;
import java.util.ArrayList;
import javax.ws.rs.FormParam;

@SwBean
public class ReponsesModeleFdrForm extends ModeleFdrForm {
    @FormParam("titreQuestion")
    private String titreQuestion;

    @FormParam("AN_rubrique-comp")
    private ArrayList<String> anRubriqueComp;

    @FormParam("AN_analyse-comp")
    private ArrayList<String> anAnalyseComp;

    @FormParam("TA_analyse-comp")
    private ArrayList<String> anTAnalyseComp;

    @FormParam("SE_theme-comp")
    private ArrayList<String> seThemeComp;

    @FormParam("SE_renvoi-comp")
    private ArrayList<String> seRenvoiComp;

    @FormParam("SE_rubrique-comp")
    private ArrayList<String> seRubriqueComp;

    @FormParam("motclef_ministere-comp")
    private ArrayList<String> motsClesMinisteres;

    private IndexationDTO indexationDTO;

    public ReponsesModeleFdrForm() {
        indexationDTO = new IndexationDTO();
        anRubriqueComp = new ArrayList<>();
        anAnalyseComp = new ArrayList<>();
        anTAnalyseComp = new ArrayList<>();
        seThemeComp = new ArrayList<>();
        seRenvoiComp = new ArrayList<>();
        seRubriqueComp = new ArrayList<>();
        motsClesMinisteres = new ArrayList<>();
    }

    public String getTitreQuestion() {
        return titreQuestion;
    }

    public void setTitreQuestion(String titreQuestion) {
        this.titreQuestion = titreQuestion;
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

    public IndexationDTO getIndexationDTO() {
        return indexationDTO;
    }

    public void setIndexationDTO(IndexationDTO indexationDTO) {
        this.indexationDTO = indexationDTO;
    }
}
