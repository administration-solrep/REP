package fr.dila.reponses.api.extraction;

import javax.xml.bind.annotation.XmlType;

@XmlType(
    propOrder = {
        "id_question",
        "legislature",
        "date_publication_jo",
        "page_jo",
        "statut",
        "auteur",
        "id_ministre_depot",
        "id_ministre_attributaire",
        "erratum_question",
        "erratum_reponse",
        "rappel",
        "date_renouvellement_question",
        "date_signalement_question",
        "date_abandon_question"
    }
)
public class QuestionJAXB {
    private int id_question;
    private int legislature;
    private String date_publication_jo;
    private String page_jo;
    private String statut;
    private Auteur auteur;
    private int id_ministre_depot;
    private int id_ministre_attributaire;
    private Rappel rappel;
    private String date_renouvellement_question;
    private String date_signalement_question;
    private String date_abandon_question;
    private Boolean erratum_question;
    private Boolean erratum_reponse;

    public QuestionJAXB() {
        super();
    }

    public QuestionJAXB(
        int id_question,
        int legislature,
        String date_publication_jo,
        String page_jo,
        String statut,
        Auteur auteur,
        int id_ministre_depot,
        int id_ministre_attributaire,
        Rappel rappel,
        boolean erratum_question,
        boolean erratum_reponse,
        String date_renouvellement_question,
        String date_signalement_question,
        String date_cloture_question,
        String date_publication_reponse
    ) {
        super();
        this.id_question = id_question;
        this.legislature = legislature;
        this.date_publication_jo = date_publication_jo;
        this.page_jo = page_jo;
        this.statut = statut;
        this.auteur = auteur;
        this.id_ministre_depot = id_ministre_depot;
        this.id_ministre_attributaire = id_ministre_attributaire;
        this.erratum_question = erratum_question;
        this.erratum_reponse = erratum_reponse;
        this.rappel = rappel;
        this.date_renouvellement_question = date_renouvellement_question;
        this.date_signalement_question = date_signalement_question;
        this.date_abandon_question = date_cloture_question;
    }

    public int getId_question() {
        return id_question;
    }

    public void setId_question(int id_question) {
        this.id_question = id_question;
    }

    public int getLegislature() {
        return legislature;
    }

    public void setLegislature(int legislature) {
        this.legislature = legislature;
    }

    public String getDate_publication_jo() {
        return date_publication_jo;
    }

    public void setDate_publication_jo(String date_publication_jo) {
        this.date_publication_jo = date_publication_jo;
    }

    public String getPage_jo() {
        return page_jo;
    }

    public void setPage_jo(String page_jo) {
        this.page_jo = page_jo;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Auteur getAuteur() {
        return auteur;
    }

    public void setAuteur(Auteur auteur) {
        this.auteur = auteur;
    }

    public int getId_ministre_depot() {
        return id_ministre_depot;
    }

    public void setId_ministre_depot(int id_ministre_depot) {
        this.id_ministre_depot = id_ministre_depot;
    }

    public int getId_ministre_attributaire() {
        return id_ministre_attributaire;
    }

    public void setId_ministre_attributaire(int id_ministre_attributaire) {
        this.id_ministre_attributaire = id_ministre_attributaire;
    }

    public Boolean getErratum_question() {
        return this.erratum_question;
    }

    public void setErratum_question(Boolean erratum_question) {
        this.erratum_question = erratum_question;
    }

    public Boolean getErratum_reponse() {
        return this.erratum_reponse;
    }

    public void setErratum_reponse(Boolean erratum_reponse) {
        this.erratum_reponse = erratum_reponse;
    }

    public Rappel getRappel() {
        return rappel;
    }

    public void setRappel(Rappel rappel) {
        this.rappel = rappel;
    }

    public String getDate_renouvellement_question() {
        return date_renouvellement_question;
    }

    public void setDate_renouvellement_question(String date_renouvellement_question) {
        this.date_renouvellement_question = date_renouvellement_question;
    }

    public String getDate_signalement_question() {
        return date_signalement_question;
    }

    public void setDate_signalement_question(String date_signalement_question) {
        this.date_signalement_question = date_signalement_question;
    }

    public String getDate_abandon_question() {
        return date_abandon_question;
    }

    public void setDate_abandon_question(String date_abandon_question) {
        this.date_abandon_question = date_abandon_question;
    }
}
