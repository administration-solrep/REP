package fr.dila.reponses.webtest.pages.dossier;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;

import fr.dila.st.webdriver.framework.STBy;

public class DossierConnexeOnglet extends DossierPage {

	private static final int	TIMEOUT					= 10;
	private static final String	FIRST_COLNAME			= "Ministère";
	private static final String	SPAN_TXT_REPONSES_BY	= "Retour à la question avec copie du texte de réponse";

	public By getTableByWithFirstColName(String firstColName) {
		String xpathExpression = "//thead//";
		String colxpath = String.format("th[contains(text(), \"%s\")]", firstColName);
		String fullcolxpath = xpathExpression + colxpath + "/../../..";
		return By.xpath(fullcolxpath);
	}

	/**
	 * Retourne le nombre de dossiers connexes
	 * 
	 * @return
	 */
	public Integer getNbDossiersConnexes() {
		getFlog().startAction("Retourne le nombre total de dossiers connexes");
		By tableBy = getTableByWithFirstColName(FIRST_COLNAME);
		waitForPageSourcePartDisplayed(tableBy, TIMEOUT);
		WebElement tableElt = findElement(tableBy);
		List<WebElement> elts = tableElt.findElements(By.xpath(".//tr/td[2]/a"));
		Integer total = 0;
		for (WebElement elt : elts) {
			total += Integer.valueOf(elt.getText());
		}
		getFlog().endAction();
		return total;
	}

	public void deplieListeQuestionsConnexe(String ministere) {
		By linkMinBy = By.partialLinkText(ministere);
		waitForPageSourcePartDisplayed(linkMinBy, 10);
		findElement(linkMinBy).click();
	}

	/**
	 * Sélectionne une liste de questions en cochant les boîtes à sélection
	 * 
	 * @param lot
	 *            la liste des identifiants des questions à cocher
	 */
	// TODO: Fusionner les méthodes quasi-similaire de la recherche, allotissemnt et dossier connexe
	public void selectQuestions(By rootBy, String[] lot) {
		getFlog().startAction("Sélection des checkbox ");
		for (String questionId : lot) {
			getFlog().action("Préparation de la sélection de " + questionId);
			By questionBy = By.partialLinkText(questionId);
			By restrictQuestionBy = new ByChained(rootBy, questionBy);
			List<WebElement> elts = getDriver().findElements(restrictQuestionBy);
			if (!elts.isEmpty()) {
				getFlog().action("Sélection de la question " + questionId);
				By relativeBoxBy = By.xpath("../../preceding-sibling::td/input");
				By boxBy = new ByChained(restrictQuestionBy, relativeBoxBy);
				WebElement boxElt = findElement(boxBy);
				boxElt.click();
			}
		}
		getFlog().endAction();
	}

	/**
	 * Sélectionner les questions d'un lot
	 * 
	 * @param lot
	 */
	public void selectQuestions(String[] lot) {
		By formBy = getFormDossierConnexeBy();
		selectQuestions(formBy, lot);
	}

	private By getFormDossierConnexeBy() {
		By formBy = By.xpath("//form[@id = \"dossier_connexe_content\"]");
		return formBy;
	}

	/**
	 * Créer un lot
	 */
	public void creerUnLot() {
		getFlog().startAction("Créer un lot");
		By creerUnLotBtnBy = By.xpath("//input[@value = \"" + "Créer un lot" + "\"]");
		waitForPageSourcePartDisplayed(creerUnLotBtnBy, 5);
		findElement(creerUnLotBtnBy).click();
		getFlog().endAction();
	}

	public void cliqueSurQuestion(String dossierCiblePourCopie) {
		getFlog().startAction("Clique sur la question" + dossierCiblePourCopie);
		By formBy = getFormDossierConnexeBy();
		WebElement formElt = findElement(formBy);
		WebElement linkQuestionElt = formElt.findElement(By.partialLinkText(dossierCiblePourCopie));
		linkQuestionElt.click();
		getFlog().endAction();
	}

	public void copieReponseSurQuestion() {
		getFlog().startAction("Copie de la réponse");
		By by = STBy.partialSpanText(SPAN_TXT_REPONSES_BY);
		findElement(by).click();
		getFlog().endAction();
	}

}
