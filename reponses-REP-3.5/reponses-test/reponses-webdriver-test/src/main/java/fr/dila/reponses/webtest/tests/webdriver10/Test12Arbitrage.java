package fr.dila.reponses.webtest.tests.webdriver10;

import junit.framework.Assert;
import fr.dila.reponses.webtest.constant.ConstantesLotQuestion;
import fr.dila.reponses.webtest.constant.ConstantesOrga;
import fr.dila.reponses.webtest.helper.AbstractWebTest;
import fr.dila.reponses.webtest.model.UserManager;
import fr.dila.reponses.webtest.pages.CorbeillePage;
import fr.dila.reponses.webtest.pages.dossier.DossierPage;
import fr.dila.reponses.webtest.pages.dossier.FeuilleRouteOnglet;
import fr.dila.reponses.webtest.pages.recherche.RechercheResultPage;
import fr.dila.st.annotations.TestDocumentation;
import fr.dila.st.webdriver.model.STUser;
import fr.sword.naiad.commons.webtest.annotation.WebTest;

/**
 * Test d'arbitrage SGG
 * @author user
 *
 */
public class Test12Arbitrage extends AbstractWebTest{

	private static final String TACHE_POUR_ARBITRAGE = "Pour arbitrage";

	@WebTest(description = "1.2.8 Arbitrage - Demande de réattribution pour arbitrage")
	@TestDocumentation(categories={"FDR"})
	public void demandeReattributionAndArbitrage() {
		getFlog().startAction("Demande réattribution");
		demandeReattribution();
		getFlog().endAction();
		getFlog().startAction("Demande arbitrage");
		demandeArbitrage();
		getFlog().endAction();
	}
	
	private void demandeReattribution(){
		String nomDossier = ConstantesLotQuestion.DOSSIER_TEST_ARBITRAGE;
		UserManager manager = new UserManager();
		STUser bdc = manager.getUserFrom(ConstantesOrga.POSTE_BDC_ECO);
		CorbeillePage corbeillePage = login(bdc, CorbeillePage.class);
		RechercheResultPage rechercheResultPage = corbeillePage.getBandeauMenu().rechercheRapide(nomDossier);
		DossierPage dossierPage = rechercheResultPage.getPage(DossierPage.class);
		FeuilleRouteOnglet fdrOnglet = dossierPage.goToOngletFDR();
		fdrOnglet.reattribuer(ConstantesOrga.MIN_AGRI);
		fdrOnglet.nonConcerne();
		logout();
	}
	
	private void demandeArbitrage(){
		String nomDossier = ConstantesLotQuestion.DOSSIER_TEST_ARBITRAGE;
		UserManager manager = new UserManager();
		STUser bdc = manager.getUserFrom(ConstantesOrga.POSTE_BDC_AGRI);
		CorbeillePage corbeillePage = login(bdc, CorbeillePage.class);
		RechercheResultPage rechercheResultPage = corbeillePage.getBandeauMenu().rechercheRapide(nomDossier);
		DossierPage dossierPage = rechercheResultPage.getPage(DossierPage.class);
		FeuilleRouteOnglet fdrOnglet = dossierPage.goToOngletFDR();
		fdrOnglet.arbitrage();
		fdrOnglet.nonConcerne();
		logout();
		
		// Login en tant que superviseur SGG et vérification du dossier
		STUser admin = manager.getUserFrom(ConstantesOrga.POSTE_ADMIN_SOLON);
		corbeillePage = login(admin, CorbeillePage.class);
		corbeillePage.openCorbeille(ConstantesOrga.MIN_ECO);
		RechercheResultPage results = corbeillePage.openTacheCorbeille(TACHE_POUR_ARBITRAGE);
		if ( !results.containsQuestion(nomDossier)){
			String message = String.format("Le superviseur SGG n'a pas reçu le dossier %s pour arbitrage", nomDossier);
			Assert.fail(message);
		}
	}
}
