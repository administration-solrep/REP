package fr.dila.reponses.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.naming.directory.DirContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.directory.ldap.LDAPSession;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

import fr.dila.cm.caselink.CaseLink;
import fr.dila.cm.cases.CaseConstants;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.service.CaseDistributionService;
import fr.dila.cm.service.CaseManagementDistributionTypeService;
import fr.dila.ecm.platform.routing.api.DocumentRoute;
import fr.dila.ecm.platform.routing.api.DocumentRoutingConstants;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.feuilleroute.ReponsesFeuilleRoute;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.api.service.DossierSignatureService;
import fr.dila.reponses.api.service.MailboxService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.core.SSRepositoryTestCase;
import fr.dila.ss.core.helper.FeuilleRouteTestHelper;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Classe de test de base de l'application Réponses.
 * 
 * - Inclut un annuaire utilisateur HSQLDB chargé à partir de fichier csv.
 */
public class ReponsesRepositoryTestCase extends SSRepositoryTestCase {

	public static final String			FeuilleRootModelFolderName	= "fdrmodelroot";

	public static final String			CASE_TITLE					= "moncase";

	protected static final String		administrator				= "Administrator";

	protected static final String		user						= "user";

	protected static final String		user1						= "user1";

	protected static final String		user2						= "user2";

	protected static final String		user3						= "user3";

	private static final Log			LOG							= LogFactory
																			.getLog(ReponsesRepositoryTestCase.class);

	private CaseDistributionService		distributionService;

	private MailboxService				correspMailboxService;

	private UserManager					userManager;

	private DocumentRoutingService		routingService;

	private DossierDistributionService	dossierDistributionService;
	
	private DossierSignatureService	dossierSignatureService;

	private DocumentModel				feuilleRootModelFolder		= null;

	protected static final String		USER_DIRECTORY				= "userLdapDirectory";

	protected static final String		GROUP_DIRECTORY				= "groupLdapDirectory";

	@Override
	protected void deployRepositoryContrib() throws Exception {
		super.deployRepositoryContrib();

		// deploy api and core bundles
		deployContrib("fr.dila.reponses.core.tests", "OSGI-INF/test-sql-directories-contrib.xml");

		deployBundle("fr.dila.reponses.core");
		deployContrib("fr.dila.reponses.core.tests", "OSGI-INF/test-st-computedgroups-contrib.xml");
		deployContrib("fr.dila.reponses.core.tests", "OSGI-INF/test-feuille-route-ecm-type-contrib.xml");
		deployContrib("fr.dila.reponses.core.tests", "OSGI-INF/test-event-contrib.xml");
		deployContrib("fr.dila.reponses.core.tests", "OSGI-INF/test-reponses-usermanager-contrib.xml");

		// deployContrib("fr.dila.reponses.core.tests", "ldap/LDAPDirectoryFactory.xml");
		// deployContrib("fr.dila.reponses.core.tests", "ldap/default-ldap-users-directory-bundle.xml");
		// deployContrib("fr.dila.reponses.core.tests", "ldap/default-ldap-groups-directory-bundle.xml");
		// deployContrib("fr.dila.reponses.core.tests", "OSGI-INF/test-personalmailbox-creator-corresp-contrib.xml");

		deployContrib("fr.dila.reponses.core.tests", "ldap/DirectoryTypes.xml");
		deployContrib("fr.dila.reponses.core.tests", "ldap/LDAPDirectoryFactory.xml");
		deployContrib("fr.dila.reponses.core.tests", "ldap/default-ldap-users-directory-bundle.xml");
		deployContrib("fr.dila.reponses.core.tests", "ldap/default-ldap-groups-directory-bundle.xml");
		deployContrib("fr.dila.reponses.core.tests", "default-repository-config.xml");

		deployContrib("fr.dila.reponses.core.tests", "OSGI-INF/test-mockministeres-framework.xml");
		deployContrib("fr.dila.reponses.core.tests", "OSGI-INF/test-mockusanddirection-framework.xml");
		deployContrib("fr.dila.reponses.core.tests", "OSGI-INF/test-mockpostes-framework.xml");

		deployLDAPServer();
		getLDAPDirectory(USER_DIRECTORY).setTestServer(SERVER);
		getLDAPDirectory(UNITE_STRUCTURELLE_DIRECTORY).setTestServer(SERVER);
		getLDAPDirectory(POSTE_DIRECTORY).setTestServer(SERVER);
		getLDAPDirectory(GROUP_DIRECTORY).setTestServer(SERVER);

		LDAPSession session = (LDAPSession) getLDAPDirectory(POSTE_DIRECTORY).getSession();
		try {
			DirContext ctx = session.getContext();
			loadDataFromLdif("test-ldap.ldif", ctx);
		} finally {
			session.close();
		}

	}

	@Override
	public void setUp() throws Exception {
		LOG.debug("ENTER SETUP");
		super.setUp();

		CaseManagementDistributionTypeService correspDistributionTypeService = Framework
				.getService(CaseManagementDistributionTypeService.class);
		assertNotNull(correspDistributionTypeService);

		correspMailboxService = ReponsesServiceLocator.getMailboxService();
		assertNotNull(correspMailboxService);

		userManager = STServiceLocator.getUserManager();
		assertNotNull(userManager);

		distributionService = STServiceLocator.getCaseDistributionService();
		assertNotNull(distributionService);

		dossierDistributionService = ReponsesServiceLocator.getDossierDistributionService();
		assertNotNull(dossierDistributionService);
		
		dossierSignatureService = ReponsesServiceLocator.getDossierSignatureService();
		assertNotNull(dossierSignatureService);

		routingService = SSServiceLocator.getDocumentRoutingService();
		assertNotNull(routingService);

		LOG.debug("EXIT SETUP");
	}

	protected DocumentModel createDocument(String type, String id) throws Exception {
		DocumentModel document = session.createDocumentModel(type);
		document.setPathInfo("/", id);
		document = session.createDocument(document);
		return document;
	}

	/**
	 * Get or create mailbox
	 * 
	 * @param name
	 *            user name
	 * @return personal mailbox of user
	 * @throws Exception
	 */
	public Mailbox getPersonalMailbox(String name) {
		final Mailbox mailbox = correspMailboxService.getUserPersonalMailbox(session, name);
		if (mailbox == null) {
			return correspMailboxService.createPersonalMailboxes(session, name).get(0);
		} else {
			return mailbox;
		}
	}

	public DocumentModel createDocumentModel(CoreSession session, String name, String type, String path)
			throws ClientException {
		DocumentModel doc = session.createDocumentModel(path, name, type);
		doc.setPropertyValue(DocumentRoutingConstants.TITLE_PROPERTY_NAME, name);
		return session.createDocument(doc);
	}

	public DocumentModel createBasicDocumentModel(CoreSession session, String name, String type, String path)
			throws ClientException {
		DocumentModel doc = session.createDocumentModel(path, name, type);
		return session.createDocument(doc);
	}

	/**
	 * Crée si ce n'est pas deja fait le dossier qui va contenir les modeles de feuilles de root Les modeles de feuilles
	 * de route sont recherche comme enfant d'un document unique de type FeuilleRouteModelFolder
	 * 
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	public DocumentModel createOrGetFeuilleRouteModelFolder(CoreSession session) throws ClientException {
		if (feuilleRootModelFolder == null) {
			feuilleRootModelFolder = createDocumentModel(session, FeuilleRootModelFolderName,
					"FeuilleRouteModelFolder", "/");
		}
		return feuilleRootModelFolder;

	}

	protected DocumentModel createSerialStep(CoreSession session, DocumentModel parent, String userMailboxId,
			String stepTitle, String stepType) throws Exception {
		DocumentModel step = FeuilleRouteTestHelper.createSerialStep(session, parent, userMailboxId, stepTitle,
				stepType);
		return step;
	}

	protected DocumentModel createFeuilleRoute(CoreSession session, String name) throws ClientException {
		DocumentModel fdrRoot = createOrGetFeuilleRouteModelFolder(session);
		DocumentModel route = createDocumentModel(session, name, STConstant.FEUILLE_ROUTE_DOCUMENT_TYPE,
				fdrRoot.getPathAsString());
		ReponsesFeuilleRoute feuilleRoute = route.getAdapter(ReponsesFeuilleRoute.class);
		assertNotNull(feuilleRoute);
		feuilleRoute.setFeuilleRouteDefaut(true);
		route = session.saveDocument(feuilleRoute.getDocument());
		return route;
	}

	public DocumentRoute createComplexRoute(CoreSession session) throws Exception {
		DocumentModel fdrroot = createOrGetFeuilleRouteModelFolder(session);

		DocumentModel route = createDocumentModel(session, "route",
				DocumentRoutingConstants.DOCUMENT_ROUTE_DOCUMENT_TYPE, fdrroot.getPathAsString());
		DocumentModel step1 = createDocumentModel(session, "step1", STConstant.ROUTE_STEP_DOCUMENT_TYPE,
				route.getPathAsString());
		Mailbox user2Mailbox = getPersonalMailbox(user2);
		step1.setPropertyValue(CaseConstants.STEP_DISTRIBUTION_MAILBOX_ID_PROPERTY_NAME, user2Mailbox.getId());
		session.saveDocument(step1);

		DocumentModel step2 = createDocumentModel(session, "step2", STConstant.ROUTE_STEP_DOCUMENT_TYPE,
				route.getPathAsString());
		step2.setPropertyValue(CaseConstants.STEP_DISTRIBUTION_MAILBOX_ID_PROPERTY_NAME, user2Mailbox.getId());
		session.saveDocument(step2);
		DocumentModel parallelFolder1 = createDocumentModel(session, "parallelFolder1",
				STConstant.STEP_FOLDER_DOCUMENT_TYPE, route.getPathAsString());
		parallelFolder1.setPropertyValue(DocumentRoutingConstants.EXECUTION_TYPE_PROPERTY_NAME,
				DocumentRoutingConstants.ExecutionTypeValues.parallel.name());
		session.saveDocument(parallelFolder1);

		DocumentModel step31 = createDocumentModel(session, "step31", STConstant.ROUTE_STEP_DOCUMENT_TYPE,
				parallelFolder1.getPathAsString());
		step31.setPropertyValue(CaseConstants.STEP_DISTRIBUTION_MAILBOX_ID_PROPERTY_NAME, user2Mailbox.getId());
		session.saveDocument(step31);

		DocumentModel step32 = createDocumentModel(session, "step32", STConstant.ROUTE_STEP_DOCUMENT_TYPE,
				parallelFolder1.getPathAsString());
		step32.setPropertyValue(CaseConstants.STEP_DISTRIBUTION_MAILBOX_ID_PROPERTY_NAME, user2Mailbox.getId());
		session.saveDocument(step32);

		DocumentModel serialFolder = createDocumentModel(session, "serialFolder1",
				STConstant.STEP_FOLDER_DOCUMENT_TYPE, parallelFolder1.getPathAsString());
		serialFolder.setPropertyValue(DocumentRoutingConstants.EXECUTION_TYPE_PROPERTY_NAME,
				DocumentRoutingConstants.ExecutionTypeValues.serial.name());
		session.saveDocument(serialFolder);

		DocumentModel step41 = createDocumentModel(session, "step41", STConstant.ROUTE_STEP_DOCUMENT_TYPE,
				serialFolder.getPathAsString());
		step41.setPropertyValue(CaseConstants.STEP_DISTRIBUTION_MAILBOX_ID_PROPERTY_NAME, user2Mailbox.getId());
		session.saveDocument(step41);

		DocumentModel step42 = createDocumentModel(session, "step42", STConstant.ROUTE_STEP_DOCUMENT_TYPE,
				serialFolder.getPathAsString());
		step42.setPropertyValue(CaseConstants.STEP_DISTRIBUTION_MAILBOX_ID_PROPERTY_NAME, user2Mailbox.getId());
		step42.setPropertyValue(CaseConstants.STEP_DISTRIBUTION_DUE_DATE_PROPERTY_NAME, new Date());
		step42.setPropertyValue(CaseConstants.STEP_DISTRIBUTION_AUTOMATIC_VALIDATION_PROPERTY_NAME, true);
		session.saveDocument(step42);

		session.saveDocument(parallelFolder1);
		session.save();

		return route.getAdapter(DocumentRoute.class);
	}

	/**
	 * Valide une feuille de route.
	 * 
	 * @param route
	 *            Feuille de route
	 * @throws ClientException
	 */
	protected void validateRoute(DocumentRoute route) throws ClientException {
		routingService.lockDocumentRoute(route, session);
		route = routingService.validateRouteModel(route, session);
		session.saveDocument(route.getDocument());
		session.save();
		routingService.unlockDocumentRoute(route, session);

		Framework.getLocalService(EventService.class).waitForAsyncCompletion();

		assertEquals("validated", route.getDocument().getCurrentLifeCycleState());
		DocumentModelList docModelList = session.getChildren(route.getDocument().getRef());
		if (docModelList.size() > 0) {
			assertEquals("validated", docModelList.get(0).getCurrentLifeCycleState());
		}
	}

	protected Dossier createDossier() throws Exception {
		DocumentModel questionDocumentModel = createDocument(DossierConstants.QUESTION_DOCUMENT_TYPE, "newQuestionTest");
		Question question = questionDocumentModel.getAdapter(Question.class);
		DocumentModel dossierDocumentModel = createDocument(DossierConstants.DOSSIER_DOCUMENT_TYPE, "newDossierTest");

		// check properties
		Long numQuestionLong = 15523L;
		String typeQuestion = VocabularyConstants.QUESTION_TYPE_QE;
		String texteQuestion = "quelle heure est il ?";
		Calendar date = GregorianCalendar.getInstance();
		String nomAuteur = "Valjean";
		String prenomAuteur = "Jean";

		question.setNumeroQuestion(numQuestionLong);
		question.setTypeQuestion(typeQuestion);
		question.setEtatQuestion(session, VocabularyConstants.ETAT_QUESTION_EN_COURS, new GregorianCalendar(), "10");
		question.setTexteQuestion(texteQuestion);
		question.setDateReceptionQuestion(date);
		question.setNomAuteur(nomAuteur);
		question.setPrenomAuteur(prenomAuteur);

		Dossier dossier = dossierDocumentModel.getAdapter(Dossier.class);
		dossier = dossierDistributionService.createDossier(session, dossier, question, null,
				VocabularyConstants.ETAT_QUESTION_EN_COURS);

		assertNotNull(dossier);
		assertNotNull(dossier.getDocument().getId());
		assertEquals(dossier.getQuestion(session).getTypeQuestion(), typeQuestion);
		assertEquals(dossier.getQuestion(session).getNumeroQuestion(), numQuestionLong);

		return dossier;
	}

	protected List<CaseLink> getReceivedCaseLinksNotDeleted(String user) throws Exception {
		NuxeoPrincipal principal = userManager.getPrincipal(user);

		closeSession();
		session = openSessionAs(principal);

		Mailbox userMailbox = getPersonalMailbox(user);
		List<CaseLink> links = distributionService.getReceivedCaseLinks(session, userMailbox, 0, 0);
		List<CaseLink> linkNotDeleted = new ArrayList<CaseLink>();
		for (CaseLink l : links) {
			String currentState = session.getCurrentLifeCycleState(l.getDocument().getRef());
			if (!"deleted".equals(currentState)) {
				linkNotDeleted.add(l);
			}
		}
		return linkNotDeleted;
	}

	/**
	 * Vérifie la présence d'un CaseLink dans la mailbox d'un utilisateur.
	 * 
	 * @param user
	 *            Nom de l'utilisateur
	 * @param actionnable
	 *            Vrai si le caselink doit être actionnable
	 * @throws Exception
	 *             Exception
	 */
	protected void verifyCaseLinkPresent(String user, boolean actionnable) throws Exception {

		List<CaseLink> links = getReceivedCaseLinksNotDeleted(user);

		assertEquals("Présence d'un CaseLink dans la mailbox de l'utilisateur <" + user + ">", 1, links.size());

		CaseLink link = links.get(0);
		if (actionnable) {
			assertTrue(link.isActionnable());
		} else {
			assertFalse(link.isActionnable());
		}
	}

	/**
	 * Vérifie l'absence d'un CaseLink dans la mailbox d'un utilisateur.
	 * 
	 * @param user
	 *            Nom de l'utilisateur
	 * @throws Exception
	 *             Exception
	 */
	protected void verifyCaseLinkAbsent(String user) throws Exception {
		List<CaseLink> links = getReceivedCaseLinksNotDeleted(user);
		assertEquals("Absence de CaseLink dans la mailbox de l'utilisateur <" + user + ">", 0, links.size());
	}

	protected DossierDistributionService getDossierDistributionService() {
		return dossierDistributionService;
	}
	
	protected DossierSignatureService getDossierSignatureService() {
		return dossierSignatureService;
	}

	protected DocumentRoutingService getRoutingService() {
		return routingService;
	}

	protected UserManager getUserManager() {
		return userManager;
	}

	protected CaseDistributionService getDistributionService() {
		return distributionService;
	}

	public DocumentModel getFeuilleRootModelFolder() {
		return feuilleRootModelFolder;
	}

}
