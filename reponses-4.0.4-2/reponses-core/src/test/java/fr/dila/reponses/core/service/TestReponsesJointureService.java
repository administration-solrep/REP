package fr.dila.reponses.core.service;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.reponses.core.recherche.RechercheFeature;
import fr.dila.reponses.core.recherche.query.ReponsesUFNXQLQueryAssembler;
import fr.dila.st.api.recherche.QueryAssembler;
import fr.dila.st.api.service.JointureService;
import fr.dila.st.core.jointure.CorrespondenceDescriptor;
import fr.dila.st.core.query.ufnxql.UFNXQLQueryAssembler;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class TestReponsesJointureService {
    private static final String BASE_QUERY = RechercheFeature.BASE_QUERY;

    @Inject
    private JointureService jointureService;

    @Test
    public void testConstructionRequeteVide() {
        String fullQuery = getFullQueryFromJointureService(StringUtils.EMPTY);
        Assert.assertEquals(BASE_QUERY, fullQuery);
    }

    @Test
    public void testConstructionRequeteQuestionOnly() {
        String fullQuery = getFullQueryFromJointureService("q.qu:numeroQuestion = '3'");
        Assert.assertEquals(BASE_QUERY + " WHERE ((q.qu:numeroQuestion = '3'))", fullQuery);
    }

    @Test
    public void testConstructionRequeteReponseOnly() {
        String fullQuery = getFullQueryFromJointureService("r.rep:txtReponse LIKE 'Hello'");
        Assert.assertEquals(
            BASE_QUERY +
            ",Dossier AS d,Reponse AS r WHERE ((r.rep:txtReponse LIKE 'Hello') AND d.dos:idDocumentQuestion = q.ecm:uuid AND d.dos:idDocumentReponse = r.ecm:uuid )",
            fullQuery
        );
    }

    @Test
    public void testConstructionRequeteReponsePlusieurs() {
        String fullQuery = getFullQueryFromJointureService(
            "r.rep:txtReponse LIKE 'Hello' AND r.rep:numReponse = '36' AND r.rep:specialFlag = 'Go'"
        );
        Assert.assertEquals(
            BASE_QUERY +
            ",Dossier AS d,Reponse AS r WHERE ((r.rep:txtReponse LIKE 'Hello' AND r.rep:numReponse = '36' AND r.rep:specialFlag = 'Go') AND d.dos:idDocumentQuestion = q.ecm:uuid AND d.dos:idDocumentReponse = r.ecm:uuid )",
            fullQuery
        );
    }

    @Test
    public void testConstructionRequeteDossierQuestion() {
        String fullQuery = getFullQueryFromJointureService(
            "q.qu:numeroQuestion = 2 AND d.dos:" +
            DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT +
            " = '50000507'"
        );
        Assert.assertEquals(
            BASE_QUERY +
            ",Dossier AS d WHERE ((q.qu:numeroQuestion = 2 AND d.dos:" +
            DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT +
            " = '50000507') AND d.dos:idDocumentQuestion = q.ecm:uuid )",
            fullQuery
        );
    }

    @Test
    public void testConstructionRequeteEtape() {
        String fullQuery = getFullQueryFromJointureService("etape.rtsk:type = '1' AND etape.rtsk:status = '2'");
        Assert.assertEquals(
            BASE_QUERY +
            ",Dossier AS d,DossierLink AS dl,RouteStep AS etape " +
            "WHERE ((etape.rtsk:type = '1' AND etape.rtsk:status = '2') " +
            "AND d.dos:idDocumentQuestion = q.ecm:uuid AND d.ecm:uuid = dl.cslk:caseDocumentId AND dl.drl:routingTaskId = etape.ecm:uuid)",
            fullQuery
        );
    }

    /**
     * Test de la nouvelle manière de récupérer les étapes
     */
    @Test
    public void testConstructionRequeteEtape2() {
        String fullQuery = getFullQueryFromJointureService("e2.rtsk:type = '1' AND e2.rtsk:status = '2'");
        Assert.assertEquals(
            BASE_QUERY +
            ",Dossier AS d, FeuilleRoute AS f,RouteStep AS e2 WHERE ((e2.rtsk:type = '1' AND e2.rtsk:status = '2') " +
            "AND d.dos:idDocumentQuestion = q.ecm:uuid AND d.dos:lastDocumentRoute = f.ecm:uuid AND ufnxql_ministere:(d.dos:ministereAttributaireCourant) AND e2.rtsk:documentRouteId = f.ecm:uuid)",
            fullQuery
        );
    }

    @Test
    public void testConstructionRequeteReponseEtDossier() {
        String whereClause =
            "r.rep.txtReponse LIKE 'Hello' AND d.dos:" +
            DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT +
            " = '50000507'";
        String fullQuery = getFullQueryFromJointureService(whereClause);
        Assert.assertEquals(
            BASE_QUERY +
            ",Dossier AS d,Reponse AS r WHERE ((r.rep.txtReponse LIKE 'Hello' AND d.dos:" +
            DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT +
            " = '50000507') AND d.dos:idDocumentQuestion = q.ecm:uuid AND d.dos:idDocumentReponse = r.ecm:uuid )",
            fullQuery
        );
    }

    @Test
    public void testConstructionRequeteFull() {
        String whereClause =
            "q.qu:numeQuestion = 2 AND r.rep.txtReponse LIKE 'Hello' AND d.dos:" +
            DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT +
            " = '50000507' AND etape.rtsk:type = '1' AND etape.rtsk:status = '2'";
        String fullQuery = getFullQueryFromJointureService(whereClause);
        Assert.assertEquals(
            BASE_QUERY +
            ",Dossier AS d,Reponse AS r,DossierLink AS dl,RouteStep AS etape WHERE ((q.qu:numeQuestion = 2 AND r.rep.txtReponse LIKE 'Hello' AND d.dos:" +
            DossierConstants.DOSSIER_ID_MINISTERE_ATTRIBUTAIRE_COURANT +
            " = '50000507' AND etape.rtsk:type = '1' AND etape.rtsk:status = '2') AND d.dos:idDocumentQuestion = q.ecm:uuid AND d.dos:idDocumentReponse = r.ecm:uuid AND d.ecm:uuid = dl.cslk:caseDocumentId AND dl.drl:routingTaskId = etape.ecm:uuid)",
            fullQuery
        );
    }

    @Test
    public void testFulltextQuestion() {
        String fullQuery = getFullQueryFromJointureService("q.ecm:fulltext_txtQuestion = 'chateau'");
        Assert.assertEquals(BASE_QUERY + " WHERE ((q.ecm:fulltext_txtQuestion = 'chateau'))", fullQuery);
    }

    @Test
    public void testFulltextQuestionEtReponse() {
        String fullQuery = getFullQueryFromJointureService(
            "q.ecm:fulltext_txtQuestion = 'chateau' OR r.ecm:fulltext_txtReponses = 'chateau'"
        );
        Assert.assertEquals(
            BASE_QUERY +
            ",Dossier AS d,Reponse AS r WHERE ((q.ecm:fulltext_txtQuestion = 'chateau' OR r.ecm:fulltext_txtReponses = 'chateau') AND d.dos:idDocumentQuestion = q.ecm:uuid AND d.dos:idDocumentReponse = r.ecm:uuid )",
            fullQuery
        );
    }

    @Test
    public void testRequeteBug0032010() {
        String where =
            "q.ecm:fulltext_idQuestion = \"12\" AND q.ixa:SE_theme IN (\"27\") AND q.ecm:fulltext_txtQuestion LIKE \"DATAR\" OR q.ixa:AN_rubrique IN (\"aménagement du territoire\")";
        String fullQuery = getFullQueryFromJointureService(where);
        Assert.assertEquals(
            BASE_QUERY +
            " WHERE ((q.ecm:fulltext_idQuestion = \"12\" AND q.ixa:SE_theme IN (\"27\") AND q.ecm:fulltext_txtQuestion LIKE \"DATAR\" OR q.ixa:AN_rubrique IN (\"aménagement du territoire\")))",
            fullQuery
        );
    }

    @Test
    public void testImplementationTransformQuery() {
        String whereClause = "(q.ecm:fulltext_txtQuestion = 'chateau ')";
        ReponsesUFNXQLQueryAssembler assembler = (ReponsesUFNXQLQueryAssembler) jointureService.getDefaultQueryAssembler();
        assembler.setWhereClause(whereClause);
        List<CorrespondenceDescriptor> new_correspondences = new ArrayList<>();
        CorrespondenceDescriptor just_one_correspondence = new CorrespondenceDescriptor();
        just_one_correspondence.setDocument(DossierConstants.QUESTION_DOCUMENT_TYPE);
        just_one_correspondence.setEmplacement("BEFORE_WHERE");
        just_one_correspondence.setDocPrefix("q.");
        just_one_correspondence.setQueryPart(BASE_QUERY);
        new_correspondences.add(just_one_correspondence);
        Assert.assertEquals(
            BASE_QUERY,
            UFNXQLQueryAssembler.Emplacement.BEFORE_WHERE.extractQueryPart(new_correspondences)
        );
        Assert.assertEquals(1, UFNXQLQueryAssembler.Emplacement.BEFORE_WHERE.filter(new_correspondences).size());
        Assert.assertSame(BASE_QUERY, UFNXQLQueryAssembler.Emplacement.BEFORE_WHERE.filter(new_correspondences).get(0));
    }

    private String getFullQueryFromJointureService(String whereClause) {
        QueryAssembler assembler = jointureService.getDefaultQueryAssembler();
        assembler.setWhereClause(whereClause);
        return assembler.getFullQuery();
    }
}
