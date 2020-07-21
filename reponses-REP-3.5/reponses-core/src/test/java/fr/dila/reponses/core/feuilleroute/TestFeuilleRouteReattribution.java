package fr.dila.reponses.core.feuilleroute;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

import fr.dila.cm.caselink.ActionableCaseLink;
import fr.dila.cm.caselink.CaseLink;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.ecm.platform.routing.api.DocumentRoute;
import fr.dila.ecm.platform.routing.api.DocumentRouteElement;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.service.FeuilleRouteService;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;
import fr.dila.reponses.core.service.DossierDistributionServiceImpl;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STSchemaConstant;

/**
 * Test de la réattribution de feuilles de routes. Une reattribution diffère de la substitution : - même instance de
 * feuille de route. - Les étapes à venir de l'ancienne instance sont supprimées - Les étapes de la nouvelles feuille de
 * route sont ajoutées à la suite - l'étape en cours est validée
 * 
 * @author jgomez
 * 
 */
public class TestFeuilleRouteReattribution extends ReponsesRepositoryTestCase {

	private static final Log				LOG						= LogFactory
																			.getLog(TestFeuilleRouteReattribution.class);

	private static final String				DEFAULT_ROUTE_NAME		= "DefaultRouteModel";

	private static final String				NEW_MINSTERE_ROUTE_NAME	= "NouveauMinistereRouteModel";

	private FeuilleRouteService				feuilleRouteService;

	private DossierDistributionServiceImpl	dossierDistributionService;

	private DocumentRoute					currentRoute;

	private DocumentRoute					newRoute;

	@Override
	public void setUp() throws Exception {
		LOG.debug("ENTER SETUP");
		super.setUp();

		feuilleRouteService = ReponsesServiceLocator.getFeuilleRouteService();
		assertNotNull(feuilleRouteService);

		dossierDistributionService = (DossierDistributionServiceImpl) ReponsesServiceLocator
				.getDossierDistributionService();
		assertNotNull(dossierDistributionService);

		LOG.debug("EXIT SETUP");
	}

	/**
	 * Vérifie la réattribution d'une feuille de route.
	 * 
	 * @throws Exception
	 */
	public void testReattribution1EtapeValide() throws Exception {
		openSession();

		// Crée la feuille de route par défaut
		currentRoute = createDefaultRoute(session);
		// Crée une nouvelle feuille de route (d'un autre ministère)
		newRoute = createRouteNouveauMinistere(session);

		// Valide les feuille de route
		validateRoute(currentRoute);
		validateRoute(newRoute);

		// Cree un dossier
		Dossier dossier = createDossier();
		DocumentModel dossierDoc = dossier.getDocument();
		// Démarre la feuille de route associée au dossier
		LOG.info("Démarre la feuille de route associée au dossier");
		DocumentRoute routeInstance = dossierDistributionService.startDefaultRoute(session, dossier);
		DocumentModel routeInstanceDoc = routeInstance.getDocument();
		assertEquals("Instance de feuille de route initiale", routeInstanceDoc.getId(), dossier.getLastDocumentRoute());

		// Récupère le CaseLink de user1
		closeSession();
		session = openSessionAs(getUserManager().getPrincipal(user1));

		Mailbox userMailbox = getPersonalMailbox(user1);
		List<CaseLink> links = getDistributionService().getReceivedCaseLinks(session, userMailbox, 0, 0);
		assertEquals(1, links.size());
		ActionableCaseLink actionableLink = null;
		for (CaseLink link : links) {
			if (link.isActionnable()) {
				actionableLink = (ActionableCaseLink) link;
				actionableLink.validate(session);
			}
		}

		// Substitue la feuille de route
		LOG.info("Substitue la feuille de route");
		closeSession();
		session = openSessionAs(getUserManager().getPrincipal(administrator));
		// Met le ministère attributaire.
		DocumentModelList steps = getRoutingService().getOrderedRouteElement(routeInstanceDoc.getId(), session);

		// réattribue le dossier
		DocumentModel routeInstance2Doc = dossierDistributionService.reattribuerDossier(session, newRoute, dossier,
				STSchemaConstant.ROUTING_TASK_VALIDATION_STATUS_NON_CONCERNE_VALUE);
		assertNotNull(routeInstance2Doc);
		dossierDistributionService.startRouteAfterSubstitution(session, routeInstanceDoc, routeInstance2Doc,
				"substitution");
		dossierDoc = session.getDocument(dossierDoc.getRef());
		dossier = dossierDoc.getAdapter(Dossier.class);
		assertEquals("Instance de feuille de route après substitution", routeInstance2Doc.getId(),
				dossier.getLastDocumentRoute());
		assertNotSame(routeInstanceDoc.getId(), routeInstance2Doc.getId());
		assertNotSame(dossier.getLastDocumentRoute(), routeInstanceDoc.getId());
		DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
		steps = documentRoutingService.getOrderedRouteElement(routeInstance2Doc.getId(), session);
		assertEquals(5, steps.size());

		assertEquals("Pour redaction user 1", steps.get(0).getName());
		assertEquals("Pour reattribution", steps.get(1).getName());
		assertEquals("Pour attribution agents BDC", steps.get(2).getName());
		assertEquals("Pour rédaction DLF", steps.get(3).getName());
		assertEquals("Pour validation PM", steps.get(4).getName());
		assertEquals(DocumentRouteElement.ElementLifeCycleState.done.name(), steps.get(0).getCurrentLifeCycleState());
		assertEquals(DocumentRouteElement.ElementLifeCycleState.done.name(), steps.get(1).getCurrentLifeCycleState());
		assertEquals(DocumentRouteElement.ElementLifeCycleState.running.name(), steps.get(2).getCurrentLifeCycleState());
		assertEquals(DocumentRouteElement.ElementLifeCycleState.ready.name(), steps.get(3).getCurrentLifeCycleState());
		assertEquals(DocumentRouteElement.ElementLifeCycleState.ready.name(), steps.get(4).getCurrentLifeCycleState());

		closeSession();
	}

	/**
	 * Vérifie la réattribution d'une feuille de route.
	 * 
	 * @throws Exception
	 */
	public void testReattributionDirect() throws Exception {
		openSession();

		// Crée la feuille de route par défaut
		currentRoute = createDefaultRoute(session);
		// Crée une nouvelle feuille de route (d'un autre ministère)
		newRoute = createRouteNouveauMinistere(session);

		// Valide les feuille de route
		validateRoute(currentRoute);
		validateRoute(newRoute);

		// Cree un dossier
		Dossier dossier = createDossier();
		DocumentModel dossierDoc = dossier.getDocument();
		// Démarre la feuille de route associée au dossier
		DocumentRoute routeInstance = dossierDistributionService.startDefaultRoute(session, dossier);
		DocumentModel routeInstanceDoc = routeInstance.getDocument();
		assertEquals("Instance de feuille de route initiale", routeInstanceDoc.getId(), dossier.getLastDocumentRoute());

		// Substitue la feuille de route
		closeSession();
		session = openSessionAs(getUserManager().getPrincipal(administrator));
		// Met le ministère attributaire.
		DocumentModelList steps = getRoutingService().getOrderedRouteElement(routeInstanceDoc.getId(), session);
		DocumentModel routeInstance2Doc = dossierDistributionService.reattribuerDossier(session, newRoute, dossier,
				STSchemaConstant.ROUTING_TASK_VALIDATION_STATUS_NON_CONCERNE_VALUE);
		assertNotNull(routeInstance2Doc);
		dossierDistributionService.startRouteAfterSubstitution(session, routeInstanceDoc, routeInstance2Doc,
				"substitution");
		dossierDoc = session.getDocument(dossierDoc.getRef());
		dossier = dossierDoc.getAdapter(Dossier.class);
		assertEquals("Instance de feuille de route après substitution", routeInstance2Doc.getId(),
				dossier.getLastDocumentRoute());
		assertNotSame(routeInstanceDoc.getId(), routeInstance2Doc.getId());
		assertNotSame(dossier.getLastDocumentRoute(), routeInstanceDoc.getId());

		steps = getRoutingService().getOrderedRouteElement(routeInstance2Doc.getId(), session);
		assertEquals(4, steps.size());

		assertEquals("Pour redaction user 1", steps.get(0).getName());
		assertEquals("Pour validation PM", steps.get(3).getName());
		assertEquals(DocumentRouteElement.ElementLifeCycleState.done.name(), steps.get(0).getCurrentLifeCycleState());
		assertEquals(DocumentRouteElement.ElementLifeCycleState.running.name(), steps.get(1).getCurrentLifeCycleState());
		assertEquals(DocumentRouteElement.ElementLifeCycleState.ready.name(), steps.get(2).getCurrentLifeCycleState());
		assertEquals(DocumentRouteElement.ElementLifeCycleState.ready.name(), steps.get(3).getCurrentLifeCycleState());

		closeSession();
	}

	/**
	 * Retourne la route par défaut (crée ses étapes par effet de bord).
	 * 
	 * @param session
	 *            Session
	 * @return Route par défaut
	 * @throws Exception
	 */
	private DocumentRoute createDefaultRoute(CoreSession session) throws Exception {
		DocumentModel route = createFeuilleRoute(session, DEFAULT_ROUTE_NAME);
		session.save();
		final String user1MboxId = getPersonalMailbox(user1).getId();
		final String user2MboxId = getPersonalMailbox(user2).getId();
		createSerialStep(session, route, user1MboxId, "Pour redaction user 1",
				VocabularyConstants.ROUTING_TASK_TYPE_REDACTION);
		createSerialStep(session, route, user2MboxId, "Pour reattribution",
				VocabularyConstants.ROUTING_TASK_TYPE_SIGNATURE);
		createSerialStep(session, route, user1MboxId, "Pour validation pm",
				VocabularyConstants.ROUTING_TASK_TYPE_VALIDATION_PM);
		route = session.saveDocument(route);
		session.save();
		return route.getAdapter(DocumentRoute.class);
	}

	/**
	 * Retourne la nouvelle feuille de route
	 * 
	 * @param session
	 *            Session
	 * @return la nouvelle route
	 * @throws Exception
	 */
	private DocumentRoute createRouteNouveauMinistere(CoreSession session) throws Exception {
		DocumentModel route = createFeuilleRoute(session, NEW_MINSTERE_ROUTE_NAME);
		session.save();
		final String user1MboxId = getPersonalMailbox(user1).getId();
		final String user2MboxId = getPersonalMailbox(user2).getId();
		createSerialStep(session, route, user1MboxId, "Pour attribution agents BDC",
				VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION);
		createSerialStep(session, route, user2MboxId, "Pour rédaction DLF",
				VocabularyConstants.ROUTING_TASK_TYPE_REDACTION);
		createSerialStep(session, route, user2MboxId, "Pour validation PM",
				VocabularyConstants.ROUTING_TASK_TYPE_VALIDATION_PM);
		session.save();
		return route.getAdapter(DocumentRoute.class);
	}

}
