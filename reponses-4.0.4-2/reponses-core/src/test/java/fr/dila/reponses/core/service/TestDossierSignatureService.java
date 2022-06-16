package fr.dila.reponses.core.service;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.api.service.DossierSignatureService;
import fr.dila.reponses.core.ReponseFeature;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * @see DossierSignatureService
 * @author tlombard
 */
@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class TestDossierSignatureService {
    private static final Log log = LogFactory.getLog(TestDossierSignatureService.class);

    @Inject
    private CoreFeature coreFeature;

    @Inject
    private DossierDistributionService dossierDistributionService;

    @Inject
    private DossierSignatureService dossierSignatureService;

    private DocumentRef question1Ref;
    private DocumentRef question2Ref;

    private DocumentRef reponse1Ref;
    private DocumentRef reponse2Ref;

    private DocumentRef dossier1Ref;
    private DocumentRef dossier2Ref;

    private static final Long NUM_QUESTION_1 = 15523L;
    private static final Long NUM_QUESTION_2 = 1234L;
    private static final String TYPE_QUESTION_1 = VocabularyConstants.QUESTION_TYPE_QE;
    private static final String TYPE_QUESTION_2 = VocabularyConstants.QUESTION_TYPE_QOAD;
    private static final Long LEGISLATURE_1 = 15L;
    private static final Long LEGISLATURE_2 = 16L;

    @Before
    public void setUp() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            log.debug("init data 1");

            DocumentModel questionDocumentModel = ReponseFeature.createDocument(
                session,
                DossierConstants.QUESTION_DOCUMENT_TYPE,
                "newQuestionTest"
            );
            DocumentModel reponseDocumentModel = ReponseFeature.createDocument(
                session,
                DossierConstants.REPONSE_DOCUMENT_TYPE,
                "ReponseSly"
            );
            reponse1Ref = reponseDocumentModel.getRef();
            question1Ref = questionDocumentModel.getRef();
            DocumentModel dossierDocumentModel = ReponseFeature.createDocument(
                session,
                DossierConstants.DOSSIER_DOCUMENT_TYPE,
                "newDossierTest"
            );
            ReponseFeature.createDocument(session, "FeuilleRouteModelFolder", "fdrRootFolder");
            dossier1Ref = dossierDocumentModel.getRef();

            log.debug("init data 2");

            DocumentModel questionDocumentModel2 = ReponseFeature.createDocument(
                session,
                DossierConstants.QUESTION_DOCUMENT_TYPE,
                "newQuestionTest"
            );
            DocumentModel reponseDocumentModel2 = ReponseFeature.createDocument(
                session,
                DossierConstants.REPONSE_DOCUMENT_TYPE,
                "ReponseSly"
            );
            reponse2Ref = reponseDocumentModel2.getRef();
            question2Ref = questionDocumentModel2.getRef();
            DocumentModel dossierDocumentModel2 = ReponseFeature.createDocument(
                session,
                DossierConstants.DOSSIER_DOCUMENT_TYPE,
                "newDossierTest"
            );
            ReponseFeature.createDocument(session, "FeuilleRouteModelFolder", "fdrRootFolder");
            dossier2Ref = dossierDocumentModel2.getRef();
            session.save();
        }
    }

    private Dossier generateDossierSenat(CoreSession session) {
        String texteQuestion = "quelle heure est il ?";
        Calendar date = GregorianCalendar.getInstance();
        String nomAuteur = "Valjean";
        String prenomAuteur = "Jean";

        Question question1 = getQuestion1(session);
        question1.setNumeroQuestion(NUM_QUESTION_1);
        question1.setTypeQuestion(TYPE_QUESTION_1);
        question1.setTexteQuestion(texteQuestion);
        question1.setDateReceptionQuestion(date);
        question1.setNomAuteur(nomAuteur);
        question1.setPrenomAuteur(prenomAuteur);
        question1.setLegislatureQuestion(LEGISLATURE_1);
        question1.setOrigineQuestion(DossierConstants.ORIGINE_QUESTION_SENAT);

        Reponse reponse1 = getReponse1(session);
        reponse1.setTexteReponse("HELLOOO");
        reponse1.setIdAuteurReponse("Sanh Ly");

        Dossier dossier = getDossier1(session);
        dossier =
            dossierDistributionService.createDossier(
                session,
                dossier,
                question1,
                reponse1,
                VocabularyConstants.ETAT_QUESTION_EN_COURS
            );

        return dossier;
    }

    private Dossier generateDossierAN(CoreSession session) {
        String texteQuestion = "Pourquoi tant de haine ?";
        Calendar date = GregorianCalendar.getInstance();
        String nomAuteur = "David";
        String prenomAuteur = "Jonathan";

        Question question2 = getQuestion2(session);
        question2.setNumeroQuestion(NUM_QUESTION_2);
        question2.setTypeQuestion(TYPE_QUESTION_2);
        question2.setTexteQuestion(texteQuestion);
        question2.setDateReceptionQuestion(date);
        question2.setNomAuteur(nomAuteur);
        question2.setPrenomAuteur(prenomAuteur);
        question2.setLegislatureQuestion(LEGISLATURE_2);
        question2.setOrigineQuestion(DossierConstants.ORIGINE_QUESTION_AN);

        Reponse reponse2 = getReponse2(session);
        reponse2.setTexteReponse("HELLOOO");
        reponse2.setIdAuteurReponse("Sanh Ly");

        Dossier dossier = getDossier2(session);
        dossier =
            dossierDistributionService.createDossier(
                session,
                dossier,
                question2,
                reponse2,
                VocabularyConstants.ETAT_QUESTION_EN_COURS
            );

        return dossier;
    }

    @Test
    public void testGenerateKey() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            String key = dossierSignatureService.generateKey(generateDossierSenat(session), session);

            Assert.assertNotNull(key);
            Assert.assertEquals(
                LEGISLATURE_1 +
                "_" +
                DossierConstants.ORIGINE_QUESTION_SENAT +
                "_" +
                VocabularyConstants.QUESTION_TYPE_QE +
                "_" +
                NUM_QUESTION_1,
                key
            );

            key = dossierSignatureService.generateKey(generateDossierAN(session), session);

            Assert.assertNotNull(key);
            Assert.assertEquals(
                LEGISLATURE_2 +
                "_" +
                DossierConstants.ORIGINE_QUESTION_AN +
                "_" +
                VocabularyConstants.QUESTION_TYPE_QOAD +
                "_" +
                NUM_QUESTION_2,
                key
            );
        }
    }

    private Question getQuestion1(CoreSession session) {
        return session.getDocument(question1Ref).getAdapter(Question.class);
    }

    private Question getQuestion2(CoreSession session) {
        return session.getDocument(question2Ref).getAdapter(Question.class);
    }

    private Reponse getReponse1(CoreSession session) {
        return session.getDocument(reponse1Ref).getAdapter(Reponse.class);
    }

    private Reponse getReponse2(CoreSession session) {
        return session.getDocument(reponse2Ref).getAdapter(Reponse.class);
    }

    private Dossier getDossier1(CoreSession session) {
        return session.getDocument(dossier1Ref).getAdapter(Dossier.class);
    }

    private Dossier getDossier2(CoreSession session) {
        return session.getDocument(dossier2Ref).getAdapter(Dossier.class);
    }
}
