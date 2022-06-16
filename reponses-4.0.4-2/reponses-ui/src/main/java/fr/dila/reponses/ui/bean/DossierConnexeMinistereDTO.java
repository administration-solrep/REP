package fr.dila.reponses.ui.bean;

public class DossierConnexeMinistereDTO {
    private String ministereId;
    private String ministereLabel;
    private int nbDossiers;

    public DossierConnexeMinistereDTO() {
        super();
    }

    public DossierConnexeMinistereDTO(String ministereId, String ministereLabel, int nbDossiers) {
        super();
        this.ministereId = ministereId;
        this.ministereLabel = ministereLabel;
        this.nbDossiers = nbDossiers;
    }

    public String getMinistereId() {
        return ministereId;
    }

    public void setMinistereId(String ministereId) {
        this.ministereId = ministereId;
    }

    public String getMinistereLabel() {
        return ministereLabel;
    }

    public void setMinistereLabel(String ministereLabel) {
        this.ministereLabel = ministereLabel;
    }

    public int getNbDossiers() {
        return nbDossiers;
    }

    public void setNbDossiers(int nbDossiers) {
        this.nbDossiers = nbDossiers;
    }
}
