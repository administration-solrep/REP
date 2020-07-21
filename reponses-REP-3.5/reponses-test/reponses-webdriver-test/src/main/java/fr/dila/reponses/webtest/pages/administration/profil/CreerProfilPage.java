package fr.dila.reponses.webtest.pages.administration.profil;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.Select;

import fr.dila.reponses.webtest.pages.ReponsesPage;
import fr.dila.st.webdriver.framework.CustomFindBy;
import fr.dila.st.webdriver.framework.CustomHow;

public class CreerProfilPage extends ReponsesPage{
	
	private static final String ID_ADD_BTN = "createProfile:nxl_profile:nxw_basefunctions_addBaseFunction";

	private static final String ID_FUNCTION_SELECT = "createProfile:nxl_profile:nxw_basefunctions_selectBaseFunction";

	public static final String ID_PROFIL_INPUT_ELT = "createProfile:nxl_profile:nxw_profilename";
	
	@FindBy(how = How.ID, using = ID_PROFIL_INPUT_ELT)
	private WebElement profilInputElt;
	
	@FindBy(how = How.ID, using = ID_FUNCTION_SELECT)
	private WebElement functionAttribueSelect;
	
	@FindBy(how = How.ID, using = ID_ADD_BTN)
	private WebElement addBtn;
	
	@CustomFindBy(how = CustomHow.INPUT_VALUE, using = "Enregistrer")
	private WebElement enregistrerBtn;
	
	/**
	 * Crée un profil avec des fonctions données
	 * @param profil le nom du profil
	 * @param fonctions les fonctions
	 */
	public void creerProfil(String profil, List<String> fonctions) {
		getFlog().startAction("Création du profil " + profil);
		setProfil(profil);
		for (String fonction : fonctions){
			addFunction(fonction);
		}
		enregistrer();
		getFlog().endAction();
	}

	private void addFunction(String fonction) {
		setSelect(fonction);
		addBtn.click();
	}

	private void setProfil(String profil) {
		fillField("Profil", profilInputElt, profil);
	}
	
	private void setSelect(String value){
		Select select = new Select(functionAttribueSelect);
		select.selectByVisibleText(value);
	}
	
	public void enregistrer(){
		clickBtn(enregistrerBtn);
	}

}
