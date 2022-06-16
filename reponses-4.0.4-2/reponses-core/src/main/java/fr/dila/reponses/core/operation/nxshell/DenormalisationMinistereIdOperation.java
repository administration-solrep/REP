package fr.dila.reponses.core.operation.nxshell;

import fr.dila.cm.cases.CaseConstants;
import fr.dila.reponses.api.service.StatsService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Une opération pour dénormaliser les id des ministères des étapes de feuille de route
 *
 */
@Operation(
    id = DenormalisationMinistereIdOperation.ID,
    category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY,
    label = "DenormaliserMinistereId",
    description = "Dénormalise les ids des ministères sur les étapes de feuille de route"
)
public class DenormalisationMinistereIdOperation {
    /**
     * Identifiant technique de l'opération.
     */
    public static final String ID = "Reponses.Denormaliser.MinistereId";

    /**
     * Logger.
     */
    private static final Log log = LogFactory.getLog(DenormalisationMinistereIdOperation.class);

    @Context
    protected CoreSession session;

    @OperationMethod
    public void run() {
        log.info("Début opération " + ID);

        final StatsService statsService = ReponsesServiceLocator.getStatsService();
        statsService.denormaliserMinistere(session);

        log.info("Fin de l'opération");
        return;
    }
}
