package fr.dila.reponses.webtest.helper;

import java.io.InputStream;

import javax.xml.bind.JAXBException;

import junit.framework.Assert;
import fr.dila.reponses.rest.api.WSQuestion;
import fr.dila.reponses.webtest.constant.ConstantesOrga;
import fr.dila.reponses.webtest.model.UserManager;
import fr.dila.reponses.webtest.pages.CorbeillePage;
import fr.dila.reponses.webtest.pages.login.LoginPage;
import fr.dila.reponses.webtest.utils.WsUtils;
import fr.dila.st.webdriver.model.STUser;
import fr.sword.naiad.commons.webtest.WebPage;
import fr.sword.naiad.commons.webtest.logger.FunctionalLogger;
import fr.sword.xsd.commons.TraitementStatut;
import fr.sword.xsd.reponses.EnvoyerQuestionsRequest;
import fr.sword.xsd.reponses.EnvoyerQuestionsResponse;
import fr.sword.xsd.reponses.ResultatTraitement;

public class AbstractWebTest extends fr.sword.naiad.commons.webtest.helper.AbstractWebTest {

	public CorbeillePage loginAsAdmin() {
		getFlog().action("Connexion en tant qu'admin");
		return login("adminsgg", "adminsgg", CorbeillePage.class);
	}

	public static String getLoginUrl() {
		String appUrl = UrlHelper.getInstance().getReponsesUrl();
		String loginUrl = appUrl;
		if (!loginUrl.endsWith("/")) {
			loginUrl += "/";
		}
		loginUrl += "login.jsp";
		return loginUrl;
	}

	public static String getLogoutUrl() {
		String appUrl = UrlHelper.getInstance().getReponsesUrl();
		String loginUrl = appUrl;
		if (!loginUrl.endsWith("/")) {
			loginUrl += "/";
		}
		loginUrl += "logout";
		return loginUrl;
	}

	public LoginPage goToLoginPage() {
		String loginUrl = getLoginUrl();
		getFlog().action("Acces à la page de login [" + loginUrl + "]");
		return WebPage.goToPage(getDriver(), getFlog(), loginUrl, LoginPage.class);
	}

	public <T extends WebPage> T login(String username, String password, Class<T> pageClazz) {
		LoginPage loginPage = goToLoginPage();
		return loginPage.submitLogin(username, password, pageClazz);
	}

	public <T extends WebPage> T login(STUser sTUser, Class<T> pageClazz) {
		LoginPage loginPage = goToLoginPage();
		return loginPage.submitLogin(sTUser.getLogin(), sTUser.getPassword(), pageClazz);
	}

	public LoginPage logout() {
		String logoutUrl = getLogoutUrl();
		getFlog().action("Acces au logout [" + logoutUrl + "]");
		return WebPage.goToPage(getDriver(), getFlog(), logoutUrl, LoginPage.class);
	}

	/**
	 * Retourne le loggueur fonctionnel
	 */
	@Override
	public FunctionalLogger getFlog() {
		return (FunctionalLogger) super.getFlog();
	}

	/**
	 * Login en tant que bdc
	 * 
	 * @return
	 */
	public CorbeillePage loginBdc() {
		UserManager manager = new UserManager();
		STUser bdc = manager.getUserFrom(ConstantesOrga.POSTE_BDC_ECO);
		CorbeillePage corbeillePage = login(bdc, CorbeillePage.class);
		return corbeillePage;
	}

	public CorbeillePage loginBdcJustice() {
		getFlog().action("Connexion en tant qu'admin ministeriel");
		return login("DGCIS_contributeur", "DGCIS_contributeur", CorbeillePage.class);
	}

	public CorbeillePage AdminMin_Justice() {
		getFlog().action("Connexion en tant qu'admin ministeriel");
		return login("ASAFFAR", "ASAFFAR", CorbeillePage.class);
	}

	public CorbeillePage contributeurMin_Justice() {
		getFlog().action("Connexion en tant que contributeur ministeriel");
		return login("TDAVIAU", "TDAVIAU", CorbeillePage.class);
	}

	public CorbeillePage loginAsAdminFonctionnel() {
		getFlog().action("Connexion en tant qu'admin fonctionnel");
		return login("adminsgg", "adminsgg", CorbeillePage.class);
	}

	public CorbeillePage AdminMin_Ecologie() {
		getFlog().action("Connexion en tant qu'admin ministeriel");
		return login("ecologie_bdc", "ecologie_bdc", CorbeillePage.class);
	}

	public CorbeillePage contributeur1Min_Ecologie() {
		getFlog().action("Connexion en tant que contributeur ministeriel");
		return login("DGFIP_contributeur", "DGFIP_contributeur", CorbeillePage.class);
	}

	public CorbeillePage contributeur2Min_Ecologie() {
		getFlog().action("Connexion en tant que contributeur ministeriel");
		return login("ecologie_dgemp", "ecologie_dgemp", CorbeillePage.class);
	}

	// TODO: Injecter un répertoire
	public void inject(WSQuestion wsQuestion, String filepath) throws JAXBException, Exception {
		InputStream stream = getClass().getResourceAsStream(filepath);
		Assert.assertNotNull(
				String.format("Le fichier de la question n'est pas à l'emplacement indiqué : %s", filepath), stream);
		Assert.assertNotNull(stream);
		EnvoyerQuestionsRequest request = WsUtils.buildRequestFromFile(stream, EnvoyerQuestionsRequest.class);
		Assert.assertNotNull(request);
		EnvoyerQuestionsResponse response = wsQuestion.envoyerQuestions(request);
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getResultatTraitement());
		for (ResultatTraitement resultat : response.getResultatTraitement()) {
			Assert.assertTrue("L'injection a échoué ",
					TraitementStatut.OK.toString().equals(resultat.getStatut().toString()));
		}
	}

	/**
	 * Injecte des dossiers de l'AN
	 * 
	 * @param filepath
	 *            le chemin du fichier
	 * @throws JAXBException
	 * @throws Exception
	 */
	public void injectAN(String filepath) throws JAXBException, Exception {
		WSQuestion wsAn = WsUtils.getWSQuestionAN();
		inject(wsAn, filepath);
	}

	/**
	 * Injecte des dossiers de l'AN
	 * 
	 * @param filepath
	 *            le chemin du fichier
	 * @throws JAXBException
	 * @throws Exception
	 */
	public void injectSenat(String filepath) throws JAXBException, Exception {
		WSQuestion wsSenat = WsUtils.getWSQuestionSenat();
		inject(wsSenat, filepath);
	}

}
