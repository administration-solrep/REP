package fr.dila.reponses.webtest.pages.administration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import fr.dila.reponses.webtest.pages.ReponsesPage;
import fr.dila.reponses.webtest.pages.administration.utilisateur.CreerUtilisateurPage;
import fr.dila.reponses.webtest.pages.administration.utilisateur.UtilisateurPage;

/**
 * La page de gestion des utilisateurs 
 * @author jgz
 *
 */
public class GestionUtilisateurPage extends ReponsesPage{

	private static final String ID_BTN_RECHERCHE = "searchForm:searchButton";

	private static final String ID_CHAMP_RECHERCHE = "searchForm:searchText";

	private static final int TIMEOUT = 10;

	@FindBy(how = How.PARTIAL_LINK_TEXT, using="Ajouter un utilisateur")
	private WebElement ajouterUnUtilisateurBtn;
	
	@FindBy(how = How.ID, using = ID_BTN_RECHERCHE)
	private WebElement rechercheBtn;

	@FindBy(how = How.ID, using = ID_CHAMP_RECHERCHE)
	private WebElement champRechercheElt;
	
	/**
	 * Aller à la page ajouter un utilisateur 
	 * @return
	 */
	public CreerUtilisateurPage ajouterUnUtilisateur(){
		return clickToPage(ajouterUnUtilisateurBtn,  CreerUtilisateurPage.class);
	}

	public UtilisateurPage rechercherUtilisateur(String utilisateur) {
		setChampRecherche(utilisateur);
		clickToPage(rechercheBtn, UtilisateurPage.class);
		By utilisateurLink = By.partialLinkText(utilisateur);
		waitForPageSourcePartDisplayed(utilisateurLink, TIMEOUT);
		return clickToPage(findElement(utilisateurLink), UtilisateurPage.class);
	}

	private void setChampRecherche(String utilisateur) {
		fillField("Champ recherche", champRechercheElt, utilisateur);
	}



}
