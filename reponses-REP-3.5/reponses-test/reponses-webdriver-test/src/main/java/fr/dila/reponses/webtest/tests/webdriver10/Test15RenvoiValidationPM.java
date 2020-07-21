package fr.dila.reponses.webtest.tests.webdriver10;

import java.rmi.UnexpectedException;

import fr.dila.reponses.webtest.constant.ConstantesEtapeType;
import fr.dila.reponses.webtest.constant.ConstantesLotQuestion;
import fr.dila.reponses.webtest.constant.ConstantesOrga;
import fr.dila.reponses.webtest.helper.AbstractWebTest;
import fr.dila.reponses.webtest.model.EtapeSerie;
import fr.dila.reponses.webtest.model.UserManager;
import fr.dila.reponses.webtest.pages.CorbeillePage;
import fr.dila.reponses.webtest.pages.dossier.DossierPage;
import fr.dila.reponses.webtest.pages.dossier.FeuilleRouteOnglet;
import fr.dila.reponses.webtest.pages.recherche.RechercheResultPage;
import fr.dila.st.annotations.TestDocumentation;
import fr.dila.st.webdriver.model.STUser;
import fr.sword.naiad.commons.webtest.annotation.WebTest;

public class Test15RenvoiValidationPM extends AbstractWebTest {
	
	@WebTest(description = "1.2.10 Demande de renvoi au Premier ministre pour validation")
	@TestDocumentation(categories={"FDR", "Recherche", "Signature"})
	public void renvoiPM() throws UnexpectedException {
		getFlog().startAction("Demande de renvoi au premier ministre pour validation "+ ConstantesLotQuestion.DOSSIER_RENVOI_PM);
		demandeRenvoiPM();
		getFlog().endAction();
	}

	/**
	 * On prend une question créée pour l'occasion, on valide toutes ses étapes jusqu'à arriver à pour transmission aux assemblée puis 
	 * on renvoi la question au premier ministre pour validation
	 * @throws UnexpectedException 
	 */
	private void demandeRenvoiPM() throws UnexpectedException {
		String nomDossier = ConstantesLotQuestion.DOSSIER_RENVOI_PM;
		UserManager manager = new UserManager();
		STUser bdc = manager.getUserFrom(ConstantesOrga.POSTE_BDC_ECO);
		CorbeillePage corbeillePage = login(bdc, CorbeillePage.class);
		RechercheResultPage rechercheResultPage = corbeillePage.getBandeauMenu().rechercheRapide(nomDossier);
		DossierPage dossierPage = rechercheResultPage.getPage(DossierPage.class);
		FeuilleRouteOnglet fdrOnglet = dossierPage.goToOngletFDR();

		fdrOnglet.pourValidationRetourPM();
		fdrOnglet.validerEtape();
		
		rechercheResultPage = corbeillePage.getBandeauMenu().rechercheRapide(nomDossier);
		dossierPage = rechercheResultPage.getPage(DossierPage.class);
		fdrOnglet = dossierPage.goToOngletFDR();

		EtapeSerie etapeRetour = new EtapeSerie(ConstantesEtapeType.POUR_RETOUR, ConstantesOrga.POSTE_BDC_ECO);
		fdrOnglet.verifierEtapeActive(etapeRetour);
		fdrOnglet.validerEtape();
		
		rechercheResultPage = corbeillePage.getBandeauMenu().rechercheRapide(nomDossier);
		dossierPage = rechercheResultPage.getPage(DossierPage.class);
		fdrOnglet = dossierPage.goToOngletFDR();

		EtapeSerie etapeSignature = new EtapeSerie(ConstantesEtapeType.POUR_SIGNATURE, ConstantesOrga.POSTE_BDC_ECO);
		fdrOnglet.verifierEtapeActive(etapeSignature);
		fdrOnglet.signerEtValiderEtape();
		
		rechercheResultPage = corbeillePage.getBandeauMenu().rechercheRapide(nomDossier);
		dossierPage = rechercheResultPage.getPage(DossierPage.class);
		fdrOnglet = dossierPage.goToOngletFDR();
		
		EtapeSerie etapeValidation = new EtapeSerie(ConstantesEtapeType.POUR_VALIDATION_RETOUR_PM, ConstantesOrga.POSTE_BDC_ECO);
		fdrOnglet.verifierEtapeActive(etapeValidation);
	}
	

}
