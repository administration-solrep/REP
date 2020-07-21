package fr.dila.reponses.webtest.pages.administration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import fr.dila.reponses.webtest.pages.ReponsesPage;
import fr.dila.reponses.webtest.pages.administration.feuilleroute.CreerUnModeleFDRPage;
import fr.dila.st.webdriver.framework.STBy;

/**
 * Gestion des modèles de feuille de route
 * 
 * @author jgz
 * 
 */
public class GestionModeleFDRPage extends ReponsesPage {

	@FindBy(how = How.XPATH, using = "//input[@value='Créer un modèle']")
	private WebElement	creerUnModeleBtn;

	@FindBy(how = How.XPATH, using = "//input[@value='Rechercher']")
	private WebElement	rechercherBtn;

	private WebElement	rechercheRapide;

	public void setRechercheRapide(String query) {
		By rechercheRapideBy = STBy.labelOnNuxeoLayoutForInput("Intitulé");
		rechercheRapide = findElement(rechercheRapideBy);
		fillField("Intitulé", rechercheRapide, query);
	}

	public CreerUnModeleFDRPage creerUnModele() {
		return clickToPage(creerUnModeleBtn, CreerUnModeleFDRPage.class);
	}

	public GestionModeleFDRPage rechercher() {
		return clickToPage(rechercherBtn, GestionModeleFDRPage.class);
	}

	/**
	 * Dupliquer la feuille de route à partir d'un nom d'un modèle de fdr
	 * 
	 * @param nomFdrSubstitution
	 * @return
	 */
	public CreerUnModeleFDRPage dupliquerFeuilleRoute(String nomFdrSubstitution) {
		By fdrBy = By.partialLinkText(nomFdrSubstitution);
		WebElement linkFdr = findElement(fdrBy);
		linkFdr.click();
		// WebElement dupliquerBtn =
		// getDriver().findElement(By.xpath("//img[@src ='/reponses/img/icons/action_duplicate.png']"));
		WebElement dupliquerBtn = getDriver().findElement(By.partialLinkText("Dupliquer"));
		return clickToPage(dupliquerBtn, CreerUnModeleFDRPage.class);
	}

}
