package fr.dila.reponses.core.cases;


import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;
import org.nuxeo.ecm.core.schema.DocumentType;
import org.nuxeo.ecm.core.versioning.VersioningService;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.cases.flux.RErratum;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.service.ReponseService;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;
import fr.dila.reponses.core.service.ReponsesServiceLocator;

/**
 * Ajout de test sur la manipulation des versions de reponses
 * @author spesnel
 *
 */
public class TestReponse extends ReponsesRepositoryTestCase {

    private static final Log LOG = LogFactory.getLog(TestReponse.class); 
    
    private ReponseService reponseService;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        deployBundle("org.nuxeo.ecm.platform.versioning.api");
        deployBundle("org.nuxeo.ecm.platform.versioning");        

        reponseService = ReponsesServiceLocator.getReponseService();
        assertNotNull(reponseService);
    }

    public void testReponseCreationHandled() throws Exception {
    	openSession();
    	
        // Cree l'objet representant le document
	    String path = "mypath";
	    
	    Dossier dossier = createDossier(); 
        DocumentType reponse = session.getDocumentType(DossierConstants.REPONSE_DOCUMENT_TYPE);
        assertNotNull("Does our type exist?", reponse);

        //create the object that represents the new document
        DocumentModel modelDesired=new DocumentModelImpl("/",path,DossierConstants.REPONSE_DOCUMENT_TYPE);
    	modelDesired.setProperty("reponse", "identifiant", 12356);
		modelDesired.setProperty("reponse", "dateValidation", new Date());
		modelDesired.setProperty("reponse", "datePublicationJOReponse", new Date());
		modelDesired.setProperty("reponse", "pageJOReponse", 3);
		modelDesired.setProperty("reponse", "verrou", "oui");
		modelDesired.setProperty("note", "note", "test");
		
		//create the document in the repository
        DocumentModel modelResult = session.createDocument(modelDesired);
        assertNotNull(modelResult.getAdapter(Reponse.class));
        assertTrue(modelResult.hasFacet("Versionable"));
        
        //save new reponse with new data
        modelResult.setProperty("note", "note", "note version 1");
        
        modelResult = reponseService.saveReponse(session, modelResult,dossier.getDocument());
        DocumentModel initVersion = modelResult;
        
        //checkVersionNumber
        assertEquals(reponseService.getReponseVersionDocumentList(session, modelResult).size(),0);
        
        //make sure that the path is the parent path (/) plus our new path
        assertEquals("path is same?","/"+path,modelResult.getPathAsString());
        assertEquals("path is same? (sanity)", "/"+path, modelDesired.getPathAsString());
        //document is created ok, let's see if event handler ran
        assertNotSame("the result object is different than the desired object?",
                modelDesired,modelResult);

        
        //test document reponse property
        assertNotNull(modelResult.getProperty("reponse", "idAuteurReponse"));
        assertEquals("Administrator", modelResult.getProperty("reponse", "idAuteurReponse"));
        assertEquals("note version 1", modelResult.getProperty("note", "note"));
        
        //test document versionning : document must be in major version 1
        Long numeroVersion = 1L;
        assertNotNull(modelResult.getPropertyValue(VersioningService.MAJOR_VERSION_PROP));
        modelResult = reponseService.incrementReponseVersion(session, modelResult);
        assertEquals(numeroVersion, modelResult.getPropertyValue(VersioningService.MAJOR_VERSION_PROP));
        //assertEquals(0, ((Long)modelResult.getPropertyValue(VersioningService.MINOR_VERSION_PROP)).longValue());
        //call service to check n° version
        assertNotNull(reponseService.getReponseMajorVersionNumber(session, modelResult));
        assertEquals(numeroVersion.intValue(), reponseService.getReponseMajorVersionNumber(session, modelResult));
        
        // CREATE A NEW VERSION (v2.0)       
        modelResult.setProperty("note", "note", "note version 2");
        modelResult = reponseService.incrementReponseVersion(session, modelResult);        
        assertEquals(2L, reponseService.getReponseMajorVersionNumber(session, modelResult));
        

        // CREATE A NEW VERSION (v3.0)        
        modelResult.setProperty("note", "note", "note version 3");
        reponseService.incrementReponseVersion(session, modelResult);
        modelResult = session.getDocument(modelResult.getRef());
        assertEquals(3L, reponseService.getReponseMajorVersionNumber(session, modelResult));        
        
        
        
        
        
        {
            DocumentRef docref = initVersion.getRef();
            
            DocumentModel rep = session.getDocument(docref);
            assertEquals(3L, reponseService.getReponseMajorVersionNumber(session, rep));
            assertEquals("note version 3", rep.getProperty("note", "note"));
        
            // verfi recup de version
            DocumentModel repv1 = reponseService.getReponseOldVersionDocument(session, rep, 1);
            assertEquals(1L, reponseService.getReponseMajorVersionNumber(session, repv1));

            DocumentModel repv2 = reponseService.getReponseOldVersionDocument(session, rep, 2);
            assertEquals(2L, reponseService.getReponseMajorVersionNumber(session, repv2));

            DocumentModel repv3 = reponseService.getReponseOldVersionDocument(session, rep, 3);
            assertEquals(3L, reponseService.getReponseMajorVersionNumber(session, repv3));

            LOG.info(rep);
            // parcours des versions
            List<DocumentModel> versions = reponseService.getReponseVersionDocumentList(session, rep);
            assertEquals(3, versions.size());
            for(DocumentModel dm : versions){
                LOG.info(dm);
                int num = reponseService.getReponseMajorVersionNumber(session, dm);
                String note = "note version " + num;
                assertEquals(note, dm.getProperty("note", "note"));
                
                String auteur = dm.getProperty(DossierConstants.REPONSE_DOCUMENT_SCHEMA, DossierConstants.DOSSIER_ID_AUTEUR_REPONSE).toString();
                
                LOG.info(auteur);
            }
        }
        
        
        session.cancel();
        closeSession();
   }
   
   /**
    * Test de l'errata de la réponse, cas de la réponse non publiée
    * @throws Exception
    */
   public void testErratumReponsesHandled_reponsenonpubliee() throws Exception{
	   openSession();
	   
       // Cree l'objet representant le document
       String path = "mypath";
       
       DocumentType reponseDoctype = session.getDocumentType(DossierConstants.REPONSE_DOCUMENT_TYPE);
       assertNotNull("Does our type exist?", reponseDoctype);

       //Sauvegarde d'une réponse non publiée
       DocumentModel modelDesired = new DocumentModelImpl("/",path,DossierConstants.REPONSE_DOCUMENT_TYPE);
       Reponse reponse = modelDesired.getAdapter(Reponse.class);
       reponse.setTexteReponse( "Ceci est le message initial");
       DocumentModel reponsesResult = session.createDocument(reponse.getDocument());
       
       DocumentModel savedReponseDoc = reponseService.saveReponseAndErratum(session, reponsesResult, null);
       Reponse savedReponse =  savedReponseDoc.getAdapter(Reponse.class);
       assertNotNull(savedReponse.getErrata());
       assertEquals(0,savedReponse.getErrata().size());
       reponse.setDateJOreponse(Calendar.getInstance());
       reponse.setPageJOreponse((long) 3);
       
       closeSession();
   }
   
   
   /**
    * Test de l'errata de la réponse, cas de la réponse publiée
    * @throws Exception
    */
   public void testErratumReponsesHandled_reponsepubliee() throws Exception{
	   openSession();
	   
       // Cree l'objet representant le document
       String path = "mypath";
       
       DocumentType reponseDoctype = session.getDocumentType(DossierConstants.REPONSE_DOCUMENT_TYPE);
       assertNotNull("Does our type exist?", reponseDoctype);

       //Sauvegarde d'une réponse non publiée
       DocumentModel modelDesired = new DocumentModelImpl("/",path,DossierConstants.REPONSE_DOCUMENT_TYPE);
       Reponse reponse = modelDesired.getAdapter(Reponse.class);
       reponse.setTexteReponse( "Ceci est le message final");
       reponse.setCurrentErratum( "Correction du message initial en final");
       reponse.setDateJOreponse(Calendar.getInstance());
       reponse.setPageJOreponse((long) 3);
       assertTrue(reponse.isPublished());
       DocumentModel reponsesResult = session.createDocument(reponse.getDocument());
       
       DocumentModel savedReponseDoc = reponseService.saveReponseAndErratum(session, reponsesResult, null);
       Reponse savedReponse =  savedReponseDoc.getAdapter(Reponse.class);
       assertNotNull(savedReponse.getErrata());
       assertEquals(1,savedReponse.getErrata().size());
       RErratum erratum = savedReponse.getErrata().get(0);
       assertEquals("Correction du message initial en final", erratum.getTexteErratum());
       assertEquals("Ceci est le message final", erratum.getTexteConsolide());
       
       closeSession();
   }
   
  
    public void testFondDeDossierCreationHandled() throws Exception {
    	openSession();
    	
        // Cree l'objet representant le document
	    String path="mypath";
		DocumentModel fddModel = session.createDocumentModel(path, "unFDD","FondDeDossier");
		assertNotNull(fddModel);
		
		// Verifie que l'objet est folderish
        session.createDocument(fddModel);
        DocumentModel doc = session.createDocumentModel(
        		fddModel.getPathAsString(), "unFile",
                "File");
        assertNotNull(doc);
        
     // Verifie que l'objet n'est pas folderish pour les objet de type case
        DocumentModel doc2 = session.createDocumentModel(
        		fddModel.getPathAsString(), "unFile",
                "Case");
        assertNotNull(doc2);
        
     // Verifie les propriétés de l'objet 
		DocumentModel modelDesired=new DocumentModelImpl("/",path,"FondDeDossier");
		assertNotNull("fond dossier doesn't exist",modelDesired);		
		DocumentModel modelResult = session.createDocument(modelDesired);
		
	    // Controle si le chemin du fichier est correct
	    assertEquals("path is same?","/"+path,modelResult.getPathAsString());
	    assertEquals("path is same? (sanity)", "/"+path, modelDesired.getPathAsString());
	       
        DocumentType reponseType = session.getDocumentType("Reponse");
        assertNotNull("Le type fdd n'existe pas ",reponseType);
        
        closeSession();
        
   }
    
    
}
