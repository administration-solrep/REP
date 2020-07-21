package fr.dila.reponses.core.feuilleroute;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.runtime.api.Framework;

import fr.dila.cm.caselink.ActionableCaseLink;
import fr.dila.cm.caselink.CaseLink;
import fr.dila.cm.cases.CaseConstants;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.ecm.platform.routing.api.DocumentRoute;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.feuilleroute.ReponsesFeuilleRoute;
import fr.dila.reponses.api.service.FeuilleRouteModelService;
import fr.dila.reponses.api.service.FeuilleRouteService;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.constant.STConstant;

/**
 * Test du service FeuilleRouteService.
 * - Méthode feuilleRouteService.getDocumentRouteStep()
 * 
 * @author jtremeaux
 */
public class TestFeuilleRouteService extends ReponsesRepositoryTestCase  {
    private static final String DEFAULT_ROUTE_NAME = "DefaultRouteModel";

    private FeuilleRouteModelService feuilleRouteModelService;

    private FeuilleRouteService feuilleRouteService;

    private DocumentRoute defaultRoute;
    
    /**
     * Identifiants des étapes de la feuille de route.
     */
    protected List<String> stepNameList;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        feuilleRouteModelService = ReponsesServiceLocator.getFeuilleRouteModelService();
        assertNotNull(feuilleRouteModelService);

        feuilleRouteService = ReponsesServiceLocator.getFeuilleRouteService();
        assertNotNull(feuilleRouteService);

    }

    public void testDefaultRoute() throws Exception {
    	openSession();
    	
    	// Crée la feuille de route par défaut
    	defaultRoute = createDefaultRoute(session);
        
    	// Valide la feuille de route
        getRoutingService().lockDocumentRoute(defaultRoute, session);
        defaultRoute = getRoutingService().validateRouteModel(defaultRoute, session);
        session.saveDocument(defaultRoute.getDocument());
        session.save();
        getRoutingService().unlockDocumentRoute(defaultRoute, session);
        
        Framework.getLocalService(EventService.class).waitForAsyncCompletion();

        assertEquals("validated", defaultRoute.getDocument().getCurrentLifeCycleState());
        assertEquals("validated", session.getChildren(defaultRoute.getDocument().getRef()).get(0).getCurrentLifeCycleState());
        
        // Vérifie la présence d'une feuille de route par défaut
        DocumentRoute defaultRoute = feuilleRouteModelService.getDefaultRouteQuestion(session);
        assertNotNull(defaultRoute);
        
        // Cree un dossier
        Dossier dossier = createDossier();
        
        // Démarre la feuille de route associée au dossier
        getDossierDistributionService().startDefaultRoute(session, dossier);

        // Valide les tâches successivement
        verifyCaseLinkPresent(user1, true);
        verifyCaseLinkAbsent(user2);
        validateUserTask(user1, 1);

        verifyCaseLinkAbsent(user1);
        verifyCaseLinkPresent(user2, true);
        validateUserTask(user2, 1);

        // Les CaseLink sont retirés des 2 mailbox
        verifyCaseLinkAbsent(user1);
        verifyCaseLinkAbsent(user2);
        
        closeSession();
    }

    private void validateUserTask(String user, int stepIndex) throws Exception {
        NuxeoPrincipal principal = getUserManager().getPrincipal(user);
        
        closeSession();
        session = openSessionAs(principal);
        
        Mailbox userMailbox = getPersonalMailbox(user);
        List<CaseLink> links = getDistributionService().getReceivedCaseLinks(session, userMailbox, 0, 0);
        assertEquals(1, links.size());

        ActionableCaseLink actionableLink = null;
        for (CaseLink link : links) {
            if (link.isActionnable()) {
                actionableLink = (ActionableCaseLink) link;
                
                // Vérifie que le CaseLink correspond à l'étape
//                DocumentModel etapeDoc = documentRoutingService.getDocumentRouteStep(session, actionableLink);
//                assertNotNull("Impossible de trouver l'étape liée au CaseLink", etapeDoc);
//                assertEquals(stepNameList.get(stepIndex), etapeDoc.getName());

                actionableLink.validate(session);
            }
        }
        assertNotNull(actionableLink);
    }

    /**
     * Retourne la route par défaut (crée ses étapes par effet de bord).
     * 
     * @param session Session
     * @return Route par défaut
     * @throws Exception
     */
    private DocumentRoute createDefaultRoute(CoreSession session) throws Exception {
        DocumentModel fdrRoot = createOrGetFeuilleRouteModelFolder(session);
        
        DocumentModel route = createDocumentModel(session, DEFAULT_ROUTE_NAME,
                STConstant.FEUILLE_ROUTE_DOCUMENT_TYPE, fdrRoot.getPathAsString());
        ReponsesFeuilleRoute feuilleRoute = route.getAdapter(ReponsesFeuilleRoute.class);
        assertNotNull(feuilleRoute);
        
        feuilleRoute.setFeuilleRouteDefaut(true);
        session.saveDocument(feuilleRoute.getDocument());
        session.save();
        
        stepNameList = new ArrayList<String>();
        
        // Etape 1
        DocumentModel step1 = createDocumentModel(session, "Tâche de distribution user1",
                STConstant.ROUTE_STEP_DOCUMENT_TYPE,
                route.getPathAsString());
        Mailbox user1Mailbox = getPersonalMailbox(user1);
        step1.setPropertyValue(
                CaseConstants.STEP_DISTRIBUTION_MAILBOX_ID_PROPERTY_NAME,
                user1Mailbox.getId());
        session.saveDocument(step1);
        stepNameList.add(step1.getName());

        // Etape 2
        DocumentModel step2 = createDocumentModel(session, "Tâche de validation user2",
                STConstant.ROUTE_STEP_DOCUMENT_TYPE,
                route.getPathAsString());
        Mailbox user2Mailbox = getPersonalMailbox(user2);
        step2.setPropertyValue(
                CaseConstants.STEP_DISTRIBUTION_MAILBOX_ID_PROPERTY_NAME,
                user2Mailbox.getId());
        session.saveDocument(step2);
        stepNameList.add(step2.getName());
        
        session.save();
        
        return route.getAdapter(DocumentRoute.class);
    }
}
