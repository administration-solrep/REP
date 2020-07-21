package fr.dila.reponses.webtest.tests.webdriver10;

import java.io.InputStream;
import java.rmi.UnexpectedException;

import org.openqa.selenium.By;

import fr.dila.reponses.rest.api.WSControle;
import fr.dila.reponses.rest.api.WSReponse;
import fr.dila.reponses.webtest.constant.ConstantesEtapeType;
import fr.dila.reponses.webtest.constant.ConstantesLotQuestion;
import fr.dila.reponses.webtest.constant.ConstantesOrga;
import fr.dila.reponses.webtest.helper.AbstractWebTest;
import fr.dila.reponses.webtest.helper.ReponsesImapHelper;
import fr.dila.reponses.webtest.model.Etape;
import fr.dila.reponses.webtest.model.EtapeBranche;
import fr.dila.reponses.webtest.model.EtapeSerie;
import fr.dila.reponses.webtest.model.FeuilleRoute;
import fr.dila.reponses.webtest.model.FeuilleRouteCatalogue;
import fr.dila.reponses.webtest.model.UserManager;
import fr.dila.reponses.webtest.pages.CorbeillePage;
import fr.dila.reponses.webtest.pages.dossier.DossierConnexeOnglet;
import fr.dila.reponses.webtest.pages.dossier.DossierPage;
import fr.dila.reponses.webtest.pages.dossier.FeuilleRouteOnglet;
import fr.dila.reponses.webtest.pages.dossier.ParapheurOnglet;
import fr.dila.reponses.webtest.utils.AutomationUtils;
import fr.dila.reponses.webtest.utils.WsUtils;
import fr.dila.st.annotations.TestDocumentation;
import fr.dila.st.webdriver.model.STUser;
import fr.sword.naiad.commons.webtest.annotation.WebTest;
import fr.sword.naiad.commons.webtest.mail.ImapConsult;
import fr.sword.xsd.reponses.ChercherReponsesRequest;
import fr.sword.xsd.reponses.ChercherReponsesResponse;
import fr.sword.xsd.reponses.ControlePublicationRequest;
import fr.sword.xsd.reponses.ControlePublicationResponse;
import junit.framework.Assert;

/**
 * Tests pour vérifier le bon traitement des questions
 * 
 * @author j
 * 
 */
public class Test07TraitementQuestions extends AbstractWebTest {

	public static final int		NUMERO_QUESTION	= 314174;
	private static final String	AN_314174		= "AN 314174";

	@WebTest(description = "1.2.2 Traitement des questions - vérification du déroulement de la feuille de route", order = 10)
	@TestDocumentation(categories = { "FDR", "Dossier" })
	public void verifierDeroulementCompletFeuilleRoute() throws UnexpectedException {
		// Pour chaque personne dans l'ordre de la fdr
		// login
		// vérification du dossier dans la bonne corbeille et click
		// vérifier la présence de la feuille de route
		// vérifier l'étape active
		// écrire un mot
		// valider
		// logout
		FeuilleRoute fdr1 = FeuilleRouteCatalogue.getFDR1();
		String nomDossier = AN_314174;
		for (Etape etape : fdr1.getEtapes()) {
			if (Etape.TYPE_SERIE.equals(etape.getKind())) {
				EtapeSerie etapeSerie = (EtapeSerie) etape;
				traiterEtape(nomDossier, etapeSerie);
			}
			if (Etape.TYPE_BRANCHE.equals(etape.getKind())) {
				EtapeBranche etapeBranche = (EtapeBranche) etape;
				for (Etape subEtape : etapeBranche.getEtapes()) {
					EtapeSerie subEtapeSerie = (EtapeSerie) subEtape;
					traiterEtape(nomDossier, subEtapeSerie);
				}
			}
		}

	}

	@WebTest(description = "1.2.2 Traitement des questions - récupération de la réponse par les assemblées", order = 20)
	@TestDocumentation(categories = { "Assemblées", "WS" })
	public void recuperationQuestionParLesAssemblees() throws Exception {
		WSReponse wsReponses = WsUtils.getWSReponses();
		InputStream stream = getClass().getResourceAsStream("/webservice/chercher_reponses_request.xml");
		ChercherReponsesRequest request = WsUtils.buildRequestFromFile(stream, ChercherReponsesRequest.class);
		Assert.assertNotNull("Le fichier de la question n'est pas à l'emplacement indiqué", stream);
		ChercherReponsesResponse response = wsReponses.chercherReponses(request);
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getReponses());
		Assert.assertEquals(1, response.getReponses().size());
		Assert.assertEquals(NUMERO_QUESTION, response.getReponses().get(0).getIdQuestions().get(0).getNumeroQuestion());
	}

	@WebTest(description = "1.2.2 Traitement des questions - Changement du mail de l'admin technique", useDriver = false, order = 30)
	@TestDocumentation(categories = { "Administration" })
	public void changementMailAdministrateurTechnique() throws Exception {
		AutomationUtils.changerMailAdministrateurTechnique(ReponsesImapHelper.MAILBOX_USER_DEFAULT);
	}

	@WebTest(description = "1.2.2 Traitement des questions - publication de la réponse par la DILA", order = 40)
	@TestDocumentation(categories = { "Publication", "WS" })
	public void publicationParDila() throws Exception {
		WSControle wsControle = WsUtils.getWSControle();
		InputStream stream = getClass().getResourceAsStream("/webservice/controle_publication_request.xml");
		ControlePublicationRequest request = WsUtils.buildRequestFromFile(stream, ControlePublicationRequest.class);
		Assert.assertNotNull("Le fichier de la question n'est pas à l'emplacement indiqué", stream);
		ControlePublicationResponse response = wsControle.controlePublication(request);
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getResultatControleQuestionReponse());
		Assert.assertEquals(1, response.getResultatControleQuestionReponse().size());
		Assert.assertEquals(WsUtils.STATUS_OK, response.getResultatControleQuestionReponse().get(0).getStatut()
				.toString());
	}

	@WebTest(description = "1.2.2 Traitement des questions - vérification du mail concernant les différences entre les réponses", order = 50)
	@TestDocumentation(categories = { "Test vide" })
	public void verificationMailReponseDifferente() throws Exception {
		getFlog().startAction("Réception du mail des différence entre les réponses");
		ReponsesImapHelper mailHelper = ReponsesImapHelper.getInstance();
		ImapConsult imap = mailHelper.initNewImapConsult();
		imap.connect();
		getFlog().endAction();
	}

	@WebTest(description = "1.2.2 Traitement des questions - Rétablir le mail de l'admin technique", useDriver = false, order = 60)
	@TestDocumentation(categories = { "Administration" })
	public void retablirMailAdministrateurTechnique() throws Exception {
		AutomationUtils.changerMailAdministrateurTechnique("admin@dila.fr");
	}

	/**
	 * Traite une étape du dossier nomDossiers
	 * 
	 * @param nomDossier
	 *            le nom du dossier
	 * @param etapeSerie
	 *            l'étape
	 * @throws UnexpectedException
	 * @throws InterruptedException
	 */
	// TODO: A déplacer dans une méthode utilitaire
	private void traiterEtape(String nomDossier, EtapeSerie etapeSerie) throws UnexpectedException {
		String poste = etapeSerie.getDestinataire();
		UserManager userManager = new UserManager();
		STUser sTUser = userManager.getUserFrom(poste);
		if (sTUser == null) {
			throw new UnexpectedException(String.format("L'utilisateur du poste %s n'a pas été trouvé", poste));
		}
		CorbeillePage corbeillePage = login(sTUser.getLogin(), sTUser.getPassword(), CorbeillePage.class);

		// On essaie de trouver la question par la recherche rapide
		DossierPage dossierPage = corbeillePage.getBandeauMenu().rechercheRapide(nomDossier, DossierPage.class);
		if (!dossierPage.containsQuestion(nomDossier)) {
			throw new UnexpectedException(String.format("L'utilisateur %s ne possède pas de question %s",
					sTUser.getLogin(), nomDossier));
		}
		FeuilleRouteOnglet fdrOnglet = dossierPage.goToOngletFDR();

		fdrOnglet.waitForPageSourcePartDisplayed(By.id("related_route_elements"), 60);
		Boolean etapeActive = fdrOnglet.verifierEtapeActive(etapeSerie);
		Assert.assertTrue(
				String.format("L'étape %s chez l'utilisateur %s pour le poste %s n'est pas active",
						etapeSerie.getEtapeType(), sTUser.getLogin(), etapeSerie.getDestinataire()), etapeActive);
		if (ConstantesEtapeType.POUR_REDACTION.equals(etapeSerie.getEtapeType())) {
			// DO nothing
			// corbeillePage.ajouterReponse("Pour des raisons fiscales, le projet envisagé par l'auteur de cette question est complêtement irréaliste ");
		}
		if (ConstantesEtapeType.POUR_SIGNATURE.equals(etapeSerie.getEtapeType())) {
			fdrOnglet.signerEtValiderEtape();
		} else {
			fdrOnglet.validerEtape();
		}
		logout();
	}

	@WebTest(description = "1.2.2 Traitement des questions - test de la copie de la réponse en passant par le dossier connexe", order = 70)
	@TestDocumentation(categories = { "Connexité", "Dossier", "Parapheur", "Recherche" })
	public void copieReponseParDossierConnexe() throws UnexpectedException {
		CorbeillePage corbeillePage = loginAsAdmin();
		String dossierOriginal = ConstantesLotQuestion.DOSSIER_TEST_TRAITEMENT_QUESTION_1;
		String dossierCiblePourCopie = ConstantesLotQuestion.DOSSIER_TEST_TRAITEMENT_QUESTION_2;
		ParapheurOnglet dossierPage = corbeillePage.getBandeauMenu().rechercheRapide(dossierOriginal,
				ParapheurOnglet.class);
		// ParapheurOnglet parapheurOnglet = dossierPage.goToOngletParapheur();
		String keyword = "cyrano";
		Boolean reponseContientKeyword = dossierPage.verifierReponseContient(keyword);
		Assert.assertFalse("Au début du test, la réponse ne doit pas inclure le mot clé", reponseContientKeyword);
		DossierConnexeOnglet dossierConnexeOnglet = dossierPage.goToOngletDossierConnexe();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			throw new UnexpectedException("Timer interrompu");
		}
		Assert.assertEquals(ConstantesLotQuestion.TOTAL_QUESTIONS_ADMIN - 1,
				(int) dossierConnexeOnglet.getNbDossiersConnexes());
		dossierConnexeOnglet.deplieListeQuestionsConnexe(ConstantesOrga.MIN_ECO);
		dossierConnexeOnglet.cliqueSurQuestion(dossierCiblePourCopie);
		dossierConnexeOnglet.copieReponseSurQuestion();

		DossierPage dossierPage2 = corbeillePage.getBandeauMenu().rechercheRapide(dossierOriginal, DossierPage.class);
		ParapheurOnglet parapheurOnglet2 = dossierPage2.goToOngletParapheur();
		Boolean reponsesPartOk = parapheurOnglet2.verifierReponseContient(keyword);
		Assert.assertTrue("La réponse n'a pas été correctement copiée sur la question cible", reponsesPartOk);
		logout();
	}

}
