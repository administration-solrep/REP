package fr.dila.reponses.webtest.pages.suivi;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import fr.dila.reponses.webtest.pages.ReponsesPage;
import fr.dila.st.webdriver.framework.CustomFindBy;
import fr.dila.st.webdriver.framework.CustomHow;

/**
 * La page de création d'une nouvelle requête
 * @author jgomez
 *
 */
public class CreerNouvelleRequetePage extends ReponsesPage{
	
	@CustomFindBy(how = CustomHow.LABEL_ON_NUXEO_LAYOUT , using = "Titre")
	private WebElement titreElt;

	@FindBy(how = How.XPATH, using = ".//input[@value=\"Sauvegarder la requête\"]")
	private WebElement sauvegardeRequeteBtn;
	
	public void setTitre(String titre){
		fillField("Titre", titreElt, titre);
	}
	
	public SuiviPage sauvegarderRequete() {
		return clickToPage(sauvegardeRequeteBtn, SuiviPage.class);
	}
	
}
