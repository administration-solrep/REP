package fr.dila.reponses.webtest.pages.dossier;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;

import fr.sword.naiad.commons.webtest.WebPage;

public class AllotissementOnglet extends DossierPage {

	private static final String	ADD_LOT_BTN_ID		= "document_properties:button_add_lot";
	private static final String	QUESTION_INPUT_ID	= "document_properties:questionTitreInput";

	public List<String> getDossierIds() {
		getFlog().startAction("Récupération des dossiers allotis");
		By tableBy = getTableAllotissementBy();
		waitForPageSourcePartDisplayed(tableBy, 10);

		By idDossierTd = By.xpath(".//table[@id=\"document_properties:table_dc\"]//tbody/tr/td[2]");
		List<WebElement> elts = getDriver().findElements(idDossierTd);
		List<String> dossierIds = new ArrayList<String>();
		for (WebElement elt : elts) {
			getFlog().action("Lien trouvés : " + elt.getText());
			dossierIds.add(elt.getText());
		}
		getFlog().endAction();
		return dossierIds;
	}

	private By getTableAllotissementBy() {
		By tableBy = By.xpath(".//table[@id=\"document_properties:table_dc\"]");
		return tableBy;
	}

	/**
	 * Ajoute un numéro de question au lot de l'onglet
	 * 
	 * @param dossierAAjouter
	 */
	public void ajouterAuLot(String dossierAAjouter) {
		By numeroQuestionAAjouterBy = By.id(QUESTION_INPUT_ID);
		waitForPageSourcePartDisplayed(numeroQuestionAAjouterBy, 10);
		WebElement elt = findElement(numeroQuestionAAjouterBy);
		fillField("Ajouter au lot", elt, dossierAAjouter);
		ajouterAuLot();
	}

	/**
	 * Appuie sur le bouton Ajouter au Lot
	 */
	private void ajouterAuLot() {
		By ajouterAuLotBy = By.id(ADD_LOT_BTN_ID);
		findElement(ajouterAuLotBy).click();
	}

	/**
	 * Retire une question du lot
	 * 
	 * @param dossierAAjouterLot
	 * @throws InterruptedException
	 */
	public void retirerDuLot(String dossierAAjouterLot) {
		getFlog().startAction("Retrait d'une question d'un lot ");
		By tableAllotissement = getTableAllotissementBy();
		waitForPageSourcePartDisplayed(tableAllotissement, 100);
		String[] tabQuestions = { dossierAAjouterLot };
		selectQuestions(tableAllotissement, tabQuestions);
		retirerDuLot();
		By tableInfo = By.xpath(".//table[@id=\"busyContentTable\"]");
		waitForPageSourcePartHide(tableInfo, TIMEOUT_IN_SECONDS);
	}

	/**
	 * Sélectionne une liste de questions en cochant les boîtes à sélection
	 * 
	 * @param lot
	 *            la liste des identifiants des questions à cocher
	 */
	public void selectQuestions(By rootBy, String[] lot) {
		getFlog().startAction("Sélection des checkbox ");
		for (String questionId : lot) {
			getFlog().action("Préparation de la sélection de " + questionId);
			By questionBy = By.xpath(".//td[contains(text(),\"" + questionId + "\")]");
			By restrictQuestionBy = new ByChained(rootBy, questionBy);
			List<WebElement> elts = getDriver().findElements(restrictQuestionBy);
			if (!elts.isEmpty()) {
				getFlog().action("Sélection de la question " + questionId);
				By relativeBoxBy = By.xpath("./preceding-sibling::td/input");
				By boxBy = new ByChained(restrictQuestionBy, relativeBoxBy);
				waitForPageSourcePartDisplayed(boxBy, 10);
				WebElement boxElt = findElement(boxBy);
				boxElt.click();
			}
		}
		getFlog().endAction();
	}

	/**
	 * Presse le bouton Retirer du lot et attend le changement de page
	 */
	private void retirerDuLot() {
		clickToPage(findElement(By.id("document_properties:button_remove_lot")), WebPage.class);
	}

}
