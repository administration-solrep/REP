package fr.dila.reponses.core.operation.distribution;

import fr.dila.cm.cases.CaseConstants;
import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.st.api.caselink.STDossierLink;
import java.util.List;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Opération permettant de supprimer les anciens DossierLink des Mailbox, lors de la validation d'une étape de feuille de route.
 *
 * @author jgomez
 * @author jtremeaux
 */
@Operation(
    id = UpdateDossierStateOperation.ID,
    category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY,
    label = "Met à jour l'état de la question après une validation",
    description = UpdateDossierStateOperation.DESCRIPTION
)
public class UpdateDossierStateOperation {
    /**
     * Identifiant technique de l'opération.
     */
    public static final String ID = "Reponses.Distribution.UpdateDossierState";

    public static final String DESCRIPTION = "Cette opération met à jour l'état d'un dossier";

    @Context
    protected OperationContext context;

    @Context
    protected CoreSession session;

    /**
     * Met à jour l'état du dossier avant en début d'étape (flags sur les étapes atteintes).
     *
     */
    @OperationMethod
    public void updateDossierState() {
        @SuppressWarnings("unchecked")
        final List<STDossierLink> caseLinks = (List<STDossierLink>) context.get(CaseConstants.OPERATION_CASE_LINKS_KEY);
        for (final STDossierLink link : caseLinks) {
            final DocumentModel dossierLinkDoc = link.getDocument();
            final DossierLink dossierLink = dossierLinkDoc.getAdapter(DossierLink.class);
            final String taskType = dossierLink.getRoutingTaskType();

            final Dossier dossier = dossierLink.getDossier(session);
            if (dossier != null) {
                if (VocabularyConstants.ROUTING_TASK_TYPE_SIGNATURE.equals(taskType)) {
                    dossier.setEtapeSignatureAtteinte(true);
                } else if (VocabularyConstants.ROUTING_TASK_TYPE_REDACTION.equals(taskType)) {
                    dossier.setEtapeRedactionAtteinte(true);
                } else if (VocabularyConstants.ROUTING_TASK_TYPE_REATTRIBUTION.equals(taskType)) {
                    // Après reflexion, je laisse la ligne ici, si on bouge celle-ci,
                    // pourquoi pas les 2 autres ?
                    dossier.setReaffectionCount(dossier.getReaffectionCount() + 1);
                    // On positionne également le flag reattribue sur la question
                    final Question question = dossier.getQuestion(session);
                    if (dossier.getReaffectionCount() > 0 && question != null && !question.getIsReattribue()) {
                        question.setIsReattribue(true);
                        session.saveDocument(question.getDocument());
                    }
                }
                dossier.save(session);
            }
        }

        session.save();
    }
}
