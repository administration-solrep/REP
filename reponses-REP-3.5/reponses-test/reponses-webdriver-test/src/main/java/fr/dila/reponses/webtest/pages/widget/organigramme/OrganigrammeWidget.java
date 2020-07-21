package fr.dila.reponses.webtest.pages.widget.organigramme;

import org.openqa.selenium.By;
import org.openqa.selenium.support.pagefactory.ByChained;

import fr.dila.st.webdriver.framework.STBy;
import fr.sword.naiad.commons.webtest.WebPage;

public class OrganigrammeWidget extends WebPage{

	/**
	 * Choisit un ministère à l'intérieur du widget organigramme
	 * @param ministere
	 */
	public void chooseMinistere(String ministere) {
		By ministereBy = STBy.partialSpanTextLocal(ministere);
		waitForPageSourcePartDisplayed(ministereBy, 10);
		By addBy = By.className("add_icon");
		By blocTdRelativeToMinistereBy = By.xpath("..");
		By blocTdBy = new ByChained(ministereBy, blocTdRelativeToMinistereBy);
		By addByRelative = new ByChained(blocTdBy, addBy);
		getDriver().findElement(addByRelative).click();
	}

}
