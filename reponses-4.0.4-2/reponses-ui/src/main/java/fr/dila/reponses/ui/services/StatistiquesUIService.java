package fr.dila.reponses.ui.services;

import fr.dila.reponses.api.stat.report.ReportStats;
import fr.dila.ss.ui.services.SSStatistiquesUIService;
import fr.dila.st.ui.th.model.SpecificContext;
import org.nuxeo.ecm.core.api.Blob;

public interface StatistiquesUIService extends SSStatistiquesUIService {
    /**
     * Renvoie les statistiques permettant de générer le tableau de bord.
     */
    ReportStats getReportStats();

    Boolean hasRightToExport(SpecificContext context);

    void doZipExport(SpecificContext context);

    Blob getGeneratedExport(SpecificContext context);

    String getGeneratedExportName(SpecificContext context);

    void generateAllExcel(SpecificContext context);

    void generateAllPdf(SpecificContext context);

    void deleteOldExportedStats(SpecificContext context);

    boolean hasRightToViewSggStat(SpecificContext context);
}
