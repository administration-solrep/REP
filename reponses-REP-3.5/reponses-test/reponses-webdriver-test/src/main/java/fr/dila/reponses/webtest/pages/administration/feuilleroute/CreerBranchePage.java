package fr.dila.reponses.webtest.pages.administration.feuilleroute;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import fr.dila.reponses.webtest.pages.ReponsesPage;

public class CreerBranchePage extends ReponsesPage{

	@FindBy(name="document_create:nxl_feuille_route_fork_detail:nxw_feuille_route_fork_detail_branch_count:nxw_feuille_route_fork_detail_branch_count_from")
	private WebElement branchesCountElt;

	@FindBy(how = How.XPATH, using = "//input[@value='Créer']")
	private WebElement creerBtn;
	
	public void setbrancheCount(Integer count){
		branchesCountElt.clear();
		branchesCountElt.sendKeys(String.valueOf(count));
	}
	
	public void creer(){
		creerBtn.click();
	}
}
