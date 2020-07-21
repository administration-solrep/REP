package fr.dila.reponses.webtest.tests.webdriver10;

import java.io.IOException;

import javax.mail.MessagingException;

import fr.dila.reponses.webtest.constant.ConstantesOrga;
import fr.dila.reponses.webtest.helper.AbstractWebTest;
import fr.dila.reponses.webtest.helper.ReponsesImapHelper;
import fr.dila.reponses.webtest.pages.CorbeillePage;
import fr.dila.reponses.webtest.pages.administration.utilisateur.RechercheUtilisateurPage;
import fr.dila.reponses.webtest.pages.espaces.EspaceAdministration;
import fr.dila.reponses.webtest.tests.webtestassert.WebTestAssert;
import fr.dila.st.annotations.TestDocumentation;
import fr.sword.naiad.commons.webtest.annotation.WebTest;
import fr.sword.naiad.commons.webtest.mail.ImapConsult;

/**
 * Test de recherche d'utilisateur
 * 
 * @author jgomez
 * 
 */
public class Test21RechercheUtilisateur extends AbstractWebTest {

	private static final String	UTILISATEUR_TEST		= "agriculture_bdc";
	private static final String	UTILISATEUR_TEST_NOM	= "Bdc";
	private static final String	UTILISATEUR_TEST_PRENOM	= "Agriculture";

	@WebTest(description = "Recherche d'un utilisateur par identifiant", order = 10)
	@TestDocumentation(categories = { "Utilisateur", "Recherche utilisateur" })
	public void testRechercherParIdentifiant() {
		getFlog().startAction("Recherche d'un utilisateur par identifiant");
		CorbeillePage corbeillePage = loginAsAdmin();
		EspaceAdministration adminPage = corbeillePage.getBandeauMenu().goToAdministration();
		RechercheUtilisateurPage rechercheUtilisateurPage = adminPage.getMenuVoletGauche().goToRechercheUtilisateur();
		rechercheUtilisateurPage.setIdentifiant(UTILISATEUR_TEST);
		rechercheUtilisateurPage.rechercher();
		WebTestAssert.assertTrue(getFlog(), "Contenu des résultats de la recherche", "La recherche ne contient pas "
				+ UTILISATEUR_TEST, rechercheUtilisateurPage.verifierResultContient(UTILISATEUR_TEST));
		logout();
		getFlog().endAction();
	}

	@WebTest(description = "Recherche d'un utilisateur par prénom", order = 20)
	@TestDocumentation(categories = { "Utilisateur", "Recherche utilisateur" })
	public void testRechercherParPrenom() {
		getFlog().startAction("Recherche d'un utilisateur par prénom");
		CorbeillePage corbeillePage = loginAsAdmin();
		EspaceAdministration adminPage = corbeillePage.getBandeauMenu().goToAdministration();
		RechercheUtilisateurPage rechercheUtilisateurPage = adminPage.getMenuVoletGauche().goToRechercheUtilisateur();
		rechercheUtilisateurPage.setPrenom(UTILISATEUR_TEST_PRENOM);
		rechercheUtilisateurPage.rechercher();
		WebTestAssert.assertTrue(getFlog(), "Contenu des résultats de la recherche", "La recherche ne contient pas "
				+ UTILISATEUR_TEST, rechercheUtilisateurPage.verifierResultContient(UTILISATEUR_TEST));
		logout();
		getFlog().endAction();
	}

	@WebTest(description = "Recherche d'un utilisateur par ministère", order = 30)
	@TestDocumentation(categories = { "Utilisateur", "Recherche utilisateur" })
	public void testRechercherParMinistere() {
		getFlog().startAction("Recherche d'un utilisateur par ministère");
		CorbeillePage corbeillePage = loginAsAdmin();
		EspaceAdministration adminPage = corbeillePage.getBandeauMenu().goToAdministration();
		RechercheUtilisateurPage rechercheUtilisateurPage = adminPage.getMenuVoletGauche().goToRechercheUtilisateur();
		rechercheUtilisateurPage.setMinistere(ConstantesOrga.MIN_AGRI);
		rechercheUtilisateurPage.rechercher();
		WebTestAssert.assertTrue(getFlog(), "Contenu des résultats de la recherche", "La recherche ne contient pas "
				+ UTILISATEUR_TEST, rechercheUtilisateurPage.verifierResultContient(UTILISATEUR_TEST));
		logout();
		getFlog().endAction();
	}

	@WebTest(description = "Recherche d'un utilisateur par nom", order = 40)
	@TestDocumentation(categories = { "Utilisateur", "Recherche utilisateur" })
	public void testRechercherParNom() {
		getFlog().startAction("Recherche d'un utilisateur par nom");
		CorbeillePage corbeillePage = loginAsAdmin();
		EspaceAdministration adminPage = corbeillePage.getBandeauMenu().goToAdministration();
		RechercheUtilisateurPage rechercheUtilisateurPage = adminPage.getMenuVoletGauche().goToRechercheUtilisateur();
		rechercheUtilisateurPage.setNom(UTILISATEUR_TEST_NOM);
		rechercheUtilisateurPage.rechercher();
		WebTestAssert.assertTrue(getFlog(), "Contenu des résultats de la recherche", "La recherche ne contient pas "
				+ UTILISATEUR_TEST, rechercheUtilisateurPage.verifierResultContient(UTILISATEUR_TEST));
		logout();
		getFlog().endAction();
	}
	
	@WebTest(description = "Recherche par ministère + export liste de résultat", order = 50)
	@TestDocumentation(categories = { "Utilisateur", "Recherche utilisateur" })
	public void testExportRecherche() throws MessagingException, IOException, InterruptedException {
		getFlog().startAction("Recherche par ministère + export liste de résultat");
		CorbeillePage corbeillePage = loginAsAdmin();
		EspaceAdministration adminPage = corbeillePage.getBandeauMenu().goToAdministration();
		RechercheUtilisateurPage rechercheUtilisateurPage = adminPage.getMenuVoletGauche().goToRechercheUtilisateur();
		rechercheUtilisateurPage.setMinistere(ConstantesOrga.MIN_AGRI);
		rechercheUtilisateurPage.rechercher();
		WebTestAssert.assertTrue(getFlog(), "Contenu des résultats de la recherche", "La recherche ne contient pas "
				+ UTILISATEUR_TEST, rechercheUtilisateurPage.verifierResultContient(UTILISATEUR_TEST));
		rechercheUtilisateurPage.exporter();
		
		getFlog().startAction("Vérification de l'arrivée du mail");
		ReponsesImapHelper mailHelper = ReponsesImapHelper.getInstance();
		ImapConsult imap = mailHelper.initNewImapConsult();
		imap.connect();
		mailHelper.waitForMessagesSubject(imap, "[REPONSES] Votre demande d'export de la recherche utilisateur");		
		getFlog().endAction();
		
		logout();
		getFlog().endAction();
	}

}
