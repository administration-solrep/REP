package fr.dila.reponses.core.feuilleroute;

import java.util.Arrays;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.runtime.api.Framework;


import fr.dila.cm.cases.CaseConstants;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.ecm.platform.routing.api.DocumentRoute;
import fr.dila.ecm.platform.routing.api.DocumentRoutingConstants;
import fr.dila.ecm.platform.routing.api.DocumentRouteElement.ElementLifeCycleState;
import fr.dila.reponses.api.feuilleroute.ReponsesFeuilleRoute;
import fr.dila.reponses.api.service.FeuilleRouteService;
import fr.dila.reponses.core.ReponsesRepositoryTestCase;
import fr.dila.reponses.core.service.DocumentRoutingServiceImpl;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.feuilleroute.DocumentRouteTableElement;
import fr.dila.st.api.feuilleroute.STRouteStep;

/**
 * Test du copier / coller d'étapes dans les feuilles de route.
 * 
 * On utilise la route suivante comme modèle :
 * E1  Pour initialisation
 * CP1
 *   E2  Pour attribution
 *   CS1
 *     E3  Pour avis
 *     CP2
 *        E4 Pour impression
 *        E5 Pour attribution
 * E6 Pour attribution
 * E7 Pour attribution
 * E8 Pour publication
 * 
 * @author jtremeaux
 * @author jgomez : Ajout du test sur la dénormalisation des étapes de la feuille de route - 2011/11/04
 * 
 */
public class TestPasteRouteStep extends ReponsesRepositoryTestCase  {
    private static final String FEUILLE_ROUTE_COPY_NAME = "FeuilleRouteCopy";

    private FeuilleRouteService feuilleRouteService;
    
    private DocumentRoute copyRoute;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        feuilleRouteService = ReponsesServiceLocator.getFeuilleRouteService();
        assertNotNull(feuilleRouteService);
        

        // Crée la feuille de route à copier
        openSession();
        copyRoute = createCopyRoute(session);
        session.save();
        closeSession();        
    }

    /**
     * Test copie à plat dans une route vide.
     * Collage d'un conteneur série dans un conteneur série -> Aucun conteneur n'est créé
     * 
     * @throws Exception
     */
    public void testPasteIntoFlat() throws Exception {
    	openSession();
    	
        // Crée une feuille de route vide
        DocumentRoute pasteRoute = createPasteRoute(session, "FeuilleRoutePasteIntoFlat");
        DocumentModel pasteRouteDoc = pasteRoute.getDocument();
        
        // Sélectionne les éléments à copier
        List<DocumentRouteTableElement> copyTable = getRoutingService().getFeuilleRouteElements(copyRoute, session);
        assertEquals(copyTable.size(), 8);
        DocumentModel copyE1 = copyTable.get(0).getDocument();
        DocumentModel copyE6 = copyTable.get(5).getDocument();
        DocumentModel copyE7 = copyTable.get(6).getDocument();
        DocumentModel copyE8 = copyTable.get(7).getDocument();
        
        String copyRouteId = copyRoute.getDocument().getId();
        checkRouteElement(copyTable.get(0), "E1", 0, ElementLifeCycleState.validated.name(),copyRouteId);
        checkRouteElement(copyTable.get(5), "E6", 0, ElementLifeCycleState.validated.name(),copyRouteId);
        checkRouteElement(copyTable.get(6), "E7", 0, ElementLifeCycleState.validated.name(),copyRouteId);
        checkRouteElement(copyTable.get(7), "E8", 0, ElementLifeCycleState.validated.name(),copyRouteId);
        
        List<DocumentModel> list = Arrays.asList(copyE1, copyE6, copyE7, copyE8);
        
        // Vérifie que l'attribut "obligatoire" est positionné
        STRouteStep copyE1Step = copyTable.get(0).getRouteStep();
        assertTrue(copyE1Step.isObligatoireSGG());
        assertTrue(copyE1Step.isObligatoireMinistere());
        
        // Colle les éléments
        getRoutingService().lockDocumentRoute(pasteRoute, session);
        getRoutingService().pasteRouteStepIntoRoute(session, pasteRouteDoc, pasteRouteDoc, null, false, list);
        getRoutingService().unlockDocumentRoute(pasteRoute, session);
        
        // Vérifie les éléments collés
        List<DocumentRouteTableElement> pasteTable = getRoutingService().getFeuilleRouteElements(pasteRoute, session);
        assertEquals(4, pasteTable.size());
        
        String pasteRouteId = pasteRoute.getDocument().getId();
        checkRouteElement(pasteTable.get(0), "E1", 0, ElementLifeCycleState.draft.name(),pasteRouteId);
        checkRouteElement(pasteTable.get(1), "E6", 0, ElementLifeCycleState.draft.name(),pasteRouteId);
        checkRouteElement(pasteTable.get(2), "E7", 0, ElementLifeCycleState.draft.name(),pasteRouteId);
        checkRouteElement(pasteTable.get(3), "E8", 0, ElementLifeCycleState.draft.name(),pasteRouteId);
        
        // Vérifie que le copier / coller garde les attributs "obligatoire"
        STRouteStep pasteE1Step = pasteTable.get(0).getRouteStep();
        assertTrue(pasteE1Step.isObligatoireSGG());
        // La copie de l'attribut dépend des rôles FEUILLE_DE_ROUTE_MODEL_VALIDATOR et FEUILLE_DE_ROUTE_MODEL_UDPATER
        assertFalse(pasteE1Step.isObligatoireMinistere());
        
        closeSession();
    }

    /**
     * Test copie à plat dans une route vide.
     * Collage d'un conteneur parallèle dans un conteneur série -> Un conteneur est créé.
     * 
     * @throws Exception
     */
    public void testPasteIntoNested() throws Exception {
    	openSession();
    	
        // Crée une feuille de route vide
        DocumentRoute pasteRoute = createPasteRoute(session, "FeuilleRoutePasteIntoNested");
        DocumentModel pasteRouteDoc = pasteRoute.getDocument();
        
        // Sélectionne les éléments à copier
        List<DocumentRouteTableElement> copyTable = getRoutingService().getFeuilleRouteElements(copyRoute, session);
        assertEquals(copyTable.size(), 8);
        DocumentModel copyE4 = copyTable.get(3).getDocument();
        DocumentModel copyE5 = copyTable.get(4).getDocument();
        
        List<DocumentModel> list = Arrays.asList(copyE4, copyE5);
        
        // Colle les éléments
        getRoutingService().lockDocumentRoute(pasteRoute, session);
        getRoutingService().pasteRouteStepIntoRoute(session, pasteRouteDoc, pasteRouteDoc, null, false, list);
        getRoutingService().unlockDocumentRoute(pasteRoute, session);
        
        // Vérifie les éléments collés
        List<DocumentRouteTableElement> pasteTable = getRoutingService().getFeuilleRouteElements(pasteRoute, session);
        assertEquals(2, pasteTable.size());
        
        String pasteRouteId = pasteRoute.getDocument().getId();
        checkRouteElement(pasteTable.get(0), "E4", 1, ElementLifeCycleState.draft.name(),pasteRouteId);
        checkRouteElement(pasteTable.get(1), "E5", 1, ElementLifeCycleState.draft.name(),pasteRouteId);
        
        closeSession();
    }

    /**
     * Test de la copie d'étapes avant / après une autre étape.
     * On vérifie la position relative des étapes collées, par rapport à l'étape "pivot".
     * On vérifie aussi la position relative des étapes collées relativement aux autres étapes collées.
     * 
     * @throws Exception
     */
    public void testPasteBeforeAfter() throws Exception {
    	openSession();
    	
        // Crée une feuille de route vide
        DocumentRoute pasteRoute = createPasteRoute(session, "FeuilleRoutePasteBeforeAfter");
        DocumentModel pasteRouteDoc = pasteRoute.getDocument();
        
        // Sélectionne les éléments à copier
        List<DocumentRouteTableElement> copyTable = getRoutingService().getFeuilleRouteElements(copyRoute, session);
        assertEquals(copyTable.size(), 8);
        DocumentModel copyE1 = copyTable.get(0).getDocument();
        DocumentModel copyE6 = copyTable.get(5).getDocument();
        DocumentModel copyE7 = copyTable.get(6).getDocument();
        DocumentModel copyE8 = copyTable.get(7).getDocument();
        
        List<DocumentModel> list = Arrays.asList(copyE1, copyE6, copyE7, copyE8);
        
        // Colle les éléments
        getRoutingService().lockDocumentRoute(pasteRoute, session);
        getRoutingService().pasteRouteStepIntoRoute(session, pasteRouteDoc, pasteRouteDoc, null, false, list);
        getRoutingService().unlockDocumentRoute(pasteRoute, session);
        
        // Vérifie les éléments collés
        List<DocumentRouteTableElement> pasteTable = getRoutingService().getFeuilleRouteElements(pasteRoute, session);
        assertEquals(4, pasteTable.size());
        
        String pasteRouteId = pasteRoute.getDocument().getId();
        checkRouteElement(pasteTable.get(0), "E1", 0, ElementLifeCycleState.draft.name(),pasteRouteId);
        checkRouteElement(pasteTable.get(1), "E6", 0, ElementLifeCycleState.draft.name(),pasteRouteId);
        checkRouteElement(pasteTable.get(2), "E7", 0, ElementLifeCycleState.draft.name(),pasteRouteId);
        checkRouteElement(pasteTable.get(3), "E8", 0, ElementLifeCycleState.draft.name(),pasteRouteId);

        // Sélectionne les éléments à copier avant
        DocumentModel pasteE1 = pasteTable.get(0).getDocument();
        DocumentModel pasteE6 = pasteTable.get(1).getDocument();
        DocumentModel pasteE7 = pasteTable.get(2).getDocument();
        List<DocumentModel> list2 = Arrays.asList(pasteE1, pasteE6);

        // Colle les éléments après E7
        getRoutingService().lockDocumentRoute(pasteRoute, session);
        getRoutingService().pasteRouteStepIntoRoute(session, pasteRouteDoc, pasteRouteDoc, pasteE7, false, list2);
        getRoutingService().unlockDocumentRoute(pasteRoute, session);

        // Vérifie les éléments collés
        List<DocumentRouteTableElement> pasteTable2 = getRoutingService().getFeuilleRouteElements(pasteRoute, session);
        assertEquals(6, pasteTable2.size());
        
        checkRouteElement(pasteTable2.get(0), "E1", 0, ElementLifeCycleState.draft.name(),pasteRouteId);
        checkRouteElement(pasteTable2.get(1), "E6", 0, ElementLifeCycleState.draft.name(),pasteRouteId);
        checkRouteElement(pasteTable2.get(2), "E7", 0, ElementLifeCycleState.draft.name(),pasteRouteId); // Pivot
        checkRouteElement(pasteTable2.get(3), "E1", 0, ElementLifeCycleState.draft.name(),pasteRouteId); // Nouvelle étape
        checkRouteElement(pasteTable2.get(4), "E6", 0, ElementLifeCycleState.draft.name(),pasteRouteId); // Nouvelle étape
        checkRouteElement(pasteTable2.get(5), "E8", 0, ElementLifeCycleState.draft.name(),pasteRouteId);

        // Colle les éléments avant E7
        getRoutingService().lockDocumentRoute(pasteRoute, session);
        getRoutingService().pasteRouteStepIntoRoute(session, pasteRouteDoc, pasteRouteDoc, pasteE7, true, list2);
        getRoutingService().unlockDocumentRoute(pasteRoute, session);

        // Vérifie les éléments collés
        List<DocumentRouteTableElement> pasteTable3 = getRoutingService().getFeuilleRouteElements(pasteRoute, session);
        assertEquals(8, pasteTable3.size());
        
        checkRouteElement(pasteTable3.get(0), "E1", 0, ElementLifeCycleState.draft.name(),pasteRouteId);
        checkRouteElement(pasteTable3.get(1), "E6", 0, ElementLifeCycleState.draft.name(),pasteRouteId);
        checkRouteElement(pasteTable3.get(2), "E1", 0, ElementLifeCycleState.draft.name(),pasteRouteId); // Nouvelle étape
        checkRouteElement(pasteTable3.get(3), "E6", 0, ElementLifeCycleState.draft.name(),pasteRouteId); // Nouvelle étape
        checkRouteElement(pasteTable3.get(4), "E7", 0, ElementLifeCycleState.draft.name(),pasteRouteId); // Pivot
        checkRouteElement(pasteTable3.get(5), "E1", 0, ElementLifeCycleState.draft.name(),pasteRouteId);
        checkRouteElement(pasteTable3.get(6), "E6", 0, ElementLifeCycleState.draft.name(),pasteRouteId);
        checkRouteElement(pasteTable3.get(7), "E8", 0, ElementLifeCycleState.draft.name(),pasteRouteId);
        
        closeSession();
    }

    /**
     * Test copier / coller d'un arbre avec un "trou" (étape non sélectionnée).
     * La séquence de conteneurs parallèle / série / parallèle est reconstituée, sans l'étape non sélectionnée.
     * 
     * @throws Exception
     */
    public void testPasteTreeWithMissingStep() throws Exception {
    	openSession();
    	
        // Crée une feuille de route vide
        DocumentRoute pasteRoute = createPasteRoute(session, "FeuilleRoutePasteTreeWithMissingStep");
        DocumentModel pasteRouteDoc = pasteRoute.getDocument();
        
        // Sélectionne les éléments à copier
        List<DocumentRouteTableElement> copyTable = getRoutingService().getFeuilleRouteElements(copyRoute, session);
        assertEquals(copyTable.size(), 8);
        DocumentModel copyE2 = copyTable.get(1).getDocument();
        DocumentModel copyE4 = copyTable.get(3).getDocument();
        DocumentModel copyE5 = copyTable.get(4).getDocument();
        
        List<DocumentModel> list = Arrays.asList(copyE2, copyE4, copyE5);
        
        // Colle les éléments
        getRoutingService().lockDocumentRoute(pasteRoute, session);
        getRoutingService().pasteRouteStepIntoRoute(session, pasteRouteDoc, pasteRouteDoc, null, false, list);
        getRoutingService().unlockDocumentRoute(pasteRoute, session);
        
        // Vérifie les éléments collés
        List<DocumentRouteTableElement> pasteTable = getRoutingService().getFeuilleRouteElements(pasteRoute, session);
        assertEquals(3, pasteTable.size());
        
        String pasteRouteId = pasteRoute.getDocument().getId();
        checkRouteElement(pasteTable.get(0), "E2", 1, ElementLifeCycleState.draft.name(),pasteRouteId);
        checkRouteElement(pasteTable.get(1), "E4", 3, ElementLifeCycleState.draft.name(),pasteRouteId);
        checkRouteElement(pasteTable.get(2), "E5", 3, ElementLifeCycleState.draft.name(),pasteRouteId);
        
        closeSession();
    }

    /**
     * Crée la feuille de route contenant les éléments à copier.
     * 
     * @param session Session
     * @return Route par défaut
     * @throws Exception
     */
    private DocumentRoute createCopyRoute(CoreSession session) throws Exception {
        DocumentModel route = createDocumentModel(session, FEUILLE_ROUTE_COPY_NAME,
                STConstant.FEUILLE_ROUTE_DOCUMENT_TYPE, "/");
        ReponsesFeuilleRoute feuilleRoute = route.getAdapter(ReponsesFeuilleRoute.class);
        assertNotNull(feuilleRoute);
        
        session.saveDocument(feuilleRoute.getDocument());
        session.save();
        
        // E1
        Mailbox user1Mailbox = getPersonalMailbox(user1);
        String mailboxId = user1Mailbox.getId();
        DocumentModel e1 = createDocumentModel(session, "E1", STConstant.ROUTE_STEP_DOCUMENT_TYPE, route.getPathAsString());
        e1.setPropertyValue(CaseConstants.STEP_DISTRIBUTION_MAILBOX_ID_PROPERTY_NAME, mailboxId);
        e1.setPropertyValue(STSchemaConstant.ROUTING_TASK_OBLIGATOIRE_SGG_PROPERTY, true);
        e1.setPropertyValue(STSchemaConstant.ROUTING_TASK_OBLIGATOIRE_MINISTERE_PROPERTY, true);
        e1.setPropertyValue(STSchemaConstant.ROUTING_TASK_DOCUMENT_ROUTE_ID_PROPERTY, route.getId());
        session.saveDocument(e1);
        
        // CP1
        DocumentModel cp1 = createDocumentModel(session, "CP1", DocumentRoutingConstants.STEP_FOLDER_DOCUMENT_TYPE, route.getPathAsString());
        cp1.setPropertyValue(DocumentRoutingConstants.EXECUTION_TYPE_PROPERTY_NAME, "parallel");
        session.saveDocument(cp1);

        // E2
        DocumentModel e2 = createDocumentModel(session, "E2", STConstant.ROUTE_STEP_DOCUMENT_TYPE, cp1.getPathAsString());
        e2.setPropertyValue(CaseConstants.STEP_DISTRIBUTION_MAILBOX_ID_PROPERTY_NAME, mailboxId);
        session.saveDocument(e2);

        // CS1
        DocumentModel cs1 = createDocumentModel(session, "CS1", DocumentRoutingConstants.STEP_FOLDER_DOCUMENT_TYPE, cp1.getPathAsString());
        cs1.setPropertyValue(DocumentRoutingConstants.EXECUTION_TYPE_PROPERTY_NAME, "serial");
        session.saveDocument(cs1);

        // E3
        DocumentModel e3 = createDocumentModel(session, "E3", STConstant.ROUTE_STEP_DOCUMENT_TYPE, cs1.getPathAsString());
        e3.setPropertyValue(CaseConstants.STEP_DISTRIBUTION_MAILBOX_ID_PROPERTY_NAME, mailboxId);
        session.saveDocument(e3);

        // CP2
        DocumentModel cp2 = createDocumentModel(session, "CP2", DocumentRoutingConstants.STEP_FOLDER_DOCUMENT_TYPE, cs1.getPathAsString());
        cp2.setPropertyValue(DocumentRoutingConstants.EXECUTION_TYPE_PROPERTY_NAME, "parallel");
        session.saveDocument(cp2);

        // E4
        DocumentModel e4 = createDocumentModel(session, "E4", STConstant.ROUTE_STEP_DOCUMENT_TYPE, cp2.getPathAsString());
        e4.setPropertyValue(CaseConstants.STEP_DISTRIBUTION_MAILBOX_ID_PROPERTY_NAME, mailboxId);
        session.saveDocument(e4);

        // E5
        DocumentModel e5 = createDocumentModel(session, "E5", STConstant.ROUTE_STEP_DOCUMENT_TYPE, cp2.getPathAsString());
        e5.setPropertyValue(CaseConstants.STEP_DISTRIBUTION_MAILBOX_ID_PROPERTY_NAME, mailboxId);
        session.saveDocument(e5);

        // E6
        DocumentModel e6 = createDocumentModel(session, "E6", STConstant.ROUTE_STEP_DOCUMENT_TYPE, route.getPathAsString());
        e6.setPropertyValue(CaseConstants.STEP_DISTRIBUTION_MAILBOX_ID_PROPERTY_NAME, mailboxId);
        session.saveDocument(e6);

        // E7
        DocumentModel e7 = createDocumentModel(session, "E7", STConstant.ROUTE_STEP_DOCUMENT_TYPE, route.getPathAsString());
        e7.setPropertyValue(CaseConstants.STEP_DISTRIBUTION_MAILBOX_ID_PROPERTY_NAME, mailboxId);
        session.saveDocument(e7);

        // E8
        DocumentModel e8 = createDocumentModel(session, "E8", STConstant.ROUTE_STEP_DOCUMENT_TYPE, route.getPathAsString());
        e8.setPropertyValue(CaseConstants.STEP_DISTRIBUTION_MAILBOX_ID_PROPERTY_NAME, mailboxId);
        session.saveDocument(e8);

        session.save();
        
        // Initialise les étapes de la nouvelle feuille de route (pour les champs de dénormalisation) en accédant à une méthode de l'implémentation
        DocumentRoutingServiceImpl implDocumentRoutingService = (DocumentRoutingServiceImpl) getRoutingService();
        implDocumentRoutingService.initDocumentRouteStep(session, feuilleRoute.getDocument());
        
        // Valide la feuille de route
        getRoutingService().lockDocumentRoute(feuilleRoute, session);
        DocumentRoute documentRoute = getRoutingService().validateRouteModel(feuilleRoute, session);
        session.saveDocument(documentRoute.getDocument());
        session.save();
        getRoutingService().unlockDocumentRoute(feuilleRoute, session);

        Framework.getLocalService(EventService.class).waitForAsyncCompletion();

        assertEquals(ElementLifeCycleState.validated.name(), documentRoute.getDocument().getCurrentLifeCycleState());
        assertEquals(ElementLifeCycleState.validated.name(), session.getChildren(documentRoute.getDocument().getRef()).get(0).getCurrentLifeCycleState());

        return feuilleRoute;
    }

    /**
     * Crée une feuille de route dans laquelle on va coller les éléments.
     * 
     * @param name Nom de la feuille de route
     * @param session Session
     * @return Route par défaut
     * @throws Exception
     */
    private DocumentRoute createPasteRoute(CoreSession session, String name) throws Exception {
        DocumentModel route = createDocumentModel(session, name,
                STConstant.FEUILLE_ROUTE_DOCUMENT_TYPE, "/");
        ReponsesFeuilleRoute feuilleRoute = route.getAdapter(ReponsesFeuilleRoute.class);
        assertNotNull(feuilleRoute);
        
        session.saveDocument(feuilleRoute.getDocument());
        session.save();

        return feuilleRoute;
    }

    /**
     * Vérifie les données d'un élément de feuille de route.
     * 
     * @param element Élément à vérifier
     * @param name Nom attendu
     * @param depth Profondeur attendue
     * @param state Etat attendu
     * @param documentRouteId L'identifiant de la feuille de route qui doit se retrouver sur l'étape
     * @throws ClientException
     */
    private void checkRouteElement(DocumentRouteTableElement element, String name, int depth, String state, String documentRouteId) throws ClientException {
        assertEquals(depth, element.getDepth());
        STRouteStep routeStep = element.getRouteStep();
        assertEquals("Nom de l'étape", name, routeStep.getName());
        
        DocumentModel routeStepDoc = routeStep.getDocument();
        assertEquals("État de l'étape", state, routeStepDoc.getCurrentLifeCycleState());
        assertEquals("Identifiant de feuille de route", documentRouteId, routeStep.getDocumentRouteId());
    }
}
