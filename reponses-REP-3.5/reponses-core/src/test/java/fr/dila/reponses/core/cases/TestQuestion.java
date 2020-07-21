package fr.dila.reponses.core.cases;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;

import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.flux.QErratum;
import fr.dila.reponses.api.cases.flux.QuestionStateChange;
import fr.dila.reponses.api.cases.flux.Signalement;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;
import fr.dila.reponses.core.cases.flux.QErratumImpl;

/**
 * @author arolin
 */
public class TestQuestion extends ReponsesRepositoryTestCase {

	private static final Log	log	= LogFactory.getLog(TestQuestion.class);

	private Question			question;

	@Override
	public void setUp() throws Exception {
		super.setUp();

		openSession();
		DocumentModel docModel = createDocument(DossierConstants.QUESTION_DOCUMENT_TYPE, "newQuestionTest");
		question = docModel.getAdapter(Question.class);
		session.save();
		closeSession();
	}

	public void testGetDocument() {

		// on verifie que le document est de type Question, possède
		// possède le schéma "Question"
		log.info("begin : test dossier type ");

		DocumentModel dossierModel = question.getDocument();
		assertNotNull(dossierModel);
		assertNotNull(dossierModel.getPath());
		assertEquals(DossierConstants.QUESTION_DOCUMENT_TYPE, dossierModel.getType());
		assertTrue(dossierModel.hasSchema(DossierConstants.QUESTION_DOCUMENT_SCHEMA));
	}

	public void testAllQuestionProperties() throws Exception {
		log.info("begin : test all the question schema properties ");

		openSession();

		// init metadata variable
		String origineQuestion = VocabularyConstants.QUESTION_ORIGINE_AN;
		String pageJO = "12";

		String typeQuestion = VocabularyConstants.QUESTION_TYPE_QE;
		Long numeroQuestion = 4L;

		Calendar dateJO = GregorianCalendar.getInstance();
		dateJO.set(2009, 5, 14);
		Calendar dateReception = GregorianCalendar.getInstance();
		dateReception.set(2010, 5, 14);
		Calendar dateRenouvellement = GregorianCalendar.getInstance();
		dateRenouvellement.set(2011, 5, 14);
		Calendar dateRetrait = GregorianCalendar.getInstance();
		dateRetrait.set(2012, 5, 14);
		Calendar dateCaducite = GregorianCalendar.getInstance();
		dateCaducite.set(2013, 6, 15);
		Calendar dateSignalement = GregorianCalendar.getInstance();
		dateSignalement.set(2013, 8, 15);

		List<String> assNatRubrique = new ArrayList<String>();
		assNatRubrique.add("anrub1");
		assNatRubrique.add("anrub2");
		List<String> analyseList = new ArrayList<String>();
		analyseList.add("patates");
		analyseList.add("choux");
		List<String> assNatTeteAnalyse = new ArrayList<String>();
		assNatTeteAnalyse.add("anta1");
		assNatTeteAnalyse.add("anta2");

		String groupePolitique = "UMP";
		String nomAuteur = "Taillerand";
		String prenomAuteur = "Jean-Marc";

		List<String> seThemeList = new ArrayList<String>();
		seThemeList.add("Bidonville");
		seThemeList.add("Afrique");
		List<String> senatQuestionRubrique = new ArrayList<String>();
		senatQuestionRubrique.add("serub1");
		senatQuestionRubrique.add("serub2");
		List<String> seRenvoisList = new ArrayList<String>();
		seRenvoisList.add("renvois1");
		seRenvoisList.add("renvois2");

		String caracteristiquesQuestion = "sansReponse";
		String texteQuestion = "C\\’est un saxophoniste important dans"
				+ " l'histoire du jazz, en particulier par la création d’une esthétique originale "
				+ "et très personnelle, privilégiant la mélodie et la sensibilité. Garbarek possède"
				+ " une identité musicale extrêmement reconnaissable, se démarquant ";

		List<String> motsClefsMinistereList = new ArrayList<String>();
		motsClefsMinistereList.add("m1");
		motsClefsMinistereList.add("m2");

		/* indexation complementaire */
		List<String> idxComplMotsClefsMinistereList = new ArrayList<String>();
		idxComplMotsClefsMinistereList.add("idxcmpm1");
		idxComplMotsClefsMinistereList.add("idxcmpm2");

		/* indexation complementaire AN */
		List<String> idxComplAssNatRubrique = new ArrayList<String>();
		idxComplAssNatRubrique.add("idxcmpanrub1");
		idxComplAssNatRubrique.add("idxcmpanrub2");
		List<String> idxComplAnalyseList = new ArrayList<String>();
		idxComplAnalyseList.add("idxcmppatates");
		idxComplAnalyseList.add("idxcmpchoux");
		List<String> idxComplAssNatTeteAnalyse = new ArrayList<String>();
		idxComplAssNatTeteAnalyse.add("idxcmpanta1");
		idxComplAssNatTeteAnalyse.add("idxcmpanta2");

		/* indexation complementaire SENAT */
		List<String> idxComplSeThemeList = new ArrayList<String>();
		idxComplSeThemeList.add("idxcmpBidonville");
		idxComplSeThemeList.add("idxcmpAfrique");
		List<String> idxComplSenatQuestionRubrique = new ArrayList<String>();
		idxComplSenatQuestionRubrique.add("idxcmpserub1");
		idxComplSenatQuestionRubrique.add("idxcmpserub2");
		List<String> idxComplSeRenvoisList = new ArrayList<String>();
		idxComplSeRenvoisList.add("idxcmprenvois1");
		idxComplSeRenvoisList.add("idxcmprenvois2");

		QErratum er1 = new QErratumImpl();
		er1.setPageJo(new Integer(12));
		er1.setTexteConsolide("TexteConsolide");
		GregorianCalendar dc = new GregorianCalendar();
		dc.setTimeInMillis(Calendar.getInstance().getTimeInMillis());
		er1.setDatePublication(dc);
		er1.setTexteErratum("TexteErratum");

		QErratum er2 = new QErratumImpl();
		er2.setPageJo(new Integer(12));
		er2.setTexteConsolide("TexteConsolide2");
		er2.setDatePublication(dc);
		er2.setTexteErratum("TexteErratum2");

		List<QErratum> erratumList = new ArrayList<QErratum>();
		erratumList.add(er1);
		erratumList.add(er2);

		// set metadata
		question.setNumeroQuestion(numeroQuestion);
		question.setOrigineQuestion(origineQuestion);
		question.setTypeQuestion(typeQuestion);
		question.setPageJO(pageJO);

		question.setDatePublicationJO(dateJO);
		question.setDateReceptionQuestion(dateReception);

		question.setAssNatRubrique(assNatRubrique);
		question.setAssNatAnalyses(analyseList);
		question.setAssNatTeteAnalyse(assNatTeteAnalyse);

		question.setNomAuteur(nomAuteur);
		question.setPrenomAuteur(prenomAuteur);
		question.setGroupePolitique(groupePolitique);

		question.setSenatQuestionThemes(seThemeList);
		question.setSenatQuestionRubrique(senatQuestionRubrique);
		question.setSenatQuestionRenvois(seRenvoisList);

		question.setTexteQuestion(texteQuestion);

		question.setMotsClefMinistere(motsClefsMinistereList);
		question.setCaracteristiquesQuestion(caracteristiquesQuestion);
		question.setErrata(erratumList);

		question.setIndexationComplAssNatAnalyses(idxComplAnalyseList);
		question.setIndexationComplAssNatRubrique(idxComplAssNatRubrique);
		question.setIndexationComplAssNatTeteAnalyse(idxComplAssNatTeteAnalyse);
		question.setIndexationComplMotsClefMinistere(idxComplMotsClefsMinistereList);
		question.setIndexationComplSenatQuestionRenvois(idxComplSeRenvoisList);
		question.setIndexationComplSenatQuestionRubrique(idxComplSenatQuestionRubrique);
		question.setIndexationComplSenatQuestionThemes(idxComplSeThemeList);

		String questionId = question.getDocument().getId();
		DocumentModel questionModel = session.saveDocument(question.getDocument());
		session.save();
		Calendar today = Calendar.getInstance(Locale.FRANCE);
		today.setTime(new Date());

		String state1 = VocabularyConstants.ETAT_QUESTION_EN_COURS;
		String state2 = VocabularyConstants.ETAT_QUESTION_REATTRIBUEEE;
		String state3 = VocabularyConstants.ETAT_QUESTION_RETIREE;

		session.saveDocument(question.getDocument());
		session.save();

		closeSession();
		openSession();
		questionModel = session.getDocument(new IdRef(questionId));
		question = questionModel.getAdapter(Question.class);

		String delaiQuestionSignale = "10";
		
		question.setEtatQuestion(session, state1, today, delaiQuestionSignale);
		question.setEtatQuestion(session, state2, today, delaiQuestionSignale);
		question.setEtatQuestion(session, state3, today, delaiQuestionSignale);

		// check properties persistance
		assertEquals(origineQuestion, question.getOrigineQuestion());
		assertEquals(numeroQuestion, question.getNumeroQuestion());
		assertEquals(typeQuestion, question.getTypeQuestion());
		assertEquals(pageJO, question.getPageJO());

		assertEquals(dateJO, question.getDatePublicationJO());
		assertEquals(dateReception, question.getDateReceptionQuestion());

		assertEquals(question.getAssNatRubrique(), assNatRubrique);
		assertEquals(question.getAssNatAnalyses(), analyseList);
		assertEquals(question.getAssNatTeteAnalyse(), assNatTeteAnalyse);

		assertEquals(nomAuteur, question.getNomAuteur());
		assertEquals(prenomAuteur, question.getPrenomAuteur());
		assertEquals(nomAuteur + " " + prenomAuteur, question.getNomCompletAuteur());
		assertEquals(groupePolitique, question.getGroupePolitique());

		assertEquals(question.getSenatQuestionThemes(), seThemeList);
		assertEquals(question.getSenatQuestionRubrique(), senatQuestionRubrique);
		assertEquals(question.getSenatQuestionRenvois(), seRenvoisList);

		assertEquals(question.getTexteQuestion(), texteQuestion);
		assertEquals(question.getCaracteristiquesQuestion(), caracteristiquesQuestion);
		assertEquals(question.getMotsClefMinistere(), motsClefsMinistereList);

		assertEquals(2, question.getErrata().size());
		List<QuestionStateChange> lstEtatsQuestion = question.getEtatQuestionHistorique(session);
		assertEquals(2, lstEtatsQuestion.size());
		assertEquals(state1, lstEtatsQuestion.get(0).getNewState());
		assertEquals(today, lstEtatsQuestion.get(0).getChangeDate());
		assertEquals(state3, lstEtatsQuestion.get(1).getNewState());
		assertEquals(today, lstEtatsQuestion.get(1).getChangeDate());

		assertEquals(state3, question.getEtatQuestionSimple());
		assertEquals(today, question.getDateRetraitQuestion());

		assertEquals(idxComplAnalyseList, question.getIndexationComplAssNatAnalyses());
		assertEquals(idxComplAssNatRubrique, question.getIndexationComplAssNatRubrique());
		assertEquals(idxComplAssNatTeteAnalyse, question.getIndexationComplAssNatTeteAnalyse());
		assertEquals(idxComplMotsClefsMinistereList, question.getIndexationComplMotsClefMinistere());
		assertEquals(idxComplSeRenvoisList, question.getIndexationComplSenatQuestionRenvois());
		assertEquals(idxComplSenatQuestionRubrique, question.getIndexationComplSenatQuestionRubrique());
		assertEquals(idxComplSeThemeList, question.getIndexationComplSenatQuestionThemes());

		assertEquals(Boolean.TRUE, question.isQuestionTypeEcrite());
		assertEquals(Boolean.TRUE, question.hasOrigineAN());

		closeSession();
		openSession();
		questionModel = session.getDocument(new IdRef(questionId));
		question = questionModel.getAdapter(Question.class);

		assertNull(question.getDateCaducite());
		assertNull(question.getDateRenouvellementQuestion());

		question.setEtatQuestion(session, VocabularyConstants.ETAT_QUESTION_CADUQUE, dateCaducite,
				delaiQuestionSignale);
		session.saveDocument(question.getDocument());
		session.save();

		closeSession();
		openSession();
		questionModel = session.getDocument(new IdRef(questionId));
		question = questionModel.getAdapter(Question.class);

		assertEquals(dateCaducite, question.getDateCaducite());

		question.setEtatQuestion(session, VocabularyConstants.ETAT_QUESTION_RENOUVELEE, dateRenouvellement,
				delaiQuestionSignale);

		session.saveDocument(question.getDocument());
		session.save();

		closeSession();
		openSession();
		questionModel = session.getDocument(new IdRef(questionId));
		question = questionModel.getAdapter(Question.class);

		assertEquals(dateRenouvellement, question.getDateRenouvellementQuestion());

		assertFalse(question.getEtatRappele());

		question.setEtatQuestion(session, VocabularyConstants.ETAT_QUESTION_RAPPELE, Calendar.getInstance(),
				delaiQuestionSignale);

		session.saveDocument(question.getDocument());
		session.save();

		closeSession();
		openSession();
		questionModel = session.getDocument(new IdRef(questionId));
		question = questionModel.getAdapter(Question.class);

		assertTrue(question.getEtatRappele());

		assertFalse(question.getEtatSignale());

		question.setEtatQuestion(session, VocabularyConstants.ETAT_QUESTION_SIGNALEE, dateSignalement,
				delaiQuestionSignale);

		session.saveDocument(question.getDocument());
		session.save();

		closeSession();
		openSession();
		questionModel = session.getDocument(new IdRef(questionId));
		question = questionModel.getAdapter(Question.class);

		assertTrue(question.getEtatSignale());
		assertFalse(VocabularyConstants.ETAT_QUESTION_SIGNALEE.equals(question.getEtatQuestionSimple()));
		
		List<Signalement> signalements = question.getSignalements();
		assertFalse(signalements.isEmpty());
		Signalement signalement = signalements.get(0);
		assertEquals(dateSignalement.getTime(),signalement.getDateEffet());
		dateSignalement.add(Calendar.DAY_OF_YEAR, 10);
		assertEquals(dateSignalement.getTime(),signalement.getDateAttendue());
		
		closeSession();
	}

	/**
	 * test le positionnement de la valeur de l'etat courant de la question suite aux appels a setEtatQuestion Celui-ci
	 * ne doit etre modifie que pour certaines valeur d'état
	 * 
	 * @throws ClientException
	 */
	public void testEtatQuestionCourant() throws ClientException {

		openSession();
		String etatModificateurs[] = new String[] { VocabularyConstants.ETAT_QUESTION_RETIREE,
				VocabularyConstants.ETAT_QUESTION_CADUQUE, VocabularyConstants.ETAT_QUESTION_EN_COURS,
				VocabularyConstants.ETAT_QUESTION_REPONDU, VocabularyConstants.ETAT_QUESTION_CLOTURE_AUTRE };

		String etatNonModificateurs[] = new String[] { VocabularyConstants.ETAT_QUESTION_SIGNALEE,
				VocabularyConstants.ETAT_QUESTION_RENOUVELEE, VocabularyConstants.ETAT_QUESTION_REATTRIBUEEE,
				VocabularyConstants.ETAT_QUESTION_RAPPELE };

		for (String etat : etatModificateurs) {
			assertTrue(QuestionImpl.canBeCurrentStateOfQuestion(etat));
		}

		for (String etat : etatNonModificateurs) {
			assertFalse(QuestionImpl.canBeCurrentStateOfQuestion(etat));
		}

		GregorianCalendar today = new GregorianCalendar(Locale.FRANCE);
		today.setTime(new Date());
		String delaiQuestionSignale = "10";
		// test la modif de l'etat courant
		for (String etat : etatModificateurs) {
			question.setEtatQuestion(session, etat, today, delaiQuestionSignale);
			assertEquals("Unexpected State", etat, question.getEtatQuestionSimple());
		}

		// test la non modif de l'etat courant
		String lastEtat = question.getEtatQuestionSimple();
		for (String etat : etatNonModificateurs) {
			question.setEtatQuestion(session, etat, today, delaiQuestionSignale);
			assertEquals("Unexpected State", lastEtat, question.getEtatQuestionSimple());
		}

		closeSession();
	}

	/**
	 * test le positionnement de l'historique suite aux appels a setEtatQuestion Celui-ci ne doit etre modifie que pour
	 * certaines valeur d'état
	 * 
	 * @throws ClientException
	 */
	public void testEtatQuestionHistorique() throws ClientException {
		openSession();

		Calendar today = GregorianCalendar.getInstance(Locale.FRANCE);
		today.setTime(new Date());

		String etatModificateurs[] = new String[] { VocabularyConstants.ETAT_QUESTION_RETIREE,
				VocabularyConstants.ETAT_QUESTION_CADUQUE, VocabularyConstants.ETAT_QUESTION_EN_COURS,
				VocabularyConstants.ETAT_QUESTION_REPONDU, VocabularyConstants.ETAT_QUESTION_CLOTURE_AUTRE,
				VocabularyConstants.ETAT_QUESTION_SIGNALEE, VocabularyConstants.ETAT_QUESTION_RENOUVELEE };

		String etatNonModificateurs[] = new String[] { VocabularyConstants.ETAT_QUESTION_REATTRIBUEEE,
				VocabularyConstants.ETAT_QUESTION_RAPPELE };

		for (String etat : etatModificateurs) {
			assertTrue("Expected true for " + etat, QuestionImpl.shouldBeListedInHistoric(etat));
		}

		for (String etat : etatNonModificateurs) {
			assertFalse("Expected false for " + etat, QuestionImpl.shouldBeListedInHistoric(etat));
		}

		String delaiQuestionSignale = "10";
		int prevSize = question.getEtatQuestionHistorique(session).size();
		for (String etat : etatModificateurs) {
			question.setEtatQuestion(session, etat, today, delaiQuestionSignale);
			session.saveDocument(question.getDocument());
			session.save();
			List<QuestionStateChange> qscList = question.getEtatQuestionHistorique(session);
			assertEquals(prevSize + 1, qscList.size());
			assertEquals(etat, qscList.get(qscList.size() - 1).getNewState());
			prevSize++;
		}

		prevSize = question.getEtatQuestionHistorique(session).size();
		String prevEtat = question.getEtatQuestion(session).getNewState();
		for (String etat : etatNonModificateurs) {
			question.setEtatQuestion(session, etat, today, delaiQuestionSignale);
			List<QuestionStateChange> qscList = question.getEtatQuestionHistorique(session);
			assertEquals(prevSize, qscList.size());
			assertEquals(prevEtat, qscList.get(qscList.size() - 1).getNewState());
		}

		closeSession();
	}

}