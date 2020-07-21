package fr.dila.reponses.webtest.tests.webdriver10;

import java.io.InputStream;
import java.rmi.UnexpectedException;

import org.junit.Ignore;

import junit.framework.Assert;
import fr.dila.reponses.rest.api.WSReponse;
import fr.dila.reponses.webtest.constant.ConstantesEtapeType;
import fr.dila.reponses.webtest.constant.ConstantesLotQuestion;
import fr.dila.reponses.webtest.constant.ConstantesOrga;
import fr.dila.reponses.webtest.helper.AbstractWebTest;
import fr.dila.reponses.webtest.model.EtapeSerie;
import fr.dila.reponses.webtest.pages.CorbeillePage;
import fr.dila.reponses.webtest.pages.dossier.DossierPage;
import fr.dila.reponses.webtest.pages.dossier.FeuilleRouteOnglet;
import fr.dila.reponses.webtest.pages.recherche.RechercheResultPage;
import fr.dila.reponses.webtest.utils.WsUtils;
import fr.dila.st.annotations.TestDocumentation;
import fr.sword.naiad.commons.webtest.annotation.WebTest;
import fr.sword.xsd.reponses.ChercherErrataReponsesRequest;
import fr.sword.xsd.reponses.ChercherErrataReponsesResponse;
import fr.sword.xsd.reponses.EnvoyerReponseErrataRequest;
import fr.sword.xsd.reponses.EnvoyerReponseErrataResponse;
/**
 * Test de l'erratum d'une réponse
 * @author user
 *
 */
public class Test09ErratumReponse extends AbstractWebTest{
	
	private static final int LAST_INDEX_ETAPE = 9;

	@WebTest(description = "1.2.3 Erratum de la réponse")
	@TestDocumentation(categories={"FDR", "Assemblées", "WS"})
	public void erratumReponse() throws Exception {
		getFlog().startAction("Création d'une étape rédaction interfacé à la fin de la feuille de route par adminsgg, rédémarrage.");
		createEtapeRedactionInterface();
		getFlog().endAction();
		getFlog().startAction("Envoi d'un erratum de la réponse");
		erratumReponses();
		getFlog().endAction();
		getFlog().startAction("Création d'une étape transmission aux assemblée, redémarrage, validation");
		createEtapeTransmissionAssemblee();
		getFlog().endAction();
		getFlog().startAction("Récupération par les assemblées");
		recuperationErratumParAssemblee();
		getFlog().endAction();
	}
	
	// Action 1
	private void createEtapeRedactionInterface() throws InterruptedException, UnexpectedException{
		EtapeSerie nvlleEtapeRedactionInterface = new EtapeSerie(ConstantesEtapeType.POUR_REDACTION_INTERFACEE, ConstantesOrga.POSTE_WS_ECO);
		createEtapeVersFinFdrEtValidation(ConstantesLotQuestion.DOSSIER_TEST_1,LAST_INDEX_ETAPE, nvlleEtapeRedactionInterface, false);
	}
	
	// Action 2
	private void erratumReponses() throws Exception{
		WSReponse service = WsUtils.getWSReponsesMinEco();
		InputStream stream = getClass().getResourceAsStream("/webservice/envoyer_reponse_errata.xml");
		EnvoyerReponseErrataRequest request = WsUtils.buildRequestFromFile(stream, EnvoyerReponseErrataRequest.class);
		Assert.assertNotNull("Le fichier de la question n'est pas à l'emplacement indiqué", stream);
		EnvoyerReponseErrataResponse response = service.envoyerReponseErrata(request);
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getResultatTraitement());
		Assert.assertEquals(1, response.getResultatTraitement().size());
		Assert.assertEquals(WsUtils.STATUS_OK, response.getResultatTraitement().get(0).getStatut().toString());
	}
	
	// Action 3
	private void createEtapeTransmissionAssemblee() throws InterruptedException, UnexpectedException{
		EtapeSerie nvlleEtapeTransmissionAssemblee = new EtapeSerie(ConstantesEtapeType.POUR_TRANSMISSION_ASSEMBLEE, ConstantesOrga.POSTE_ADMIN_SOLON);
		createEtapeVersFinFdrEtValidation(ConstantesLotQuestion.DOSSIER_TEST_1,LAST_INDEX_ETAPE + 1,nvlleEtapeTransmissionAssemblee, true);
	}

	// Action 4
	private void recuperationErratumParAssemblee() throws Exception{
		WSReponse service = WsUtils.getWSReponses();
		ChercherErrataReponsesRequest request = new ChercherErrataReponsesRequest();
		request.setJeton("0");
		ChercherErrataReponsesResponse response = service.chercherErrataReponses(request);
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getErrataReponses());
		Assert.assertEquals(1, response.getErrataReponses().size());
		Assert.assertEquals("Le forfait internet est à 30.01 euros par mois", response.getErrataReponses().get(0).getTexteErratum());
	}

	/**
	 * Crée une étape à la fin d'une feuille de route et redémarre
	 * @param nomDossier le nom du dossier
	 * @param etape l'étape à ajouter
	 * @throws InterruptedException 
	 * @throws UnexpectedException 
	 */
	private void createEtapeVersFinFdrEtValidation(String nomDossier,Integer lastIndex, EtapeSerie etape, boolean validerEtape) throws InterruptedException, UnexpectedException {
		CorbeillePage corbeillePage = loginAsAdmin();
		RechercheResultPage resultPage = corbeillePage.getBandeauMenu().rechercheRapide(nomDossier);
		if (!resultPage.containsQuestion(nomDossier)){
			Assert.fail("Pas de dossier " + nomDossier);
		}
		DossierPage dossierPage = resultPage.getPage(DossierPage.class);
		FeuilleRouteOnglet fdrOnglet = dossierPage.goToOngletFDR();
		fdrOnglet.lock();
		fdrOnglet.ajouterApres(lastIndex, etape);
		fdrOnglet.unlock();
		fdrOnglet.redemarrerFdr();
		if (validerEtape){
			RechercheResultPage resultPage2 = corbeillePage.getBandeauMenu().rechercheRapide(nomDossier);
			DossierPage dossierPage2 = resultPage2.getPage(DossierPage.class);
			FeuilleRouteOnglet fdrOnglet2 = dossierPage2.goToOngletFDR();
			fdrOnglet2.validerEtape();
		}
		logout();
	}
	
}
