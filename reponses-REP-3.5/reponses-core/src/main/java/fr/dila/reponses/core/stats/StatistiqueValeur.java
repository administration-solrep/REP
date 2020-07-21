package fr.dila.reponses.core.stats;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="STATISTIQUE_VALEUR")
public class StatistiqueValeur implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    @Column(name="IDRAPPORT")
    private String idRapport;
    
    @Column(name="LIBELLE")
    private String label;
    
    @Column(name="VALEUR")
    private String valeur;
    
    @Column(name="REQUETE")
    private String requete;
    
    public StatistiqueValeur() {
    }

    public StatistiqueValeur(long id, String idRapport, String label, String valeur, String requete) {
        super();
        this.id = id;
        this.idRapport = idRapport;
        this.label = label;
        this.valeur = valeur;
        this.requete = requete;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIdRapport() {
        return idRapport;
    }

    public void setIdRapport(String idRapport) {
        this.idRapport = idRapport;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValeur() {
        return valeur;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }

    public String getRequete() {
        return requete;
    }

    public void setRequete(String requete) {
        this.requete = requete;
    }
    
    
}
