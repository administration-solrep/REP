package fr.dila.reponses.webtest.tests.webdriver10;

import java.rmi.UnexpectedException;

import org.junit.Assert;

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
import fr.dila.reponses.webtest.utils.AttributionUtils;
import fr.dila.reponses.webtest.utils.WsUtils;
import fr.dila.st.annotations.TestDocumentation;
import fr.dila.st.webdriver.model.STUser;
import fr.sword.naiad.commons.webtest.annotation.WebTest;
import fr.sword.xsd.commons.TraitementStatut;
import fr.sword.xsd.reponses.ChercherAttributionsDateResponse;
import fr.sword.xsd.reponses.ChercherAttributionsResponse;
import fr.sword.xsd.reponses.QuestionId;
import fr.sword.xsd.reponses.QuestionSource;
import fr.sword.xsd.reponses.QuestionType;
/**
 * Tests de réattribution
 * @author user
 *
 */
public class Test10Reattribution extends AbstractWebTest{
	
	@WebTest(description = "1.2.6 Reattribution")
	@TestDocumentation(categories={"FDR"})
	public void testReattribution() throws Exception {
		getFlog().startAction("Demande de réattribution");
		demandeReattribution();
		getFlog().endAction();
		getFlog().startAction("Refus de réattribution");
		refusReattribution();
		getFlog().endAction();
		getFlog().startAction("Validation webservice attribution après refus");
		verifierWebServiceApresRefus();
		getFlog().endAction();
		getFlog().startAction("Nouvelle demande de réattribution");
		nouvelleDemandeReattribution();
		getFlog().endAction();
		getFlog().startAction("Validation réattribution");
		validerReattribution();
		getFlog().endAction();
		getFlog().startAction("Validation webservice attribution après nouvelle demande");
		verifierWebServiceApresAttribution();
		getFlog().endAction();
	}
	
	/** 
	 * Action 1 :
	 * - Connexion avec agent BDC Min. Eco
	 * - Rechercher et accéder au dossier "AN 314161"
	 * - Aller sur onglet FDR
	 * - Réattribution au Min. Agri
	 * - Indiquer "Non concerné"
	 * - Déconnexion
	 */
	private void demandeReattribution(){
		String nomDossier = ConstantesLotQuestion.DOSSIER_TEST_REATTRIBUTION;
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
	
	/**
	 *  Action 2 :
	 *  - Connexion avec Agent BDC Agri
	 *  - Rechercher et accéder au dossier "AN 314161"
	 *  - Aller sur l'onglet FDR
	 *  - Refus de validation et retour BDC
	 *  - Déconnexion
	 */
	private void refusReattribution(){
		String nomDossier = ConstantesLotQuestion.DOSSIER_TEST_REATTRIBUTION;
		UserManager manager = new UserManager();
		STUser bdc = manager.getUserFrom(ConstantesOrga.POSTE_BDC_AGRI);
		CorbeillePage corbeillePage = login(bdc, CorbeillePage.class);
		RechercheResultPage rechercheResultPage = corbeillePage.getBandeauMenu().rechercheRapide(nomDossier);
		DossierPage dossierPage = rechercheResultPage.getPage(DossierPage.class);
		FeuilleRouteOnglet fdrOnglet = dossierPage.goToOngletFDR();
		fdrOnglet.refusValidationEtRetourBDC();
		logout();
	}
	
	/**
	 *  Action 3 :
	 *  - Génération d'un id pour la question "AN 314161"
	 *  - Vérification de l'attribution du dossier au Min. Eco par AN : true attendu
	 *  - Vérification de l'attribution du dossier par SE : false attendu
	 *  - Vérification de l'attribution du dossier au Min. Eco par Gvt : true attendu
	 *  
	 * @throws Exception
	 */
	private void verifierWebServiceApresRefus() throws Exception{
		QuestionId dossierIdTestReattribution = creerQuestionId(ConstantesLotQuestion.DOSSIER_TEST_REATTRIBUTION, 14, QuestionType.QE);
		verifierAttribution(TraitementStatut.OK, ConstantesOrga.MIN_ECO, WsUtils.WS_AN_USERNAME, WsUtils.WS_AN_PASSWORD, dossierIdTestReattribution);
		verifierAttribution(TraitementStatut.KO, null,WsUtils.WS_SENAT_USERNAME, WsUtils.WS_SENAT_PASSWORD, dossierIdTestReattribution);
		verifierAttribution(TraitementStatut.OK, ConstantesOrga.MIN_ECO,WsUtils.WS_MIN_USERNAME,WsUtils.WS_MIN_PASSWORD, dossierIdTestReattribution);
		logout();
	}

	/**
	 *  Action 4 :
	 *  - Connexion avec Agent BDC Min Eco.
	 *  - Rechercher et accéder au dossier "AN 314161"
	 *  - Aller sur l'onglet FDR
	 *  - Réattribution au Min. Agri
	 * - Indiquer "Non concerné"
	 * - Déconnexion
	 */
	private void nouvelleDemandeReattribution(){
		String nomDossier = ConstantesLotQuestion.DOSSIER_TEST_REATTRIBUTION;
		UserManager manager = new UserManager();
		STUser bdc = manager.getUserFrom(ConstantesOrga.POSTE_BDC_ECO);
		CorbeillePage corbeillePage = login(bdc, CorbeillePage.class);
		RechercheResultPage rechercheResultPage = corbeillePage.getBandeauMenu().rechercheRapide(nomDossier);
		DossierPage dossierPage = rechercheResultPage.getPage(DossierPage.class);
		FeuilleRouteOnglet fdrOnglet = dossierPage.goToOngletFDR();
		fdrOnglet.reattribuerAlternativeMethode(ConstantesOrga.MIN_AGRI);
		fdrOnglet.nonConcerne();
		logout();
	}
	
	/**
	 *  Action 5
	 *  - Connexion avec Agent BDC Agri
	 *  - Rechercher et accéder au dossier "AN 314161"
	 *  - Aller sur l'onglet FDR
	 *  - Vérifier que l'étape active est une étape de réattribution ayant pour destinataire le poste BDC Agri
	 *  - Valider l'étape
	 *  - Déconnexion
	 *  
	 * @throws UnexpectedException
	 */
	private void validerReattribution() throws UnexpectedException {
		String nomDossier = ConstantesLotQuestion.DOSSIER_TEST_REATTRIBUTION;
		UserManager manager = new UserManager();
		STUser bdc = manager.getUserFrom(ConstantesOrga.POSTE_BDC_AGRI);
		CorbeillePage corbeillePage = login(bdc, CorbeillePage.class);
		RechercheResultPage rechercheResultPage = corbeillePage.getBandeauMenu().rechercheRapide(nomDossier);
		DossierPage dossierPage = rechercheResultPage.getPage(DossierPage.class);
		FeuilleRouteOnglet fdrOnglet = dossierPage.goToOngletFDR();
		EtapeSerie etape = new EtapeSerie(ConstantesEtapeType.POUR_REATTRIBUTION, ConstantesOrga.POSTE_BDC_AGRI);
		Boolean etapeActive = fdrOnglet.verifierEtapeActive(etape);
		Assert.assertTrue(String.format("L'étape %s chez l'utilisateur %s pour le poste %s n'est pas active",etape.getEtapeType(), bdc.getLogin(), etape.getDestinataire()), etapeActive);
		fdrOnglet.validerEtape();
		logout();
	}
	
	/**
	 * Action 6 :
	 *  - Génération d'un id pour la question "AN 314161"
	 *  - Vérification de l'attribution du dossier au Min. Agri par AN : true attendu
	 *  - Vérification de l'attribution du dossier par SE : false attendu
	 *  - Vérification de l'attribution du dossier au Min. Agri par Gvt : true attendu
	 *  
	 * @throws Exception
	 */
	private void verifierWebServiceApresAttribution() throws Exception{
		QuestionId dossierIdTestReattribution = creerQuestionId(ConstantesLotQuestion.DOSSIER_TEST_REATTRIBUTION, 14, QuestionType.QE);
		verifierAttribution(TraitementStatut.OK, ConstantesOrga.MIN_AGRI, WsUtils.WS_AN_USERNAME, WsUtils.WS_AN_PASSWORD, dossierIdTestReattribution);
		verifierAttribution(TraitementStatut.KO, null, WsUtils.WS_SENAT_USERNAME, WsUtils.WS_SENAT_PASSWORD, dossierIdTestReattribution);
		verifierAttribution(TraitementStatut.OK,ConstantesOrga.MIN_AGRI, WsUtils.WS_MIN_USERNAME,WsUtils.WS_MIN_PASSWORD, dossierIdTestReattribution);
		logout();
	}
	
	/**
	 * Vérification de l'attribution ou de la non-attribution d'un dossier par le webservice chercherAttribution et chercherAttributionDate
	 * @param dossierDevraitEtreAttribue Vrai si le dossier doit être attribué
	 * @param loginWebservice le login du webservice sur lequel on veut se connecter
	 * @param idDossier l'identifiant du dossier
	 * @throws Exception 
	 */
	private void verifierAttribution(TraitementStatut expectedStatut, String expectedMinistere, String loginWebservice, String password, QuestionId questionId) throws Exception {
		verifierAttributionNormal(expectedStatut, expectedMinistere, loginWebservice, password, questionId);
		verifierAttributionDate(expectedStatut, expectedMinistere, loginWebservice,password, questionId);
	}
	
	/**
	 * Vérification de l'attribution ou de la non-attribution d'un dossier par le webservice chercherAttribution
	 * @param dossierDevraitEtreAttribue Vrai si le dossier doit être attribué
	 * @param loginWebservice le login du webservice sur lequel on veut se connecter
	 * @param idDossier l'identifiant du dossier
	 * @throws Exception 
	 */
	private void verifierAttributionNormal(TraitementStatut expectedStatut, String expectedMinistere, String loginWebservice, String password, QuestionId questionId) throws Exception {
		String message = String.format("Vérification de l'attribution du dossier %s par le point d'entrée chercherAttribution", questionId.getNumeroQuestion());
		getFlog().startAction(message);
		ChercherAttributionsResponse reponse = AttributionUtils.chercherAttributions(loginWebservice, password, questionId);
		Assert.assertEquals(expectedStatut.toString(), reponse.getStatut().toString());
		if (reponse.getAttributions().isEmpty()){
			return;
		}
		String ministereDossier = reponse.getAttributions().get(0).getAttributaire().getMinistre().getIntituleMinistere().toString();
		Boolean ministereCorrect = expectedMinistere.equals(ministereDossier);
		String messsageErreur = String.format("Le dossier n'est pas attribué au ministère %s mais au ministere %s", expectedMinistere, ministereDossier);
		Assert.assertTrue(messsageErreur, ministereCorrect);
		getFlog().endAction();
	}
	
	/**
	 * Vérification de l'attribution ou de la non-attribution d'un dossier par le webservice chercherAttributionDate
	 * @param dossierDevraitEtreAttribue Vrai si le dossier doit être attribué
	 * @param loginWebservice le login du webservice sur lequel on veut se connecter
	 * @param idDossier l'identifiant du dossier
	 * @throws Exception 
	 */
	private void verifierAttributionDate(TraitementStatut expectedStatut, String expectedMinistere, String loginWebservice, String password, QuestionId questionId) throws Exception {
		String message = String.format("Vérification de l'attribution du dossier %s par le point d'entrée chercherAttributionDate", questionId.getNumeroQuestion());
		getFlog().startAction(message);
		ChercherAttributionsDateResponse reponse = AttributionUtils.chercherAttributionsDate(loginWebservice, password, questionId);
		Assert.assertNotNull("La réponse ne doit pas être nul", reponse);
		Assert.assertNotNull("Le statut de la réponse ne doit pas être nul", reponse.getStatut());
		Assert.assertEquals("Le résultat du traitement est différent de celui attendu", expectedStatut.toString(), reponse.getStatut().toString());
		if ( reponse.getAttributions().isEmpty()){
			return;
		}
		String ministereDossier = reponse.getAttributions().get(0).getAttributaire().getMinistre().getIntituleMinistere().toString();
		Boolean ministereCorrect = expectedMinistere.equals(ministereDossier);
		String messsageErreur = String.format("Le dossier n'est pas attribué au ministère %s mais au ministere %s", expectedMinistere, ministereDossier);
		Assert.assertTrue(messsageErreur, ministereCorrect);
		getFlog().endAction();
	}
	
	/**
	 * Génère un id de question unique.
	 */
	private static QuestionId creerQuestionId(String idDossier, Integer legislature, QuestionType type) {
		QuestionId questionId = new QuestionId();
		questionId.setLegislature(legislature);
		questionId.setType(type);
		String[] tabsplit = idDossier.split(" ");
		QuestionSource source = QuestionSource.valueOf(tabsplit[0]);
		String numeroQuestion = tabsplit[1];
		questionId.setNumeroQuestion(Integer.valueOf(numeroQuestion));
		questionId.setSource(source);
		return questionId;
	}
}
