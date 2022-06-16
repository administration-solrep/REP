package fr.dila.reponses.core.service;

import static fr.dila.reponses.core.ReponseFeature.user1;
import static fr.dila.ss.api.constant.SSConstant.ROUTE_STEP_DOCUMENT_TYPE;
import static fr.dila.st.api.constant.STSchemaConstant.COMMENT_SCHEMA;
import static fr.dila.st.core.util.ObjectHelper.requireNonNullElseGet;
import static org.nuxeo.ecm.core.api.CoreInstance.openCoreSession;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.cases.flux.Renouvellement;
import fr.dila.reponses.api.cases.flux.Signalement;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.api.service.ReponseService;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.reponses.core.cases.flux.SignalementImpl;
import fr.dila.reponses.core.mock.MockFeuilleRouteModelService;
import fr.dila.st.core.service.STServiceLocator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.impl.ACPImpl;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.platform.comment.api.CommentableDocument;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * @author arolin
 */
@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
@Deploy("fr.dila.reponses.core.tests:OSGI-INF/service/test-feuille-route-model-framework.xml")
public class TestDossierDistributionService {
    private static final Log log = LogFactory.getLog(TestDossierDistributionService.class);

    @Inject
    private CoreSession session;

    @Inject
    private UserManager um;

    @Inject
    private CoreFeature coreFeature;

    @Inject
    private ReponseService reponseService;

    @Inject
    private DossierDistributionService dossierDistributionService;

    @Inject
    private ReponseFeature reponseFeature;

    private DocumentRef questionRef;
    private DocumentRef reponseRef;
    private DocumentRef dossierRef;

    @Before
    public void setUp() {
        STServiceLocator.getSTParametreService().clearCache();

        ((MockFeuilleRouteModelService) ReponsesServiceLocator.getFeuilleRouteModelService()).clear();
        reponseFeature.setFeuilleRootModelFolderId(null);
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            reponseFeature.createOrGetFeuilleRouteModelFolder(session);
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
            reponseRef = reponseDocumentModel.getRef();
            questionRef = questionDocumentModel.getRef();

            DocumentModel dossierDocumentModel = ReponseFeature.createDocument(
                session,
                DossierConstants.DOSSIER_DOCUMENT_TYPE,
                "newDossierTest"
            );
            dossierRef = dossierDocumentModel.getRef();

            if (ReponsesServiceLocator.getFeuilleRouteModelService().getFeuilleRouteModelFolder(session) == null) {
                ReponseFeature.createDocument(session, "FeuilleRouteModelFolder", "fdrRootFolder");
            }

            session.save();
        }
    }

    @Test
    public void testGetDossierPiecesWithDossier() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // on verifie que les différents objet
            log.info("begin : test dossier type ");

            // check properties
            Long numQuestionLong = 15523L;
            String typeQuestion = VocabularyConstants.QUESTION_TYPE_QE;

            String texteQuestion = "quelle heure est il ?";
            Calendar date = GregorianCalendar.getInstance();
            String nomAuteur = "Valjean";
            String prenomAuteur = "Jean";

            Question question = getQuestion(session);
            question.setNumeroQuestion(numQuestionLong);
            question.setTypeQuestion(typeQuestion);
            question.setTexteQuestion(texteQuestion);
            question.setDateReceptionQuestion(date);
            question.setNomAuteur(nomAuteur);
            question.setPrenomAuteur(prenomAuteur);

            Reponse reponse = getReponse(session);
            reponse.setTexteReponse("HELLOOO");
            reponse.setIdAuteurReponse("Sanh Ly");

            Dossier dossier = getDossier(session);
            dossier =
                dossierDistributionService.createDossier(
                    session,
                    dossier,
                    question,
                    reponse,
                    VocabularyConstants.ETAT_QUESTION_EN_COURS
                );

            // Démarre la feuille de route associée au dossier
            dossierDistributionService.startDefaultRoute(session, dossier);

            Question questionFromDossier = dossier.getQuestion(session);
            Assert.assertNotNull(questionFromDossier);
            Assert.assertNotNull(questionFromDossier.getDocument());
            Assert.assertEquals(questionFromDossier.getDocument().getType(), DossierConstants.QUESTION_DOCUMENT_TYPE);
            Assert.assertNotNull(questionFromDossier.getDocument().getId());

            Assert.assertEquals(typeQuestion, questionFromDossier.getTypeQuestion());
            Assert.assertEquals(numQuestionLong, questionFromDossier.getNumeroQuestion());
            Assert.assertEquals("Valjean Jean", questionFromDossier.getNomCompletAuteur());

            Reponse reponseFromDossier = dossier.getReponse(session);
            Assert.assertNotNull(reponseFromDossier);
            Assert.assertNotNull(reponseFromDossier.getDocument().getId());
        }
    }

    @Test
    public void testSignalementOperations() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // on verifie que les différents objet
            log.info("------------TEST SIGNALEMENT--------------- ");

            // check properties
            Calendar date = GregorianCalendar.getInstance();

            Signalement unSignalement = new SignalementImpl();
            unSignalement.setDateAttendue(date.getTime());
            unSignalement.setDateEffet(date.getTime());

            Assert.assertNotNull(unSignalement.getSignalementMap());
            List<Signalement> signalements = new ArrayList<>();
            signalements.add(unSignalement);

            Question question = getQuestion(session);
            question.setDateReceptionQuestion(date);
            question.setSignalements(signalements);

            Dossier dossier = getDossier(session);
            dossier =
                dossierDistributionService.createDossier(
                    session,
                    dossier,
                    question,
                    null,
                    VocabularyConstants.ETAT_QUESTION_EN_COURS
                );
            question.setTypeQuestion(VocabularyConstants.QUESTION_TYPE_QE);
            Assert.assertNotNull(dossier.getQuestion(session).getSignalements());
            Assert.assertTrue(dossier.getQuestion(session).getSignalements().size() == 1);

            // Démarre la feuille de route associée au dossier
            dossierDistributionService.startDefaultRoute(session, dossier);

            log.info("------------FIN TEST SIGNALEMENT -------------");
        }
    }

    @Test
    public void testRenouvellementOperations() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // on verifie que les différents objet
            log.info("------------TEST RENOUVELLEMENT--------------- ");

            // check properties
            Calendar dateRenouvellement = GregorianCalendar.getInstance();
            Calendar dateReception = GregorianCalendar.getInstance();

            Question question = getQuestion(session);
            question.setDateReceptionQuestion(dateReception);
            question.setEtatQuestion(session, VocabularyConstants.ETAT_QUESTION_RENOUVELEE, dateRenouvellement, "10");

            Dossier dossier = getDossier(session);
            dossier =
                dossierDistributionService.createDossier(
                    session,
                    dossier,
                    question,
                    null,
                    VocabularyConstants.ETAT_QUESTION_EN_COURS
                );

            // Démarre la feuille de route associée au dossier
            dossierDistributionService.startDefaultRoute(session, dossier);

            Assert.assertNotNull(
                dossierDistributionService.getLastRenouvellement(dossier.getQuestion(session).getDocument())
            );
            Assert.assertEquals(1, dossier.getQuestion(session).getRenouvellements().size());
            Renouvellement renouvellement = dossierDistributionService.getLastRenouvellement(
                dossier.getQuestion(session).getDocument()
            );
            Assert.assertEquals(dateRenouvellement.getTime(), renouvellement.getDateEffet());

            log.info("------------FIN TEST RENOUVELLEMENT -------------");
        }
    }

    @Test
    public void testRechercheQuestionFromNumero() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // on verifie que les différents objet
            log.info("------------Recherche de la question par numero de la question --------------- ");
            // check properties
            Long numQuestionLong = 15523L;
            String typeQuestion = "ecrite";
            String texteQuestion = "quelle heure est il ?";
            Calendar date = GregorianCalendar.getInstance();

            Question question = getQuestion(session);
            question.setNumeroQuestion(numQuestionLong);
            question.setTypeQuestion(typeQuestion);
            question.setTexteQuestion(texteQuestion);
            question.setDateReceptionQuestion(date);

            // set numero question to dossier
            Dossier dossierDoc = getDossier(session);
            dossierDoc.setNumeroQuestion(numQuestionLong);

            Dossier dossier = dossierDistributionService.createDossier(
                session,
                dossierDoc,
                question,
                null,
                VocabularyConstants.ETAT_QUESTION_EN_COURS
            );

            Assert.assertNotNull(dossier);
            Assert.assertNotNull(dossier.getNumeroQuestion());
            Assert.assertEquals(dossier.getNumeroQuestion(), numQuestionLong);

            // Démarre la feuille de route associée au dossier
            dossierDistributionService.startDefaultRoute(session, dossier);

            //
            DocumentModelList list = dossierDistributionService.getDossierFrom(session, numQuestionLong.toString());
            Assert.assertNotNull(list);
            System.out.println("------------ Liste de question : " + list);
            System.out.println("------------ nombre de document : " + list.size());
            //
            Assert.assertEquals(list.size(), 1);
            Iterator<DocumentModel> iteratorModel = list.iterator();
            while (iteratorModel.hasNext()) {
                DocumentModel dossierDocmodel = iteratorModel.next();
                DocumentModel reponseModel = reponseService.getReponseFromDossier(session, dossierDocmodel);
                Reponse reponseAdapter = reponseModel.getAdapter(Reponse.class);
                Assert.assertNotNull(reponseAdapter);

                Dossier unDossier = dossierDocmodel.getAdapter(Dossier.class);
                Question laQuestion = unDossier.getQuestion(session);
                Assert.assertEquals(numQuestionLong, laQuestion.getNumeroQuestion());
                Assert.assertEquals(typeQuestion, laQuestion.getTypeQuestion());
                Assert.assertEquals(texteQuestion, laQuestion.getTexteQuestion());
                Assert.assertEquals(date, laQuestion.getDateReceptionQuestion());
            }
        }
    }

    @Test
    public void testIsExistingQuestion() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            Long numeroQuestion = 15523L;
            String typeQuestion = "ecrite";
            String origineQuestion = "AN";
            Long legislatureQuestion = 50L;

            Question question = getQuestion(session);
            question.setNumeroQuestion(numeroQuestion);
            question.setTypeQuestion(typeQuestion);
            question.setOrigineQuestion(origineQuestion);
            question.setLegislatureQuestion(legislatureQuestion);

            Dossier dossier = getDossier(session);
            dossier =
                dossierDistributionService.createDossier(
                    session,
                    dossier,
                    question,
                    null,
                    VocabularyConstants.ETAT_QUESTION_EN_COURS
                );

            Question q = dossier.getQuestion(session);
            Assert.assertEquals(numeroQuestion, q.getNumeroQuestion());
            Assert.assertEquals(typeQuestion, q.getTypeQuestion());
            Assert.assertEquals(origineQuestion, q.getOrigineQuestion());
            Assert.assertEquals(legislatureQuestion, q.getLegislatureQuestion());

            try {
                String id = dossierDistributionService.retrieveDocumentQuestionId(
                    session,
                    numeroQuestion,
                    typeQuestion,
                    origineQuestion,
                    legislatureQuestion
                );
                Assert.assertNotNull(id);
                Assert.assertEquals(q.getDocument().getId(), id);

                Assert.assertTrue(
                    dossierDistributionService.isExistingQuestion(
                        session,
                        numeroQuestion,
                        typeQuestion,
                        origineQuestion,
                        legislatureQuestion
                    )
                );

                Assert.assertFalse(
                    dossierDistributionService.isExistingQuestion(
                        session,
                        numeroQuestion,
                        "NOT EXISTING TYPE",
                        origineQuestion,
                        legislatureQuestion
                    )
                );
            } catch (NuxeoException e) {
                Assert.fail("testIsExistingQuestion");
            }

            try {
                dossierDistributionService.isExistingQuestion(
                    null,
                    numeroQuestion,
                    typeQuestion,
                    origineQuestion,
                    legislatureQuestion
                );
                Assert.fail();
            } catch (IllegalArgumentException e) {
                // exception expected : session null
            }

            try {
                dossierDistributionService.isExistingQuestion(
                    session,
                    numeroQuestion,
                    " ",
                    origineQuestion,
                    legislatureQuestion
                );
                Assert.fail();
            } catch (IllegalArgumentException e) {
                // exception expected : type blank
            }

            try {
                dossierDistributionService.isExistingQuestion(
                    session,
                    numeroQuestion,
                    null,
                    origineQuestion,
                    legislatureQuestion
                );
                Assert.fail();
            } catch (IllegalArgumentException e) {
                // exception expected : type null
            }

            try {
                dossierDistributionService.isExistingQuestion(
                    session,
                    numeroQuestion,
                    typeQuestion,
                    " ",
                    legislatureQuestion
                );
                Assert.fail();
            } catch (IllegalArgumentException e) {
                // exception expected : source blank
            }

            try {
                dossierDistributionService.isExistingQuestion(
                    session,
                    numeroQuestion,
                    typeQuestion,
                    null,
                    legislatureQuestion
                );
                Assert.fail();
            } catch (IllegalArgumentException e) {
                // exception expected : source null
            }
        }
    }

    @Test
    @Deploy("org.nuxeo.ecm.platform.comment.api")
    @Deploy("org.nuxeo.ecm.platform.comment.core")
    @Deploy("org.nuxeo.ecm.platform.comment")
    @Deploy("org.nuxeo.ecm.relations")
    public void testAddCommentToStep() {
        DocumentModel fdr = reponseFeature.createFeuilleRoute(session, "myFdr");
        addPermission(session, fdr.getRef(), user1, "ReadWrite");
        NuxeoPrincipal user = um.getPrincipal(user1);
        try (CloseableCoreSession userSession = openCoreSession(session.getRepositoryName(), user)) {
            DocumentModel routeStep = userSession.createDocumentModel(
                fdr.getPathAsString(),
                "myRouteStep",
                ROUTE_STEP_DOCUMENT_TYPE
            );
            routeStep = userSession.createDocument(routeStep);
            routeStep = userSession.getDocument(routeStep.getRef());

            CommentableDocument commentDoc = routeStep.getAdapter(CommentableDocument.class);
            Assertions.assertThat(commentDoc.getComments()).isEmpty();

            dossierDistributionService.addCommentToStep(userSession, routeStep, "some specific note");
            routeStep = userSession.getDocument(routeStep.getRef());
            List<DocumentModel> comments = routeStep.getAdapter(CommentableDocument.class).getComments();
            Assertions.assertThat(comments).isNotEmpty();
            Assertions
                .assertThat(comments)
                .extracting(
                    comment -> getPropertyFromDocumentModel(comment, "author"),
                    comment -> getPropertyFromDocumentModel(comment, "text")
                )
                .containsExactly(Assertions.tuple(user1, "some specific note"));
        }
    }

    private static String getPropertyFromDocumentModel(DocumentModel docModel, String propertyName) {
        return (String) docModel.getProperty(COMMENT_SCHEMA, propertyName);
    }

    private Question getQuestion(CoreSession session) {
        return session.getDocument(questionRef).getAdapter(Question.class);
    }

    private Reponse getReponse(CoreSession session) {
        return session.getDocument(reponseRef).getAdapter(Reponse.class);
    }

    private Dossier getDossier(CoreSession session) {
        return session.getDocument(dossierRef).getAdapter(Dossier.class);
    }

    private void addPermission(CoreSession session, DocumentRef docRef, String username, String permission) {
        ACP acp = requireNonNullElseGet(session.getACP(docRef), ACPImpl::new);
        ACL acl = acp.getOrCreateACL(ACL.LOCAL_ACL);
        acl.add(ACE.builder(username, permission).build());
        acp.addACL(acl);
        session.setACP(docRef, acp, true);
    }
}
