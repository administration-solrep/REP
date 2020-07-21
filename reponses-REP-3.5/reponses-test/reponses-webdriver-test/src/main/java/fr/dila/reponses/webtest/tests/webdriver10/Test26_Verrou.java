package fr.dila.reponses.webtest.tests.webdriver10;

import org.junit.Assert;
import org.openqa.selenium.TimeoutException;
import fr.dila.reponses.webtest.constant.ConstantesLotQuestion;
import fr.dila.reponses.webtest.helper.AbstractWebTest;
import fr.dila.reponses.webtest.pages.CorbeillePage;
import fr.dila.reponses.webtest.pages.dossier.DossierPage;
import fr.dila.reponses.webtest.pages.dossier.FeuilleRouteOnglet;
import fr.dila.reponses.webtest.pages.recherche.RechercheResultPage;
import fr.dila.st.annotations.TestDocumentation;
import fr.sword.naiad.commons.webtest.annotation.WebTest;

//Classe verifiant la presence du verrou en fonction des differents profils 
public class Test26_Verrou extends AbstractWebTest {

	public static final String	DOSSIER_TEST_VERROU	= ConstantesLotQuestion.DOSSIER_TEST_VERROU;

	// Connecté en tant qu'admin fonctionnel -
	@WebTest(description = "1.1.1 Connecté en tant qu'admin fonctionnel ", order = 10)
	@TestDocumentation(categories = { "FDR" })
	public void AdminFonctionnel() throws InterruptedException {
		try {
			CorbeillePage corbeillePage = loginAsAdminFonctionnel();
			RechercheResultPage rechercheResultPage = corbeillePage.getBandeauMenu().rechercheRapide(
					DOSSIER_TEST_VERROU);
			DossierPage dossierPage = rechercheResultPage.getPage(DossierPage.class);
			Assert.assertTrue("Le verrou doit être présent pour cet utilisateur ", dossierPage.hasLock());
			dossierPage.lock();
			dossierPage.unlock();
			logout();

		} catch (TimeoutException e) {
			Assert.fail("Un timeout a été rencontré sur un élément de la page");
		}
	}

	// Connecté en tant qu'admin ministeriel justice -
	@WebTest(description = "1.1.3 Connecté en tant qu admin ministeriel Min Justice", order = 30)
	@TestDocumentation(categories = { "FDR" })
	public void adminMinJustice() throws InterruptedException {
		try {
			CorbeillePage corbeillePage = AdminMin_Justice();
			RechercheResultPage rechercheResultPage = corbeillePage.getBandeauMenu().rechercheRapide(
					DOSSIER_TEST_VERROU);
			DossierPage dossierPage = rechercheResultPage.getPage(DossierPage.class);
			Assert.assertTrue("Le verrou doit être présent pour cet utilisateur", dossierPage.hasLock());
			dossierPage.lock();
			dossierPage.unlock();
			logout();
		} catch (TimeoutException e) {
			Assert.fail("Un timeout a été rencontré sur un élément de la page");
		}
	}

	// Connecté en tant que contributeur ministeriel justice -
	@WebTest(description = "1.1.4 Connecté en tant qu contrib1 ministeriel Min Justice", order = 40)
	@TestDocumentation(categories = { "FDR" })
	public void ContribMinJustice() throws InterruptedException {
		try {
			CorbeillePage corbeillePage = contributeurMin_Justice();
			RechercheResultPage rechercheResultPage = corbeillePage.getBandeauMenu().rechercheRapide(
					DOSSIER_TEST_VERROU);
			DossierPage dossierPage = rechercheResultPage.getPage(DossierPage.class);
			Assert.assertTrue("Le verrou doit être présent pour cet utilisateur", dossierPage.hasLock());
			// dossierPage.goToOngletFDR();
			dossierPage.lock();
			dossierPage.unlock();
			logout();
		} catch (TimeoutException e) {
			Assert.fail("Un timeout a été rencontré sur un élément de la page");
		}
	}

	// Connecté en tant qu'utilisateur du poste justice -
	@WebTest(description = "1.1.6 Connecté en tant qu user du poste Min Justice", order = 60)
	@TestDocumentation(categories = { "FDR" })
	public void UserJustice() throws InterruptedException {
		try {
			CorbeillePage corbeillePage = loginBdcJustice();
			RechercheResultPage rechercheResultPage = corbeillePage.getBandeauMenu().rechercheRapide(
					DOSSIER_TEST_VERROU);
			DossierPage dossierPage = rechercheResultPage.getPage(DossierPage.class);
			Assert.assertTrue("Le verrou doit être présent pour cet utilisateur car rattaché au poste BDC Justice",
					!dossierPage.hasLock());
			logout();
		} catch (TimeoutException e) {
			Assert.fail("Un timeout a été rencontré sur un élément de la page");
		}
	}

	// Connecté en tant qu'admin ministeriel Ecologie -
	@WebTest(description = "1.1.7 Connecté en tant qu admin Min Ecologie", order = 70)
	@TestDocumentation(categories = { "FDR" })
	public void adminMinEcologie() throws InterruptedException {
		try {
			CorbeillePage corbeillePage = AdminMin_Ecologie();
			RechercheResultPage rechercheResultPage = corbeillePage.getBandeauMenu().rechercheRapide(
					DOSSIER_TEST_VERROU);
			DossierPage dossierPage = rechercheResultPage.getPage(DossierPage.class);
			Assert.assertTrue("Le verrou ne doit pas être présent pour cet utilisateur", !dossierPage.hasLock());
			logout();

		} catch (TimeoutException e) {
			Assert.fail("Un timeout a été rencontré sur un élément de la page");
		}
	}

	// Connecté en tant que contributeur ministeriel Ecologie -
	@WebTest(description = "1.1.8 Connecté en tant qu contrib1 Min Ecologie", order = 80)
	@TestDocumentation(categories = { "FDR" })
	public void ContribMinEcologie() throws InterruptedException {
		try {
			CorbeillePage corbeillePage = contributeur1Min_Ecologie();
			RechercheResultPage rechercheResultPage = corbeillePage.getBandeauMenu().rechercheRapide(
					DOSSIER_TEST_VERROU);
			DossierPage dossierPage = rechercheResultPage.getPage(DossierPage.class);
			Assert.assertTrue("Le verrou ne doit pas être présent pour cet utilisateur", !dossierPage.hasLock());

			logout();
		} catch (TimeoutException e) {
			Assert.fail("Un timeout a été rencontré sur un élément de la page");
		}
	}

	// Connecté en tant que contributeur2 ministeriel Ecologie -
	@WebTest(description = "1.1.9 Connecté en tant qu contrib1 Min Ecologie", order = 90)
	@TestDocumentation(categories = { "FDR" })
	public void Contrib2MinEcologie() throws InterruptedException {
		try {
			CorbeillePage corbeillePage = contributeur2Min_Ecologie();
			RechercheResultPage rechercheResultPage = corbeillePage.getBandeauMenu().rechercheRapide(
					DOSSIER_TEST_VERROU);
			DossierPage dossierPage = rechercheResultPage.getPage(DossierPage.class);
			Assert.assertTrue("Le verrou ne doit pas être présent pour cet utilisateur", !dossierPage.hasLock());
			logout();
		} catch (TimeoutException e) {
			Assert.fail("Un timeout a été rencontré sur un élément de la page");
		}
	}

	// Connecté en tant qu'utilisateur du poste Ecologie -
	@WebTest(description = "1.1.10 connecté en tant qu' user d'un autre ministère Economie", order = 100)
	@TestDocumentation(categories = { "FDR" })
	public void UserEcologie() throws InterruptedException {
		try {
			CorbeillePage corbeillePage = loginBdc();
			RechercheResultPage rechercheResultPage = corbeillePage.getBandeauMenu().rechercheRapide(
					DOSSIER_TEST_VERROU);
			DossierPage dossierPage = rechercheResultPage.getPage(DossierPage.class);
			Assert.assertTrue("Le verrou ne doit pas être présent pour cet utilisateur", !dossierPage.hasLock());
			logout();
		} catch (TimeoutException e) {
			Assert.fail("Un timeout a été rencontré sur un élément de la page");
		}
	}

	// ************Validation etape ********///
	@WebTest(description = "1.1.11 Validation d'etape", order = 110)
	@TestDocumentation(categories = { "FDR" })
	public void ValiderEtape() {
		try {
			CorbeillePage corbeillePage = loginAsAdminFonctionnel();
			RechercheResultPage rechercheResultPage = corbeillePage.getBandeauMenu().rechercheRapide(
					DOSSIER_TEST_VERROU);
			DossierPage dossierPage = rechercheResultPage.getPage(DossierPage.class);
			Assert.assertTrue("pas de droit pour verrouiller le dossier", dossierPage.hasLock());
			FeuilleRouteOnglet fdrOnglet = dossierPage.goToOngletFDR();
			fdrOnglet.validerEtape();
			logout();

		} catch (TimeoutException e) {
			Assert.fail("Un timeout a été rencontré sur un élément de la page");
		}
	}

	// Connecté en tant qu'admin fonctionnel apres validation d'etape -
	@WebTest(description = "1.1.12 Connecté en tant qu'admin fonctionnel ", order = 120)
	@TestDocumentation(categories = { "FDR" })
	public void AdminFonctionnel_Etape2() throws InterruptedException {
		try {
			CorbeillePage corbeillePage = loginAsAdminFonctionnel();
			RechercheResultPage rechercheResultPage = corbeillePage.getBandeauMenu().rechercheRapide(
					DOSSIER_TEST_VERROU);
			DossierPage dossierPage = rechercheResultPage.getPage(DossierPage.class);
			Assert.assertTrue("pas de droit pour verrouiller le dossier", dossierPage.hasLock());
			// dossierPage.goToOngletFDR();
			dossierPage.lock();
			dossierPage.unlock();
			logout();

		} catch (TimeoutException e) {
			Assert.fail("Un timeout a été rencontré sur un élément de la page");
		}
	}

	// Connecté en tant qu'admin ministeriel justice -
	@WebTest(description = "1.1.13 Connecté en tant qu admin ministeriel Min Justice", order = 130)
	@TestDocumentation(categories = { "FDR" })
	public void adminMinJustice_Etape2() throws InterruptedException {
		try {
			CorbeillePage corbeillePage = AdminMin_Justice();
			RechercheResultPage rechercheResultPage = corbeillePage.getBandeauMenu().rechercheRapide(
					DOSSIER_TEST_VERROU);
			DossierPage dossierPage = rechercheResultPage.getPage(DossierPage.class);
			Assert.assertTrue("pas de droit pour verrouiller le dossier", !dossierPage.hasLock());
			logout();
		} catch (TimeoutException e) {
			Assert.fail("Un timeout a été rencontré sur un élément de la page");
		}
	}

	// Connecté en tant que contributeur ministeriel justice -
	@WebTest(description = "1.1.14 Connecté en tant qu contrib1 ministeriel Min Justice", order = 140)
	@TestDocumentation(categories = { "FDR" })
	public void ContribMinJustice_Etape2() throws InterruptedException {
		try {
			CorbeillePage corbeillePage = contributeurMin_Justice();
			RechercheResultPage rechercheResultPage = corbeillePage.getBandeauMenu().rechercheRapide(
					DOSSIER_TEST_VERROU);
			DossierPage dossierPage = rechercheResultPage.getPage(DossierPage.class);
			Assert.assertTrue("pas de droit pour verrouiller le dossier", !dossierPage.hasLock());

			logout();
		} catch (TimeoutException e) {
			Assert.fail("Un timeout a été rencontré sur un élément de la page");
		}
	}

	// Connecté en tant qu'utilisateur du poste justice -
	@WebTest(description = "1.1.15 Connecté en tant qu user du poste Min Justice", order = 150)
	@TestDocumentation(categories = { "FDR" })
	public void UserJustice_Etape2() throws InterruptedException {
		try {
			CorbeillePage corbeillePage = loginBdcJustice();
			RechercheResultPage rechercheResultPage = corbeillePage.getBandeauMenu().rechercheRapide(
					DOSSIER_TEST_VERROU);
			DossierPage dossierPage = rechercheResultPage.getPage(DossierPage.class);
			Assert.assertTrue("Pas de verrou visible pour cet utilisateur", !dossierPage.hasLock());
			logout();
		} catch (TimeoutException e) {
			Assert.fail("Un timeout a été rencontré sur un élément de la page");
		}
	}

	// Connecté en tant qu'admin ministeriel Ecologie -
	@WebTest(description = "1.1.16 Connecté en tant qu admin Min Ecologie", order = 160)
	@TestDocumentation(categories = { "FDR" })
	public void adminMinEcologie_Etape2() throws InterruptedException {
		try {
			CorbeillePage corbeillePage = AdminMin_Ecologie();
			RechercheResultPage rechercheResultPage = corbeillePage.getBandeauMenu().rechercheRapide(
					DOSSIER_TEST_VERROU);
			DossierPage dossierPage = rechercheResultPage.getPage(DossierPage.class);
			Assert.assertTrue("Le verrou doit être présent pour cet utilisateur", dossierPage.hasLock());
			dossierPage.lock();
			dossierPage.unlock();
			logout();

		} catch (TimeoutException e) {
			Assert.fail("Un timeout a été rencontré sur un élément de la page");
		}
	}

	// Connecté en tant que contributeur ministeriel Ecologie -
	@WebTest(description = "1.1.17 Connecté en tant qu contrib1 Min Ecologie", order = 170)
	@TestDocumentation(categories = { "FDR" })
	public void ContribMinEcologie_Etape2() throws InterruptedException {
		try {
			CorbeillePage corbeillePage = contributeur1Min_Ecologie();
			RechercheResultPage rechercheResultPage = corbeillePage.getBandeauMenu().rechercheRapide(
					DOSSIER_TEST_VERROU);
			DossierPage dossierPage = rechercheResultPage.getPage(DossierPage.class);
			Assert.assertTrue("Le verrou doit être présent pour cet utilisateur", dossierPage.hasLock());
			dossierPage.lock();
			dossierPage.unlock();

			logout();
		} catch (TimeoutException e) {
			Assert.fail("Un timeout a été rencontré sur un élément de la page");
		}
	}

	// Connecté en tant que contributeur2 ministeriel Ecologie -
	@WebTest(description = "1.1.18 Connecté en tant qu contrib1 Min Ecologie", order = 180)
	@TestDocumentation(categories = { "FDR" })
	public void Contrib2MinEcologie_Etape2() throws InterruptedException {
		try {
			CorbeillePage corbeillePage = contributeur2Min_Ecologie();
			RechercheResultPage rechercheResultPage = corbeillePage.getBandeauMenu().rechercheRapide(
					DOSSIER_TEST_VERROU);
			DossierPage dossierPage = rechercheResultPage.getPage(DossierPage.class);
			Assert.assertTrue("Le verrou ne doit pas être présent pour cet utilisateur car pas rattaché au poste",
					!dossierPage.hasLock());
			logout();
		} catch (TimeoutException e) {
			Assert.fail("Un timeout a été rencontré sur un élément de la page");
		}
	}

	// Connecté en tant qu'utilisateur du poste Ecologie -
	@WebTest(description = "1.1.19 Connecté en tant qu' user d'un autre ministère", order = 190)
	@TestDocumentation(categories = { "FDR" })
	public void UserEcologie_Etape2() throws InterruptedException {
		try {
			CorbeillePage corbeillePage = loginBdc();
			RechercheResultPage rechercheResultPage = corbeillePage.getBandeauMenu().rechercheRapide(
					DOSSIER_TEST_VERROU);
			DossierPage dossierPage = rechercheResultPage.getPage(DossierPage.class);
			Assert.assertTrue("Le verrou ne doit pas être présent pour cet utilisateur", !dossierPage.hasLock());
			logout();
		} catch (TimeoutException e) {
			Assert.fail("Un timeout a été rencontré sur un élément de la page");
		}
	}

}
