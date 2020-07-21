package fr.dila.reponses.webtest.pages.administration.delegation;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fr.dila.reponses.webtest.pages.ReponsesPage;
import fr.dila.st.webdriver.framework.CustomFindBy;
import fr.dila.st.webdriver.framework.CustomHow;

/**
 * La page de délégation des droits
 * @author user
 *
 */
public class DelegationDroitsPage extends ReponsesPage{

	@CustomFindBy(how = CustomHow.INPUT_VALUE, using = "Créer une délégation")
	private WebElement creerUneDelegationBtn;
	
	
	public CreerDelegationPage goToCreerUneDelegation() {
		return clickToPage(creerUneDelegationBtn, CreerDelegationPage.class);
	}

	/**
	 * Retourne vrai si la page contient une délégation
	 * @param utilisateur l'utilisateur sur lequel a été créé la délégation
	 * @return
	 */
	public boolean hasDelegation(String utilisateur) {
		By delegationBy = By.partialLinkText(utilisateur);
		return containsEltLocatedBy(delegationBy);
	}

}
