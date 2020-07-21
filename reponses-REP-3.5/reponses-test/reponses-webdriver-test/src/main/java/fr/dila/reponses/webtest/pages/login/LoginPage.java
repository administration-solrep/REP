package fr.dila.reponses.webtest.pages.login;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import fr.dila.reponses.webtest.pages.ReponsesPage;
import fr.sword.naiad.commons.webtest.WebPage;
import junit.framework.Assert;

/**
 * Classe représentant la page de login de Réponses
 * 
 * @author SPL
 */
public class LoginPage extends ReponsesPage {

	public static final String LANGUAGE_FR = "fr";
	
	public static final String NEW_PASSWORD_LINKTEXT = "Cliquez ici";

	public static final String RENSEIGNEMENT_LINKTEXT = "Pour tout renseignement sur les conditions";

	/**
	 * Valeur extraite du template de parametre dans reponses-content-template-contrib.xml
	 */
	private static final String RENSEIGNEMENT_LINK_URL = "http://test.com/renseignement/renseignement.html";
	
	@FindBy(how = How.ID, id = "username")
	private WebElement usernameElt;

	@FindBy(how = How.ID, id = "password")
	private WebElement passwordElt;

	@FindBy(how = How.XPATH, using = "//input[@class='login_button']")
	private WebElement loginButtonElt;
	
	@FindBy(how = How.ID, id = "solon_identification_plateforme_div")
	private WebElement identificationPlateformeDiv;


	public LoginPage() {
		super();
	}

	@Override
	public void checkIfLoaded() {
		waitForPageSourcePart(By.id("password"), TIMEOUT_IN_SECONDS);
	}

	public void setUsername(final String username) {
		fillField("Identifiant", usernameElt, username);
	}

	public void setPassword(final String password) {
		fillField("Mot de passe", passwordElt, password);
	}

	public void submit(){
 		getFlog().actionClickButton("Connexion");
        loginButtonElt.click();
	}

	
	public boolean hasLinkNewPassword(){
		return hasElement(By.partialLinkText(NEW_PASSWORD_LINKTEXT));
	}
	
	private boolean hasLinkRenseignement() {
		final List<WebElement> webElements = getElementsBy(By.partialLinkText(RENSEIGNEMENT_LINKTEXT));
		if (webElements.isEmpty()) {
			return false;
		}
		WebElement webElement = webElements.get(0);
		String url = webElement.getAttribute("href");
		return url != null && url.equals(RENSEIGNEMENT_LINK_URL);
	}
	
	/**
	 * Vérifie la présence du message sur la page de login
	 * 
	 * @param message (le message à rechercher)
	 * @return
	 */
	public boolean isTextPresent(String message) {
		return getDriver().getPageSource().contains(message);
	}

	public void checkLinkNewPassword(){
		getFlog().check("Test la présence du lien de demande de mot de passe ("+NEW_PASSWORD_LINKTEXT+")");
		if(!hasLinkNewPassword()){
			getFlog().checkFailed("Le lien de demande de mot de passe ("+NEW_PASSWORD_LINKTEXT+") n'est pas présent");
		}
	}
	
	public void checkLinkRenseignement(){
		getFlog().check("Test la présence du lien ("+RENSEIGNEMENT_LINKTEXT+"...)");
		if(!hasLinkRenseignement()){
			getFlog().checkFailed("Le lien (" + RENSEIGNEMENT_LINKTEXT+"...) n'est pas présent");
		}
	}
	
	public NewPasswordPage goToNewPasswordPage(){
		WebElement elt = getDriver().findElement(By.partialLinkText(NEW_PASSWORD_LINKTEXT));
		getFlog().actionClickLink(NEW_PASSWORD_LINKTEXT);
		elt.click();
		return getPage(NewPasswordPage.class);
	}
	
	/**
	 * Soumet le formulaire de login
	 */
	public <T extends WebPage> T submitLogin(final String login, final String password, final Class<T> pageClazz) {
		getFlog().startAction(String.format("Connexion en tant que [%s]", login));
		waitForFieldValue("username", "");
		sleep(2);
		setUsername(login);
		setPassword(password);
		
        // Pour éviter l'erreur "Identifiant non renseigné", on attend que les infos soient bien présentes sur la page
        try {
        	waitForFieldValue("username", login);
        	waitForFieldValue("password", password);
        } catch (RuntimeException e) {
        	// Il est déjà arrivé qu'un des champs ne soit pas ajouté dans la page (étrange, mais oui !)
        	// dans ce cas, on tente de remettre les champs et on retente
        	getFlog().action("#SAUVETAGE#: le mot de passe ("+password+") ou l'identifiant ("+login+") n'est pas renseigné sur la page, alors on recommence"); 
            setUsername(login);
            setPassword(password);
        	waitForFieldValue("password", password);
        }

        Assert.assertEquals(password, getDriver().findElement(By.id("password")).getAttribute("value"));
        
		submit();
		getFlog().endAction();
		return getPage(pageClazz);
	}

	public void checkIdentificationPlateforme() {
		getFlog().check("Test la présence de la div identification plateforme");
		if(identificationPlateformeDiv == null || !identificationPlateformeDiv.getText().contains("REPONSES")){
			getFlog().checkFailed("L'identification de la plateforme n'est pas bonne");
		}
	}

}
