package fr.dila.reponses.webtest.tests.webdriver10;

import junit.framework.Assert;
import fr.dila.reponses.webtest.helper.AbstractWebTest;
import fr.dila.reponses.webtest.pages.CorbeillePage;
import fr.dila.reponses.webtest.pages.dossier.BordereauOnglet;
import fr.dila.reponses.webtest.pages.dossier.DossierPage;
import fr.dila.reponses.webtest.pages.recherche.RecherchePage;
import fr.dila.reponses.webtest.pages.recherche.RechercheResultPage;
import fr.dila.st.annotations.TestDocumentation;
import fr.sword.naiad.commons.webtest.annotation.WebTest;

/**
 * Test de la partie fonctionnelle Question
 * 
 * @author user
 * 
 */

public class Test04Question extends AbstractWebTest {

	@WebTest(description = "Injection des questions pour la partie déroulement des tests", useDriver = false)
	@TestDocumentation(categories = { "WS", "Assemblées" })
	public void testInjection() throws Exception {
		injectAN("/questions/question_AN_015.xml");
		injectAN("/questions/question_AN_002.xml");
		injectAN("/questions/question_AN_003.xml");
		injectAN("/questions/question_AN_004.xml");
		injectAN("/questions/question_AN_005.xml");
		injectAN("/questions/question_AN_006.xml");
		injectAN("/questions/question_AN_010.xml");
		injectAN("/questions/question_AN_011.xml");
		injectAN("/questions/question_AN_012.xml");
		injectAN("/questions/question_AN_013.xml");
		injectAN("/questions/question_AN_014.xml");
		injectAN("/injection/an/019_injection_question_an.xml");
		injectAN("/injection/an/020_injection_question_an.xml");
		injectAN("/injection/an/021_injection_question_an.xml");
	}

	@WebTest(description = "vérification des rubriques des question injectées dans test04question", order = 90)
	@TestDocumentation(categories = { "WS", "Assemblées", "Indexation", "Bordereau" })
	public void vérificationIndexationBordereauQEInjectees() {
		CorbeillePage corbeillePage = loginAsAdmin();
		RecherchePage rp = corbeillePage.getBandeauMenu().goToRecherche();
		RechercheResultPage resultrp = rp.rechercher();

		checkIndexationDossier(resultrp, "AN 666", "enfants", "santé", new String[] { "dyslexie", "prise en charge" });
		checkIndexationDossier(resultrp, "AN 11211", "enfants", "santé", new String[] { "dyslexie", "prise en charge" });
		checkIndexationDossier(resultrp, "AN 26035", "enfants", "santé", new String[] { "dyslexie", "prise en charge" });

		checkIndexationDossier(resultrp, "AN 314159", "administration", "activités", new String[] { "montant" });
		checkIndexationDossier(resultrp, "AN 314160", "administration", "activités", new String[] { "montant" });

		logout();
	}

	/*
	 * Vérifie les données d'indexation de la QE
	 */
	public void checkIndexationDossier(RechercheResultPage resultrp, String numQuestion, String rubrique,
			String ta_rubrique, String[] analyse) {
		resultrp.displayQuestionDetail(numQuestion);
		DossierPage dossierPage = resultrp.getPage(DossierPage.class);
		BordereauOnglet bordereauOnglet = dossierPage.goToOngletBordereau();
		bordereauOnglet.waitForPageSourcePart("Indexation", 5);
		Assert.assertTrue(bordereauOnglet.contientTeteAnalyse(ta_rubrique));
		Assert.assertTrue(bordereauOnglet.contientRubrique(rubrique));
		Assert.assertTrue(bordereauOnglet.contientAnalyses(analyse));
	}

}
