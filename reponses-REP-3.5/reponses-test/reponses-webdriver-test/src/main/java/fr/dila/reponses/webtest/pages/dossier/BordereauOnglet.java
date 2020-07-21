package fr.dila.reponses.webtest.pages.dossier;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;

import fr.dila.st.webdriver.helper.NameShortener;

public class BordereauOnglet extends DossierPage {

	private static final String	DIR_PILOTE_ID				= "document_properties:nxl_bordereau_direction_pilote:nxw_direction_pilote_bordereau_suggest";
	private static final String	RUBRIQUE_AN_INPUT_ID		= "document_properties:nxl_bdIndexANComp:nxw_indexation_an_comp_indexLabel_0";
	private static final String	RUBRIQUE_AN_LINK_ID			= "document_properties:nxl_bdIndexANComp:nxw_indexation_an_comp_addIndex_0";
	private static final String	RUBRIQUE_AN_XPATH			= ".//span[@id='document_properties:nxl_bdIndexAN:nxw_indexation_an_indexPanel']/table/tbody/tr[1]/td[2]";
	private static final String	TETE_ANALYSE_AN_XPATH		= ".//span[@id='document_properties:nxl_bdIndexAN:nxw_indexation_an_indexPanel']/table/tbody/tr[2]/td[2]";
	private static final String	ANALYSES_AN_XPATH_FORMAT	= ".//span[@id='document_properties:nxl_bdIndexAN:nxw_indexation_an_indexPanel']/table/tbody/tr[%s]/td[2]";

	/**
	 * Ajouter une rubrique dans l'indexation complémentaire
	 * 
	 * @param rubriqueAjoutee
	 *            la rubrique à ajouter
	 */
	public void ajouterIndexationComplementaireRubrique(String rubriqueAjoutee) {
		By rubriqueANBy = By.id(RUBRIQUE_AN_INPUT_ID);
		setAutocompleteValue(new NameShortener(rubriqueAjoutee, 0), rubriqueANBy, 0, true);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		By rubriqueANLinkIdBy = By.id(RUBRIQUE_AN_LINK_ID);
		WebElement rubriqueANLinkIdElt = findElement(rubriqueANLinkIdBy);
		int elementPostion = rubriqueANLinkIdElt.getLocation().getY() - 25;
		String js = String.format("window.scroll(0,%s)", elementPostion);
		((JavascriptExecutor) this.getDriver()).executeScript(js);
		rubriqueANLinkIdElt.click();
	}

	/*
	 * Vérifie que le mot clef d'indexation COMPLETAIRE correspond bien à la rubrique de la qe
	 * @param rubriqueAjoutee le mot clef
	 */
	public boolean contientRubriqueComplementaire(String rubriqueAjoutee) {
		getFlog().startAction("Vérification de la rubrique " + rubriqueAjoutee);
		By blocBy = By.id("document_properties:nxl_bdIndexAN_1:nxw_indexation_an_1_indexPanel");
		String xpathExpression = String.format(".//td[contains(text(), \"%s\")]", rubriqueAjoutee);
		By rubriqueAjouteBy = By.xpath(xpathExpression);
		By rubriqueAjouteRestrictedByd = new ByChained(blocBy, rubriqueAjouteBy);
		List<WebElement> elts = getDriver().findElements(rubriqueAjouteRestrictedByd);
		getFlog().endAction();
		return !elts.isEmpty();
	}

	/*
	 * Vérifie que le mot clef d'indexation correspond bien à la rubrique de la qe
	 * @param rubriqueAjoutee le mot clef
	 */
	public boolean contientRubrique(String rubriqueAjoutee) {
		getFlog().startAction("Vérification de la rubrique " + rubriqueAjoutee);
		WebElement elt = findElement(By.xpath(RUBRIQUE_AN_XPATH));
		getFlog().endAction();
		return elt.getText().equals(rubriqueAjoutee);
	}

	/*
	 * Vérifie que le mot clef d'indexation correspond bien à la tête d'analyse de la qe
	 * @param TAAjoutee le mot clef
	 */
	public boolean contientTeteAnalyse(String TAAjoutee) {
		getFlog().startAction("Vérification de la tete d'analyse " + TAAjoutee);
		WebElement elt = findElement(By.xpath(TETE_ANALYSE_AN_XPATH));
		getFlog().endAction();
		return elt.getText().equals(TAAjoutee);
	}

	/*
	 * Vérifie que les mots clefs d'indexation d'analyse correspondent bien à celles de la qe
	 * @param analyse les mots clefs à vérifier
	 */
	public boolean contientAnalyses(String[] analyse) {
		getFlog().startAction("Vérification des mots clefs d'analyse.");
		String xpath;
		for (int i = 0; i < analyse.length; i++) {
			getFlog().startAction("Vérification du mot clef d'analyse " + analyse[i]);
			xpath = String.format(ANALYSES_AN_XPATH_FORMAT, i + 3);
			WebElement elt = findElement(By.xpath(xpath));
			if (!elt.getText().equals(analyse[i])) {
				return false;
			}
		}
		getFlog().endAction();
		return true;
	}

	public void changerDirectionPilote(String directionPilote) {
		By directionPiloteIdBy = By.id(DIR_PILOTE_ID);
		setAutocompleteValue(new NameShortener(directionPilote, 0), directionPiloteIdBy);
	}

}
