package fr.dila.reponses.webtest.tests.webdriver10;

import java.util.ArrayList;
import java.util.List;

import fr.dila.reponses.webtest.helper.AbstractWebTest;
import fr.dila.reponses.webtest.pages.CorbeillePage;
import fr.dila.reponses.webtest.pages.administration.GestionProfilsPage;
import fr.dila.reponses.webtest.pages.administration.GestionUtilisateurPage;
import fr.dila.reponses.webtest.pages.administration.profil.CreerProfilPage;
import fr.dila.reponses.webtest.pages.administration.utilisateur.UtilisateurPage;
import fr.dila.reponses.webtest.pages.espaces.EspaceAdministrationMenuVoletGauche;
import fr.dila.reponses.webtest.tests.webtestassert.WebTestAssert;
import fr.dila.st.annotations.TestDocumentation;
import fr.dila.st.webdriver.model.STUser;
import fr.sword.naiad.commons.webtest.annotation.WebTest;

/**
 * Test des profils
 * 
 * @author jgomez
 * 
 */
public class Test20Profil extends AbstractWebTest {

	private static final String	UTILISATEUR_TEST			= "agriculture_bdc";
	private static final String	UTILISATEUR_CONTR_MIN_TEST	= "agriculture_sdpv";
	private static final String	PROFIL_TEST					= "admintest";
	private static final String	ACCES_PROFIL					= "Gestion des profils : lecture";
	private static final String	ACCES_ADMIN					= "Accès à l'espace d'administration";
	private static final String	PROFIL_A_SUPPR_ID			= "editUser:nxl_user:nxw_groups_1_delete";

	@WebTest(description = "Tests sur droits et profils")
	@TestDocumentation(categories = { "Utilisateur", "Droits et profils" })
	public void testDroitsEtProfils() {
		getFlog().startAction("Vérifier que l'utilisateur n'a pas accès à la gestion des droits");
		testNonAccesAdministration();
		getFlog().endAction();
		getFlog().startAction("Création du profil admintest");
		testCreationProfilAdminMinisteriel();
		getFlog().endAction();
		getFlog().startAction("Ajout du profil de test sur l'utilisateur de test");
		testAjoutProfilSurUtilisateur();
		getFlog().endAction();
		getFlog().startAction("Vérification que le droit ajouté à l'utilisateur soit bien appliqué");
		testAjoutDroitAdministrationSurCetUtilisateur();
		getFlog().endAction();
		getFlog().startAction("Suppression du profil pour l'utilisateur test");
		suppressionDroitAdministrationSurCetUtilisateur();
		getFlog().endAction();
	}

	/**
	 * Test de non accès au droit Action 1
	 */
	// private void testNonVisibiliteDroitAvecUtilisateur() {
	// getFlog().startAction("Regarder si l'utilisateur n'a pas accès au droit");
	// CorbeillePage corbeillePage = loginAsUtilisateurTest();
	// EspaceAdministrationMenuVoletGauche menuVoletGauche = corbeillePage.getBandeauMenu().goToAdministration()
	// .getMenuVoletGauche();
	// getFlog().endAction();
	// WebTestAssert.assertFalse(getFlog(), "Non présence du lien de gestion des profils",
	// "Le lien de Gestion des profils est présent", menuVoletGauche.contientAccesAuProfil());
	// logout();
	// }

	/**
	 * Test de non accès à l'admin Action 0
	 */
	private void testNonAccesAdministration() {
		getFlog().startAction("Regarder si l'utilisateur n'a pas accès à l'administration");
		CorbeillePage corbeillePage = loginAsUtilisateurTest();
		WebTestAssert.assertFalse(getFlog(), "Non présence du lien d'accès à l'administration",
				"Le lien d'accès à l'administration est présent", corbeillePage.getBandeauMenu()
						.ContientLienAdministration());
		logout();
	}

	/**
	 * Test de création d'un profil profil admintest Action 2
	 */
	private void testCreationProfilAdminMinisteriel() {
		getFlog().startAction("Création d'un profil");
		CorbeillePage corbeillePage = loginAsAdmin();
		GestionProfilsPage gestionProfilPage = corbeillePage.getBandeauMenu().goToAdministration().getMenuVoletGauche()
				.goToGestionProfils();
		CreerProfilPage creerProfilPage = gestionProfilPage.goToAjouterProfil();
		List<String> fonctions = new ArrayList<String>();
		fonctions.add(ACCES_PROFIL);
		fonctions.add(ACCES_ADMIN);
		creerProfilPage.creerProfil(PROFIL_TEST, fonctions);
		getFlog().endAction();
		corbeillePage.getBandeauMenu().goToAdministration().getMenuVoletGauche().goToGestionProfils();
		WebTestAssert.assertTrue(getFlog(), "Présence du profil " + PROFIL_TEST, "Le profil " + PROFIL_TEST
				+ " est absent", gestionProfilPage.verifierPresenceProfil(PROFIL_TEST));
		logout();
	}

	/**
	 * Test d'ajout du profil sur l'utilisateur de test Action 3
	 */
	private void testAjoutProfilSurUtilisateur() {
		getFlog().startAction("Ajout du profil sur l'utilisateur");
		CorbeillePage corbeillePage = loginAsAdmin();
		GestionUtilisateurPage gestionUtilisateurPage = corbeillePage.getBandeauMenu().goToAdministration()
				.getMenuVoletGauche().goToGestionDesUtilisateurs();
		UtilisateurPage utilisateurPage = gestionUtilisateurPage.rechercherUtilisateur(UTILISATEUR_CONTR_MIN_TEST);
		utilisateurPage.passerEnModeModification();
		utilisateurPage.ajouterProfil(PROFIL_TEST);
		utilisateurPage.enregistrer();
		// Ajout profil également sur agriculture_bdc
		gestionUtilisateurPage = corbeillePage.getBandeauMenu().goToAdministration().getMenuVoletGauche()
				.goToGestionDesUtilisateurs();
		utilisateurPage = gestionUtilisateurPage.rechercherUtilisateur(UTILISATEUR_TEST);
		utilisateurPage.passerEnModeModification();
		utilisateurPage.ajouterProfil(PROFIL_TEST);
		utilisateurPage.enregistrer();
		logout();
		getFlog().endAction();
	}

	/**
	 * Test d'application du droit sur cet utilisateur Action 4
	 */
	private void testAjoutDroitAdministrationSurCetUtilisateur() {
		getFlog().startAction("Ajout du profil de test sur l'utilisateur agriculture_sdpv");
		CorbeillePage corbeillePage = loginAsUtilisateurTest();
		// verification d'un droit admin
		EspaceAdministrationMenuVoletGauche menuVoletGauche = corbeillePage.getBandeauMenu().goToAdministration()
				.getMenuVoletGauche();
		WebTestAssert.assertTrue(getFlog(), "Présence du lien de gestion des profils",
				"Le lien de Gestion des profils est absent", menuVoletGauche.contientAccesAuProfil());
		getFlog().endAction();
		logout();
	}

	/**
	 * Test de suppression de droits sur cet utilisateur Action 5
	 * 
	 * @return
	 */
	private void suppressionDroitAdministrationSurCetUtilisateur() {
		getFlog().startAction("Suppression du profil de test sur l'utilisateur agriculture_sdpv");
		CorbeillePage corbeillePage = loginAsAdmin();
		GestionUtilisateurPage gestionUtilisateurPage = corbeillePage.getBandeauMenu().goToAdministration()
				.getMenuVoletGauche().goToGestionDesUtilisateurs();
		UtilisateurPage utilisateurPage = gestionUtilisateurPage.rechercherUtilisateur(UTILISATEUR_CONTR_MIN_TEST);
		utilisateurPage.passerEnModeModification();
		utilisateurPage.supprimerProfil(PROFIL_A_SUPPR_ID);
		utilisateurPage.enregistrer();
		logout();
		getFlog().endAction();
	}

	private CorbeillePage loginAsUtilisateurTest() {
		STUser sTUser = new STUser(UTILISATEUR_CONTR_MIN_TEST, UTILISATEUR_CONTR_MIN_TEST, "");
		return login(sTUser, CorbeillePage.class);
	}

}
