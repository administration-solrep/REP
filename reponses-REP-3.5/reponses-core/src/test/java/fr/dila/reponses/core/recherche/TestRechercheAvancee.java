package fr.dila.reponses.core.recherche;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModelList;

import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.recherche.Requete;

/**
 * 
 * @author jgomez
 * 
 */

public class TestRechercheAvancee  extends RechercheTestCase   {


//    Numéro Question
    public void testRequeteAvancee_numeroQuestion_mauvaisintervalle() throws Exception {
    	openSession();
        Requete requete = createRequete();
        // mauvais intervalle !
        requete.setNumeroRange(7, 200);
        DocumentModelList retour_numero_rien = rs.query(session, requete);
        assertEquals(0,retour_numero_rien.size());
        closeSession();
    }

    public void testRequeteAvancee_numeroQuestion_bonintervalle() throws Exception {
    	openSession();
        // bon intervalle !       
        Requete requete = createRequete();
        requete.setNumeroRange(0, 20000);
        DocumentModelList docs = session.query("SELECT * FROM Question" );
        assertNotNull(docs);
        assertEquals(1,docs.size());
        DocumentModelList retour_numero = rs.query(session, requete);
        assertEquals(1,retour_numero.size());
        closeSession();
    }

    // Date JO Question
    public void testRequeteAvancee_dateJOQuestion_mauvaisintervalle() throws Exception {
    	openSession();
        Requete requete = createRequete();
        Calendar dateDebut = GregorianCalendar.getInstance();
        dateDebut.set(2000,5,14);
        Calendar dateFin = GregorianCalendar.getInstance();
        dateFin.set(2005,5,14);
        requete.setDateRange(dateDebut, dateFin);
        DocumentModelList retour_date = rs.query(session, requete);
        assertEquals(0,retour_date.size());
        closeSession();
    }
    
    public void testRequeteAvancee_dateJOQuestion_bonintervalle() throws Exception {
    	openSession();
        Requete requete = createRequete();
        Calendar dateDebut = GregorianCalendar.getInstance();
        dateDebut.set(2000,5,14);
        Calendar dateFin = GregorianCalendar.getInstance();
        dateFin.set(2015,5,14);
        requete.setDateRange( dateDebut, dateFin);
        DocumentModelList retour_date = rs.query(session, requete);
        assertEquals(1,retour_date.size());
        closeSession();
    }
    
////    //Test date de debut null
    public void testRequeteAvancee_dateJOQuestion_debutnull() throws Exception {
    	openSession();
        Requete requete = createRequete();
        Calendar dateFin = GregorianCalendar.getInstance();
        dateFin.set(2015,5,14);
        requete.setDateRange( null, dateFin);
        DocumentModelList retour_date = rs.query(session, requete);
        assertEquals(1,retour_date.size());
        closeSession();
    }
//    
////  //Test date de debut null qui echoue
    public void testRequeteAvancee_dateJOQuestion_debutnullmauvais() throws Exception {
    	openSession();
        Requete requete = createRequete();
        Calendar dateFin = GregorianCalendar.getInstance();
        dateFin.set(1990,5,14);
        requete.setDateRange( null, dateFin);
        DocumentModelList retour_date = rs.query(session, requete);
        assertEquals(0,retour_date.size());
        closeSession();
    }
//    
//    
////  //Test date de fin null
    public void testRequeteAvancee_dateJOQuestion_finnull() throws Exception {
    	openSession();
        Requete requete = createRequete();
        Calendar dateDebut = GregorianCalendar.getInstance();
        dateDebut.set(2000,5,14);
        requete.setDateRange( dateDebut, null);
        DocumentModelList retour_date = rs.query(session, requete);
        assertEquals(1,retour_date.size());
        closeSession();
    }
//    
////  //Test date de fin null qui echoue
    public void testRequeteAvancee_dateJOQuestion_finnullmauvais() throws Exception {
    	openSession();
        Requete requete = createRequete();
        Calendar dateDebut = GregorianCalendar.getInstance();
        dateDebut.set(2020,5,14);
        requete.setDateRange( dateDebut, null);
        DocumentModelList retour_date = rs.query(session, requete);
        assertEquals(0,retour_date.size());
        closeSession();
    }
//    
//    
//    // Date JO Reponse
    public void testRequeteAvancee_dateJOReponse_mauvaisintervalle() throws Exception {
    	openSession();
        Requete requete = createRequete();
        Calendar dateDebut = GregorianCalendar.getInstance();
        dateDebut.set(1990,5,14);
        Calendar dateFin = GregorianCalendar.getInstance();
        dateFin.set(2000,5,14);
        requete.setDateRangeReponse(dateDebut, dateFin);
        DocumentModelList retour_date = rs.query(session, requete);
        assertEquals(0,retour_date.size());
        closeSession();
    }
    // Groupe politique
    public void testRequeteAvancee_groupePolitique_bon() throws Exception {
    	openSession();
          Requete requete = createRequete();
          requete.setGroupePolitique("UMP");
          DocumentModelList  retour = rs.query(session, requete);
          assertEquals(1,retour.size());
          closeSession();
      }
//    
    public void testRequeteAvancee_groupePolitique_mauvais() throws Exception {
    	openSession();
        Requete requete = createRequete();
        requete.setGroupePolitique("PS");
        DocumentModelList  retour = rs.query(session, requete);
        assertEquals(0,retour.size());
        closeSession();
    }
    
    public void testRequeteAvancee_nomAuteur_bon() throws Exception {
    	openSession();
        Requete requete = createRequete();
        String nomauteur = "Taillerand Jean-Marc";
        requete.setNomAuteur(nomauteur);
        assertEquals("Taillerand Jean-Marc",question1.getNomCompletAuteur());
        DocumentModelList  retour = rs.query(session, requete);
        assertEquals(1,retour.size());
        closeSession();
    }
    
    public void testRequeteAvancee_nomAuteur_mauvais() throws Exception {
    	openSession();
        Requete requete = createRequete();
        requete.setNomAuteur("Taillerant Jean-Marc");
        DocumentModelList  retour = rs.query(session, requete);
        assertEquals(0,retour.size());
        closeSession();
    }
    
    public void testRequeteAvancee_accent() throws Exception {
    	openSession();
        String nomauteur = "Hervé André";
        String nomauteurSansAccent = Normalizer.normalize(nomauteur, Normalizer.Form.NFD);
        nomauteurSansAccent = nomauteurSansAccent.replaceAll("[^\\p{ASCII}]", "");
        assertEquals("Herve Andre",nomauteurSansAccent);
        closeSession();
    }
    
    public void testRequeteAvancee_parenthese() throws Exception {
    	openSession();
        Requete requete = createRequete();
        List<String> origineQuestion = new ArrayList<String>();
        requete.init();
        origineQuestion.add("AN");
        origineQuestion.add("SENAT");
        requete.setOrigineQuestion(origineQuestion);
        List<String> typeQuestions = new ArrayList<String>();
        typeQuestions.add(VocabularyConstants.QUESTION_TYPE_QE);
        requete.setTypeQuestion(typeQuestions);
        requete.doBeforeQuery();
        assertEquals(BASE_QUERY + " WHERE (((q.qu:origineQuestion = 'AN' OR q.qu:origineQuestion = 'SENAT') AND (q.qu:typeQuestion = 'QE')))",rs.getFullQuery(session,requete));
        closeSession();
    }
    
    /**
     * Assure que le traitement du champ question est bien protégé par des parenthèses.
     * @throws Exception
     */
    public void testRequeteNumeroQuestion() throws Exception {
    	openSession();
        Requete requete = createRequete();
        requete.init();
        requete.setChampRequeteSimple("50;1546");
        List<String> typeQuestions = new ArrayList<String>();
        typeQuestions.add(VocabularyConstants.QUESTION_TYPE_QE);
        requete.setTypeQuestion(typeQuestions);
        requete.doBeforeQuery();
     //   assertEquals("((q.ecm:fulltext_idQuestion LIKE '50') OR (q.qu:numeroQuestion = 1546))",requete.getClauseChampRequeteSimple());
        assertEquals(BASE_QUERY + " WHERE ((((q.ecm:fulltext_idQuestion LIKE '50') OR (q.qu:numeroQuestion = 1546)) AND (q.qu:typeQuestion = 'QE')))",rs.getFullQuery(session,requete));
        closeSession();
    }
    
}