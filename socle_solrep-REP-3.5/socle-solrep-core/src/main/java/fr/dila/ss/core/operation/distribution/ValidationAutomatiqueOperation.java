package fr.dila.ss.core.operation.distribution;

import java.util.Collections;
import java.util.List;

import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.ecm.platform.routing.api.DocumentRouteStep;
import fr.dila.ecm.platform.routing.api.DocumentRoutingConstants;
import fr.dila.ss.api.service.DossierDistributionService;
import fr.dila.ss.api.service.SSFeuilleRouteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STOperationConstant;
import fr.dila.st.api.feuilleroute.STRouteStep;

/**
 * An operation that set the step to the done state.
 *
 * @author FEO
 */
@Operation(id = ValidationAutomatiqueOperation.ID, category = DocumentRoutingConstants.OPERATION_CATEGORY_ROUTING_NAME, label = "Validation automatique", description = "Valide un step")
public class ValidationAutomatiqueOperation {
    /**
     * Identifiant technique de l'opération.
     */
    public final static String ID = "ST.Distribution.ValidationAutomatique";

    @Context
    protected OperationContext context;

    /**
     * Default constructor
     */
    public ValidationAutomatiqueOperation(){
    	// do nothing
    }
    
    @OperationMethod
    public void setStepDone() throws ClientException {
        final CoreSession session = context.getCoreSession();

        SSFeuilleRouteService feuilleRouteService = SSServiceLocator.getSSFeuilleRouteService();
        DocumentRouteStep step = (DocumentRouteStep) context.get(DocumentRoutingConstants.OPERATION_STEP_DOCUMENT_KEY);
        @SuppressWarnings("unchecked")
        List<DocumentModel> dossierDocList = (List<DocumentModel>) context.get(STOperationConstant.OPERATION_CASES_KEY);
        STRouteStep routeStep = step.getDocument().getAdapter(STRouteStep.class);
        
        //exécute des traitements spécifiques à l'étape sur laquelle on arrive
        feuilleRouteService.doValidationAutomatiqueOperation(session, routeStep, dossierDocList);
        
        if (!feuilleRouteService.canDistributeStep(session, routeStep, dossierDocList)) {
            routeStep.setAutomaticValidated(false);
            session.saveDocument(routeStep.getDocument());
            session.save();
            step.setDone(session);
            
            // Notifie la fin de l'exécution d'une étape (cas où le dossier n'est pas distribué)            
            afterDossierDistributedProcess(session, routeStep.getDistributionMailboxId(), dossierDocList.get(0));            
        }
    }
    
    private void afterDossierDistributedProcess(final CoreSession session,  String mailboxId, DocumentModel dossierDoc) throws ClientException {
        // Traite uniquement les évènements de distribution de dossier
        // Initialise les droits du dossier        
        final List<String> mailboxIdList = Collections.singletonList(mailboxId);
        final DossierDistributionService dossierDistributionService = SSServiceLocator.getDossierDistributionService();
        dossierDistributionService.initDossierDistributionAcl(session, dossierDoc, mailboxIdList);
     }
}
