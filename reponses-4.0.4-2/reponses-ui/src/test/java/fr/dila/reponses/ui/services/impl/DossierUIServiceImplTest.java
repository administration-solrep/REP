package fr.dila.reponses.ui.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.ui.bean.ConsultDossierDTO;
import fr.dila.reponses.ui.bean.actions.ReponsesDossierActionDTO;
import fr.dila.reponses.ui.bean.actions.ReponsesRoutingActionDTO;
import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.actions.DossierActionService;
import fr.dila.reponses.ui.services.actions.ReponseActionService;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.reponses.ui.services.actions.ReponsesDocumentRoutingActionService;
import fr.dila.reponses.ui.services.actions.ReponsesDossierDistributionActionService;
import fr.dila.reponses.ui.services.files.ReponsesFondDeDossierUIService;
import fr.dila.ss.ui.bean.actions.SSNavigationActionDTO;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.SSFeuilleRouteUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.services.actions.NavigationActionService;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.ss.ui.services.actions.SSCorbeilleActionService;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.services.actions.DossierLockActionService;
import fr.dila.st.ui.services.actions.STActionsServiceLocator;
import fr.dila.st.ui.services.actions.STLockActionService;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Arrays;
import java.util.Date;
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
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(
    {
        ReponsesActionsServiceLocator.class,
        ReponsesUIServiceLocator.class,
        STActionsServiceLocator.class,
        SSActionsServiceLocator.class,
        SSUIServiceLocator.class,
        STServiceLocator.class
    }
)
@PowerMockIgnore("javax.management.*")
public class DossierUIServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private DossierUIServiceImpl service = new DossierUIServiceImpl();

    @Mock
    private CoreSession session;

    @Mock
    private DocumentModel document;

    @Mock
    private DocumentModel questionDocument;

    @Mock
    private Dossier dossier;

    @Mock
    private Reponse reponse;

    @Mock
    private Question question;

    @Mock
    private ReponsesDocumentRoutingActionService documentRoutingAction;

    @Mock
    private ReponsesDossierDistributionActionService dossierDistributionActionService;

    @Mock
    private SSCorbeilleActionService ssCorbeilleActionService;

    @Mock
    private DossierActionService dossierAction;

    @Mock
    private DossierLockActionService dossierLockAction;

    @Mock
    private STLockActionService stLockAction;

    @Mock
    private NavigationActionService navigationActionService;

    @Mock
    private ReponsesFondDeDossierUIService fondDeDossierUIService;

    @Mock
    private ReponseActionService reponseActionService;

    @Mock
    private SSFeuilleRouteUIService ssFeuillreRouteUIService;

    @Mock
    private STParametreService parametreService;

    @Before
    public void before() {
        mockStatic(ReponsesActionsServiceLocator.class);
        mockStatic(ReponsesUIServiceLocator.class);
        mockStatic(STActionsServiceLocator.class);
        mockStatic(SSActionsServiceLocator.class);
        mockStatic(SSUIServiceLocator.class);
        mockStatic(STServiceLocator.class);

        when(ReponsesActionsServiceLocator.getDossierActionService()).thenReturn(dossierAction);
        when(ReponsesActionsServiceLocator.getReponsesDossierDistributionActionService())
            .thenReturn(dossierDistributionActionService);
        when(SSActionsServiceLocator.getSSCorbeilleActionService()).thenReturn(ssCorbeilleActionService);
        when(ReponsesActionsServiceLocator.getReponsesDocumentRoutingActionService()).thenReturn(documentRoutingAction);
        when(ReponsesActionsServiceLocator.getReponseActionService()).thenReturn(reponseActionService);

        when(ReponsesUIServiceLocator.getFondDeDossierUIService()).thenReturn(fondDeDossierUIService);

        when(STActionsServiceLocator.getDossierLockActionService()).thenReturn(dossierLockAction);
        when(STActionsServiceLocator.getSTLockActionService()).thenReturn(stLockAction);

        when(SSActionsServiceLocator.getNavigationActionService()).thenReturn(navigationActionService);
        when(SSUIServiceLocator.getSSFeuilleRouteUIService()).thenReturn(ssFeuillreRouteUIService);
        when(STServiceLocator.getSTParametreService()).thenReturn(parametreService);
    }

    @Test
    public void testGetDossierConsult() {
        Map<String, Object> map = new HashMap<>();
        map.put(DossierUIServiceImpl.DOSSIER_ID_KEY, "idDossier");
        SpecificContext context = new SpecificContext();
        context.setSession(session);
        context.setContextData(map);
        context.setCurrentDocument(document);

        when(session.getDocument(any())).thenReturn(document);

        createMockDossier();

        ConsultDossierDTO returnDto = new ConsultDossierDTO();
        returnDto.setId("idDossier");

        //Mock des services Actions
        when(dossierAction.canReadAllotissement(any())).thenReturn(true);
        when(dossierAction.canReadDossierConnexe(any())).thenReturn(false);
        when(dossierAction.isDossierContainsMinistere(any(), any())).thenReturn(true);
        when(ssFeuillreRouteUIService.getNextStepLabels(any(), any())).thenReturn(Arrays.asList("a", "b"));
        when(ssFeuillreRouteUIService.getCurrentStepLabel(any())).thenReturn(Arrays.asList("c"));
        when(ssFeuillreRouteUIService.getLastStepDate(any(), any())).thenReturn(new Date(1));

        when(reponseActionService.canUserBriserReponse(any())).thenReturn(true);

        when(fondDeDossierUIService.canUserUpdateFondDossier(context)).thenReturn(true);

        when(documentRoutingAction.hasRelatedRoute(any(), any())).thenReturn(false);
        when(documentRoutingAction.isFeuilleRouteVisible(any(), any(), any())).thenReturn(true);

        when(documentRoutingAction.isInProgressStep(any())).thenReturn(true);
        when(parametreService.getParametreValue(session, STParametreConstant.QUESTION_DUREE_TRAITEMENT))
            .thenReturn("10");

        ConsultDossierDTO dto = service.getDossierConsult(context);

        assertNotNull(dto);
        assertEquals("idDossier", dto.getId());
        assertNotNull(dto.getQuestionInfo());
        assertEquals(new Long(1L), dto.getQuestionInfo().getNumeroQuestion());
        assertEquals("monType", dto.getQuestionInfo().getTypeQuestion());

        assertEquals(10, context.getContextData().size());
        assertNotNull(context.getFromContextData(ReponsesContextDataKey.DOSSIER));
        assertNotNull(context.getFromContextData(ReponsesContextDataKey.DOSSIER_ACTIONS));
        assertNotNull(context.getFromContextData(STContextDataKey.DOSSIER_LOCK_ACTIONS));
        assertNotNull(context.getFromContextData(STContextDataKey.LOCK_ACTIONS));
        assertNotNull(context.getFromContextData(ReponsesContextDataKey.DOSSIER_DISTRIBUTION_ACTIONS));
        assertNotNull(context.getFromContextData(ReponsesContextDataKey.ROUTING_ACTIONS));
        assertNotNull(context.getFromContextData(SSContextDataKey.NAVIGATION_ACTIONS));
        assertNotNull(context.getFromContextData(SSContextDataKey.IS_IN_PROGRESS_STEP));

        ConsultDossierDTO dossierContext = context.getFromContextData(ReponsesContextDataKey.DOSSIER);
        assertEquals(dto, dossierContext);

        ReponsesDossierActionDTO dossierActions = context.getFromContextData(ReponsesContextDataKey.DOSSIER_ACTIONS);
        assertThat(dossierActions.getCanReadAllotissement()).isTrue();
        assertThat(dossierActions.getCanReadDossierConnexe()).isFalse();
        assertThat(dossierActions.getIsDossierContainsMinistere()).isTrue();
        assertThat(dossierActions.getIsDossierContainsMinistere()).isTrue();
        assertThat(dossierActions.getCanUserBriserReponse()).isTrue();
        assertThat(dto.getNextStepLabel()).containsExactly("a", "b");
        assertThat(dto.getActualStepLabel()).containsExactly("c");
        assertThat(dto.getIsDone()).isFalse();
        assertNull(dto.getDateLastStep());

        ReponsesRoutingActionDTO routingActions = context.getFromContextData(ReponsesContextDataKey.ROUTING_ACTIONS);
        assertThat(routingActions.getHasRelatedRoute()).isFalse();
        assertThat(routingActions.getIsFeuilleRouteVisible()).isTrue();

        SSNavigationActionDTO navigationActions = context.getFromContextData(SSContextDataKey.NAVIGATION_ACTIONS);
        assertNotNull(navigationActions.getIsFromEspaceTravail());
    }

    private void createMockDossier() {
        when(document.getAdapter(Dossier.class)).thenReturn(dossier);
        when(document.getId()).thenReturn("idDossier");
        when(document.getType()).thenReturn("Dossier");
        when(dossier.getQuestion(any())).thenReturn(question);
        when(dossier.getReponse(any())).thenReturn(reponse);

        createQuestionMock();
    }

    private void createQuestionMock() {
        when(question.getDocument()).thenReturn(questionDocument);
        when(questionDocument.getType()).thenReturn("Question");
        when(
            questionDocument.getPropertyValue(
                DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX + ":" + DossierConstants.DOSSIER_TYPE_QUESTION
            )
        )
            .thenReturn("monType");
        when(
            questionDocument.getPropertyValue(
                DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX + ":" + DossierConstants.DOSSIER_NUMERO_QUESTION
            )
        )
            .thenReturn(1L);
        when(
            questionDocument.getPropertyValue(
                DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX +
                ":" +
                DossierConstants.DOSSIER_CIVILITE_AUTEUR_QUESTION
            )
        )
            .thenReturn("Mr");
        when(
            questionDocument.getPropertyValue(
                DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX + ":" + DossierConstants.DOSSIER_PRENOM_AUTEUR_QUESTION
            )
        )
            .thenReturn("prenom");
        when(
            questionDocument.getPropertyValue(
                DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX + ":" + DossierConstants.DOSSIER_NOM_AUTEUR_QUESTION
            )
        )
            .thenReturn("nom");
        when(
            questionDocument.getPropertyValue(
                DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX +
                ":" +
                DossierConstants.DOSSIER_GROUPE_POLITIQUE_QUESTION
            )
        )
            .thenReturn("politique");
    }
}
