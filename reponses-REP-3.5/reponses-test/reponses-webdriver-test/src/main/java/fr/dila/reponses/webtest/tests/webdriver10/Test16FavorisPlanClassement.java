package fr.dila.reponses.webtest.tests.webdriver10;

import junit.framework.Assert;
import fr.dila.reponses.webtest.constant.ConstantesLotQuestion;
import fr.dila.reponses.webtest.constant.ConstantesOrga;
import fr.dila.reponses.webtest.helper.AbstractWebTest;
import fr.dila.reponses.webtest.model.UserManager;
import fr.dila.reponses.webtest.pages.CorbeillePage;
import fr.dila.reponses.webtest.pages.classement.PlanClassementPage;
import fr.dila.reponses.webtest.pages.recherche.RechercheResultPage;
import fr.dila.st.annotations.TestDocumentation;
import fr.dila.st.webdriver.model.STUser;
import fr.sword.naiad.commons.webtest.annotation.WebTest;

/**
 * Tests sur les favoris du plan de classement
 * @author user
 *
 */
public class Test16FavorisPlanClassement extends AbstractWebTest{

	private static final String SS_RUBRIQUE_ACTIVITES = "activités";
	private static final String RUBRIQUE_ADMINISTRATION = "administration";

	@WebTest(description = "1.3 Plan de classement - Ajout d'un favoris du plan de classement")
	@TestDocumentation(categories={"Favoris", "Plan de classement"})
	public void ajouterUnFavoriPlanClassement(){
		UserManager manager = new UserManager();
		STUser bdc = manager.getUserFrom(ConstantesOrga.POSTE_BDC_ECO);
		CorbeillePage corbeillePage = login(bdc, CorbeillePage.class);
		PlanClassementPage planClassementPage = corbeillePage.getBandeauMenu().goToPlanClassement();
		planClassementPage.open(RUBRIQUE_ADMINISTRATION);
		try {
			Thread.sleep(3500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Assert.assertTrue("La sous-rubrique doit être activité", planClassementPage.containsPlanClassementItem(SS_RUBRIQUE_ACTIVITES));
		planClassementPage.ajouteAuxFavoris(SS_RUBRIQUE_ACTIVITES);
		try {
			Thread.sleep(3500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Boolean appartientAuxFavoris = planClassementPage.appartientAuxFavoris(SS_RUBRIQUE_ACTIVITES);
		Assert.assertTrue("Le favoris " + SS_RUBRIQUE_ACTIVITES + " doit appartenir aux favoris", appartientAuxFavoris);
		logout();
		corbeillePage = login(bdc, CorbeillePage.class);
		appartientAuxFavoris = planClassementPage.appartientAuxFavoris(SS_RUBRIQUE_ACTIVITES);
		Assert.assertTrue("Le favoris " + SS_RUBRIQUE_ACTIVITES + " n'est pas persistant", appartientAuxFavoris);
		RechercheResultPage resultPage = planClassementPage.executerRequeteDuFavori(SS_RUBRIQUE_ACTIVITES);
		try {
			Thread.sleep(3500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Integer total = ConstantesLotQuestion.TOTAL_DOSSIERS_DE_SS_RUBRIQUE_ACTIVITE_VISIBLE_PAR_BDC;
		Assert.assertEquals("Le nombre de dossiers retournés par ce favoris doit être : " + total, total, resultPage.getResultCount());
		logout();
	}
	
	
}
