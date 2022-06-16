package fr.dila.reponses.core.operation.distribution;

import fr.dila.cm.cases.CaseConstants;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;

/**
 * Opération permettant de passer un dossier done au statut running
 *
 */
@Operation(
    id = DossierDoneToRunningOperation.ID,
    category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY,
    label = "Dossier done to running",
    description = DossierDoneToRunningOperation.DESCRIPTION
)
public class DossierDoneToRunningOperation {
    /**
     * Identifiant technique de l'opération.
     */
    public static final String ID = "Reponses.Dossier.DoneToRunning";

    public static final String DESCRIPTION = "Cette opération passe un dossier du statut done à running";

    private static final STLogger LOGGER = STLogFactory.getLog(DossierDoneToRunningOperation.class);

    @Context
    protected OperationContext context;

    @Context
    protected CoreSession session;

    @Param(name = "id", required = true)
    protected String id;

    @OperationMethod
    public void doneToRunning() {
        LOGGER.info(ReponsesLogEnumImpl.INIT_OPERATION_DOSSIER_DONE_TO_RUNNING);

        LOGGER.info(ReponsesLogEnumImpl.PROCESS_OPERATION_DOSSIER_DONE_TO_RUNNING, "Running runStep : id = " + id);

        DocumentModel dossierDoc = session.getDocument(new IdRef(id));

        if (dossierDoc == null) {
            LOGGER.error(
                ReponsesLogEnumImpl.FAIL_OPERATION_DOSSIER_DONE_TO_RUNNING,
                "Argument id non valide, dossier non trouvé"
            );
        } else {
            //Passage à l'état running
            LOGGER.info(
                ReponsesLogEnumImpl.PROCESS_OPERATION_DOSSIER_DONE_TO_RUNNING,
                "changement du statut vers \"running\" "
            );
            Dossier dossier = dossierDoc.getAdapter(Dossier.class);
            String state = dossier.getDocument().getCurrentLifeCycleState();
            String resultat = "";
            if (state.equals(STDossier.DossierState.done.name())) {
                LOGGER.info(
                    ReponsesLogEnumImpl.PROCESS_OPERATION_DOSSIER_DONE_TO_RUNNING,
                    "dossier relancé : " + dossier.getQuestionId()
                );
                dossier.getDocument().followTransition(STDossier.DossierTransition.backToRunning.name());
                session.saveDocument(dossier.getDocument());
                resultat = "réussite";
            } else {
                LOGGER.error(
                    ReponsesLogEnumImpl.FAIL_OPERATION_DOSSIER_DONE_TO_RUNNING,
                    "Le dossier n'est pas à l'état done. Script inutile ici."
                );
                resultat = "échec";
            }

            LOGGER.info(
                ReponsesLogEnumImpl.PROCESS_OPERATION_DOSSIER_DONE_TO_RUNNING,
                "Fin de l'opération : " + resultat
            );
        }

        LOGGER.info(ReponsesLogEnumImpl.END_OPERATION_DOSSIER_DONE_TO_RUNNING);
    }
}
