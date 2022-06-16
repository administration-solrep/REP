package fr.dila.reponses.core.feuilleroute;

import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.service.CaseDistributionService;
import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.feuilleroute.ReponsesRouteStep;
import fr.dila.reponses.api.service.DocumentRoutingService;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.api.service.FeuilleRouteModelService;
import fr.dila.reponses.api.service.ReponseFeuilleRouteService;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.reponses.core.mock.MockFeuilleRouteModelService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.constant.SSFeuilleRouteConstant;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.core.helper.FeuilleRouteTestHelper;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.api.service.STParametreService;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.exception.FeuilleRouteAlreadyLockedException;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.exception.FeuilleRouteNotLockedException;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
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
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class TestFeuilleRouteReponse {
    private static final String DEFAULT_ROUTE_NAME = "DefaultRouteModel";

    private static final Log LOG = LogFactory.getLog(TestFeuilleRouteReponse.class);

    @Inject
    private CoreFeature coreFeature;

    @Inject
    private ReponseFeature reponseFeature;

    @Inject
    protected FeuilleRouteModelService feuilleRouteModelService;

    @Inject
    protected ReponseFeuilleRouteService feuilleRouteService;

    @Inject
    private DossierDistributionService dossierDistributionService;

    @Inject
    private DocumentRoutingService documentRoutingService;

    @Inject
    private CaseDistributionService caseDistributionService;

    @Inject
    private STParametreService paramService;

    @Before
    public void setUp() {
        reponseFeature.setFeuilleRootModelFolderId(null);
        ((MockFeuilleRouteModelService) ReponsesServiceLocator.getFeuilleRouteModelService()).clear();
        paramService.clearCache();
    }

    // Une feuille de route qui ressemble un plus à celle utilisée par réponse
    private SSFeuilleRoute createFeuilleRoute(CoreSession session) throws Exception {
        DocumentModel route = reponseFeature.createFeuilleRoute(session, DEFAULT_ROUTE_NAME);

        final String userMboxid1 = reponseFeature.getPersonalMailbox(session, ReponseFeature.user1).getId();
        final String userMboxid2 = reponseFeature.getPersonalMailbox(session, ReponseFeature.user2).getId();
        FeuilleRouteTestHelper.createSerialStep(
            session,
            route,
            userMboxid1,
            "Pour attribution agents BDC",
            VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION
        );
        FeuilleRouteTestHelper.createSerialStep(
            session,
            route,
            userMboxid2,
            "Pour rédaction DLF",
            VocabularyConstants.ROUTING_TASK_TYPE_REDACTION
        );
        FeuilleRouteTestHelper.createSerialStep(
            session,
            route,
            userMboxid2,
            "Pour validation PM",
            VocabularyConstants.ROUTING_TASK_TYPE_VALIDATION_PM
        );
        session.save();
        return route.getAdapter(SSFeuilleRoute.class);
    }

    private Dossier createDossier(CoreSession session) throws Exception {
        DocumentModel questionDocumentModel = ReponseFeature.createDocument(
            session,
            DossierConstants.QUESTION_DOCUMENT_TYPE,
            "newQuestionTest2"
        );
        Question question = questionDocumentModel.getAdapter(Question.class);
        DocumentModel dossierDocumentModel = ReponseFeature.createDocument(
            session,
            DossierConstants.DOSSIER_DOCUMENT_TYPE,
            "newDossierTest2"
        );
        // check properties
        Long numQuestionLong = 15524L;
        String typeQuestion = VocabularyConstants.QUESTION_TYPE_QE;
        question.setTypeQuestion(typeQuestion);
        String texteQuestion = "quelle heure est il ?";
        Calendar date = GregorianCalendar.getInstance();
        String nomAuteur = "Valjean";
        String prenomAuteur = "Jean";
        question.setNumeroQuestion(numQuestionLong);
        question.setTypeQuestion(typeQuestion);
        question.setTexteQuestion(texteQuestion);
        question.setDateReceptionQuestion(date);
        question.setNomAuteur(nomAuteur);
        question.setPrenomAuteur(prenomAuteur);
        Dossier dossier = dossierDocumentModel.getAdapter(Dossier.class);
        dossier =
            dossierDistributionService.createDossier(
                session,
                dossier,
                question,
                null,
                VocabularyConstants.ETAT_QUESTION_EN_COURS
            );
        return dossier;
    }

    private void createAndValidateFdr(CoreSession session)
        throws Exception, FeuilleRouteAlreadyLockedException, FeuilleRouteNotLockedException {
        // Crée la feuille de route par défaut
        SSFeuilleRoute feuille_de_route_reponse = createFeuilleRoute(session);
        Assert.assertNotNull(feuille_de_route_reponse);
        // Valide la feuille de route
        reponseFeature.validateRoute(session, feuille_de_route_reponse);
    }

    @Test
    public void testValidationPMStep() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            createAndValidateFdr(session);

            FeuilleRoute defaultRoute = feuilleRouteModelService.getDefaultRouteQuestion(session);
            Assert.assertNotNull(defaultRoute);

            Dossier dossier = createDossier(session);
            session.saveDocument(dossier.getDocument());
            FeuilleRoute routeInstance = dossierDistributionService.startDefaultRoute(session, dossier);
            DocumentModel routeInstanceDoc = routeInstance.getDocument();
            DocumentModel pm_step = feuilleRouteService.getValidationPMStep(session, routeInstanceDoc.getId());
            Assert.assertNotNull(pm_step);
            Assert.assertEquals("Pour validation PM", pm_step.getTitle());
        }
    }

    @Test
    public void testDuplicationFdr()
        throws FeuilleRouteAlreadyLockedException, FeuilleRouteNotLockedException, Exception {
        DocumentRef defaultRouteDuplicateRef = null;
        String defaultRouteName = null;
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            createAndValidateFdr(session);

            final String query =
                "SELECT f.ecm:uuid AS id FROM " +
                SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE +
                " AS f WHERE f.fdr:feuilleRouteDefaut = 1 " +
                " AND f.ecm:currentLifeCycleState = 'validated' ";

            final DocumentRef refs[] = QueryUtils.doUFNXQLQueryForIds(session, query, null, 1, 0);
            LOG.info(refs.length);

            FeuilleRoute defaultRoute = feuilleRouteModelService.getDefaultRouteQuestion(session);
            Assert.assertNotNull(defaultRoute);
            defaultRouteName = defaultRoute.getName();
            Mailbox userMailbox = reponseFeature.getPersonalMailbox(session, ReponseFeature.user1);
            DocumentModel routeToCopyDoc = defaultRoute.getDocument();

            FeuilleRoute defaultRouteDuplicate = documentRoutingService.duplicateRouteModel(
                session,
                routeToCopyDoc,
                userMailbox.getId()
            );
            defaultRouteDuplicateRef = defaultRouteDuplicate.getDocument().getRef();
            session.saveDocument(defaultRouteDuplicate.getDocument());
            session.save();
        }

        coreFeature.waitForAsyncCompletion();

        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel defaultRouteDuplicateDoc = session.getDocument(defaultRouteDuplicateRef);
            Assert.assertEquals(defaultRouteDuplicateDoc.getTitle(), defaultRouteName + " (Copie)");
            Assert.assertEquals("draft", defaultRouteDuplicateDoc.getCurrentLifeCycleState());
        }
    }

    @Test
    public void testDuplicationEtValidationFdr()
        throws FeuilleRouteAlreadyLockedException, FeuilleRouteNotLockedException, Exception {
        DocumentRef defaultRouteDuplicateRef = null;
        String defaultRouteName = null;
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            createAndValidateFdr(session);

            FeuilleRoute defaultRoute = feuilleRouteModelService.getDefaultRouteQuestion(session);
            defaultRouteName = defaultRoute.getName();
            Assert.assertNotNull(defaultRoute);
            Mailbox userMailbox = reponseFeature.getPersonalMailbox(session, ReponseFeature.user1);
            LOG.info("duplicate model");
            FeuilleRoute defaultRouteDuplicate = documentRoutingService.duplicateRouteModel(
                session,
                defaultRoute.getDocument(),
                userMailbox.getId()
            );
            defaultRouteDuplicateRef = defaultRouteDuplicate.getDocument().getRef();
        }

        coreFeature.waitForAsyncCompletion();

        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel defaultRouteDuplicateDoc = session.getDocument(defaultRouteDuplicateRef);
            Assert.assertEquals(defaultRouteDuplicateDoc.getTitle(), defaultRouteName + " (Copie)");
            Assert.assertEquals("draft", defaultRouteDuplicateDoc.getCurrentLifeCycleState());
        }

        // Validation de la fdr
        LOG.info("validate fdr");
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            SSFeuilleRoute defaultRouteDuplicate = session
                .getDocument(defaultRouteDuplicateRef)
                .getAdapter(SSFeuilleRoute.class);
            reponseFeature.validateRoute(session, defaultRouteDuplicate);
        }
    }

    @Test
    public void testDuplicationStepValidated()
        throws FeuilleRouteAlreadyLockedException, FeuilleRouteNotLockedException, Exception {
        DocumentRef defaultRouteDuplicateRef = null;
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            createAndValidateFdr(session);
            FeuilleRoute defaultRoute = feuilleRouteModelService.getDefaultRouteQuestion(session);
            Assert.assertNotNull(defaultRoute);
            Mailbox userMailbox = reponseFeature.getPersonalMailbox(session, ReponseFeature.user1);
            FeuilleRoute defaultRouteDuplicate = documentRoutingService.duplicateRouteModel(
                session,
                defaultRoute.getDocument(),
                userMailbox.getId()
            );
            defaultRouteDuplicateRef = defaultRouteDuplicate.getDocument().getRef();
            session.save();
        }

        coreFeature.waitForAsyncCompletion();

        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            SSFeuilleRoute defaultRouteDuplicate = session
                .getDocument(defaultRouteDuplicateRef)
                .getAdapter(SSFeuilleRoute.class);

            // Après la duplication d'une FDR, la nouvelle FDR est invalidée (passage à
            // l'état draft), si elle est validée, ainsi que toutes ses étapes
            DocumentModelList children = session.getChildren(defaultRouteDuplicateRef);
            DocumentModel step0 = children.get(0);
            DocumentModel step1 = children.get(1);
            Assert.assertEquals("draft", step0.getCurrentLifeCycleState());
            Assert.assertEquals("draft", step1.getCurrentLifeCycleState());

            // Passage d'étapes de fdr à l'état validated
            step0.followTransition("toValidated");
            step1.followTransition("toValidated");
            Assert.assertEquals("validated", step0.getCurrentLifeCycleState());
            Assert.assertEquals("validated", step1.getCurrentLifeCycleState());

            // Test de la méthode qui remet les états d'étape de modèle de fdr à 'draft'
            // Cette méthode est appelée lors de la validation d'un modèle de fdr
            documentRoutingService.checkAndMakeAllStateStepDraft(session, defaultRouteDuplicate);

            step0 = session.getChildren(defaultRouteDuplicateRef).get(0);
            step1 = session.getChildren(defaultRouteDuplicateRef).get(1);

            // Si les états d'étape sont repassés à 'draft' c'est que la méthode fait ce qu'on lui demande
            Assert.assertEquals("draft", step0.getCurrentLifeCycleState());
            Assert.assertEquals("draft", step1.getCurrentLifeCycleState());

            // Validation de la fdr pour vérifier que le mécanisme de validation fonctionne toujours
            reponseFeature.validateRoute(session, defaultRouteDuplicate);
        }
    }

    @Test
    public void testAddStepAttente()
        throws FeuilleRouteAlreadyLockedException, FeuilleRouteNotLockedException, Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            createAndValidateFdr(session);
            Dossier dossier = createDossier(session);
            session.saveDocument(dossier.getDocument());
            SSFeuilleRoute routeInstance = dossierDistributionService.startDefaultRoute(session, dossier);

            DocumentModelList routeSteps = session.getChildren(routeInstance.getDocument().getRef());
            Assert.assertEquals(3, routeSteps.size());
            Assert.assertTrue(routeSteps.get(0).getAdapter(ReponsesRouteStep.class).isRunning());

            Mailbox user1Mailbox = reponseFeature.getPersonalMailbox(session, ReponseFeature.user1);
            List<STDossierLink> dossierLinks = caseDistributionService.getReceivedCaseLinks(
                session,
                user1Mailbox,
                0,
                0
            );

            // Test de l'ajout d'étape pour attente
            documentRoutingService.lockDocumentRoute(routeInstance, session);
            feuilleRouteService.addStepAttente(session, (DossierLink) dossierLinks.get(0));
            documentRoutingService.unlockDocumentRoute(routeInstance, session);

            routeSteps = session.getChildren(routeInstance.getDocument().getRef());
            Assert.assertEquals(4, routeSteps.size());
            ReponsesRouteStep step0 = routeSteps.get(0).getAdapter(ReponsesRouteStep.class);
            ReponsesRouteStep step1 = routeSteps.get(1).getAdapter(ReponsesRouteStep.class);
            Assert.assertTrue(step0.isDone());
            Assert.assertEquals(
                SSFeuilleRouteConstant.ROUTING_TASK_VALIDATION_STATUS_AVIS_FAVORABLE_VALUE,
                step0.getValidationStatus()
            );
            Assert.assertTrue(step1.isRunning());
            Assert.assertEquals(VocabularyConstants.ROUTING_TASK_TYPE_ATTENTE, step1.getType());
            Assert.assertEquals(user1Mailbox.getId(), step1.getDistributionMailboxId());
        }
    }

    @Test
    public void testStepAfterReattribution()
        throws FeuilleRouteAlreadyLockedException, FeuilleRouteNotLockedException, Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            createAndValidateFdr(session);
            Dossier dossier = createDossier(session);
            session.saveDocument(dossier.getDocument());
            SSFeuilleRoute routeInstance = dossierDistributionService.startDefaultRoute(session, dossier);

            DocumentModelList routeSteps = session.getChildren(routeInstance.getDocument().getRef());
            Assert.assertEquals(3, routeSteps.size());
            Assert.assertTrue(routeSteps.get(0).getAdapter(ReponsesRouteStep.class).isRunning());

            Mailbox user1Mailbox = reponseFeature.getPersonalMailbox(session, ReponseFeature.user1);
            Mailbox user2Mailbox = reponseFeature.getPersonalMailbox(session, ReponseFeature.user2);
            List<STDossierLink> dossierLinks = caseDistributionService.getReceivedCaseLinks(
                session,
                user1Mailbox,
                0,
                0
            );

            // Test de l'ajout d'étape pour réattribution
            documentRoutingService.lockDocumentRoute(routeInstance, session);
            feuilleRouteService.addStepAfterReattribution(
                session,
                (DossierLink) dossierLinks.get(0),
                user2Mailbox.getId()
            );
            documentRoutingService.unlockDocumentRoute(routeInstance, session);

            routeSteps = session.getChildren(routeInstance.getDocument().getRef());
            Assert.assertEquals(4, routeSteps.size());
            ReponsesRouteStep step0 = routeSteps.get(0).getAdapter(ReponsesRouteStep.class);
            ReponsesRouteStep step1 = routeSteps.get(1).getAdapter(ReponsesRouteStep.class);
            Assert.assertTrue(step0.isDone());
            Assert.assertEquals(
                SSFeuilleRouteConstant.ROUTING_TASK_VALIDATION_STATUS_NON_CONCERNE_VALUE,
                step0.getValidationStatus()
            );
            Assert.assertTrue(step1.isRunning());
            Assert.assertEquals(VocabularyConstants.ROUTING_TASK_TYPE_REATTRIBUTION, step1.getType());
            Assert.assertEquals(user2Mailbox.getId(), step1.getDistributionMailboxId());
        }
    }
}
