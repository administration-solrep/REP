package fr.dila.reponses.webtest.pages.administration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import fr.dila.reponses.webtest.pages.ReponsesPage;
import fr.dila.reponses.webtest.pages.administration.profil.CreerProfilPage;

/**
 * La page de gestion des profils
 * @author jgomes
 *
 */
public class GestionProfilsPage extends ReponsesPage{

	private static final String CREER_PROFIL_BTN = "createProfileActionsForm:createProfileButton";
	
	@FindBy(how = How.ID, using = CREER_PROFIL_BTN)
	private WebElement ajouterProfilBtn;
	
	
	public CreerProfilPage goToAjouterProfil() {
		return clickToPage(ajouterProfilBtn, CreerProfilPage.class);
	}

	/**
	 * Vérifier la présence d'un profil donné
	 * @param profilTest
	 */
	public Boolean verifierPresenceProfil(String profil) {
		String nameAction = String.format("Regarde si le profil %s est dans la liste des profils", profil);
		getFlog().startAction(nameAction);
		By linkProfil = By.partialLinkText(profil);
		boolean containsEltLocatedBy = containsEltLocatedBy(linkProfil);
		getFlog().endAction();
		return containsEltLocatedBy;
	}
	
}
