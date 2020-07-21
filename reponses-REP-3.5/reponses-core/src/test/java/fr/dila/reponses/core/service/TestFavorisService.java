/**
 * 
 */
package fr.dila.reponses.core.service;

import java.util.Calendar;
import java.util.List;

import javax.naming.NameAlreadyBoundException;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.service.FavorisDossierService;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;
import fr.dila.reponses.core.recherche.EnvReponseFixture;

/**
 * @author jgomez
 * 
 */
public class TestFavorisService extends ReponsesRepositoryTestCase {

    private static final String FAVORIS_DOSSIER_ROOT = "FavorisDossierRoot";

    private FavorisDossierService fvs;
    private EnvReponseFixture fixture;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        fvs = ReponsesServiceLocator.getFavorisDossierService();
        assertNotNull(fvs);
        
        openSession();
        fixture = new EnvReponseFixture();
        fixture.setUpEnv(session);
        createDummyRootFolder();
        session.save();
        closeSession();
    }


    public void testCreateFavorisDossier() throws ClientException, NameAlreadyBoundException {
    	openSession();
        //Test création du favoris
    	Calendar dateValidite = Calendar.getInstance();
        fvs.createFavorisRepertoire(session, "dossier_securite",dateValidite);
        Dossier dossier1 = fixture.getDossier1();
        assertNotNull(dossier1.getQuestion(session));
        DocumentModel favori = fvs.createFavorisDossier(session, dossier1.getDocument(),"dossier_securite");
        assertNotNull(favori);   
        
        //Test création du répertoire favoris
        List<DocumentModel> favorisRepertoires = fvs.getFavorisRepertoires(session);
        assertNotNull(favorisRepertoires);
        assertEquals(1,favorisRepertoires.size());
        closeSession();
    }

    /**
     * 
     * Test l'ajout des dossiers aux favoris
     * 
     * @throws ClientException
     * @throws NameAlreadyBoundException 
     */
    public void testAdd() throws ClientException, NameAlreadyBoundException {
    	openSession();
        DocumentModelList docs = new DocumentModelListImpl();
        docs.add(fixture.getDossier1().getDocument());
        Calendar dateValidite = Calendar.getInstance();
        DocumentModel parent = fvs.createFavorisRepertoire(session, "dossier_securite",dateValidite);
        fvs.add(session, docs, "dossier_securite");
        DocumentModelList returnedDocs = (DocumentModelList) fvs.getFavoris(session, parent.getId());
        assertEquals(1,returnedDocs.size());
        DocumentModel favoris = returnedDocs.get(0);
        assertEquals(fixture.getDossier1().getQuestion(session).getDocument().getId(),favoris.getPropertyValue("fvd:targetDocument"));
        closeSession();
    }
    
    /**
     * 
     * Test la suppression des favoris 
     * 
     * @throws ClientException
     * @throws NameAlreadyBoundException 
     */
    public void testDelete() throws ClientException, NameAlreadyBoundException {
    	openSession();
        DocumentModelList docsToAdd = new DocumentModelListImpl();
        docsToAdd.add(fixture.getDossier1().getDocument());
        Calendar dateValidite = Calendar.getInstance();
        DocumentModel parent = fvs.createFavorisRepertoire(session, "dossier_securite", dateValidite);
        fvs.add(session, docsToAdd,"dossier_securite");
        DocumentModelList docsToDelete = new DocumentModelListImpl();
        docsToDelete.add(fixture.getDossier1().getDocument());
        fvs.delete(session, docsToDelete);
        DocumentModelList returnedDocs = (DocumentModelList) fvs.getFavoris(session, parent.getId());
        assertEquals(0,returnedDocs.size());
        closeSession();
    }
    
    /**
     * Cree le dossier qui contient les favoris, içi à la racine de la session, sera crée par le service content-template dans le userworkspace. 
     * @throws ClientException 
     */
    private void createDummyRootFolder() throws ClientException{
        DocumentModel root = session.getRootDocument();
        DocumentModel favorisRoot = new DocumentModelImpl(root.getPathAsString(),"favoris",FAVORIS_DOSSIER_ROOT);
        session.createDocument(favorisRoot);
        session.saveDocument(favorisRoot);
        session.save();
    }
}
