package fr.dila.reponses.core.recherche;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IterableQueryResult;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.schema.DublincoreSchemaUtils;

public class TestUFNXQLForResearch  extends ReponsesRepositoryTestCase {

    private static Log log  = LogFactory.getLog(TestUFNXQLForResearch.class);
    
    private DocumentModel dossierDoc1;
    private String questionId1;
    
    @Override
    public void setUp() throws Exception{
        super.setUp();

        openSession();
        
        EnvReponseFixture fixture = new EnvReponseFixture();
        fixture.setUpEnv(session);
        Dossier dossier1 =  fixture.getDossier1();
        dossierDoc1 = dossier1.getDocument();
        questionId1 =  dossier1.getQuestion(session).getDocument().getId();
    }
    
    @Override
    public void tearDown() throws Exception {
    	closeSession();
    	super.tearDown();
    }
    
    public void testListQuestion() throws ClientException {
        {
            String query = "SELECT q.ecm:uuid AS id FROM Question AS q";
            Object[] params = null;
            IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params);
            try {
                Iterator<Map<String, Serializable>> it = res.iterator();
                while(it.hasNext()){
                    Map<String, Serializable> m = it.next();
                    assertEquals(1, m.size());
                    Serializable sid = m.get("id");
                    assertNotNull(sid);
                    String id = (String)sid;
                    assertNotNull(id);
                    assertEquals(questionId1, id);
    //                for(String key : m.keySet()){
    //                    log.info(key + " --> " + m.get(key));
    //                }
                }
                assertEquals(1, res.size());
            }finally{
                res.close();
            }
        }
        {
            // nombre de question par auteur
            
            String query = "SELECT q.qu:nomCompletAuteur AS auteur, count() FROM Question AS q GROUP BY q.qu:nomCompletAuteur";
            Object[] params = null;
            IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params);
            try {
                Iterator<Map<String, Serializable>> it = res.iterator();
                while(it.hasNext()){
                    Map<String, Serializable> m = it.next();
                    assertEquals(2, m.size());
                    
                    String auteur = (String)m.get("auteur");
                    assertNotNull(auteur);
                    assertEquals("Taillerand Jean-Marc", auteur);
                    
                    Long count = (Long)m.get("count()");
                    assertNotNull(count);
                    assertEquals(new Long(1), count);
                   
//                    for(String key : m.keySet()){
//                        log.info(key + " --> " + m.get(key));
//                    }
                }
                assertEquals(1, res.size());
            }finally{
                res.close();
            }
        }
    }
    
    public void testQuestionDossier() throws ClientException {
        String query = null;
        IterableQueryResult res = null;
        query = "SELECT  q.ecm:uuid as id, d.ecm:uuid, d.dc:title " +
            "FROM Dossier AS d, Question AS q WHERE " + 
            "q.ecm:uuid = d.dos:idDocumentQuestion AND d.dc:title LIKE ?" ;
        Object[] params = new Object[]{"Do%4"};
        res = QueryUtils.doUFNXQLQuery(session, query, params);
        try {
            Iterator<Map<String, Serializable>> it = res.iterator();
            while(it.hasNext()){
                Map<String, Serializable> m = it.next();
                assertEquals(3, m.size());
                
                String id = (String)m.get("id");
                assertNotNull(id);
                assertEquals(questionId1, id);
                
                id = (String)m.get("d.ecm:uuid");
                assertNotNull(id);
                assertEquals(dossierDoc1.getId(), id);
                
                String title = (String)m.get("d.dc:title");
                assertNotNull(title);
                assertEquals(DublincoreSchemaUtils.getTitle(dossierDoc1), title);
                
            }
            assertEquals(1, res.size());
        }finally{
            if(res != null){
                res.close();
                res = null;
            }
        }
        
        try {
            query = "SELECT q.ecm:uuid AS id FROM Question AS q WHERE isChildOf(q.ecm:uuid, SELECT d.ecm:uuid FROM Dossier AS d) = 1";
            res = QueryUtils.doUFNXQLQuery(session, query, null);
            fail();
        } catch(ClientException e){
            // expect exception because use isChildOf with optimized type and with only one parameter
        } finally {
            if(res != null){
                res.close();
                res = null;
            }
        }
        
        try {
            query = "SELECT q.ecm:uuid AS id FROM Question AS q WHERE isChildOf(q.ecm:uuid, q.ecm:parentId, SELECT d.ecm:uuid FROM Dossier AS d WHERE d.dc:title LIKE 'Do%4') = 1";
            res = QueryUtils.doUFNXQLQuery(session, query, null);
            assertEquals(1, res.size());
        } finally {
            if(res != null){
                res.close();
                res = null;
            }
        }
    }
    
    public void testQuestionIndexation1() throws ClientException {

        String query = "SELECT  q.ecm:uuid FROM Question AS q WHERE q.ixa:AN_rubrique = ?";
        Object[] params = new Object[]{"ecologie"};
        IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params);
        try {
            Iterator<Map<String, Serializable>> it = res.iterator();
            while (it.hasNext()) {
                Map<String, Serializable> m = it.next();
                for (String key : m.keySet()) {
                    log.info(key + " --> " + m.get(key));
                }
            }
            assertEquals(1, res.size());
        } finally {
            res.close();
        }
    }
    
    public void testQuestionIndexation() throws ClientException {

        {
            String query = "SELECT  q.ecm:uuid, q.ixa:AN_rubrique FROM Question AS q";
            Object[] params = null;
            IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params);
            try {
                Iterator<Map<String, Serializable>> it = res.iterator();
                while (it.hasNext()) {
                    Map<String, Serializable> m = it.next();
                    for (String key : m.keySet()) {
                        log.info(key + " --> " + m.get(key));
                    }
                    String id = (String) m.get("q.ecm:uuid");
                    assertEquals(questionId1, id);
                }
                // 2 resultats :
                // les combinaison de l'id de la question et des deux rubrique d'indexation
                assertEquals(2, res.size());
            } finally {
                res.close();
            }
        }

        // SELECT IXA_AN_RUBRIQUE.ITEM AS LABEL, COUNT(IXA_AN_RUBRIQUE.ITEM) AS COUNT
        {
            String query = "SELECT  q.ixa:AN_rubrique AS label, count() as count FROM Question AS q group by q.ixa:AN_rubrique";
            Object[] params = null;
            IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params);
            try {
                Iterator<Map<String, Serializable>> it = res.iterator();
                while (it.hasNext()) {
                    Map<String, Serializable> m = it.next();
                    for (String key : m.keySet()) {
                        log.info(key + " --> " + m.get(key));
                    }
                    String label = (String) m.get("label");
                    Long count = (Long)m.get("count");
                    assertEquals(new Long(1L), count);
                    assertTrue("ecologie".equals(label) || "agroalimentaire".equals(label));
                }
                // deux rubrique d'indexation pour 
                assertEquals(2, res.size());
            } finally {
                res.close();
            }
        }
    }
    
}
