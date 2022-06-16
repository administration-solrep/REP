package fr.dila.reponses.core.feuilleroute;

import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.service.ReponseFeuilleRouteService;
import fr.dila.reponses.core.ReponseFeature;
import fr.dila.ss.api.domain.feuilleroute.StepFolder;
import fr.dila.ss.core.helper.FeuilleRouteTestHelper;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteExecutionType;
import java.util.ArrayList;
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
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * Test de la recherche d'étape suivante de feuille de route.
 *
 * @author bgamard
 */
@RunWith(FeaturesRunner.class)
@Features(ReponseFeature.class)
public class TestFeuilleRouteFindNextStep {
    private static final Log LOG = LogFactory.getLog(TestFeuilleRouteFindNextStep.class);

    private static final String DEFAULT_ROUTE_NAME = "DefaultRouteModel";

    @Inject
    private CoreFeature coreFeature;

    @Inject
    private ReponseFeature reponseFeature;

    @Inject
    private ReponseFeuilleRouteService feuilleRouteService;

    /**
     * Identifiants des étapes de la feuille de route.
     */
    protected List<String> stepNameList;

    @Test
    public void testFindNextStep() throws Exception {
        LOG.debug("Creation de la feuille de route");
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel route = reponseFeature.createFeuilleRoute(session, DEFAULT_ROUTE_NAME);
            session.save();

            String user1MboxId = reponseFeature.getPersonalMailbox(session, ReponseFeature.user1).getId();
            String user2MboxId = reponseFeature.getPersonalMailbox(session, ReponseFeature.user2).getId();
            String user3MboxId = reponseFeature.getPersonalMailbox(session, ReponseFeature.user3).getId();

            DocumentModel step1 = FeuilleRouteTestHelper.createSerialStep(
                session,
                route,
                user3MboxId,
                "Pour attribution agents BDC",
                VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION
            );
            DocumentModel step2 = FeuilleRouteTestHelper.createSerialStep(
                session,
                route,
                user2MboxId,
                "Pour rédaction DLF",
                VocabularyConstants.ROUTING_TASK_TYPE_REDACTION
            );
            DocumentModel step3 = FeuilleRouteTestHelper.createSerialStep(
                session,
                route,
                user2MboxId,
                "Pour validation PM",
                VocabularyConstants.ROUTING_TASK_TYPE_VALIDATION_PM
            );
            DocumentModel step4 = FeuilleRouteTestHelper.createSerialStep(
                session,
                route,
                user1MboxId,
                "Pour attribution DGEFP",
                VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION
            );

            DocumentModel parallelContainer1 = createParallelContainer(session, route);
            DocumentModel serialContainer1 = createSerialContainer(session, parallelContainer1);
            DocumentModel serialContainer2 = createSerialContainer(session, parallelContainer1);

            DocumentModel step411 = FeuilleRouteTestHelper.createSerialStep(
                session,
                serialContainer1,
                user1MboxId,
                "Etape en parallèle 1.1",
                VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION
            );
            DocumentModel step412 = FeuilleRouteTestHelper.createSerialStep(
                session,
                serialContainer1,
                user3MboxId,
                "Etape en parallèle 1.2",
                VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION
            );
            DocumentModel step413 = FeuilleRouteTestHelper.createSerialStep(
                session,
                serialContainer1,
                user2MboxId,
                "Etape en parallèle 1.3",
                VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION
            );

            DocumentModel step421 = FeuilleRouteTestHelper.createSerialStep(
                session,
                serialContainer2,
                user1MboxId,
                "Etape en parallèle 2.1",
                VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION
            );
            DocumentModel step422 = FeuilleRouteTestHelper.createSerialStep(
                session,
                serialContainer2,
                user3MboxId,
                "Etape en parallèle 2.2",
                VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION
            );

            DocumentModel parallelContainer2 = createParallelContainer(session, serialContainer2);
            DocumentModel serialContainer3 = createSerialContainer(session, parallelContainer2);
            DocumentModel serialContainer4 = createSerialContainer(session, parallelContainer2);

            DocumentModel step42211 = FeuilleRouteTestHelper.createSerialStep(
                session,
                serialContainer3,
                user1MboxId,
                "Etape en parallèle 2.2.1.1",
                VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION
            );
            DocumentModel step42212 = FeuilleRouteTestHelper.createSerialStep(
                session,
                serialContainer3,
                user3MboxId,
                "Etape en parallèle 2.2.1.2",
                VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION
            );

            DocumentModel step42221 = FeuilleRouteTestHelper.createSerialStep(
                session,
                serialContainer4,
                user1MboxId,
                "Etape en parallèle 2.1.2.1",
                VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION
            );
            DocumentModel step42222 = FeuilleRouteTestHelper.createSerialStep(
                session,
                serialContainer4,
                user3MboxId,
                "Etape en parallèle 2.2.2.2",
                VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION
            );

            DocumentModel step5 = FeuilleRouteTestHelper.createSerialStep(
                session,
                route,
                user2MboxId,
                "Pour signature",
                VocabularyConstants.ROUTING_TASK_TYPE_SIGNATURE
            );
            DocumentModel step6 = FeuilleRouteTestHelper.createSerialStep(
                session,
                route,
                user3MboxId,
                "Pour transmission aux assemblées",
                VocabularyConstants.ROUTING_TASK_TYPE_TRANSMISSION_ASSEMBLEE
            );

            DocumentModel parallelContainer3 = createParallelContainer(session, route);
            DocumentModel serialContainer5 = createSerialContainer(session, parallelContainer3);
            DocumentModel serialContainer6 = createSerialContainer(session, parallelContainer3);

            DocumentModel step61 = FeuilleRouteTestHelper.createSerialStep(
                session,
                serialContainer5,
                user1MboxId,
                "Etape en parallèle 3.1",
                VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION
            );
            DocumentModel step62 = FeuilleRouteTestHelper.createSerialStep(
                session,
                serialContainer6,
                user3MboxId,
                "Etape en parallèle 3.2",
                VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION
            );

            DocumentModel step7 = FeuilleRouteTestHelper.createSerialStep(
                session,
                route,
                user2MboxId,
                "Pour information",
                VocabularyConstants.ROUTING_TASK_TYPE_INFORMATION
            );

            DocumentModel parallelContainer4 = createParallelContainer(session, route);
            DocumentModel serialContainer7 = createSerialContainer(session, parallelContainer4);
            createSerialContainer(session, parallelContainer4);

            DocumentModel step71 = FeuilleRouteTestHelper.createSerialStep(
                session,
                serialContainer7,
                user1MboxId,
                "Etape en parallèle 4.1",
                VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION
            );

            DocumentModel step8 = FeuilleRouteTestHelper.createSerialStep(
                session,
                route,
                user2MboxId,
                "Pour information",
                VocabularyConstants.ROUTING_TASK_TYPE_INFORMATION
            );

            createParallelContainer(session, route);
            session.save();
            LOG.debug("Fin de la creation de la feuille de route");

            DocumentModel nextStep = feuilleRouteService.findNextSteps(session, route.getId(), step1, null).get(0);
            Assert.assertEquals(step2.getId(), nextStep.getId());

            nextStep = feuilleRouteService.findNextSteps(session, route.getId(), step3, null).get(0);
            Assert.assertEquals(step4.getId(), nextStep.getId());

            nextStep = feuilleRouteService.findNextSteps(session, route.getId(), step412, null).get(0);
            Assert.assertEquals(step413.getId(), nextStep.getId());

            nextStep = feuilleRouteService.findNextSteps(session, route.getId(), step421, null).get(0);
            Assert.assertEquals(step422.getId(), nextStep.getId());

            nextStep = feuilleRouteService.findNextSteps(session, route.getId(), step413, null).get(0);
            Assert.assertEquals(step5.getId(), nextStep.getId());

            List<DocumentModel> nextSteps = feuilleRouteService.findNextSteps(session, route.getId(), step422, null);
            Assert.assertEquals(2, nextSteps.size());
            List<String> expectedIds = new ArrayList<>();
            expectedIds.add(step42211.getId());
            expectedIds.add(step42221.getId());
            for (DocumentModel stepDoc : nextSteps) {
                if (!expectedIds.contains(stepDoc.getId())) {
                    throw new AssertionError();
                }
            }

            nextSteps = feuilleRouteService.findNextSteps(session, route.getId(), step4, null);
            Assert.assertEquals(2, nextSteps.size());
            expectedIds = new ArrayList<>();
            expectedIds.add(step411.getId());
            expectedIds.add(step421.getId());
            for (DocumentModel stepDoc : nextSteps) {
                if (!expectedIds.contains(stepDoc.getId())) {
                    throw new AssertionError();
                }
            }

            nextStep = feuilleRouteService.findNextSteps(session, route.getId(), step42211, null).get(0);
            Assert.assertEquals(step42212.getId(), nextStep.getId());

            nextStep = feuilleRouteService.findNextSteps(session, route.getId(), step42221, null).get(0);
            Assert.assertEquals(step42222.getId(), nextStep.getId());

            nextStep = feuilleRouteService.findNextSteps(session, route.getId(), step42212, null).get(0);
            Assert.assertEquals(step5.getId(), nextStep.getId());

            nextStep = feuilleRouteService.findNextSteps(session, route.getId(), step42222, null).get(0);
            Assert.assertEquals(step5.getId(), nextStep.getId());

            nextSteps = feuilleRouteService.findNextSteps(session, route.getId(), step6, null);
            Assert.assertEquals(2, nextSteps.size());
            expectedIds = new ArrayList<>();
            expectedIds.add(step61.getId());
            expectedIds.add(step62.getId());
            for (DocumentModel stepDoc : nextSteps) {
                if (!expectedIds.contains(stepDoc.getId())) {
                    throw new AssertionError();
                }
            }

            nextStep = feuilleRouteService.findNextSteps(session, route.getId(), step61, null).get(0);
            Assert.assertEquals(step7.getId(), nextStep.getId());

            nextStep = feuilleRouteService.findNextSteps(session, route.getId(), step62, null).get(0);
            Assert.assertEquals(step7.getId(), nextStep.getId());

            nextStep = feuilleRouteService.findNextSteps(session, route.getId(), step7, null).get(0);
            Assert.assertEquals(step71.getId(), nextStep.getId());

            nextStep = feuilleRouteService.findNextSteps(session, route.getId(), step71, null).get(0);
            Assert.assertEquals(step8.getId(), nextStep.getId());

            Assert.assertEquals(0, feuilleRouteService.findNextSteps(session, route.getId(), step8, null).size());
        }
    }

    private DocumentModel createParallelContainer(CoreSession session, DocumentModel parent) throws Exception {
        DocumentModel stepFolderDoc = reponseFeature.createDocument(
            session,
            "parallel",
            FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP_FOLDER,
            parent.getPathAsString()
        );
        StepFolder stepFolder = stepFolderDoc.getAdapter(StepFolder.class);
        stepFolder.setExecution(FeuilleRouteExecutionType.parallel);
        session.saveDocument(stepFolderDoc);
        return stepFolderDoc;
    }

    private DocumentModel createSerialContainer(CoreSession session, DocumentModel parent) throws Exception {
        DocumentModel stepFolderDoc = reponseFeature.createDocument(
            session,
            "serial",
            FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP_FOLDER,
            parent.getPathAsString()
        );
        StepFolder stepFolder = stepFolderDoc.getAdapter(StepFolder.class);
        stepFolder.setExecution(FeuilleRouteExecutionType.serial);
        session.saveDocument(stepFolderDoc);
        return stepFolderDoc;
    }
}
