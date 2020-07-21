package fr.dila.reponses.webtest.pages.suivi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.openqa.selenium.support.ui.Select;

import fr.dila.reponses.webtest.pages.ReponsesPage;
import fr.dila.reponses.webtest.pages.recherche.RechercheResultPage;

/**
 * La page de l'espace de suivi
 * 
 * @author jgomez
 * 
 */
public class SuiviPage extends ReponsesPage {

	private static final String	CREER_UNE_ALERTE_LINK		= "Créer une alerte";

	private static final int	TIMEOUT						= 1000;

	private static final String	ID_SELECT_FIELD				= "requeteur:smartSearchForm:nxl_nxql_incremental_smart_query:nxw_nxql_incremental_smart_query_widget:nxl_incremental_smart_query_selection:nxl_incremental_smart_query_selection_rowSelector";

	private static final String	ID_INPUT_FIELD_NUMERO		= "requeteur:smartSearchForm:nxl_nxql_incremental_smart_query:nxw_nxql_incremental_smart_query_widget:nxl_incremental_smart_query_selection:nxl_incremental_smart_query_selection_selectionSubview_question_condition_numero:nxw_question_condition_numero:nxw_numero_1";

	private static final String	BLOC_REQUETES_GENERALES		= "Requêtes générales";

	private static final String	BLOC_REQUETES_PERSONNELLES	= "Requêtes personnelles";

	private static final String	BLOC_ALERTES				= "Alertes";

	public static final String	CHAMP_NUMERO_QUESTION		= "question_condition_numero";

	private static final String	QUESTION_VALUE				= "Numéro de question";

	/**
	 * Retourne un locator sur le panel requête générales de l'espace suivu
	 * 
	 * @return
	 */
	public By getRequetesGeneralesBy () {
		return getLocatorBlocSuivi(BLOC_REQUETES_GENERALES, 2);
	}

	/**
	 * Retourne un locator sur le panel requêtes personnelles de l'espace suivu
	 * 
	 * @return
	 */
	public By getRequetesPersonnellesBy () {
		return getLocatorBlocSuivi(BLOC_REQUETES_PERSONNELLES, 1);
	}

	/**
	 * Retourne un locator sur le panel Alertes de l'espace suivu
	 * 
	 * @return
	 */
	public By getAlertesBy () {
		String xpathExpression = String.format("//div[@class='userMailboxes' and ./div/h4[contains(., '%s')]]/..",
				BLOC_ALERTES);
		return By.xpath(xpathExpression);
	}

	private By getLocatorBlocSuivi (String blocName, int index) {
		String xpathExpression = String.format("(//div[@id='uMContent' and ./h6[contains(., '%s')]]/div)[" + index
				+ "]", blocName);
		return By.xpath(xpathExpression);
	}

	public void ajouterLigne (String nomChamp, Integer dossierTestSuiviNumero) {
		By selectFieldBy = By.id(ID_SELECT_FIELD);
		WebElement selectElt = findElement(selectFieldBy);
		Select selectFieldSelect = new Select(selectElt);
		selectFieldSelect.selectByValue(nomChamp);

		By inputFieldBy = By.id(ID_INPUT_FIELD_NUMERO);
		waitForPageSourcePart(inputFieldBy, TIMEOUT);
		WebElement inputFieldElt = findElement(inputFieldBy);
		inputFieldElt.sendKeys(String.valueOf(dossierTestSuiviNumero));

		By ajouterBtnBy = By.xpath("//input[@value=\"Ajouter\"]");
		WebElement ajouterBtn = findElement(ajouterBtnBy);
		ajouterBtn.click();

		By valueBy = By.xpath("//td[text() = \"" + QUESTION_VALUE + "\"]");
		waitForPageSourcePart(valueBy, TIMEOUT);

	}

	public RechercheResultPage lancer () {
		By LancerBtnBy = By.xpath("//input[@value=\"Lancer\"]");
		WebElement lancerBtn = findElement(LancerBtnBy);
		return clickToPage(lancerBtn, RechercheResultPage.class);
	}

	public RechercheResultPage lancerRequetePerso (String nomSousRequete) {
		return clickToPage(getRequetePersoElt(nomSousRequete), RechercheResultPage.class);
	}

	public boolean contientRequetePerso (String nomSousRequete) {
		By requeteBy = By.partialLinkText(nomSousRequete);
		By requeteRestrictedBy = new ByChained(getRequetesPersonnellesBy(), requeteBy);
		List<WebElement> elts = getDriver().findElements(requeteRestrictedBy);
		return !elts.isEmpty();
	}

	public void creerAlerteSur (String requeteQuestion, String nomAlerte, List<String> destinataires) {
		if (!contientRequetePerso(requeteQuestion)) {
			throw new NoSuchElementException("Pas de requête de nom " + requeteQuestion);
		}
		WebElement requeteElt = getRequetePersoElt(requeteQuestion);
		By imgActionBy = By.xpath("../../..//img");
		WebElement actionsElt = requeteElt.findElement(imgActionBy);
		actionsElt.click();
		CreerAlertePage creerAlertePage = creerAlerte();
		creerAlertePage.setTitre(nomAlerte);
		for (String destinataire : destinataires) {
			creerAlertePage.setSelectionDestinataire(destinataire);
		}
		Calendar datePemiereExecution = Calendar.getInstance();
		datePemiereExecution.add(Calendar.DAY_OF_YEAR, -1);
		creerAlertePage.setDatePremiereExecution(datePemiereExecution.getTime());
		Calendar dateFin = Calendar.getInstance();
		dateFin.add(Calendar.DAY_OF_YEAR, 1);
		creerAlertePage.setDateFin(dateFin.getTime());
		creerAlertePage.setFrequence(1);
		creerAlertePage.sauvegarder();
	}

	private CreerAlertePage creerAlerte () {
		By linkCreerAlerteBy = By.partialLinkText(CREER_UNE_ALERTE_LINK);
		WebElement creerAlertBtn = findElement(linkCreerAlerteBy);
		return clickToPage(creerAlertBtn, CreerAlertePage.class);
	}

	private WebElement getRequetePersoElt (String requeteQuestion) {
		By requeteBy = By.partialLinkText(requeteQuestion);
		By requeteRestrictedBy = new ByChained(getRequetesPersonnellesBy(), requeteBy);
		WebElement elt = getDriver().findElement(requeteRestrictedBy);
		return elt;
	}

	public Boolean contientAlerte (String nomAlerte) {
		By alerteBy = By.partialLinkText(nomAlerte);
		By alerteRestrictedBy = new ByChained(getAlertesBy(), alerteBy);
		List<WebElement> elts = getDriver().findElements(alerteRestrictedBy);
		return !elts.isEmpty();
	}

	public List<String> getListeNomsRequetesGenerales () {
		By requetesBy = By.xpath(".//a");
		By requetesRestrictedBy = new ByChained(getRequetesGeneralesBy(), requetesBy);
		List<WebElement> requetesElt = getDriver().findElements(requetesRestrictedBy);
		List<String> requetesStr = new ArrayList<String>();
		for (WebElement requete : requetesElt) {
			requetesStr.add(requete.getText());
		}
		return requetesStr;
	}

	public RechercheResultPage lancerRequeteGenerale (String nomRequeteGenerale) {
		By requeteBy = By.partialLinkText(nomRequeteGenerale);
		By requeteRestrictedBy = new ByChained(getRequetesGeneralesBy(), requeteBy);
		WebElement elt = getDriver().findElement(requeteRestrictedBy);
		return clickToPage(elt, RechercheResultPage.class);
	}

	public void sauvegarderRequeteSous (String nomSousRequete) {
		CreerNouvelleRequetePage newRequetePage = sauvegarderRequeteSous();
		newRequetePage.setTitre(nomSousRequete);
		newRequetePage.sauvegarderRequete();
	}

	private CreerNouvelleRequetePage sauvegarderRequeteSous () {
		By sauvegarderSousBy = By.xpath(".//input[@value=\"Sauvegarder cette requête sous ...\"]");
		WebElement sauvegarderSousElt = findElement(sauvegarderSousBy);
		return clickToPage(sauvegarderSousElt, CreerNouvelleRequetePage.class);
	}

}
