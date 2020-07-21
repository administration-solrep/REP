package fr.dila.reponses.core.feuilleroute;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.lifecycle.LifeCycleConstants;

import fr.dila.cm.caselink.ActionableCaseLink;
import fr.dila.cm.caselink.CaseLink;
import fr.dila.cm.cases.CaseConstants;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.ecm.platform.routing.api.DocumentRoute;
import fr.dila.ecm.platform.routing.api.DocumentRouteElement;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.feuilleroute.ReponsesFeuilleRoute;
import fr.dila.reponses.api.service.FeuilleRouteModelService;
import fr.dila.reponses.api.service.FeuilleRouteService;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.feuilleroute.DocumentRouteTableElement;

/**
 * Ce test exécute une feuille de route jusqu'à sa terminaison, puis ajoute une nouvelle étape et 
 * lance à nouveau la feuille de route.
 * 
 * @author jtremeaux
 */
public class TestFeuilleRouteBackToReady extends ReponsesRepositoryTestCase  {
	
	private static final Log LOG = LogFactory.getLog(TestFeuilleRouteBackToReady.class); 
	
    private static final String DEFAULT_ROUTE_NAME = "DefaultRouteModel";

    protected FeuilleRouteModelService feuilleRouteModelService;

    protected FeuilleRouteService feuilleRouteService;

    protected DocumentRoute defaultRoute;
    
    protected static final String STATE_DRAFT = DocumentRouteElement.ElementLifeCycleState.draft.name();
    
    protected static final String STATE_READY = DocumentRouteElement.ElementLifeCycleState.ready.name();
    
    protected static final String STATE_RUNNING = DocumentRouteElement.ElementLifeCycleState.running.name();
    
    protected static final String STATE_DONE = DocumentRouteElement.ElementLifeCycleState.done.name();
    
    @Override
    public void setUp() throws Exception {
        super.setUp();

        feuilleRouteService = ReponsesServiceLocator.getFeuilleRouteService();
        assertNotNull(feuilleRouteService);

        feuilleRouteModelService = ReponsesServiceLocator.getFeuilleRouteModelService();
        assertNotNull(feuilleRouteModelService);
    }

    public void testFeuilleRouteBackToReady() throws Exception {
    	openSession();
    	// Crée la feuille de route par défaut
    	defaultRoute = createDefaultRoute(session);
        
    	// Valide la feuille de route
        validateRoute(defaultRoute);
        
        // Vérifie la présence d'une feuille de route par défaut
        DocumentRoute defaultRoute = feuilleRouteModelService.getDefaultRouteQuestion(session);
        assertNotNull(defaultRoute);
        
        // Crée le dossier
        Dossier dossier = createDossier();
        
        // Démarre la feuille de route associée au dossier
        DocumentRoute feuilleRouteInstance = getDossierDistributionService().startDefaultRoute(session, dossier);

        LOG.info("Feuille de route démarrée - Ajout d'une étape");
        
        // Ajoute une étape à la feuille de route
        DocumentModel feuilleRouteInstanceDoc = feuilleRouteInstance.getDocument();
        DocumentModel step2Doc = session.createDocumentModel(feuilleRouteInstanceDoc.getPathAsString(), "Tâche de validation user2", STConstant.ROUTE_STEP_DOCUMENT_TYPE);
        Mailbox user2Mailbox = getPersonalMailbox(user2);
        step2Doc.setPropertyValue(CaseConstants.STEP_DISTRIBUTION_MAILBOX_ID_PROPERTY_NAME, user2Mailbox.getId());
        step2Doc.putContextData(LifeCycleConstants.INITIAL_LIFECYCLE_STATE_OPTION_NAME, STATE_READY);
        step2Doc = session.createDocument(step2Doc);
        session.save();
        
        waitForAsyncEventCompletion();

        // Vérifie l'état de la feuille de route et de ses éléments
        checkFeuilleRouteState(feuilleRouteInstance, STATE_RUNNING, new String[] { STATE_RUNNING, STATE_READY });

        // Valide les étapes
        verifyCaseLinkPresent(user1, true);
        verifyCaseLinkAbsent(user2);
        
        validateUserTask(user1);
        verifyCaseLinkAbsent(user1);
        verifyCaseLinkPresent(user2, true);
        
        validateUserTask(user2);
        verifyCaseLinkAbsent(user1);
        verifyCaseLinkAbsent(user2);
        
        // Vérifie que la route et ses éléments sont a l'état terminé
        checkFeuilleRouteState(feuilleRouteInstance, STATE_DONE, new String[] { STATE_DONE, STATE_DONE });
        
        // Ajoute une étape à la feuille de route une fois terminée
        DocumentModel step3Doc = session.createDocumentModel(feuilleRouteInstanceDoc.getPathAsString(), "Tâche de validation user3", STConstant.ROUTE_STEP_DOCUMENT_TYPE);
        Mailbox user3Mailbox = getPersonalMailbox(user3);
        step3Doc.setPropertyValue(CaseConstants.STEP_DISTRIBUTION_MAILBOX_ID_PROPERTY_NAME, user3Mailbox.getId());
        step3Doc.putContextData(LifeCycleConstants.INITIAL_LIFECYCLE_STATE_OPTION_NAME, STATE_READY);
        step3Doc = session.createDocument(step3Doc);
        session.save();
        
        // Vérifie l'état de la feuille de route et de ses éléments
        checkFeuilleRouteState(feuilleRouteInstance, STATE_DONE, new String[] { STATE_DONE, STATE_DONE, STATE_READY });

        // Redémarre la feuille de route
        feuilleRouteInstance.backToReady(session);
        session.save();
        waitForAsyncEventCompletion();

        // Vérifie l'état de la feuille de route et de ses éléments
        checkFeuilleRouteState(feuilleRouteInstance, STATE_READY, new String[] { STATE_DONE, STATE_DONE, STATE_READY });

        feuilleRouteInstance.run(session);
        
        // Vérifie l'état de la feuille de route et de ses éléments
        checkFeuilleRouteState(feuilleRouteInstance, STATE_RUNNING, new String[] { STATE_DONE, STATE_DONE, STATE_RUNNING });

        // Valide la nouvelle étape
        verifyCaseLinkAbsent(user1);
        verifyCaseLinkAbsent(user2);
        verifyCaseLinkPresent(user3, true);
        
        validateUserTask(user3);
        verifyCaseLinkAbsent(user1);
        verifyCaseLinkAbsent(user2);
        verifyCaseLinkAbsent(user3);
        
        closeSession();
    }

    private void validateUserTask(String user) throws Exception {
        closeSession();
        session = openSessionAs(getUserManager().getPrincipal(user));
        
        Mailbox userMailbox = getPersonalMailbox(user);
        List<CaseLink> links = getDistributionService().getReceivedCaseLinks(session, userMailbox, 0, 0);
        assertEquals("Présence du CaseLink dans la Mailbox de l'utilisateur", 1, links.size());

        ActionableCaseLink actionableLink = null;
        for (CaseLink link : links) {
            if (link.isActionnable()) {
                actionableLink = (ActionableCaseLink) link;
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
        
        DocumentModel route = createDocumentModel(session, DEFAULT_ROUTE_NAME, STConstant.FEUILLE_ROUTE_DOCUMENT_TYPE, fdrRoot.getPathAsString());
        ReponsesFeuilleRoute feuilleRoute = route.getAdapter(ReponsesFeuilleRoute.class);
        assertNotNull(feuilleRoute);

        feuilleRoute.setFeuilleRouteDefaut(true);
        session.saveDocument(feuilleRoute.getDocument());
        session.save();

        // Etape 1
        DocumentModel step1 = createDocumentModel(session, "Tâche de validation user1", STConstant.ROUTE_STEP_DOCUMENT_TYPE, route.getPathAsString());
        Mailbox user1Mailbox = getPersonalMailbox(user1);
        step1.setPropertyValue(CaseConstants.STEP_DISTRIBUTION_MAILBOX_ID_PROPERTY_NAME, user1Mailbox.getId());
        session.saveDocument(step1);

        session.save();

        return route.getAdapter(DocumentRoute.class);
    }
    
    protected void checkFeuilleRouteState(DocumentRoute route, String routeState, String... routeElementState) throws ClientException {
        closeSession();
        session = openSessionAs(administrator);

        // Vérifie l'état en cours de la feuille de route
        DocumentModel routeDoc = session.getDocument(route.getDocument().getRef());
        assertEquals("Etat en cours de la feuille de route", routeState, routeDoc.getCurrentLifeCycleState());
        
        // Vérifie l'état en cours des éléments de la feuille de route
        List<DocumentRouteTableElement> stepTable = getRoutingService().getFeuilleRouteElements(route, session);
        assertEquals(routeElementState.length, stepTable.size());
        for (int i = 0; i < stepTable.size(); i++) {
            DocumentRouteTableElement stepTableElement = stepTable.get(i);
            String stepState = routeElementState[i];

            DocumentModel stepDoc = stepTableElement.getDocument();
            assertEquals("Etat en cours de l'élément de la feuille de route", stepState, stepDoc.getCurrentLifeCycleState());
        }
    }
}
