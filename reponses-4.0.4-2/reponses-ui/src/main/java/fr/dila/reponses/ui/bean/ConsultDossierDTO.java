package fr.dila.reponses.ui.bean;

import fr.dila.ss.ui.bean.SSConsultDossierDTO;

public class ConsultDossierDTO extends SSConsultDossierDTO {
    private QuestionHeaderDTO questionInfo;

    private Boolean isSigned;

    private boolean urgent;

    private boolean renouvele;

    private boolean signale;

    private String delai;

    public ConsultDossierDTO() {}

    public QuestionHeaderDTO getQuestionInfo() {
        return questionInfo;
    }

    public void setQuestionInfo(QuestionHeaderDTO questionInfo) {
        this.questionInfo = questionInfo;
    }

    public Boolean getIsSigned() {
        return isSigned;
    }

    public void setIsSigned(Boolean isSigned) {
        this.isSigned = isSigned;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }

    public boolean isRenouvele() {
        return renouvele;
    }

    public void setRenouvele(boolean renouvele) {
        this.renouvele = renouvele;
    }

    public boolean isSignale() {
        return signale;
    }

    public void setSignale(boolean signale) {
        this.signale = signale;
    }

    public String getDelai() {
        return delai;
    }

    public void setDelai(String delai) {
        this.delai = delai;
    }
}
