package fr.dila.reponses.core.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;
import fr.dila.reponses.core.recherche.RechercheTestCase;
import fr.dila.reponses.core.recherche.query.ReponsesUFNXQLQueryAssembler;
import fr.dila.st.api.recherche.QueryAssembler;
import fr.dila.st.api.service.JointureService;
import fr.dila.st.core.jointure.CorrespondenceDescriptor;
import fr.dila.st.core.query.ufnxql.UFNXQLQueryAssembler;
import fr.dila.st.core.service.STServiceLocator;

public class TestReponsesJointureService extends ReponsesRepositoryTestCase {

	private static final String BASE_QUERY = RechercheTestCase.BASE_QUERY;
    
    private JointureService jointureService;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        openSession();
        jointureService = STServiceLocator.getJointureService();
        assertNotNull(jointureService);
    }
    
    @Override
    public void tearDown() throws Exception {
    	closeSession();
    	super.tearDown();
    }

    public void testConstructionRequeteVide() {
        String fullQuery = getFullQueryFromJointureService(StringUtils.EMPTY);
        assertEquals(BASE_QUERY, fullQuery);
    }


    public void testConstructionRequeteQuestionOnly(){
        String fullQuery = getFullQueryFromJointureService("q.qu:numeroQuestion = '3'");
        assertEquals(BASE_QUERY + " WHERE ((q.qu:numeroQuestion = '3'))", fullQuery);
    }

    public void testConstructionRequeteReponseOnly() {
        String fullQuery = getFullQueryFromJointureService("r.rep:txtReponse LIKE 'Hello'");
        assertEquals(BASE_QUERY + ",Dossier AS d,Reponse AS r WHERE ((r.rep:txtReponse LIKE 'Hello') AND d.dos:idDocumentQuestion = q.ecm:uuid AND d.dos:idDocumentReponse = r.ecm:uuid )", fullQuery);
    }
    
    public void testConstructionRequeteReponsePlusieurs() {
        String fullQuery = getFullQueryFromJointureService("r.rep:txtReponse LIKE 'Hello' AND r.rep:numReponse = '36' AND r.rep:specialFlag = 'Go'");
        assertEquals(BASE_QUERY + ",Dossier AS d,Reponse AS r WHERE ((r.rep:txtReponse LIKE 'Hello' AND r.rep:numReponse = '36' AND r.rep:specialFlag = 'Go') AND d.dos:idDocumentQuestion = q.ecm:uuid AND d.dos:idDocumentReponse = r.ecm:uuid )", fullQuery);
    }
    
    public void testConstructionRequeteDossierQuestion() {
        String fullQuery = getFullQueryFromJointureService("q.qu:numeroQuestion = 2 AND d.dos:" + DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT + " = '50000507'");
        assertEquals(BASE_QUERY + ",Dossier AS d WHERE ((q.qu:numeroQuestion = 2 AND d.dos:" + DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT + " = '50000507') AND d.dos:idDocumentQuestion = q.ecm:uuid )", fullQuery);
    }
    
    public void testConstructionRequeteEtape() {
        String fullQuery = getFullQueryFromJointureService("etape.rtsk:type = '1' AND etape.rtsk:status = '2'");
        assertEquals(BASE_QUERY + ",Dossier AS d,DossierLink AS dl,RouteStep AS etape " +
        		"WHERE ((etape.rtsk:type = '1' AND etape.rtsk:status = '2') " +
        		"AND d.dos:idDocumentQuestion = q.ecm:uuid AND d.ecm:uuid = dl.cslk:caseDocumentId AND dl.drl:routingTaskId = etape.ecm:uuid)", fullQuery);
    }
    
    /**
     * Test de la nouvelle manière de récupérer les étapes
     * @throws ClientException
     */
    public void testConstructionRequeteEtape2() throws ClientException {
        String fullQuery = getFullQueryFromJointureService("e2.rtsk:type = '1' AND e2.rtsk:status = '2'");
        assertEquals(BASE_QUERY + ",Dossier AS d, FeuilleRoute AS f,RouteStep AS e2 WHERE ((e2.rtsk:type = '1' AND e2.rtsk:status = '2') " +
        		"AND d.dos:idDocumentQuestion = q.ecm:uuid AND d.dos:lastDocumentRoute = f.ecm:uuid AND ufnxql_ministere:(d.dos:ministereAttributaireCourant) AND e2.rtsk:documentRouteId = f.ecm:uuid)", fullQuery);
    }
    
    public void testConstructionRequeteReponseEtDossier() {
        String whereClause = "r.rep.txtReponse LIKE 'Hello' AND d.dos:" + DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT + " = '50000507'";
        String fullQuery = getFullQueryFromJointureService(whereClause);
        assertEquals(BASE_QUERY + ",Dossier AS d,Reponse AS r WHERE ((r.rep.txtReponse LIKE 'Hello' AND d.dos:" + DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT + " = '50000507') AND d.dos:idDocumentQuestion = q.ecm:uuid AND d.dos:idDocumentReponse = r.ecm:uuid )", fullQuery);
    }
    
    public void testConstructionRequeteFull() {
        String whereClause = "q.qu:numeQuestion = 2 AND r.rep.txtReponse LIKE 'Hello' AND d.dos:" + DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT + " = '50000507' AND etape.rtsk:type = '1' AND etape.rtsk:status = '2'";
        String fullQuery = getFullQueryFromJointureService(whereClause);
        assertEquals(BASE_QUERY + ",Dossier AS d,Reponse AS r,DossierLink AS dl,RouteStep AS etape WHERE ((q.qu:numeQuestion = 2 AND r.rep.txtReponse LIKE 'Hello' AND d.dos:" + DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT + " = '50000507' AND etape.rtsk:type = '1' AND etape.rtsk:status = '2') AND d.dos:idDocumentQuestion = q.ecm:uuid AND d.dos:idDocumentReponse = r.ecm:uuid AND d.ecm:uuid = dl.cslk:caseDocumentId AND dl.drl:routingTaskId = etape.ecm:uuid)", fullQuery);
    }

    public void testFulltextQuestion() {
        String fullQuery = getFullQueryFromJointureService("q.ecm:fulltext_txtQuestion = 'chateau'");
        assertEquals(BASE_QUERY + " WHERE ((q.ecm:fulltext_txtQuestion = 'chateau'))", fullQuery);
    }
    
    public void testFulltextQuestionEtReponse() {
        String fullQuery = getFullQueryFromJointureService("q.ecm:fulltext_txtQuestion = 'chateau' OR r.ecm:fulltext_txtReponses = 'chateau'");
        assertEquals(BASE_QUERY + ",Dossier AS d,Reponse AS r WHERE ((q.ecm:fulltext_txtQuestion = 'chateau' OR r.ecm:fulltext_txtReponses = 'chateau') AND d.dos:idDocumentQuestion = q.ecm:uuid AND d.dos:idDocumentReponse = r.ecm:uuid )", fullQuery);
    }
    
    public void testRequeteBug0032010() {
        String where = "q.ecm:fulltext_idQuestion = \"12\" AND q.ixa:SE_theme IN (\"27\") AND q.ecm:fulltext_txtQuestion LIKE \"DATAR\" OR q.ixa:AN_rubrique IN (\"aménagement du territoire\")";
        String fullQuery = getFullQueryFromJointureService(where);
        assertEquals(BASE_QUERY + " WHERE ((q.ecm:fulltext_idQuestion = \"12\" AND q.ixa:SE_theme IN (\"27\") AND q.ecm:fulltext_txtQuestion LIKE \"DATAR\" OR q.ixa:AN_rubrique IN (\"aménagement du territoire\")))", fullQuery);
    }

    public void testImplementationTransformQuery() {
        String whereClause = "(q.ecm:fulltext_txtQuestion = 'chateau ')";
        ReponsesUFNXQLQueryAssembler assembler = (ReponsesUFNXQLQueryAssembler) jointureService.getDefaultQueryAssembler();
        assembler.setWhereClause(whereClause);
        List<CorrespondenceDescriptor> new_correspondences = new ArrayList<CorrespondenceDescriptor>();
        CorrespondenceDescriptor just_one_correspondence = new CorrespondenceDescriptor();
        just_one_correspondence.setDocument(DossierConstants.QUESTION_DOCUMENT_TYPE);
        just_one_correspondence.setEmplacement("BEFORE_WHERE");
        just_one_correspondence.setDocPrefix("q.");
        just_one_correspondence.setQueryPart(BASE_QUERY);
        new_correspondences.add(just_one_correspondence);
        assertEquals(BASE_QUERY, UFNXQLQueryAssembler.Emplacement.BEFORE_WHERE.extractQueryPart(new_correspondences));
        assertEquals(1,UFNXQLQueryAssembler.Emplacement.BEFORE_WHERE.filter(new_correspondences).size());
        assertSame(BASE_QUERY, UFNXQLQueryAssembler.Emplacement.BEFORE_WHERE.filter(new_correspondences).get(0));
    }

    private String getFullQueryFromJointureService(String whereClause) {
        QueryAssembler assembler = jointureService.getDefaultQueryAssembler();
        assembler.setWhereClause(whereClause);
        return assembler.getFullQuery();
    }
}
