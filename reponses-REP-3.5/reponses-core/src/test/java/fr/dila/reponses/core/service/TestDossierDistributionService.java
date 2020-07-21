package fr.dila.reponses.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.model.PropertyException;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.cases.flux.Renouvellement;
import fr.dila.reponses.api.cases.flux.Signalement;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.service.ReponseService;
import fr.dila.reponses.api.service.ReponsesMailService;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;
import fr.dila.reponses.core.cases.flux.SignalementImpl;

/**
 * @author arolin
 */
public class TestDossierDistributionService extends ReponsesRepositoryTestCase {

	private static final Log	log	= LogFactory.getLog(TestDossierDistributionService.class);

	private Question			question;
	private Reponse				reponse;

	private DocumentModel		dossierDocumentModel;

	private ReponseService		reponseService;

	private ReponsesMailService	reponsesMailService;

	@Override
	public void setUp() throws Exception {

		super.setUp();

		reponseService = ReponsesServiceLocator.getReponseService();
		assertNotNull(reponseService);

		reponsesMailService = ReponsesServiceLocator.getReponsesMailService();
		assertNotNull(reponsesMailService);

		openSession();

		DocumentModel questionDocumentModel = createDocument(DossierConstants.QUESTION_DOCUMENT_TYPE, "newQuestionTest");
		DocumentModel reponseDocumentModel = createDocument(DossierConstants.REPONSE_DOCUMENT_TYPE, "ReponseSly");
		reponse = reponseDocumentModel.getAdapter(Reponse.class);
		question = questionDocumentModel.getAdapter(Question.class);

		dossierDocumentModel = createDocument(DossierConstants.DOSSIER_DOCUMENT_TYPE, "newDossierTest");

		createDocument("FeuilleRouteModelFolder", "fdrRootFolder");

		session.save();
		closeSession();
	}

	public void testGetDossierPiecesWithDossier() throws PropertyException, ClientException {
		openSession();
		// on verifie que les différents objet
		log.info("begin : test dossier type ");

		// check properties
		Long numQuestionLong = 15523L;
		String typeQuestion = VocabularyConstants.QUESTION_TYPE_QE;

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

		reponse.setTexteReponse("HELLOOO");
		reponse.setIdAuteurReponse("Sanh Ly");

		Dossier dossier = dossierDocumentModel.getAdapter(Dossier.class);
		dossier = getDossierDistributionService().createDossier(session, dossier, question, reponse,
				VocabularyConstants.ETAT_QUESTION_EN_COURS);

		// Démarre la feuille de route associée au dossier
		getDossierDistributionService().startDefaultRoute(session, dossier);

		Question questionFromDossier = dossier.getQuestion(session);
		assertNotNull(questionFromDossier);
		assertNotNull(questionFromDossier.getDocument());
		assertEquals(questionFromDossier.getDocument().getType(), DossierConstants.QUESTION_DOCUMENT_TYPE);
		assertNotNull(questionFromDossier.getDocument().getId());

		assertEquals(typeQuestion, questionFromDossier.getTypeQuestion());
		assertEquals(numQuestionLong, questionFromDossier.getNumeroQuestion());
		assertEquals("Valjean Jean", questionFromDossier.getNomCompletAuteur());

		Reponse reponseFromDossier = dossier.getReponse(session);
		assertNotNull(reponseFromDossier);
		assertNotNull(reponseFromDossier.getDocument().getId());

		closeSession();
	}

	public void testSignalementOperations() throws ClientException {
		openSession();
		// on verifie que les différents objet
		log.info("------------TEST SIGNALEMENT--------------- ");

		// check properties
		Calendar date = GregorianCalendar.getInstance();

		Signalement unSignalement = new SignalementImpl();
		unSignalement.setDateAttendue(date.getTime());
		unSignalement.setDateEffet(date.getTime());

		assertNotNull(unSignalement.getSignalementMap());
		List<Signalement> signalements = new ArrayList<Signalement>();
		signalements.add(unSignalement);

		question.setDateReceptionQuestion(date);
		question.setSignalements(signalements);

		Dossier dossier = dossierDocumentModel.getAdapter(Dossier.class);
		dossier = getDossierDistributionService().createDossier(session, dossier, question, null,
				VocabularyConstants.ETAT_QUESTION_EN_COURS);
		question.setTypeQuestion(VocabularyConstants.QUESTION_TYPE_QE);
		assertNotNull(dossier.getQuestion(session).getSignalements());
		assertTrue(dossier.getQuestion(session).getSignalements().size() == 1);

		// Démarre la feuille de route associée au dossier
		getDossierDistributionService().startDefaultRoute(session, dossier);

		log.info("------------FIN TEST SIGNALEMENT -------------");
		closeSession();
	}

	public void testRenouvellementOperations() throws ClientException {
		openSession();
		// on verifie que les différents objet
		log.info("------------TEST RENOUVELLEMENT--------------- ");

		// check properties
		Calendar dateRenouvellement = GregorianCalendar.getInstance();
		Calendar dateReception = GregorianCalendar.getInstance();

		question.setDateReceptionQuestion(dateReception);
		question.setEtatQuestion(session, VocabularyConstants.ETAT_QUESTION_RENOUVELEE, dateRenouvellement, "10");

		Dossier dossier = dossierDocumentModel.getAdapter(Dossier.class);
		dossier = getDossierDistributionService().createDossier(session, dossier, question, null,
				VocabularyConstants.ETAT_QUESTION_EN_COURS);

		// Démarre la feuille de route associée au dossier
		getDossierDistributionService().startDefaultRoute(session, dossier);

		assertNotNull(getDossierDistributionService().getLastRenouvellement(dossier.getQuestion(session).getDocument()));
		assertEquals(1, dossier.getQuestion(session).getRenouvellements().size());
		Renouvellement renouvellement = getDossierDistributionService().getLastRenouvellement(
				dossier.getQuestion(session).getDocument());
		assertEquals(dateRenouvellement.getTime(), renouvellement.getDateEffet());

		log.info("------------FIN TEST RENOUVELLEMENT -------------");
		closeSession();
	}

	public void testRechercheQuestionFromNumero() throws ClientException {
		openSession();
		// on verifie que les différents objet
		log.info("------------Recherche de la question par numero de la question --------------- ");
		// check properties
		Long numQuestionLong = 15523L;
		String typeQuestion = "ecrite";
		String texteQuestion = "quelle heure est il ?";
		Calendar date = GregorianCalendar.getInstance();

		question.setNumeroQuestion(numQuestionLong);
		question.setTypeQuestion(typeQuestion);
		// TODO check
		question.setTexteQuestion(texteQuestion);
		question.setDateReceptionQuestion(date);

		// set numero question to dossier
		Dossier dossierDoc = dossierDocumentModel.getAdapter(Dossier.class);
		dossierDoc.setNumeroQuestion(numQuestionLong);

		Dossier dossier = getDossierDistributionService().createDossier(session, dossierDoc, question, null,
				VocabularyConstants.ETAT_QUESTION_EN_COURS);

		assertNotNull(dossier);
		assertNotNull(dossier.getNumeroQuestion());
		assertEquals(dossier.getNumeroQuestion(), numQuestionLong);

		// Démarre la feuille de route associée au dossier
		getDossierDistributionService().startDefaultRoute(session, dossier);

		//
		DocumentModelList list = getDossierDistributionService().getDossierFrom(session, numQuestionLong.toString());
		assertNotNull(list);
		System.out.println("------------ Liste de question : " + list);
		System.out.println("------------ nombre de document : " + list.size());
		//
		assertEquals(list.size(), 1);
		Iterator<DocumentModel> iteratorModel = list.iterator();
		while (iteratorModel.hasNext()) {
			DocumentModel dossierDocmodel = iteratorModel.next();
			DocumentModel reponseModel = reponseService.getReponseFromDossier(session, dossierDocmodel);
			Reponse reponseAdapter = reponseModel.getAdapter(Reponse.class);
			assertNotNull(reponseAdapter);

			Dossier unDossier = dossierDocmodel.getAdapter(Dossier.class);
			Question laQuestion = unDossier.getQuestion(session);
			assertEquals(numQuestionLong, laQuestion.getNumeroQuestion());
			assertEquals(typeQuestion, laQuestion.getTypeQuestion());
			assertEquals(texteQuestion, laQuestion.getTexteQuestion());
			assertEquals(date, laQuestion.getDateReceptionQuestion());

		}
		closeSession();
	}

	public void testIsExistingQuestion() throws ClientException {
		openSession();

		Long numeroQuestion = 15523L;
		String typeQuestion = "ecrite";
		String origineQuestion = "AN";
		Long legislatureQuestion = 50L;

		question.setNumeroQuestion(numeroQuestion);
		question.setTypeQuestion(typeQuestion);
		question.setOrigineQuestion(origineQuestion);
		question.setLegislatureQuestion(legislatureQuestion);
		Dossier dossier = dossierDocumentModel.getAdapter(Dossier.class);
		dossier = getDossierDistributionService().createDossier(session, dossier, question, null,
				VocabularyConstants.ETAT_QUESTION_EN_COURS);

		Question q = dossier.getQuestion(session);
		assertEquals(numeroQuestion, q.getNumeroQuestion());
		assertEquals(typeQuestion, q.getTypeQuestion());
		assertEquals(origineQuestion, q.getOrigineQuestion());
		assertEquals(legislatureQuestion, q.getLegislatureQuestion());

		assertNotNull(getDossierDistributionService());

		try {
			String id = getDossierDistributionService().retrieveDocumentQuestionId(session, numeroQuestion,
					typeQuestion, origineQuestion, legislatureQuestion);
			assertNotNull(id);
			assertEquals(q.getDocument().getId(), id);

			assertTrue(getDossierDistributionService().isExistingQuestion(session, numeroQuestion, typeQuestion,
					origineQuestion, legislatureQuestion));

			assertFalse(getDossierDistributionService().isExistingQuestion(session, numeroQuestion,
					"NOT EXISTING TYPE", origineQuestion, legislatureQuestion));
		} catch (ClientException e) {
			fail("testIsExistingQuestion");
		}

		try {
			getDossierDistributionService().isExistingQuestion(null, numeroQuestion, typeQuestion, origineQuestion,
					legislatureQuestion);
			fail();
		} catch (IllegalArgumentException e) {
			// exception expected : session null
		}

		try {
			getDossierDistributionService().isExistingQuestion(session, numeroQuestion, " ", origineQuestion,
					legislatureQuestion);
			fail();
		} catch (IllegalArgumentException e) {
			// exception expected : type blank
		}

		try {
			getDossierDistributionService().isExistingQuestion(session, numeroQuestion, null, origineQuestion,
					legislatureQuestion);
			fail();
		} catch (IllegalArgumentException e) {
			// exception expected : type null
		}

		try {
			getDossierDistributionService().isExistingQuestion(session, numeroQuestion, typeQuestion, " ",
					legislatureQuestion);
			fail();
		} catch (IllegalArgumentException e) {
			// exception expected : source blank
		}

		try {
			getDossierDistributionService().isExistingQuestion(session, numeroQuestion, typeQuestion, null,
					legislatureQuestion);
			fail();
		} catch (IllegalArgumentException e) {
			// exception expected : source null
		}

		closeSession();
	}
}