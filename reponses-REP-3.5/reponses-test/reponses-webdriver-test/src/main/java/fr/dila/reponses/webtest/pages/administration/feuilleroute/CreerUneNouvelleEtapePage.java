package fr.dila.reponses.webtest.pages.administration.feuilleroute;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.Select;

import fr.dila.reponses.webtest.pages.ReponsesPage;
import fr.dila.st.webdriver.helper.NameShortener;

public class CreerUneNouvelleEtapePage extends ReponsesPage{
	
	@FindBy(id="document_create:nxl_routing_task_detail:routing_task_type")
	private WebElement typeElt;
	
	@FindBy(name="document_create:nxl_routing_task_detail:nxw_routing_task_deadline:nxw_routing_task_deadline_from")
	private WebElement echeanceIndicativeElt;
	
	@FindBy(id="document_create:nxl_routing_task_detail:nxw_routing_task_automatic_validation")
	private WebElement validationAutomatiqueElt;
	
	@FindBy(id="document_create:nxl_routing_task_detail:nxw_routing_task_obligatoire_sgg")
	private WebElement obligatoireSGGElt;
	
	@FindBy(id="document_create:nxl_routing_task_detail:nxw_routing_task_obligatoire_ministere")
	private WebElement obligatoireMinistereElt;
	
	@FindBy(how = How.XPATH, using = "//input[@value='Créer']")
	public WebElement creerBtn;
	
	
	public void setType(String typeEtape){
		Select selectType = new Select(typeElt);
		selectType.selectByVisibleText(typeEtape);
	}
	
	public void setDestinataire(NameShortener shortener){
		By destinataireBy = By.id("document_create:nxl_routing_task_detail:nxw_routing_task_distribution_mailbox_suggest");
		setAutocompleteValue(shortener, destinataireBy);
	}
	
	public void setEcheanceIndicative(String echeance){
		echeanceIndicativeElt.sendKeys(echeance);
	}
	
	public void toggleValidationAutomatique(){
		validationAutomatiqueElt.click();
	}
	
	public void toggleObligatoireSGG(){
		obligatoireSGGElt.click();
	}
	
	public void toggleObligatoireMinistere(){
		obligatoireMinistereElt.click();
	}
	
	public void creer(){
		creerBtn.click();
	}
	
}
