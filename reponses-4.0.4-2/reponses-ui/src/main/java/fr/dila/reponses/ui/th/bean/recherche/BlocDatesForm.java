package fr.dila.reponses.ui.th.bean.recherche;

import static fr.dila.reponses.api.constant.ReponsesConstant.RECHERCHE_DOCUMENT_TYPE;
import static fr.dila.reponses.api.constant.RequeteConstants.DATE_JO_QUESTION_DEBUT_XPATH;
import static fr.dila.reponses.api.constant.RequeteConstants.DATE_JO_QUESTION_FIN_XPATH;
import static fr.dila.reponses.api.constant.RequeteConstants.DATE_JO_REPONSE_DEBUT_XPATH;
import static fr.dila.reponses.api.constant.RequeteConstants.DATE_JO_REPONSE_FIN_XPATH;
import static fr.dila.reponses.api.constant.RequeteConstants.DATE_SIGNALEMENT_DEBUT_XPATH;
import static fr.dila.reponses.api.constant.RequeteConstants.DATE_SIGNALEMENT_FIN_XPATH;

import fr.dila.st.ui.annot.NxProp;
import fr.dila.st.ui.annot.SwBean;
import java.io.Serializable;
import java.util.Calendar;
import javax.ws.rs.FormParam;

@SwBean
public class BlocDatesForm implements Serializable {
    private static final long serialVersionUID = -2656274966888301301L;

    @NxProp(xpath = DATE_JO_QUESTION_DEBUT_XPATH, docType = RECHERCHE_DOCUMENT_TYPE)
    @FormParam("dateJOQuestionDebut")
    private Calendar dateJOQuestionDebut;

    @NxProp(xpath = DATE_JO_QUESTION_FIN_XPATH, docType = RECHERCHE_DOCUMENT_TYPE)
    @FormParam("dateJOQuestionFin")
    private Calendar dateJOQuestionFin;

    @NxProp(xpath = DATE_JO_REPONSE_DEBUT_XPATH, docType = RECHERCHE_DOCUMENT_TYPE)
    @FormParam("dateJOReponsesDebut")
    private Calendar dateJOReponsesDebut;

    @NxProp(xpath = DATE_JO_REPONSE_FIN_XPATH, docType = RECHERCHE_DOCUMENT_TYPE)
    @FormParam("dateJOReponsesFin")
    private Calendar dateJOReponsesFin;

    @NxProp(xpath = DATE_SIGNALEMENT_DEBUT_XPATH, docType = RECHERCHE_DOCUMENT_TYPE)
    @FormParam("dateSignalementDebut")
    private Calendar dateSignalementDebut;

    @NxProp(xpath = DATE_SIGNALEMENT_FIN_XPATH, docType = RECHERCHE_DOCUMENT_TYPE)
    @FormParam("dateSignalementFin")
    private Calendar dateSignalementFin;

    public BlocDatesForm() {}

    public Calendar getDateJOQuestionDebut() {
        return dateJOQuestionDebut;
    }

    public void setDateJOQuestionDebut(Calendar dateJOQuestionDebut) {
        this.dateJOQuestionDebut = dateJOQuestionDebut;
    }

    public Calendar getDateJOQuestionFin() {
        return dateJOQuestionFin;
    }

    public void setDateJOQuestionFin(Calendar dateJOQuestionFin) {
        this.dateJOQuestionFin = dateJOQuestionFin;
    }

    public Calendar getDateJOReponsesDebut() {
        return dateJOReponsesDebut;
    }

    public void setDateJOReponsesDebut(Calendar dateJOReponsesDebut) {
        this.dateJOReponsesDebut = dateJOReponsesDebut;
    }

    public Calendar getDateJOReponsesFin() {
        return dateJOReponsesFin;
    }

    public void setDateJOReponsesFin(Calendar dateJOReponsesFin) {
        this.dateJOReponsesFin = dateJOReponsesFin;
    }

    public Calendar getDateSignalementDebut() {
        return dateSignalementDebut;
    }

    public void setDateSignalementDebut(Calendar dateSignalementDebut) {
        this.dateSignalementDebut = dateSignalementDebut;
    }

    public Calendar getDateSignalementFin() {
        return dateSignalementFin;
    }

    public void setDateSignalementFin(Calendar dateSignalementFin) {
        this.dateSignalementFin = dateSignalementFin;
    }
}
