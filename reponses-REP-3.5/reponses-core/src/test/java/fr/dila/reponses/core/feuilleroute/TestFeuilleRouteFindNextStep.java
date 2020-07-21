package fr.dila.reponses.core.feuilleroute;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.service.FeuilleRouteModelService;
import fr.dila.reponses.api.service.FeuilleRouteService;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.ss.api.domain.feuilleroute.StepFolder;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STSchemaConstant;

/**
 * Test de la recherche d'étape suivante de feuille de route.
 * 
 * @author bgamard
 */
public class TestFeuilleRouteFindNextStep extends ReponsesRepositoryTestCase {
	
	private static final Log LOG = LogFactory.getLog(TestFeuilleRouteFindNextStep.class);
	
    private static final String DEFAULT_ROUTE_NAME = "DefaultRouteModel";

    private FeuilleRouteModelService feuilleRouteModelService;

    private FeuilleRouteService feuilleRouteService;


    
    /**
     * Identifiants des étapes de la feuille de route.
     */
    protected List<String> stepNameList;

    @Override
    public void setUp() throws Exception {
    	LOG.debug("ENTER TEST SETUP");
    	
        super.setUp();

        feuilleRouteModelService = ReponsesServiceLocator.getFeuilleRouteModelService();
        assertNotNull(feuilleRouteModelService);

        feuilleRouteService = ReponsesServiceLocator.getFeuilleRouteService();
        assertNotNull(feuilleRouteService);
        
        LOG.debug("EXIT TEST SETUP");
    }

    public void testFindNextStep() throws Exception {
    	LOG.debug("Creation de la feuille de route");
    	openSession();
    	
        DocumentModel route = createFeuilleRoute(session, DEFAULT_ROUTE_NAME);
        session.save();

        String user1MboxId = getPersonalMailbox(user1).getId();
        String user2MboxId = getPersonalMailbox(user2).getId();
        String user3MboxId = getPersonalMailbox(user3).getId();
        
        DocumentModel step1 = createSerialStep(session, route, user3MboxId, "Pour attribution agents BDC", VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION);
        DocumentModel step2 = createSerialStep(session, route, user2MboxId, "Pour rédaction DLF", VocabularyConstants.ROUTING_TASK_TYPE_REDACTION);
        DocumentModel step3 = createSerialStep(session, route, user2MboxId, "Pour validation PM", VocabularyConstants.ROUTING_TASK_TYPE_VALIDATION_PM);
        DocumentModel step4 = createSerialStep(session, route, user1MboxId, "Pour attribution DGEFP", VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION);
        
        DocumentModel parallelContainer1 = createParallelContainer(session, route);
        DocumentModel serialContainer1 = createSerialContainer(session, parallelContainer1);
        DocumentModel serialContainer2 = createSerialContainer(session, parallelContainer1);
        
        DocumentModel step411 = createSerialStep(session, serialContainer1, user1MboxId, "Etape en parallèle 1.1", VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION);
        DocumentModel step412 = createSerialStep(session, serialContainer1, user3MboxId, "Etape en parallèle 1.2", VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION);
        DocumentModel step413 = createSerialStep(session, serialContainer1, user2MboxId, "Etape en parallèle 1.3", VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION);
        
        DocumentModel step421 = createSerialStep(session, serialContainer2, user1MboxId, "Etape en parallèle 2.1", VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION);
        DocumentModel step422 = createSerialStep(session, serialContainer2, user3MboxId, "Etape en parallèle 2.2", VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION);
        
        DocumentModel parallelContainer2 = createParallelContainer(session, serialContainer2);
        DocumentModel serialContainer3 = createSerialContainer(session, parallelContainer2);
        DocumentModel serialContainer4 = createSerialContainer(session, parallelContainer2);
        
        DocumentModel step42211 = createSerialStep(session, serialContainer3, user1MboxId, "Etape en parallèle 2.2.1.1", VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION);
        DocumentModel step42212 = createSerialStep(session, serialContainer3, user3MboxId, "Etape en parallèle 2.2.1.2", VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION);
        
        DocumentModel step42221 = createSerialStep(session, serialContainer4, user1MboxId, "Etape en parallèle 2.1.2.1", VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION);
        DocumentModel step42222 = createSerialStep(session, serialContainer4, user3MboxId, "Etape en parallèle 2.2.2.2", VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION);
        
        
        DocumentModel step5 = createSerialStep(session, route, user2MboxId, "Pour signature", VocabularyConstants.ROUTING_TASK_TYPE_SIGNATURE);
        DocumentModel step6 = createSerialStep(session, route, user3MboxId, "Pour transmission aux assemblées", VocabularyConstants.ROUTING_TASK_TYPE_TRANSMISSION_ASSEMBLEE);
        
        DocumentModel parallelContainer3 = createParallelContainer(session, route);
        DocumentModel serialContainer5 = createSerialContainer(session, parallelContainer3);
        DocumentModel serialContainer6 = createSerialContainer(session, parallelContainer3);
        
        DocumentModel step61 = createSerialStep(session, serialContainer5, user1MboxId, "Etape en parallèle 3.1", VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION);
        DocumentModel step62 = createSerialStep(session, serialContainer6, user3MboxId, "Etape en parallèle 3.2", VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION);
        
        DocumentModel step7 = createSerialStep(session, route, user2MboxId, "Pour information", VocabularyConstants.ROUTING_TASK_TYPE_INFORMATION);
        
        DocumentModel parallelContainer4 = createParallelContainer(session, route);
        DocumentModel serialContainer7 = createSerialContainer(session, parallelContainer4);
        createSerialContainer(session, parallelContainer4);
        
        DocumentModel step71 = createSerialStep(session, serialContainer7, user1MboxId, "Etape en parallèle 4.1", VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION);
        
        DocumentModel step8 = createSerialStep(session, route, user2MboxId, "Pour information", VocabularyConstants.ROUTING_TASK_TYPE_INFORMATION);
        
        createParallelContainer(session, route);
        session.save();
        LOG.debug("Fin de la creation de la feuille de route");
        
        
        DocumentModel nextStep = feuilleRouteService.findNextSteps(session, route.getId(), step1, null).get(0);
        assertEquals(step2.getId(), nextStep.getId());
        
        nextStep = feuilleRouteService.findNextSteps(session, route.getId(), step3, null).get(0);
        assertEquals(step4.getId(), nextStep.getId());
        
        nextStep = feuilleRouteService.findNextSteps(session, route.getId(), step412, null).get(0);
        assertEquals(step413.getId(), nextStep.getId());
        
        nextStep = feuilleRouteService.findNextSteps(session, route.getId(), step421, null).get(0);
        assertEquals(step422.getId(), nextStep.getId());
        
        nextStep = feuilleRouteService.findNextSteps(session, route.getId(), step413, null).get(0);
        assertEquals(step5.getId(), nextStep.getId());
        
        List<DocumentModel> nextSteps = feuilleRouteService.findNextSteps(session, route.getId(), step422, null);
        assertEquals(2, nextSteps.size());
        List<String> expectedIds = new ArrayList<String>();
        expectedIds.add(step42211.getId());
        expectedIds.add(step42221.getId());
        for(DocumentModel stepDoc : nextSteps) {
            if(!expectedIds.contains(stepDoc.getId())) {
                throw new AssertionError();
            }
        }
        
        nextSteps = feuilleRouteService.findNextSteps(session, route.getId(), step4, null);
        assertEquals(2, nextSteps.size());
        expectedIds = new ArrayList<String>();
        expectedIds.add(step411.getId());
        expectedIds.add(step421.getId());
        for(DocumentModel stepDoc : nextSteps) {
            if(!expectedIds.contains(stepDoc.getId())) {
                throw new AssertionError();
            }
        }
        
        nextStep = feuilleRouteService.findNextSteps(session, route.getId(), step42211, null).get(0);
        assertEquals(step42212.getId(), nextStep.getId());
        
        nextStep = feuilleRouteService.findNextSteps(session, route.getId(), step42221, null).get(0);
        assertEquals(step42222.getId(), nextStep.getId());
        
        nextStep = feuilleRouteService.findNextSteps(session, route.getId(), step42212, null).get(0);
        assertEquals(step5.getId(), nextStep.getId());
        
        nextStep = feuilleRouteService.findNextSteps(session, route.getId(), step42222, null).get(0);
        assertEquals(step5.getId(), nextStep.getId());
        
        nextSteps = feuilleRouteService.findNextSteps(session, route.getId(), step6, null);
        assertEquals(2, nextSteps.size());
        expectedIds = new ArrayList<String>();
        expectedIds.add(step61.getId());
        expectedIds.add(step62.getId());
        for(DocumentModel stepDoc : nextSteps) {
            if(!expectedIds.contains(stepDoc.getId())) {
                throw new AssertionError();
            }
        }
        
        nextStep = feuilleRouteService.findNextSteps(session, route.getId(), step61, null).get(0);
        assertEquals(step7.getId(), nextStep.getId());
        
        nextStep = feuilleRouteService.findNextSteps(session, route.getId(), step62, null).get(0);
        assertEquals(step7.getId(), nextStep.getId());
        
        nextStep = feuilleRouteService.findNextSteps(session, route.getId(), step7, null).get(0);
        assertEquals(step71.getId(), nextStep.getId());
        
        nextStep = feuilleRouteService.findNextSteps(session, route.getId(), step71, null).get(0);
        assertEquals(step8.getId(), nextStep.getId());
        
        assertEquals(0, feuilleRouteService.findNextSteps(session, route.getId(), step8, null).size());
        
        closeSession();
    }

    
    private DocumentModel createParallelContainer(CoreSession session, DocumentModel parent) throws Exception {
        DocumentModel stepFolderDoc = createDocumentModel(session, "parallel", STConstant.STEP_FOLDER_DOCUMENT_TYPE, parent.getPathAsString());
        StepFolder stepFolder = stepFolderDoc.getAdapter(StepFolder.class);
        stepFolder.setExecution(STSchemaConstant.STEP_FOLDER_EXECUTION_PARALLEL_VALUE);
        session.saveDocument(stepFolderDoc);
        return stepFolderDoc;
    }
    
    private DocumentModel createSerialContainer(CoreSession session, DocumentModel parent) throws Exception {
        DocumentModel stepFolderDoc = createDocumentModel(session, "serial", STConstant.STEP_FOLDER_DOCUMENT_TYPE, parent.getPathAsString());
        StepFolder stepFolder = stepFolderDoc.getAdapter(StepFolder.class);
        stepFolder.setExecution(STSchemaConstant.STEP_FOLDER_EXECUTION_SERIAL_VALUE);
        session.saveDocument(stepFolderDoc);
        return stepFolderDoc;
    }
}
