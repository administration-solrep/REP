package fr.dila.reponses.ui.bean;

public class DetailMigrationHistoriqueMAJTimbresDTO {
    private String id;
    private String message;
    private String etat;
    private String details;

    public DetailMigrationHistoriqueMAJTimbresDTO() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
