package fr.dila.reponses.ui.bean;

public class AllotissementDTO {
    private String dossierId;
    private String sourceNumeroQuestion;
    private String auteur;
    private String minAttributaire;
    private String motsCles;
    private String etatQuestion;
    private String erreurAllotissement;
    private boolean directeur;

    public AllotissementDTO() {
        super();
    }

    public AllotissementDTO(
        String dossierId,
        String sourceNumeroQuestion,
        String auteur,
        String minAttributaire,
        String motsCles,
        String etatQuestion
    ) {
        super();
        this.dossierId = dossierId;
        this.sourceNumeroQuestion = sourceNumeroQuestion;
        this.auteur = auteur;
        this.minAttributaire = minAttributaire;
        this.motsCles = motsCles;
        this.etatQuestion = etatQuestion;
    }

    public String getDossierId() {
        return dossierId;
    }

    public void setDossierId(String questionId) {
        this.dossierId = questionId;
    }

    public String getSourceNumeroQuestion() {
        return sourceNumeroQuestion;
    }

    public void setSourceNumeroQuestion(String sourceNumeroQuestion) {
        this.sourceNumeroQuestion = sourceNumeroQuestion;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getMinAttributaire() {
        return minAttributaire;
    }

    public void setMinAttributaire(String minAttributaire) {
        this.minAttributaire = minAttributaire;
    }

    public String getMotsCles() {
        return motsCles;
    }

    public void setMotsCles(String motsCles) {
        this.motsCles = motsCles;
    }

    public String getErreurAllotissement() {
        return erreurAllotissement;
    }

    public void setErreurAllotissement(String erreurAllotissement) {
        this.erreurAllotissement = erreurAllotissement;
    }

    public String getEtatQuestion() {
        return etatQuestion;
    }

    public void setEtatQuestion(String etatQuestion) {
        this.etatQuestion = etatQuestion;
    }

    public boolean isDirecteur() {
        return directeur;
    }

    public void setDirecteur(boolean directeur) {
        this.directeur = directeur;
    }
}
