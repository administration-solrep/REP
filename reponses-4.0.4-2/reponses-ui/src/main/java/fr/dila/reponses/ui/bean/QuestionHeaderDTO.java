package fr.dila.reponses.ui.bean;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.st.ui.annot.NxProp;

public class QuestionHeaderDTO {
    @NxProp(
        xpath = DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX + ":" + DossierConstants.DOSSIER_TYPE_QUESTION,
        docType = DossierConstants.QUESTION_DOCUMENT_TYPE
    )
    private String typeQuestion = "";

    @NxProp(
        xpath = DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX + ":" + DossierConstants.DOSSIER_NUMERO_QUESTION,
        docType = DossierConstants.QUESTION_DOCUMENT_TYPE
    )
    private Long numeroQuestion;

    @NxProp(
        xpath = DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX +
        ":" +
        DossierConstants.DOSSIER_CIVILITE_AUTEUR_QUESTION,
        docType = DossierConstants.QUESTION_DOCUMENT_TYPE
    )
    private String civiliteAuteur = "";

    @NxProp(
        xpath = DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX +
        ":" +
        DossierConstants.DOSSIER_PRENOM_AUTEUR_QUESTION,
        docType = DossierConstants.QUESTION_DOCUMENT_TYPE
    )
    private String prenomAuteur = "";

    @NxProp(
        xpath = DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX + ":" + DossierConstants.DOSSIER_NOM_AUTEUR_QUESTION,
        docType = DossierConstants.QUESTION_DOCUMENT_TYPE
    )
    private String nomAuteur = "";

    @NxProp(
        xpath = DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX +
        ":" +
        DossierConstants.DOSSIER_GROUPE_POLITIQUE_QUESTION,
        docType = DossierConstants.QUESTION_DOCUMENT_TYPE
    )
    private String groupePolitique = "";

    public QuestionHeaderDTO() {}

    public String getTypeQuestion() {
        return typeQuestion;
    }

    public void setTypeQuestion(String typeQuestion) {
        this.typeQuestion = typeQuestion;
    }

    public Long getNumeroQuestion() {
        return numeroQuestion;
    }

    public void setNumeroQuestion(Long numeroQuestion) {
        this.numeroQuestion = numeroQuestion;
    }

    public String getCiviliteAuteur() {
        return civiliteAuteur;
    }

    public void setCiviliteAuteur(String civiliteAuteur) {
        this.civiliteAuteur = civiliteAuteur;
    }

    public String getPrenomAuteur() {
        return prenomAuteur;
    }

    public void setPrenomAuteur(String prenomAuteur) {
        this.prenomAuteur = prenomAuteur;
    }

    public String getNomAuteur() {
        return nomAuteur;
    }

    public void setNomAuteur(String nomAuteur) {
        this.nomAuteur = nomAuteur;
    }

    public String getGroupePolitique() {
        return groupePolitique;
    }

    public void setGroupePolitique(String groupePolitique) {
        this.groupePolitique = groupePolitique;
    }
}
