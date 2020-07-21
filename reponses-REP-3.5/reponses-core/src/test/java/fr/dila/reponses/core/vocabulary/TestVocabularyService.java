/**
 * 
 */
package fr.dila.reponses.core.vocabulary;

import java.util.List;

import org.nuxeo.runtime.api.Framework;

import fr.dila.cm.test.CaseManagementRepositoryTestCase;
import fr.dila.reponses.api.service.ReponsesVocabularyService;
import fr.dila.reponses.api.vocabulary.VocabularyConnector;
import fr.dila.reponses.core.service.ReponsesServiceLocator;

/**
 * @author admin
 * 
 */
public class TestVocabularyService extends CaseManagementRepositoryTestCase {

	//private static final Log log = LogFactory.getLog(TestVocabularyService.class);

	private static final String XVOCABULARY_TEST = "xvocabulary_test";
    private static final String VOCABULARY_TEST = "vocabulary_test";
    private static final String TA_RUBRIQUE_TEST = "TA_rubrique_test";
    private ReponsesVocabularyService vocService;

	@Override
	public void setUp() throws Exception {

		super.setUp();
		// deploy directory service + sql factory
		deployBundle("org.nuxeo.ecm.directory");
		deployBundle("org.nuxeo.ecm.directory.sql");

		// deploy schemas for test dirs
        deployContrib("fr.dila.st.core", "OSGI-INF/service/vocabulary-framework.xml");
		deployBundle("fr.dila.reponses.core");
		deployContrib("fr.dila.reponses.core.tests","OSGI-INF/test-vocabulary-contrib.xml");
        deployContrib("fr.dila.reponses.core", "OSGI-INF/service/vocabulary-framework.xml");
        deployContrib("fr.dila.reponses.core", "OSGI-INF/reponses-vocabulaire-contrib.xml");

		vocService = Framework.getService(ReponsesVocabularyService.class);
		assertNotNull(vocService);
		assertNotNull(ReponsesServiceLocator.getVocabularyService());
	}

	public void testCheckVocabulary() throws Exception {
	    VocabularyConnector indexationElt = vocService.getVocabularyConnector(TA_RUBRIQUE_TEST);
	    boolean check =  indexationElt.check("irrigation");
		assertEquals(true,check);
	}
	
	public void testCheckTARubrique() throws Exception {
	    VocabularyConnector indexationElt =  vocService.getVocabularyConnector(TA_RUBRIQUE_TEST);
	    boolean check =  indexationElt.check("helloWorld");
        assertEquals(false,check);
    }
	
	public void testGetIndexList() throws Exception{
        /** Test sur un vocabulary **/
	    VocabularyConnector indexationElt =  vocService.getVocabularyConnector(VOCABULARY_TEST);
        
//      // On envoie pol, on attend 'politiques communautaires'
        List<String> result_list_pol = indexationElt.getSuggestion("pol");
        assertEquals(1,result_list_pol.size());
        assertTrue(result_list_pol.contains("politiques communautaires"));
        
//      // On envoie tom, on attend 'tomates' et 'tommes de savoie'
        List<String> result_list_tom = indexationElt.getSuggestion("tom");
        assertEquals(2,result_list_tom.size());
        assertTrue(result_list_tom.contains("tomates"));
        assertTrue(result_list_tom.contains("tommes de savoie"));
        
//      // On envoie rien, on n'attend rien.
        List<String> result_list_rien = indexationElt.getSuggestion("rien");
        assertEquals(0,result_list_rien.size());
        assertEquals(0,result_list_rien.size());
        
        /** Test sur un xvocabulary **/
        indexationElt = vocService.getVocabularyConnector(XVOCABULARY_TEST);
        // On envoie bla, on attend 'blabla'
        List<String> result_list_bla =indexationElt.getSuggestion("bla");
        assertTrue(result_list_bla.contains("blabla"));
    }
    
    // Un test sur la fonction qui va devenir publique, avec l'ouverture de la session à l'intérieur de l'helper.
    public void testGetIndexListMinimal() throws Exception{
        VocabularyConnector indexationElt = vocService.getVocabularyConnector(VOCABULARY_TEST);
        List<String> result_list_pol = indexationElt.getSuggestion("pol");
        assertEquals(1,result_list_pol.size());
        assertTrue(result_list_pol.contains("politiques communautaires"));
    }
    
    // Dans le cas de voc multiple vocs
    public void testGetIndexListMultiple() throws Exception{
        VocabularyConnector indexationEltVoc = vocService.getVocabularyConnectorGroup(VOCABULARY_TEST,XVOCABULARY_TEST);
        List<String> result_list_bla = indexationEltVoc.getSuggestion("bla");
        assertEquals(3,result_list_bla.size());
        assertTrue(result_list_bla.contains("blabla"));
        assertTrue(result_list_bla.contains("blaireau"));
        assertTrue(result_list_bla.contains("blame"));
    }
    
    public void testGetMapVocabularyToZone(){
        assertNotNull(vocService.getMapVocabularyToZone());
        assertEquals(3,vocService.getMapVocabularyToZone().size());
        
    }
    
    public void testVocabularyList() throws Exception{
        assertNotNull( vocService.getVocabularyList());
        assertEquals(7,vocService.getVocabularyList().size());
    }
    
    
    
}
