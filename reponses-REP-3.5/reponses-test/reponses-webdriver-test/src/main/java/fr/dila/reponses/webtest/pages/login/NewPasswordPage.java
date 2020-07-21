package fr.dila.reponses.webtest.pages.login;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import fr.dila.reponses.webtest.pages.ReponsesPage;
import fr.sword.naiad.commons.webtest.WebPage;

/**
 * Page représentant la page de demande de nouveau mot de passe
 * 
 * @author SPL
 *
 */
public class NewPasswordPage extends ReponsesPage {

	public static final String NEW_PASSWORD_TITLE = "Demande mot de passe";
	
	public static final String BACK_CONNECTION_BUTTON = "Retour à la connexion";
	
	public static final String RENSEIGNEMENT_LINKTEXT = LoginPage.RENSEIGNEMENT_LINKTEXT;
	
	@FindBy(how = How.XPATH, xpath = "//input[@name='Cancel']")
	private WebElement cancelButtonElt;
	
	@FindBy(how = How.ID, id = "validateBtn")
	private WebElement submitButtonElt;
	
	@FindBy(how = How.ID, id = "username")
	private WebElement usernameElt;

	@FindBy(how = How.ID, id = "email")
	private WebElement emailElt;

	
	public NewPasswordPage(){
		super();
	}
	
	@Override
	public void checkIfLoaded() {
		waitForPageSourcePart(By.xpath("//td[contains(.,'"+NEW_PASSWORD_TITLE+"')]"), TIMEOUT_IN_SECONDS);
	}
	
	public LoginPage backToLoginPage(){
		getFlog().actionClickButton(BACK_CONNECTION_BUTTON);
		cancelButtonElt.click();
		return getPage(LoginPage.class);
	}
	
	public void setUsername(String username){
		fillField("Identifiant", usernameElt, username);
	}
	
	public void setEmail(String email){
		fillField("Mèl", emailElt, email);
	}
	
	public void submit(){
		getFlog().actionClickButton("Valider");
		submitButtonElt.click();
	}
	
	public <T extends WebPage> T askNewPassword(String username, String email, Class<T> pageClazz){
		setUsername(username);
		setEmail(email);
		submit();
		return getPage(pageClazz);
	}
	
	public boolean hasLinkRenseignement(){
		return hasElement(By.partialLinkText(RENSEIGNEMENT_LINKTEXT));
	}
	
	public void checkLinkRenseignement(){
		getFlog().check("Test la présence du lien ("+RENSEIGNEMENT_LINKTEXT+"...)");
		if(!hasLinkRenseignement()){
			getFlog().checkFailed("Le lien ("+RENSEIGNEMENT_LINKTEXT+"...) n'est pas présent");
		}
	}
	
	
}
