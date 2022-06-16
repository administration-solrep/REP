package fr.dila.reponses.core.feuilleroute;

import fr.dila.cm.cases.CaseDistribConstants;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.service.CaseDistributionService;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.feuilleroute.ReponsesFeuilleRoute;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.api.service.STParametreService;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.List;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * Test de la substitution de feuilles de routes.
 *
 * @author jtremeaux
 */
@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class TestFeuilleRouteSubstitution {
    private static final String DEFAULT_ROUTE_NAME = "DefaultRouteModel";

    @Inject
    private CoreFeature coreFeature;

    @Inject
    private ReponseFeature reponseFeature;

    @Inject
    private DossierDistributionService dossierDistributionService;

    @Inject
    private CaseDistributionService caseDistributionService;

    private SSFeuilleRoute defaultRoute;

    @Before
    public void setUp() {
        ServiceUtil.getRequiredService(STParametreService.class).clearCache();
        reponseFeature.setFeuilleRootModelFolderId(null);
        Framework.getProperties().put("nuxeo.url", "http://localhost:8080/");
    }

    /**
     * Vérifie la substitution de feuilles de routes.
     *
     * @throws Exception
     */
    @Test
    public void testSubstitution() throws Exception {
        DocumentRef routeInstanceRef = null;
        DocumentRef routeRef = null;
        DocumentRef dossierRef = null;
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // Crée la feuille de route par défaut
            defaultRoute = createDefaultRoute(session);
            DocumentModel routeDoc = defaultRoute.getDocument();
            routeRef = routeDoc.getRef();

            // Valide la feuille de route
            reponseFeature.validateRoute(session, defaultRoute);

            // Cree un dossier
            Dossier dossier = reponseFeature.createDossier(session);
            DocumentModel dossierDoc = dossier.getDocument();
            dossierRef = dossierDoc.getRef();

            // Démarre la feuille de route associée au dossier
            FeuilleRoute routeInstance = dossierDistributionService.startDefaultRoute(session, dossier);
            DocumentModel routeInstanceDoc = routeInstance.getDocument();
            routeInstanceRef = routeInstanceDoc.getRef();
            Assert.assertEquals(
                "Instance de feuille de route initiale",
                routeInstanceDoc.getId(),
                dossier.getLastDocumentRoute()
            );
        }

        try (CloseableCoreSession session = coreFeature.openCoreSession(ReponseFeature.user1)) {
            // Récupère le CaseLink de user1

            Mailbox userMailbox = reponseFeature.getPersonalMailbox(session, ReponseFeature.user1);
            List<STDossierLink> links = caseDistributionService.getReceivedCaseLinks(session, userMailbox, 0, 0);
            Assert.assertEquals(1, links.size());
            // Substitue la feuille de route
        }
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel dossierDoc = session.getDocument(dossierRef);
            DocumentModel routeDoc = session.getDocument(routeRef);
            DocumentModel routeInstanceDoc = session.getDocument(routeInstanceRef);

            DocumentModel routeInstance2Doc = dossierDistributionService.substituerFeuilleRoute(
                session,
                dossierDoc,
                routeInstanceDoc,
                routeDoc,
                "substitution"
            );
            Assert.assertNotNull(routeInstance2Doc);
            dossierDistributionService.startRouteAfterSubstitution(
                session,
                routeInstanceDoc,
                routeInstance2Doc,
                "substitution"
            );

            dossierDoc.refresh();
            Dossier dossier = dossierDoc.getAdapter(Dossier.class);

            Assert.assertEquals(
                "Instance de feuille de route après substitution",
                routeInstance2Doc.getId(),
                dossier.getLastDocumentRoute()
            );
            Assert.assertNotSame(routeInstanceDoc.getId(), routeInstance2Doc.getId());
            Assert.assertNotSame(dossier.getLastDocumentRoute(), routeInstanceDoc.getId());
        }
    }

    /**
     * Vérifie l'ajout d'une feuille de route.
     *
     * @throws Exception
     */
    @Test
    public void testAjout() throws Exception {
        DocumentRef dossierRef = null;
        DocumentRef routeRef = null;
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // Crée la feuille de route par défaut
            defaultRoute = createDefaultRoute(session);
            DocumentModel routeDoc = defaultRoute.getDocument();
            routeRef = routeDoc.getRef();

            // Valide la feuille de route
            reponseFeature.validateRoute(session, defaultRoute);

            // Cree un dossier
            Dossier dossier = reponseFeature.createDossier(session);
            dossierRef = dossier.getDocument().getRef();
        }

        // Ajoute la feuille de route
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel dossierDoc = session.getDocument(dossierRef);
            DocumentModel routeDoc = session.getDocument(routeRef);

            DocumentModel routeInstance2Doc = dossierDistributionService.substituerFeuilleRoute(
                session,
                dossierDoc,
                null,
                routeDoc,
                "substitution"
            );
            Assert.assertNotNull(routeInstance2Doc);
            dossierDistributionService.startRouteAfterSubstitution(session, null, routeInstance2Doc, "substitution");

            dossierDoc.refresh();
            Dossier dossier = dossierDoc.getAdapter(Dossier.class);

            Assert.assertEquals(
                "Instance de feuille de route après substitution",
                routeInstance2Doc.getId(),
                dossier.getLastDocumentRoute()
            );
            Assert.assertNotSame(null, routeInstance2Doc.getId());
            Assert.assertNotSame(dossier.getLastDocumentRoute(), null);
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
            SSConstant.ROUTE_STEP_DOCUMENT_TYPE,
            route.getPathAsString()
        );
        Mailbox user1Mailbox = reponseFeature.getPersonalMailbox(session, ReponseFeature.user1);
        step1.setPropertyValue(CaseDistribConstants.STEP_DISTRIBUTION_MAILBOX_ID_PROPERTY_NAME, user1Mailbox.getId());
        session.saveDocument(step1);

        session.save();

        return route.getAdapter(SSFeuilleRoute.class);
    }
}
