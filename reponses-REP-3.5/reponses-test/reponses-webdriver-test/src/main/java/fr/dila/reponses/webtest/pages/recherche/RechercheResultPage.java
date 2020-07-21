package fr.dila.reponses.webtest.pages.recherche;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;

import fr.dila.reponses.webtest.pages.ReponsesPage;

public class RechercheResultPage extends ReponsesPage {

	private static final String	LOT_INPUT_ID		= "document_properties:questionTitreInput";

	private static final String	CREER_LOT_BTN_ID	= "document_properties:button_create_lot";

	private static final String	CREER_UN_LOT		= "Créer un lot";

	/**
	 * Retourne vrai si le tableau de résultats contient la question
	 * 
	 * @param origineQuestion
	 * @param numeroQuestion
	 * @return
	 */
	public Boolean containsQuestion(String origineQuestion, String numeroQuestion) {
		String numeroQuestionComplet = origineQuestion + " " + numeroQuestion;
		return containsQuestion(numeroQuestionComplet);
	}

	/**
	 * Retourne le nombre de résultats retourné par la recherche
	 * 
	 * @return
	 */
	public Integer getResultCount() {
		// Le total des recherche est à l'intérieur du div selection_buttons
		String xpathExpression = String.format(".//span[contains(text(),\"%s\")]", "dossier");
		By rechercheResultBy = new ByChained(By.id("selection_buttons"), By.xpath(xpathExpression));
		List<WebElement> elements = getDriver().findElements(rechercheResultBy);
		if (elements.isEmpty()) {
			return 0;
		}
		WebElement rechercheResultSpan = elements.get(0);
		String nombreResultSpan = rechercheResultSpan.getText();
		String intValue = nombreResultSpan.replaceAll("[a-zA-Z]", "").trim();
		return Integer.valueOf(intValue);
	}

	/**
	 * Créer un lot de dossier directeur donnée
	 * 
	 * @param dossierDirecteurLot1
	 */
	public void creerUnLot(String dossierDirecteur) {
		creerUnLot();

		By entrerLotInputBy = By.id(LOT_INPUT_ID);
		WebElement inputLotElt = findElement(entrerLotInputBy);
		fillField("Entrer le numéro du dossier directeur", inputLotElt, dossierDirecteur);

		By creerLotBtnBy = By.id(CREER_LOT_BTN_ID);
		WebElement validerLotBtn = findElement(creerLotBtnBy);
		validerLotBtn.click();
	}

	/**
	 * Presse le bouton créer un lot quand il apparait
	 */
	private void creerUnLot() {
		By inputCreerUnLotBy = By.xpath("//input[@value = \"" + CREER_UN_LOT + "\"]");
		waitForPageSourcePartDisplayed(inputCreerUnLotBy, TIMEOUT_IN_SECONDS);
		int attempts = 0;
		while (attempts < 2) {
			try {
				findElement(inputCreerUnLotBy).click();
				break;
			} catch (StaleElementReferenceException exc) {
				// element no longer in dom, on retente
				if (attempts == 2) {
					throw exc;
				}
			}
			attempts++;
		}
	}

	/**
	 * Sélectionne une liste de questions en cochant les boîtes à sélection
	 * 
	 * @param lot
	 *            la liste des identifiants des questions à cocher
	 */
	public <T extends RechercheResultPage> T selectQuestions(String[] lot) {
		RechercheResultPage resultPage = this;
		getFlog().startAction("Sélection des checkbox ");
		for (String questionId : lot) {
			getFlog().action("Préparation de la sélection de " + questionId);
			By questionBy = By.partialLinkText(questionId);
			List<WebElement> elts = getDriver().findElements(questionBy);
			if (!elts.isEmpty()) {
				getFlog().action("Sélection de la question " + questionId);
				By relativeBoxBy = By.xpath("../../preceding-sibling::td/input");
				By boxBy = new ByChained(questionBy, relativeBoxBy);
				waitForPageSourcePartDisplayed(boxBy, TIMEOUT_IN_SECONDS);
				WebElement boxElt = findElement(boxBy);
				resultPage = click(boxElt, this.getClass());
			}
		}
		getFlog().endAction();
		return (T) resultPage;
	}

	public void displayQuestionDetail(String idQuestion) {
		getFlog().startAction("Affichage du détail de la question " + idQuestion);

		By questionBy = By.partialLinkText(idQuestion);
		WebElement link = getDriver().findElement(questionBy);
		if (link != null) {
			link.click();
			By affichageDetail = By.className("subbodyContainer");
			waitForPageSourcePart(affichageDetail, TIMEOUT_IN_SECONDS);
		}
		getFlog().endAction();
	}

	/**
	 * Vérifie les actions qui sont disponnibles pour les questions en cours pour un administrateur
	 */
	public boolean areActionsAvailable() {
		getFlog().startCheck("Vérification de la présence des boutons d'actions");

		WebElement actionDossierContainer = getDriver().findElement(By.className("dossierActions"));
		WebElement actionFdrContainer = getDriver().findElement(By.className("fdrActions"));
		boolean availableDossier = false;
		boolean availableFdr = false;

		if (actionDossierContainer.isDisplayed() && actionDossierContainer.isEnabled()) {

			WebElement refreshElem = actionDossierContainer.findElement(By.xpath("//img[@title='Mettre à jour']"));
			WebElement lockElem = actionDossierContainer.findElement(By.xpath("//img[@title='Verrouiller']"));

			if (refreshElem.isDisplayed() && lockElem.isDisplayed()) {
				availableDossier = true;
				getFlog().check("Les actions verrouiller et rafraichir pour le dossier sont bien affichées");
			} else {
				if (!refreshElem.isDisplayed()) {
					getFlog().checkFailed("L'action rafraîchir n'est pas affichée");
				}

				if (!lockElem.isDisplayed()) {
					getFlog().checkFailed("L'action verrouiller n'est pas affichée");
				}
			}

		} else {
			getFlog().checkFailed("Le bandeau des actions de dossier n'est pas disponnible");
		}

		if (actionFdrContainer.isDisplayed() && actionFdrContainer.isEnabled()) {
			WebElement mailElem = actionFdrContainer
					.findElement(By.xpath("//img[@title='Envoyer ce dossier par mél']"));
			WebElement printElem = actionFdrContainer.findElement(By.xpath("//img[@title='Imprimer le dossier']"));
			WebElement pdfElem = actionFdrContainer.findElement(By
					.xpath("//img[@title='Editer la fiche du dossier au format PDF']"));

			if (mailElem.isDisplayed() && printElem.isDisplayed() && pdfElem.isDisplayed()) {
				availableFdr = true;
				getFlog().check("Les actions sur la feuille de route sont bien disponnibles");
			} else {
				if (!mailElem.isDisplayed()) {
					getFlog().checkFailed("L'action envoyer par mail n'est pas affichée");
				}

				if (!printElem.isDisplayed()) {
					getFlog().checkFailed("L'action imprimer n'est pas affichée");
				}

				if (!pdfElem.isDisplayed()) {
					getFlog().checkFailed("L'action convertir en pdf n'est pas affichée");
				}
			}

		} else {
			getFlog().checkFailed("Le bandeau des actions de feuille de route n'est pas disponnible");
		}

		getFlog().endCheck();

		return availableDossier && availableFdr;

	}

}
