package fr.dila.reponses.core.recherche;

import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.recherche.Requete;

/**
 * 
 * @author jgomez
 * 
 */

public class TestRechercheCore  extends RechercheTestCase   {
    
    public static Boolean RUN_ALL_TEST = false;
    
    public void testEmptyRequete() throws Exception{
    	openSession();
        Requete requete = rs.createRequete(session, "requete");
        requete.init();
        requete.doBeforeQuery();
        List<DocumentModel> docs = rs.query(session,requete);
        assertEquals(BASE_QUERY, rs.getFullQuery(session,requete)); 
        assertNotNull(docs);
        assertEquals(1,docs.size());
        DocumentModel docRetour = docs.get(0);
        String groupePol = (String) docRetour.getProperty("qu:groupePolitique").getValue();
        assertEquals("UMP",groupePol);
        docs = rs.query(session,requete);
        assertNotNull(docs);
        assertEquals(1,docs.size());
        closeSession();
    }
    

    public void testFixture() throws Exception{
    	openSession();
        assertEquals(new Long(4),dossier1.getNumeroQuestion());
        assertEquals(Boolean.TRUE,reponse1.isPublished());
        assertEquals(Boolean.TRUE,dossier1.getReponse(session).isPublished());
        assertEquals(Boolean.TRUE,dossier1.getQuestion(session).hasReponseInitiee());
        closeSession();
    }
    
    
    public void testQuery() throws Exception{
    	openSession();
        DocumentModelList docs =  session.query("SELECT * FROM Document WHERE ecm:primaryType IN ('Question')");
        assertNotNull(docs);
        assertEquals(1,docs.size());
        docs =  session.query("SELECT * FROM Question" );
        assertNotNull(docs);
        assertEquals(1,docs.size());
        Question dos = docs.get(0).getAdapter(Question.class);
        assertEquals(4,dos.getNumeroQuestion().intValue());
        assertEquals(DossierConstants.QUESTION_DOCUMENT_TYPE, docs.get(0).getType());
        docs =  session.query(String.format("SELECT * FROM Question WHERE qu:numeroQuestion = '%s'","4" ));
        assertNotNull(docs);
        assertEquals(1,docs.size());
        docs =  session.query(String.format("SELECT * FROM Question WHERE qu:numeroQuestion <> '%s'","4" ));
        assertNotNull(docs);
        assertEquals(0,docs.size());
        closeSession();
    }
    
}