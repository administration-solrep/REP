package fr.dila.reponses.webtest.pages.administration.utilisateur;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fr.dila.reponses.webtest.pages.ReponsesPage;
import fr.dila.st.webdriver.framework.CustomFindBy;
import fr.dila.st.webdriver.framework.CustomHow;
import fr.dila.st.webdriver.helper.NameShortener;

/**
 * La recherche des utilisateurs
 * @author jgomez
 *
 */
public class RechercheUtilisateurPage extends ReponsesPage{

	private static final int TIMEOUT = 10;

	private static final String ID_INPUT_MIN = "searchUserForm:nxl_recherche_utilisateur:nxw_ministere_suggest";

	@CustomFindBy( how = CustomHow.LABEL_ON_NUXEO_LAYOUT, using = "Identifiant")
	private WebElement identifiantElt;
	
	@CustomFindBy( how = CustomHow.LABEL_ON_NUXEO_LAYOUT, using = "Prénom")
	private WebElement prenomElt;
	
	@CustomFindBy( how = CustomHow.LABEL_ON_NUXEO_LAYOUT, using = "Nom")
	private WebElement nomElt;
	
	@CustomFindBy( how = CustomHow.INPUT_VALUE, using = "Recherche")
	private WebElement validerBtn;
	
	@CustomFindBy(how=CustomHow.INPUT_VALUE, using = "Exporter les résultats de recherche sous format Excel")
	private WebElement exportBtn;
	
	
	public void setPrenom(String prenom){
		fillField("Prénom", prenomElt, prenom);
	}
	
	public void setNom(String nom) {
		fillField("Nom", nomElt, nom);		
	}
	
	public void setIdentifiant(String identifiant){
		fillField("Identifiant", identifiantElt, identifiant);
	}

	public void setMinistere(String ministere){
		By ministereBy = By.id(ID_INPUT_MIN);
		setAutocompleteValue(new NameShortener(ministere,0), ministereBy, 0, true);
	}
	
	public void rechercher(){
		clickBtn(validerBtn);
	}

	public Boolean verifierResultContient(String utilisateur) {
		By resultLien = By.partialLinkText(utilisateur);
		waitForPageSourcePartDisplayed(resultLien, TIMEOUT);
		return containsEltLocatedBy(resultLien);
		
	}
	
	public void exporter(){
		clickBtn(exportBtn);
	}
}
