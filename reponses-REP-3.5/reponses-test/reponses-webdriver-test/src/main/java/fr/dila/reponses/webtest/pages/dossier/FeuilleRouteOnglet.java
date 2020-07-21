package fr.dila.reponses.webtest.pages.dossier;

import java.rmi.UnexpectedException;
import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import fr.dila.reponses.webtest.model.EtapeSerie;
import fr.dila.reponses.webtest.pages.administration.feuilleroute.FDRPage;
import fr.dila.reponses.webtest.pages.widget.organigramme.OrganigrammeWidget;
import fr.dila.st.webdriver.helper.NameShortener;

public class FeuilleRouteOnglet extends FDRPage {

	private static final String	VALIDER											= "Valider";

	private static final String	IMG_TITLE_REATTRIBUTION							= "Réattribution";

	private static final String	IMG_TITLE_DEVERROUILLER							= "Déverrouiller";

	private static final String	IMG_TITLE_VERROUILLER							= "Verrouiller";

	private static final String	IMG_TITLE_REDEMARRER							= "Redémarrer";

	private static final String	IMG_TITLE_NON_CONCERNE							= "Non concerné";

	private static final String	IMG_TITLE_REORIENTATION							= "Réorientation";

	private static final String	IMG_TITLE_ARBITRAGE_SGG							= "Arbitrage SGG";

	private static final String	IMG_TITLE_REFUS_VALIDATION_ENVOI_ETAPE_SUIVANTE	= "Refus de validation et envoi étape suivante";

	private static final String	VALIDER_ETAPE									= "Envoi à l'étape suivante";

	private static final String	SIGNER_ET_VALIDER_ETAPE							= "Signer et envoyer à l'étape suivante";

	private static final String	IMG_TITLE_REFUS_VALIDATION_ENVOI_BDC			= "Refus de validation et retour au BDC du ministère attributaire";

	private static final String	IMG_TITLE_METTRE_EN_ATTENTE						= "Mettre en attente";

	private static final String	IMG_TITLE_POUR_VALIDATION_RTR_PM				= "Pour validation retour Premier ministre";
	
	private static final String TEXT_VALIDATION_ETAPE_FDR						= "Vous venez de valider le dossier";

	/**
	 * Vérifie si l'étape active correspondt à l'étape passée en paramètre
	 * 
	 * @param etape
	 * @return
	 * @throws UnexpectedException
	 * @throws InterruptedException
	 */
	public boolean verifierEtapeActive(EtapeSerie etape) throws UnexpectedException {
		// String xpathExpression = "//img[contains(@title, \"%s\")]/..";
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			throw new UnexpectedException("Timer interrompu");
		}
		String xpathExpression = "//img[contains(@src, \"%s\")]/..";
		// By imgEnCoursBy = By.xpath(String.format(xpathExpression, "Étape en cours"));
		By imgEnCoursBy = By.xpath(String
				.format(xpathExpression, "/reponses/img/icons/bullet_ball_glass_yellow_16.png"));
		List<WebElement> elts = getDriver().findElements(imgEnCoursBy);
		if (elts.isEmpty()) {
			getFlog().actionFailed("Pas d'étape en cours trouvé");
			return false;
		}
		for (WebElement img : elts) {
			String formatString = "../td[contains(text(),\"%s\")]";
			List<WebElement> destinatairesTrouves = img.findElements(By.xpath(String.format(formatString,
					etape.getDestinataire())));
			List<WebElement> etapeTypeTrouves = img.findElements(By.xpath(String.format(formatString,
					etape.getEtapeType())));
			if (!destinatairesTrouves.isEmpty() && !etapeTypeTrouves.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Valide l'étape
	 */
	public void validerEtape() {
		getFlog().startAction("Valide l'étape");
		String xexpression = String.format("//img[@title = \"%s\"]", VALIDER_ETAPE);
		By imgBy = By.xpath(xexpression);
		waitForPageSourcePartDisplayed(imgBy, TWO);
		WebElement imgElt = findElement(imgBy);		
		imgElt.click();
		
		WebElement element = new WebDriverWait(getDriver(), 5).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//li[@class='infoFeedback']")));
		
		

		getFlog().endAction();
	}

	/**
	 * Signe et valide l'étape
	 * 
	 * @throws UnexpectedException
	 */
	public void signerEtValiderEtape() throws UnexpectedException {
		getFlog().startAction("Signe et valide l'étape");
		String xexpression = String.format("//img[@title = \"%s\"]", SIGNER_ET_VALIDER_ETAPE);
		By imgBy = By.xpath(xexpression);
		WebElement imgElt = findElement(imgBy);
		imgElt.click();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			throw new UnexpectedException("Timer interrompu");
		}
		Alert alert = getDriver().switchTo().alert();
		alert.accept();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			throw new UnexpectedException("Timer interrompu");
		}
		getFlog().endAction();
	}

	/**
	 * Dévérouiller la feuille de route
	 */
	public void unlock() {
		getFlog().startAction("Déverouille le dossier");
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

	public void redemarrerFdr() throws UnexpectedException {
		getFlog().startAction("Redémarre la feuille de route");
		clickOnImg(IMG_TITLE_REDEMARRER);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			throw new UnexpectedException("Timer interrompu");
		}
		Alert alert = getDriver().switchTo().alert();
		alert.accept();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			throw new UnexpectedException("Timer interrompu");
		}
		getFlog().endAction();
	}

	public void reattribuer(String ministere) {
		getFlog().startAction("Réattribuer");
		clickOnImg(IMG_TITLE_REATTRIBUTION);
		By suggestInputBy = By
				.id("unconcerned_panel_form:nxl_ministere_reattribution:nxw_reattributionMinistere_suggest");
		setAutocompleteValue(new NameShortener(ministere, 0), suggestInputBy, 0, false);
		By validerLinkBy = By.partialLinkText(VALIDER);
		WebElement validerElt = findElement(validerLinkBy);
		validerElt.click();
		getFlog().endAction();
	}

	public void avis(String ministere) {
		getFlog().startAction("avis");
		clickOnImg(IMG_TITLE_VERROUILLER);
		By suggestInputBy = By
				.id("unconcerned_panel_form:nxl_ministere_reattribution:nxw_reattributionMinistere_suggest");
		setAutocompleteValue(new NameShortener(ministere, 0), suggestInputBy, 0, false);
		By validerLinkBy = By.partialLinkText(VALIDER);
		WebElement validerElt = findElement(validerLinkBy);
		validerElt.click();
		getFlog().endAction();
	}

	public void reattribuerAlternativeMethode(String ministere) {
		getFlog().startAction("Réattribuer");
		clickOnImg(IMG_TITLE_REATTRIBUTION);
		OrganigrammeWidget organigrammeWidget = openOrganigramme();
		organigrammeWidget.chooseMinistere(ministere);
		By validerLinkBy = By.partialLinkText(VALIDER);
		WebElement validerElt = findElement(validerLinkBy);
		validerElt.click();
		getFlog().endAction();
	}

	private OrganigrammeWidget openOrganigramme() {
		getFlog().startAction("Ouvre l'organigramme");
		By linkBy = By.id("unconcerned_panel_form:nxl_ministere_reattribution:nxw_reattributionMinistere_findButton");
		WebElement linkOrgaElt = findElement(linkBy);
		getFlog().endAction();
		return click(linkOrgaElt, OrganigrammeWidget.class);
	}

	public void nonConcerne() {
		getFlog().startAction("Non concerné");
		clickOnImg(IMG_TITLE_NON_CONCERNE);
		getFlog().endAction();
	}

	public void reorienter() {
		getFlog().startAction("Réorienter");
		clickOnImg(IMG_TITLE_REORIENTATION);
		getFlog().endAction();
	}

	public void refusValidationEtEnvoiEtapeSuivante() {
		getFlog().startAction("Refus de validation");
		clickOnImg(IMG_TITLE_REFUS_VALIDATION_ENVOI_ETAPE_SUIVANTE);
		getFlog().endAction();
	}

	public void arbitrage() {
		getFlog().startAction("Arbitrage du SGG");
		clickOnImg(IMG_TITLE_ARBITRAGE_SGG);
		getFlog().endAction();
	}

	public void refusValidationEtRetourBDC() {
		getFlog().startAction("Refus de réattribution et retour au BDC attributaire");
		clickOnImg(IMG_TITLE_REFUS_VALIDATION_ENVOI_BDC);
		getFlog().endAction();
	}

	public void pourValidationRetourPM() {
		getFlog().startAction("Renvoi au premier ministre pour validation");
		clickOnImg(IMG_TITLE_POUR_VALIDATION_RTR_PM);
		getFlog().endAction();
	}

	public void miseEnAttente() {
		getFlog().startAction("Mise en attente de l'étape");
		clickOnImg(IMG_TITLE_METTRE_EN_ATTENTE);
		getFlog().endAction();
	}

}
