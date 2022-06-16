package fr.dila.reponses.core.feuilleroute;

import fr.dila.cm.caselink.ActionableCaseLink;
import fr.dila.cm.cases.CaseDistribConstants;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.service.CaseDistributionService;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.feuilleroute.ReponsesFeuilleRoute;
import fr.dila.reponses.api.service.DocumentRoutingService;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.api.service.FeuilleRouteModelService;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.st.api.caselink.STDossierLink;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * Test du service FeuilleRouteService.
 * - Méthode feuilleRouteService.getDocumentRouteStep()
 *
 * @author jtremeaux
 */
@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class TestFeuilleRouteService {
    private static final String DEFAULT_ROUTE_NAME = "DefaultRouteModel";

    @Inject
    private CoreFeature coreFeature;

    @Inject
    private ReponseFeature reponseFeature;

    @Inject
    private FeuilleRouteModelService feuilleRouteModelService;

    @Inject
    private DocumentRoutingService documentRoutingService;

    @Inject
    private DossierDistributionService dossierDistributionService;

    @Inject
    private CaseDistributionService caseDistributionService;

    private SSFeuilleRoute defaultRoute;

    /**
     * Identifiants des étapes de la feuille de route.
     */
    protected List<String> stepNameList;

    @Test
    public void testDefaultRoute() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // Crée la feuille de route par défaut
            defaultRoute = createDefaultRoute(session);

            // Valide la feuille de route
            documentRoutingService.lockDocumentRoute(defaultRoute, session);
            defaultRoute = documentRoutingService.validateRouteModel(defaultRoute, session);
            session.saveDocument(defaultRoute.getDocument());
            session.save();
            documentRoutingService.unlockDocumentRoute(defaultRoute, session);
        }

        coreFeature.waitForAsyncCompletion();

        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            Assert.assertEquals("validated", defaultRoute.getDocument().getCurrentLifeCycleState());
            Assert.assertEquals(
                "validated",
                session.getChildren(defaultRoute.getDocument().getRef()).get(0).getCurrentLifeCycleState()
            );

            // Vérifie la présence d'une feuille de route par défaut
            SSFeuilleRoute defaultRoute = feuilleRouteModelService.getDefaultRouteQuestion(session);
            Assert.assertNotNull(defaultRoute);

            // Cree un dossier
            Dossier dossier = reponseFeature.createDossier(session);

            // Démarre la feuille de route associée au dossier
            dossierDistributionService.startDefaultRoute(session, dossier);

            // Valide les tâches successivement
            reponseFeature.verifyCaseLinkPresent(ReponseFeature.user1, true);
            reponseFeature.verifyCaseLinkAbsent(ReponseFeature.user2);
            validateUserTask(ReponseFeature.user1, 1);

            reponseFeature.verifyCaseLinkAbsent(ReponseFeature.user1);
            reponseFeature.verifyCaseLinkPresent(ReponseFeature.user2, true);
            validateUserTask(ReponseFeature.user2, 1);

            // Les CaseLink sont retirés des 2 mailbox
            reponseFeature.verifyCaseLinkAbsent(ReponseFeature.user1);
            reponseFeature.verifyCaseLinkAbsent(ReponseFeature.user2);
        }
    }

    private void validateUserTask(String user, int stepIndex) throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession(user)) {
            Mailbox userMailbox = reponseFeature.getPersonalMailbox(session, user);
            List<STDossierLink> links = caseDistributionService.getReceivedCaseLinks(session, userMailbox, 0, 0);
            Assert.assertEquals(1, links.size());

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

        stepNameList = new ArrayList<>();

        // Etape 1
        DocumentModel step1 = reponseFeature.createDocument(
            session,
            "Tâche de distribution user1",
            SSConstant.ROUTE_STEP_DOCUMENT_TYPE,
            route.getPathAsString()
        );
        Mailbox user1Mailbox = reponseFeature.getPersonalMailbox(session, ReponseFeature.user1);
        step1.setPropertyValue(CaseDistribConstants.STEP_DISTRIBUTION_MAILBOX_ID_PROPERTY_NAME, user1Mailbox.getId());
        session.saveDocument(step1);
        stepNameList.add(step1.getName());

        // Etape 2
        DocumentModel step2 = reponseFeature.createDocument(
            session,
            "Tâche de validation user2",
            SSConstant.ROUTE_STEP_DOCUMENT_TYPE,
            route.getPathAsString()
        );
        Mailbox user2Mailbox = reponseFeature.getPersonalMailbox(session, ReponseFeature.user2);
        step2.setPropertyValue(CaseDistribConstants.STEP_DISTRIBUTION_MAILBOX_ID_PROPERTY_NAME, user2Mailbox.getId());
        session.saveDocument(step2);
        stepNameList.add(step2.getName());

        session.save();

        return route.getAdapter(SSFeuilleRoute.class);
    }
}
