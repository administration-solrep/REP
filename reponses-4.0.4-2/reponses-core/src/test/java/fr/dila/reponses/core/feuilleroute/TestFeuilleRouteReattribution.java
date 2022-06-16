package fr.dila.reponses.core.feuilleroute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import fr.dila.cm.caselink.ActionableCaseLink;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.service.CaseDistributionService;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.reponses.core.mock.MockFeuilleRouteModelService;
import fr.dila.reponses.core.service.DossierDistributionServiceImpl;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.ss.api.constant.SSFeuilleRouteConstant;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.core.helper.FeuilleRouteTestHelper;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement.ElementLifeCycleState;
import java.text.MessageFormat;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * Test de la réattribution de feuilles de routes. Une reattribution diffère de la substitution : - même instance de
 * feuille de route. - Les étapes à venir de l'ancienne instance sont supprimées - Les étapes de la nouvelles feuille de
 * route sont ajoutées à la suite - l'étape en cours est validée
 *
 * @author jgomez
 *
 */
@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class TestFeuilleRouteReattribution {
    private static final Log LOG = LogFactory.getLog(TestFeuilleRouteReattribution.class);

    private static final String DEFAULT_ROUTE_NAME = "DefaultRouteModel";

    private static final String NEW_MINSTERE_ROUTE_NAME = "NouveauMinistereRouteModel";

    @Inject
    private CoreFeature coreFeature;

    @Inject
    private ReponseFeature reponseFeature;

    @Inject
    private DossierDistributionServiceImpl dossierDistributionService;

    @Inject
    private DocumentRoutingService documentRoutingService;

    @Inject
    private CaseDistributionService caseDistributionService;

    private SSFeuilleRoute currentRoute;

    private SSFeuilleRoute newRoute;

    /**
     * Vérifie la réattribution d'une feuille de route.
     *
     * @throws Exception
     */
    @Test
    public void testReattribution1EtapeValide() throws Exception {
        DocumentRef dossierRef = null;
        DocumentRef routeInstanceRef = null;
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // Crée la feuille de route par défaut
            currentRoute = createDefaultRoute(session);
            // Crée une nouvelle feuille de route (d'un autre ministère)
            newRoute = createRouteNouveauMinistere(session);

            // Valide les feuille de route
            reponseFeature.validateRoute(session, currentRoute);
            reponseFeature.validateRoute(session, newRoute);

            // Cree un dossier
            Dossier dossier = reponseFeature.createDossier(session);
            dossierRef = dossier.getDocument().getRef();

            // Démarre la feuille de route associée au dossier
            LOG.info("Démarre la feuille de route associée au dossier");
            FeuilleRoute routeInstance = dossierDistributionService.startDefaultRoute(session, dossier);
            DocumentModel routeInstanceDoc = routeInstance.getDocument();
            routeInstanceRef = routeInstanceDoc.getRef();
            assertEquals(
                "Instance de feuille de route initiale",
                routeInstanceDoc.getId(),
                dossier.getLastDocumentRoute()
            );
        }

        // Récupère le CaseLink de user1

        try (CloseableCoreSession session = coreFeature.openCoreSession(ReponseFeature.user1)) {
            Mailbox userMailbox = reponseFeature.getPersonalMailbox(session, ReponseFeature.user1);
            List<STDossierLink> links = caseDistributionService.getReceivedCaseLinks(session, userMailbox, 0, 0);
            assertEquals(1, links.size());
            ActionableCaseLink actionableLink = null;
            for (STDossierLink link : links) {
                if (link.isActionnable()) {
                    actionableLink = link.getDocument().getAdapter(ActionableCaseLink.class);
                    actionableLink.validate(session);
                }
            }
        }

        // Substitue la feuille de route
        LOG.info("Substitue la feuille de route");
        DocumentModel routeInstanceDoc = null;
        DocumentModel routeInstance2Doc = null;
        Dossier dossier = null;
        DocumentModelList steps = null;
        String dossierId = null;
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            routeInstanceDoc = session.getDocument(routeInstanceRef);
            DocumentModel dossierDoc = session.getDocument(dossierRef);
            dossier = dossierDoc.getAdapter(Dossier.class);

            // Met le ministère attributaire.
            steps = documentRoutingService.getOrderedRouteElement(routeInstanceDoc.getId(), session);

            // réattribue le dossier
            routeInstance2Doc =
                dossierDistributionService.reattribuerDossier(
                    session,
                    newRoute,
                    dossier,
                    SSFeuilleRouteConstant.ROUTING_TASK_VALIDATION_STATUS_NON_CONCERNE_VALUE
                );
            assertNotNull(routeInstance2Doc);
            dossierDistributionService.startRouteAfterSubstitution(
                session,
                routeInstanceDoc,
                routeInstance2Doc,
                "substitution"
            );
            dossierId = dossier.getDocument().getId();
        }
        coreFeature.waitForAsyncCompletion();

        String routeId = routeInstance2Doc.getId();

        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            dossier = session.getDocument(new IdRef(dossierId)).getAdapter(Dossier.class);
            assertEquals("Instance de feuille de route après substitution", routeId, dossier.getLastDocumentRoute());
            assertNotSame(routeInstanceDoc.getId(), routeId);
            assertNotSame(dossier.getLastDocumentRoute(), routeInstanceDoc.getId());
            DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
            steps = documentRoutingService.getOrderedRouteElement(routeId, session);
            assertEquals(5, steps.size());

            checkStep(steps, 0, "Pour redaction user 1", FeuilleRouteElement.ElementLifeCycleState.done, routeId);
            checkStep(steps, 1, "Pour reattribution", FeuilleRouteElement.ElementLifeCycleState.done, routeId);
            checkStep(
                steps,
                2,
                "Pour attribution agents BDC",
                FeuilleRouteElement.ElementLifeCycleState.running,
                routeId
            );
            checkStep(steps, 3, "Pour rédaction DLF", FeuilleRouteElement.ElementLifeCycleState.ready, routeId);
            checkStep(steps, 4, "Pour validation PM", FeuilleRouteElement.ElementLifeCycleState.ready, routeId);
        }
    }

    private void checkStep(
        DocumentModelList steps,
        int i,
        String expectedName,
        ElementLifeCycleState expectedLifecycleState,
        String expectedRoute
    ) {
        String msgPattern = "{0} : expected = {1} ; real = {2}";

        String realName = steps.get(i).getName();
        assertEquals(MessageFormat.format(msgPattern, i, expectedName, realName), expectedName, realName);
        String realStateName = steps.get(i).getCurrentLifeCycleState();
        assertEquals(
            MessageFormat.format(msgPattern, i, expectedLifecycleState, realStateName),
            expectedLifecycleState.name(),
            realStateName
        );
        String realDocRouteId = steps.get(i).getAdapter(SSRouteStep.class).getDocumentRouteId();
        assertEquals(MessageFormat.format(msgPattern, i, expectedRoute, realDocRouteId), expectedRoute, realDocRouteId);
    }

    @Before
    public void setUp() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            ((MockFeuilleRouteModelService) ReponsesServiceLocator.getFeuilleRouteModelService()).clear();
            (STServiceLocator.getSTParametreService()).clearCache();
            reponseFeature.setFeuilleRootModelFolderId(null);

            reponseFeature.getPersonalMailbox(session, ReponseFeature.user1);
        }
    }

    /**
     * Vérifie la réattribution d'une feuille de route.
     *
     * @throws Exception
     */
    @Test
    public void testReattributionDirect() throws Exception {
        DocumentRef dossierRef = null;
        DocumentRef routeInstanceRef = null;
        DocumentModelList steps = null;
        DocumentModel routeInstanceDoc = null;
        DocumentModel routeInstance2Doc = null;
        Dossier dossier = null;
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // Crée la feuille de route par défaut
            currentRoute = createDefaultRoute(session);
            // Crée une nouvelle feuille de route (d'un autre ministère)
            newRoute = createRouteNouveauMinistere(session);

            // Valide les feuille de route
            reponseFeature.validateRoute(session, currentRoute);
            reponseFeature.validateRoute(session, newRoute);
        }
        coreFeature.waitForAsyncCompletion();
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // Cree un dossier
            dossier = reponseFeature.createDossier(session);
            dossierRef = dossier.getDocument().getRef();

            // Démarre la feuille de route associée au dossier
            FeuilleRoute routeInstance = dossierDistributionService.startDefaultRoute(session, dossier);
            routeInstanceDoc = routeInstance.getDocument();
            routeInstanceRef = routeInstanceDoc.getRef();
            assertEquals(
                "Instance de feuille de route initiale",
                routeInstanceDoc.getId(),
                dossier.getLastDocumentRoute()
            );

            // Tests sur la feuille de route avant substitution
            steps = documentRoutingService.getOrderedRouteElement(dossier.getLastDocumentRoute(), session);
            assertEquals(3, steps.size());

            assertEquals("Pour redaction user 1", steps.get(0).getName());
            assertEquals("Pour reattribution", steps.get(1).getName());
            assertEquals("Pour validation pm", steps.get(2).getName());
            assertEquals(
                FeuilleRouteElement.ElementLifeCycleState.running.name(),
                steps.get(0).getCurrentLifeCycleState()
            );
            assertEquals(
                FeuilleRouteElement.ElementLifeCycleState.ready.name(),
                steps.get(1).getCurrentLifeCycleState()
            );
            assertEquals(
                FeuilleRouteElement.ElementLifeCycleState.ready.name(),
                steps.get(2).getCurrentLifeCycleState()
            );
        }

        // Substitue la feuille de route
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            routeInstanceDoc = session.getDocument(routeInstanceRef);
            DocumentModel dossierDoc = session.getDocument(dossierRef);
            dossier = dossierDoc.getAdapter(Dossier.class);

            // Met le ministère attributaire.
            steps = documentRoutingService.getOrderedRouteElement(routeInstanceDoc.getId(), session);
            routeInstance2Doc =
                dossierDistributionService.reattribuerDossier(
                    session,
                    newRoute,
                    dossier,
                    SSFeuilleRouteConstant.ROUTING_TASK_VALIDATION_STATUS_NON_CONCERNE_VALUE
                );
            assertNotNull(routeInstance2Doc);
            dossierDistributionService.startRouteAfterSubstitution(
                session,
                routeInstanceDoc,
                routeInstance2Doc,
                "substitution"
            );

            dossier = session.getDocument(new IdRef(dossier.getDocument().getId())).getAdapter(Dossier.class);
            routeInstanceRef = new IdRef(dossier.getLastDocumentRoute());

            assertEquals(
                "Instance de feuille de route après substitution",
                routeInstance2Doc.getId(),
                dossier.getLastDocumentRoute()
            );
            assertNotSame(routeInstanceDoc.getId(), routeInstance2Doc.getId());
            assertNotSame(dossier.getLastDocumentRoute(), routeInstanceDoc.getId());
        }
        coreFeature.waitForAsyncCompletion();
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            dossier = session.getDocument(dossierRef).getAdapter(Dossier.class);

            steps = documentRoutingService.getOrderedRouteElement(dossier.getLastDocumentRoute(), session);
            assertEquals(4, steps.size());

            assertEquals("Pour redaction user 1", steps.get(0).getName());
            assertEquals("Pour validation PM", steps.get(3).getName());
            assertEquals(
                FeuilleRouteElement.ElementLifeCycleState.done.name(),
                steps.get(0).getCurrentLifeCycleState()
            );
            assertEquals(
                FeuilleRouteElement.ElementLifeCycleState.running.name(),
                steps.get(1).getCurrentLifeCycleState()
            );
            assertEquals(
                FeuilleRouteElement.ElementLifeCycleState.ready.name(),
                steps.get(2).getCurrentLifeCycleState()
            );
            assertEquals(
                FeuilleRouteElement.ElementLifeCycleState.ready.name(),
                steps.get(3).getCurrentLifeCycleState()
            );
        }
    }

    /**
     * Retourne la route par défaut (crée ses étapes par effet de bord).
     *
     * @param session
     *            Session
     * @return Route par défaut
     * @throws Exception
     */
    private SSFeuilleRoute createDefaultRoute(CoreSession session) throws Exception {
        DocumentModel route = reponseFeature.createFeuilleRoute(session, DEFAULT_ROUTE_NAME);
        session.save();
        final String user1MboxId = reponseFeature.getPersonalMailbox(session, ReponseFeature.user1).getId();
        final String user2MboxId = reponseFeature.getPersonalMailbox(session, ReponseFeature.user2).getId();
        FeuilleRouteTestHelper.createSerialStep(
            session,
            route,
            user1MboxId,
            "Pour redaction user 1",
            VocabularyConstants.ROUTING_TASK_TYPE_REDACTION
        );
        FeuilleRouteTestHelper.createSerialStep(
            session,
            route,
            user2MboxId,
            "Pour reattribution",
            VocabularyConstants.ROUTING_TASK_TYPE_SIGNATURE
        );
        FeuilleRouteTestHelper.createSerialStep(
            session,
            route,
            user1MboxId,
            "Pour validation pm",
            VocabularyConstants.ROUTING_TASK_TYPE_VALIDATION_PM
        );
        route = session.saveDocument(route);
        session.save();
        return route.getAdapter(SSFeuilleRoute.class);
    }

    /**
     * Retourne la nouvelle feuille de route
     *
     * @param session
     *            Session
     * @return la nouvelle route
     * @throws Exception
     */
    private SSFeuilleRoute createRouteNouveauMinistere(CoreSession session) throws Exception {
        DocumentModel route = reponseFeature.createFeuilleRoute(session, NEW_MINSTERE_ROUTE_NAME);
        session.save();
        final String user1MboxId = reponseFeature.getPersonalMailbox(session, ReponseFeature.user1).getId();
        final String user2MboxId = reponseFeature.getPersonalMailbox(session, ReponseFeature.user2).getId();
        FeuilleRouteTestHelper.createSerialStep(
            session,
            route,
            user1MboxId,
            "Pour attribution agents BDC",
            VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION
        );
        FeuilleRouteTestHelper.createSerialStep(
            session,
            route,
            user2MboxId,
            "Pour rédaction DLF",
            VocabularyConstants.ROUTING_TASK_TYPE_REDACTION
        );
        FeuilleRouteTestHelper.createSerialStep(
            session,
            route,
            user2MboxId,
            "Pour validation PM",
            VocabularyConstants.ROUTING_TASK_TYPE_VALIDATION_PM
        );
        session.save();
        return route.getAdapter(SSFeuilleRoute.class);
    }
}
