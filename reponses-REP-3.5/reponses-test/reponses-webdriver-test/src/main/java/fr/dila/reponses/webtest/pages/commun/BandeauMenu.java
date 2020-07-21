package fr.dila.reponses.webtest.pages.commun;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import fr.dila.reponses.webtest.pages.ReponsesPage;
import fr.dila.reponses.webtest.pages.classement.PlanClassementPage;
import fr.dila.reponses.webtest.pages.dossier.DossierPage;
import fr.dila.reponses.webtest.pages.espaces.EspaceAdministration;
import fr.dila.reponses.webtest.pages.recherche.RecherchePage;
import fr.dila.reponses.webtest.pages.recherche.RechercheResultPage;
import fr.dila.reponses.webtest.pages.suivi.SuiviPage;

public class BandeauMenu extends ReponsesPage {

	@FindBy(how = How.PARTIAL_LINK_TEXT, using = "Espace de travail")
	private WebElement	espaceTravail;

	@FindBy(how = How.PARTIAL_LINK_TEXT, using = "Plan de classement")
	private WebElement	planClassement;

	@FindBy(how = How.PARTIAL_LINK_TEXT, using = "Recherche")
	private WebElement	recherche;

	@FindBy(how = How.PARTIAL_LINK_TEXT, using = "Suivi")
	private WebElement	suivi;

	@FindBy(how = How.PARTIAL_LINK_TEXT, using = "Administration")
	private WebElement	administration;

	@FindBy(how = How.PARTIAL_LINK_TEXT, using = "Statistiques")
	private WebElement	statistiques;

	@FindBy(id = "userMetaServicesSearchForm:questionIdInput")
	private WebElement	rechercheRapideInputElt;

	@FindBy(id = "userMetaServicesSearchForm:rechercheNumeroSubmitButton")
	private WebElement	rechercheRapideAtteindreBtn;

	/**
	 * Va vers l'espace de travail
	 * 
	 * @return
	 */
	public ReponsesPage goToEspaceTravail() {
		return goToReponsePage(espaceTravail);
	}

	/**
	 * Va vers le plan de classement
	 * 
	 * @return
	 */
	public PlanClassementPage goToPlanClassement() {
		return clickToPage(planClassement, PlanClassementPage.class);
	}

	/**
	 * Va vers la recherche
	 * 
	 * @return
	 */
	public RecherchePage goToRecherche() {
		return clickToPage(recherche, RecherchePage.class);
	}

	/**
	 * Va vers le suivi
	 * 
	 * @return
	 */
	public SuiviPage goToSuivi() {
		return clickToPage(suivi, SuiviPage.class);
	}

	private ReponsesPage goToReponsePage(WebElement element) {
		return clickToPage(element, ReponsesPage.class);
	}

	/**
	 * Va vers l'administration
	 * 
	 * @return
	 */
	public EspaceAdministration goToAdministration() {
		return clickToPage(administration, EspaceAdministration.class);
	}

	/**
	 * Donne info si lien vers administration est présent
	 */
	public boolean ContientLienAdministration() {
		By by = By.partialLinkText("Administration");
		return containsEltLocatedBy(by);
	}

	/**
	 * Va vers l'administration
	 * 
	 * @return
	 */
	public ReponsesPage goToStatistiques() {
		return goToReponsePage(statistiques);
	}

	public RechercheResultPage rechercheRapide(String query) {
		return rechercheRapide(query, RechercheResultPage.class);
	}

	public <T extends ReponsesPage> T rechercheRapide(String query, Class<T> clazz) {
		fillField("Recherche rapide", rechercheRapideInputElt, query);
		return clickToPage(rechercheRapideAtteindreBtn, clazz);
	}

}
