package fr.dila.reponses.webtest.tests.webdriver10;

import org.junit.Assert;

import fr.dila.reponses.webtest.constant.ConstantesLotQuestion;
import fr.dila.reponses.webtest.helper.AbstractWebTest;
import fr.dila.reponses.webtest.pages.CorbeillePage;
import fr.dila.reponses.webtest.pages.recherche.RecherchePage;
import fr.dila.reponses.webtest.pages.recherche.RechercheResultPage;
import fr.dila.st.annotations.TestDocumentation;
import fr.sword.naiad.commons.webtest.annotation.WebTest;

/**
 * Test pour la recherche avancée des questions
 * 
 * @author user
 * 
 */
public class Test06RechercheQuestion extends AbstractWebTest {

	/**
	 * On vérifie que le nombre de questions injectés corresponds
	 */
	@WebTest(description = "1.2.1 Ajout de question - Vérification du total", order = 10)
	@TestDocumentation(categories = { "Recherche" })
	public void testVerificationInjection() {
		CorbeillePage corbeillePage = loginAsAdmin();
		RecherchePage recherchePage = corbeillePage.getBandeauMenu().goToRecherche();
		RechercheResultPage result = recherchePage.rechercher();
		Assert.assertEquals(ConstantesLotQuestion.TOTAL_QUESTIONS_APRES_INJECTION, result.getResultCount());
		logout();
	}

	/**
	 * On recherche quelques questions avec la recherche rapide
	 * 
	 * @throws InterruptedException
	 */
	@WebTest(description = "1.2.2 Ajout de question - recherche rapide sur quelques questions", order = 20)
	@TestDocumentation(categories = { "Recherche" })
	public void testRechercheRapide() {
		CorbeillePage corbeillePage = loginAsAdmin();
		for (String requete : ConstantesLotQuestion.REQUETES_DE_VERIFICATION) {
			RechercheResultPage results = corbeillePage.getBandeauMenu().rechercheRapide(requete);
			Assert.assertEquals(1, (int) results.getResultCount());
		}
		logout();
	}

	@WebTest(description = "1.2.3 Recherche - Vérification de l'affichage d'une question en cours", order = 30)
	@TestDocumentation(categories = { "Recherche" })
	public void testAffichageDesResultats() {
		CorbeillePage corbeillePage = loginAsAdmin();
		RechercheResultPage result = corbeillePage.getBandeauMenu().rechercheRapide("*");
		result.displayQuestionDetail(ConstantesLotQuestion.QUESTION_EN_COURS);
		Assert.assertTrue(result.areActionsAvailable());
		logout();
	}

}
