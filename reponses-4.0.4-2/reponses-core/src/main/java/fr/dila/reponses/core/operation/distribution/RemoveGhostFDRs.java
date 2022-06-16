package fr.dila.reponses.core.operation.distribution;

import static fr.sword.naiad.nuxeo.ufnxql.core.query.FlexibleQueryMaker.COL_ID;
import static java.util.stream.Collectors.toList;

import fr.dila.cm.cases.CaseConstants;
import fr.dila.reponses.core.work.CancelFDRWork;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.operation.STVersion;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.util.List;
import java.util.stream.StreamSupport;
import org.apache.commons.collections4.ListUtils;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.work.api.WorkManager;

/**
 * Suppression des feuilles de route à l'état running n'ayant pas de dossiers
 * attachés
 * <p>
 * Lors d'un désallotissement, la feuille de route directrice était détachée des
 * dossiers mais n'était pas annulée, ce qui laisser une feuille de route à
 * 'fantôme'
 *
 * @author SCE
 */
@Operation(
    id = RemoveGhostFDRs.ID,
    category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY,
    description = "Suppression des feuilles de route à l'état running n'ayant pas de dossiers attachés"
)
@STVersion(version = "4.0.2")
public class RemoveGhostFDRs {
    public static final String ID = "Remove.Ghost.FDR";

    private static final STLogger LOG = STLogFactory.getLog(RemoveGhostFDRs.class);

    private static final int BUCKET_SIZE = 60;

    @Context
    private NuxeoPrincipal principal;

    @Context
    private CoreSession session;

    public RemoveGhostFDRs() {
        super();
    }

    @OperationMethod
    public void run() {
        if (!principal.isAdministrator()) {
            return;
        }

        List<String> routeIds;

        String queryGhostFDRQuery =
            "Select h.ID FROM HIERARCHY h, MISC m, DUBLINCORE d WHERE PRIMARYTYPE = 'FeuilleRoute' AND h.ID = m.ID AND h.ID = d.ID AND m.ID = d.ID AND m.LIFECYCLESTATE = 'running' AND h.ID NOT IN(Select ID FROM FROUTINST_ATTACHDOCUMENTIDS)";
        try (
            IterableQueryResult res = QueryUtils.doSqlQuery(
                session,
                new String[] { COL_ID },
                queryGhostFDRQuery,
                new Object[] {}
            )
        ) {
            routeIds =
                StreamSupport.stream(res.spliterator(), false).map(row -> (String) row.get(COL_ID)).collect(toList());
        }

        LOG.info(
            STLogEnumImpl.DEFAULT,
            String.format(
                "%d feuilles de route à l'état running, sans dossiers attachés, seront annulées%n%s",
                routeIds.size(),
                routeIds
            )
        );
        WorkManager workManager = ServiceUtil.getRequiredService(WorkManager.class);
        ListUtils.partition(routeIds, BUCKET_SIZE).stream().map(CancelFDRWork::new).forEach(workManager::schedule);
    }
}
