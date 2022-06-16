package fr.dila.reponses.core.feuilleroute;

import fr.dila.cm.caselink.ActionableCaseLink;
import fr.dila.cm.cases.CaseDistribConstants;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.service.CaseDistributionService;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.feuilleroute.ReponsesFeuilleRoute;
import fr.dila.reponses.api.service.DocumentRoutingService;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.api.service.ReponseFeuilleRouteService;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.api.dossier.STDossier;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteStep;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.model.RouteTableElement;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.LifeCycleConstants;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * Ce test exécute une feuille de route jusqu'à sa terminaison, puis ajoute une nouvelle étape et
 * lance à nouveau la feuille de route.
 *
 * @author jtremeaux
 */
@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class TestFeuilleRouteBackToReady {
    private static final Log LOG = LogFactory.getLog(TestFeuilleRouteBackToReady.class);

    private static final String DEFAULT_ROUTE_NAME = "DefaultRouteModel";

    @Inject
    private CoreFeature coreFeature;

    @Inject
    private ReponseFeature reponseFeature;

    @Inject
    protected ReponseFeuilleRouteService feuilleRouteService;

    @Inject
    private CaseDistributionService caseDistributionService;

    @Inject
    private DocumentRoutingService routingService;

    @Inject
    private DossierDistributionService dossierDistributionService;

    protected DocumentRef defaultRouteRef;

    protected static final String STATE_DRAFT = FeuilleRouteElement.ElementLifeCycleState.draft.name();

    protected static final String STATE_READY = FeuilleRouteElement.ElementLifeCycleState.ready.name();

    protected static final String STATE_RUNNING = FeuilleRouteElement.ElementLifeCycleState.running.name();

    protected static final String STATE_DONE = FeuilleRouteElement.ElementLifeCycleState.done.name();

    @Test
    public void testFeuilleRouteBackToReady() throws Exception {
        DocumentRef feuilleRouteInstanceRef = null;
        SSFeuilleRoute defaultRoute = null;
        Dossier dossier = null;
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // Crée la feuille de route par défaut
            defaultRoute = createDefaultRoute(session);

            DocumentModelList routeStepList = ReponsesServiceLocator
                .getDocumentRoutingService()
                .getOrderedRouteElement(defaultRoute.getDocument().getId(), session);
            Assert.assertEquals(1, routeStepList.size());
            Assert.assertEquals(
                "Tâche de validation user1",
                routeStepList.get(0).getAdapter(FeuilleRouteStep.class).getName()
            );

            // Valide la feuille de route
            reponseFeature.validateRoute(session, defaultRoute);
            defaultRouteRef = defaultRoute.getDocument().getRef();

            // Vérifie la présence d'une feuille de route par défaut
            defaultRoute = ReponsesServiceLocator.getFeuilleRouteModelService().getDefaultRouteQuestion(session);
            Assert.assertNotNull(defaultRoute);

            // Crée le dossier
            dossier = reponseFeature.createDossier(session);
            Assert.assertEquals(STATE_RUNNING, dossier.getDocument().getCurrentLifeCycleState());

            // Démarre la feuille de route associée au dossier
            SSFeuilleRoute feuilleRouteInstance = dossierDistributionService.startDefaultRoute(session, dossier);
            feuilleRouteInstance.getDocument().refresh();
            Assert.assertEquals(STATE_RUNNING, feuilleRouteInstance.getDocument().getCurrentLifeCycleState());
            feuilleRouteInstanceRef = feuilleRouteInstance.getDocument().getRef();

            // Ajoute une étape à la feuille de route
            LOG.info("Feuille de route démarrée - Ajout d'une étape");
            DocumentModel feuilleRouteInstanceDoc = feuilleRouteInstance.getDocument();
            DocumentModel step2Doc = session.createDocumentModel(
                feuilleRouteInstanceDoc.getPathAsString(),
                "Tâche de validation user2",
                SSConstant.ROUTE_STEP_DOCUMENT_TYPE
            );
            Mailbox user2Mailbox = reponseFeature.getPersonalMailbox(session, ReponseFeature.user2);
            step2Doc.setPropertyValue(
                CaseDistribConstants.STEP_DISTRIBUTION_MAILBOX_ID_PROPERTY_NAME,
                user2Mailbox.getId()
            );
            step2Doc.putContextData(LifeCycleConstants.INITIAL_LIFECYCLE_STATE_OPTION_NAME, STATE_READY);
            step2Doc = session.createDocument(step2Doc);
            session.save();

            feuilleRouteInstance = session.getDocument(feuilleRouteInstanceRef).getAdapter(SSFeuilleRoute.class);
            // Vérifie l'état de la feuille de route et de ses éléments
            checkFeuilleRouteState(
                session,
                feuilleRouteInstance,
                STATE_RUNNING,
                new String[] { STATE_RUNNING, STATE_READY }
            );

            // Valide les étapes
            reponseFeature.verifyCaseLinkPresent(ReponseFeature.user1, true);
            reponseFeature.verifyCaseLinkAbsent(ReponseFeature.user2);

            validateUserTask(ReponseFeature.user1);
            // Vérifie l'état de la feuille de route et de ses éléments
            checkFeuilleRouteState(
                session,
                feuilleRouteInstance,
                STATE_RUNNING,
                new String[] { STATE_DONE, STATE_RUNNING }
            );

            reponseFeature.verifyCaseLinkAbsent(ReponseFeature.user1);
            reponseFeature.verifyCaseLinkPresent(ReponseFeature.user2, true);

            session.save();
            validateUserTask(ReponseFeature.user2);
            reponseFeature.verifyCaseLinkAbsent(ReponseFeature.user1);
            reponseFeature.verifyCaseLinkAbsent(ReponseFeature.user2);
            Assert.assertEquals(
                STATE_DONE,
                session.getDocument(dossier.getDocument().getRef()).getCurrentLifeCycleState()
            );

            feuilleRouteInstance = session.getDocument(feuilleRouteInstanceRef).getAdapter(SSFeuilleRoute.class);
            // Vérifie que la route et ses éléments sont a l'état terminé
            checkFeuilleRouteState(session, feuilleRouteInstance, STATE_DONE, new String[] { STATE_DONE, STATE_DONE });

            // Ajoute une étape à la feuille de route une fois terminée
            DocumentModel step3Doc = session.createDocumentModel(
                feuilleRouteInstance.getDocument().getPathAsString(),
                "Tâche de validation user3",
                SSConstant.ROUTE_STEP_DOCUMENT_TYPE
            );
            Mailbox user3Mailbox = reponseFeature.getPersonalMailbox(session, ReponseFeature.user3);
            step3Doc.setPropertyValue(
                CaseDistribConstants.STEP_DISTRIBUTION_MAILBOX_ID_PROPERTY_NAME,
                user3Mailbox.getId()
            );
            step3Doc.putContextData(LifeCycleConstants.INITIAL_LIFECYCLE_STATE_OPTION_NAME, STATE_READY);
            step3Doc = session.createDocument(step3Doc);
            session.save();

            // Vérifie l'état de la feuille de route et de ses éléments
            checkFeuilleRouteState(
                session,
                feuilleRouteInstance,
                STATE_DONE,
                new String[] { STATE_DONE, STATE_DONE, STATE_READY }
            );

            // Redémarre la feuille de route
            dossier.getDocument().followTransition(STDossier.DossierTransition.backToRunning.name());
            feuilleRouteInstance.backToReady(session);
            session.save();
        }

        coreFeature.waitForAsyncCompletion();

        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            SSFeuilleRoute feuilleRouteInstance = session
                .getDocument(feuilleRouteInstanceRef)
                .getAdapter(SSFeuilleRoute.class);

            // Vérifie l'état de la feuille de route et de ses éléments
            checkFeuilleRouteState(
                session,
                feuilleRouteInstance,
                STATE_READY,
                new String[] { STATE_DONE, STATE_DONE, STATE_READY }
            );

            feuilleRouteInstance.run(session);

            // Vérifie l'état de la feuille de route et de ses éléments
            checkFeuilleRouteState(
                session,
                feuilleRouteInstance,
                STATE_RUNNING,
                new String[] { STATE_DONE, STATE_DONE, STATE_RUNNING }
            );
        }

        // Valide la nouvelle étape
        reponseFeature.verifyCaseLinkAbsent(ReponseFeature.user1);
        reponseFeature.verifyCaseLinkAbsent(ReponseFeature.user2);
        reponseFeature.verifyCaseLinkPresent(ReponseFeature.user3, true);

        validateUserTask(ReponseFeature.user3);
        reponseFeature.verifyCaseLinkAbsent(ReponseFeature.user1);
        reponseFeature.verifyCaseLinkAbsent(ReponseFeature.user2);
        reponseFeature.verifyCaseLinkAbsent(ReponseFeature.user3);
    }

    private void validateUserTask(String user) throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession(user)) {
            Mailbox userMailbox = reponseFeature.getPersonalMailbox(session, user);
            List<STDossierLink> links = caseDistributionService.getReceivedCaseLinks(session, userMailbox, 0, 0);
            Assert.assertEquals("Présence du CaseLink dans la Mailbox de l'utilisateur", 1, links.size());

            ActionableCaseLink actionableLink = null;
            for (STDossierLink link : links) {
                if (link.isActionnable()) {
                    actionableLink = link.getDocument().getAdapter(ActionableCaseLink.class);
                    actionableLink.validate(session);
                }
            }
            Assert.assertNotNull(actionableLink);
        }
    }

    /**
     * Retourne la route par défaut (crée ses étapes par effet de bord).
     *
     * @param session Session
     * @return Route par défaut
     * @throws Exception
     */
    private SSFeuilleRoute createDefaultRoute(CoreSession session) throws Exception {
        DocumentModel fdrRoot = reponseFeature.createOrGetFeuilleRouteModelFolder(session);

        DocumentModel route = reponseFeature.createDocument(
            session,
            DEFAULT_ROUTE_NAME,
            SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE,
            fdrRoot.getPathAsString()
        );
        ReponsesFeuilleRoute feuilleRoute = route.getAdapter(ReponsesFeuilleRoute.class);
        Assert.assertNotNull(feuilleRoute);

        feuilleRoute.setFeuilleRouteDefaut(true);
        session.saveDocument(feuilleRoute.getDocument());
        session.save();

        // Etape 1
        DocumentModel step1 = reponseFeature.createDocument(
            session,
            "Tâche de validation user1",
            "RouteStep",
            route.getPathAsString()
        );
        Mailbox user1Mailbox = reponseFeature.getPersonalMailbox(session, ReponseFeature.user1);
        step1.setPropertyValue(CaseDistribConstants.STEP_DISTRIBUTION_MAILBOX_ID_PROPERTY_NAME, user1Mailbox.getId());
        session.saveDocument(step1);

        session.save();

        return feuilleRoute;
    }

    protected void checkFeuilleRouteState(
        CoreSession session,
        SSFeuilleRoute route,
        String routeState,
        String... routeElementState
    ) {
        // Vérifie l'état en cours de la feuille de route
        DocumentModel routeDoc = session.getDocument(route.getDocument().getRef());
        Assert.assertEquals("Etat en cours de la feuille de route", routeState, routeDoc.getCurrentLifeCycleState());

        // Vérifie l'état en cours des éléments de la feuille de route
        List<RouteTableElement> stepTable = routingService.getFeuilleRouteElements(route, session);
        Assert.assertEquals(routeElementState.length, stepTable.size());
        for (int i = 0; i < stepTable.size(); i++) {
            RouteTableElement stepTableElement = stepTable.get(i);
            String stepState = routeElementState[i];

            DocumentModel stepDoc = stepTableElement.getDocument();
            Assert.assertEquals(
                "Etat en cours de l'élément de la feuille de route",
                stepState,
                stepDoc.getCurrentLifeCycleState()
            );
        }
    }
}
