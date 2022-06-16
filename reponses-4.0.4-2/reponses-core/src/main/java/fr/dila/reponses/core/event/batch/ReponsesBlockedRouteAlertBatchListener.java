package fr.dila.reponses.core.event.batch;

import fr.dila.reponses.core.export.RepDossierIdConfig;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.core.event.batch.AbstractBlockedRouteAlertBatchListener;
import fr.dila.st.api.logging.STLogEnum;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;
import java.util.List;
import javax.activation.DataSource;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Surcharge du batch des dossiers bloqués de solrep.
 *
 */
public class ReponsesBlockedRouteAlertBatchListener
    extends AbstractBlockedRouteAlertBatchListener
    implements StoppableBatch {
    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(ReponsesBlockedRouteAlertBatchListener.class);

    private static final String QUERY_DOSSIERS_BLOQUES =
        "SELECT id FROM dossier_reponse dos WHERE dos.id IN (SELECT id from misc where misc.lifecyclestate = 'running') " +
        " AND dos.lastdocumentroute IN (SELECT documentrouteid FROM routing_task r1 WHERE r1.id IN (SELECT id FROM misc WHERE misc.lifecyclestate = 'ready'))" +
        " AND dos.lastdocumentroute NOT IN (SELECT documentrouteid FROM routing_task r2 WHERE r2.id IN (SELECT id FROM misc where misc.lifecyclestate = 'running'))";

    public ReponsesBlockedRouteAlertBatchListener() {
        super();
    }

    @Override
    protected DataSource generateData(CoreSession session, List<String> idsDossiersDoc) {
        RepDossierIdConfig dossierIdConfig = new RepDossierIdConfig(idsDossiersDoc);
        return dossierIdConfig.getDataSource(session);
    }

    @Override
    protected String getQuery() {
        return QUERY_DOSSIERS_BLOQUES;
    }

    @Override
    public STLogEnum getStoppageCode() {
        return SSLogEnumImpl.CANCEL_B_DOSS_BLOQUES_TEC;
    }

    @Override
    public STLogger getStoppageLogger() {
        return LOGGER;
    }
}
