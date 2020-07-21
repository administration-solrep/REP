package fr.dila.reponses.core.operation.distribution;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

import fr.dila.cm.caselink.ActionableCaseLink;
import fr.dila.cm.caselink.CaseLink;
import fr.dila.cm.cases.CaseConstants;
import fr.dila.ecm.platform.routing.api.DocumentRouteStep;
import fr.dila.ecm.platform.routing.api.DocumentRoutingConstants;
import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.feuilleroute.ReponsesRouteStep;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.api.service.FeuilleRouteService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.constant.STDossierLinkConstant;
import fr.dila.st.api.constant.STOperationConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.feuilleroute.STRouteStep;

/**
 * Opération permettant de supprimer les anciens DossierLink des Mailbox, lors de la validation d'une étape
 * de feuille de route.
 * 
 * @author jgomez
 * @author jtremeaux
 */
@Operation(id = RemoveCaseLinkOperation.ID, category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY, label = "Remove Case Links from Mailboxes", description = RemoveCaseLinkOperation.DESCRIPTION)
public class RemoveCaseLinkOperation {
    /**
     * Identifiant technique de l'opération.
     */
    public final static String ID = "Reponses.Distribution.RemoveCaseLink";

    public final static String DESCRIPTION = "Cette opération enlève les case link des mailbox et met à jour la date de fin d'étape";

    private static final String QUERY_DOSSIER_LINK_BY_STEP_FMT = "SELECT * FROM " + 
        STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE + " WHERE " + 
        STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH + 
        " = 'todo' and " + 
        STSchemaConstant.ACTIONABLE_CASE_LINK_SCHEMA_PREFIX + ":" + 
        STSchemaConstant.ACTIONABLE_CASE_LINK_STEP_DOCUMENT_ID_PROPERTY + " = '%s'";
    
    @Context
    protected OperationContext context;

    @Context
    protected CoreSession session;

    @OperationMethod
    public void removeCaseLink() throws ClientException {
        DocumentRouteStep step = (DocumentRouteStep) context.get(DocumentRoutingConstants.OPERATION_STEP_DOCUMENT_KEY);
        ReponsesRouteStep routeStep = step.getDocument().getAdapter(ReponsesRouteStep.class);
        
        List<CaseLink> caseLinkList = fetchCaseLinks();
        
        @SuppressWarnings("unchecked")
        List<DocumentModel> dossierDocList = (List<DocumentModel>) context.get(STOperationConstant.OPERATION_CASES_KEY);

        // Met à jour les données de l'étape après validation
        final FeuilleRouteService feuilleRouteService = ReponsesServiceLocator.getFeuilleRouteService();
        feuilleRouteService.updateRouteStepFieldAfterValidation(session, routeStep, dossierDocList, caseLinkList);
        
        // mise à jour de la date de fin
        updateDateFinEtape();
        
        final DossierDistributionService dossierDistributionService = ReponsesServiceLocator.getDossierDistributionService();
        
        boolean calculEcheance = true;
        for (CaseLink link : caseLinkList) {
            DossierLink dossierLink = link.getDocument().getAdapter(DossierLink.class);
            if (calculEcheance) {
                // on recalcule les échéances opérationnelles du dossier
                Dossier dossier = dossierLink.getDossier(session);
                if (dossier != null) {
                    // Récupère le service de feuilles de route
                    final FeuilleRouteService routingService = ReponsesServiceLocator.getFeuilleRouteService();
                    routingService.calculEcheanceFeuilleRoute(session, dossier);
                    calculEcheance = false;
                }
            }
            
            // Supprime le DossierLink
            dossierDistributionService.deleteDossierLink(session, dossierLink);
        }
    }

    /**
     * Met à jour la date de fin effective de l'étape de feuille de route.
     */
    protected void updateDateFinEtape() {
        DocumentRouteStep step = (DocumentRouteStep) context.get(DocumentRoutingConstants.OPERATION_STEP_DOCUMENT_KEY);
        STRouteStep stStep = step.getDocument().getAdapter(STRouteStep.class);
        stStep.setDateFinEtape(GregorianCalendar.getInstance());
        stStep.save(session);
    }

    protected List<CaseLink> fetchCaseLinks() throws ClientException {
        List<CaseLink> links = new ArrayList<CaseLink>();
        CaseLink link = (CaseLink) context.get(CaseConstants.OPERATION_CASE_LINK_KEY);
        if (link != null) {
            links.add(link);
        }
        @SuppressWarnings("unchecked")
        List<CaseLink> attachedLinks = (List<CaseLink>) context.get(CaseConstants.OPERATION_CASE_LINKS_KEY);
        if (attachedLinks != null && !attachedLinks.isEmpty()) {
            links.addAll(attachedLinks);
        }
        if (links.isEmpty()) {
            links.addAll(fetchCaseLinksFromStep());
        }
        return links;
    }

    protected List<CaseLink> fetchCaseLinksFromStep() throws ClientException {
        DocumentRouteStep step = (DocumentRouteStep) context.get(DocumentRoutingConstants.OPERATION_STEP_DOCUMENT_KEY);
        String query = String.format(QUERY_DOSSIER_LINK_BY_STEP_FMT, step.getDocument().getId());
        DocumentModelList docs = session.query(query);
        List<CaseLink> result = new ArrayList<CaseLink>();
        for (DocumentModel doc : docs) {
            result.add(doc.getAdapter(ActionableCaseLink.class));
        }
        return result;
    }
}
