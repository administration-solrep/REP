package fr.dila.reponses.core.feuilleroute;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.cm.caselink.ActionableCaseLink;
import fr.dila.cm.caselink.CaseLink;
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
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.service.STServiceLocator;

public class TestFeuilleRoutePourInformationReponse extends ReponsesRepositoryTestCase {
	private static final String			DEFAULT_ROUTE_NAME	= "DefaultRouteModel";

	private FeuilleRouteModelService	feuilleRouteModelService;

	private FeuilleRouteService			feuilleRouteService;

	private STParametreService			parametreService;

	@Override
	public void setUp() throws Exception {
		super.setUp();

		feuilleRouteService = ReponsesServiceLocator.getFeuilleRouteService();
		assertNotNull(feuilleRouteService);

		parametreService = STServiceLocator.getSTParametreService();
		assertNotNull(parametreService);

		feuilleRouteModelService = ReponsesServiceLocator.getFeuilleRouteModelService();
		assertNotNull(feuilleRouteModelService);
	}

	// Une feuille de route qui ressemble un plus à celle utilisée par réponse
	private DocumentRoute createFeuilleRoute(CoreSession session) throws Exception {

		final DocumentModel route = createFeuilleRoute(session, DEFAULT_ROUTE_NAME);
		session.save();

		final String user1MboxId = getPersonalMailbox(user1).getId();
		final String user2MboxId = getPersonalMailbox(user2).getId();
		final String user3MboxId = getPersonalMailbox(user3).getId();

		createSerialStep(session, route, user1MboxId, "Pour attribution agents BDC",
				VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION);
		createSerialStep(session, route, user2MboxId, "Pour Information DLF",
				VocabularyConstants.ROUTING_TASK_TYPE_INFORMATION);
		createSerialStep(session, route, user3MboxId, "Pour validation PM",
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
		// TODO check
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
		// Valide la feuille de route
		getRoutingService().lockDocumentRoute(feuille_de_route_reponse, session);
		feuille_de_route_reponse = getRoutingService().validateRouteModel(feuille_de_route_reponse, session);
		session.saveDocument(feuille_de_route_reponse.getDocument());
		session.save();
		getRoutingService().unlockDocumentRoute(feuille_de_route_reponse, session);
		waitForAsyncEventCompletion();

		assertEquals("validated", feuille_de_route_reponse.getDocument().getCurrentLifeCycleState());
		assertEquals("validated", session.getChildren(feuille_de_route_reponse.getDocument().getRef()).get(0)
				.getCurrentLifeCycleState());
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

	public void testValidationPourInformationStep() throws Exception {
		openSession();

		createAndValidateFdr();

		DocumentRoute defaultRoute = feuilleRouteModelService.getDefaultRouteQuestion(session);
		assertNotNull(defaultRoute);

		Dossier dossier = createDossier(session);
		session.saveDocument(dossier.getDocument());

		// Démarre la feuille de route associée au dossier
		getDossierDistributionService().startDefaultRoute(session, dossier);

		// validation de l'étape 1
		verifyCaseLinkPresent(user1, true);
		validateUserTask(user1);

		// la fdr doit maintenant être à l'étape 3
		verifyCaseLinkPresent(user3, true);

		closeSession();
	}

}
