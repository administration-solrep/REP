package fr.dila.reponses.webtest.tests.webdriver10;

import fr.dila.reponses.webtest.helper.AbstractWebTest;
import fr.dila.reponses.webtest.pages.CorbeillePage;
import fr.dila.reponses.webtest.pages.login.LoginPage;
import fr.dila.reponses.webtest.pages.login.NewPasswordPage;
import fr.dila.st.annotations.TestDocumentation;
import fr.sword.naiad.commons.webtest.annotation.WebTest;

/**
 * Test de la page de login.
 * 
 * @author SPL
 */
public class Test01LoginPage extends AbstractWebTest {

	
	@WebTest(description="Test des liens sur la page de login", order =10)
	@TestDocumentation(categories={"Utilisateur", "Accueil"})
	public void testLoginPage() {
		
		LoginPage loginPage = goToLoginPage();
		
		loginPage.checkLinkNewPassword();
		
		loginPage.checkLinkRenseignement();
		
		loginPage.checkIdentificationPlateforme();
		
		NewPasswordPage newpasswordpage = loginPage.goToNewPasswordPage();
		
		newpasswordpage.checkLinkRenseignement();
		
		newpasswordpage.backToLoginPage();
		
	}
	
	@WebTest(description = "Test de connexion", order =20)
	@TestDocumentation(categories={"Utilisateur", "Accueil"})
	public void testDoLogin() {
		
		final String username = "adminsgg";
		final String password = username;
		
		CorbeillePage corbeillePage = login(username, password, CorbeillePage.class);
		
		corbeillePage.checkIdentificationPlateforme();
		
		logout();

	}

}
