package fr.dila.reponses.webtest.pages.administration.feuilleroute;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;

import fr.dila.reponses.webtest.model.Etape;
import fr.dila.reponses.webtest.model.EtapeBranche;
import fr.dila.reponses.webtest.model.EtapeSerie;
import fr.dila.reponses.webtest.pages.dossier.DossierPage;
import fr.dila.st.webdriver.helper.NameShortener;

/**
 * Les méthode communes à la gestion de la feuille de route dans l'onglet dossier et la feuille de route modèle
 * 
 * @author user
 * 
 */
public class FDRPage extends DossierPage {

	private static final String	CARACTERISER_ETAPE	= "Caractériser l'étape";

	private static final String	AJOUTER_APRES		= "Ajouter après";

	private ChoixEtapePopup goAjouter(Integer etapeIndexPosition, String intituleAction) {
		getFlog().startAction("Ajouter une étape à l'index " + etapeIndexPosition);
		ByChained etapeTableContentBy = new ByChained(By.className("dataOutput"), By.tagName("tbody"));
		By trEtapesBy = new ByChained(etapeTableContentBy, By.tagName("tr"));
		List<WebElement> trEtapeElts = getDriver().findElements(trEtapesBy);
		if (etapeIndexPosition >= trEtapeElts.size()) {
			getFlog().checkFailed("Le nombre d'étapes de la feuille de route n'est pas correct");
			throw new IndexOutOfBoundsException();
		}
		WebElement trEtapeElt = trEtapeElts.get(etapeIndexPosition);
		List<WebElement> imgActionIconElts = trEtapeElt.findElements(By.className("cellActionIcon"));
		// On prend la dernière image du tr : pour une étape série, il y a une image, pour une étape // il y en a 2, la
		// ligne est donc valable pour les deux cas.
		// Pas valable dans tous les cas
		WebElement imgActionIconElt = null;
		if (imgActionIconElts.isEmpty()) {
			throw new IllegalArgumentException("Pas d'étape sur la feuille de route");
		}
		if (AJOUTER_APRES.equals(intituleAction)) {
			imgActionIconElt = imgActionIconElts.get(0);
		}
		if (CARACTERISER_ETAPE.equals(intituleAction)) {
			imgActionIconElt = imgActionIconElts.get(imgActionIconElts.size() - 1);
		}
		imgActionIconElt.click();
		WebElement linkAjouterApresElt = getDriver().findElement(By.partialLinkText(intituleAction));
		getFlog().endAction();
		return click(linkAjouterApresElt, ChoixEtapePopup.class);
	}

	/**
	 * Ajouter en serie
	 * 
	 * @param etape
	 * @param creerUneNouvelleEtape
	 */
	public void ajouterSerie(EtapeSerie etape, CreerUneNouvelleEtapePage creerUneNouvelleEtape) {
		creerUneNouvelleEtape.setType(etape.getEtapeType());
		creerUneNouvelleEtape.setDestinataire(new NameShortener(etape.getDestinataire(), 0));
		creerUneNouvelleEtape.setEcheanceIndicative(etape.getEcheance());
		if (etape.isValidationAutomatique()) {
			creerUneNouvelleEtape.toggleValidationAutomatique();
		}
		if (etape.isObligatoireMinistere()) {
			creerUneNouvelleEtape.toggleObligatoireMinistere();
		}
		if (etape.isObligatoireSGG()) {
			creerUneNouvelleEtape.toggleObligatoireSGG();
		}
		creerUneNouvelleEtape.creer();
	}

	public void ajouter(int indexEtape, Etape nouvelleEtape, String intituleAction) {
		ChoixEtapePopup popChoix = goAjouter(indexEtape, intituleAction);
		// Etape série
		if (nouvelleEtape instanceof EtapeSerie) {
			CreerUneNouvelleEtapePage creerUneNouvelleEtape = popChoix.ajouterUneEtape();
			ajouterSerie((EtapeSerie) nouvelleEtape, creerUneNouvelleEtape);
		}
		if (nouvelleEtape instanceof EtapeBranche) {
			EtapeBranche branche = (EtapeBranche) nouvelleEtape;
			popChoix.creerDesBranches(branche.getEtapes().size());
			ajouterBranche(indexEtape, branche);
		}
	}

	private void ajouterBranche(int indexEtape, EtapeBranche nouvelleEtape) {
		Integer indexBranche = indexEtape + 1;
		for (Etape etape : nouvelleEtape.getEtapes()) {
			ajouter(indexBranche, etape, CARACTERISER_ETAPE);
			indexBranche++;
		}
	}

	public void ajouterApres(int indexEtape, Etape nouvelleEtape) {
		ajouter(indexEtape, nouvelleEtape, AJOUTER_APRES);
	}

	/**
	 * Va à la popup de choix des étapes en cliquant après sur l'étape à la position etapeIndexPosition
	 * 
	 * @param etapeIndexPosition
	 *            la position de l'étape
	 * @return
	 */
	public ChoixEtapePopup goAjouterBetween(Integer etapeIndexPosition) {
		return goAjouter(etapeIndexPosition, CARACTERISER_ETAPE);
	}
}
