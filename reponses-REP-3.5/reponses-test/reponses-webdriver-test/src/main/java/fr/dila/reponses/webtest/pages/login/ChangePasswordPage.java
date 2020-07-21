package fr.dila.reponses.webtest.pages.login;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import fr.dila.reponses.webtest.pages.ReponsesPage;
import fr.sword.naiad.commons.webtest.WebPage;

public class ChangePasswordPage extends ReponsesPage{

	@FindBy(how = How.ID, id = "resetPasswordUser:firstPassword")
	private WebElement firstPassword;
	
	@FindBy(how = How.ID, id = "resetPasswordUser:secondPassword")
	private WebElement secondPassword;
	
	@FindBy(how = How.ID, id = "resetPasswordUser:save")
	private WebElement saveBtn;

	public  <T extends WebPage>  T changePasswordAndSubmit(String password, Class<T> pageClazz){
		fillField("firstPassword", firstPassword, password);
		fillField("secondPassword", secondPassword, password);
		saveBtn.click();
		return getPage(pageClazz);
	}

	public Boolean verifierRejetMotDePasse() {
		String nameAction = String.format("Regarde si le mot de passe est rejeté");
		getFlog().startAction(nameAction);
		
		boolean elementFound = false;
		WebElement elem = null;
		By errorMessage = By.xpath("//*[contains(.,\"Conformément aux recommandations de l'ANSSI, le mot de passe doit respecter les critères suivants :\")]");
		try {
			elem = getDriver().findElement(errorMessage);
		} catch (Exception e) {
		}
		if (elem != null) {
			elementFound = true;
		}
		getFlog().endAction();
		return elementFound;
	}
	
}
