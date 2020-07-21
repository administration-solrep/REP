package fr.dila.reponses.core.recherche;

import org.nuxeo.ecm.core.api.DocumentModelList;

import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.recherche.Requete;
import fr.dila.reponses.api.recherche.Requete.INDEX_MODE;

/**
 * 
 * @author jgomez
 * 
 */

public class TestRechercheIndex  extends RechercheTestCase   {

    /** INDEXATION AN **/
    // Indexation AN rubrique
    public void testIndexationAN_rubrique_bon() throws Exception {
    	openSession();
        Requete requete = createRequete();
        requete.addVocEntry(VocabularyConstants.AN_RUBRIQUE, "Navet");
        requete.addVocEntry(VocabularyConstants.AN_RUBRIQUE, "Jesus");
        requete.addVocEntry(VocabularyConstants.AN_RUBRIQUE, "agroalimentaire");
        //TODO : Voir la spec pour voir si le comportement correspond aux attentes
        DocumentModelList retour = rs.query(session, requete);
        assertEquals(1,retour.size());
        closeSession();
    }
    
    public void testIndexationAN_rubrique_mauvais() throws Exception {
    	openSession();
        Requete requete = createRequete();
        requete.addVocEntry(VocabularyConstants.AN_RUBRIQUE, "Chat");
        requete.addVocEntry(VocabularyConstants.AN_RUBRIQUE, "Biere");
        requete.addVocEntry(VocabularyConstants.AN_RUBRIQUE, "Autruche");
        DocumentModelList retour = rs.query(session, requete);
        assertEquals(0,retour.size());
        closeSession();
    }
    
    // Indexation AN analyse
    public void testIndexationAN_analyse_bon() throws Exception {
    	openSession();
        Requete requete = createRequete();
        requete.addVocEntry(VocabularyConstants.AN_ANALYSE, "Vin");
        requete.addVocEntry(VocabularyConstants.AN_ANALYSE, "choux");
        DocumentModelList retour = rs.query(session, requete);
        assertEquals(1,retour.size());
        closeSession();
    }
    
    public void testIndexationAN_analyse_mauvais() throws Exception {
    	openSession();
        Requete requete = createRequete();
        requete.addVocEntry(VocabularyConstants.AN_ANALYSE, "test1");
        requete.addVocEntry(VocabularyConstants.AN_ANALYSE, "test2");
        requete.addVocEntry(VocabularyConstants.AN_ANALYSE, "test3");
        DocumentModelList retour = rs.query(session, requete);
        assertEquals(0,retour.size());
        closeSession();
    }
    
    // Indexation AN TA
    public void testIndexationAN_TA_bon() throws Exception {
    	openSession();
        Requete requete = createRequete();
        requete.addVocEntry(VocabularyConstants.TA_RUBRIQUE, "vélo");
        requete.addVocEntry(VocabularyConstants.TA_RUBRIQUE, "ta1");
        DocumentModelList retour = rs.query(session, requete);
        assertEquals(1,retour.size());
        closeSession();
    }
    
    public void testIndexationAN_TA_mauvais() throws Exception {
    	openSession();
        Requete requete = createRequete();
        requete.addVocEntry(VocabularyConstants.TA_RUBRIQUE, "test1");
        requete.addVocEntry(VocabularyConstants.TA_RUBRIQUE, "test2");
        requete.addVocEntry(VocabularyConstants.TA_RUBRIQUE, "test3");
        DocumentModelList retour = rs.query(session, requete);
        assertEquals(0,retour.size());
        closeSession();
    }
    
    /** INDEXATION SENAT **/
    // SE theme
    public void testIndexationSenat_theme_bon() throws Exception {
    	openSession();
        Requete requete = createRequete();
        requete.addVocEntry(VocabularyConstants.SE_THEME, "Bidonville");
        DocumentModelList retour = rs.query(session, requete);
        assertEquals(1,retour.size());
        closeSession();
    }

    public void testIndexationSenat_theme_mauvais() throws Exception {
    	openSession();
        Requete requete = createRequete();
        requete.addVocEntry(VocabularyConstants.SE_THEME, "Liberté");
        requete.addVocEntry(VocabularyConstants.SE_THEME, "Egalité");
        DocumentModelList retour = rs.query(session, requete);
        assertEquals(0,retour.size());
        closeSession();
    }
    
    //SE rubrique
    public void testIndexationSenat_rubrique_bon() throws Exception {
    	openSession();
        Requete requete = createRequete();
        requete.addVocEntry(VocabularyConstants.SE_RUBRIQUE, "videos");
        requete.addVocEntry(VocabularyConstants.SE_RUBRIQUE, "BD");
        requete.addVocEntry(VocabularyConstants.SE_RUBRIQUE, "serub2");
        DocumentModelList retour = rs.query(session, requete);
        assertEquals(1,retour.size());
        closeSession();
    }
    
    public void testIndexationSenat_rubrique_mauvais() throws Exception {
    	openSession();
        Requete requete = createRequete();
        requete.addVocEntry(VocabularyConstants.SE_RUBRIQUE, "test1");
        requete.addVocEntry(VocabularyConstants.SE_RUBRIQUE, "test2");
        requete.addVocEntry(VocabularyConstants.SE_RUBRIQUE, "test3");
        DocumentModelList retour = rs.query(session, requete);
        assertEquals(0,retour.size());
        closeSession();
    }
    
    //SE_renvois
    public void testIndexationSenat_renvois_bon() throws Exception {
    	openSession();
        Requete requete = createRequete();
        requete.addVocEntry(VocabularyConstants.SE_RENVOI, "renvois1");
        DocumentModelList retour = rs.query(session, requete);
        assertEquals(1,retour.size());
        closeSession();
    }
    
    public void testIndexationSenat_renvois_mauvais() throws Exception {
    	openSession();
        Requete requete = createRequete();
        requete.addVocEntry(VocabularyConstants.SE_RENVOI, "renvois8");
        requete.addVocEntry(VocabularyConstants.SE_RENVOI, "renvois9");
        requete.addVocEntry(VocabularyConstants.SE_RENVOI, "renvois10");
        DocumentModelList retour = rs.query(session, requete);
        assertEquals(0,retour.size());
        closeSession();
    }
    //mots clef ministere
    public void testIndexationMinistere_bon() throws Exception {
    	openSession();
        Requete requete = createRequete();
        requete.addVocEntry(VocabularyConstants.MOTSCLEF_MINISTERE, "m1");
        requete.addVocEntry(VocabularyConstants.MOTSCLEF_MINISTERE, "m2");
        DocumentModelList retour = rs.query(session, requete);
        assertEquals(1,retour.size());
        closeSession();
    }
    
    public void testIndexationMinistere_mauvais() throws Exception {
    	openSession();
        Requete requete = createRequete();
        requete.addVocEntry(VocabularyConstants.MOTSCLEF_MINISTERE, "m6");
        requete.addVocEntry(VocabularyConstants.MOTSCLEF_MINISTERE, "m22");
        DocumentModelList retour = rs.query(session, requete);
        assertEquals(0,retour.size());
        closeSession();
    }
    
    public void testIndexation_complementaire() throws Exception {
    	openSession();
        Requete requete = createRequete();
        requete.setIndexationMode(INDEX_MODE.INDEX_COMPL);
        requete.addVocEntry(VocabularyConstants.SE_THEME, "tata");
        DocumentModelList retour = rs.query(session, requete);
        assertEquals(1,retour.size());
        closeSession();
    }
    
    public void testIndexation_tous() throws Exception {
    	openSession();
        Requete requete = createRequete();
        requete.setIndexationMode(INDEX_MODE.TOUS);
        requete.addVocEntry(VocabularyConstants.SE_THEME, "toto");
        DocumentModelList retour = rs.query(session, requete);
        assertEquals(1,retour.size());
        closeSession();
    }
    
    public void testIndexation_accent() throws Exception {
    	openSession();
        Requete requete = createRequete();
        requete.init();
        requete.setIndexationMode(INDEX_MODE.INDEX_COMPL);
        requete.addVocEntry(VocabularyConstants.AN_RUBRIQUE, "lutte contre l'exclusion");
        DocumentModelList retour = rs.query(session, requete);
        assertEquals(1,retour.size());
        closeSession();
    }
    
    
}