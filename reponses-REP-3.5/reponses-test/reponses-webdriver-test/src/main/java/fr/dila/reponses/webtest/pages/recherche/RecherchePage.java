package fr.dila.reponses.webtest.pages.recherche;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.Select;

import fr.dila.reponses.webtest.pages.ReponsesPage;
import fr.dila.st.webdriver.framework.CustomFindBy;
import fr.dila.st.webdriver.framework.CustomHow;
import fr.dila.st.webdriver.framework.STBy;
import fr.dila.st.webdriver.helper.AutoCompletionHelper;
import fr.dila.st.webdriver.helper.NameShortener;

public class RecherchePage extends ReponsesPage{

	private static final String DIRECTION_PILOTE_INPUT = "requete_all_form:nxl_requeteSimple:nxw_directionPilote_suggest";

	private static final String MINISTERE_INPUT = "requete_all_form:nxl_requeteSimple:nxw_ministereRattachement_suggest";
	
	private static final String INDEXATION_COMPLEMENTAIRE = "Indexation complémentaire";

	public static final String INDEXATION_AN_ID = "requete_all_form:nxl_requeteIndexAN:nxw_simple_indexation_an_indexLabel_";
	
	public static final String INDEXATION_AN_ADD_ID = "requete_all_form:nxl_requeteIndexAN:nxw_simple_indexation_an_addIndexLink_";
	
	public static final String RECHERCHE_EXACTE_ID = "requete_all_form:nxl_requeteTexteIntegral:nxw_appliquerRechercheExacte";
	
	public static final String CRITERE_RECHERCHE_ID = "requete_all_form:nxl_requeteTexteIntegral:nxw_critere_requete";
	
	public static final String DANS_TEXTE_REPONSES_ID = "requete_all_form:nxl_requeteTexteIntegral:nxw_dansTexteReponse";
	
	public static final String DANS_TEXTE_QUESTION_ID = "requete_all_form:nxl_requeteTexteIntegral:nxw_dansTexteQuestion";
	
	@FindBy(id="requete_all_form:nxl_requeteSimple:nxw_legislature")
	private WebElement legislatureElt;
	
	@FindBy(id="requete_all_form:nxl_requeteSimple:nxw_type_question_list")
	private WebElement typeQuestionElt;
	
	@FindBy(id="requete_all_form:nxl_requeteSimple:nxw_auteur")
	private WebElement auteurElt;
	
	@FindBy(id="requete_all_form:nxl_requeteSimple:nxw_critereRequete")
	private WebElement numeros;
	
	@FindBy(how = How.PARTIAL_LINK_TEXT, using = "Recherche sur les mots-clés")
	private WebElement voletRechercheMotsCles;
	
	@FindBy(how = How.PARTIAL_LINK_TEXT, using = "Recherche sur le texte intégral")
	private WebElement voletRechercheTexteIntegral;
	
	@FindBy(id="requete_all_form:rechercheSubmitButton")
	private WebElement rechercherBtn;
	
	@CustomFindBy(how = CustomHow.LABEL_TEXT, using = INDEXATION_COMPLEMENTAIRE)
	private WebElement indexationComplementaireElt;
	
	@FindBy(id = RECHERCHE_EXACTE_ID)
	private WebElement rechercheExacteElt;
	
	@FindBy(id = CRITERE_RECHERCHE_ID)
	private WebElement critereRechercheElt;
	
	@FindBy(id = DANS_TEXTE_REPONSES_ID)
	private WebElement dansTexteReponseElt;
	
	@FindBy(id = DANS_TEXTE_QUESTION_ID)
	private WebElement dansTexteQuestionElt;
	
	
	public void setLegislature(String idLegislature){
		Select selectLegislature = new Select(this.legislatureElt);
		selectLegislature.selectByValue(idLegislature);
	}
	
	public void setTypeQuestion(String typeQuestion){
		Select selectTypeQuestion = new Select(this.typeQuestionElt);
		selectTypeQuestion.selectByValue(typeQuestion);
	}
	
	public void setAuteur(String auteur){
		fillField("Auteur", this.auteurElt, auteur);
	}
	
	public void setMinistereRattachement(String minRattachement){
		By minRattachementBy = By.id(DIRECTION_PILOTE_INPUT);
		setAutocompleteValue(new NameShortener(minRattachement, 0), minRattachementBy);
	}
	
	public void setNumeros(String requete) {
		fillField("Numeros", this.numeros, requete);
	}
	
	public RechercheResultPage rechercher() {
		return clickToPage(rechercherBtn, RechercheResultPage.class);
	}

	public void toggleIndexationComplementaire() {
		indexationComplementaireElt.click();
	}

	public void setANRubrique(String rubriqueAjoutee) {
		By indexationAnBy = By.id(INDEXATION_AN_ID);
		AutoCompletionHelper.setRubriqueValue(getFlog(), getDriver(),indexationAnBy, new NameShortener(rubriqueAjoutee, 0));
		
		By addBy = By.id(INDEXATION_AN_ADD_ID);
		WebElement addBtn = getDriver().findElement(addBy);
		addBtn.click();
	}

	public void toggleRechercheExacte() {
		rechercheExacteElt.click();
	}

	public void setExpressionRecherche(String rechercheKeyword) {
		fillField("Saisir votre expression : ", critereRechercheElt, rechercheKeyword);
		
	}

	public void toggleDansTexteReponse() {
		dansTexteReponseElt.click();
	}

	public void toggleDansTexteQuestion() {
		dansTexteQuestionElt.click();
		
	}

	/**
	 * Déplie ou replie le volet de recherche sur les mots-clés
	 */
	public void toggleRechercheMotsCles() {
		By by = STBy.labelText(INDEXATION_COMPLEMENTAIRE);
		voletRechercheMotsCles.click();		
		waitForPageSourcePartDisplayed(by, 10);
	}
	
	/**
	 * Déplie ou replie le volet de recherche sur le texte intégral
	 */
	public void toggleRechercheTexteIntegral() {
		voletRechercheTexteIntegral.click();
		By by = By.id(DANS_TEXTE_QUESTION_ID);
		waitForPageSourcePartDisplayed(by, 10);
	}
	

	public void setMinistere(String ministereRattachement) {
		By ministereRattachementIdBy = By.id(MINISTERE_INPUT);
		setAutocompleteValue(new NameShortener(ministereRattachement,0), ministereRattachementIdBy);
		
	}

	public void setDirectionPilote(String directionPilote) {
		By directionPiloteIdBy = By.id(DIRECTION_PILOTE_INPUT);
		setAutocompleteValue(new NameShortener(directionPilote,0), directionPiloteIdBy);
	}
	
}
