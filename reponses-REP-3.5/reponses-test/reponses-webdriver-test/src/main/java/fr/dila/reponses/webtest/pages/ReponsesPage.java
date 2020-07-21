package fr.dila.reponses.webtest.pages;

import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import fr.dila.reponses.webtest.pages.commun.BandeauMenu;
import fr.dila.st.webdriver.framework.CustomPageFactory;
import fr.dila.st.webdriver.model.CommonWebPage;
import fr.sword.naiad.commons.webtest.WebPage;
import fr.sword.naiad.commons.webtest.logger.WebLogger;

public class ReponsesPage extends CommonWebPage {

	public static Integer TWO = 2;
	//Redéfinition des champs statics à surcharger
	static {
		TIMEOUT_IN_SECONDS = 300;
	}
	
	public ReponsesPage(){
		super();
	}
	
	public BandeauMenu getBandeauMenu(){
		return getPage(BandeauMenu.class);
	}
	
	public <T extends WebPage> T click(WebElement element, Class<T> class1) {
		if (element == null){
			throw new NoSuchElementException("Pas d'élément trouvé");
		}
		getFlog().startAction("Clique sur un lien " + element.getText());
		element.click();
		T page =  getPage(class1);
		getFlog().endAction();
		return page;
	}
	
	public static <T extends WebPage> T getPage(final WebDriver driver, final WebLogger webLogger, final Class<T> pageClazz) {
		final T page = CustomPageFactory.initElements(driver, pageClazz);
		page.setWebLogger(webLogger);
		page.setDriver(driver);
		page.checkIfLoaded();
		return page;
	}
	
    /** Idem que findElements, mais filtre sur les éléments visibles. Car Selenium refuse de cliquer sur les éléments invisibles. **/
    public List<WebElement> findDisplayedElements(WebDriver driver, By by) {
    	List<WebElement> list = driver.findElements(by);
    	for (Iterator<WebElement> it = list.iterator();it.hasNext();) {
    		WebElement element = it.next();
    		if (!element.isDisplayed() ) {
    			it.remove();
    		}
    	}
    	return list;
    }
}
