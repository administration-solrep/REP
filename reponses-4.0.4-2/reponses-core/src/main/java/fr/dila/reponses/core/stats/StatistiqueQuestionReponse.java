package fr.dila.reponses.core.stats;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "STATISTIQUE_QUESTION_REPONSE")
public class StatistiqueQuestionReponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "MINISTERE")
    private String ministere;

    @Column(name = "EDITION")
    private String edition;

    @Column(name = "ACTIF")
    private boolean actif;

    @Column(name = "ORDRE_PROTOCOLAIRE")
    private int ordreProtocolaire;

    @Column(name = "ORIGINE")
    private String origine;

    @Column(name = "NBQUESTION")
    private int nbQuestion;

    @Column(name = "NBREPONDU1MOIS")
    private int nbRepondu1Mois;

    @Column(name = "NBREPONDU2MOIS")
    private int nbRepondu2Mois;

    @Column(name = "NBREPONDUSUPERIEUR")
    private int nbReponduSuperieur;

    public StatistiqueQuestionReponse() {}

    public StatistiqueQuestionReponse(
        long id,
        String ministere,
        String edition,
        boolean actif,
        int ordreProtocolaire,
        String origine,
        int nbQuestion,
        int nbRepondu1Mois,
        int nbRepondu2Mois,
        int nbReponduSuperieur
    ) {
        super();
        this.id = id;
        this.ministere = ministere;
        this.edition = edition;
        this.actif = actif;
        this.ordreProtocolaire = ordreProtocolaire;
        this.origine = origine;
        this.nbQuestion = nbQuestion;
        this.nbRepondu1Mois = nbRepondu1Mois;
        this.nbRepondu2Mois = nbRepondu2Mois;
        this.nbReponduSuperieur = nbReponduSuperieur;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMinistere() {
        return ministere;
    }

    public void setMinistere(String ministere) {
        this.ministere = ministere;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public int getOrdreProtocolaire() {
        return ordreProtocolaire;
    }

    public void setOrdreProtocolaire(int ordreProtocolaire) {
        this.ordreProtocolaire = ordreProtocolaire;
    }

    public String getOrigine() {
        return origine;
    }

    public void setOrigine(String origine) {
        this.origine = origine;
    }

    public int getNbQuestion() {
        return nbQuestion;
    }

    public void setNbQuestion(int nbQuestion) {
        this.nbQuestion = nbQuestion;
    }

    public int getNbRepondu1Mois() {
        return nbRepondu1Mois;
    }

    public void setNbRepondu1Mois(int nbRepondu1Mois) {
        this.nbRepondu1Mois = nbRepondu1Mois;
    }

    public int getNbRepondu2Mois() {
        return nbRepondu2Mois;
    }

    public void setNbRepondu2Mois(int nbRepondu2Mois) {
        this.nbRepondu2Mois = nbRepondu2Mois;
    }

    public int getNbReponduSuperieur() {
        return nbReponduSuperieur;
    }

    public void setNbReponduSuperieur(int nbReponduSuperieur) {
        this.nbReponduSuperieur = nbReponduSuperieur;
    }

    @Override
    public String toString() {
        return (
            "StatistiqueQuestionReponse [id=" +
            id +
            ", ministere=" +
            ministere +
            ", actif=" +
            actif +
            ", ordreProtocolaire=" +
            ordreProtocolaire +
            ", origine=" +
            origine +
            ", nbQuestion=" +
            nbQuestion +
            ", nbRepondu1Mois=" +
            nbRepondu1Mois +
            ", nbRepondu2Mois=" +
            nbRepondu2Mois +
            ", nbReponduSuperieur=" +
            nbReponduSuperieur +
            "]"
        );
    }
}
