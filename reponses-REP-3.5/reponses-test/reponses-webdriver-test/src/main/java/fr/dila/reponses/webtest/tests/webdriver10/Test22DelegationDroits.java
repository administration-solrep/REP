package fr.dila.reponses.webtest.tests.webdriver10;

import java.util.Calendar;
import java.util.Date;

import fr.dila.reponses.webtest.constant.ConstantesOrga;
import fr.dila.reponses.webtest.helper.AbstractWebTest;
import fr.dila.reponses.webtest.pages.CorbeillePage;
import fr.dila.reponses.webtest.pages.administration.delegation.CreerDelegationPage;
import fr.dila.reponses.webtest.pages.administration.delegation.DelegationDroitsPage;
import fr.dila.reponses.webtest.tests.webtestassert.WebTestAssert;
import fr.dila.st.annotations.TestDocumentation;
import fr.dila.st.webdriver.model.STUser;
import fr.sword.naiad.commons.webtest.annotation.WebTest;

/**
 * Test sur la délégation des droits
 * 
 * @author jgomez
 * 
 */
public class Test22DelegationDroits extends AbstractWebTest {

	private static final String	UTILISATEUR_TEST_LOGIN		= "agriculture_bdc";
	private static final String	UTILISATEUR_TEST_PWD		= "agriculture_bdc";
	private static final String	UTILISATEUR_TEST_RECHERCHE	= "Agriculture Bdc";

	@WebTest(description = "Test de délégation des droits")
	@TestDocumentation(categories = { "Utilisateur", "Droits et profils" })
	public void testDelegationDroits() {
		getFlog()
				.startAction(
						"Délégation des droits : vérification que les droits n'ont pas encore été transmis à l'utilisateur de test");
		testVerificationDroitsAUnUtilisateurTestAvant();
		getFlog().endAction();
		getFlog().startAction("Délégation des droits : délégation des droits à un utilisateur de test");
		testDelegationDesDroitsAUnUtilisateurTest();
		getFlog().endAction();
		getFlog().startAction(
				"Délégation des droits : vérification que les droits ont bien été transmis à l'utilisateur de test");
		testVerificationDroitsAUnUtilisateurTestApres();
		getFlog().endAction();
	}

	// Action 1
	private void testVerificationDroitsAUnUtilisateurTestAvant() {
		getFlog().startAction("Vérifier que l'utilisateur de test ne possède pas la corbeille Premier ministre");
		CorbeillePage corbeillePage = loginAsUtilisateurTest();
		WebTestAssert.assertFalse(getFlog(), "Absence de la corbeille premier ministre créée",
				"La corbeille Premier Ministre est pas présente",
				corbeillePage.hasCorbeille(ConstantesOrga.PREMIER_MINISTRE));
		logout();
		getFlog().endAction();
	}

	// Action 2
	private void testDelegationDesDroitsAUnUtilisateurTest() {
		getFlog().startAction("Creer une délégation de droit sur l'utilisateur de test");
		CorbeillePage corbeillePage = loginAsAdmin();
		DelegationDroitsPage delegationDroits = corbeillePage.getBandeauMenu().goToAdministration()
				.getMenuVoletGauche().goToDelegationDesDroits();
		CreerDelegationPage creerUneDelegation = delegationDroits.goToCreerUneDelegation();
		Date dateDebut = Calendar.getInstance().getTime();
		Calendar calFin = Calendar.getInstance();
		calFin.add(Calendar.DAY_OF_YEAR, 1);
		Date dateFin = calFin.getTime();
		creerUneDelegation.creer(UTILISATEUR_TEST_RECHERCHE, dateDebut, dateFin, "Webservices Ministériels");
		creerUneDelegation.retourALaListe();
		WebTestAssert.assertTrue(getFlog(), "Présence de la délégation créée", "La délégation n'a pas été créée",
				delegationDroits.hasDelegation(UTILISATEUR_TEST_RECHERCHE));
		logout();
		getFlog().endAction();
	}

	// Action 3
	private void testVerificationDroitsAUnUtilisateurTestApres() {
		getFlog().startAction("Vérifier que l'utilisateur de test possède bien la corbeille Premier ministre");
		CorbeillePage corbeillePage = loginAsUtilisateurTest();
		WebTestAssert.assertTrue(getFlog(), "Présence de la corbeille premier ministre créée",
				"La corbeille Premier Ministre n'est pas présente",
				corbeillePage.hasCorbeille(ConstantesOrga.PREMIER_MINISTRE));
		logout();
		getFlog().endAction();
	}

	private CorbeillePage loginAsUtilisateurTest() {
		STUser sTUser = new STUser(UTILISATEUR_TEST_LOGIN, UTILISATEUR_TEST_PWD, "");
		return login(sTUser, CorbeillePage.class);
	}

}
