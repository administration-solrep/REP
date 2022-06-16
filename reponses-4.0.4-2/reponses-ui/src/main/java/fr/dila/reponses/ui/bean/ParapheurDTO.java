package fr.dila.reponses.ui.bean;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.st.core.requete.recherchechamp.descriptor.ChampDescriptor;
import fr.dila.st.ui.annot.NxProp;
import fr.dila.st.ui.annot.SwBean;
import fr.dila.st.ui.bean.SelectValueDTO;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.FormParam;

@SwBean
public class ParapheurDTO {
    @NxProp(xpath = "qu:texteQuestion", docType = "Question")
    @FormParam("texteQuestion")
    private String texteQuestion;

    @NxProp(xpath = "qu:texte_joint", docType = "Question")
    @FormParam("texteJoint")
    private String texteJoint;

    @NxProp(xpath = "note:note", docType = "Reponse")
    @FormParam("tinymce")
    private String reponseNote;

    private String reponseType;

    private SelectValueDTO version;

    private Boolean isEdit;

    @NxProp(
        xpath = DossierConstants.REPONSE_DOCUMENT_SCHEMA_PREFIX + ":" + DossierConstants.REPONSE_CURRENT_ERRATUM,
        docType = DossierConstants.REPONSE_DOCUMENT_TYPE
    )
    @FormParam("erratum")
    private String erratum;

    private boolean published;

    private boolean hasErrata;

    private List<ChampDescriptor> champs = new ArrayList<>();

    public List<ChampDescriptor> getChamps() {
        return champs;
    }

    public void setChamps(List<ChampDescriptor> champs) {
        this.champs = champs;
    }

    public ParapheurDTO() {}

    public String getTexteQuestion() {
        return texteQuestion;
    }

    public void setTexteQuestion(String texteQuestion) {
        this.texteQuestion = texteQuestion;
    }

    public String getTexteJoint() {
        return texteJoint;
    }

    public void setTexteJoint(String texteJoint) {
        this.texteJoint = texteJoint;
    }

    public String getReponseNote() {
        return reponseNote;
    }

    public void setReponseNote(String reponseNote) {
        this.reponseNote = reponseNote;
    }

    public String getReponseType() {
        return reponseType;
    }

    public void setReponseType(String reponseType) {
        this.reponseType = reponseType;
    }

    public SelectValueDTO getVersion() {
        return version;
    }

    public void setVersion(SelectValueDTO version) {
        this.version = version;
    }

    public Boolean getIsEdit() {
        return isEdit;
    }

    public void setIsEdit(Boolean isEdit) {
        this.isEdit = isEdit;
    }

    public String getErratum() {
        return erratum;
    }

    public void setErratum(String erratum) {
        this.erratum = erratum;
    }

    public boolean getPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public boolean isHasErrata() {
        return hasErrata;
    }

    public void setHasErrata(boolean hasErrata) {
        this.hasErrata = hasErrata;
    }
}
