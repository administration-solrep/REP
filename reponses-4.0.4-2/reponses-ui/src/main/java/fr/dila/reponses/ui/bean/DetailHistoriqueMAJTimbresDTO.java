package fr.dila.reponses.ui.bean;

import java.util.Calendar;

public class DetailHistoriqueMAJTimbresDTO {
    private String id;
    private String message;
    private String etat;
    private Calendar dateDebut;
    private Calendar dateFin;
    private String questionsAMigrer;
    private String questionsMigrees;
    private String details;

    public DetailHistoriqueMAJTimbresDTO() {
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

    public Calendar getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Calendar dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Calendar getDateFin() {
        return dateFin;
    }

    public void setDateFin(Calendar dateFin) {
        this.dateFin = dateFin;
    }

    public String getQuestionsAMigrer() {
        return questionsAMigrer;
    }

    public void setQuestionsAMigrer(String questionsAMigrer) {
        this.questionsAMigrer = questionsAMigrer;
    }

    public String getQuestionsMigrees() {
        return questionsMigrees;
    }

    public void setQuestionsMigrees(String questionsMigrees) {
        this.questionsMigrees = questionsMigrees;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
