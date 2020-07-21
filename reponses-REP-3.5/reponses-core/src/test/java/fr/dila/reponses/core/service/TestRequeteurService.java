package fr.dila.reponses.core.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.RequeteConstants;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;
import fr.dila.reponses.core.recherche.EnvReponseFixture;
import fr.dila.reponses.core.recherche.RechercheTestCase;
import fr.dila.reponses.core.requeteur.MockSession;
import fr.dila.st.api.constant.STRequeteConstants;
import fr.dila.st.api.requeteur.RequeteExperte;
import fr.dila.st.api.service.RequeteurService;
import fr.dila.st.core.service.STServiceLocator;


public class TestRequeteurService  extends ReponsesRepositoryTestCase {

    public static final String BASE_QUERY = RechercheTestCase.BASE_QUERY;
    
    private static final String NOW = "NOW";
    private RequeteExperte requete1;
    private RequeteExperte requete2;
    private RequeteExperte requete3;
    private RequeteExperte requete4;
    private RequeteExperte requete5;
    private RequeteurService service;
    
    @Override
    public void setUp() throws Exception{
        super.setUp();
        openSession();
        requete1 = createRequete1();
        requete2 = createRequete2();
        requete3 = createRequete3();
        requete4 = createRequete4();
        requete5 = createRequete5();
        service =  STServiceLocator.getRequeteurService();
        EnvReponseFixture fixture = new EnvReponseFixture();
        fixture.setUpEnv(session);
    }
    
    @Override
    public void tearDown() throws Exception{
    	closeSession();
    	super.tearDown();
    }
    
    public void testInit(){
        assertNotNull(service);
        assertNotNull(requete1);
        assertEquals(true,requete1.getDocument().hasSchema(STRequeteConstants.SMART_FOLDER_SCHEMA));
    }
    
    public void testGetPattern1(){
        String pattern = service.getPattern(session,requete1);
        assertEquals(BASE_QUERY + " WHERE ((q.qu:numeroQuestion = '4'))",pattern);
    }
    
    public void testCount1() throws ClientException{
        Long count = service.countResults(session,requete1);
        assertEquals(1,(long) count);
    }
    
    public void testQuery1() throws ClientException{
        List<DocumentModel> docs = service.query(session,requete1);
        assertNotNull(docs);
        assertEquals(1,docs.size());
    }
    
    public void testGetPattern(){
    	doTestGetPattern2();
    	doTestGetPattern3();
    	doTestGetPattern4();
    	doTestGetPattern5();
    }
    
    
    private void doTestGetPattern2(){
        String pattern = service.getPattern(session,requete2);
        assertEquals(BASE_QUERY + ",Dossier AS d,Reponse AS r WHERE ((q.qu:numeroQuestion = '4' AND r.note:note LIKE = 'HELLO') AND d.dos:idDocumentQuestion = q.ecm:uuid AND d.dos:idDocumentReponse = r.ecm:uuid )",pattern);
    }
    
    
    
    //Pattern avec la fonction date (valeur en jours)
    private void doTestGetPattern3(){
       Map<String,Object> env = new HashMap<String,Object>();
       env.put(NOW, new DateTime(2011,7,5,10,36, 0, 0));
       String pattern = service.getPattern(session,requete3,env);
       assertEquals(BASE_QUERY + " WHERE ((q.qu:numeroQuestion = '4' AND q.qu:datePublication < DATE '2011-06-20'))",pattern);
    }
    
    //Pattern avec la fonction date (valeur en mois)
    private void doTestGetPattern4(){
       Map<String,Object> env = new HashMap<String,Object>();
       env.put(NOW, new DateTime(2011,7,5,10,36, 0, 0));
       String pattern = service.getPattern(session, requete4,env);
       assertEquals(BASE_QUERY + " WHERE ((q.qu:numeroQuestion = '4' AND q.qu:datePublication < DATE '2011-06-05'))",pattern);
    }
    
    //Pattern avec la fonction ministere
    public void doTestGetPattern5(){
       Map<String,Object> env = new HashMap<String,Object>();
       env.put(NOW, new DateTime(2011,7,5,10,36, 0, 0));
       String pattern = service.getPattern(new MockSession(), requete5,env);
       assertEquals(BASE_QUERY + ",Dossier AS d WHERE ((q.qu:numeroQuestion = '4' AND d.dos:" + DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT + " IN ('503','502')) AND d.dos:idDocumentQuestion = q.ecm:uuid )",pattern);
    }
    
    /**
     * Création d'une requête simple.
     * @return
     * @throws Exception
     */
    private RequeteExperte createRequete1() throws Exception{
        DocumentModel doc = session.createDocumentModel(RequeteConstants.REQUETE_EXPERTE_DOCUMENT_TYPE);
        doc = session.createDocument(doc);
        RequeteExperte requete =  doc.getAdapter(RequeteExperte.class);
        requete.setWhereClause("q.qu:numeroQuestion = '4'");
        return requete;
    }
    
    /**
     * Création d'une requête plus élaborée pour voir si le service de joiture fonctionne correctement. 
     * @return
     * @throws Exception
     */
    private RequeteExperte createRequete2() throws Exception{
        DocumentModel doc = session.createDocumentModel(RequeteConstants.REQUETE_EXPERTE_DOCUMENT_TYPE);
        doc = session.createDocument(doc);
        RequeteExperte requete =  doc.getAdapter(RequeteExperte.class);
        requete.setWhereClause("q.qu:numeroQuestion = '4' AND r.note:note LIKE = 'HELLO'");
        return requete;
    }

    
    /**
     * Création d'une requête avec un mot-clé spécial ufnxql_date: pour être résolue 
     * directement par le requêteur. 
     * @return
     * @throws Exception
     */
    private RequeteExperte createRequete3() throws Exception{
        DocumentModel doc = session.createDocumentModel(RequeteConstants.REQUETE_EXPERTE_DOCUMENT_TYPE);
        doc = session.createDocument(doc);
        RequeteExperte requete =  doc.getAdapter(RequeteExperte.class);
        requete.setWhereClause("q.qu:numeroQuestion = '4' AND q.qu:datePublication < ufnxql_date:(NOW-15J)");
        return requete;
    }
    
    /**
     * Création d'une requête avec un mot-clé spécial ufnxql_date: pour être résolue 
     * directement par le requêteur. 
     * @return
     * @throws Exception
     */
    private RequeteExperte createRequete4() throws Exception{
        DocumentModel doc = session.createDocumentModel(RequeteConstants.REQUETE_EXPERTE_DOCUMENT_TYPE);
        doc = session.createDocument(doc);
        RequeteExperte requete =  doc.getAdapter(RequeteExperte.class);
        requete.setWhereClause("q.qu:numeroQuestion = '4' AND q.qu:datePublication < ufnxql_date:(NOW-1M)");
        return requete;
    }
    
    /** Création d'une requête avec un mot-clé spécial ufnxql_ministere: pour être résolue 
    * directement par le requêteur. 
    * @return
    * @throws Exception
    */
    private RequeteExperte createRequete5() throws Exception{
       DocumentModel doc = session.createDocumentModel(RequeteConstants.REQUETE_EXPERTE_DOCUMENT_TYPE);
       doc = session.createDocument(doc);
       RequeteExperte requete =  doc.getAdapter(RequeteExperte.class);
       requete.setWhereClause("q.qu:numeroQuestion = '4' AND ufnxql_ministere:(d.dos:" + DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT + ")");
       return requete;
   }
}
