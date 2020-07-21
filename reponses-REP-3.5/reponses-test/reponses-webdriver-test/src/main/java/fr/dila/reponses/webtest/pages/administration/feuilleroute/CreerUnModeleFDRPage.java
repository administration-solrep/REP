package fr.dila.reponses.webtest.pages.administration.feuilleroute;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import fr.dila.reponses.webtest.model.Etape;
import fr.dila.reponses.webtest.model.EtapeSerie;
import fr.dila.reponses.webtest.model.FeuilleRoute;
import fr.dila.st.webdriver.framework.STBy;
import fr.dila.st.webdriver.helper.NameShortener;

public class CreerUnModeleFDRPage extends FDRPage {

	private WebElement intituleElt;
	
	@FindBy(how = How.XPATH, using = "//input[@value='Créer']")
	private WebElement creerBtnElt;
	
	@FindBy(how = How.XPATH, using = "//input[@value='Valider le modèle']")
	private WebElement validerBtnElt;
	
	@FindBy(how = How.XPATH, using = "//input[@value='Retour à la liste']")
	private WebElement retourBtnElt;
	
	public void setIntitule(String intitule) {
		By intituleBy = STBy.labelOnNuxeoLayoutForInput("Intitulé");
		intituleElt = findElement(intituleBy);
		fillField("Intitulé", intituleElt, intitule);
	}
	
	public void setDirectionPilote(NameShortener dirShortener) {
		By directionBy = STBy.labelOnNuxeoLayoutForInput("Direction pilote");
		setAutocompleteValue(dirShortener, directionBy);
	}
	
	public void setMinistere(NameShortener minShortener) {
		By ministereBy = STBy.labelOnNuxeoLayoutForInput("Ministère");
		setAutocompleteValue(minShortener, ministereBy);
	}

	public CreerUnModeleFDRPage creer() {
		return clickToPage(creerBtnElt, CreerUnModeleFDRPage.class);
	}

	public ChoixEtapePopup ajouterPremiereEtape() {
		By ajouterPremiereEtapeBy = By.partialLinkText("Ajouter la première étape");
		WebElement ajouterPremiereEtapeBtn = findElement(ajouterPremiereEtapeBy);
		return click(ajouterPremiereEtapeBtn, ChoixEtapePopup.class);
	}
	
	/**
	 * Ajouter une première étape
	 * @param etape
	 */
	public void ajouterPremiereEtape(EtapeSerie etape) {
		ChoixEtapePopup popup = ajouterPremiereEtape();
		CreerUneNouvelleEtapePage creerUneNouvelleEtape = popup.ajouterUneEtape();
		ajouterSerie(etape, creerUneNouvelleEtape);
	}

	public void valider() {
		validerBtnElt.click();
	}
	
	public void retour() {
		retourBtnElt.click();
	}

	public void creerModele(FeuilleRoute fdr) {
		setIntitule(fdr.getIntitule());
		NameShortener minShortener = new NameShortener(fdr.getMinistere());
		this.setMinistere(minShortener);
		if (fdr.getIsModeleParDefaut()) {
			this.toogleIsModeleParDefaut();
		}
		// Il y a beaucoup de direction, on tape presque le nom complet
		NameShortener dirShortener = new NameShortener(fdr.getDirectionPilote(), 2);
		setDirectionPilote(dirShortener);
		creer();
		List<Etape> etapes = fdr.getEtapes();
		ajouterPremiereEtape((EtapeSerie) etapes.get(0));
		for (Integer indexEtape = 1; indexEtape < etapes.size(); indexEtape++) {
			Etape nouvelleEtape = etapes.get(indexEtape);
			ajouterApres(indexEtape - 1, nouvelleEtape);
		}
		valider();
		retourBtnElt.click();
	}

	private void toogleIsModeleParDefaut() {
		By modelFdParDefautBy = By.name("document_create:nxl_feuille_route_detail:nxw_feuille_route_defaut");
		WebElement modelFdParDefautElt = findElement(modelFdParDefautBy);
		modelFdParDefautElt.click();
	}
	
		
}
