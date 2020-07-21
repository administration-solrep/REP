package fr.dila.reponses.webtest.tests.webdriver10;

import java.rmi.UnexpectedException;
import java.util.List;

import javax.mail.Message;
import javax.xml.bind.JAXBException;

import junit.framework.Assert;
import fr.dila.reponses.webtest.helper.AbstractWebTest;
import fr.dila.reponses.webtest.helper.ReponsesImapHelper;
import fr.dila.reponses.webtest.pages.CorbeillePage;
import fr.dila.reponses.webtest.pages.administration.GestionRestrictionAccesPage;
import fr.dila.reponses.webtest.pages.dossier.AllotissementOnglet;
import fr.dila.reponses.webtest.pages.dossier.DossierPage;
import fr.dila.reponses.webtest.pages.login.LoginPage;
import fr.dila.reponses.webtest.tests.webtestassert.WebTestAssert;
import fr.dila.st.annotations.TestDocumentation;
import fr.sword.naiad.commons.webtest.WebPage;
import fr.sword.naiad.commons.webtest.annotation.WebTest;
import fr.sword.naiad.commons.webtest.mail.ImapConsult;

/**
 * Test de la restriction d'accès
 * @author jbrunet
 *
 */
public class Test25AccesRestreint extends AbstractWebTest{

	private static final String UTILISATEUR_BDC = "bdc";
	private static final String UTILISATEUR_ADMIN = "adminsgg";
	private static final String DESCRIPTION_ACCES_RESTREINT = "Accès restreint";
	private static final String MESSAGE_ACCES_NON_AUTORISE = "Utilisateur non autorisé";
	private static final String ACCES_DOSSIER = "@view_cm_case";

	@WebTest(description = "Test sur la restriction d'accès")
	@TestDocumentation(categories={"Utilisateur", "Administration"})
	public void accesRestreint() throws Exception {
		getFlog().startAction("Préparation des tests de la restriction d'accès");
		preparatifsTests();
		getFlog().endAction();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			throw new UnexpectedException("Timer interrompu");
		}
		getFlog().startAction("Tests d'ouverture de dossier via lien mail");
		accesDossierViaLien();
		getFlog().endAction();
		getFlog().startAction("Mise en place de la restriction d'accès");
		testMiseEnPlaceRestriction();
		getFlog().endAction();
		getFlog().startAction("Connexion avec administrateur fonctionnel pendant la restriction d'accès");
		testConnexionAdminAvecRestriction();
		getFlog().endAction();
		getFlog().startAction("Connexion avec bdc pendant la restriction d'accès");
		testConnexionBdcAvecRestriction();
		getFlog().endAction();
		getFlog().startAction("Test liens direct bdc pendant la restriction d'accès");
		testConnexionBdcAvecRestrictionDepuisLien();
		getFlog().endAction();
		getFlog().startAction("Retrait de la restriction d'accès");
		testRetraitRestriction();
		getFlog().endAction();
		getFlog().startAction("Vérification du retrait de la restriction d'accès");
		testRestrictionRetiree();
		getFlog().endAction();		
	}
	
	private void preparatifsTests() throws JAXBException, Exception {
		
		// Nettoyage boite mail
		getFlog().startAction("Nettoyage de la boîte mail");
		ReponsesImapHelper mailHelper = ReponsesImapHelper.getInstance();
		ImapConsult imap = mailHelper.initNewImapConsult();
		imap.connect();
		mailHelper.clearInbox(imap);
		getFlog().endAction();
		
		// Création d'un nouveau dossier
		getFlog().startAction("Injection d'une nouvelle question");
		injectAN("/injection/an/022_injection_question_an_AccesRestreint.xml");
		getFlog().endAction();
	}

	private void accesDossierViaLien() throws Exception {
		// Récupération du mèl pour obtenir le lien
		getFlog().startAction("Récupération du lien");
		ReponsesImapHelper mailHelper = ReponsesImapHelper.getInstance();
		ImapConsult imap = mailHelper.initNewImapConsult();
		imap.connect();
		Message[] messages = mailHelper.waitForTaskMessages(imap);
		Assert.assertEquals("Il doit y avoir un message et un seul dans la boite mail", 1, messages.length);
		Message taskMessage = messages[0];
		String url = mailHelper.getDossierLink(taskMessage);
		getFlog().action("Le lien suivant vers le dossier a été récupéré : " + url );
		getFlog().endAction();
		
		// Accès au dossier
		getFlog().startAction("Accès au lien vers le dossier");
		url = url + ACCES_DOSSIER;
		LoginPage loginPage = WebPage.goToPage(getDriver(),getFlog(), url, LoginPage.class);
		getFlog().endAction();
		getFlog().startAction("Tentative d'accès au dossier");
		DossierPage dossierPage = loginPage.submitLogin(UTILISATEUR_BDC, UTILISATEUR_BDC, DossierPage.class);
		AllotissementOnglet allotissement = dossierPage.goToOngletAllotissement();
		List<String> dossierIds = allotissement.getDossierIds();
		Assert.assertTrue("Aucun allotissement ne doit avoir eu lieu sur ce dossier", dossierIds.isEmpty());
		logout();
		getFlog().endAction();
	}
	
	/**
	 * Mise en place de la restriction d'accès
	 */
	private void testMiseEnPlaceRestriction() {
		getFlog().startAction("Activation de la restriction d'accès");
		CorbeillePage corbeillePage = loginAsAdmin();
		GestionRestrictionAccesPage gestionRestrictionAccesPage = corbeillePage.getBandeauMenu().goToAdministration().getMenuVoletGauche().goToRestrictionAcces();
		gestionRestrictionAccesPage.activerRestrictionAcces();
		gestionRestrictionAccesPage.setDescriptionRestrictionAcces("Accès restreint");
		gestionRestrictionAccesPage.enregistrerChangements();
		getFlog().endAction();
		logout();
	}
	
	private void testConnexionAdminAvecRestriction() {
		getFlog().startAction("Restriction d'accès activée : connexion avec administrateur fonctionnel");
		LoginPage loginPage = goToLoginPage();
		WebTestAssert.assertTrue(getFlog(), "La restriction d'accès est en place", "Le message de restriction d'accès n'est pas visible", loginPage.isTextPresent(DESCRIPTION_ACCES_RESTREINT));
		CorbeillePage corbeillePage = loginAsAdmin();
		WebTestAssert.assertTrue(getFlog(), "Utilisateur connecté", "Utilisateur non connecté",corbeillePage.isConnected(UTILISATEUR_ADMIN));
		getFlog().endAction();
		logout();
	}
	
	private void testConnexionBdcAvecRestriction() {
		getFlog().startAction("Restriction d'accès activée : connexion avec bdc");
		LoginPage loginPage = goToLoginPage();
		WebTestAssert.assertTrue(getFlog(), "La restriction d'accès est en place", "Le message de restriction d'accès n'est pas visible", loginPage.isTextPresent(DESCRIPTION_ACCES_RESTREINT));
		loginPage = login(UTILISATEUR_BDC, UTILISATEUR_BDC, LoginPage.class);
		WebTestAssert.assertTrue(getFlog(), "La restriction d'accès est en place", "Le message de restriction d'accès n'est pas visible", loginPage.isTextPresent(DESCRIPTION_ACCES_RESTREINT));
		WebTestAssert.assertTrue(getFlog(), "Utilisateur non autorisé", "Message de non autorisation absent", loginPage.isTextPresent(MESSAGE_ACCES_NON_AUTORISE));
		getFlog().endAction();
		logout();
	}
	
	private void testConnexionBdcAvecRestrictionDepuisLien() throws Exception {
		// Récupération du mèl pour obtenir le lien
		getFlog().startAction("Récupération du lien");
		ReponsesImapHelper mailHelper = ReponsesImapHelper.getInstance();
		ImapConsult imap = mailHelper.initNewImapConsult();
		imap.connect();
		Message[] messages = mailHelper.waitForTaskMessages(imap);
		Assert.assertEquals("Il y avoir un message et un seul dans la boite mail", 1, messages.length);
		Message taskMessage = messages[0];
		String url = mailHelper.getDossierLink(taskMessage);
		getFlog().action("Le lien suivant vers le dossier a été récupéré : " + url );
		getFlog().endAction();
		
		// Accès au dossier
		getFlog().startAction("Accès au lien vers le dossier");
		url = url + ACCES_DOSSIER;
		LoginPage loginPage = WebPage.goToPage(getDriver(),getFlog(), url, LoginPage.class);
		getFlog().endAction();
		getFlog().startAction("Tentative d'accès au dossier");
		WebTestAssert.assertTrue(getFlog(), "La restriction d'accès est en place", "Le message de restriction d'accès n'est pas visible", loginPage.isTextPresent(DESCRIPTION_ACCES_RESTREINT));
		loginPage.submitLogin(UTILISATEUR_BDC, UTILISATEUR_BDC, LoginPage.class);
		WebTestAssert.assertTrue(getFlog(), "La restriction d'accès est en place", "Le message de restriction d'accès n'est pas visible", loginPage.isTextPresent(DESCRIPTION_ACCES_RESTREINT));
		WebTestAssert.assertTrue(getFlog(), "Utilisateur non autorisé", "Message de non autorisation absent", loginPage.isTextPresent(MESSAGE_ACCES_NON_AUTORISE));
		getFlog().endAction();
		logout();
	}
	
	private void testRetraitRestriction() {
		getFlog().startAction("Désactivation de la restriction d'accès");
		CorbeillePage corbeillePage = loginAsAdmin();
		GestionRestrictionAccesPage gestionRestrictionAccesPage = corbeillePage.getBandeauMenu().goToAdministration().getMenuVoletGauche().goToRestrictionAcces();
		gestionRestrictionAccesPage.desactiverRestrictionAcces();
		gestionRestrictionAccesPage.setDescriptionRestrictionAcces("");
		gestionRestrictionAccesPage.enregistrerChangements();
		getFlog().endAction();
		logout();
	}
	
	private void testRestrictionRetiree() {
		getFlog().startAction("Vérification de la désactivation de la restriction d'accès");
		LoginPage loginPage = goToLoginPage();
		WebTestAssert.assertFalse(getFlog(), "La restriction d'accès n'est plus en place", "Le message de restriction d'accès est toujours visible", loginPage.isTextPresent(DESCRIPTION_ACCES_RESTREINT));
		getFlog().endAction();
		logout();
	}
	
}
