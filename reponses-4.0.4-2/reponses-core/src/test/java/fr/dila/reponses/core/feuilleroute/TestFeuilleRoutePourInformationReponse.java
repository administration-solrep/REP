package fr.dila.reponses.core.feuilleroute;

import fr.dila.cm.caselink.ActionableCaseLink;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.service.CaseDistributionService;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.service.DocumentRoutingService;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.api.service.FeuilleRouteModelService;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.core.helper.FeuilleRouteTestHelper;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.api.service.STParametreService;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.exception.FeuilleRouteAlreadyLockedException;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.exception.FeuilleRouteNotLockedException;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class TestFeuilleRoutePourInformationReponse {
    private static final String DEFAULT_ROUTE_NAME = "DefaultRouteModel";

    @Inject
    private CoreFeature coreFeature;

    @Inject
    private ReponseFeature reponseFeature;

    @Inject
    private FeuilleRouteModelService feuilleRouteModelService;

    @Inject
    private DossierDistributionService dossierDistributionService;

    @Inject
    private CaseDistributionService distributionService;

    @Inject
    private DocumentRoutingService documentRoutingService;

    @Before
    public void setup() {
        ServiceUtil.getRequiredService(STParametreService.class).clearCache();
    }

    // Une feuille de route qui ressemble un plus à celle utilisée par réponse
    private SSFeuilleRoute createFeuilleRoute(CoreSession session) throws Exception {
        final DocumentModel route = reponseFeature.createFeuilleRoute(session, DEFAULT_ROUTE_NAME);
        session.save();

        final String user1MboxId = reponseFeature.getPersonalMailbox(session, ReponseFeature.user1).getId();
        final String user2MboxId = reponseFeature.getPersonalMailbox(session, ReponseFeature.user2).getId();
        final String user3MboxId = reponseFeature.getPersonalMailbox(session, ReponseFeature.user3).getId();

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
            "Pour Information DLF",
            VocabularyConstants.ROUTING_TASK_TYPE_INFORMATION
        );
        FeuilleRouteTestHelper.createSerialStep(
            session,
            route,
            user3MboxId,
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

    private void createAndValidateFdr()
        throws Exception, FeuilleRouteAlreadyLockedException, FeuilleRouteNotLockedException {
        DocumentRef feuilleRouteRef;
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // Crée la feuille de route par défaut
            SSFeuilleRoute feuille_de_route_reponse = createFeuilleRoute(session);
            feuilleRouteRef = feuille_de_route_reponse.getDocument().getRef();
            // Valide la feuille de route
            documentRoutingService.lockDocumentRoute(feuille_de_route_reponse, session);
            feuille_de_route_reponse = documentRoutingService.validateRouteModel(feuille_de_route_reponse, session);
            session.saveDocument(feuille_de_route_reponse.getDocument());
            session.save();
            documentRoutingService.unlockDocumentRoute(feuille_de_route_reponse, session);
        }
        coreFeature.waitForAsyncCompletion();

        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel feuilleRouteDoc = session.getDocument(feuilleRouteRef);
            Assert.assertEquals("validated", feuilleRouteDoc.getCurrentLifeCycleState());
            Assert.assertEquals("validated", session.getChildren(feuilleRouteRef).get(0).getCurrentLifeCycleState());
        }
    }

    private void validateUserTask(String user) throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession(user)) {
            Mailbox userMailbox = reponseFeature.getPersonalMailbox(session, user);
            List<STDossierLink> links = distributionService.getReceivedCaseLinks(session, userMailbox, 0, 0);
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

    @Test
    public void testValidationPourInformationStep() throws Exception {
        createAndValidateFdr();
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            FeuilleRoute defaultRoute = feuilleRouteModelService.getDefaultRouteQuestion(session);
            Assert.assertNotNull(defaultRoute);

            Dossier dossier = createDossier(session);
            session.saveDocument(dossier.getDocument());

            // Démarre la feuille de route associée au dossier
            dossierDistributionService.startDefaultRoute(session, dossier);
        }
        // validation de l'étape 1
        reponseFeature.verifyCaseLinkPresent(ReponseFeature.user1, true);

        validateUserTask(ReponseFeature.user1);

        // la fdr doit maintenant être à l'étape 3
        reponseFeature.verifyCaseLinkPresent(ReponseFeature.user3, true);
    }
}
