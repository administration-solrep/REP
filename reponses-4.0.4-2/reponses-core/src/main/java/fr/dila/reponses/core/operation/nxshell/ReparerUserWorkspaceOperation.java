package fr.dila.reponses.core.operation.nxshell;

import fr.dila.cm.cases.CaseConstants;
import fr.dila.st.api.constant.STAlertConstant;
import fr.dila.st.api.service.SecurityService;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.SecurityConstants;

/**
 * Une opération pour réparer les droits sur les workspaces utilisateurs.
 *
 * @author bgamard
 */
@Operation(
    id = ReparerUserWorkspaceOperation.ID,
    category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY,
    label = "ReparerUserWorkspace",
    description = "Répare les droits sur les workspaces utilisateurs"
)
public class ReparerUserWorkspaceOperation {
    /**
     * Identifiant technique de l'opération.
     */
    public static final String ID = "Reponses.Reparer.UserWorkspace";

    /**
     * Logger.
     */
    private static final Log log = LogFactory.getLog(ReparerUserWorkspaceOperation.class);

    @Context
    protected CoreSession session;

    @OperationMethod
    public void run() {
        log.info("Début opération " + ID);
        final String query = "SELECT w.ecm:uuid as id FROM Workspace as w";

        log.info("Comptage de tous les workspaces");
        final Long count = QueryUtils.doCountQuery(session, QueryUtils.ufnxqlToFnxqlQuery(query), null);
        log.info(count + " workspaces");

        final long limit = 50;
        for (Long offset = (long) 0; offset <= count; offset += limit) {
            final Long borne = count < offset + limit ? count : offset + limit;
            log.info("Début traitement workspace de " + offset + " à " + borne);

            final List<DocumentModel> workspacesDoc = QueryUtils.doUFNXQLQueryAndFetchForDocuments(
                session,
                "Workspace",
                query,
                null,
                limit,
                0
            );

            for (final DocumentModel workspaceDoc : workspacesDoc) {
                if (workspaceDoc.getPathAsString().startsWith("/case-management/UserWorkspaces")) {
                    final String username = DublincoreSchemaUtils.getCreator(workspaceDoc);
                    for (final DocumentModel childDoc : session.getChildren(workspaceDoc.getRef())) {
                        if (
                            childDoc.getType().equals("SmartFolder") ||
                            childDoc.getType().equals(STAlertConstant.ALERT_DOCUMENT_TYPE)
                        ) {
                            if (childDoc.getACP().getACL(ACL.LOCAL_ACL) == null) {
                                final SecurityService service = STServiceLocator.getSecurityService();
                                service.addAceToAcl(childDoc, ACL.LOCAL_ACL, username, SecurityConstants.EVERYTHING);
                            }
                        }
                    }
                }
            }
            log.info("Fin traitement workspace de " + offset + " à " + borne);
        }
        log.info("Fin de l'opération" + ID);
        return;
    }
}
