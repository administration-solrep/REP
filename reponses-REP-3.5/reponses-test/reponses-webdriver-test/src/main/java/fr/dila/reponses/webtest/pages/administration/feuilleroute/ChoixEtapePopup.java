package fr.dila.reponses.webtest.pages.administration.feuilleroute;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import fr.dila.reponses.webtest.pages.ReponsesPage;

public class ChoixEtapePopup extends ReponsesPage{

	@FindBy(how=How.PARTIAL_LINK_TEXT, using="Ajouter une étape")
	private WebElement ajouterUneEtapeBtn;
	
	@FindBy(how=How.PARTIAL_LINK_TEXT, using="Créer des branches")
	private WebElement creerDesBranchesBtn;
	
	
	/**
	 * Aller vers la page de création d'une nouvelle étape
	 * @return
	 */
	public CreerUneNouvelleEtapePage ajouterUneEtape() {
		return clickToPage(ajouterUneEtapeBtn, CreerUneNouvelleEtapePage.class);
	}
	
	public void creerDesBranches(Integer brancheCount) {
		CreerBranchePage creerBranchePage = clickToPage(creerDesBranchesBtn, CreerBranchePage.class);
		creerBranchePage.setbrancheCount(brancheCount);
		creerBranchePage.creer();
	}
	
}
