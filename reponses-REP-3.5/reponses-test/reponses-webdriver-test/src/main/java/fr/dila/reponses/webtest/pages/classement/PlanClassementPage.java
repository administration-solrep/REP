package fr.dila.reponses.webtest.pages.classement;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;

import fr.dila.reponses.webtest.model.PlanClassementItem;
import fr.dila.reponses.webtest.pages.ReponsesPage;
import fr.dila.reponses.webtest.pages.recherche.RechercheResultPage;
import fr.dila.st.webdriver.framework.CustomFindBy;
import fr.dila.st.webdriver.framework.CustomHow;
import fr.dila.st.webdriver.framework.STBy;

/**
 * Une classe pour gérer le plan de classement
 * 
 */

public class PlanClassementPage extends ReponsesPage {

	private static final String		AJOUTER_AUX_FAVORIS	= "Ajouter aux favoris";

	private static final String		SENAT				= "Sénat";

	private static final String		ASSEMBLEE_NATIONALE	= "Assemblée nationale";

	private static final Integer	TIMEOUT				= 10;

	@CustomFindBy(how = CustomHow.LABEL_TEXT, using = ASSEMBLEE_NATIONALE)
	private WebElement				modeANElt;

	/**
	 * Retourne un locator sur le bloc du plan de classement
	 * 
	 * @return
	 */
	private By getPlanClassementBy () {
		String xpathExpression = String.format("//h4[text() = '%s']/..", "Plan de classement");
		return By.xpath(xpathExpression);
	}

	/**
	 * Retourne un locator sur le bloc des favoris du plan de classement
	 * 
	 * @return
	 */
	private By getFavorisPanelBy () {
		String xpathExpression = String.format("//div[@class='userMailboxesContent' and ./h4[contains(., '%s')]]/..",
				"Mes favoris du plan de classement");
		return By.xpath(xpathExpression);
	}

	/**
	 * Bascule en mode AN
	 * 
	 * @throws InterruptedException
	 */
	public void toggleAn () throws InterruptedException {
		WebElement labelAnElt = getByLabelInPlanClassementNode(ASSEMBLEE_NATIONALE);
		labelAnElt.click();
		Thread.sleep(500);
	}

	/**
	 * Bascule en mode Sénat
	 * 
	 * @throws InterruptedException
	 */
	public void toggleSenat () throws InterruptedException {
		WebElement labelSenatElt = getByLabelInPlanClassementNode(SENAT);
		labelSenatElt.click();
		Thread.sleep(300);
	}

	private WebElement getByLabelInPlanClassementNode (String libelle) {
		By labelAnBy = STBy.labelText(libelle);
		By labelAnFullPageBy = new ByChained(getPlanClassementBy(), labelAnBy);
		WebElement labelAnElt = findElement(labelAnFullPageBy);
		return labelAnElt;
	}

	/**
	 * Retourne la liste des items du plan de classement de plus haut niveau
	 * 
	 * @return
	 */
	public List<PlanClassementItem> getTopLevelItems () {
		By spanBy = By.tagName("span");
		By topLevelItemsBy = new ByChained(getPlanClassementBy(), spanBy);
		List<WebElement> topLevelItemsElts = getDriver().findElements(topLevelItemsBy);
		List<PlanClassementItem> items = new ArrayList<PlanClassementItem>();
		for (WebElement elt : topLevelItemsElts) {
			String content = new String(elt.getText());
			if (!StringUtils.isEmpty(content)) {
				items.add(new PlanClassementItem(content));
			}
		}
		return items;
	}

	/**
	 * Ouvre une rubrique du plan de classement
	 * 
	 * @param rubrique
	 *            le nom d'une rubrique
	 */
	public void open (String rubrique) {
		getFlog().startAction("Ouvrir la rubrique du plan de classement " + rubrique);
		By planClassementItemBy = STBy.partialSpanText(rubrique);
		By planClassementItemRestreintBy = new ByChained(getPlanClassementBy(), planClassementItemBy);
		WebElement elt = findElement(planClassementItemRestreintBy);
		elt.click();
		// Un timer crade, je ne vois pas trop comment gérer ce genre de chose simplement
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		getFlog().endAction();
	}

	/**
	 * Retourne vrai si le plan de classement contient la rubrique ou la sous rubrique
	 * 
	 * @param item
	 *            le nom d'une rubrique ou d'une sous-rubrique
	 * @return
	 */
	public boolean containsPlanClassementItem (String item) {
		By planClassementItemBy = STBy.partialLinkText(item);
		By planClassementItemRestreintBy = new ByChained(getPlanClassementBy(), planClassementItemBy);
		List<WebElement> elts = getDriver().findElements(planClassementItemRestreintBy);
		return !elts.isEmpty();
	}

	/**
	 * Ajoute la sous-rubrique aux favoris
	 * 
	 * @param ssRubrique
	 *            la sous-rubrique
	 */
	public void ajouteAuxFavoris (final String ssRubrique) {
		getFlog().startAction("Ajoute le favoris " + ssRubrique);
		By planClassementItemBy = STBy.partialLinkText(ssRubrique);
		By planClassementItemRestreintBy = new ByChained(getPlanClassementBy(), planClassementItemBy);
		waitForPageSourcePartDisplayed(planClassementItemBy, TIMEOUT);
		WebElement elt = findElement(planClassementItemRestreintBy);
		rightClick(elt);
		By actionBy = STBy.partialSpanText(AJOUTER_AUX_FAVORIS);
		waitForPageSourcePartDisplayed(actionBy, TIMEOUT);
		WebElement actionElt = findElement(actionBy);
		actionElt.click();
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		getFlog().endAction();
	}

	/**
	 * Retourne vrai si la sous-rubrique appartient aux favoris du plan de classement
	 */
	public Boolean appartientAuxFavoris (String ssRubrique) {
		List<WebElement> elts = getFavorisBy(ssRubrique);
		return !elts.isEmpty();
	}

	private List<WebElement> getFavorisBy (String ssRubrique) {
		By ssRubriqueBy = STBy.partialSpanText(ssRubrique);
		By ssRubriqueRestreintBy = new ByChained(getFavorisPanelBy(), ssRubriqueBy);
		List<WebElement> elts = getDriver().findElements(ssRubriqueRestreintBy);
		return elts;
	}

	/**
	 * Exécute la requête du favori
	 * 
	 * @param nomFavoris
	 */
	public RechercheResultPage executerRequeteDuFavori (String nomFavoris) {
		By ssRubriqueBy = STBy.partialSpanText(nomFavoris);
		By ssRubriqueRestreintBy = new ByChained(getFavorisPanelBy(), ssRubriqueBy);
		WebElement elt = findElement(ssRubriqueRestreintBy);
		return clickToPage(elt, RechercheResultPage.class);
	}

}
