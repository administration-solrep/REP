package fr.dila.reponses.ui.services.actions.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.feuilleroute.ReponsesRouteStep;
import fr.dila.reponses.api.service.ReponseFeuilleRouteService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.services.actions.DossierActionService;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.ss.ui.services.actions.SSCorbeilleActionService;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.webengine.WebEngine;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ ReponsesActionsServiceLocator.class, SSActionsServiceLocator.class, ReponsesServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class ReponsesDocumentRoutingActionServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    ReponsesDocumentRoutingActionServiceImpl service = new ReponsesDocumentRoutingActionServiceImpl();

    @Mock
    DossierActionService dossierAction;

    @Mock
    SSCorbeilleActionService corbeilleAction;

    @Mock
    ReponseFeuilleRouteService reponseFeuilleRouteService;

    @Mock
    DocumentModel document;

    @Mock
    SSPrincipal principal;

    @Mock
    DossierLink dossierLink;

    @Mock
    CoreSession session;

    @Mock
    private SpecificContext context;

    @Before
    public void before() {
        dossierAction = Mockito.mock(DossierActionService.class);
        corbeilleAction = Mockito.mock(SSCorbeilleActionService.class);
        document = Mockito.mock(DocumentModel.class);
        principal = Mockito.mock(SSPrincipal.class);
        dossierLink = Mockito.mock(DossierLink.class);
        reponseFeuilleRouteService = Mockito.mock(ReponseFeuilleRouteService.class);

        PowerMockito.mockStatic(ReponsesActionsServiceLocator.class);
        PowerMockito.mockStatic(ReponsesServiceLocator.class);
        PowerMockito.mockStatic(WebEngine.class);

        Mockito.when(ReponsesActionsServiceLocator.getDossierActionService()).thenReturn(dossierAction);

        PowerMockito.mockStatic(SSActionsServiceLocator.class);
        when(SSActionsServiceLocator.getSSCorbeilleActionService()).thenReturn(corbeilleAction);

        Mockito.when(ReponsesServiceLocator.getFeuilleRouteService()).thenReturn(reponseFeuilleRouteService);
    }

    @Test
    public void testIsFeuilleRouteVisible() {
        Mockito.when(principal.isMemberOf(ReponsesBaseFunctionConstant.FEUILLE_ROUTE_VIEWER)).thenReturn(true);
        Mockito.when(dossierAction.isDossierContainsMinistere(Mockito.any(), Mockito.any())).thenReturn(false);

        assertEquals(true, service.isFeuilleRouteVisible(session, principal, document));

        Mockito.when(principal.isMemberOf(ReponsesBaseFunctionConstant.FEUILLE_ROUTE_VIEWER)).thenReturn(false);
        assertEquals(false, service.isFeuilleRouteVisible(session, principal, document));

        Mockito.when(dossierAction.isDossierContainsMinistere(Mockito.any(), Mockito.any())).thenReturn(true);
        assertEquals(true, service.isFeuilleRouteVisible(session, principal, document));
    }

    @Test
    public void testIsStepTransmissionAssemblees() {
        SpecificContext context = new SpecificContext();
        dossierLink.setRoutingTaskType(VocabularyConstants.ROUTING_TASK_TYPE_AVIS);
        Mockito.when(corbeilleAction.getCurrentDossierLink(Mockito.any())).thenReturn(dossierLink);

        Mockito
            .when(dossierLink.getRoutingTaskType())
            .thenReturn(VocabularyConstants.ROUTING_TASK_TYPE_TRANSMISSION_ASSEMBLEE);
        assertEquals(true, service.isStepTransmissionAssemblees(context));

        Mockito.when(dossierLink.getRoutingTaskType()).thenReturn(VocabularyConstants.ROUTING_TASK_TYPE_AVIS);
        assertEquals(false, service.isStepTransmissionAssemblees(context));
    }

    @Test
    public void testIsStepInMinistere() {
        CoreSession session = Mockito.mock(CoreSession.class);
        Dossier dossier = Mockito.mock(Dossier.class);
        Mockito.when(context.getSession()).thenReturn(session);
        Mockito.when(session.getPrincipal()).thenReturn(principal);

        service = Mockito.mock(ReponsesDocumentRoutingActionServiceImpl.class);
        Mockito.when(service.isStepInMinistere(Mockito.any())).thenCallRealMethod();
        Mockito.when(service.isMailboxIdInUserPostes(Mockito.any(), Mockito.any())).thenReturn(false);
        assertEquals(false, service.isStepInMinistere(context));

        Mockito.when(principal.isAdministrator()).thenReturn(true);
        assertEquals(true, service.isStepInMinistere(context));
        //On reset la valeur du mock pour le test suivant
        Mockito.when(principal.isAdministrator()).thenReturn(false);

        Mockito
            .when(principal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_PARAPHEUR_ADMIN_UPDATER))
            .thenReturn(true);
        assertEquals(true, service.isStepInMinistere(context));
        Mockito
            .when(principal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_PARAPHEUR_ADMIN_UPDATER))
            .thenReturn(false);

        Mockito.when(corbeilleAction.getCurrentDossierLink(Mockito.any())).thenReturn(dossierLink);
        Mockito.when(service.isMailboxIdInUserPostes(Mockito.any(), Mockito.any())).thenReturn(true);
        assertEquals(true, service.isStepInMinistere(context));
        Mockito.when(service.isMailboxIdInUserPostes(Mockito.any(), Mockito.any())).thenReturn(false);
        Mockito.when(corbeilleAction.getCurrentDossierLink(Mockito.any())).thenReturn(null);

        Mockito.when(context.getCurrentDocument()).thenReturn(document);
        Mockito.when(document.getAdapter(Dossier.class)).thenReturn(dossier);
        Set<String> listeId = new HashSet<>();
        listeId.add("1");
        Mockito.when(principal.getMinistereIdSet()).thenReturn(listeId);
        Mockito.when(dossier.getIdMinistereAttributaireCourant()).thenReturn("1");
        Mockito
            .when(principal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_PARAPHEUR_ADMIN_MIN_UPDATER))
            .thenReturn(true);
        assertEquals(true, service.isStepInMinistere(context));
        Mockito
            .when(principal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_PARAPHEUR_ADMIN_MIN_UPDATER))
            .thenReturn(false);

        List<DocumentModel> listeStep = new ArrayList<>();
        DocumentModel stepDoc = Mockito.mock(DocumentModel.class);
        ReponsesRouteStep routeStep = Mockito.mock(ReponsesRouteStep.class);
        Mockito.when(stepDoc.getAdapter(ReponsesRouteStep.class)).thenReturn(routeStep);
        listeStep.add(stepDoc);
        Mockito.when(reponseFeuilleRouteService.getRunningSteps(Mockito.any(), Mockito.any())).thenReturn(listeStep);
        Mockito.when(service.isMailboxIdInUserPostes(Mockito.any(), Mockito.any())).thenReturn(true);
        assertEquals(true, service.isStepInMinistere(context));
    }

    @Test
    public void testIsStepPourInfomation() {
        when(corbeilleAction.getCurrentDossierLink(context)).thenReturn(dossierLink);
        when(dossierLink.getRoutingTaskType()).thenReturn(VocabularyConstants.ROUTING_TASK_TYPE_INFORMATION);
        assertTrue(service.isStepPourInformation(context));

        when(dossierLink.getRoutingTaskType()).thenReturn(VocabularyConstants.ROUTING_TASK_TYPE_ACTUALISATION);
        assertFalse(service.isStepPourInformation(context));
    }
}
