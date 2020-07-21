package fr.dila.reponses.webtest.pages.espaces;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import fr.dila.reponses.webtest.pages.ReponsesPage;
import fr.dila.reponses.webtest.pages.administration.GestionModeleFDRPage;
import fr.dila.reponses.webtest.pages.administration.GestionOrganigrammePage;
import fr.dila.reponses.webtest.pages.administration.GestionProfilsPage;
import fr.dila.reponses.webtest.pages.administration.GestionRestrictionAccesPage;
import fr.dila.reponses.webtest.pages.administration.GestionUtilisateurPage;
import fr.dila.reponses.webtest.pages.administration.delegation.DelegationDroitsPage;
import fr.dila.reponses.webtest.pages.administration.utilisateur.RechercheUtilisateurPage;
import fr.sword.naiad.commons.webtest.WebPage;
import junit.framework.Assert;

/**
 * Le volet gauche accessible dans l'espace administration
 * @author user
 *
 */
public class EspaceAdministrationMenuVoletGauche extends ReponsesPage{

	private static final String GESTION_DES_UTILISATEURS = "Gestion des utilisateurs";

	private static final String GESTION_DE_L_ORGANIGRAMME = "Gestion de l'organigramme";

	private static final String LINK_GESTION_DES_MODELES = "Gestion des modèles de feuilles de route";

	private static final String LINK_GESTION_DES_PROFILS = "Gestion des profils";
	
	private static final String LINK_RECHERCHE_UTILISATEUR = "Recherche d'utilisateurs";
	
	private static final String LINK_DELEGATION_DROITS= "Délégation des droits";
	
	private static final String GESTION_RESTRICTION_ACCES= "Gestion de l'accès";

	@FindBy(how = How.PARTIAL_LINK_TEXT, using = GESTION_DES_UTILISATEURS)
	private WebElement gestionUtilisateurs;
	
	@FindBy(how = How.PARTIAL_LINK_TEXT, using = GESTION_DE_L_ORGANIGRAMME)
	private WebElement gestionOrganigramme;	
	
	@FindBy(how = How.PARTIAL_LINK_TEXT, using = LINK_GESTION_DES_MODELES)
	private WebElement gestionModeleFDR;	
	
	@FindBy(how = How.PARTIAL_LINK_TEXT, using = LINK_GESTION_DES_PROFILS)
	private WebElement gestionProfils;	
	
	@FindBy(how = How.PARTIAL_LINK_TEXT, using = LINK_RECHERCHE_UTILISATEUR)
	private WebElement rechercheUtilisateur;	
	
	@FindBy(how = How.PARTIAL_LINK_TEXT, using = LINK_DELEGATION_DROITS)
	private WebElement delegationDroits;
	
	@FindBy(how = How.PARTIAL_LINK_TEXT, using = GESTION_RESTRICTION_ACCES)
	private WebElement gestionRestrictionAcces;
	
	/** Clique sur un item du menu d'administration, et attend que l'affichage soit complet.
	 * On suppose qu'à l'appel de cette méthode, la partie droite de l'écran "Administration" est vide
	 */
	private <T extends WebPage> T clickAndWaitDisplay(WebElement element, Class<T> clazz) {
		final String selector = "td.themeCellFrame > div.themeRegion:empty";
		// On a 2 de ces zones lorsque la partie droite de l'administration est vide
		Assert.assertTrue(getDriver().findElements(By.cssSelector(selector)).size() == 2);
		T t = click(element, clazz);
		 new WebDriverWait(getDriver(), TIMEOUT_IN_SECONDS).until(new ExpectedCondition<Boolean>() {
	            @Override
	            public Boolean apply(final WebDriver wdriver) {
	            	// Lorsque la partie droite est remplie, on a plus qu'une de ces zones
                    final List<WebElement> elt = wdriver.findElements(By.cssSelector(selector));
                    return elt.size() == 1;
	            }
	        });
		 return t;
	}
	
	/**
	 * Aller à la page gestion des utilisateurs
	 * @return
	 */
	public GestionUtilisateurPage goToGestionDesUtilisateurs() {
		return clickAndWaitDisplay(gestionUtilisateurs, GestionUtilisateurPage.class);
	}

	/**
	 * Aller à la page gestion de l'organigramme
	 */
	public GestionOrganigrammePage goToGestionOrganigramme() {
		return clickAndWaitDisplay(gestionOrganigramme, GestionOrganigrammePage.class);
	}

	public GestionModeleFDRPage goToGestionModelesFdr() {
		return clickAndWaitDisplay(gestionModeleFDR, GestionModeleFDRPage.class);
	}

	public RechercheUtilisateurPage goToRechercheUtilisateur() {
		return clickAndWaitDisplay(rechercheUtilisateur, RechercheUtilisateurPage.class);
	}
	
	public DelegationDroitsPage goToDelegationDesDroits() {
		return clickAndWaitDisplay(delegationDroits, DelegationDroitsPage.class);
	}

	/**
	 * Aller à la page de gestion des profils
	 * @return
	 */
	public GestionProfilsPage goToGestionProfils() {
		return clickAndWaitDisplay(gestionProfils, GestionProfilsPage.class);
	}

	public boolean contientAccesAuProfil() {
		By by = By.partialLinkText(LINK_GESTION_DES_PROFILS);
		return containsEltLocatedBy(by);
	}
	
	public GestionRestrictionAccesPage goToRestrictionAcces() {
		return clickAndWaitDisplay(gestionRestrictionAcces, GestionRestrictionAccesPage.class);
	}

	
}
