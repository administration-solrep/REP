package fr.dila.reponses.webtest.pages.administration;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import fr.dila.reponses.webtest.pages.ReponsesPage;

/**
 * Gestion de l'état de l'application
 * @author jbrunet
 *
 */
public class GestionRestrictionAccesPage extends ReponsesPage{

	@FindBy(how = How.XPATH, using = "//input[@value='TRUE']")
	private WebElement activerRestrictionAccesBtn;
	
	@FindBy(how = How.XPATH, using = "//input[@value='FALSE']")
	private WebElement desactiverRestrictionAccesBtn;
	
	@FindBy(how = How.XPATH, using = "//input[@value='Enregistrer']")
	private WebElement enregistrerBtn;
	
	@FindBy(how = How.XPATH, using = "//textarea[@class='dataInputText']")
	private WebElement descriptionRestriction;
	
	public void setDescriptionRestrictionAcces(String description){
		fillField("Description de la restriction", descriptionRestriction, description);
	}
	
	public GestionRestrictionAccesPage activerRestrictionAcces(){
		return click(activerRestrictionAccesBtn, GestionRestrictionAccesPage.class);
	}
	
	public GestionRestrictionAccesPage desactiverRestrictionAcces(){
		return click(desactiverRestrictionAccesBtn, GestionRestrictionAccesPage.class);
	}
	
	public GestionRestrictionAccesPage enregistrerChangements() {
		return clickToPage(enregistrerBtn, GestionRestrictionAccesPage.class);
	}

}
