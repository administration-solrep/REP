package fr.dila.reponses.webtest.pages.administration.delegation;

import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.Select;

import fr.dila.reponses.webtest.pages.ReponsesPage;
import fr.dila.st.webdriver.helper.NameShortener;
import fr.dila.st.webdriver.utils.DateUtils;

/**
 * La page de création d'une délégation des droits
 * 
 * @author jgomez
 * 
 */
public class CreerDelegationPage extends ReponsesPage {

	private static final String	LIEN_RETOUR_A_LA_LISTE	= "Retour à la liste";

	private static final String	ID_CREATE_BTN			= "document_create:button_create";

	private static final String	FIN_DATE_INPUT			= "document_create:nxl_delegation_detail:nxw_delegation_detail_date_finInputDate";

	private static final String	DEBUT_DATE_INPUT		= "document_create:nxl_delegation_detail:nxw_delegation_detail_date_debutInputDate";

	private static final String	UTILISATEUR_INPUT		= "document_create:nxl_delegation_detail:nxw_delegation_detail_utilisateur_destinataire_suggest";

	private static final String	ID_FUNCTION_SELECT		= "document_create:nxl_delegation_detail:nxw_delegation_detail_profil_list_selectProfile";

	private static final String	ID_ADD_BTN				= "document_create:nxl_delegation_detail:nxw_delegation_detail_profil_list_addVecteurImage";

	@FindBy(id = ID_CREATE_BTN)
	private WebElement			creerBtn;

	@FindBy(id = DEBUT_DATE_INPUT)
	private WebElement			dateDebutInput;

	@FindBy(id = FIN_DATE_INPUT)
	private WebElement			dateFinInput;

	@FindBy(how = How.ID, using = ID_FUNCTION_SELECT)
	private WebElement			functionAttribueSelect;

	@FindBy(how = How.ID, using = ID_ADD_BTN)
	private WebElement			addBtn;

	public void creer(String utilisateur, Date dateDebut, Date dateFin, String profil) {
		getFlog().startAction("Créer une délégation de droits sur l'utilisateur " + utilisateur);
		setUtilisateur(utilisateur);
		setDateDebut(dateDebut);
		setDateFin(dateFin);
		setSelect(profil);
		creer();
		getFlog().endAction();
	}

	private void creer() {
		clickBtn(creerBtn);
	}

	private void setDateDebut(Date dateDebut) {
		String formatDate = DateUtils.formatDate(dateDebut);
		fillField("Date de début", dateDebutInput, formatDate);
	}

	private void setUtilisateur(String utilisateur) {
		By utilisateurBy = By.id(UTILISATEUR_INPUT);
		setAutocompleteValue(new NameShortener(utilisateur, utilisateur.length() - 5), utilisateurBy);
	}

	private void setSelect(String value) {
		Select select = new Select(functionAttribueSelect);
		select.selectByVisibleText(value);
		addBtn.click();
	}

	private void setDateFin(Date dateFin) {
		String formatDate = DateUtils.formatDate(dateFin);
		fillField("Date de fin", dateFinInput, formatDate);
	}

	/**
	 * Retourne vers la liste des délégations
	 */
	public void retourALaListe() {
		By retourLink = By.partialLinkText(LIEN_RETOUR_A_LA_LISTE);
		WebElement elt = findElement(retourLink);
		clickLien(elt);
	}

}
