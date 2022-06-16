package fr.dila.reponses.core.event.batch;

import fr.dila.reponses.core.export.RepDossierIdConfig;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.core.event.batch.AbstractDossierLinkIncoherentBatchListener;
import fr.dila.st.api.logging.STLogEnum;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;
import java.util.ArrayList;
import java.util.List;
import javax.activation.DataSource;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Batch détection dossier link incohérent
 *
 */
public class DossierLinkIncoherentBatchListener
    extends AbstractDossierLinkIncoherentBatchListener
    implements StoppableBatch {
    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(DossierLinkIncoherentBatchListener.class);

    /**
     * Requête dossier link en todo mais étape état diff de running
     */
    private static final String STEP_NOT_RUNNING_QUERY =
        "select id from dossier_reponse where lastdocumentroute in (select documentrouteid from routing_task " +
        " where id in (select routingtaskid from dossier_reponses_link where id in (select id from misc where lifecyclestate = 'todo')) " +
        " and id not in (select id from misc where lifecyclestate ='running'))";

    /**
     * Requête étape en running mais pas de dossier link à l'état todo
     */
    private static final String DOSSIER_LINK_MISSING_QUERY =
        "select id from dossier_reponse where lastdocumentroute in (select documentrouteid from routing_task " +
        "	where id not in (select routingtaskid from dossier_reponses_link where id in (select id from misc where lifecyclestate = 'todo')) " +
        "	and id in (select id from misc where lifecyclestate ='running'))";

    /**
     * Requête des dossiers dont la feuille de route est terminée, mais pour
     * lesquels des dossiers link existent
     */
    private static final String DOSSIER_LINK_EXISTING_QUERY =
        "select id from dossier_reponse where lastdocumentroute in (select id from misc where lifecyclestate ='done') " +
        " and id in (select caseDocumentId from case_link where id in (select id from misc where lifecyclestate = 'todo'))";

    /**
     * Default constructor
     */
    public DossierLinkIncoherentBatchListener() {
        super();
    }

    @Override
    protected DataSource generateData(CoreSession session, List<String> dossiersIds) {
        RepDossierIdConfig dossierIdConfig = new RepDossierIdConfig(dossiersIds);
        return dossierIdConfig.getDataSource(session);
    }

    @Override
    protected List<String> getQueries() {
        List<String> queries = new ArrayList<>();
        queries.add(STEP_NOT_RUNNING_QUERY);
        queries.add(DOSSIER_LINK_MISSING_QUERY);
        queries.add(DOSSIER_LINK_EXISTING_QUERY);
        return queries;
    }

    @Override
    public STLogEnum getStoppageCode() {
        return SSLogEnumImpl.CANCEL_B_DL_INCOHERENT_TEC;
    }

    @Override
    public STLogger getStoppageLogger() {
        return LOGGER;
    }
}
