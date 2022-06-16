package fr.dila.reponses.core.cases;

import fr.dila.cm.cases.CaseConstants;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesFondDeDossierConstants;
import fr.dila.reponses.core.ReponseFeature;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * @author arolin
 */
@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class TestDossier {
    private static final Log LOG = LogFactory.getLog(TestDossier.class);

    @Inject
    private CoreFeature coreFeature;

    private DocumentRef envelopeDossierRef;

    private DocumentRef questionRef;

    private DocumentRef reponseRef;

    @Before
    public void setUp() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel docModel = ReponseFeature.createDocument(
                session,
                DossierConstants.DOSSIER_DOCUMENT_TYPE,
                "newDossierTest"
            );
            envelopeDossierRef = docModel.getRef();
            docModel = ReponseFeature.createDocument(session, "CaseItem", "i1");
            docModel =
                ReponseFeature.createDocument(
                    session,
                    ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_DOCUMENT_TYPE,
                    "i2"
                );
            DocumentModel question = ReponseFeature.createDocument(
                session,
                DossierConstants.QUESTION_DOCUMENT_TYPE,
                "question1"
            );
            DocumentModel reponse = ReponseFeature.createDocument(
                session,
                DossierConstants.REPONSE_DOCUMENT_TYPE,
                "reponse1"
            );
            questionRef = question.getRef();
            reponseRef = reponse.getRef();
            session.save();
        }
    }

    @Test
    public void testGetDocument() {
        // on verifie que le case est bien créé, est de type Question, possède
        // une facet "Case" et possède le schéma "Question"
        LOG.info("begin : test dossier type ");
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel dossierModel = session.getDocument(envelopeDossierRef);
            Assert.assertNotNull(dossierModel);
            Assert.assertEquals(DossierConstants.DOSSIER_DOCUMENT_TYPE, dossierModel.getType());

            Assert.assertTrue(dossierModel.hasSchema(CaseConstants.CASE_SCHEMA));
            Assert.assertTrue(dossierModel.hasSchema(DossierConstants.DOSSIER_SCHEMA));
        }
    }

    @Test
    public void testDossierProperties() {
        LOG.info("begin : test dossier schema properties ");
        Long numeroQuestion = 4L;
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel dossierModel = session.getDocument(envelopeDossierRef);
            Dossier envelopeDossier = dossierModel.getAdapter(Dossier.class);
            envelopeDossier.setNumeroQuestion(numeroQuestion);
            Assert.assertEquals(envelopeDossier.getNumeroQuestion(), numeroQuestion);
        }
    }

    @Test
    public void testDossierQuestionReponseMethods() {
        final String texteQuestion = "quelle heure est il ?";
        final String texteReponse = "il est 12H00.";
        String questionId = null;
        LOG.info("begin : test question et reponse dans le dossier");
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel dossierModel = session.getDocument(envelopeDossierRef);
            Dossier envelopeDossier = dossierModel.getAdapter(Dossier.class);

            // test Question
            DocumentModel questionModel = session.getDocument(questionRef);
            Assert.assertEquals(DossierConstants.QUESTION_DOCUMENT_TYPE, questionModel.getType());
            Assert.assertTrue(questionModel.hasFacet("Versionable"));

            Question qq = questionModel.getAdapter(Question.class);
            qq.setTexteQuestion(texteQuestion);

            questionModel = session.saveDocument(questionModel);

            Question question = questionModel.getAdapter(Question.class);
            questionId = question.getId();
            envelopeDossier.setQuestionId(question.getId());
            envelopeDossier.save(session);

            session.save();
        }
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel dossierModel = session.getDocument(envelopeDossierRef);
            Dossier envelopeDossier = dossierModel.getAdapter(Dossier.class);

            Assert.assertEquals(questionId, envelopeDossier.getQuestionId());

            // test getQuestion method
            Question q = envelopeDossier.getQuestion(session);
            Assert.assertNotNull(q);
            Assert.assertEquals(texteQuestion, q.getTexteQuestion());

            // test Question
            DocumentModel reponse = session.getDocument(reponseRef);
            Assert.assertEquals("Reponse", reponse.getType());
            Assert.assertTrue(reponse.hasFacet("Versionable"));

            reponse.setProperty("note", "note", texteReponse);
            session.saveDocument(reponse);

            envelopeDossier.setReponseId(reponse.getId());
            envelopeDossier.save(session);

            session.save();
        }
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel dossierModel = session.getDocument(envelopeDossierRef);
            Dossier envelopeDossier = dossierModel.getAdapter(Dossier.class);

            // test getQuestion method
            Reponse r = envelopeDossier.getReponse(session);
            Assert.assertEquals(texteReponse, r.getTexteReponse());
        }
    }
}
