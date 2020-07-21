package fr.dila.reponses.webtest.tests.webdriver10;

import java.util.List;

import junit.framework.Assert;

import org.junit.Ignore;

import fr.dila.reponses.webtest.constant.ConstantesLotQuestion;
import fr.dila.reponses.webtest.constant.ConstantesOrga;
import fr.dila.reponses.webtest.helper.AbstractWebTest;
import fr.dila.reponses.webtest.model.PlanClassementItem;
import fr.dila.reponses.webtest.pages.CorbeillePage;
import fr.dila.reponses.webtest.pages.classement.PlanClassementPage;
import fr.dila.reponses.webtest.pages.dossier.BordereauOnglet;
import fr.dila.reponses.webtest.pages.dossier.DossierConnexeOnglet;
import fr.dila.reponses.webtest.pages.dossier.DossierPage;
import fr.dila.reponses.webtest.pages.dossier.ParapheurOnglet;
import fr.dila.reponses.webtest.pages.recherche.RecherchePage;
import fr.dila.reponses.webtest.pages.recherche.RechercheResultPage;
import fr.dila.st.annotations.TestDocumentation;
import fr.sword.naiad.commons.webtest.annotation.WebTest;



/**
 * Tests de modification du bordereau
 * @author jgomez
 *
 */
public class Test18Modification extends AbstractWebTest{

	private static final String RECHERCHE_KEYWORD = "dindon";

	private static final String NOUVELLE_DIRECTION = ConstantesOrga.DIR_DAJ;

	//private static final String NOUVEAU_MINISTERE = ConstantesOrga.MIN_AGRI;

	private static final String RUBRIQUE_AJOUTEE = "décorations, insignes et emblèmes";
	
	public static final String DOSSIER_TEST = ConstantesLotQuestion.DOSSIER_TEST_MODIFICATION;
	
	private static final Integer RUBRIQUE_AJOUTEE_TOTAL = 1;
	
	private static final Integer NB_DOSSIERS_CONNEXE_APRES_MODIF = 8;
	
	@WebTest(description="1.2.10 Modification - Modification du bordereau - indexation", order =10)
	@TestDocumentation(categories={"Indexation", "Bordereau"})
	public void modificationIndexationBordereau(){
		CorbeillePage corbeillePage = loginBdc();
		DossierPage dossierPage = goToPageSurDossierTest(corbeillePage);
		BordereauOnglet bordereauOnglet = dossierPage.goToOngletBordereau();
		dossierPage.lock();
		bordereauOnglet.ajouterIndexationComplementaireRubrique(RUBRIQUE_AJOUTEE);
		// sauvegarde et déverouille
		dossierPage.sauvegarder();
		dossierPage.unlock();
		boolean contientRubrique = bordereauOnglet.contientRubriqueComplementaire(RUBRIQUE_AJOUTEE);
		Assert.assertTrue("Le bordereau doit posséder la rubrique " + RUBRIQUE_AJOUTEE, contientRubrique);
		logout();
	}

	
	@WebTest(description="1.2.10 Modification - indexation - Impact sur la recherche", order =20)
	@TestDocumentation(categories={"Rubrique", "Recherche", "Indexation"})
	public void impactRechercheApresIndexationBordereau(){
		CorbeillePage corbeillePage = loginBdc();
		RecherchePage recherchePage = corbeillePage.getBandeauMenu().goToRecherche();
		recherchePage.toggleRechercheMotsCles();
		recherchePage.toggleIndexationComplementaire();
		recherchePage.setANRubrique(RUBRIQUE_AJOUTEE);
		try {
			Thread.sleep(3500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		RechercheResultPage resultPage = recherchePage.rechercher();
		if (!resultPage.containsQuestion(DOSSIER_TEST)){
			Assert.fail(String.format("Le dossier %s doit être présent chez bdc", DOSSIER_TEST));
		}
		logout();
	}	
	
	@WebTest(description="1.2.10 Modification - indexation - Impact sur le plan de classement", order =30)
	@TestDocumentation(categories={"Indexation", "Plan de classement"})
	public void impactPlanClassementApresIndexationBordereau(){
		CorbeillePage corbeillePage = loginBdc();
		PlanClassementPage planClassementPage = corbeillePage.getBandeauMenu().goToPlanClassement();
		List<PlanClassementItem> items = planClassementPage.getTopLevelItems();
		Boolean rubriqueAjouteeFound = false;
		Integer total = 0;
		for (PlanClassementItem item : items){
			if (RUBRIQUE_AJOUTEE.equals(item.getTag())){
				rubriqueAjouteeFound = true;
				total = item.getCount();
			}
		}
		Assert.assertTrue("Le plan de classement doit contenir la rubrique " + RUBRIQUE_AJOUTEE, rubriqueAjouteeFound);
		Assert.assertEquals("La rubrique " + RUBRIQUE_AJOUTEE + " doit avoir " + RUBRIQUE_AJOUTEE_TOTAL + " dossiers", RUBRIQUE_AJOUTEE_TOTAL, total);
		logout();
	}
	
	@WebTest(description="1.2.10 Modification - indexation - Impact dossier connexe", order =40)
	@TestDocumentation(categories={"Test ignoré"})//@TestDocumentation(category={"Indexation", "Connexité", "Test ignoré"})
	@Ignore
	public void impactDossierConnexeApresIndexationBordereau(){
		CorbeillePage corbeillePage = loginBdc();
		DossierPage dossierPage = goToPageSurDossierTest(corbeillePage);
		DossierConnexeOnglet dossierConnexeOnglet = dossierPage.goToOngletDossierConnexe();
		int nbDossiersConnexes = dossierConnexeOnglet.getNbDossiersConnexes();
		Assert.assertEquals("Le nombre de questions connexe doit être égal à " + NB_DOSSIERS_CONNEXE_APRES_MODIF,(int) NB_DOSSIERS_CONNEXE_APRES_MODIF, nbDossiersConnexes);
		logout();
	}
	
	@WebTest(description="1.2.10 Modification - changement ministère de rattachement et direction pilote - Modification du bordereau", order =50)
	@TestDocumentation(categories={"Bordereau", "Dossier"})
	public void modificationMinistereEtDirectionBordereau(){
		CorbeillePage corbeillePage = loginBdc();
		DossierPage dossierPage = goToPageSurDossierTest(corbeillePage);
		BordereauOnglet bordereauOnglet = dossierPage.goToOngletBordereau();
		dossierPage.lock();
		bordereauOnglet.changerDirectionPilote(NOUVELLE_DIRECTION);
		dossierPage.sauvegarder();
		dossierPage.unlock();
		logout();
	}
	
	
	@WebTest(description="1.2.10 Modification - changement ministère de rattachement et direction pilote - Impact recherche", order =60)
	@TestDocumentation(categories={"Recherche", "Bordereau", "Dossier"})
	public void impactRechercheApresModificationMinEtDirBordereau(){
		CorbeillePage corbeillePage = loginBdc();
		RecherchePage recherchePage = corbeillePage.getBandeauMenu().goToRecherche();
		recherchePage.setDirectionPilote(ConstantesOrga.DIR_DAJ);
		RechercheResultPage resultPage = recherchePage.rechercher();
		if (!resultPage.containsQuestion(DOSSIER_TEST)){
			Assert.fail(String.format("Le dossier %s doit être présent chez bdc", DOSSIER_TEST));
		}
		logout();
	}
	
	@WebTest(description="1.2.10 Modification - Modification de la réponse", order =70)
	@TestDocumentation(categories={"Parapheur"})
	public void modificationReponse() {
		CorbeillePage corbeillePage = loginBdc();
		DossierPage dossierPage = goToPageSurDossierTest(corbeillePage);
		ParapheurOnglet parapheurOnglet = dossierPage.goToOngletParapheur();
		dossierPage.lock();
		parapheurOnglet.ajouterResponseAuDebut("Quel est le dindon difficile ?");
		dossierPage.sauvegarder();
		dossierPage.unlock();
		logout();
	}
	
	@WebTest(description="1.2.10 Modification - Modification de la réponse - Impact recherche", order =80)
	@TestDocumentation(categories={"Recherche", "Parapheur"})
	public void impactRechercheApresModificationReponse(){
		CorbeillePage corbeillePage = loginBdc();
		RecherchePage recherchePage = corbeillePage.getBandeauMenu().goToRecherche();
		recherchePage.toggleRechercheTexteIntegral();
		recherchePage.toggleRechercheExacte();
		recherchePage.setExpressionRecherche(RECHERCHE_KEYWORD);
		recherchePage.toggleDansTexteReponse();
		recherchePage.toggleDansTexteQuestion();
		RechercheResultPage resultPage = recherchePage.rechercher();
		if (!resultPage.containsQuestion(DOSSIER_TEST)){
			Assert.fail(String.format("Le dossier %s doit être présent chez bdc", DOSSIER_TEST));
		}
		Assert.assertEquals("La recherche doit retourner un seul résultat", 1, (int) resultPage.getResultCount());
		logout();
	}
	
	/**
	 * Vérifie que le dossier existe bien et se dirige vers la page
	 * @param corbeillePage la page de la corbeille de l'utilisateur
	 * @return la page du dossier
	 */
	private DossierPage goToPageSurDossierTest(CorbeillePage corbeillePage) {
		RechercheResultPage resultPage = corbeillePage.getBandeauMenu().rechercheRapide(DOSSIER_TEST);
		if (!resultPage.containsQuestion(DOSSIER_TEST)){
			Assert.fail(String.format("Le dossier %s doit être présent chez bdc", DOSSIER_TEST));
		}
		return resultPage.getPage(DossierPage.class);
	}
	
	
}

