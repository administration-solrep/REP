package fr.dila.reponses.webtest.tests.webdriver10;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Message;
import javax.mail.MessagingException;

import junit.framework.Assert;
import fr.dila.reponses.webtest.constant.ConstantesLotQuestion;
import fr.dila.reponses.webtest.helper.AbstractWebTest;
import fr.dila.reponses.webtest.helper.ReponsesImapHelper;
import fr.dila.reponses.webtest.pages.CorbeillePage;
import fr.dila.reponses.webtest.pages.recherche.RechercheResultPage;
import fr.dila.reponses.webtest.pages.suivi.SuiviPage;
import fr.dila.reponses.webtest.utils.AutomationUtils;
import fr.dila.st.annotations.TestDocumentation;
import fr.sword.naiad.commons.webtest.annotation.WebTest;
import fr.sword.naiad.commons.webtest.mail.ImapConsult;

/**
 * Test de l'espace de suivi
 * 
 * @author user
 * 
 */
public class Test17Suivi extends AbstractWebTest {

	private static final int	TOTAL_REQUETES_GENERALES	= 12;
	// private static final String USER_BDC = "bdc";
	private static final String	REQUETE_QUESTION			= "requete_question_"
																	+ ConstantesLotQuestion.DOSSIER_TEST_SUIVI_NUMERO;

	@WebTest(description = "Suivi - Ajout d'une requête", order = 10)
	@TestDocumentation(categories = { "Suivi", "Recherche" })
	public void ajoutRequete() {
		CorbeillePage corbeillePage = loginBdc();
		SuiviPage suiviPage = corbeillePage.getBandeauMenu().goToSuivi();
		suiviPage.ajouterLigne(SuiviPage.CHAMP_NUMERO_QUESTION, ConstantesLotQuestion.DOSSIER_TEST_SUIVI_NUMERO);
		RechercheResultPage rechercheResultsPage = suiviPage.lancer();
		Boolean contientDossier = rechercheResultsPage.containsQuestion(ConstantesLotQuestion.DOSSIER_TEST_SUIVI);
		Assert.assertTrue("La recherche experte doit contenir le dossier " + ConstantesLotQuestion.DOSSIER_TEST_SUIVI,
				contientDossier);
		Assert.assertEquals("La recherche experte doit retourner 1 seul résultat ", 1,
				(int) rechercheResultsPage.getResultCount());
		String nomSousRequete = REQUETE_QUESTION;
		suiviPage.sauvegarderRequeteSous(nomSousRequete);
		Assert.assertTrue(nomSousRequete + " n'apparait pas dans les requêtes personnelles",
				suiviPage.contientRequetePerso(nomSousRequete));
		rechercheResultsPage = suiviPage.lancerRequetePerso(nomSousRequete);
		Assert.assertEquals("La recherche experte doit retourner 1 seul résultat ", 1,
				(int) rechercheResultsPage.getResultCount());
		logout();
	}

	@WebTest(description = "Suivi - Ajout d'une alerte", order = 20)
	@TestDocumentation(categories = { "Suivi", "Alerte" })
	public void creationAlerte() {
		CorbeillePage corbeillePage = loginBdc();
		SuiviPage suiviPage = corbeillePage.getBandeauMenu().goToSuivi();
		String nomAlerte = "alerte_sur_" + REQUETE_QUESTION;
		List<String> destinataires = new ArrayList<String>();
		// destinataires.add(USER_BDC);
		destinataires.add(Test02GestionOrga.ID_NOUVEL_UTILISATEUR_NUM_UN);
		suiviPage.creerAlerteSur(REQUETE_QUESTION, nomAlerte, destinataires);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Boolean contientAlerte = suiviPage.contientAlerte(nomAlerte);
		Assert.assertTrue(nomAlerte + " n'apparait pas dans les alertes", contientAlerte);
		logout();
	}

	@WebTest(description = "Suivi - Lancement du batch d'alerte par le nuxeo shell", useDriver = false, order = 30)
	@TestDocumentation(categories = { "Suivi", "Batch", "Alerte" })
	public void declenchementBatchAlertMail() throws Exception {
		AutomationUtils.sendBatchAlerte();
	}

	@WebTest(description = "Suivi - Vérification de chacune des requêtes", order = 40)
	@TestDocumentation(categories = { "Suivi", "Recherche" })
	public void verificationRequetesGenerales() {
		CorbeillePage corbeillePage = loginBdc();
		Map<String, Integer> resultatsReferenceMap = creationResultatReferences();
		SuiviPage suiviPage = corbeillePage.getBandeauMenu().goToSuivi();
		List<String> requetesGenerales = suiviPage.getListeNomsRequetesGenerales();
		Assert.assertEquals(TOTAL_REQUETES_GENERALES, requetesGenerales.size());
		for (String nomRequeteGenerale : requetesGenerales) {
			RechercheResultPage resultPage = suiviPage.lancerRequeteGenerale(nomRequeteGenerale);
			verifierResultatRequeteGenerale(resultatsReferenceMap, nomRequeteGenerale, resultPage.getResultCount());
		}
	}

	// TODO: Vérification du lancement de batch et réception du mail
	@WebTest(description = "Suivi - Réception du mail de l'alerte créé", useDriver = false, order = 50)
	@TestDocumentation(categories = { "Suivi", "Alerte", "Mail" })
	public void receptionMailAlerte() throws MessagingException, IOException {
		getFlog().startAction("Réception du mail de l'alerte");
		ReponsesImapHelper mailHelper = ReponsesImapHelper.getInstance();
		ImapConsult imap = mailHelper.initNewImapConsult();
		imap.connect();
		Message[] messages = mailHelper.waitForAlerteMessages(imap);
		Assert.assertNotNull("Le retour est null", messages);
		Assert.assertTrue("Il n'y a aucun message", messages.length != 0);
		Message alert = messages[0];
		Assert.assertTrue("Le mail ne contient pas le nombre de résultats attendus",
				mailHelper.mailContientUnResultat(alert));
		getFlog().endAction();
	}

	private void verifierResultatRequeteGenerale(Map<String, Integer> resultatsReference, String nomRequeteGenerale,
			Integer resultCount) {
		Assert.assertEquals("La requête " + nomRequeteGenerale + " ne retourne pas le bon résultat ",
				resultatsReference.get(nomRequeteGenerale), resultCount);
	}

	public Map<String, Integer> creationResultatReferences() {
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		resultMap.put("Historique des validations", 9);
		resultMap.put("Liste des questions pas encore à l'étape de rédaction 15 jours après publication", 8);
		resultMap.put("Liste des questions pas encore à l'étape de rédaction 30 jours après publication", 8);
		resultMap.put("Liste des questions pas encore à l'étape de signature 25 jours après publication", 7);
		resultMap.put("Liste des questions pas encore à l'étape de signature 45 jours après publication", 7);
		resultMap
				.put("Liste des questions publiées depuis moins d'un mois et ayant fait l'objet de plus de 2 changements d'affectation",
						0);
		resultMap.put("Liste des demandes d’arbitrages", 1);
		resultMap.put("Liste des questions sans réponse depuis plus d'un mois", 8);
		resultMap.put("Liste des questions signalées n'ayant pas encore obtenu de réponse", 1);
		resultMap.put("Liste des questions transmises à la validation du Premier Ministre", 0);
		resultMap.put("Liste des questions signalées", 1);
		resultMap.put("Liste des questions renouvelées", 0);
		return resultMap;
	}
}
