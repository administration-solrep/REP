package fr.dila.reponses.ui.services.actions.impl;

import static fr.dila.ss.ui.services.impl.SSMailboxListComponentServiceImpl.REFRESH_CORBEILLE_KEY;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.feuilleroute.ReponsesFeuilleRoute;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.api.service.ReponseFeuilleRouteService;
import fr.dila.reponses.api.service.ReponseService;
import fr.dila.reponses.api.service.ReponsesArbitrageService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.services.actions.ReponseActionService;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.api.service.SSJournalService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.ss.ui.services.actions.SSCorbeilleActionService;
import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.UnrestrictedGetDocumentRunner;
import fr.dila.st.ui.enums.ContextDataKey;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.services.actions.DossierLockActionService;
import fr.dila.st.ui.services.actions.STActionsServiceLocator;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.session.UserSession;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(
    {
        ReponsesActionsServiceLocator.class,
        ReponsesServiceLocator.class,
        STActionsServiceLocator.class,
        SSActionsServiceLocator.class,
        STServiceLocator.class,
        SSServiceLocator.class,
        ReponsesDossierDistributionActionServiceImpl.class
    }
)
@PowerMockIgnore("javax.management.*")
public class DossierDistributionActionServiceImplTest {
    private static final String DOSSIER_LINK_ID_VALUE = "dossierLinkIdValue";

    private static final String MINISTERE_ID_VALUE = "ministereIdValue";

    private static final String ROUTING_TASK_ID = "routingTaskId";

    private static final String REATTRIBUTION_OBSERVATIONS = "reattributionObservations";

    private static final String REATTRIBUTION_MINISTERE = "reattributionMinistere";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    ReponsesDossierDistributionActionServiceImpl service = new ReponsesDossierDistributionActionServiceImpl();

    @Mock
    SSCorbeilleActionService ssCorbeilleActionService;

    @Mock
    private ReponseService reponseService;

    @Mock
    private DossierLockActionService dossierLockActionService;

    @Mock
    private ReponseFeuilleRouteService reponseFeuilleRouteService;

    @Mock
    private DossierDistributionService dossierDistributionService;

    @Mock
    ReponseActionService reponseActionService;

    @Mock
    private ReponsesArbitrageService reponsesArbitrageService;

    @Mock
    private STMinisteresService ministeresService;

    @Mock
    private STPostesService postesService;

    @Mock
    private MailboxPosteService mailboxPosteService;

    @Mock
    private UnrestrictedGetDocumentRunner unrestrictedGetDocumentRunner;

    @Mock
    DossierLink dossierLink;

    @Mock
    CoreSession session;

    @Mock
    SpecificContext context;

    @Mock
    WebContext webContext;

    @Mock
    UserSession userSession;

    @Mock
    DocumentModel dossierLinkDoc;

    @Mock
    private DocumentModel dossierDoc;

    @Mock
    private Dossier dossier;

    @Mock
    private Reponse reponse;

    @Mock
    private DocumentModel reponseDoc;

    @Mock
    private DocumentModel etapeDoc;

    private Map<String, Object> contextData = new HashMap<>();

    @Before
    public void setUp() {
        PowerMockito.mockStatic(ReponsesActionsServiceLocator.class);
        when(ReponsesActionsServiceLocator.getReponseActionService()).thenReturn(reponseActionService);

        PowerMockito.mockStatic(SSActionsServiceLocator.class);
        when(SSActionsServiceLocator.getSSCorbeilleActionService()).thenReturn(ssCorbeilleActionService);

        PowerMockito.mockStatic(ReponsesServiceLocator.class);
        when(ReponsesServiceLocator.getReponseService()).thenReturn(reponseService);
        when(ReponsesServiceLocator.getFeuilleRouteService()).thenReturn(reponseFeuilleRouteService);
        when(ReponsesServiceLocator.getDossierDistributionService()).thenReturn(dossierDistributionService);
        when(ReponsesServiceLocator.getReponsesArbitrageService()).thenReturn(reponsesArbitrageService);

        PowerMockito.mockStatic(STActionsServiceLocator.class);
        when(STActionsServiceLocator.getDossierLockActionService()).thenReturn(dossierLockActionService);

        PowerMockito.mockStatic(SSServiceLocator.class);
        when(SSServiceLocator.getSSJournalService()).thenReturn(mock(SSJournalService.class));
        when(SSServiceLocator.getMailboxPosteService()).thenReturn(mailboxPosteService);

        PowerMockito.mockStatic(STServiceLocator.class);
        when(STServiceLocator.getSTMinisteresService()).thenReturn(ministeresService);
        when(STServiceLocator.getSTPostesService()).thenReturn(postesService);

        PowerMockito.doCallRealMethod().when(context).getFromContextData(any(ContextDataKey.class));

        when(ssCorbeilleActionService.getCurrentDossierLink(context)).thenReturn(dossierLink);
        when(context.getSession()).thenReturn(session);
        when(context.getMessageQueue()).thenReturn(mock(SolonAlertManager.class));

        contextData.put(STContextDataKey.MINISTERE_ID.getName(), MINISTERE_ID_VALUE);
        contextData.put(STContextDataKey.CURRENT_DOSSIER_LINK.getName(), DOSSIER_LINK_ID_VALUE);
        when(context.getContextData()).thenReturn(contextData);
        when(context.getFromContextData(STContextDataKey.MINISTERE_ID)).thenReturn(MINISTERE_ID_VALUE);
        when(context.getFromContextData(ReponsesContextDataKey.OBSERVATIONS)).thenReturn(REATTRIBUTION_OBSERVATIONS);
        when(dossierLinkDoc.getAdapter(DossierLink.class)).thenReturn(dossierLink);
        when(session.getDocument(new IdRef(DOSSIER_LINK_ID_VALUE))).thenReturn(dossierLinkDoc);
        when(dossierLink.getDocument()).thenReturn(dossierLinkDoc);
        when(dossierLink.getDossier(session)).thenReturn(dossier);
        when(context.getCurrentDocument()).thenReturn(dossierDoc);
        when(dossierDoc.getAdapter(Dossier.class)).thenReturn(dossier);
        when(dossier.getReponse(session)).thenReturn(reponse);
        when(dossier.getDocument()).thenReturn(dossierDoc);
        when(reponse.getDocument()).thenReturn(reponseDoc);

        when(context.getWebcontext()).thenReturn(webContext);
        when(webContext.getUserSession()).thenReturn(userSession);
    }

    @Test
    public void testDonnerAvisDefavorableAddStepTrue() {
        when(reponseActionService.reponseHasChanged(session, reponseDoc)).thenReturn(TRUE);
        service.donnerAvisDefavorableEtInsererTaches(context);
        verify(reponseService).incrementReponseVersion(session, reponseDoc);
        verify(reponseFeuilleRouteService).addStepAfterReject(session, dossierLink.getRoutingTaskId(), dossier);
        verify(dossierDistributionService).rejeterDossierLink(session, dossierDoc, dossierLinkDoc);
        verify(dossierLink).setDateDebutValidation(any());
        verify(dossierLink).save(session);
        verify(dossierLockActionService).unlockDossier(context);
    }

    // ne doit pas incrémenter la version de la réponse si elle n'a pas changé
    @Test
    public void testDonnerAvisDefavorableReponseUnchanged() {
        when(reponseActionService.reponseHasChanged(session, reponseDoc)).thenReturn(FALSE);
        service.donnerAvisDefavorableEtInsererTaches(context);
        verify(reponseService, never()).incrementReponseVersion(session, reponseDoc);
        verify(dossierLockActionService).unlockDossier(context);
    }

    @Test
    public void testDonnerAvisDefavorableAddStepFalse() {
        service.donnerAvisDefavorableEtPoursuivre(context);
        verify(reponseFeuilleRouteService, never())
            .addStepAfterReject(session, dossierLink.getRoutingTaskId(), dossier);
        verify(dossierLockActionService).unlockDossier(context);
    }

    @Test
    public void testGetValidationPMStep() {
        STDossier stDossier = mock(STDossier.class);
        when(dossierDoc.getAdapter(STDossier.class)).thenReturn(stDossier);
        when(stDossier.getLastDocumentRoute()).thenReturn("lastDocumentRoute");
        DocumentModel validationPmStep = mock(DocumentModel.class);
        when(reponseFeuilleRouteService.getValidationPMStep(session, "lastDocumentRoute")).thenReturn(validationPmStep);
        assertThat(service.getValidationPMStep(context)).isEqualTo(validationPmStep);
    }

    @Test
    public void testDonnerAvisFavorable() {
        when(dossierLink.getRoutingTaskType()).thenReturn(VocabularyConstants.ROUTING_TASK_TYPE_ARBITRAGE);

        service.donnerAvisFavorable(context);
        verify(dossierDistributionService).validerEtape(session, dossierDoc, dossierLinkDoc);
        verify(dossierLink).setDateDebutValidation(any(Calendar.class));
        verify(dossierLink).save(session);
        verify(dossierLockActionService).unlockCurrentDossier(context);
        verify(userSession).put(REFRESH_CORBEILLE_KEY, TRUE);
    }

    @Test
    public void testDonnerAvisFavorableRoutingTaskTypeReorientation() {
        when(dossierLink.getRoutingTaskType()).thenReturn(VocabularyConstants.ROUTING_TASK_TYPE_REORIENTATION);

        service.donnerAvisFavorable(context);
        verify(dossierDistributionService).validerEtape(session, dossierDoc, dossierLinkDoc);
        verify(dossierLink, never()).setDateDebutValidation(any(Calendar.class));
    }

    @Test
    public void testDemandeArbitrageSGG() {
        when(reponsesArbitrageService.canAddStepArbitrageSGG(dossierDoc)).thenReturn(TRUE);
        service.demandeArbitrageSGG(context);
        verify(reponsesArbitrageService).addStepArbitrageSGG(session, dossierDoc, dossierLinkDoc);
        verify(dossierDistributionService).validerEtapeNonConcerne(session, dossierDoc, dossierLinkDoc);
        verify(dossierLockActionService).unlockDossier(context);
    }

    @Test(expected = NuxeoException.class)
    public void testDemandeArbitrageSGGCannotAddStep() {
        when(reponsesArbitrageService.canAddStepArbitrageSGG(dossierDoc)).thenReturn(FALSE);
        service.demandeArbitrageSGG(context);
        verify(reponsesArbitrageService, never()).addStepArbitrageSGG(session, dossierDoc, dossierLinkDoc);
        verify(dossierDistributionService, never()).validerEtapeNonConcerne(session, dossierDoc, dossierLinkDoc);
    }

    @Test
    public void testMettreEnAttente() {
        service.mettreEnAttente(context);
        verify(reponseFeuilleRouteService).addStepAttente(session, dossierLink);
        verify(dossierLockActionService).unlockCurrentDossier(context);
    }

    @Test
    public void testAttributionApresArbitrage() {
        when(context.getFromContextData(STContextDataKey.MINISTERE_ID)).thenReturn(REATTRIBUTION_MINISTERE);
        when(context.getFromContextData(ReponsesContextDataKey.OBSERVATIONS)).thenReturn(REATTRIBUTION_OBSERVATIONS);
        service.attributionApresArbitrage(context);
        verify(reponsesArbitrageService)
            .attributionAfterArbitrage(
                session,
                dossierLink,
                dossierDoc,
                REATTRIBUTION_MINISTERE,
                REATTRIBUTION_OBSERVATIONS
            );
    }

    @Test
    public void testValidationRetourPM() {
        when(dossierLink.getRoutingTaskId()).thenReturn(ROUTING_TASK_ID);
        when(session.getDocument(new IdRef(ROUTING_TASK_ID))).thenReturn(etapeDoc);

        service.validationRetourPM(context);
        verify(reponseFeuilleRouteService).addStepValidationRetourPM(session, etapeDoc, dossierDoc);
    }

    @Test
    public void testReattributionDirecte() {
        when(ministeresService.getEntiteNode(anyString())).thenReturn(null);
        when(dossier.isArbitrated()).thenReturn(TRUE);
        service.reattributionDirecte(context);
        verify(reponsesArbitrageService)
            .reattributionDirecte(session, dossierLink, dossierDoc, MINISTERE_ID_VALUE, REATTRIBUTION_OBSERVATIONS);
        verify(dossierLockActionService).unlockCurrentDossier(context);
        verify(dossierLink).setDateDebutValidation(any(Calendar.class));
        verify(dossierLink).save(session);
    }

    @Test
    public void testReattributionDirecteDisserArbitrated() {
        when(dossier.isArbitrated()).thenReturn(FALSE);
        service.reattributionDirecte(context);
        verify(reponsesArbitrageService, never())
            .reattributionDirecte(
                session,
                dossierLink,
                dossierDoc,
                REATTRIBUTION_MINISTERE,
                REATTRIBUTION_OBSERVATIONS
            );
    }

    @Test
    public void testNonConcerne() {
        service.nonConcerne(context);
        verify(dossierLink).setDateDebutValidation(any(Calendar.class));
        verify(dossierLink).save(session);
        verify(dossierDistributionService).validerEtapeNonConcerne(session, dossierDoc, dossierLinkDoc);
        verify(dossierLockActionService).unlockCurrentDossier(context);
    }

    @Test
    public void testNonConcerneReorientation() {
        // Given
        when(dossierLink.getRoutingTaskId()).thenReturn(ROUTING_TASK_ID);
        when(session.getDocument(new IdRef(ROUTING_TASK_ID))).thenReturn(etapeDoc);

        // When
        service.nonConcerneReorientation(context);

        // Then
        verify(reponseFeuilleRouteService).addStepAfterReorientation(session, etapeDoc);
        verify(context.getMessageQueue()).addInfoToQueue("Étape 'Pour réorientation' ajoutée");
        verify(dossierDistributionService).validerEtapeNonConcerne(session, dossierDoc, dossierLinkDoc);
    }

    @Test
    public void testNonConcerneReattribution() throws Exception {
        PowerMockito
            .whenNew(UnrestrictedGetDocumentRunner.class)
            .withAnyArguments()
            .thenReturn(unrestrictedGetDocumentRunner);
        when(context.getFromContextData(STContextDataKey.MINISTERE_ID)).thenReturn("idMini");
        when(unrestrictedGetDocumentRunner.getById(any())).thenReturn(etapeDoc);
        when(dossier.getLastDocumentRoute()).thenReturn("lastDocumentRoute");
        DocumentModel feuilleRouteDoc = mock(DocumentModel.class);
        when(session.getDocument(new IdRef(dossier.getLastDocumentRoute()))).thenReturn(feuilleRouteDoc);
        ReponsesFeuilleRoute feuilleRoute = mock(ReponsesFeuilleRoute.class);
        when(feuilleRouteDoc.getAdapter(ReponsesFeuilleRoute.class)).thenReturn(feuilleRoute);
        when(feuilleRoute.getAttachedDocuments()).thenReturn(Collections.emptyList());
        PosteNode posteBdc = mock(PosteNode.class);
        when(postesService.getPosteBdcInEntite(any())).thenReturn(posteBdc);

        boolean success = service.nonConcerneReattribution(context);

        verify(dossierLink).setDateDebutValidation(any(Calendar.class));
        verify(dossierLink).save(session);
        verify(dossierLockActionService).unlockCurrentDossier(context);
        assertTrue(success);
    }
}
