package fr.dila.reponses.webtest.pages.administration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fr.dila.reponses.webtest.pages.ReponsesPage;
import fr.dila.reponses.webtest.pages.widget.organigramme.OrganigrammeContextMenu;
import fr.dila.st.webdriver.framework.CustomFindBy;
import fr.dila.st.webdriver.framework.CustomHow;
import fr.dila.st.webdriver.framework.STBy;

/**
 * La page de gestion de l'organigramme
 * @author jgz
 *
 */
public class GestionOrganigrammePage extends ReponsesPage{

	@CustomFindBy(how = CustomHow.LABEL_TEXT, using="Eléments actifs et inactifs")
	private WebElement elementsActifsEtInactifsBtn;
	
	@CustomFindBy(how = CustomHow.LABEL_TEXT, using="Eléments actifs seulement")
	private WebElement elementsActifsBtn;
	
	public OrganigrammeContextMenu rightClickOn(String nomElementOrganigramme) {
		By elementOrganigrammeBy = STBy.partialSpanText(nomElementOrganigramme);
		WebElement elementOrganigramme = findElement(elementOrganigrammeBy);
		return rightClickOnOrganigramme(elementOrganigramme);
	}
	
	public void unfold(String nomElementOrganigramme) {
		By elementOrganigrammeBy = STBy.nodeNameForToggleBtnOrga(nomElementOrganigramme);
		waitForPageSourcePart(elementOrganigrammeBy, TIMEOUT_IN_SECONDS);
		WebElement elementOrganigramme = findElement(elementOrganigrammeBy);
		unfold(elementOrganigramme);
	}

	/**
	 * Clique droit sur un élement de l'organigramme
	 * @param element un element de l'organigramme
	 */
	public OrganigrammeContextMenu rightClickOnOrganigramme(WebElement element){
		return rightClick(element, OrganigrammeContextMenu.class);
	}

	/**
	 * Déplie un élement de l'organigramme
	 * @param element
	 */
	private void unfold(WebElement element) {
		element.click();
	}
	
	/**
	 * Rafraichir l'organigramme
	 * @throws InterruptedException 
	 */
	public void refresh() throws InterruptedException {
		elementsActifsEtInactifsBtn.click();
		Thread.sleep(1000);
		elementsActifsBtn.click();
		Thread.sleep(1000);
	}
	
	/**
	 * Vérifier la présence d'un profil donné
	 * @param profilTest
	 */
	public Boolean verifierRejetIdentifiant() {
		String nameAction = String.format("Regarde si l'identifiant est rejeté à cause de sa taille");
		getFlog().startAction(nameAction);
		By errorMessage = By.className("errorFeedback");
		boolean containsEltLocatedBy = containsEltLocatedBy(errorMessage);
		if (!containsEltLocatedBy) {
			errorMessage = By.className("errorMessage");
			containsEltLocatedBy = containsEltLocatedBy(errorMessage);
		}
		getFlog().endAction();
		return containsEltLocatedBy;
	}

}
