package fr.dila.reponses.core;

import static fr.dila.st.api.constant.STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE;

import com.google.inject.Binder;
import fr.dila.cm.cases.CaseConstants;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.mailbox.MailboxConstants;
import fr.dila.cm.service.CaseDistributionService;
import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.feuilleroute.ReponsesFeuilleRoute;
import fr.dila.reponses.api.service.DocumentRoutingService;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.api.service.ReponsesMailboxService;
import fr.dila.reponses.core.mock.MockFeuilleRouteModelService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.core.test.STAuditFeature;
import fr.dila.st.core.test.STCommonFeature;
import fr.sword.naiad.nuxeo.commons.core.schema.DublincorePropertyUtil;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.BlacklistComponent;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.RunnerFeature;

@Features({ STAuditFeature.class, STCommonFeature.class })
@Deploy("fr.dila.ss.api")
@Deploy("fr.dila.ss.core")
@Deploy("fr.dila.reponses.api")
@Deploy("fr.dila.reponses.core")
@Deploy("fr.dila.reponses.core.tests:OSGI-INF/test-event-contrib.xml")
@Deploy("fr.dila.reponses.core.tests:OSGI-INF/test-feuille-route-ecm-type-contrib.xml")
@Deploy("fr.dila.reponses.core.tests:OSGI-INF/test-vocabulary-contrib.xml")
@Deploy("fr.dila.reponses.core.tests:OSGI-INF/service/test-feuille-route-model-framework.xml")
@BlacklistComponent({ "fr.dila.st.core.datasources.contrib" })
public class ReponseFeature implements RunnerFeature {
    public static final String user = "user";

    public static final String user1 = "user1";

    public static final String user2 = "user2";

    public static final String user3 = "user3";

    public static final String FeuilleRootModelFolderName = "fdrmodelroot";

    private String feuilleRootModelFolderId = null;

    private CoreFeature coreFeature;

    public static final String ID_MINISTERE_INTERROGE = "6000002";

    public ReponseFeature() {
        // do nothing
    }

    @Override
    public void beforeSetup(FeaturesRunner runner) {
        Framework.getProperties().put("nuxeo.url", "http://test/");

        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModelList res = session.query(
                String.format("SELECT * from %s", MailboxConstants.MAILBOX_ROOT_DOCUMENT_TYPE)
            );
            if (res == null || res.isEmpty()) {
                DocumentModel doc = session.createDocumentModel(
                    "/",
                    "mailboxroot",
                    MailboxConstants.MAILBOX_ROOT_DOCUMENT_TYPE
                );
                session.createDocument(doc);
                session.save();
            }
            if (!session.exists(new PathRef(CaseConstants.CASE_ROOT_DOCUMENT_PATH))) {
                createDocument(session, "case-root", "CaseRoot", "/case-management");
                session.save();
            }
        }
    }

    @Override
    public void configure(FeaturesRunner runner, Binder binder) {
        coreFeature = runner.getFeature(CoreFeature.class);
    }

    public static DocumentModel createDocument(CoreSession session, String type, String id) {
        DocumentModel document = session.createDocumentModel("/", id, type);
        return session.createDocument(document);
    }

    public DossierLink createDossierLink(CoreSession session, String name) {
        DocumentModel dossierLinkDoc = session.createDocumentModel("/", name, DOSSIER_LINK_DOCUMENT_TYPE);
        //        PropertyUtil.setProperty(dossierLinkDoc, CASE_LINK_SCHEMA, "caseDocumentId", createDossier(session).getDocument().getId());
        return session.createDocument(dossierLinkDoc).getAdapter(DossierLink.class);
    }

    public DocumentModel createDocument(CoreSession session, String name, String type, String path) {
        DocumentModel doc = session.createDocumentModel(path, name, type);
        DublincorePropertyUtil.setTitle(doc, name);
        return session.createDocument(doc);
    }

    public Dossier createDossier(CoreSession session) {
        return createDossier(session, 15523L, null);
    }

    public Dossier createDossier(CoreSession session, Long numQuestionLong, String origineQuestion) {
        DossierDistributionService dossierDistributionService = ServiceUtil.getRequiredService(
            DossierDistributionService.class
        );

        DocumentModel questionDocumentModel = createDocument(
            session,
            DossierConstants.QUESTION_DOCUMENT_TYPE,
            "newQuestionTest"
        );
        Question question = questionDocumentModel.getAdapter(Question.class);
        DocumentModel dossierDocumentModel = createDocument(
            session,
            DossierConstants.DOSSIER_DOCUMENT_TYPE,
            "newDossierTest"
        );

        // check properties
        String typeQuestion = VocabularyConstants.QUESTION_TYPE_QE;
        String texteQuestion = "quelle heure est il ?";
        Calendar date = GregorianCalendar.getInstance();
        String nomAuteur = "Valjean";
        String prenomAuteur = "Jean";

        question.setNumeroQuestion(numQuestionLong);
        question.setOrigineQuestion(origineQuestion);
        question.setTypeQuestion(typeQuestion);
        question.setEtatQuestion(session, VocabularyConstants.ETAT_QUESTION_EN_COURS, new GregorianCalendar(), "10");
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

        Assert.assertNotNull(dossier);
        Assert.assertNotNull(dossier.getDocument().getId());
        Assert.assertEquals(dossier.getQuestion(session).getTypeQuestion(), typeQuestion);
        Assert.assertEquals(dossier.getQuestion(session).getOrigineQuestion(), origineQuestion);
        Assert.assertEquals(dossier.getQuestion(session).getNumeroQuestion(), numQuestionLong);

        return dossier;
    }

    /**
     * Valide une feuille de route.
     *
     * @param route
     *            Feuille de route
     */
    public void validateRoute(CoreSession session, SSFeuilleRoute route) {
        DocumentRoutingService routingService = ServiceUtil.getRequiredService(DocumentRoutingService.class);
        routingService.lockDocumentRoute(route, session);
        route = routingService.validateRouteModel(route, session);
        session.saveDocument(route.getDocument());
        session.save();
        routingService.unlockDocumentRoute(route, session);

        //		ServiceUtil.getRequiredService(EventService.class).waitForAsyncCompletion();

        Assert.assertEquals("validated", route.getDocument().getCurrentLifeCycleState());
        DocumentModelList docModelList = session.getChildren(route.getDocument().getRef());
        if (docModelList.size() > 0) {
            Assert.assertEquals("validated", docModelList.get(0).getCurrentLifeCycleState());
        }
    }

    public Mailbox getPersonalMailbox(CoreSession session, String name) {
        final ReponsesMailboxService correspMailboxService = ServiceUtil.getRequiredService(
            ReponsesMailboxService.class
        );

        final Mailbox mailbox = correspMailboxService.getUserPersonalMailbox(session, name);
        if (mailbox == null) {
            UserManager userManager = ServiceUtil.getRequiredService(UserManager.class);
            DocumentModel userModel = userManager.getUserModel(name);
            return correspMailboxService.createPersonalMailboxes(session, userModel);
        } else {
            return mailbox;
        }
    }

    /**
     * Vérifie la présence d'un CaseLink dans la mailbox d'un utilisateur.
     *
     * @param user
     *            Nom de l'utilisateur
     * @param actionnable
     *            Vrai si le caselink doit être actionnable
     * @throws Exception
     *             Exception
     */
    public void verifyCaseLinkPresent(String user, boolean actionnable) throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession(user)) {
            List<STDossierLink> links = getReceivedCaseLinksNotDeleted(session, user);

            Assert.assertEquals(
                "Présence d'un CaseLink dans la mailbox de l'utilisateur <" + user + ">",
                1,
                links.size()
            );

            STDossierLink link = links.get(0);

            if (actionnable) {
                Assert.assertTrue(link.isActionnable());
            } else {
                Assert.assertFalse(link.isActionnable());
            }
        }
    }

    /**
     * Vérifie l'absence d'un CaseLink dans la mailbox d'un utilisateur.
     *
     * @param user
     *            Nom de l'utilisateur
     * @throws Exception
     *             Exception
     */
    public void verifyCaseLinkAbsent(String user) throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession(user)) {
            List<STDossierLink> links = getReceivedCaseLinksNotDeleted(session, user);
            Assert.assertEquals("Absence de CaseLink dans la mailbox de l'utilisateur <" + user + ">", 0, links.size());
        }
    }

    public List<STDossierLink> getReceivedCaseLinksNotDeleted(CoreSession session, String user) throws Exception {
        Mailbox userMailbox = getPersonalMailbox(session, user);
        CaseDistributionService distributionService = ServiceUtil.getRequiredService(CaseDistributionService.class);
        List<STDossierLink> links = distributionService.getReceivedCaseLinks(session, userMailbox, 0, 0);
        List<STDossierLink> linkNotDeleted = new ArrayList<>();
        for (STDossierLink l : links) {
            if (!l.getDocument().isTrashed()) {
                linkNotDeleted.add(l);
            }
        }
        return linkNotDeleted;
    }

    /**
     * Crée si ce n'est pas deja fait le dossier qui va contenir les modeles de feuilles de root Les modeles de feuilles
     * de route sont recherche comme enfant d'un document unique de type FeuilleRouteModelFolder
     *
     * @param session
     * @return
     *
     */
    public DocumentModel createOrGetFeuilleRouteModelFolder(CoreSession session) {
        if (feuilleRootModelFolderId == null) {
            DocumentModel feuilleRootModelFolder = createDocument(
                session,
                FeuilleRootModelFolderName,
                "FeuilleRouteModelFolder",
                "/"
            );
            feuilleRootModelFolderId = feuilleRootModelFolder.getId();

            (
                (MockFeuilleRouteModelService) ReponsesServiceLocator.getFeuilleRouteModelService()
            ).setFeuilleRouteModelFolderDocId(feuilleRootModelFolder.getId());

            return feuilleRootModelFolder;
        } else {
            return session.getDocument(new IdRef(feuilleRootModelFolderId));
        }
    }

    public String getFeuilleRootModelFolderId() {
        return feuilleRootModelFolderId;
    }

    public DocumentModel createFeuilleRoute(CoreSession session, String name) {
        DocumentModel fdrRoot = createOrGetFeuilleRouteModelFolder(session);
        DocumentModel route = createDocument(
            session,
            name,
            SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE,
            fdrRoot.getPathAsString()
        );
        ReponsesFeuilleRoute feuilleRoute = route.getAdapter(ReponsesFeuilleRoute.class);
        Assert.assertNotNull(feuilleRoute);
        feuilleRoute.setFeuilleRouteDefaut(true);
        route = session.saveDocument(feuilleRoute.getDocument());
        return route;
    }

    public void setFeuilleRootModelFolderId(String feuilleRootModelFolderId) {
        this.feuilleRootModelFolderId = feuilleRootModelFolderId;
    }

    public static SSPrincipal newMockSSPrincipal() {
        SSPrincipal mockPrincipal = Mockito.mock(SSPrincipal.class);

        Mockito
            .when(mockPrincipal.getMinistereIdSet())
            .thenAnswer(
                (Answer<Set<String>>) invocation -> {
                    Set<String> ministereIdSet = new HashSet<>();
                    ministereIdSet.add("502");
                    ministereIdSet.add("503");
                    return ministereIdSet;
                }
            );

        Mockito.when(mockPrincipal.isAdministrator()).thenReturn(true);

        return mockPrincipal;
    }
}
