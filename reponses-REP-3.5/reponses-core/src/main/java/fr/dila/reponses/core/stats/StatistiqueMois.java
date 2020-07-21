package fr.dila.reponses.core.stats;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="STATISTIQUE_MOIS")
public class StatistiqueMois implements Serializable {
    
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
    
    @Column(name = "MOIS")
    private String mois;
    
    @Column(name = "NBQUESTION")
    private int nbQuestion;
    
    @Column(name = "NBREPONSE")
    private int nbReponse;
    
    @Column(name = "NBREPONSETOTAL")
    private int nbReponseTotal;
    
    public StatistiqueMois() {
    }

    public StatistiqueMois(long id, String ministere, String edition, boolean actif, int ordreProtocolaire, String mois, int nbQuestion, int nbReponse, int nbReponseTotal) {
        super();
        this.id = id;
        this.ministere = ministere;
        this.edition = edition;
        this.actif = actif;
        this.ordreProtocolaire = ordreProtocolaire;
        this.mois = mois;
        this.nbQuestion = nbQuestion;
        this.nbReponse = nbReponse;
        this.nbReponseTotal = nbReponseTotal;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }
    
    public String getMinistere() {
        return ministere;
    }

    public void setMinistere(String ministere) {
        this.ministere = ministere;
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

    public String getMois() {
        return mois;
    }

    public void setMois(String mois) {
        this.mois = mois;
    }

    public int getNbQuestion() {
        return nbQuestion;
    }

    public void setNbQuestion(int nbQuestion) {
        this.nbQuestion = nbQuestion;
    }

    public int getNbReponse() {
        return nbReponse;
    }

    public void setNbReponse(int nbReponse) {
        this.nbReponse = nbReponse;
    }
    
    public int getNbReponseTotal() {
        return nbReponseTotal;
    }

    public void setNbReponseTotal(int nbReponseTotal) {
        this.nbReponseTotal = nbReponseTotal;
    }

    @Override
    public String toString() {
        return "StatistiqueMois [id=" + id + ", ministere=" + ministere + ", actif=" + actif + ", ordreProtocolaire=" + ordreProtocolaire + ", mois=" + mois + ", nbQuestion=" + nbQuestion + ", nbReponse=" + nbReponse + ", nbReponseTotal=" + nbReponseTotal + "]";
    }
}
