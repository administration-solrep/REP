package fr.dila.reponses.core.stats;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "STATISTIQUE_GROUPE")
public class StatistiqueGroupe implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "MINISTERE")
    private String ministere;

    @Column(name = "EDITION")
    private String edition;

    @Column(name = "GROUPE")
    private String groupe;

    @Column(name = "ORIGINE")
    private String origine;

    @Column(name = "ORDRE_PROTOCOLAIRE")
    private int ordreProtocolaire;

    @Column(name = "ACTIF")
    private boolean actif;

    @Column(name = "NBQUESTION")
    private int nbQuestion;

    public StatistiqueGroupe() {}

    public StatistiqueGroupe(
        long id,
        String ministere,
        String origine,
        String edition,
        String groupe,
        int ordreProtocolaire,
        boolean actif,
        int nbQuestion
    ) {
        this.id = id;
        this.ministere = ministere;
        this.edition = edition;
        this.origine = origine;
        this.groupe = groupe;
        this.ordreProtocolaire = ordreProtocolaire;
        this.actif = actif;
        this.nbQuestion = nbQuestion;
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

    public String getOrigine() {
        return origine;
    }

    public void setOrigine(String origine) {
        this.origine = origine;
    }

    public String getGroupe() {
        return groupe;
    }

    public void setGroupe(String groupe) {
        this.groupe = groupe;
    }

    public int getOrdreProtocolaire() {
        return ordreProtocolaire;
    }

    public void setOrdreProtocolaire(int ordreProtocolaire) {
        this.ordreProtocolaire = ordreProtocolaire;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public int getNbQuestion() {
        return nbQuestion;
    }

    public void setNbQuestion(int nbQuestion) {
        this.nbQuestion = nbQuestion;
    }

    @Override
    public String toString() {
        return (
            "StatistiqueGroupe [id=" +
            id +
            ", ministere=" +
            ministere +
            ", edition=" +
            edition +
            ", groupe=" +
            groupe +
            ", ordreProtocolaire=" +
            ordreProtocolaire +
            ", actif=" +
            actif +
            ", nbQuestion=" +
            nbQuestion +
            "]"
        );
    }
}
