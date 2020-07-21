package fr.dila.reponses.core.cases;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;

import fr.dila.reponses.api.cases.Allotissement;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;

/**
 * @author asatre, spl
 */
public class TestAllotissement extends ReponsesRepositoryTestCase {

    private static final Log LOG = LogFactory.getLog(TestAllotissement.class);
    
    public void testFieldEcriture() throws Exception{
    	LOG.info("Start testFieldEcriture");
    	
    	openSession();
    	
    	DocumentModel docModel = createDocument(DossierConstants.ALLOTISSEMENT_DOCUMENT_TYPE, "newAllotTest");    	
    	assertNotNull(docModel);
    	
    	Allotissement allotissement = docModel.getAdapter(Allotissement.class);
    	assertNotNull(allotissement);
    	assertEquals(docModel, allotissement.getDocument());
    	
    	final String docId = docModel.getId();
    	final int NB_ID = 5;
    	final String unNom = "un nom";
    	List<String> idDossiers = new ArrayList<String>();
    	for(int i = 0; i < NB_ID; ++i){
    	    idDossiers.add("id n " + i);
    	}
    	
    	allotissement.setIdDossiers(idDossiers);
    	allotissement.setNom(unNom);
    	session.saveDocument(allotissement.getDocument());
    	session.save();
    	closeSession();
    	openSession();
    	
    	docModel = session.getDocument(new IdRef(docId));
    	allotissement = docModel.getAdapter(Allotissement.class);
    	
    	assertEquals(unNom, allotissement.getNom());
    	List<String> readIdDossiers = allotissement.getIdDossiers();
    	assertEquals(NB_ID, readIdDossiers.size());
    	
    	for(int i = 0; i < NB_ID; ++i){
    	    assertEquals(idDossiers.get(i), readIdDossiers.get(i));
    	}
    	
    	closeSession();
    }

}