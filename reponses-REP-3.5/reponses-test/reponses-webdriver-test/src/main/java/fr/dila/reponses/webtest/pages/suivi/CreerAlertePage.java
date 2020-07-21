package fr.dila.reponses.webtest.pages.suivi;

import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import fr.dila.reponses.webtest.pages.ReponsesPage;
import fr.dila.st.webdriver.framework.CustomFindBy;
import fr.dila.st.webdriver.framework.CustomHow;
import fr.dila.st.webdriver.framework.STBy;
import fr.dila.st.webdriver.helper.AutoCompletionHelper;
import fr.dila.st.webdriver.helper.NameShortener;
import fr.dila.st.webdriver.utils.DateUtils;

/**
 * La page de création d'une alerte
 * @author jgomez
 *
 */
public class CreerAlertePage extends ReponsesPage{

	private static final String INPUT_DATE_PREMIERE_EXECUTION_ID = "requeteur:alertForm:nxl_alert:nxw_dateValidityBeginInputDate";

	private static final String INPUT_DATE_FIN_ID = "requeteur:alertForm:nxl_alert:nxw_dateValidityEndInputDate";
	
	private static final String INPUT_FREQUENCE_ID = "requeteur:alertForm:nxl_alert:nxw_periodicity";
	
	@CustomFindBy(how = CustomHow.LABEL_ON_NUXEO_LAYOUT, using = "Titre")
	private WebElement titreElt;
	
	@FindBy(id = INPUT_DATE_PREMIERE_EXECUTION_ID)
	private WebElement datePremiereExecutionElt;
	
	@FindBy(id = INPUT_DATE_FIN_ID)
	private WebElement dateFinElt;
	
	@FindBy(id = INPUT_FREQUENCE_ID)
	private WebElement frequenceElt;
	
	@FindBy( how = How.XPATH, using = ".//input[@value = \"Sauvegarder\"]")
	private WebElement sauvegarderBtn;
	
	public void setTitre(String titre){
		fillField("Titre", titreElt, titre);
	}
	
	public void setSelectionDestinataire(String user){
		By by = STBy.labelOnNuxeoLayoutForInput("Sélection des destinataires");
		AutoCompletionHelper.setUserValue(getFlog(), getDriver(), by, new NameShortener(user, 0));
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public SuiviPage sauvegarder() {
		return clickToPage(sauvegarderBtn, SuiviPage.class);
	}

	public void setDatePremiereExecution(Date datePremiereExecution) {
		String formatDate = DateUtils.formatDatetime(datePremiereExecution);
		fillField("Date première exécution", datePremiereExecutionElt, formatDate);
	}

	public void setDateFin(Date dateFin) {
		String formatDate = DateUtils.formatDatetime(dateFin);
		fillField("Date fin", dateFinElt, formatDate);
	}
	
	public void setFrequence(Integer frequence) {
		fillField("Frequence", frequenceElt, String.valueOf(frequence));
	}
	
}
