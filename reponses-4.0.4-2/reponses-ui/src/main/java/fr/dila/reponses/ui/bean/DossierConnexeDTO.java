package fr.dila.reponses.ui.bean;

public class DossierConnexeDTO extends AllotissementDTO {
    private boolean disabled;
    private String dossierTextQuestion;
    private String dossierTextReponse;
    private String reponseId;
    private String indexPrinc;

    public DossierConnexeDTO() {
        super();
    }

    public DossierConnexeDTO(
        String dossierId,
        String sourceNumeroQuestion,
        String auteur,
        String minAttributaire,
        String motsCles,
        String etatQuestion
    ) {
        super(dossierId, sourceNumeroQuestion, auteur, minAttributaire, motsCles, etatQuestion);
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getDossierTextQuestion() {
        return dossierTextQuestion;
    }

    public void setDossierTextQuestion(String dossierTextQuestion) {
        this.dossierTextQuestion = dossierTextQuestion;
    }

    public String getDossierTextReponse() {
        return dossierTextReponse;
    }

    public void setDossierTextReponse(String dossierTextReponse) {
        this.dossierTextReponse = dossierTextReponse;
    }

    public String getReponseId() {
        return reponseId;
    }

    public void setReponseId(String reponseId) {
        this.reponseId = reponseId;
    }

    public String getIndexPrinc() {
        return indexPrinc;
    }

    public void setIndexPrinc(String indexPrinc) {
        this.indexPrinc = indexPrinc;
    }
}
