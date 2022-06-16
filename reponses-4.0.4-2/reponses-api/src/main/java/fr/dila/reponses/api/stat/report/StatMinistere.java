package fr.dila.reponses.api.stat.report;

import java.io.Serializable;

/** Une statistique sur un ministère */
public class StatMinistere implements Serializable {
    private static final long serialVersionUID = -6700858382039389139L;

    private String ministere;
    /** Taux (pourcentage arrondi à l'unité) affichable */
    private String taux2mois;
    private Long nbQuestions;

    public StatMinistere() {
        super();
    }

    public String getMinistere() {
        return ministere;
    }

    public void setMinistere(String ministere) {
        this.ministere = ministere;
    }

    public String getTaux2mois() {
        return taux2mois;
    }

    public void setTaux2mois(String taux2mois) {
        this.taux2mois = taux2mois;
    }

    public Long getNbQuestions() {
        return nbQuestions;
    }

    public void setNbQuestions(Long nbQuestions) {
        this.nbQuestions = nbQuestions;
    }
}
