package fr.dila.reponses.webtest.tests.webdriver10;

import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.Assert;

import fr.dila.reponses.webtest.constant.ConstantesOrga;
import fr.dila.reponses.webtest.helper.AbstractWebTest;
import fr.dila.reponses.webtest.pages.CorbeillePage;
import fr.dila.reponses.webtest.pages.dossier.AllotissementOnglet;
import fr.dila.reponses.webtest.pages.dossier.DossierConnexeOnglet;
import fr.dila.reponses.webtest.pages.dossier.DossierPage;
import fr.dila.reponses.webtest.pages.recherche.RecherchePage;
import fr.dila.reponses.webtest.pages.recherche.RechercheResultPage;
import fr.dila.st.annotations.TestDocumentation;
import fr.dila.st.core.util.StringUtil;
import fr.sword.naiad.commons.webtest.annotation.WebTest;

public class Test19Allotissement extends AbstractWebTest {

	private static final String MESSAGE_NON_APPARTENANCE_ALLOTISSEMENT = "La liste des dossiers de l'allotissement ne comprends pas le dossier %s";

	private static final String MESSAGE_DOSSIER_ENCORE_DANS_ALLOTISSEMENT = "La liste des dossiers de l'allotissement comprends encore le dossier %s";
	
	private static final String[] LOT_1 = {"AN 314900", "AN 314901"};
	
	private static final String DOSSIER_DIRECTEUR_LOT_1 = "AN 314901";
	
	private static final String DOSSIER_DIRECTEUR_LOT_1_AFFICHAGE = "AN 314901 *";
	
	private static final String DOSSIER_A_AJOUTER_LOT_1 = "AN 314902";
	
	private static final String DOSSIER_ALLOTISSEMENT_TEST_DOSSIER_CONNEXE = "AN 314903";
	
	private static final String[] LOT_2 = {"AN 314903", "AN 314904"};
	
	private static final String DOSSIER_DIRECTEUR_LOT_2_AFFICHAGE = "AN 314903 *";
	
	private static final String DOSSIER_ALLOTISSEMENT_TEST_DOSSIER_SUR_LOT_DEJA_CREE = "AN 314905";
	
	private static final String[] LOT_3 = {"AN 314905", "AN 314904"};
	private static final String[] LOT_3_NUM = {"314905", "314904"};
	
	private static final String DOSSIER_ALLOTISSEMENT_TEST_DOSSIER_SUR_MINISTERE_DIFFERENT = "AN 314161";
	
	private static final String[] LOT_4 = {"AN 314161", "AN 314906"};

	private static final String NUMERO_DOSSIER_RAPPELE = "7666";
	private static final String DOSSIER_RAPPELE = "SENAT " + NUMERO_DOSSIER_RAPPELE;
	
	private static final String DOSSIER_DIRECTEUR_LOT_RAPPELE = DOSSIER_RAPPELE + " *";

	private static final Object DOSSIER_RAPPEL = "SENAT 9538";

	@WebTest(description = "Vérifier que les questions de rappel sont bien alloties", order = 10)
	@TestDocumentation(categories={"WS", "Assemblées", "Allotissement", "Recherche"})
	public void verificationQuestionRappelAlloties() throws JAXBException, Exception {
		
		// Injection de deux questions : une directrice, et l'autre de rappel
		injectSenat("/injection/rappel/001_injection_question_rappel.xml");
		injectSenat("/injection/rappel/002_injection_question_rappel.xml");

		// Vérification que l'allotissement entre ces 2 questions est bien créé.
		CorbeillePage corbeillePage = loginAsAdmin();
		RecherchePage recherchePage = corbeillePage.getBandeauMenu().goToRecherche();
		recherchePage.setNumeros(NUMERO_DOSSIER_RAPPELE);
		RechercheResultPage resultPage = recherchePage.rechercher();
		DossierPage dossierPage = resultPage.getPage(DossierPage.class);
		AllotissementOnglet allotissement = dossierPage.goToOngletAllotissement();
		List<String> dossierIds = allotissement.getDossierIds();
		Assert.assertEquals(2, dossierIds.size());
		Assert.assertTrue(String.format(MESSAGE_NON_APPARTENANCE_ALLOTISSEMENT, DOSSIER_DIRECTEUR_LOT_RAPPELE) ,dossierIds.contains(DOSSIER_DIRECTEUR_LOT_RAPPELE));
		Assert.assertTrue(String.format(MESSAGE_NON_APPARTENANCE_ALLOTISSEMENT, DOSSIER_RAPPEL) ,dossierIds.contains(DOSSIER_RAPPEL));
		logout();
	}
	
	@WebTest(description = "Injections des questions relatives à l'allotissement", useDriver = false, order = 20)
	@TestDocumentation(categories={"WS", "Assemblées", "Allotissement"})
	public void injectionQuestionsAllotissement() throws JAXBException, Exception {
		injectAN("/allotissement/question_allotissement_AN_001.xml");
		injectAN("/allotissement/question_allotissement_AN_002.xml");
		injectAN("/allotissement/question_allotissement_AN_003.xml");
		injectAN("/allotissement/question_allotissement_AN_004.xml");
		injectAN("/allotissement/question_allotissement_AN_005.xml");
		injectAN("/allotissement/question_allotissement_AN_006.xml");
		injectAN("/allotissement/question_allotissement_AN_007.xml");
	}
	
	@WebTest(description = "Allotir les questions par la recherche", order = 30)
	@TestDocumentation(categories={"Recherche", "Allotissement"})
	public void allotissementParRecherche() {
		CorbeillePage corbeillePage = loginAsAdmin();
		RecherchePage recherchePage = corbeillePage.getBandeauMenu().goToRecherche();
		RechercheResultPage resultPage = recherchePage.rechercher();
		resultPage = resultPage.selectQuestions(LOT_1);
		resultPage.creerUnLot(DOSSIER_DIRECTEUR_LOT_1);
		RechercheResultPage resultPage2 = corbeillePage.getBandeauMenu().rechercheRapide(DOSSIER_DIRECTEUR_LOT_1);
		DossierPage dossierPage = resultPage2.getPage(DossierPage.class);
		AllotissementOnglet allotissement = dossierPage.goToOngletAllotissement();
		List<String> dossierIds = allotissement.getDossierIds();
		Assert.assertTrue(String.format(MESSAGE_NON_APPARTENANCE_ALLOTISSEMENT, LOT_1[0]) ,dossierIds.contains(LOT_1[0]));
		Assert.assertTrue(String.format(MESSAGE_NON_APPARTENANCE_ALLOTISSEMENT, DOSSIER_DIRECTEUR_LOT_1_AFFICHAGE) ,dossierIds.contains(DOSSIER_DIRECTEUR_LOT_1_AFFICHAGE));
		logout();
		
	}
	
	@WebTest(description = "Allotir les questions par l'onglet allotissement", order = 40)
	@TestDocumentation(categories={"Recherche", "Allotissement"})
	public void allotissementParOngletAllotissement() {
		CorbeillePage corbeillePage = loginAsAdmin();
		RechercheResultPage resultPage = corbeillePage.getBandeauMenu().rechercheRapide(DOSSIER_DIRECTEUR_LOT_1);
		DossierPage dossierPage = resultPage.getPage(DossierPage.class);
		AllotissementOnglet allotissement = dossierPage.goToOngletAllotissement();
		allotissement.ajouterAuLot(DOSSIER_A_AJOUTER_LOT_1);
		List<String> dossierIds = allotissement.getDossierIds();
		Assert.assertTrue(String.format(MESSAGE_NON_APPARTENANCE_ALLOTISSEMENT, DOSSIER_A_AJOUTER_LOT_1) ,dossierIds.contains(DOSSIER_A_AJOUTER_LOT_1));
		logout();
	}
	
	@WebTest(description = "Désallotir une question", order = 50)
	@TestDocumentation(categories={"Allotissement", "Recherche"})
	public void allotissementDesallotir() {
		CorbeillePage corbeillePage = loginAsAdmin();
		RechercheResultPage resultPage = corbeillePage.getBandeauMenu().rechercheRapide(DOSSIER_DIRECTEUR_LOT_1);
		DossierPage dossierPage = resultPage.getPage(DossierPage.class);
		AllotissementOnglet allotissement = dossierPage.goToOngletAllotissement();
		allotissement.retirerDuLot(DOSSIER_A_AJOUTER_LOT_1);
		List<String> dossierIds = allotissement.getDossierIds();
		Assert.assertFalse(String.format(MESSAGE_DOSSIER_ENCORE_DANS_ALLOTISSEMENT, DOSSIER_A_AJOUTER_LOT_1), dossierIds.contains(DOSSIER_A_AJOUTER_LOT_1));
		logout();
	}
	
	
	@WebTest(description = "Allotir les questions par dossier connexe", order = 60)
	@TestDocumentation(categories={"Connexité", "Allotissement", "Recherche"})
	public void allotissementParDossierConnexe() {
		CorbeillePage corbeillePage = loginAsAdmin();
		RechercheResultPage resultPage = corbeillePage.getBandeauMenu().rechercheRapide(DOSSIER_ALLOTISSEMENT_TEST_DOSSIER_CONNEXE);
		DossierPage dossierPage = resultPage.getPage(DossierPage.class);
		DossierConnexeOnglet dossierConnexe = dossierPage.goToOngletDossierConnexe();
		dossierConnexe.deplieListeQuestionsConnexe(ConstantesOrga.MIN_ECO);
		dossierConnexe.selectQuestions(LOT_2);
		dossierConnexe.creerUnLot();
		try {
			Thread.sleep(3500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		AllotissementOnglet allotissement = dossierPage.goToOngletAllotissement();
		List<String> dossierIds = allotissement.getDossierIds();
		Assert.assertTrue(String.format(MESSAGE_NON_APPARTENANCE_ALLOTISSEMENT, DOSSIER_DIRECTEUR_LOT_2_AFFICHAGE) ,dossierIds.contains(DOSSIER_DIRECTEUR_LOT_2_AFFICHAGE));
		Assert.assertTrue(String.format(MESSAGE_NON_APPARTENANCE_ALLOTISSEMENT, LOT_2[1]) ,dossierIds.contains(LOT_2[1]));
		logout();
	}
	
	
	@WebTest(description = "Allotir une question déjà allotie sur un autre lot", order = 70)
	@TestDocumentation(categories={"Allotissement", "Recherche"})
	public void allotissementQuestionSurAutreLot() {
		CorbeillePage corbeillePage = loginAsAdmin();
		RecherchePage recherchePage = corbeillePage.getBandeauMenu().goToRecherche();
		recherchePage.setNumeros(StringUtil.join(LOT_3_NUM, ", ", ""));
		RechercheResultPage resultPage = recherchePage.rechercher();
		resultPage = resultPage.selectQuestions(LOT_3);
		resultPage.creerUnLot(DOSSIER_ALLOTISSEMENT_TEST_DOSSIER_SUR_LOT_DEJA_CREE);
		RechercheResultPage resultPage2 = corbeillePage.getBandeauMenu().rechercheRapide(DOSSIER_ALLOTISSEMENT_TEST_DOSSIER_SUR_LOT_DEJA_CREE);
		DossierPage dossierPage = resultPage2.getPage(DossierPage.class);
		AllotissementOnglet allotissement = dossierPage.goToOngletAllotissement();
		List<String> dossierIds = allotissement.getDossierIds();
		Assert.assertTrue("Aucun allotissement ne doit avoir eu lieu sur ce dossier", dossierIds.isEmpty());
		logout();
	}
	
	@WebTest(description = "Désallotir le lot", order = 80)
	@TestDocumentation(categories={"Allotissement", "Recherche"})
	public void allotissementDesallotirLot() {
		CorbeillePage corbeillePage = loginAsAdmin();
		RechercheResultPage resultPage = corbeillePage.getBandeauMenu().rechercheRapide(DOSSIER_DIRECTEUR_LOT_1);
		DossierPage dossierPage = resultPage.getPage(DossierPage.class);
		AllotissementOnglet allotissement = dossierPage.goToOngletAllotissement();
		allotissement.retirerDuLot(LOT_1[0]);
		logout();
	}
	
	
	@WebTest(description = "Allotir une question sur un ministère différent du ministère attributaire", order = 90)
	@TestDocumentation(categories={"Allotissement", "Recherche"})
	public void allotissementQuestionSurMinistereDifferentMinistereAttributaire() {
		CorbeillePage corbeillePage = loginAsAdmin();
		RecherchePage recherchePage = corbeillePage.getBandeauMenu().goToRecherche();
		RechercheResultPage resultPage = recherchePage.rechercher();
		resultPage = resultPage.selectQuestions(LOT_4);
		resultPage.creerUnLot(DOSSIER_ALLOTISSEMENT_TEST_DOSSIER_SUR_MINISTERE_DIFFERENT);
		RechercheResultPage resultPage2 = corbeillePage.getBandeauMenu().rechercheRapide(DOSSIER_ALLOTISSEMENT_TEST_DOSSIER_SUR_MINISTERE_DIFFERENT);
		DossierPage dossierPage = resultPage2.getPage(DossierPage.class);
		AllotissementOnglet allotissement = dossierPage.goToOngletAllotissement();
		List<String> dossierIds = allotissement.getDossierIds();
		Assert.assertTrue("Aucun allotissement ne doit avoir eu lieu sur ce dossier", dossierIds.isEmpty());
	}
}
