package fr.dila.reponses.webtest.tests.webdriver10;

import java.util.List;

import junit.framework.Assert;
import fr.dila.reponses.webtest.constant.ConstantesLotQuestion;
import fr.dila.reponses.webtest.helper.AbstractWebTest;
import fr.dila.reponses.webtest.model.PlanClassementItem;
import fr.dila.reponses.webtest.pages.CorbeillePage;
import fr.dila.reponses.webtest.pages.classement.PlanClassementPage;
import fr.dila.st.annotations.TestDocumentation;
import fr.sword.naiad.commons.webtest.annotation.WebTest;

/**
 * Les tests du pla de classement 
 * @author jgomez
 *
 */
public class Test05PlanClassement extends AbstractWebTest{

	@WebTest(description="1.2.1 Ajout de question - Vérification de l'ajout des questions par le plan de classement")
	@TestDocumentation(categories={"Plan de classement"})
	public void testVerificationPlanClassemenentAN() throws InterruptedException{
		CorbeillePage corbeillePage = loginAsAdmin();
		PlanClassementPage planClassementPage = corbeillePage.getBandeauMenu().goToPlanClassement();
		planClassementPage.toggleAn();
		List<PlanClassementItem> topLevelItems = planClassementPage.getTopLevelItems();
		Assert.assertEquals((int) ConstantesLotQuestion.PLAN_CLASSEMENT_AN_COUNT, topLevelItems.size());
		PlanClassementItem administration = topLevelItems.get(0);
		Assert.assertEquals("Le tag doit être administration", "administration", administration.getTag());
		Assert.assertEquals("Le nombre de tag doit être " + ConstantesLotQuestion.TOTAL_QUESTIONS_ADMIN, (int) ConstantesLotQuestion.TOTAL_QUESTIONS_ADMIN, (int) administration.getCount());
		logout();
	}
	
}
