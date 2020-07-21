package fr.dila.reponses.core.stats;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="STATISTIQUE_DIRECTION")
public class StatistiqueDirection implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "MINISTERE")
    private String ministere;
    
    @Column(name = "EDITION")
    private String edition;
    
    @Column(name = "DIRECTION")
    private String direction;
    
    @Column(name = "ORDRE_PROTOCOLAIRE")
    private int ordreProtocolaire;
    
    @Column(name = "ACTIF")
    private boolean actif;
    
    @Column(name = "NBQUESTIONENCOURS")
    private int nbQuestionEnCours;
    
    public StatistiqueDirection() {
    }

    public StatistiqueDirection(long id, String direction, String ministere, String edition, int ordreProtocolaire, boolean actif, int nbQuestionEnCours) {
        super();
        this.id = id;
        this.direction = direction;
        this.ministere = ministere;
        this.edition = edition;
        this.ordreProtocolaire = ordreProtocolaire;
        this.actif = actif;
        this.nbQuestionEnCours = nbQuestionEnCours;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
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

    public int getNbQuestionEnCours() {
        return nbQuestionEnCours;
    }

    public void setNbQuestionEnCours(int nbQuestionEnCours) {
        this.nbQuestionEnCours = nbQuestionEnCours;
    }

    @Override
    public String toString() {
        return "StatistiqueDirection [id=" + id + ", ministere=" + ministere + ", edition=" + edition + ", ordreProtocolaire=" + ordreProtocolaire + ", actif=" + actif + ", nbQuestionEnCours=" + nbQuestionEnCours + "]";
    }
}
