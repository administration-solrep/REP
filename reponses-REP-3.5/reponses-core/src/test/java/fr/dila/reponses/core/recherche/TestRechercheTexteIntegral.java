package fr.dila.reponses.core.recherche;

import org.nuxeo.ecm.core.api.DocumentModelList;

import fr.dila.reponses.api.recherche.Requete;
import fr.dila.reponses.api.service.RechercheService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.constant.STQueryConstant;

/**
 * 
 * @author jgomez
 * 
 */

public class TestRechercheTexteIntegral  extends RechercheTestCase   {

    
   
    @Override
    public void setUp() throws Exception {
        super.setUp();
        openSession();
    }
    
    @Override
    public void tearDown() throws Exception {
    	closeSession();
        super.tearDown();        
    }

    // Test tous les champs nuls
    public void testSubClauseNull() throws Exception {
        Requete requete = createRequete();
        DocumentModelList retour = rs.query(session, requete);
        assertEquals(1,retour.size());
    }
    
    // S'assure que la sous-clause est bien calculé -> plutot dans le test requete
    public void testComputeSubClause() throws Exception {
        Requete requete = createRequete();
        requete.setQueryType(STQueryConstant.NXQL);
        requete.setDansTexteReponse(false);
        requete.setDansTexteQuestion(true);
        requete.setDansTitre(true);
        String critere = "cheval-et-chat'lioe";
        String critere_result = "${cheval-et-chat''lioe}";
        requete.setCritereRechercheIntegral(critere);
        requete.doBeforeQuery();
        String subclause = requete.getSubClause();
        assertEquals(String.format("(ecm:fulltext_txtQuestion = \"%s\" OR ecm:fulltext_senatTitre = \"%s\")",critere_result,critere_result),subclause);
    }
    
    public void testSearchChateau() throws Exception {
        Requete requete = createRequete();
        requete.init();
        requete.setDansTexteQuestion(true);
        requete.setCritereRechercheIntegral("avec");
        requete.doBeforeQuery();
        RechercheService rechercheService = ReponsesServiceLocator.getRechercheService();
        String query = rechercheService.getFullQuery(session,requete);
        assertEquals(BASE_QUERY + " WHERE (((q.ecm:fulltext_txtQuestion = \"${avec}\")))",query);
    }
    
    public void testSearchMedecin() throws Exception {
        Requete requete = createRequete();
        requete.init();
        requete.setDansTexteQuestion(true);
        requete.setCritereRechercheIntegral("médecin\r\n");
        requete.doBeforeQuery();
        RechercheService rechercheService = ReponsesServiceLocator.getRechercheService();
        String query = rechercheService.getFullQuery(session,requete);
        assertEquals(BASE_QUERY + " WHERE (((q.ecm:fulltext_txtQuestion = \"${médecin}\")))",query);
    }
    
    public void testSearchChaineNumérique() throws Exception {
        Requete requete = createRequete();
        requete.init();
        requete.setDansTexteQuestion(true);
        requete.setCritereRechercheIntegral("Une voiture à 75 000 euros");
        requete.doBeforeQuery();
        RechercheService rechercheService = ReponsesServiceLocator.getRechercheService();
        String query = rechercheService.getFullQuery(session,requete);
        assertEquals(BASE_QUERY + " WHERE (((q.ecm:fulltext_txtQuestion = \"${Une} ${voiture} ${75} ${000} ${euros}\")))",query);
    }
    
    public void testRechercheExacte() throws Exception {
        Requete requete = createRequete();
        requete.init();
        requete.setDansTexteQuestion(true);
        requete.setCritereRechercheIntegral("un cadeau de roi, c'est vrai");
        requete.setAppliquerRechercheExacte(true);
        requete.doBeforeQuery();
        RechercheService rechercheService = ReponsesServiceLocator.getRechercheService();
        String query = rechercheService.getFullQuery(session,requete);
        assertEquals(BASE_QUERY + " WHERE (((q.ecm:fulltext_txtQuestion = \"un cadeau de roi, c''est vrai\")))",query);
    }
    
    public void testRechercheExacteMinus() throws Exception {
        Requete requete = createRequete();
        requete.init();
        requete.setDansTexteQuestion(true);
        requete.setCritereRechercheIntegral("renforcée par le décret n° 92-1354");
        requete.setAppliquerRechercheExacte(true);
        requete.doBeforeQuery();
        RechercheService rechercheService = ReponsesServiceLocator.getRechercheService();
        String query = rechercheService.getFullQuery(session,requete);
        assertEquals(BASE_QUERY + " WHERE (((q.ecm:fulltext_txtQuestion = \"renforcée par le décret n° 92{-}1354\")))",query);
    }
    
}