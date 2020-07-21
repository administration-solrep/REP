package fr.dila.reponses.core.feuilleroute;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;

import fr.dila.cm.mailbox.Mailbox;
import fr.dila.ecm.platform.routing.api.DocumentRoute;
import fr.dila.ecm.platform.routing.api.exception.DocumentRouteAlredayLockedException;
import fr.dila.ecm.platform.routing.api.exception.DocumentRouteNotLockedException;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.service.FeuilleRouteModelService;
import fr.dila.reponses.api.service.FeuilleRouteService;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.core.query.QueryUtils;

public class TestFeuilleRouteReponse extends ReponsesRepositoryTestCase {
	private static final String			DEFAULT_ROUTE_NAME	= "DefaultRouteModel";

	private static final Log			LOG					= LogFactory.getLog(TestFeuilleRouteReponse.class);

	protected FeuilleRouteModelService	feuilleRouteModelService;

	protected FeuilleRouteService		feuilleRouteService;

	@Override
	public void setUp() throws Exception {
		super.setUp();

		feuilleRouteModelService = ReponsesServiceLocator.getFeuilleRouteModelService();
		assertNotNull(feuilleRouteModelService);

		feuilleRouteService = ReponsesServiceLocator.getFeuilleRouteService();
		assertNotNull(feuilleRouteService);
	}

	// Une feuille de route qui ressemble un plus à celle utilisée par réponse
	private DocumentRoute createFeuilleRoute(CoreSession session) throws Exception {

		DocumentModel route = createFeuilleRoute(session, DEFAULT_ROUTE_NAME);

		final String userMboxid1 = getPersonalMailbox(user1).getId();
		final String userMboxid2 = getPersonalMailbox(user2).getId();
		createSerialStep(session, route, userMboxid1, "Pour attribution agents BDC",
				VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION);
		createSerialStep(session, route, userMboxid2, "Pour rédaction DLF",
				VocabularyConstants.ROUTING_TASK_TYPE_REDACTION);
		createSerialStep(session, route, userMboxid2, "Pour validation PM",
				VocabularyConstants.ROUTING_TASK_TYPE_VALIDATION_PM);
		session.save();
		return route.getAdapter(DocumentRoute.class);
	}

	private Dossier createDossier(CoreSession session) throws Exception {
		DocumentModel questionDocumentModel = createDocument(DossierConstants.QUESTION_DOCUMENT_TYPE,
				"newQuestionTest2");
		Question question = questionDocumentModel.getAdapter(Question.class);
		DocumentModel dossierDocumentModel = createDocument(DossierConstants.DOSSIER_DOCUMENT_TYPE, "newDossierTest2");
		// check properties
		Long numQuestionLong = 15524L;
		String typeQuestion = VocabularyConstants.QUESTION_TYPE_QE;
		question.setTypeQuestion(typeQuestion);
		String texteQuestion = "quelle heure est il ?";
		Calendar date = GregorianCalendar.getInstance();
		String nomAuteur = "Valjean";
		String prenomAuteur = "Jean";
		question.setNumeroQuestion(numQuestionLong);
		question.setTypeQuestion(typeQuestion);
		question.setTexteQuestion(texteQuestion);
		question.setDateReceptionQuestion(date);
		question.setNomAuteur(nomAuteur);
		question.setPrenomAuteur(prenomAuteur);
		Dossier dossier = dossierDocumentModel.getAdapter(Dossier.class);
		dossier = getDossierDistributionService().createDossier(session, dossier, question, null,
				VocabularyConstants.ETAT_QUESTION_EN_COURS);
		return dossier;
	}

	private void createAndValidateFdr() throws Exception, DocumentRouteAlredayLockedException, ClientException,
			DocumentRouteNotLockedException {
		// Crée la feuille de route par défaut
		DocumentRoute feuille_de_route_reponse = createFeuilleRoute(session);
		assertNotNull(feuille_de_route_reponse);
		// Valide la feuille de route
		validateRoute(feuille_de_route_reponse);
	}

	public void testValidationPMStep() throws Exception {
		openSession();

		createAndValidateFdr();

		DocumentRoute defaultRoute = feuilleRouteModelService.getDefaultRouteQuestion(session);
		assertNotNull(defaultRoute);

		Dossier dossier = createDossier(session);
		session.saveDocument(dossier.getDocument());
		DocumentRoute routeInstance = getDossierDistributionService().startDefaultRoute(session, dossier);
		DocumentModel routeInstanceDoc = routeInstance.getDocument();
		DocumentModel pm_step = feuilleRouteService.getValidationPMStep(session, routeInstanceDoc.getId());
		assertNotNull(pm_step);
		assertEquals("Pour validation PM", pm_step.getTitle());

		closeSession();
	}

	public void testDuplicationFdr() throws DocumentRouteAlredayLockedException, DocumentRouteNotLockedException,
			ClientException, Exception {
		openSession();

		createAndValidateFdr();

		final String query = "SELECT f.ecm:uuid AS id FROM " + STConstant.FEUILLE_ROUTE_DOCUMENT_TYPE
				+ " AS f WHERE f.fdr:feuilleRouteDefaut = 1 " + " AND f.ecm:currentLifeCycleState = 'validated' ";

		final DocumentRef refs[] = QueryUtils.doUFNXQLQueryForIds(session, query, null, 1, 0);
		LOG.info(refs.length);

		DocumentRoute defaultRoute = feuilleRouteModelService.getDefaultRouteQuestion(session);
		assertNotNull(defaultRoute);
		Mailbox userMailbox = getPersonalMailbox(user1);
		DocumentModel routeToCopyDoc = defaultRoute.getDocument();

		DocumentRoute defaultRouteDuplicate = getRoutingService().duplicateRouteModel(session, routeToCopyDoc,
				userMailbox.getId());
		session.saveDocument(defaultRouteDuplicate.getDocument());
		session.save();

		waitForAsyncEventCompletion();
		assertNotNull(defaultRouteDuplicate);
		assertEquals(defaultRouteDuplicate.getDocument().getTitle(), defaultRoute.getName() + " (Copie)");
		assertEquals("draft", defaultRouteDuplicate.getDocument().getCurrentLifeCycleState());

		closeSession();
	}

	public void testDuplicationEtValidationFdr() throws DocumentRouteAlredayLockedException,
			DocumentRouteNotLockedException, ClientException, Exception {
		openSession();
		createAndValidateFdr();

		DocumentRoute defaultRoute = feuilleRouteModelService.getDefaultRouteQuestion(session);
		assertNotNull(defaultRoute);
		Mailbox userMailbox = getPersonalMailbox(user1);
		LOG.info("duplicate model");
		DocumentRoute defaultRouteDuplicate = getRoutingService().duplicateRouteModel(session,
				defaultRoute.getDocument(), userMailbox.getId());
		final String defaultRouteDuplicateId = defaultRouteDuplicate.getDocument().getId();
		waitForAsyncEventCompletion();

		assertEquals(defaultRouteDuplicate.getDocument().getTitle(), defaultRoute.getName() + " (Copie)");
		assertEquals("draft", defaultRouteDuplicate.getDocument().getCurrentLifeCycleState());

		session.save();
		closeSession();

		// Validation de la fdr
		LOG.info("validate fdr");
		openSession();
		defaultRouteDuplicate = session.getDocument(new IdRef(defaultRouteDuplicateId)).getAdapter(DocumentRoute.class);
		validateRoute(defaultRouteDuplicate);
		closeSession();
	}

	public void testDuplicationStepValidated() throws DocumentRouteAlredayLockedException,
			DocumentRouteNotLockedException, ClientException, Exception {
		openSession();

		createAndValidateFdr();
		DocumentRoute defaultRoute = feuilleRouteModelService.getDefaultRouteQuestion(session);
		assertNotNull(defaultRoute);
		Mailbox userMailbox = getPersonalMailbox(user1);
		DocumentRoute defaultRouteDuplicate = getRoutingService().duplicateRouteModel(session,
				defaultRoute.getDocument(), userMailbox.getId());
		session.save();

		waitForAsyncEventCompletion();

		closeSession();
		openSession();

		// Passage d'étapes de fdr à l'état validated
		DocumentModelList children = session.getChildren(defaultRouteDuplicate.getDocument().getRef());
		DocumentModel step0 = children.get(0);
		DocumentModel step1 = children.get(1);
		assertEquals("draft", step0.getCurrentLifeCycleState());
		assertEquals("draft", step1.getCurrentLifeCycleState());
		step0.followTransition("toValidated");
		step1.followTransition("toValidated");
		assertEquals("validated", step0.getCurrentLifeCycleState());
		assertEquals("validated", step1.getCurrentLifeCycleState());

		// Test de la méthode qui remet les états d'étape de modèle de fdr à 'draft'
		// Cette méthode est appelée lors de la validation d'un modèle de fdr
		getRoutingService().checkAndMakeAllStateStepDraft(session, defaultRouteDuplicate);

		step0 = session.getChildren(defaultRouteDuplicate.getDocument().getRef()).get(0);
		step1 = session.getChildren(defaultRouteDuplicate.getDocument().getRef()).get(1);

		// Si les états d'étape sont repassés à 'draft' c'est que la méthode fait ce qu'on lui demande
		assertEquals("draft", step0.getCurrentLifeCycleState());
		assertEquals("draft", step1.getCurrentLifeCycleState());

		// Validation de la fdr pour vérifier que le mécanisme de validation fonctionne toujours
		validateRoute(defaultRouteDuplicate);

		closeSession();
	}
}
