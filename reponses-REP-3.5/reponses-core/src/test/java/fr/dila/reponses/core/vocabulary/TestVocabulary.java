package fr.dila.reponses.core.vocabulary;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.schema.NXSchema;
import org.nuxeo.ecm.core.schema.types.Schema;
import org.nuxeo.ecm.directory.Directory;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.runtime.test.NXRuntimeTestCase;

import fr.dila.st.core.service.STServiceLocator;

/**
 * Test du service de vocabulaire.
 * 
 * @author jtremeaux
 */
public class TestVocabulary extends NXRuntimeTestCase {

	private static final String TA_RUBRIQUE_TEST = "TA_rubrique_test";

    private static final String AN_RUBRIQUE_TEST = "AN_rubrique_test";

    private static final Log log = LogFactory.getLog(TestVocabulary.class);
	
    // initialize by setUp
	private DirectoryService directoryService;
	
    @Override
    public void setUp() throws Exception {
        super.setUp();

        // deploy directory service + sql factory
        deployBundle("org.nuxeo.ecm.directory");
        deployBundle("org.nuxeo.ecm.directory.sql");
        
        // deploy schemas for test dirs
        deployContrib("org.nuxeo.ecm.core.schema", "OSGI-INF/SchemaService.xml");
        deployContrib("org.nuxeo.ecm.core", "OSGI-INF/CoreExtensions.xml");        
        deployContrib("org.nuxeo.ecm.directory.types.contrib","OSGI-INF/DirectoryTypes.xml");
        
		//import local contrib vocabulary test
		deployContrib("fr.dila.reponses.core.tests","OSGI-INF/test-vocabulary-contrib.xml");
		        
        directoryService = STServiceLocator.getDirectoryService();
        assertNotNull(directoryService);
	}
	

	public void testDeclSchemaVocabulary(){
	    Schema schema = NXSchema.getSchemaManager().getSchema("vocabulary");
	    assertNotNull(schema);
	}
	

    public void testVocDilaCreation() throws Exception {
        log.info("begin : test vocabulary conf info");
        Set<String> names = new HashSet<String>();
        List<Directory> dirs = directoryService.getDirectories();
        for(Directory d : dirs){
            names.add(d.getName());
        }
        assertTrue(names.contains(AN_RUBRIQUE_TEST));
        assertTrue(names.contains(TA_RUBRIQUE_TEST));

	}

    public void testVocDilaValue() throws Exception {
        log.info("begin : test vocabulary value");
		Session anRubriqueTestSession = null;
        Session taRubriqueTestSession = null;
        try {
	        Directory anRubriqueDir = directoryService.getDirectory(AN_RUBRIQUE_TEST);
	        assertNotNull(anRubriqueDir);
	
	        anRubriqueTestSession = directoryService.open(AN_RUBRIQUE_TEST);
	        assertTrue(anRubriqueTestSession.hasEntry("Têtes d'analyse : Liste générale"));
	
	
	        Directory taRubrique = directoryService.getDirectory(TA_RUBRIQUE_TEST);
	        assertNotNull(taRubrique);
	        taRubriqueTestSession = directoryService.open(TA_RUBRIQUE_TEST);
	        assertTrue(taRubriqueTestSession.hasEntry("2"));
	        DocumentModel affil = taRubriqueTestSession.getEntry("2");
	        assertEquals("Têtes d'analyse : Liste générale", affil.getProperty("xvocabulary", "parent"));
        } finally {
        	if(anRubriqueTestSession != null){
        		anRubriqueTestSession.close();
        	}
        	if(taRubriqueTestSession != null){
        		taRubriqueTestSession.close();
        	}
        }
	}

    public void testNewVocSchema() throws Exception {

        log.info("begin : test new vocabulary schema");

        Set<String> names = new HashSet<String>();
        List<Directory> dirs = directoryService.getDirectories();
        for(Directory d : dirs){
            names.add(d.getName());
        }
        assertTrue(names.contains(AN_RUBRIQUE_TEST));
        assertTrue(names.contains(TA_RUBRIQUE_TEST));
        assertTrue(names.contains("legislature"));
		
        Session legislatureSession = null;
        try {
	        Directory legislatureDir = directoryService.getDirectory("legislature");
	        assertNotNull(legislatureDir);
	        legislatureSession = directoryService.open("legislature");
	        assertTrue(legislatureSession.hasEntry("13"));
	        DocumentModelList listLegisltature = legislatureSession.getEntries();
	        assertNotNull(listLegisltature);
	        int nbLegisltature = listLegisltature.size();
	        assertNotNull(nbLegisltature);
	        DocumentModel legislatureCourante = listLegisltature.get(1);
	        String nomLegisltature = (String) legislatureCourante.getProperty("vocabularyLegislature", "label");
	        assertNotNull(nomLegisltature);
        } finally {
        	if(legislatureSession != null){
        		legislatureSession.close();
        	}
        }
	}
	
    /**
     * Test d'un filtre exclusif sur les entrées d'un vocabulaire.
     * 
     * @throws Exception
     */
    public void testFilter() throws Exception {
        
        Session session = null;
        try {
            session = directoryService.open(AN_RUBRIQUE_TEST);
            assertNotNull(session);
            Map<String, Serializable> filter = new HashMap<String, Serializable>();
            DocumentModelList list = session.query(filter);
            boolean found = false;
            for (Iterator<DocumentModel> iter = list.iterator(); iter.hasNext();) {
                DocumentModel doc = iter.next();
                String id = (String) doc.getProperty("vocabulary", "id");
                if ("administration".equals(id)) {
                    found = true;
                }
            }
            assertTrue(found);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
