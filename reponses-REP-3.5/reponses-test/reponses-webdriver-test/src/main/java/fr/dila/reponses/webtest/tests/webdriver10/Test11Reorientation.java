package fr.dila.reponses.webtest.tests.webdriver10;

import java.rmi.UnexpectedException;

import junit.framework.Assert;
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

/**
 * Test de réorientation
 * @author jgomez
 *
 */
public class Test11Reorientation extends AbstractWebTest{

	private static final String POUR_REORIENTATION = "Pour réorientation";

	@WebTest(description="1.2.7 Réorientation - Demande de réorientation")
	@TestDocumentation(categories={"FDR", "Recherche"})
	public void demandeReorientation() throws UnexpectedException{
		// Réattribution sur le ministère de l'agriculture
		String nomDossier = ConstantesLotQuestion.DOSSIER_TEST_REORIENTATION;
		UserManager manager = new UserManager();
		STUser bdc = manager.getUserFrom(ConstantesOrga.POSTE_BDC_ECO);
		CorbeillePage corbeillePage = login(bdc, CorbeillePage.class);
		RechercheResultPage rechercheResultPage = corbeillePage.getBandeauMenu().rechercheRapide(nomDossier);
		DossierPage dossierPage = rechercheResultPage.getPage(DossierPage.class);
		FeuilleRouteOnglet fdrOnglet = dossierPage.goToOngletFDR();
		EtapeSerie etape = new EtapeSerie(ConstantesEtapeType.POUR_ATTRIBUTION, ConstantesOrga.POSTE_BDC_ECO);
		Boolean etapeActive = fdrOnglet.verifierEtapeActive(etape);
		Assert.assertTrue(String.format("L'étape %s chez l'utilisateur %s pour le poste %s n'est pas active", etape.getEtapeType(), bdc.getLogin(), etape.getDestinataire()), etapeActive);
		fdrOnglet.validerEtape();
		logout();
		
		// Réorientation 
		STUser agentDGEFP = manager.getUserFrom(ConstantesOrga.POSTE_DGEFP);
		CorbeillePage corbeilleAgentDGEFP = login(agentDGEFP, CorbeillePage.class);
		RechercheResultPage rechercheResultAgentDGEFP = corbeilleAgentDGEFP.getBandeauMenu().rechercheRapide(nomDossier);
		DossierPage dossierPageAgentDGEFP = rechercheResultAgentDGEFP.getPage(DossierPage.class);
		FeuilleRouteOnglet fdrOngletAgentDGEFP = dossierPageAgentDGEFP.goToOngletFDR();
		fdrOngletAgentDGEFP.reorienter();
		fdrOngletAgentDGEFP.nonConcerne();
		logout();

		// Connexion sur éco et vérification la présence du dossier sur la corbeille Pour réorientation
		CorbeillePage corbeillePageEcoRetour = login(bdc, CorbeillePage.class);
		RechercheResultPage rechercheResultEcoPage = corbeillePageEcoRetour.openTacheCorbeille(POUR_REORIENTATION);
		if ( !rechercheResultEcoPage.containsQuestion(nomDossier)){
			String message = String.format("L'agent bdc n'a pas reçu le dossier %s pour reorientation", nomDossier);
			Assert.fail(message);
		}
		
	}
	
	
	
}
