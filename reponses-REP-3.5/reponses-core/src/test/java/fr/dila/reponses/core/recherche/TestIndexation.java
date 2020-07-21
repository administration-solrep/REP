package fr.dila.reponses.core.recherche;


import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.recherche.ReponsesIndexableDocument;
import fr.dila.reponses.api.service.RechercheService;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;
import fr.dila.reponses.core.service.ReponsesServiceLocator;

public class TestIndexation extends ReponsesRepositoryTestCase  {

    private static final Log log = LogFactory.getLog(TestIndexation.class); 
    private RechercheService rs;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
	    rs = ReponsesServiceLocator.getRechercheService();
	    assertNotNull(ReponsesServiceLocator.getVocabularyService());
    }
    
    public ReponsesIndexableDocument createIndexableDocument() throws Exception {
        ReponsesIndexableDocument indexableDoc =null;
        try {
            DocumentModel requeteDoc = rs.createRequete(session, "testRequete").getDocument();
            indexableDoc = requeteDoc.getAdapter(ReponsesIndexableDocument.class);
        } catch (ClientException e) {
            log.error("Incapable de créer la requête de test");
            log.error(e);
        }
        return indexableDoc;
    }
    
    /**
     * Crée un document indexable avec qq valeurs par défaut
     * @return
     * @throws Exception 
     */
    public ReponsesIndexableDocument getDefaultIndexableDocument() throws Exception {
        ReponsesIndexableDocument indexableDoc = createIndexableDocument();
        indexableDoc.addVocEntry(VocabularyConstants.AN_RUBRIQUE,"truc");
        indexableDoc.addVocEntry(VocabularyConstants.AN_RUBRIQUE,"machin");
        indexableDoc.addVocEntry(VocabularyConstants.SE_THEME,"obj1");
        indexableDoc.addVocEntry(VocabularyConstants.SE_THEME,"obj2");
        indexableDoc.addVocEntry(VocabularyConstants.SE_THEME,"obj3");
        indexableDoc.addVocEntry(VocabularyConstants.TA_RUBRIQUE,"bidouille");
        return indexableDoc;
    }
    
    @SuppressWarnings("unchecked")
	public void testAddAndRemoveVocEntry() throws Exception{
    	openSession();
        ReponsesIndexableDocument indexableDoc = createIndexableDocument();
        String vocAN = VocabularyConstants.AN_RUBRIQUE;
        String label1 = "chômage : indemnisation";
        String label2 = "collectivités territoriales";
        indexableDoc.addVocEntry(vocAN,label1);
        indexableDoc.addVocEntry(vocAN,label2);
        List<String> AN_list = (List<String>) indexableDoc.getDocument().getProperty(DossierConstants.INDEXATION_DOCUMENT_SCHEMA,vocAN);
        assertNotNull(AN_list);
        assertEquals(2,AN_list.size());
        indexableDoc.removeVocEntry(vocAN, label1);
        AN_list = (List<String>) indexableDoc.getDocument().getProperty(DossierConstants.INDEXATION_DOCUMENT_SCHEMA,vocAN);
        assertNotNull(AN_list);
        assertEquals(1,AN_list.size());        
        closeSession();
    }
    
	public void testAddAndRemoveVocEntryException() throws Exception{
		openSession();
		
        // Test ajout et retrait sur un vocabulaire ne faisant pas partie du schéma
	    ReponsesIndexableDocument indexableDoc = createIndexableDocument();
	    try{
	        indexableDoc.addVocEntry("notavoc","terre");	
	    	fail("addVoc aurait du lancer une exception");
	    } catch (Exception e) {
			//Ok, c'est une exeption
		}
	    
	    closeSession();
	}
    
    public void testListIndexByZone() throws Exception{
    	openSession();
        ReponsesIndexableDocument indexableDoc = getDefaultIndexableDocument();
        assertNotNull(indexableDoc.getListIndexByZone(VocabularyConstants.INDEXATION_ZONE_AN));
        assertEquals(3,indexableDoc.getListIndexByZone(VocabularyConstants.INDEXATION_ZONE_AN).size());
        assertNotNull(indexableDoc.getListIndexByZone(VocabularyConstants.INDEXATION_ZONE_SENAT));
        assertEquals(3,indexableDoc.getListIndexByZone(VocabularyConstants.INDEXATION_ZONE_SENAT).size());
        indexableDoc.removeVocEntry(VocabularyConstants.AN_RUBRIQUE, "machin");
        indexableDoc.addVocEntry(VocabularyConstants.MOTSCLEF_MINISTERE, "chose");
        assertNotNull(indexableDoc.getListIndexByZone(VocabularyConstants.INDEXATION_ZONE_AN));
        assertEquals(2,indexableDoc.getListIndexByZone(VocabularyConstants.INDEXATION_ZONE_AN).size());
        assertNotNull(indexableDoc.getListIndexByZone(VocabularyConstants.INDEXATION_ZONE_SENAT));
        assertEquals(3,indexableDoc.getListIndexByZone(VocabularyConstants.INDEXATION_ZONE_SENAT).size());
        assertNotNull(indexableDoc.getListIndexByZone(VocabularyConstants.INDEXATION_ZONE_MINISTERE));
        assertEquals(1,indexableDoc.getListIndexByZone(VocabularyConstants.INDEXATION_ZONE_MINISTERE).size());
        assertEquals("motclef_ministere",indexableDoc.getListIndexByZone(VocabularyConstants.INDEXATION_ZONE_MINISTERE).get(0)[0]);
        assertEquals("chose",indexableDoc.getListIndexByZone(VocabularyConstants.INDEXATION_ZONE_MINISTERE).get(0)[1]);
        closeSession();
    }
    
    /**
     * Test le retour de tous les mots-clef sous forme de string
     * @throws Exception
     */
    public void testMotsClef() throws Exception{
    	openSession();
        ReponsesIndexableDocument indexableDoc = getDefaultIndexableDocument();
        String motsClef = indexableDoc.getMotsClef();
        assertNotNull(motsClef);
        assertEquals("obj1 obj2 obj3 truc machin bidouille ",indexableDoc.getMotsClef());
        closeSession();
    }
}
