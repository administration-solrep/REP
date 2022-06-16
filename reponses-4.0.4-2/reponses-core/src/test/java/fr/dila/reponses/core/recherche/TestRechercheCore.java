package fr.dila.reponses.core.recherche;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.service.RechercheService;
import java.util.List;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 *
 * @author jgomez
 *
 */
@RunWith(FeaturesRunner.class)
@Features(RechercheFeature.class)
public class TestRechercheCore {
    public static Boolean RUN_ALL_TEST = false;

    @Inject
    private CoreFeature coreFeature;

    @Inject
    private RechercheFeature rechercheFeature;

    @Inject
    private RechercheService rs;

    @Test
    public void testEmptyRequete() throws Exception {
        rechercheFeature.runTest(
            "requete",
            (session, requete) -> {
                requete.init();
                requete.doBeforeQuery();
                List<DocumentModel> docs = rs.query(session, requete);
                Assert.assertEquals(RechercheFeature.BASE_QUERY, rs.getFullQuery(session, requete));
                Assert.assertNotNull(docs);
                Assert.assertEquals(1, docs.size());
                DocumentModel docRetour = docs.get(0);
                String groupePol = (String) docRetour.getProperty("qu:groupePolitique").getValue();
                Assert.assertEquals("UMP", groupePol);
                docs = rs.query(session, requete);
                Assert.assertNotNull(docs);
                Assert.assertEquals(1, docs.size());
            }
        );
    }

    @Test
    public void testFixture() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            Dossier dossier1 = rechercheFeature.getFixture().getDossier1(session);
            Reponse reponse1 = rechercheFeature.getFixture().getReponse1(session);
            Assert.assertEquals(new Long(4), dossier1.getNumeroQuestion());
            Assert.assertEquals(Boolean.TRUE, reponse1.isPublished());
            Assert.assertEquals(Boolean.TRUE, dossier1.getReponse(session).isPublished());
            Assert.assertEquals(Boolean.TRUE, dossier1.getQuestion(session).hasReponseInitiee());
        }
    }

    @Test
    public void testQuery() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModelList docs = session.query("SELECT * FROM Document WHERE ecm:primaryType IN ('Question')");
            Assert.assertNotNull(docs);
            Assert.assertEquals(1, docs.size());
            docs = session.query("SELECT * FROM Question");
            Assert.assertNotNull(docs);
            Assert.assertEquals(1, docs.size());
            Question dos = docs.get(0).getAdapter(Question.class);
            Assert.assertEquals(4, dos.getNumeroQuestion().intValue());
            Assert.assertEquals(DossierConstants.QUESTION_DOCUMENT_TYPE, docs.get(0).getType());
            docs = session.query(String.format("SELECT * FROM Question WHERE qu:numeroQuestion = '%s'", "4"));
            Assert.assertNotNull(docs);
            Assert.assertEquals(1, docs.size());
            docs = session.query(String.format("SELECT * FROM Question WHERE qu:numeroQuestion <> '%s'", "4"));
            Assert.assertNotNull(docs);
            Assert.assertEquals(0, docs.size());
        }
    }
}
