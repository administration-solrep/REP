package fr.dila.reponses.api.service;

import fr.dila.reponses.api.stat.report.ReportStats;
import fr.dila.st.api.event.batch.BatchLoggerModel;
import java.io.Serializable;
import org.nuxeo.ecm.core.api.CoreSession;

public interface StatsService extends Serializable {
    /**
     * Calcul et rempli les tables de statistiques.
     */
    public void computeStats(final CoreSession session, final BatchLoggerModel batchLoggerModel);

    /**
     * Dénormalise les directions sur les étapes de feuille de route passées.
     *
     */
    public void denormaliserDirection(CoreSession session);

    /**
     * Dénormalise les labels sur les étapes suivante de feuille de route pour les
     * dossiers.
     *
     */
    public void denormaliserEtapeSuivante(CoreSession session);

    /**
     * Dénormalise les ministeres sur les étapes de feuille de route
     *
     * @param session
     *
     */
    void denormaliserMinistere(CoreSession session);

    /**
     * Génération des statistiques du rapport quotidien (pour affichage hors Birt)
     */
    void generateReportStats(CoreSession session);

    /**
     * Récupération des statitiques du rapport quotidien.
     *
     * @return un objet ReportStats
     */
    ReportStats getDailyReportStats();
}
