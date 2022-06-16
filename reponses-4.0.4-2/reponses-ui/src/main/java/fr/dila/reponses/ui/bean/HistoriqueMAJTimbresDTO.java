package fr.dila.reponses.ui.bean;

import java.util.Calendar;

public class HistoriqueMAJTimbresDTO {
    private String id;
    private String message;
    private String etat;
    private Calendar dateDebut;
    private Calendar dateFin;
    private Long questionsEnCoursAMigrer;
    private Long questionsEnCoursMigrees;
    private Long questionsClosesAMigrer;
    private Long questionsClosesMigrees;

    public HistoriqueMAJTimbresDTO() {
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

    public Long getQuestionsEnCoursAMigrer() {
        return questionsEnCoursAMigrer;
    }

    public void setQuestionsEnCoursAMigrer(Long questionsEnCoursAMigrer) {
        this.questionsEnCoursAMigrer = questionsEnCoursAMigrer;
    }

    public Long getQuestionsEnCoursMigrees() {
        return questionsEnCoursMigrees;
    }

    public void setQuestionsEnCoursMigrees(Long questionsEnCoursMigrees) {
        this.questionsEnCoursMigrees = questionsEnCoursMigrees;
    }

    public Long getQuestionsClosesAMigrer() {
        return questionsClosesAMigrer;
    }

    public void setQuestionsClosesAMigrer(Long questionsClosesAMigrer) {
        this.questionsClosesAMigrer = questionsClosesAMigrer;
    }

    public Long getQuestionsClosesMigrees() {
        return questionsClosesMigrees;
    }

    public void setQuestionsClosesMigrees(Long questionsClosesMigrees) {
        this.questionsClosesMigrees = questionsClosesMigrees;
    }
}
