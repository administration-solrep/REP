package fr.dila.reponses.webtest.tests.webdriver10;

import org.junit.Assert;
import org.openqa.selenium.TimeoutException;

import fr.dila.reponses.webtest.constant.ConstantesEtapeType;
import fr.dila.reponses.webtest.constant.ConstantesOrga;
import fr.dila.reponses.webtest.helper.AbstractWebTest;
import fr.dila.reponses.webtest.model.Etape;
import fr.dila.reponses.webtest.model.EtapeSerie;
import fr.dila.reponses.webtest.model.FeuilleRoute;
import fr.dila.reponses.webtest.model.FeuilleRouteCatalogue;
import fr.dila.reponses.webtest.pages.CorbeillePage;
import fr.dila.reponses.webtest.pages.administration.GestionModeleFDRPage;
import fr.dila.reponses.webtest.pages.administration.feuilleroute.CreerUnModeleFDRPage;
import fr.dila.reponses.webtest.pages.espaces.EspaceAdministration;
import fr.dila.st.annotations.TestDocumentation;
import fr.sword.naiad.commons.webtest.annotation.WebTest;

public class Test03ModeleFdr extends AbstractWebTest {

	/**
	 * Création du modèle de Fdr type ( le modèle par défaut )
	 * 
	 * @throws InterruptedException
	 */
	@WebTest(description = "1.1.3 Modèle de FdR - création d'un modèle type", order = 10)
	@TestDocumentation(categories = { "FDR" })
	public void testCreationModeleType() throws InterruptedException {
		try {
			CorbeillePage corbeillePage = loginAsAdmin();
			EspaceAdministration espaceAdministration = corbeillePage.getBandeauMenu().goToAdministration();
			GestionModeleFDRPage gestionModele = espaceAdministration.getMenuVoletGauche().goToGestionModelesFdr();
			CreerUnModeleFDRPage creerModele = gestionModele.creerUnModele();
			FeuilleRoute fdr1 = FeuilleRouteCatalogue.getFDR1();
			creerModele.creerModele(fdr1);
			logout();
		} catch (TimeoutException e) {
			Assert.fail("Un timeout a été rencontré sur un élément de la page");
		}
	}

	@WebTest(description = "1.1.3 Modèle de FdR - création d'un modèle pour la substitution", order = 20)
	@TestDocumentation(categories = { "FDR" })
	public void testCreationModeleSubstitution() {
		try {
			CorbeillePage corbeillePage = loginAsAdmin();
			EspaceAdministration espaceAdministration = corbeillePage.getBandeauMenu().goToAdministration();
			GestionModeleFDRPage gestionModele = espaceAdministration.getMenuVoletGauche().goToGestionModelesFdr();
			CreerUnModeleFDRPage creerModele = gestionModele.creerUnModele();
			FeuilleRoute fdr2 = FeuilleRouteCatalogue.getFDR2();
			creerModele.creerModele(fdr2);
			logout();
		} catch (TimeoutException e) {
			Assert.fail("Un timeout a été rencontré sur un élément de la page");
		}
	}

	@WebTest(description = "1.1.3 Modèle de FdR - substitution du modèle", order = 30)
	@TestDocumentation(categories = { "FDR" })
	public void testSubstituer() {
		try {
			CorbeillePage corbeillePage = loginAsAdmin();
			EspaceAdministration espaceAdministration = corbeillePage.getBandeauMenu().goToAdministration();
			GestionModeleFDRPage gestionModele = espaceAdministration.getMenuVoletGauche().goToGestionModelesFdr();
			CreerUnModeleFDRPage creerModele = gestionModele.dupliquerFeuilleRoute(FeuilleRouteCatalogue.NOM_FDR_2);
			Etape etapeBonus = new EtapeSerie(ConstantesEtapeType.POUR_REDACTION, ConstantesOrga.POSTE_DLF);
			creerModele.ajouterApres(1, etapeBonus);
			creerModele.valider();
			creerModele.retour();
			logout();
		} catch (TimeoutException e) {
			Assert.fail("Un timeout a été rencontré sur un élément de la page");
		}
	}

	@WebTest(description = "1.1.3 Modèle de FdR - création d'un modèle pour le ministère agriculture", order = 40)
	@TestDocumentation(categories = { "FDR" })
	public void testCreationModeleAgriculture() {
		try {
			CorbeillePage corbeillePage = loginAsAdmin();
			EspaceAdministration espaceAdministration = corbeillePage.getBandeauMenu().goToAdministration();
			GestionModeleFDRPage gestionModele = espaceAdministration.getMenuVoletGauche().goToGestionModelesFdr();
			CreerUnModeleFDRPage creerModele = gestionModele.creerUnModele();
			FeuilleRoute fdr3 = FeuilleRouteCatalogue.getFDR3();
			creerModele.creerModele(fdr3);
			logout();
		} catch (TimeoutException e) {
			Assert.fail("Un timeout a été rencontré sur un élément de la page");
		}
	}

	@WebTest(description = "1.1.3 Modèle de FdR - création d'un modèle type", order = 10)
	@TestDocumentation(categories = { "FDR" })
	public void testCreationAvis() throws InterruptedException {
		try {
			CorbeillePage corbeillePage = loginAsAdmin();
			EspaceAdministration espaceAdministration = corbeillePage.getBandeauMenu().goToAdministration();
			GestionModeleFDRPage gestionModele = espaceAdministration.getMenuVoletGauche().goToGestionModelesFdr();
			CreerUnModeleFDRPage creerModele = gestionModele.creerUnModele();
			FeuilleRoute fdr = FeuilleRouteCatalogue.getFDR();
			creerModele.creerModele(fdr);
			logout();
		} catch (TimeoutException e) {
			Assert.fail("Un timeout a été rencontré sur un élément de la page");
		}
	}

}
