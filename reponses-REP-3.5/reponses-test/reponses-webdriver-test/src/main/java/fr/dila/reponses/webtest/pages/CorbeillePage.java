package fr.dila.reponses.webtest.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import fr.dila.reponses.webtest.pages.dossier.DossierPage;
import fr.dila.reponses.webtest.pages.recherche.RechercheResultPage;
import fr.dila.st.webdriver.framework.STBy;

/**
 * Page des corbeilles.
 * 
 * @author SPL
 */
public class CorbeillePage extends ReponsesPage {

	private final static String MESSAGE_ACCUEIL_BANDEAU = "Mon identifiant dans REPONSES : ";
	
	@FindBy(how = How.XPATH, using = "//div[@class='plateformeIdentification']")
	private WebElement identificationPlateformeDiv;
	
	public CorbeillePage(){
		super();
	}
	
	@Override
	public void checkIfLoaded() {
		waitForPageSourcePart(By.xpath("//div[@class='userMailboxes']"), TIMEOUT_IN_SECONDS);
	}
	
	public boolean isConnected(String username) {
		return getDriver().getPageSource().contains(MESSAGE_ACCUEIL_BANDEAU + username);
	}
	
	/**
	 * Retourne true si la corbeille contient des questions.
	 * @return
	 */
	public boolean hasQuestions() {
	    List<WebElement> questionsRows = getDriver().findElements(By.cssSelector("#corbeille_content .dataOutput tbody tr"));
        return questionsRows.size() > 0;
	}

	/**
	 * Ouvre le dossier
	 * @param nomDossier
	 */
	public DossierPage openDossier(String nomDossier) {
		By by = By.partialLinkText(nomDossier);
		WebElement elt = findElement(by);
		return clickToPage(elt, DossierPage.class);
	}

	/**
	 * Ouvre une corbeille d'un ministère
	 * @param ministere
	 */
	public void openCorbeille(String ministere){
		By ministereLinkBy = STBy.partialSpanText(ministere);
		WebElement ministereCorbeilleElt = findElement(ministereLinkBy);
		ministereCorbeilleElt.click();
	}
	
	/**
	 * Ouvre un tâche d'une corbeille ministère (par exemple : Pour arbitrage)
	 * @param ministere
	 */
	public RechercheResultPage openTacheCorbeille(String tache){
		By tacheBy = STBy.partialLinkText(tache);
		waitForPageSourcePartDisplayed(tacheBy, 10);
		WebElement tacheElt = findElement(tacheBy);
		return clickToPage(tacheElt, RechercheResultPage.class);
	}

	/**
	 * Vrai si la corbeille est présent
	 * @param corbeilleName
	 * @return
	 */
	public boolean hasCorbeille(String corbeilleName) {
		By corbeilleLinkBy = STBy.partialSpanText(corbeilleName);
		return containsEltLocatedBy(corbeilleLinkBy);
	}
	
	public CorbeillePage refreshCorbeilles() {
		WebElement refreshCorbeilleBtn = findElement(By.xpath("//form[@id='mailboxMenuForm']/ul/li/a"));
		return clickToPage(refreshCorbeilleBtn, CorbeillePage.class);
	}
	
	public void checkIdentificationPlateforme() {
		getFlog().check("Test la présence de la div identification plateforme");
		if(identificationPlateformeDiv == null || !identificationPlateformeDiv.getText().contains("REPONSES")){
			getFlog().checkFailed("L'identification de la plateforme n'est pas bonne");
		}
	}

}
