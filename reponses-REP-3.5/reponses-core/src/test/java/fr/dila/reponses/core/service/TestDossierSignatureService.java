package fr.dila.reponses.core.service;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.service.DossierSignatureService;
import fr.dila.reponses.api.service.ReponseService;
import fr.dila.reponses.api.service.ReponsesMailService;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;

/**
 * @see DossierSignatureService
 * @author tlombard
 */
public class TestDossierSignatureService extends ReponsesRepositoryTestCase {
	private static final Log log = LogFactory.getLog(TestDossierSignatureService.class);

	private Question question1;
	private Reponse reponse1;
	
	private Question question2;
	private Reponse reponse2;

	private DocumentModel dossierDocumentModel;
	private DocumentModel dossierDocumentModel2;

	private ReponseService reponseService;

	private ReponsesMailService reponsesMailService;
	
	private static final Long NUM_QUESTION_1 = 15523L;
	private static final Long NUM_QUESTION_2 = 1234L;
	private static final String TYPE_QUESTION_1 = VocabularyConstants.QUESTION_TYPE_QE;
	private static final String TYPE_QUESTION_2 = VocabularyConstants.QUESTION_TYPE_QOAD;
	private static final Long LEGISLATURE_1= 15L;
	private static final Long LEGISLATURE_2= 16L;

	@Override
	public void setUp() throws Exception {
		super.setUp();

		reponseService = ReponsesServiceLocator.getReponseService();
		assertNotNull(reponseService);

		reponsesMailService = ReponsesServiceLocator.getReponsesMailService();
		assertNotNull(reponsesMailService);

		openSession();

		DocumentModel questionDocumentModel = createDocument(DossierConstants.QUESTION_DOCUMENT_TYPE,
				"newQuestionTest");
		DocumentModel reponseDocumentModel = createDocument(DossierConstants.REPONSE_DOCUMENT_TYPE, "ReponseSly");
		reponse1 = reponseDocumentModel.getAdapter(Reponse.class);
		question1 = questionDocumentModel.getAdapter(Question.class);
		dossierDocumentModel = createDocument(DossierConstants.DOSSIER_DOCUMENT_TYPE, "newDossierTest");
		createDocument("FeuilleRouteModelFolder", "fdrRootFolder");
		
		DocumentModel questionDocumentModel2 = createDocument(DossierConstants.QUESTION_DOCUMENT_TYPE,
				"newQuestionTest");
		DocumentModel reponseDocumentModel2 = createDocument(DossierConstants.REPONSE_DOCUMENT_TYPE, "ReponseSly");
		reponse2 = reponseDocumentModel2.getAdapter(Reponse.class);
		question2 = questionDocumentModel2.getAdapter(Question.class);
		dossierDocumentModel2 = createDocument(DossierConstants.DOSSIER_DOCUMENT_TYPE, "newDossierTest");
		createDocument("FeuilleRouteModelFolder", "fdrRootFolder");

		session.save();
		closeSession();
	}

	private Dossier generateDossierSenat() throws ClientException {
		String texteQuestion = "quelle heure est il ?";
		Calendar date = GregorianCalendar.getInstance();
		String nomAuteur = "Valjean";
		String prenomAuteur = "Jean";

		question1.setNumeroQuestion(NUM_QUESTION_1);
		question1.setTypeQuestion(TYPE_QUESTION_1);
		question1.setTexteQuestion(texteQuestion);
		question1.setDateReceptionQuestion(date);
		question1.setNomAuteur(nomAuteur);
		question1.setPrenomAuteur(prenomAuteur);
		question1.setLegislatureQuestion(LEGISLATURE_1);
		question1.setOrigineQuestion(DossierConstants.ORIGINE_QUESTION_SENAT);

		reponse1.setTexteReponse("HELLOOO");
		reponse1.setIdAuteurReponse("Sanh Ly");

		Dossier dossier = dossierDocumentModel.getAdapter(Dossier.class);
		dossier = getDossierDistributionService().createDossier(session, dossier, question1, reponse1,
				VocabularyConstants.ETAT_QUESTION_EN_COURS);

		return dossier;
	}
	
	private Dossier generateDossierAN() throws ClientException {
		String texteQuestion = "Pourquoi tant de haine ?";
		Calendar date = GregorianCalendar.getInstance();
		String nomAuteur = "David";
		String prenomAuteur = "Jonathan";

		question2.setNumeroQuestion(NUM_QUESTION_2);
		question2.setTypeQuestion(TYPE_QUESTION_2);
		question2.setTexteQuestion(texteQuestion);
		question2.setDateReceptionQuestion(date);
		question2.setNomAuteur(nomAuteur);
		question2.setPrenomAuteur(prenomAuteur);
		question2.setLegislatureQuestion(LEGISLATURE_2);
		question2.setOrigineQuestion(DossierConstants.ORIGINE_QUESTION_AN);

		reponse2.setTexteReponse("HELLOOO");
		reponse2.setIdAuteurReponse("Sanh Ly");

		Dossier dossier = dossierDocumentModel2.getAdapter(Dossier.class);
		dossier = getDossierDistributionService().createDossier(session, dossier, question2, reponse2,
				VocabularyConstants.ETAT_QUESTION_EN_COURS);

		return dossier;
	}
	
	public void testGenerateKey() throws ClientException {
		openSession();
		
		String key = getDossierSignatureService().generateKey(generateDossierSenat(), session);

		assertNotNull(key);
		assertEquals(LEGISLATURE_1 + "_" + DossierConstants.ORIGINE_QUESTION_SENAT + "_"
				+ VocabularyConstants.QUESTION_TYPE_QE + "_" + NUM_QUESTION_1, key);
		
		key =getDossierSignatureService().generateKey(generateDossierAN(), session);
		
		assertNotNull(key);
		assertEquals(LEGISLATURE_2 + "_" + DossierConstants.ORIGINE_QUESTION_AN+ "_"
				+ VocabularyConstants.QUESTION_TYPE_QOAD + "_" + NUM_QUESTION_2, key);
	}
}
