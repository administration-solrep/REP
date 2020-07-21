package fr.dila.reponses.core.suivi;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PathRef;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.flux.Signalement;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;
import fr.dila.st.api.requeteur.RequeteExperte;
import fr.dila.st.api.service.RequeteurService;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Classe de test pour les requêtes préparamétrées définies dans la contribution reponses-content-template.xml
 * 
 * @author jgomez
 * 
 */
public class TestRequetePreparametre extends ReponsesRepositoryTestCase {

	private RequeteurService	rs;

	private static final Log	log			= LogFactory.getLog(TestRequetePreparametre.class);

	public String				BIBLIOPATH	= "/case-management/biblio-requetes-root/";

	public enum RequetePreparam {
		R2("r2-questions-etape-redaction-15joursapres-date-publication", 2), R3(
				"r3-questions-etape-redaction-30joursapres-date-publication", 1), R4(
				"r4-questions-etape-signature-25joursapres-date-publication", 4), R5(
				"r5-questions-etape-signature-45joursapres-date-publication", 1), R6(
				"r6-questions-deposees-moins-un-moi-plus-de-deux-reaffectations", 1), R11(
				"r11-questions-sans-reponse-plus-un-mois", 3), R12("r12-questions-signalées-sans-reponse", 1);

		private String	name;
		private int		expectedResults;

		RequetePreparam(String name, int expectedResults) {
			this.name = name;
			this.expectedResults = expectedResults;
		}

		public String getName() {
			return this.name;
		}

		public int getExpectedResults() {
			return this.expectedResults;
		}
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		deployContrib("fr.dila.reponses.core.tests", "OSGI-INF/test-requetes-preparametrees-contrib.xml");
		rs = STServiceLocator.getRequeteurService();
		openSession();
		createDossierTrouveParRequeteR2();
		createDossierTrouveParRequeteR3();
		createDossierTrouveParRequeteR4();
		createDossierTrouveParRequeteR5();
		createDossierTrouveParRequeteR6();
		createDossierTrouveParRequeteR11();
		createDossierTrouveParRequeteR12();
		closeSession();
	}

	public void testInit() throws ClientException {
		openSession();
		// Root
		DocumentModel biblio = getBiblio();
		displayRequetes(biblio);
		for (RequetePreparam r : RequetePreparam.values()) {
			valideResultatsRequete(r);
		}
		closeSession();
	}

	private void createDossierTrouveParRequeteR2() throws Exception {
		Dossier dossier = createDossier();
		assertFalse("Le flag du dossier relatif à l'étape de redaction doit être à faux",
				dossier.getEtapeRedactionAtteinte());
		// Positionnement d'une date de publication plus de 17 jours avant maintenant
		DateTime datePublication = new DateTime();
		datePublication = datePublication.minusDays(17);
		Question question = dossier.getQuestion(session);
		question.setDatePublicationJO(datePublication.toCalendar(Locale.FRENCH));
		session.saveDocument(question.getDocument());
		session.saveDocument(dossier.getDocument());
		session.save();
	}

	private void createDossierTrouveParRequeteR3() throws Exception {
		Dossier dossier = createDossier();
		assertFalse("Le flag du dossier relatif à l'étape de redaction doit être à faux",
				dossier.getEtapeRedactionAtteinte());
		// Positionnement d'une date de publication plus de 33 jours avant maintenant
		DateTime datePublication = new DateTime();
		datePublication = datePublication.minusDays(33);
		Question question = dossier.getQuestion(session);
		question.setDatePublicationJO(datePublication.toCalendar(Locale.FRENCH));
		session.saveDocument(question.getDocument());
		session.saveDocument(dossier.getDocument());
		session.save();
	}

	private void createDossierTrouveParRequeteR4() throws Exception {
		Dossier dossier = createDossier();
		dossier.setEtapeRedactionAtteinte(true);
		assertFalse("Le flag du dossier relatif à l'étape de signature doit être à faux",
				dossier.getEtapeSignatureAtteinte());
		// Positionnement d'une date de publication plus de 28 jours avant maintenant
		DateTime datePublication = new DateTime();
		datePublication = datePublication.minusDays(28);
		Question question = dossier.getQuestion(session);
		question.setDatePublicationJO(datePublication.toCalendar(Locale.FRENCH));
		session.saveDocument(question.getDocument());
		session.saveDocument(dossier.getDocument());
		session.save();
	}

	private void createDossierTrouveParRequeteR5() throws Exception {
		Dossier dossier = createDossier();
		dossier.setEtapeRedactionAtteinte(true);
		assertFalse("Le flag du dossier relatif à l'étape de signature doit être à faux",
				dossier.getEtapeSignatureAtteinte());
		// Positionnement d'une date de publication plus de 50 jours avant maintenant
		DateTime datePublication = new DateTime();
		datePublication = datePublication.minusDays(50);
		Question question = dossier.getQuestion(session);
		question.setDatePublicationJO(datePublication.toCalendar(Locale.FRENCH));
		session.saveDocument(question.getDocument());
		session.saveDocument(dossier.getDocument());
		session.save();
	}

	private void createDossierTrouveParRequeteR6() throws Exception {
		Dossier dossier = createDossier();
		dossier.setEtapeRedactionAtteinte(true);
		dossier.setReaffectionCount(3L);
		DateTime datePublication = new DateTime();
		// Une date de publication de moins d'un mois
		datePublication = datePublication.minusDays(20);
		Question question = dossier.getQuestion(session);
		question.setDatePublicationJO(datePublication.toCalendar(Locale.FRENCH));
		session.saveDocument(question.getDocument());
		session.saveDocument(dossier.getDocument());
		session.save();
	}

	private void createDossierTrouveParRequeteR11() throws Exception {
		Dossier dossier = createDossier();
		dossier.setEtapeRedactionAtteinte(true);
		DateTime datePublication = new DateTime();
		// Une date de publication de plus d'un mois
		datePublication = datePublication.minusDays(40);
		Question question = dossier.getQuestion(session);
		question.setDatePublicationJO(datePublication.toCalendar(Locale.FRENCH));
		session.saveDocument(question.getDocument());
		session.saveDocument(dossier.getDocument());
		session.save();
	}

	private void createDossierTrouveParRequeteR12() throws Exception {
		Dossier dossier = createDossier();
		dossier.setEtapeRedactionAtteinte(true);
		String delaiQuestionSignalee = "12";
		Calendar dateSignalement = GregorianCalendar.getInstance();
		
		// Une date de publication de plus d'un mois
		Question question = dossier.getQuestion(session);
		question.setEtatQuestion(session, VocabularyConstants.ETAT_QUESTION_SIGNALEE, dateSignalement, delaiQuestionSignalee);
		DateTime datePublication = new DateTime();
		datePublication = datePublication.minusDays(5);
		question.setDatePublicationJO(datePublication.toCalendar(Locale.FRENCH));
		assertFalse("La question ne doit pas avoir de réponse publiée",
				question.getEtatQuestion(session).equals("repondu"));
		assertTrue("La question doit être dans un état signalé", question.getEtatSignale());
		
		List<Signalement> signalements = question.getSignalements();
		assertFalse(signalements.isEmpty());
		Signalement signalement = signalements.get(0);
		assertEquals(dateSignalement.getTime(),signalement.getDateEffet());
		dateSignalement.add(Calendar.DAY_OF_YEAR, Integer.parseInt(delaiQuestionSignalee));
		assertEquals(dateSignalement.getTime(),signalement.getDateAttendue());
		
		session.saveDocument(question.getDocument());
		session.saveDocument(dossier.getDocument());
		session.save();
	}

	private void valideResultatsRequete(RequetePreparam requete) throws ClientException {
		DocumentModel requeteR2Doc = session.getDocument(new PathRef(BIBLIOPATH + requete.getName()));
		List<DocumentModel> results = getResults(requeteR2Doc);
		assertEquals("La requête " + requete.getName() + " attend " + requete.getExpectedResults() + " et non "
				+ results.size(), requete.getExpectedResults(), results.size());
	}

	private List<DocumentModel> getResults(DocumentModel requeteR2Doc) throws ClientException {
		RequeteExperte requeteR2 = requeteR2Doc.getAdapter(RequeteExperte.class);
		List<DocumentModel> results = rs.query(session, requeteR2);
		log.error(requeteR2Doc.getName());
		log.error(rs.getPattern(session, requeteR2));
		return results;
	}

	private void displayRequetes(DocumentModel biblio) throws ClientException {
		DocumentModelList requetes = session.getChildren(biblio.getRef());
		for (DocumentModel requeteDoc : requetes) {
			log.error(requeteDoc.getName());
		}
	}

	private DocumentModel getBiblio() throws ClientException {
		DocumentModel biblio = session.getDocument(new PathRef(BIBLIOPATH));
		return biblio;
	}
}
