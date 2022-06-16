package fr.dila.reponses.api.stat.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Les statistiques du rapport à afficher en page de login.
 *
 * @author tlombard
 */
public class ReportStats implements Serializable {
    private static final long serialVersionUID = -8333058419016271342L;

    /** Stats des ministères, classés par ordre protocolaire. */
    private List<StatMinistere> ministereStats = new ArrayList<>();

    /** Statistiques globales sur l'ensemble du gvt */
    private StatGlobale globalStats = new StatGlobale();

    public List<StatMinistere> getMinistereStats() {
        return ministereStats;
    }

    public void setMinistereStats(List<StatMinistere> ministereStats) {
        this.ministereStats = ministereStats;
    }

    public StatGlobale getGlobalStats() {
        return globalStats;
    }

    public void setGlobalStats(StatGlobale globalStats) {
        this.globalStats = globalStats;
    }
}
