package fr.dila.reponses.webtest.pages.dossier;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fr.dila.reponses.webtest.pages.ReponsesPage;
import fr.sword.naiad.commons.webtest.WebPage;

/**
 * La vue sur un dossier
 * 
 * @author jgomez
 * 
 */
public class DossierPage extends ReponsesPage {

	private static final String	IMG_TITLE_DEVERROUILLER	= "Déverrouiller";

	private static final String	IMG_TITLE_VERROUILLER	= "Verrouiller";

	private static final String	IMG_TITLE_SAUVEGARDER	= "Sauvegarder";

	private static By			feuilleRouteOnglet		= By.partialLinkText("Feuille de route");

	private static By			bordereauOnglet			= By.partialLinkText("Bordereau");

	private static By			dossierConnexeOnglet	= By.partialLinkText("Dossiers Connexes");

	private static By			parapheurOnglet			= By.partialLinkText("Parapheur");

	private static By			allotissementOnglet		= By.partialLinkText("Allotissement");

	private <T extends WebPage> T clickAndWaitDisplay(By by, Class<T> clazz) {
		waitForPageSourcePartDisplayed(by, TIMEOUT_IN_SECONDS);
		waitForPageSourcePart(by, TIMEOUT_IN_SECONDS);
		WebElement element = findElement(by);
		return clickToPage(element, clazz);
	}

	public FeuilleRouteOnglet goToOngletFDR() {
		return clickAndWaitDisplay(feuilleRouteOnglet, FeuilleRouteOnglet.class);
	}

	public BordereauOnglet goToOngletBordereau() {
		return clickAndWaitDisplay(bordereauOnglet, BordereauOnglet.class);
	}

	public DossierConnexeOnglet goToOngletDossierConnexe() {
		return clickAndWaitDisplay(dossierConnexeOnglet, DossierConnexeOnglet.class);
	}

	public ParapheurOnglet goToOngletParapheur() {
		return clickAndWaitDisplay(parapheurOnglet, ParapheurOnglet.class);
	}

	public AllotissementOnglet goToOngletAllotissement() {
		return clickAndWaitDisplay(allotissementOnglet, AllotissementOnglet.class);
	}

	/**
	 * Dévérouiller la feuille de route
	 */
	public void unlock() {
		getFlog().startAction("Déverrouille le dossier");
		clickOnImg(IMG_TITLE_DEVERROUILLER);
		getFlog().endAction();
	}

	/**
	 * Vérouiller la feuille de route
	 */
	public void lock() {
		getFlog().startAction("Verrouille le dossier");
		clickOnImg(IMG_TITLE_VERROUILLER);
		getFlog().endAction();
	}

	public void sauvegarder() {
		getFlog().startAction("Sauvegarde le dossier");
		clickOnImg(IMG_TITLE_SAUVEGARDER);
		getFlog().endAction();
	}

	// test presence verrou

	public boolean hasLock() {
		getFlog().startAction("Vérifier présence verrou");
		By imgBy = getImgBy(IMG_TITLE_VERROUILLER);
		List<WebElement> elts = getDriver().findElements(imgBy);
		getFlog().endAction();
		return !elts.isEmpty();

	}

}
