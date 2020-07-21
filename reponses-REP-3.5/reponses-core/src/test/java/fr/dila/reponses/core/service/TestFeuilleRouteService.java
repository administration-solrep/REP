package fr.dila.reponses.core.service;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;

import fr.dila.cm.cases.CaseConstants;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.ecm.platform.routing.api.DocumentRoute;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.feuilleroute.ReponsesFeuilleRoute;
import fr.dila.reponses.api.service.FeuilleRouteModelService;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;
import fr.dila.st.api.constant.STConstant;

/**
 * Teste la selection du modele de feuille de route pour un dossier
 * 
 * @author spesnel
 * 
 */
public class TestFeuilleRouteService extends ReponsesRepositoryTestCase {

	private static final Log	log										= LogFactory
																				.getLog(TestFeuilleRouteService.class);

	private static final String	DEFAULT_ROUTE_NAME						= "DefaultRouteModel";
	private static final String	DEFAULT_ROUTE_NAME_FOR_MINISTERE_ECO	= "DefaultRouteModelForEco";

	private static final String	ID_MINISTERE_ECO						= "50000507";
	private static final String	SENAT_QUESTION_TITRE					= "un titre pour le senat et pas l'assemblée";

	private static final String	SENAT_RUB_1								= "senat rub'1";
	private static final String	SENAT_RUB_2								= "senat rub'2";
	private static final String	SENAT_THEME_1							= "senat theme'1";
	private static final String	SENAT_THEME_2							= "senat theme'2";
	private static final String	SENAT_THEME_3							= "senat theme'3";
	private static final String	AN_RUB_1								= "an'rubrique";
	private static final String	AN_RUB_2								= "an'rubrique 2";
	private static final String	AN_RUB_3								= "an'rubrique 3";
	private static final String	AN_TA_1									= "tête d'analyse";
	private static final String	AN_TA_2									= "tête d'analyse 2";
	private static final String	AN_ana_1								= "analyse 1";
	private static final String	AN_ana_2								= "analyse 2";

	private DocumentRef			defaultRouteRef;
	private DocumentRef			defaultRouteForEcoRef;
	private DocumentRef			routeSenatTitreRef;
	private DocumentRef			routeSenatThemeRef;
	private DocumentRef			routeSenatRubriqueRef;
	private DocumentRef			routeANRubriqueRef;
	private DocumentRef			routeANRubriqueTeteRef;
	private DocumentRef			routeANRubriqueTeteAnalyseRef;

	private int					nbModelCreated;
	
	private String delaiQuestionSignalee = "10";

	@Override
	public void setUp() throws Exception {

		super.setUp();

		openSession();

		createOrGetFeuilleRouteModelFolder(session);

		DocumentRoute route = createDefaultRoute(session, DEFAULT_ROUTE_NAME, null, null, null, null, null, null, null);
		defaultRouteRef = route.getDocument().getRef();
		route = createDefaultRoute(session, DEFAULT_ROUTE_NAME_FOR_MINISTERE_ECO, ID_MINISTERE_ECO, null, null, null,
				null, null, null);
		defaultRouteForEcoRef = route.getDocument().getRef();

		route = createDefaultRoute(session, DEFAULT_ROUTE_NAME_FOR_MINISTERE_ECO, ID_MINISTERE_ECO, null, null, null,
				null, null, SENAT_QUESTION_TITRE);
		routeSenatTitreRef = route.getDocument().getRef();

		List<String> senatThemes = new ArrayList<String>();
		senatThemes.add(SENAT_THEME_1);
		senatThemes.add(SENAT_THEME_2);
		route = createDefaultRoute(session, DEFAULT_ROUTE_NAME_FOR_MINISTERE_ECO, ID_MINISTERE_ECO, null, null, null,
				null, senatThemes, null);
		routeSenatThemeRef = route.getDocument().getRef();

		List<String> senatThemesbis = new ArrayList<String>();
		senatThemes.add(SENAT_THEME_3);
		route = createDefaultRoute(session, DEFAULT_ROUTE_NAME_FOR_MINISTERE_ECO, ID_MINISTERE_ECO, null, null, null,
				null, senatThemesbis, null);
		/* routeSenatTheme2Ref = */route.getDocument().getRef();

		route = createDefaultRoute(session, DEFAULT_ROUTE_NAME_FOR_MINISTERE_ECO, ID_MINISTERE_ECO, null, null, null,
				null, senatThemesbis, null);
		/* routeSenatTheme2CopieRef = */route.getDocument().getRef();

		List<String> senatRubriques = new ArrayList<String>();
		senatRubriques.add(SENAT_RUB_1);
		senatRubriques.add(SENAT_RUB_2);
		route = createDefaultRoute(session, DEFAULT_ROUTE_NAME_FOR_MINISTERE_ECO, ID_MINISTERE_ECO, null, null, null,
				senatRubriques, null, null);
		routeSenatRubriqueRef = route.getDocument().getRef();

		List<String> anRubriques = new ArrayList<String>();
		anRubriques.add(AN_RUB_1);

		List<String> anTete = new ArrayList<String>();
		anTete.add(AN_TA_1);

		List<String> analyses = new ArrayList<String>();
		analyses.add(AN_ana_1);
		analyses.add(AN_ana_2);
		route = createDefaultRoute(session, DEFAULT_ROUTE_NAME_FOR_MINISTERE_ECO, ID_MINISTERE_ECO, anRubriques,
				analyses, anTete, null, null, null);
		routeANRubriqueTeteAnalyseRef = route.getDocument().getRef();

		anRubriques.clear();
		anRubriques.add(AN_RUB_2);
		anTete.clear();
		anTete.add(AN_TA_2);
		route = createDefaultRoute(session, DEFAULT_ROUTE_NAME_FOR_MINISTERE_ECO, ID_MINISTERE_ECO, anRubriques, null,
				anTete, null, null, null);
		routeANRubriqueTeteRef = route.getDocument().getRef();

		anRubriques.clear();
		anRubriques.add(AN_RUB_3);
		route = createDefaultRoute(session, DEFAULT_ROUTE_NAME_FOR_MINISTERE_ECO, ID_MINISTERE_ECO, anRubriques, null,
				null, null, null, null);
		routeANRubriqueRef = route.getDocument().getRef();

		nbModelCreated = 10;

		session.save();
		closeSession();
	}

	public void testGetService() throws Exception {
		openSession();
		FeuilleRouteModelService fdrService = ReponsesServiceLocator.getFeuilleRouteModelService();
		assertNotNull(fdrService);

		String id = fdrService.getFeuilleRouteModelFolderId(session);
		assertEquals(getFeuilleRootModelFolder().getId(), id);

		DocumentModel dm = fdrService.getFeuilleRouteModelFolder(session);
		assertNotNull(dm);
		assertEquals(id, dm.getId());

		String query = "SELECT * FROM FeuilleRoute WHERE ecm:parentId = '" + id + "'";
		DocumentModelList dml = session.query(query);
		assertEquals(nbModelCreated, dml.size());
		closeSession();
	}

	public void testSelectForNotQE() throws Exception {
		openSession();
		FeuilleRouteModelService feuilleRouteModelService = ReponsesServiceLocator.getFeuilleRouteModelService();
		assertNotNull(feuilleRouteModelService);

		openSession();

		Question question = createQuestion();
		Dossier dossier = createDossier(question);

		assertNull(feuilleRouteModelService.selectRouteForDossier(session, dossier));
		closeSession();
	}

	public void testSelectForWithoutState() throws Exception {
		openSession();
		FeuilleRouteModelService feuilleRouteModelService = ReponsesServiceLocator.getFeuilleRouteModelService();
		assertNotNull(feuilleRouteModelService);

		openSession();

		Question question = createQuestion();
		question.setTypeQuestion(VocabularyConstants.QUESTION_TYPE_QE);
		question.setOrigineQuestion("AN");

		session.saveDocument(question.getDocument());
		Dossier dossier = createDossier(question);

		assertNull(feuilleRouteModelService.selectRouteForDossier(session, dossier));
		closeSession();
	}

	public void testSelectForANWithoutIdMinistere() throws Exception {
		openSession();
		FeuilleRouteModelService feuilleRouteModelService = ReponsesServiceLocator.getFeuilleRouteModelService();
		assertNotNull(feuilleRouteModelService);

		openSession();

		Question question = createQuestion();
		question.setTypeQuestion(VocabularyConstants.QUESTION_TYPE_QE);
		question.setEtatQuestion(session, VocabularyConstants.ETAT_QUESTION_EN_COURS, new GregorianCalendar(), delaiQuestionSignalee);
		question.setOrigineQuestion("AN");

		session.saveDocument(question.getDocument());
		Dossier dossier = createDossier(question);

		DocumentRoute route = feuilleRouteModelService.selectRouteForDossier(session, dossier);
		assertNotNull(route);
		log.debug(route);
		assertEquals(defaultRouteRef, route.getDocument().getRef());
		closeSession();

	}

	public void testSelectForAN() throws Exception {
		openSession();
		FeuilleRouteModelService feuilleRouteModelService = ReponsesServiceLocator.getFeuilleRouteModelService();
		assertNotNull(feuilleRouteModelService);

		openSession();

		Question question = createQuestion();
		question.setTypeQuestion(VocabularyConstants.QUESTION_TYPE_QE);
		question.setEtatQuestion(session, VocabularyConstants.ETAT_QUESTION_EN_COURS, new GregorianCalendar(), delaiQuestionSignalee);
		question.setOrigineQuestion("AN");
		question.setIdMinistereInterroge(ID_MINISTERE_ECO);

		session.saveDocument(question.getDocument());
		Dossier dossier = createDossier(question);

		DocumentRoute route = feuilleRouteModelService.selectRouteForDossier(session, dossier);
		assertNotNull(route);
		assertEquals(defaultRouteForEcoRef, route.getDocument().getRef());
		closeSession();
	}

	public void testSelectForANWithRubrique() throws Exception {
		FeuilleRouteModelService feuilleRouteModelService = ReponsesServiceLocator.getFeuilleRouteModelService();
		assertNotNull(feuilleRouteModelService);

		openSession();

		Question question = createQuestion();
		question.setTypeQuestion(VocabularyConstants.QUESTION_TYPE_QE);
		question.setEtatQuestion(session, VocabularyConstants.ETAT_QUESTION_EN_COURS, new GregorianCalendar(), delaiQuestionSignalee);
		question.setOrigineQuestion("AN");
		question.setIdMinistereInterroge(ID_MINISTERE_ECO);
		List<String> rubriqueAssNat = new ArrayList<String>();
		rubriqueAssNat.add(AN_RUB_3);
		List<String> taAssNat = new ArrayList<String>();
		taAssNat.add("une ta");
		question.setAssNatRubrique(rubriqueAssNat);
		question.setAssNatTeteAnalyse(taAssNat);

		session.saveDocument(question.getDocument());
		Dossier dossier = createDossier(question);

		DocumentRoute route = feuilleRouteModelService.selectRouteForDossier(session, dossier);
		assertNotNull(route);
		assertEquals(routeANRubriqueRef, route.getDocument().getRef());
		closeSession();
	}

	public void testSelectForANWithRubriqueTA() throws Exception {
		FeuilleRouteModelService feuilleRouteModelService = ReponsesServiceLocator.getFeuilleRouteModelService();
		assertNotNull(feuilleRouteModelService);

		openSession();

		{
			Question question = createQuestion();
			question.setTypeQuestion(VocabularyConstants.QUESTION_TYPE_QE);
			question.setEtatQuestion(session, VocabularyConstants.ETAT_QUESTION_EN_COURS, new GregorianCalendar(), delaiQuestionSignalee);
			question.setOrigineQuestion("AN");
			question.setIdMinistereInterroge(ID_MINISTERE_ECO);
			List<String> rubriqueAssNat = new ArrayList<String>();
			rubriqueAssNat.add(AN_RUB_2);
			List<String> taAssNat = new ArrayList<String>();
			taAssNat.add(AN_TA_2);
			question.setAssNatRubrique(rubriqueAssNat);
			question.setAssNatTeteAnalyse(taAssNat);

			session.saveDocument(question.getDocument());
			Dossier dossier = createDossier(question);

			DocumentRoute route = feuilleRouteModelService.selectRouteForDossier(session, dossier);
			assertNotNull(route);
			assertEquals(routeANRubriqueTeteRef, route.getDocument().getRef());
		}

		{
			Question question = createQuestion();
			question.setTypeQuestion(VocabularyConstants.QUESTION_TYPE_QE);
			question.setEtatQuestion(session, VocabularyConstants.ETAT_QUESTION_EN_COURS, new GregorianCalendar(), delaiQuestionSignalee);
			question.setOrigineQuestion("AN");
			question.setIdMinistereInterroge(ID_MINISTERE_ECO);
			List<String> rubriqueAssNat = new ArrayList<String>();
			rubriqueAssNat.add(AN_RUB_2);
			List<String> taAssNat = new ArrayList<String>();
			taAssNat.add(AN_TA_2);
			List<String> anaAssNat = new ArrayList<String>();
			anaAssNat.add("une analyse qcq");
			question.setAssNatRubrique(rubriqueAssNat);
			question.setAssNatTeteAnalyse(taAssNat);
			question.setAssNatAnalyses(anaAssNat);

			session.saveDocument(question.getDocument());
			Dossier dossier = createDossier(question);

			DocumentRoute route = feuilleRouteModelService.selectRouteForDossier(session, dossier);
			assertNotNull(route);
			assertEquals(routeANRubriqueTeteRef, route.getDocument().getRef());
		}

		closeSession();
	}

	public void testSelectForANWithRubriqueTAAnalyse() throws Exception {
		FeuilleRouteModelService feuilleRouteModelService = ReponsesServiceLocator.getFeuilleRouteModelService();
		assertNotNull(feuilleRouteModelService);

		openSession();

		Question question = createQuestion();
		question.setTypeQuestion(VocabularyConstants.QUESTION_TYPE_QE);
		question.setEtatQuestion(session, VocabularyConstants.ETAT_QUESTION_EN_COURS, new GregorianCalendar(), delaiQuestionSignalee);
		question.setOrigineQuestion("AN");
		question.setIdMinistereInterroge(ID_MINISTERE_ECO);
		List<String> rubriqueAssNat = new ArrayList<String>();
		rubriqueAssNat.add(AN_RUB_1);
		List<String> taAssNat = new ArrayList<String>();
		taAssNat.add(AN_TA_1);
		List<String> anaAssNat = new ArrayList<String>();
		anaAssNat.add(AN_ana_1);
		anaAssNat.add(AN_ana_2);
		question.setAssNatRubrique(rubriqueAssNat);
		question.setAssNatTeteAnalyse(taAssNat);
		question.setAssNatAnalyses(anaAssNat);

		session.saveDocument(question.getDocument());
		Dossier dossier = createDossier(question);

		DocumentRoute route = feuilleRouteModelService.selectRouteForDossier(session, dossier);
		assertNotNull(route);
		assertEquals(routeANRubriqueTeteAnalyseRef, route.getDocument().getRef());

		closeSession();
	}

	public void testSelectForSenatWithoutIdMinistere() throws Exception {
		FeuilleRouteModelService feuilleRouteModelService = ReponsesServiceLocator.getFeuilleRouteModelService();
		assertNotNull(feuilleRouteModelService);

		openSession();

		Question question = createQuestion();
		question.setTypeQuestion(VocabularyConstants.QUESTION_TYPE_QE);
		question.setEtatQuestion(session, VocabularyConstants.ETAT_QUESTION_EN_COURS, new GregorianCalendar(), delaiQuestionSignalee);
		question.setOrigineQuestion("SENAT");

		session.saveDocument(question.getDocument());
		Dossier dossier = createDossier(question);

		DocumentRoute route = feuilleRouteModelService.selectRouteForDossier(session, dossier);
		assertNotNull(route);
		assertEquals(defaultRouteRef, route.getDocument().getRef());

		closeSession();
	}

	public void testSelectForSenat() throws Exception {
		FeuilleRouteModelService feuilleRouteModelService = ReponsesServiceLocator.getFeuilleRouteModelService();
		assertNotNull(feuilleRouteModelService);

		openSession();

		Question question = createQuestion();
		question.setTypeQuestion(VocabularyConstants.QUESTION_TYPE_QE);
		question.setEtatQuestion(session, VocabularyConstants.ETAT_QUESTION_EN_COURS, new GregorianCalendar(), delaiQuestionSignalee);
		question.setOrigineQuestion("SENAT");
		question.setIdMinistereInterroge(ID_MINISTERE_ECO);

		session.saveDocument(question.getDocument());
		Dossier dossier = createDossier(question);

		DocumentRoute route = feuilleRouteModelService.selectRouteForDossier(session, dossier);
		assertNotNull(route);
		assertEquals(defaultRouteForEcoRef, route.getDocument().getRef());

		closeSession();
	}

	public void testSelectForSenatWithTitre() throws Exception {
		FeuilleRouteModelService feuilleRouteModelService = ReponsesServiceLocator.getFeuilleRouteModelService();
		assertNotNull(feuilleRouteModelService);

		openSession();

		Question question = createQuestion();
		question.setTypeQuestion(VocabularyConstants.QUESTION_TYPE_QE);
		question.setEtatQuestion(session, VocabularyConstants.ETAT_QUESTION_EN_COURS, new GregorianCalendar(), delaiQuestionSignalee);
		question.setOrigineQuestion("SENAT");
		question.setIdMinistereInterroge(ID_MINISTERE_ECO);
		question.setSenatQuestionTitre(SENAT_QUESTION_TITRE);

		session.saveDocument(question.getDocument());
		Dossier dossier = createDossier(question);

		DocumentRoute route = feuilleRouteModelService.selectRouteForDossier(session, dossier);
		assertNotNull(route);
		assertEquals(routeSenatTitreRef, route.getDocument().getRef());

		closeSession();
	}

	public void testSelectForSenatWithTheme() throws Exception {
		FeuilleRouteModelService feuilleRouteModelService = ReponsesServiceLocator.getFeuilleRouteModelService();
		assertNotNull(feuilleRouteModelService);

		openSession();

		{
			Question question = createQuestion();
			question.setTypeQuestion(VocabularyConstants.QUESTION_TYPE_QE);
			question.setEtatQuestion(session, VocabularyConstants.ETAT_QUESTION_EN_COURS, new GregorianCalendar(), delaiQuestionSignalee);
			question.setOrigineQuestion("SENAT");
			question.setIdMinistereInterroge(ID_MINISTERE_ECO);
			List<String> senatThemes = new ArrayList<String>();
			senatThemes.add(SENAT_THEME_2);
			senatThemes.add(SENAT_THEME_1);
			question.setSenatQuestionThemes(senatThemes);
			List<String> senatRubriques = new ArrayList<String>();
			senatRubriques.add(SENAT_RUB_1);
			senatRubriques.add(SENAT_RUB_2);
			question.setSenatQuestionRubrique(senatRubriques);

			session.saveDocument(question.getDocument());
			Dossier dossier = createDossier(question);

			DocumentRoute route = feuilleRouteModelService.selectRouteForDossier(session, dossier);
			assertNotNull(route);
			assertEquals(routeSenatThemeRef, route.getDocument().getRef());
		}

		closeSession();
	}

	public void testSelectForSenatWithRubrique() throws Exception {
		FeuilleRouteModelService feuilleRouteModelService = ReponsesServiceLocator.getFeuilleRouteModelService();
		assertNotNull(feuilleRouteModelService);

		openSession();

		{
			Question question = createQuestion();
			question.setTypeQuestion(VocabularyConstants.QUESTION_TYPE_QE);
			question.setEtatQuestion(session, VocabularyConstants.ETAT_QUESTION_EN_COURS, new GregorianCalendar(), delaiQuestionSignalee);
			question.setOrigineQuestion("SENAT");
			question.setIdMinistereInterroge(ID_MINISTERE_ECO);
			List<String> senatThemes = new ArrayList<String>();
			senatThemes.add(SENAT_THEME_1);
			senatThemes.add(SENAT_THEME_2);
			senatThemes.add("un theme en plus non connu");
			question.setSenatQuestionThemes(senatThemes);
			List<String> senatRubriques = new ArrayList<String>();
			senatRubriques.add(SENAT_RUB_1);
			senatRubriques.add(SENAT_RUB_2);
			question.setSenatQuestionRubrique(senatRubriques);

			session.saveDocument(question.getDocument());
			Dossier dossier = createDossier(question);

			DocumentRoute route = feuilleRouteModelService.selectRouteForDossier(session, dossier);
			assertNotNull(route);
			assertEquals(routeSenatRubriqueRef, route.getDocument().getRef());
		}

		{
			Question question = createQuestion();
			question.setTypeQuestion(VocabularyConstants.QUESTION_TYPE_QE);
			question.setEtatQuestion(session, VocabularyConstants.ETAT_QUESTION_EN_COURS, new GregorianCalendar(), delaiQuestionSignalee);
			question.setOrigineQuestion("SENAT");
			question.setIdMinistereInterroge(ID_MINISTERE_ECO);
			List<String> senatThemes = new ArrayList<String>();
			senatThemes.add(SENAT_THEME_3); // plsuieurs feuille pour ce theme donc doit se rabattre sur la rubrique
			question.setSenatQuestionThemes(senatThemes);
			List<String> senatRubriques = new ArrayList<String>();
			senatRubriques.add(SENAT_RUB_1);
			senatRubriques.add(SENAT_RUB_2);
			question.setSenatQuestionRubrique(senatRubriques);

			session.saveDocument(question.getDocument());
			Dossier dossier = createDossier(question);

			DocumentRoute route = feuilleRouteModelService.selectRouteForDossier(session, dossier);
			assertNotNull(route);
			assertEquals(routeSenatRubriqueRef, route.getDocument().getRef());
		}

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
	private DocumentRoute createDefaultRoute(CoreSession session, String name, String idMinistere,
			List<String> rubriqueAssNat, List<String> analyseAssNat, List<String> teteAnalyseAssNat,
			List<String> rubriqueSenat, List<String> themeSenat, String titreSenat) throws Exception {

		DocumentModel root = createOrGetFeuilleRouteModelFolder(session);
		String rootpath = root.getPathAsString();

		DocumentModel route = createDocumentModel(session, name, STConstant.FEUILLE_ROUTE_DOCUMENT_TYPE, rootpath);
		ReponsesFeuilleRoute feuilleRoute = route.getAdapter(ReponsesFeuilleRoute.class);
		assertNotNull(feuilleRoute);

		if (idMinistere != null) {
			feuilleRoute.setMinistere(idMinistere);
		}

		if (rubriqueAssNat != null) {
			feuilleRoute.getDocument().setProperty(DossierConstants.INDEXATION_DOCUMENT_SCHEMA,
					DossierConstants.DOSSIER_ASSEMBLE_NATIONALE_RUBRIQUE_QUESTION, rubriqueAssNat);
		}
		if (analyseAssNat != null) {
			feuilleRoute.getDocument().setProperty(DossierConstants.INDEXATION_DOCUMENT_SCHEMA,
					DossierConstants.DOSSIER_ASSEMBLE_NATIONALE_ANALYSES_QUESTION, analyseAssNat);
		}
		if (teteAnalyseAssNat != null) {
			feuilleRoute.getDocument().setProperty(DossierConstants.INDEXATION_DOCUMENT_SCHEMA,
					DossierConstants.DOSSIER_ASSEMBLE_NATIONALE_TETE_ANALYSE_QUESTION, teteAnalyseAssNat);
		}

		if (rubriqueSenat != null) {
			feuilleRoute.getDocument().setProperty(DossierConstants.INDEXATION_DOCUMENT_SCHEMA,
					DossierConstants.DOSSIER_SENAT_RUBRIQUE_QUESTION, rubriqueSenat);
		}
		if (themeSenat != null) {
			feuilleRoute.getDocument().setProperty(DossierConstants.INDEXATION_DOCUMENT_SCHEMA,
					DossierConstants.DOSSIER_SENAT_THEMES_QUESTION, themeSenat);
		}

		if (titreSenat != null) {
			feuilleRoute.setTitreQuestion(titreSenat);
		}

		feuilleRoute.setFeuilleRouteDefaut(true);
		session.saveDocument(feuilleRoute.getDocument());
		feuilleRoute.validate(session);
		session.save();

		List<String> stepNameList = new ArrayList<String>();

		// Etape 1
		DocumentModel step1 = createDocumentModel(session, "Tâche de distribution user1",
				STConstant.ROUTE_STEP_DOCUMENT_TYPE, route.getPathAsString());
		Mailbox user1Mailbox = getPersonalMailbox(user1);
		step1.setPropertyValue(CaseConstants.STEP_DISTRIBUTION_MAILBOX_ID_PROPERTY_NAME, user1Mailbox.getId());
		session.saveDocument(step1);
		stepNameList.add(step1.getName());

		// Etape 2
		DocumentModel step2 = createDocumentModel(session, "Tâche de validation user2",
				STConstant.ROUTE_STEP_DOCUMENT_TYPE, route.getPathAsString());
		Mailbox user2Mailbox = getPersonalMailbox(user2);
		step2.setPropertyValue(CaseConstants.STEP_DISTRIBUTION_MAILBOX_ID_PROPERTY_NAME, user2Mailbox.getId());
		session.saveDocument(step2);
		stepNameList.add(step2.getName());

		session.save();

		return route.getAdapter(DocumentRoute.class);
	}

	private Dossier createDossier(Question q) throws Exception {
		DocumentModel docModel = createDocument(DossierConstants.DOSSIER_DOCUMENT_TYPE, "newDossierTest");
		Dossier dossier = docModel.getAdapter(Dossier.class);
		assertNotNull(dossier);

		dossier.setQuestionId(q.getDocument().getId());

		dossier.setIdMinistereAttributaireCourant(q.getIdMinistereInterroge());

		dossier.save(session);
		session.save();
		return dossier;
	}

	private Question createQuestion() throws Exception {
		DocumentModel questionDM = createDocument(DossierConstants.QUESTION_DOCUMENT_TYPE, "question1");
		Question question = questionDM.getAdapter(Question.class);
		return question;
	}

}
