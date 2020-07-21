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

public class Test14MiseEnAttente extends AbstractWebTest {
	
	@WebTest(description = "1.2.10 Demande de mise en attente cas après première étape")
	@TestDocumentation(categories={"FDR", "Dossier"})
	public void miseEnAttenteCas1() throws UnexpectedException{
		getFlog().startAction("Demande de mise en attente question "+ConstantesLotQuestion.DOSSIER_MISE_ATTENTE_CAS1);
		demandeMiseEnAttenteCas1();
		getFlog().endAction();
	}
	
	/**
	 * On prend une question qui ne bouge pas et on lui ajoute immédiatement une étape de mise en attente, 
	 * on valide la première étape et on vérifie qu'on est bien à l'étape pour attente
	 * @throws UnexpectedException
	 */
	private void demandeMiseEnAttenteCas1() throws UnexpectedException {
		String nomDossier = ConstantesLotQuestion.DOSSIER_MISE_ATTENTE_CAS1;
		UserManager manager = new UserManager();
		STUser bdc = manager.getUserFrom(ConstantesOrga.POSTE_BDC_ECO);
		CorbeillePage corbeillePage = login(bdc, CorbeillePage.class);
		RechercheResultPage rechercheResultPage = corbeillePage.getBandeauMenu().rechercheRapide(nomDossier);
		DossierPage dossierPage = rechercheResultPage.getPage(DossierPage.class);
		FeuilleRouteOnglet fdrOnglet = dossierPage.goToOngletFDR();
		
		fdrOnglet.miseEnAttente();
		fdrOnglet.validerEtape();
		
		rechercheResultPage = corbeillePage.getBandeauMenu().rechercheRapide(nomDossier);
		dossierPage = rechercheResultPage.getPage(DossierPage.class);
		fdrOnglet = dossierPage.goToOngletFDR();
		
		EtapeSerie etape = new EtapeSerie(ConstantesEtapeType.POUR_ATTENTE, ConstantesOrga.POSTE_BDC_ECO);
		Boolean etapeActive = fdrOnglet.verifierEtapeActive(etape);
		Assert.assertTrue(String.format("L'étape %s chez l'utilisateur %s pour le poste %s n'est pas active",etape.getEtapeType(), bdc.getLogin(), etape.getDestinataire()), etapeActive);
		logout();
		
	}
	
	
	

}
