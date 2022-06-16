package fr.dila.reponses.core.operation.distribution;

import fr.dila.cm.cases.CaseConstants;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;

/**
 * Opération appellée à la fin de la chaine de création de CaseLink.
 *
 * @author bgamard
 */
@Operation(
    id = CaseLinkCreatedOperation.ID,
    category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY,
    label = "Case Link created",
    description = "Fire event when CaseLink is created."
)
public class CaseLinkCreatedOperation {
    /**
     * Identifiant technique de l'opération.
     */
    public static final String ID = "Reponses.Distribution.CaseLinkCreated";

    @Context
    protected OperationContext context;

    @OperationMethod
    public void caseLinkCreated() {}
}
