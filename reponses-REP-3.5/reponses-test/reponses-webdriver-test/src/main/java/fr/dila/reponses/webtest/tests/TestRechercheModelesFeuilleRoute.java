package fr.dila.reponses.webtest.tests;

import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Ignore;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fr.dila.reponses.webtest.helper.AbstractWebTest;
import fr.dila.reponses.webtest.pages.CorbeillePage;
import fr.sword.naiad.commons.webtest.annotation.WebTest;

/**
 * Test de la recherche de modèles de feuille de route.
 * 
 * @author bgamard
 * 
 */
public class TestRechercheModelesFeuilleRoute extends AbstractWebTest {

	@WebTest(description="Recherche feuille de route")
	@Ignore	// J'ai rajouté un Ignore pour être iso, car ce test n'était pas lancé avant d'être copié dans src/test/java
    public void testRechercheRapide() {
        getDriver().manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

        login("adminsgg", "adminsgg", CorbeillePage.class);

        getFlog().action("Navigation vers l'administration");
        getDriver().findElement(By.xpath("//a[contains(text(), 'Administration')]")).click();
        
        getFlog().action("Navigation vers les modèles de feuille de route");
        getDriver().findElement(By.xpath("//a[contains(text(), 'Gestion des modèles de feuilles de route')]")).click();
        
        getFlog().action("Recherche du titre \"agri\"");
        WebElement titleField = getDriver().findElement(By.id("searchModeleFeuilleRouteForm:nxl_recherche_rapide_modeles_fdr:nxw_recherche_rapide_modeles_fdr_title"));
        titleField.sendKeys("agri");
        
        getFlog().action("Lancement de la recherche");
        getDriver().findElement(By.id("searchModeleFeuilleRouteForm:search")).click();
        
        getFlog().action("Vérification de la feuille de route trouvée");
        Assert.assertTrue(getDriver().findElements(By.xpath("//span[contains(text(), '1 modèle de feuille de route')]")).size() == 1);
        
        getFlog().action("Ouverture du menu contextuel");
        getDriver().findElement(By.id("feuille_route_model_folder_content:nxl_feuille_route_model_listing:nxw_feuille_route_list_intitule:titleref")).click();
        
        getFlog().action("Ouverture de la feuille de route");
        getDriver().findElement(By.xpath("//li[@class='ctxMenuItemStyle'][1]/a")).click();
        
        getFlog().action("Retour à la liste des modèles de feuille de route");
        getDriver().findElement(By.xpath("//input[@value='Retour à la liste']")).click();
        
        getFlog().action("Vérification du retour vers la recherche");
        Assert.assertTrue(getDriver().findElements(By.xpath("//span[contains(text(), '1 modèle de feuille de route')]")).size() == 1);
        
        getFlog().action("Reset de la recherche");
        getDriver().findElement(By.id("searchModeleFeuilleRouteForm:resetSearch")).click();
        
        getFlog().action("Vérification du reset");
        Assert.assertTrue(getDriver().findElements(By.xpath("//span[contains(text(), '1 modèle de feuille de route')]")).size() == 0);
        
        logout();

        getFlog().testFinished();
    }
    
	@WebTest(description="Recherche feuille de route 2")
	@Ignore
    public void testRecherche() {
        getDriver().manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        
        login("adminsgg", "adminsgg", CorbeillePage.class);

        getFlog().action("Navigation vers l'administration");
        getDriver().findElement(By.xpath("//a[contains(text(), 'Administration')]")).click();
        
        getFlog().action("Navigation vers la recherche des modèles de feuille de route");
        getDriver().findElement(By.xpath("//a[contains(text(), 'Recherche de modèle de feuille de route')]")).click();
        
        getFlog().action("Remplissage des critères de recherche");
        WebElement titleField = getDriver().findElement(By.id("searchModeleFeuilleRouteForm:nxl_recherche_modele_feuille_route_feuille_route:nxw_feuille_route_title"));
        titleField.sendKeys("route");
        
        getFlog().action("Lancement de la recherche");
        getDriver().findElement(By.id("searchModeleFeuilleRouteForm:search")).click();
        
        getFlog().action("Vérification des feuilles de route trouvées");
        Assert.assertTrue(getDriver().findElements(By.xpath("//span[contains(text(), 'modèles de feuilles de route')]")).size() == 1);
        
        getFlog().action("Retour à la recherche");
        getDriver().findElement(By.id("smartSearchResultsActions:edit_search")).click();
        
        getFlog().action("Vérification du retour à la recherche");
        Assert.assertTrue(getDriver().findElements(By.xpath("//span[contains(text(), 'Recherche de modèles de feuilles de route')]")).size() == 1);
        
        getFlog().action("Reset de la recherche");
        getDriver().findElement(By.id("searchModeleFeuilleRouteForm:resetSearch")).click();
        
        logout();

        getFlog().testFinished();
    }
}
