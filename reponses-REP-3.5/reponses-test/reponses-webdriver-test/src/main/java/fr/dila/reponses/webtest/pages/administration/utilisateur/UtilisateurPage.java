package fr.dila.reponses.webtest.pages.administration.utilisateur;

import java.rmi.UnexpectedException;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import fr.dila.reponses.webtest.pages.ReponsesPage;
import fr.dila.st.webdriver.framework.CustomFindBy;
import fr.dila.st.webdriver.framework.CustomHow;
import fr.dila.st.webdriver.helper.NameShortener;

/**
 * La page de l'utilisateur
 * 
 * @author jgomez
 * 
 */
public class UtilisateurPage extends ReponsesPage {

	private static final String	PROFIL_INPUT_ID	= "editUser:nxl_user:nxw_groups_suggest";

	@CustomFindBy(how = CustomHow.INPUT_VALUE, using = "Enregistrer")
	private WebElement			enregistrerBtn;

	@FindBy(how = How.PARTIAL_LINK_TEXT, using = "Modifier")
	private WebElement			ongletModification;

	@FindBy(how = How.PARTIAL_LINK_TEXT, using = "Supprimer")
	private WebElement			supprimer;

	@FindBy(how = How.PARTIAL_LINK_TEXT, using = "OK")
	private WebElement			OK;

	public void enregistrer() {
		clickBtn(enregistrerBtn);
	}

	public void passerEnModeModification() {
		clickLien(ongletModification);
	}

	public void ajouterProfil(String profilTest) {
		By profilInputIdBy = By.id(PROFIL_INPUT_ID);
		setAutocompleteValue(new NameShortener(profilTest), profilInputIdBy, 0, true);
	}

	public void supprimerProfil(String profilTest) {
		By profilInputIdBy = By.id(profilTest);
		WebElement postElt = findElement(profilInputIdBy);
		postElt.click();
	}

	public void supprimer() throws UnexpectedException {
		clickLien(supprimer);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			throw new UnexpectedException("Timer interrompu");
		}
		Alert alert = getDriver().switchTo().alert();
		alert.accept();
	}
}
