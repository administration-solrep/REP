package fr.dila.reponses.ui.bean;

public class MiseAJourTimbresRecapitulatifDTO {
    private String id;
    private String ancienMinistere;
    private String nouveauMinistere;
    private String migrerDossierClos;
    private String briserSignature;
    private Long nbQuestionsCloses;
    private Long nbQuestionsOuvertes;
    private Long nbQuestionsEnAttente;
    private Long nbModeleFDR;

    public MiseAJourTimbresRecapitulatifDTO() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAncienMinistere() {
        return ancienMinistere;
    }

    public void setAncienMinistere(String ancienMinistere) {
        this.ancienMinistere = ancienMinistere;
    }

    public String getNouveauMinistere() {
        return nouveauMinistere;
    }

    public void setNouveauMinistere(String nouveauMinistere) {
        this.nouveauMinistere = nouveauMinistere;
    }

    public String getMigrerDossierClos() {
        return migrerDossierClos;
    }

    public void setMigrerDossierClos(String migrerDossierClos) {
        this.migrerDossierClos = migrerDossierClos;
    }

    public String getBriserSignature() {
        return briserSignature;
    }

    public void setBriserSignature(String briserSignature) {
        this.briserSignature = briserSignature;
    }

    public Long getNbQuestionsCloses() {
        return nbQuestionsCloses;
    }

    public void setNbQuestionsCloses(Long nbQuestionsCloses) {
        this.nbQuestionsCloses = nbQuestionsCloses;
    }

    public Long getNbQuestionsOuvertes() {
        return nbQuestionsOuvertes;
    }

    public void setNbQuestionsOuvertes(Long nbQuestionsOuvertes) {
        this.nbQuestionsOuvertes = nbQuestionsOuvertes;
    }

    public Long getNbQuestionsEnAttente() {
        return nbQuestionsEnAttente;
    }

    public void setNbQuestionsEnAttente(Long nbQuestionsEnAttente) {
        this.nbQuestionsEnAttente = nbQuestionsEnAttente;
    }

    public Long getNbModeleFDR() {
        return nbModeleFDR;
    }

    public void setNbModeleFDR(Long nbModeleFDR) {
        this.nbModeleFDR = nbModeleFDR;
    }
}
