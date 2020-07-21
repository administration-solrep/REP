package fr.dila.reponses.core.recherche;

import static fr.dila.reponses.api.constant.DossierConstants.DOSSIER_ETAT_NON_RETIRE;
import static fr.dila.reponses.api.constant.DossierConstants.DOSSIER_ETAT_REATTRIBUE;
import static fr.dila.reponses.api.constant.DossierConstants.DOSSIER_ETAT_RETIRE;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModelList;

import fr.dila.reponses.api.constant.RequeteConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.recherche.Requete;
/**
 * 
 * @author jgomez
 * 
 */

public class TestRechercheMetadonnees  extends RechercheTestCase   {

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

    // Origine question
    public void testOrigineQuestion_mauvais() throws Exception {
        Requete requete = createRequete();
        List<String> origineList = new ArrayList<String>();
        origineList.add("SE");
        requete.setOrigineQuestion(origineList);
        requete.init();
        DocumentModelList retour = rs.query(session, requete);
        assertEquals(0,retour.size());
    }
    
    public void testOrigineQuestion_bon() throws Exception {
        Requete requete = createRequete();
        List<String> origineList = new ArrayList<String>();
        origineList.add("SE");
        origineList.add("AN");
        requete.setOrigineQuestion(origineList);
        requete.init();
        String query = rs.getFullQuery(session,requete);
        assertEquals(BASE_QUERY + " WHERE (((q.qu:origineQuestion = 'SE' OR q.qu:origineQuestion = 'AN')))",query);
        DocumentModelList retour = rs.query(session, requete);
        assertEquals(1,retour.size());
    }
    
    // Caractéristique question
    public void testCaracteristiqueQuestion_mauvais() throws Exception {
        Requete requete = createRequete();
        requete.init();
        List<String> cars = new ArrayList<String>();
        cars.add(RequeteConstants.SANS_REPONSE_PUBLIEE);
        requete.setCaracteristiqueQuestion(cars);
        requete.doBeforeQuery();
        DocumentModelList retour = rs.query(session, requete);
        assertEquals(0,retour.size());
    }
    
    
    /**
     * Les 2 conditions ne s'annulent plus, on recherche les questions en cours ou à l'état répondu.
     * @throws Exception
     */
    public void testCaracteristiqueQuestion_bon() throws Exception {
        Requete requete = createRequete();
        requete.init();
        List<String> cars = new ArrayList<String>();
        cars.add(RequeteConstants.AVEC_REPONSE_PUBLIEE);
        cars.add(RequeteConstants.SANS_REPONSE_PUBLIEE);
        requete.setCaracteristiqueQuestion(cars);
        requete.doBeforeQuery();
        DocumentModelList retour = rs.query(session, requete);
        assertEquals(0,retour.size());
    }
    
    // Voir avec le directory service
    public void test_type_bon() throws Exception {
        Requete requete = createRequete();
        List<String> typeQuestions = new ArrayList<String>();
        typeQuestions.add(VocabularyConstants.QUESTION_TYPE_QE);
        requete.setTypeQuestion(typeQuestions);
        requete.init();        
        DocumentModelList retour = rs.query(session, requete);
        assertEquals(1,retour.size());
    }
    
    // Voir avec le directory service
    public void test_type_mauvais() throws Exception {
        Requete requete = createRequete();
        List<String> typeQuestions = new ArrayList<String>();
        typeQuestions.add(VocabularyConstants.QUESTION_TYPE_QOSD);
        requete.setTypeQuestion(typeQuestions);
        DocumentModelList retour = rs.query(session, requete);
        assertEquals(0,retour.size());
    }
    
    public void test_type_etat_question_vide() throws Exception {
        Requete requete = createRequete();
        requete.init();
        requete.doBeforeQuery();
        String query = rs.getFullQuery(session,requete);
        assertEquals(BASE_QUERY,query);
    }
    
    public void test_type_etat_autre_etat_cloture() throws Exception {
        Requete requete = createRequete();
        requete.init();
        requete.setEtatClotureAutre(true);
        requete.doBeforeQuery();
        String query = rs.getFullQuery(session,requete);
        assertEquals(BASE_QUERY + " WHERE (((q.qu:etatQuestion = 'cloture_autre')))",query);
    }
    
    public void test_type_etat_caduque() throws Exception {
        Requete requete = createRequete();
        requete.init();
        requete.setEtatCaduque(true);
        requete.doBeforeQuery();
        String query = rs.getFullQuery(session,requete);
        assertEquals(BASE_QUERY + " WHERE (((q.qu:etatQuestion = 'caduque')))",query);
    }
    
    public void test_type_etat_non_clos_et_clos() throws Exception {
        Requete requete = createRequete();
        requete.init();
        requete.setEtatCaduque(true);
        requete.setEtatClotureAutre(true);
        requete.doBeforeQuery();
        String query = rs.getFullQuery(session,requete);
        assertEquals(BASE_QUERY + " WHERE (((q.qu:etatQuestion = 'caduque' OR q.qu:etatQuestion = 'cloture_autre')))",query);
    }
    
    
    public void test_type_etat_question_tous() throws Exception {
        Requete requete = createRequete();
        requete.init();
        requete.setEtat(DOSSIER_ETAT_NON_RETIRE, true);
        requete.setEtat(DOSSIER_ETAT_RETIRE, true);
        requete.setEtat(DOSSIER_ETAT_REATTRIBUE, true);
        requete.doBeforeQuery();
        String query = rs.getFullQuery(session,requete);
        assertEquals(BASE_QUERY + " WHERE (((q.qu:etatQuestion = 'retiree' AND q.qu:etatReattribue = 1)))",query);
    }
    
    
}