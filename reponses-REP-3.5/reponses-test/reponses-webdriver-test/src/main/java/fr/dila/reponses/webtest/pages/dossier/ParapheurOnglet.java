package fr.dila.reponses.webtest.pages.dossier;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;

public class ParapheurOnglet extends DossierPage {

	private static final int	TIMEOUT	= 10;

	/**
	 * Ajoute un bout de texte au début d'une réponse déjà existant
	 * 
	 * @param la
	 *            part de la réponse
	 */
	public void ajouterResponseAuDebut(String reponsePart) {
		getFlog().startAction("Ajoute quelques mots à la réponse");
		By containerBy = By.xpath("//td[@class=\"mceIframeContainer mceFirst mceLast\"]");
		By iframe = By.tagName("iframe");
		By iframeReponseBy = new ByChained(containerBy, iframe);
		WebDriver driver = getDriver();
		WebElement elt = driver.findElement(iframeReponseBy);
		driver.switchTo().frame(elt);
		driver.findElement(By.id("tinymce")).sendKeys(reponsePart);
		driver.switchTo().defaultContent();
		getFlog().endAction();
	}

	public Boolean verifierReponseContient(String contentPart) {
		getFlog().startAction(
				"Verification du contenu de la réponse, retourne vrai si la sentence " + contentPart
						+ " appartient à la réponse");
		WebElement reponseElt = getElementReponse();
		String reponseContent = reponseElt.getText();
		System.out.println(reponseContent);
		Boolean contientPart = reponseContent.contains(contentPart);
		getFlog().endAction();
		return contientPart;
	}

	private WebElement getElementReponse() {
		By titleReponseBy = By
				.xpath("//h3[@class = 'summaryTitle' and contains(text(), 'Réponse')]/following-sibling::div[2]");
		waitForPageSourcePartDisplayed(titleReponseBy, TIMEOUT);
		WebElement titleReponseElt = findElement(titleReponseBy);
		By tdBy = By.xpath(".//td[@class = 'fieldColumn']");
		WebElement reponsesElt = titleReponseElt.findElement(tdBy);
		return reponsesElt;
	}

}
