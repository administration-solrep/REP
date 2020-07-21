package fr.dila.reponses.core.cases;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.cm.cases.CaseConstants;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesFondDeDossierConstants;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;

/**
 * @author arolin
 */
public class TestDossier extends ReponsesRepositoryTestCase {

    private static final Log LOG = LogFactory.getLog(TestDossier.class);

    private Dossier envelopeDossier;

    private DocumentModel question;

    private DocumentModel reponse;

    @Override
    public void setUp() throws Exception {

        super.setUp();

        openSession();
        DocumentModel docModel = createDocument(DossierConstants.DOSSIER_DOCUMENT_TYPE, "newDossierTest");
        envelopeDossier = docModel.getAdapter(Dossier.class);
        docModel = createDocument("CaseItem", "i1");
        docModel = createDocument(ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_DOCUMENT_TYPE, "i2");
        question = createDocument(DossierConstants.QUESTION_DOCUMENT_TYPE, "question1");
        reponse = createDocument(DossierConstants.REPONSE_DOCUMENT_TYPE, "reponse1");
        session.save();
        closeSession();
    }

    public void testGetDocument() {

        // on verifie que le case est bien créé, est de type Question, possède
        // une facet "Case" et possède le schéma "Question"
        LOG.info("begin : test dossier type ");

        DocumentModel dossierModel = envelopeDossier.getDocument();
        assertNotNull(dossierModel);
        assertEquals(DossierConstants.DOSSIER_DOCUMENT_TYPE, dossierModel.getType());

        assertTrue(dossierModel.hasSchema(CaseConstants.CASE_SCHEMA));        
        assertTrue(dossierModel.hasSchema(DossierConstants.DOSSIER_SCHEMA));
    }

    public void testDossierProperties() {
        LOG.info("begin : test dossier schema properties ");
        Long numeroQuestion = 4L;
        
        envelopeDossier.setNumeroQuestion(numeroQuestion);
        assertEquals(envelopeDossier.getNumeroQuestion(), numeroQuestion);
    }

    public void testDossierQuestionReponseMethods() throws ClientException {

        LOG.info("begin : test question et reponse dans le dossier");
        openSession();
        // test Question
        DocumentModel questionModel = question;
        assertEquals(DossierConstants.QUESTION_DOCUMENT_TYPE, questionModel.getType());
        assertTrue(questionModel.hasFacet("Versionable"));

        String texteQuestion = "quelle heure est il ?";
        Question qq = question.getAdapter(Question.class);
        qq.setTexteQuestion(texteQuestion);
        
        question = session.saveDocument(question);
        
        String questionId = question.getId();
        envelopeDossier.setQuestionId(question.getId());
        envelopeDossier.save(session);
        
        session.save();
        
        closeSession();
        openSession();

        assertEquals(questionId, envelopeDossier.getQuestionId());
        
        // test getQuestion method
        Question q = envelopeDossier.getQuestion(session);
        assertNotNull(q);
        assertEquals(texteQuestion, q.getTexteQuestion());

        // test Question
        assertEquals("Reponse", reponse.getType());
        assertTrue(reponse.hasFacet("Versionable"));

        String texteReponse = "il est 12H00.";
        reponse.setProperty("note", "note", texteReponse);
        session.saveDocument(reponse);
        
        envelopeDossier.setReponseId(reponse.getId());
        envelopeDossier.save(session);
        
        session.save();
        closeSession();
        openSession();

        // test getQuestion method
        Reponse r = envelopeDossier.getReponse(session);
        assertEquals(texteReponse, r.getTexteReponse());
        
        closeSession();
    }

}