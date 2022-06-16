package fr.dila.reponses.core.stats;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "STATISTIQUE_MINISTERE")
public class StatistiqueMinistere implements Serializable {
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

    @Column(name = "NBRENOUVELLE")
    private int nbRenouvelle;

    @Column(name = "NBRENOUVELLEEC")
    private int nbRenouvelleEC;

    @Column(name = "NBRETIRE")
    private int nbRetire;

    @Column(name = "NBQUESTIONSSREPONSE")
    private int nbQuestionSsReponse;

    @Column(name = "NBQUESTIONSSREPONSESUP2MOIS")
    private int nbQuestionSsReponseSup2Mois;

    public StatistiqueMinistere() {}

    public StatistiqueMinistere(
        long id,
        String ministere,
        String edition,
        boolean actif,
        int ordreProtocolaire,
        int nbRenouvelle,
        int nbRenouvelleEC,
        int nbRetire,
        int nbQuestionSsReponse,
        int nbQuestionSsReponseSup2Mois
    ) {
        super();
        this.id = id;
        this.ministere = ministere;
        this.edition = edition;
        this.actif = actif;
        this.ordreProtocolaire = ordreProtocolaire;
        this.nbRenouvelle = nbRenouvelle;
        this.nbRetire = nbRetire;
        this.nbQuestionSsReponse = nbQuestionSsReponse;
        this.nbQuestionSsReponseSup2Mois = nbQuestionSsReponseSup2Mois;
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

    public int getNbRenouvelle() {
        return nbRenouvelle;
    }

    public void setNbRenouvelle(int nbRenouvelle) {
        this.nbRenouvelle = nbRenouvelle;
    }

    public int getNbRenouvelleEC() {
        return nbRenouvelleEC;
    }

    public void setNbRenouvelleEC(int nbRenouvelleEC) {
        this.nbRenouvelleEC = nbRenouvelleEC;
    }

    public int getNbRetire() {
        return nbRetire;
    }

    public void setNbRetire(int nbRetire) {
        this.nbRetire = nbRetire;
    }

    public int getNbQuestionSsReponse() {
        return nbQuestionSsReponse;
    }

    public void setNbQuestionSsReponse(int nbQuestionSsReponse) {
        this.nbQuestionSsReponse = nbQuestionSsReponse;
    }

    public int getNbQuestionSsReponseSup2Mois() {
        return nbQuestionSsReponseSup2Mois;
    }

    public void setNbQuestionSsReponseSup2Mois(int nbQuestionSsReponseSup2Mois) {
        this.nbQuestionSsReponseSup2Mois = nbQuestionSsReponseSup2Mois;
    }

    @Override
    public String toString() {
        return (
            "StatistiqueMinistere [id=" +
            id +
            ", ministere=" +
            ministere +
            ", actif=" +
            actif +
            ", ordreProtocolaire=" +
            ordreProtocolaire +
            ", nbRenouvelle=" +
            nbRenouvelle +
            ", nbRetire=" +
            nbRetire +
            ", nbQuestionSsReponse=" +
            nbQuestionSsReponse +
            ", nbQuestionSsReponseSup2Mois=" +
            nbQuestionSsReponseSup2Mois +
            "]"
        );
    }
}
