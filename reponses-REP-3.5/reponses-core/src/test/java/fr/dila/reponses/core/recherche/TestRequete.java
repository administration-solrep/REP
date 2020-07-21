package fr.dila.reponses.core.recherche;


import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.reponses.api.recherche.Requete;
import fr.dila.reponses.api.service.RechercheService;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.constant.STQueryConstant;

public class TestRequete extends ReponsesRepositoryTestCase {

    private static final Log log = LogFactory.getLog(TestRequete.class); 
    private RechercheService rs;
    
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
	    rs = ReponsesServiceLocator.getRechercheService();
	    assertNotNull(ReponsesServiceLocator.getVocabularyService());
	    openSession();
    }
    
    @Override
    public void tearDown() throws Exception {
    	closeSession();
    	super.tearDown();
    }
    
    
    
    
    //On s'assure que les dates sont bien initialisées à une date de début 
    // et de fin arbitraires mais éloignées, pour permettre une recherche avec une seule date.
    public void testPostTraitementDate() throws Exception{
        Requete requete = createRequete();
        requete.setDateQuestionDebut(new GregorianCalendar());
        assertNotNull(requete.getDateQuestionDebut());
        assertNull(requete.getDateQuestionFin());
        requete.doBeforeQuery();
        assertNotNull(requete.getDateQuestionDebut());
        assertNotNull(requete.getDateQuestionFin());
    }
    
    /**
     * Un problème dans le cacul des intervalles de date
     * @throws Exception 
     */
    public void testBug0029563() throws Exception{
        Requete requete = createRequete();
        Calendar dateDebut = new GregorianCalendar();
        Calendar dateFin = new GregorianCalendar();
        dateFin.add(3, Calendar.MONTH);
        requete.setDateRange(dateDebut, dateFin);
        requete.doBeforeQuery();
        // On s'assure que les dates ne bougent pas ...
        assertEquals(dateDebut.getTime(), requete.getDateQuestionDebut().getTime());
        assertEquals(dateFin.getTime(), requete.getDateQuestionFin().getTime());
        // On met la date de fin nulle, on s'assure qu'elle est bien recalculée la première fois et intouchée la seconde.
        dateDebut = new GregorianCalendar();
        dateFin = null;
        requete.setDateRange(dateDebut, dateFin);
        requete.doBeforeQuery();
        assertEquals(dateDebut.getTime(), requete.getDateQuestionDebut().getTime());
    }
    
    //On s'assure que la sous-clause du texte intégral renvoie bien avec les champs null ou faux
    public void testHasTextRechercheSelected() throws Exception{
        Requete requete = createRequete();
        requete.setDansTexteQuestion(true);
        requete.setDansTexteReponse(false);
        requete.setDansTitre(true);
        assertEquals(Boolean.TRUE,requete.hasTextRechercheSelected());
        
    }
    
    //On s'assure que la sous-clause du texte intégral est bien calculé
    public void testPostTraitementTexteIntegral() throws Exception{
    	Requete requete = createRequete();
        requete.setQueryType(STQueryConstant.NXQL);
        requete.setDansTexteQuestion(true);
        requete.setDansTexteReponse(false);
        requete.setDansTitre(true);
        assertEquals(Boolean.FALSE,requete.getDansTexteReponse());
        assertEquals(Boolean.TRUE,requete.getDansTexteQuestion());
        assertEquals(Boolean.TRUE,requete.getDansTitre());
        String critere = "alouette, gentille alouette";
        String computed_critere = "${alouette,} ${gentille} ${alouette}";
        requete.setCritereRechercheIntegral(critere);
        requete.doBeforeQuery();
        assertNotNull(requete.getSubClause());
        String subclause = requete.getSubClause();
        assertEquals(String.format("(ecm:fulltext_txtQuestion = \"%s\" OR ecm:fulltext_senatTitre = \"%s\")",computed_critere,computed_critere),subclause);
    }
    
    //On s'assure que la sous-clause du texte intégral renvoie bien avec les champs null ou faux
    public void testPostTraitementTexteIntegralEmpty() throws Exception{
    	Requete requete = createRequete();
        requete.setQueryType(STQueryConstant.NXQL);
        requete.setDansTexteQuestion(null);
        requete.setDansTexteReponse(null);
        requete.setDansTitre(null);
        requete.setCritereRechercheIntegral(null);
        requete.doBeforeQuery();
        assertEquals(null,requete.getSubClause());
        
        requete.setDansTexteQuestion(false);
        requete.setDansTexteReponse(false);
        requete.setDansTitre(false);
        requete.setCritereRechercheIntegral(null);
        requete.doBeforeQuery();
        assertEquals(null,requete.getSubClause());
    }
    
    //Initialisation de la requete
    public void testInit() throws Exception{
    	Requete requete = createRequete();
        requete.init();
        assertEquals(Boolean.TRUE,requete.getDansTexteQuestion());
        requete.setCritereRechercheIntegral("hhdleo");
        requete.getDocument().reset();
        requete.init();
        assertEquals(Boolean.TRUE,requete.getDansTexteQuestion());
    }
    
    private Requete createRequete() throws Exception {
        try {
            return  rs.createRequete(session, "testRequete");
        } catch (ClientException e) {
            log.error("Incapable de créer la requête de test", e);
            throw e;
        }
    }
    
}
