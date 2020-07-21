package fr.dila.reponses.webtest.tests;

import org.junit.Assert;
import org.junit.Ignore;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import fr.dila.reponses.webtest.helper.AbstractWebTest;
import fr.dila.reponses.webtest.pages.CorbeillePage;
import fr.sword.naiad.commons.webtest.annotation.WebTest;

/**
 * Test des corbeilles.
 * 
 * @author bgamard
 * 
 */
public class TestCorbeilles extends AbstractWebTest {

    @WebTest(description="Test affichage de la corbeille sélectionnable")
    @Ignore
    public void testCorbeilleSelectionnable() {
        WebDriverWait wait = new WebDriverWait(getDriver(), 30);
        
        
        CorbeillePage corbeillePage = login("finance_dgefp", "finance_dgefp", CorbeillePage.class);

        // Vérification du titre du menu
        getDriver().findElement(By.xpath("//form[@id='mailboxMenuForm' and contains(text(), 'Corbeille sélectionnée')]"));
        
        // Saisie d'un poste en autocomplétion
        WebElement posteField = getDriver().findElement(By.id("mailboxTreeForm:nxl_corbeille_selection_poste:nxw_corbeille_selection_poste_single_suggest"));
        posteField.sendKeys("agents bdc");
        By posteAutocomplete = By.xpath("//td[contains(text(), 'Agents BDC (Economie)')]");
        wait.until(ExpectedConditions.visibilityOfElementLocated(posteAutocomplete));
        getDriver().findElement(posteAutocomplete).click();
        
        // Attente de la validation de l'item autocomplété
        
        By autocompletedItemBy = By.xpath("//span[@id='mailboxTreeForm:nxl_corbeille_selection_poste:nxw_corbeille_selection_poste_single_node' and contains(text(), 'Agents BDC (Economie)')]");
        wait.until(ExpectedConditions.visibilityOfElementLocated(autocompletedItemBy));
        
        // Validation de l'outil de sélection de poste
        getDriver().findElement(By.id("mailboxTreeForm:selectCorbeilleSubmit")).click();
        
        Assert.assertTrue(corbeillePage.hasQuestions());
        
        // Retour à l'état initial
        getDriver().findElement(By.id("mailboxTreeForm:nxl_corbeille_selection_poste:nxw_corbeille_selection_poste_single_delete")).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(autocompletedItemBy));
        getDriver().findElement(By.id("mailboxTreeForm:selectCorbeilleSubmit")).click();
        
        // Ouverture de l'organigramme
        getDriver().findElement(By.id("mailboxTreeForm:nxl_corbeille_selection_poste:nxw_corbeille_selection_poste_user_findButton")).click();
        
        By node = By.id("mailboxTreeForm:nxl_corbeille_selection_poste:nxw_corbeille_selection_poste_user_tree:node:0:node:6::min:handle:img:collapsed");
        wait.until(ExpectedConditions.visibilityOfElementLocated(node));
        getDriver().findElement(node).click();
        
        node = By.id("mailboxTreeForm:nxl_corbeille_selection_poste:nxw_corbeille_selection_poste_user_tree:node:0:node:6:node:0::us:handle:img:collapsed");
        wait.until(ExpectedConditions.visibilityOfElementLocated(node));
        getDriver().findElement(node).click();
        
        node = By.id("mailboxTreeForm:nxl_corbeille_selection_poste:nxw_corbeille_selection_poste_user_tree:node:0:node:6:node:0:node:0::poste:handle:img:collapsed");
        wait.until(ExpectedConditions.visibilityOfElementLocated(node));
        getDriver().findElement(node).click();
        
        node = By.id("mailboxTreeForm:nxl_corbeille_selection_poste:nxw_corbeille_selection_poste_user_tree:node:0:node:6:node:0:node:0:node:1::nxw_corbeille_selection_poste_user_add_min");
        wait.until(ExpectedConditions.visibilityOfElementLocated(node));
        getDriver().findElement(node).click();
        
        autocompletedItemBy = By.xpath("//span[@id='mailboxTreeForm:nxl_corbeille_selection_poste:nxw_corbeille_selection_poste_user_node' and contains(text(), 'Bdc bdc@dila.fr')]");
        wait.until(ExpectedConditions.visibilityOfElementLocated(autocompletedItemBy));
        getDriver().findElement(By.id("mailboxTreeForm:selectCorbeilleSubmit")).click();
        
        Assert.assertTrue(corbeillePage.hasQuestions());
        
        logout();

    }
    
    @WebTest(description="Test affichage de la corbeille SGG")
    public void testCorbeilleSGG() {

        CorbeillePage corbeillePage = login("adminsgg", "adminsgg", CorbeillePage.class);

        Assert.assertFalse(corbeillePage.hasQuestions());
        
        // Vérification du titre du menu
        Assert.assertTrue(corbeillePage.hasElement(By.xpath("//*[contains(text(), 'Mes Corbeilles')]")));
        
        WebElement treeElement = getDriver().findElement(By.id("mailboxTreeForm:corbeilleTree:childs"));
        Assert.assertTrue(treeElement.findElements(By.className("rich-tree-node")).size() > 5);
        
        logout();

    }
}
