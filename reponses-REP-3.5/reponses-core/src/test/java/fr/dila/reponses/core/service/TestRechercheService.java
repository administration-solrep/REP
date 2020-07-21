package fr.dila.reponses.core.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.directory.shared.ldap.util.ArrayUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesParametreConstant;
import fr.dila.reponses.api.recherche.Requete;
import fr.dila.reponses.api.service.RechercheService;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;
import fr.dila.st.api.recherche.Recherche;
import fr.dila.st.core.helper.ParameterTestHelper;
import fr.dila.st.core.service.STServiceLocator;



public class TestRechercheService extends ReponsesRepositoryTestCase {

	private static final Log LOG = LogFactory.getLog(TestRechercheService.class);
	
	private RechercheService rs;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		deployBundle("fr.dila.reponses.core");
		deployContrib("fr.dila.reponses.core.tests", "OSGI-INF/test-recherche-contrib.xml");		
	    openSession();
		rs = ReponsesServiceLocator.getRechercheService();
		assertNotNull(rs);
		
		assertNotNull(STServiceLocator.getSTParametreService());
	} 
	
	@Override
	public void tearDown() throws Exception {
		closeSession();
		super.tearDown();
	}
	
	public void testGetListDocumentRecherche() {
		List<Recherche> rechercheList = rs.getRecherches();
		assertEquals(2, rechercheList.size());
		assertEquals("recherche_simple_mots_clefs_sur_actes",rechercheList.get(0).getRechercheName());
		assertEquals("simple",rechercheList.get(0).getMode());
		assertEquals("recherche_complexe_mots_clefs_sur_actes",rechercheList.get(1).getRechercheName());
		assertEquals("complexe",rechercheList.get(1).getMode() );
	}
	
	public void testGetRecherche() {
		Recherche recherche_simple = rs.getRecherche("simple", "mots-clefs", "acte");
		assertNotNull(recherche_simple);
		assertEquals("recherche_simple_mots_clefs_sur_actes", recherche_simple.getRechercheName());
		assertEquals("recherche_simple_contentview", recherche_simple.getContentViewName());
		Recherche recherche_complexe = rs.getRecherche("complexe", "mots-clefs", "acte");
		assertNotNull(recherche_complexe);
		assertEquals("recherche_complexe_mots_clefs_sur_actes", recherche_complexe.getRechercheName());
		assertEquals("recherche_complexe_contentview", recherche_complexe.getContentViewName());
	}
	
	public void testGetRechercheByName(){
		Recherche recherche_simple = rs.getRecherche("recherche_simple_mots_clefs_sur_actes");
		assertNotNull(recherche_simple);
		assertEquals("simple",recherche_simple.getMode());
		Recherche recherche_complexe = rs.getRecherche("recherche_complexe_mots_clefs_sur_actes");
		assertNotNull(recherche_complexe);
		assertEquals("RequeteComplexe",recherche_complexe.getRequeteName());
	}
	
	public void testFdr() throws ClientException{
	    Requete req = rs.createRequete(session, "essai");
	    req.getDocument().setProperty("requeteFeuilleRoute","typeStep","1");
	    req.getDocument().setProperty("requeteFeuilleRoute","statusStep","validated_2");
	    req.doBeforeQuery();
        String query = rs.getWhereClause(req, "requeteFdr");
        assertEquals("(e2.rtsk:type = \'1\' AND e2.rtsk:validationStatus = '2' AND e2.ecm:currentLifeCycleState = 'validated')",query);
    }
	
   public void testCumulPanneaux() throws ClientException{
        Requete req = rs.createRequete(session, "essai");
        req.getDocument().setProperty("requeteFeuilleRoute","typeStep","1");
        req.getDocument().setProperty("requeteFeuilleRoute","statusStep","validated_2");
        List<String> index = new ArrayList<String>();
        index.add("cailloux");
        index.add("chou");
        req.getDocument().setProperty("indexation","SE_theme",index);
        req.getDocument().setProperty("indexation","SE_rubrique",index);
        req.doBeforeQuery();
        String query = rs.getWhereClause(req, "requeteFdr","requeteIndex");
        assertEquals("(e2.rtsk:type = '1' AND e2.rtsk:validationStatus = '2' AND e2.ecm:currentLifeCycleState = 'validated') AND ((q.ixa:SE_rubrique = 'cailloux' OR q.ixa:SE_rubrique = 'chou') AND (q.ixa:SE_theme = 'cailloux' OR q.ixa:SE_theme = 'chou'))",query);
    }
   
   public void testGetRequeteParts() throws ClientException{
       Requete req = rs.createRequete(session, "essai");
       req.setIndexationMode(Requete.INDEX_MODE.INDEX_ORIG);
       String[] requeteModelNames = ((RechercheServiceImpl) rs).getRequeteParts(req);
       assertEquals(6,requeteModelNames.length);
       assertEquals(false, ArrayUtils.contains(requeteModelNames, "requeteIndexCompl"));
       assertEquals(true, ArrayUtils.contains(requeteModelNames, "requeteIndex"));
   }
   
   public void testCumulTousPanneaux() throws ClientException{
       Requete req = rs.createRequete(session, "essai");
       req.getDocument().setProperty("requeteFeuilleRoute","typeStep","1");
       req.getDocument().setProperty("requeteFeuilleRoute","statusStep","validated_2");
       List<String> index = new ArrayList<String>();
       index.add("cailloux");
       index.add("chou");
       req.getDocument().setProperty("indexation","SE_theme",index);
       req.getDocument().setProperty("indexation","SE_rubrique",index);
       req.setIndexationMode(Requete.INDEX_MODE.INDEX_ORIG);
       req.doBeforeQuery();
       String[] queryModels =  ((RechercheServiceImpl) rs).getRequeteParts(req);
       String query = rs.getWhereClause(req,queryModels);
       assertEquals("((q.ixa:SE_rubrique = 'cailloux' OR q.ixa:SE_rubrique = 'chou') AND (q.ixa:SE_theme = 'cailloux' OR q.ixa:SE_theme = 'chou')) AND (e2.rtsk:type = '1' AND e2.rtsk:validationStatus = '2' AND e2.ecm:currentLifeCycleState = 'validated')",query);
   }
   
   public void testCumulPanneauxNuls() throws ClientException{
       Requete req = rs.createRequete(session, "essai");
       req.getDocument().setProperty("requeteFeuilleRoute","typeStep","1");
       req.getDocument().setProperty("requeteFeuilleRoute","statusStep","validated_2");
       List<String> index = new ArrayList<String>();
       index.add("cailloux");
       index.add("chou");
       req.doBeforeQuery();
       String query = rs.getWhereClause(req, "requeteFdr","requeteIndex","requeteComplexe");
       assertEquals("(e2.rtsk:type = \'1\' AND e2.rtsk:validationStatus = '2' AND e2.ecm:currentLifeCycleState = 'validated')",query);
   }

   public void testTousPanneauxNuls() throws ClientException{
       Requete req = rs.createRequete(session, "essai");
       String query = rs.getWhereClause(req, "requeteFdr","requeteIndex","requeteComplexe");
       assertEquals("",query);
   }
   
   public void testWrongPanneau() throws ClientException{
       Requete req = rs.createRequete(session, "essai");
       String query = rs.getWhereClause(req, "requeteFdr","indexation","requeteComplexe","requeteTexteIntegral");
       assertEquals("",query);
   }
   
   public void testSearchQuestionBySourceNumero() throws ClientException {
	   // recherche une question sans que le parametre sur la legislation courante
	   // soit defini
	   try {
		   rs.searchQuestionBySourceNumero(session,  "555");
		   fail("legislation parameter incorrect : should raise exception");
	   } catch (ClientException e){
		   // expected exception
		   LOG.info(e.getMessage());
		   assertTrue(e.getMessage().contains("législature courante n'est pas paramétrée correctement"));		   
	   }
	   
	   // create parameter for legislation
	   final Long legislature = 12L;
	   ParameterTestHelper.changeOrCreateParammeter(session, ReponsesParametreConstant.LEGISLATURE_COURANTE, Long.toString(legislature));
	   session.save();
	   
  	   // recherche une question avec que le parametre sur la legislation courante
	   // defini
	   try {
		   rs.searchQuestionBySourceNumero(session,  "555");
		   fail("ne devrait pas trouvé de question : should raise exception");
	   } catch (ClientException e){
		   // expected exception
		   LOG.info(e.getMessage());
		   assertTrue(e.getMessage().startsWith("Aucune question trouvée"));		   
	   }
	   
	   
	   // create question for test
	   DocumentModel doc;
	   Question question;	   
	   doc = createDocumentModel(session,  "q-555-1", DossierConstants.QUESTION_DOCUMENT_TYPE, "/");
	   question = doc.getAdapter(Question.class);
	   question.setOrigineQuestion("AN");
	   question.setNumeroQuestion(555L);
	   question.setTypeQuestion("QE");
	   question.setLegislatureQuestion(legislature);
	   doc = session.saveDocument(doc);
	   final String question555Id = doc.getId();
	   
	   doc = createDocumentModel(session,  "q-555-2", DossierConstants.QUESTION_DOCUMENT_TYPE, "/");
	   question = doc.getAdapter(Question.class);
	   question.setOrigineQuestion("AN");
	   question.setNumeroQuestion(555L);
	   question.setTypeQuestion("QE");
	   question.setLegislatureQuestion(2L);
	   doc = session.saveDocument(doc);
	   
	   doc = createDocumentModel(session,  "q-666", DossierConstants.QUESTION_DOCUMENT_TYPE, "/");
	   question = doc.getAdapter(Question.class);
	   question.setOrigineQuestion("AN");
	   question.setNumeroQuestion(666L);
	   question.setTypeQuestion("QO");
	   question.setLegislatureQuestion(legislature);
	   doc = session.saveDocument(doc);
	   
	   doc = createDocumentModel(session,  "q-777-1", DossierConstants.QUESTION_DOCUMENT_TYPE, "/");
	   question = doc.getAdapter(Question.class);
	   question.setOrigineQuestion("AN");
	   question.setNumeroQuestion(777L);
	   question.setTypeQuestion("QE");
	   question.setLegislatureQuestion(legislature);
	   doc = session.saveDocument(doc);
	   
	   doc = createDocumentModel(session,  "q-777-2", DossierConstants.QUESTION_DOCUMENT_TYPE, "/");
	   question = doc.getAdapter(Question.class);
	   question.setOrigineQuestion("AN");
	   question.setNumeroQuestion(777L);
	   question.setTypeQuestion("QE");
	   question.setLegislatureQuestion(legislature);
	   doc = session.saveDocument(doc);
	   
	   session.save();
	   
	   Question q = rs.searchQuestionBySourceNumero(session,  "AN 555");
	   assertEquals(question555Id, q.getDocument().getId());
	   
	   // recherche d'une question : le numéro existe mais n'est pas une QE
	   try {
		   rs.searchQuestionBySourceNumero(session,  "AN 666");
		   fail("ne devrait pas trouvé de question : should raise exception");
	   } catch (ClientException e){
		   // expected exception
		   LOG.info(e.getMessage());
		   assertTrue(e.getMessage().startsWith("Aucune question trouvée"));		   
	   }
	   
	// recherche d'une question : plusieurs correspondance
	   try {
		   rs.searchQuestionBySourceNumero(session,  "AN 777");
		   fail("devrait trouvé plus d'une question : should raise exception");
	   } catch (ClientException e){
		   // expected exception
		   LOG.info(e.getMessage());
		   assertTrue(e.getMessage().startsWith("Plusieurs questions trouvées"));		   
	   }
	   
   }
   
}
