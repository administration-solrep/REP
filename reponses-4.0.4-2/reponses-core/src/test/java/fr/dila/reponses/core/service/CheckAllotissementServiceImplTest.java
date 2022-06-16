package fr.dila.reponses.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.cases.flux.QuestionStateChange;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.exception.CheckAllotissementException;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.api.service.CheckAllotissementService;
import fr.dila.reponses.api.service.DocumentRoutingService;
import fr.dila.reponses.api.service.ReponsesCorbeilleService;
import fr.dila.ss.api.constant.SSFeuilleRouteConstant;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.core.organigramme.PosteNodeImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.model.RouteTableElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ ReponsesServiceLocator.class, STServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class CheckAllotissementServiceImplTest {
    private static final List<String> QUESTION_ETATS = ImmutableList.of(
        VocabularyConstants.ETAT_QUESTION_SIGNALEE,
        VocabularyConstants.ETAT_QUESTION_RENOUVELEE,
        VocabularyConstants.ETAT_QUESTION_RETIREE,
        VocabularyConstants.ETAT_QUESTION_CADUQUE,
        VocabularyConstants.ETAT_QUESTION_REATTRIBUEEE,
        VocabularyConstants.ETAT_QUESTION_EN_COURS,
        VocabularyConstants.ETAT_QUESTION_REPONDU,
        VocabularyConstants.ETAT_QUESTION_CLOTURE_AUTRE,
        VocabularyConstants.ETAT_QUESTION_RAPPELE
    );
    private static final List<String> QUESTION_ETATS_FIN = ImmutableList.of(
        VocabularyConstants.ETAT_QUESTION_RETIREE,
        VocabularyConstants.ETAT_QUESTION_CADUQUE,
        VocabularyConstants.ETAT_QUESTION_REPONDU,
        VocabularyConstants.ETAT_QUESTION_CLOTURE_AUTRE
    );

    private static final List<Boolean> BOOLEAN_VALUES = ImmutableList.of(true, false);
    private static final Logger LOG = LoggerFactory.getLogger(CheckAllotissementServiceImplTest.class);

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private Question mockQuestion;

    @Mock
    private CoreSession mockSession;

    @Mock
    private SSPrincipal mockPrincipal;

    @Mock
    private ReponsesCorbeilleService mockCorbeilleService;

    @Mock
    private STPostesService mockPostesService;

    @Mock
    private DocumentModel mockDocumentModel;

    @Mock
    private DocumentRef mockDocumentRef;

    @Mock
    private DocumentModel mockDlDocModel;

    @Mock
    private QuestionStateChange mockQuestionEtatChange;

    @Mock
    private Dossier mockDossier;

    @Mock
    private Reponse mockReponse;

    @Mock
    private DocumentRoutingService mockDRService;

    @Mock
    private AllotissementService mockAllotissementService;

    private CheckAllotissementService service;

    @Before
    public void before() {
        PowerMockito.mockStatic(ReponsesServiceLocator.class);
        when(ReponsesServiceLocator.getCorbeilleService()).thenReturn(mockCorbeilleService);
        when(ReponsesServiceLocator.getDocumentRoutingService()).thenReturn(mockDRService);
        when(ReponsesServiceLocator.getAllotissementService()).thenReturn(mockAllotissementService);

        PowerMockito.mockStatic(STServiceLocator.class);
        when(STServiceLocator.getSTPostesService()).thenReturn(mockPostesService);

        when(mockSession.getDocument(Mockito.any(DocumentRef.class))).thenReturn(mockDocumentModel);
        when(mockSession.getPrincipal()).thenReturn(mockPrincipal);

        when(mockQuestion.getEtatQuestion(mockSession)).thenReturn(mockQuestionEtatChange);
        when(mockQuestion.getDocument()).thenReturn(mockDocumentModel);
        when(mockDocumentModel.getRef()).thenReturn(mockDocumentRef);
        when(mockQuestion.getDossier(mockSession)).thenReturn(mockDossier);
        when(mockDossier.getReponse(mockSession)).thenReturn(mockReponse);

        service = new CheckAllotissementServiceImpl();
    }

    @Test
    public void runAllTests() {
        StringBuffer report = new StringBuffer();
        for (boolean isAdmin : BOOLEAN_VALUES) {
            for (boolean hasDossierLink : BOOLEAN_VALUES) {
                for (String newState : QUESTION_ETATS) {
                    for (boolean isQE : BOOLEAN_VALUES) {
                        // Par défaut, tout devrait retourner une erreur
                        boolean hasError = true;
                        LOG.warn(
                            "Test en cours : \n état : " +
                            newState +
                            "\n estAdmin : " +
                            isAdmin +
                            "\n accesDossierLink : " +
                            hasDossierLink +
                            "\n est une QE : " +
                            isQE
                        );

                        // Admin et question en cours et QE
                        if (!QUESTION_ETATS_FIN.contains(newState) && isQE && (isAdmin || hasDossierLink)) {
                            hasError = false;
                        }

                        // En dehors de la méthode buildant les tests pour ne pas géner le cas plus bas
                        if (!QUESTION_ETATS_FIN.contains(newState)) {
                            when(mockDRService.getFeuilleRouteElements(any(), any())).thenReturn(new ArrayList<>());
                        } else {
                            List<RouteTableElement> lstRoute = Lists.newArrayList(
                                buildFakeTransmissionAssembleeRouteTableElement()
                            );
                            when(mockDRService.getFeuilleRouteElements(any(), any())).thenReturn(lstRoute);
                        }

                        if (hasError) {
                            CheckAllotissementException excep = catchThrowableOfType(
                                () -> mockAndTest(isAdmin, hasDossierLink, newState, isQE),
                                CheckAllotissementException.class
                            );
                            assertThat(excep).isNotNull();
                        } else {
                            try {
                                mockAndTest(isAdmin, hasDossierLink, newState, isQE);
                            } catch (CheckAllotissementException e) {
                                fail("Should not throw CheckAllotissementException : " + e.getMessage());
                            }
                        }
                    }
                }
            }
        }

        //Verification manuelle du cas où on est une QE sur un poste pas chargé de mission avec un dossierLink pour une QE qui a passé l'étape de transmission aux assemblées

        List<RouteTableElement> lstRoute = Lists.newArrayList(buildFakeTransmissionAssembleeRouteTableElement());
        when(mockDRService.getFeuilleRouteElements(any(), any())).thenReturn(lstRoute);
        CheckAllotissementException excep = catchThrowableOfType(
            () -> mockAndTest(false, true, VocabularyConstants.ETAT_QUESTION_EN_COURS, true),
            CheckAllotissementException.class
        );
        assertThat(excep).isNotNull();

        if (report.length() > 0) {
            fail(report.toString());
        }
    }

    private PosteNode buildFakePoste(boolean isChargeMission) {
        PosteNode poste = new PosteNodeImpl();
        poste.setChargeMissionSGG(isChargeMission);
        poste.setLabel("Test");

        return poste;
    }

    private RouteTableElement buildFakeTransmissionAssembleeRouteTableElement() {
        RouteTableElement mockRouteElement = mock(RouteTableElement.class);
        DocumentModel mockDoc = mock(DocumentModel.class);
        SSRouteStep mockRouteStep = mock(SSRouteStep.class);

        when(mockRouteElement.getDocument()).thenReturn(mockDoc);
        when(mockDoc.isFolder()).thenReturn(false);
        when(mockDoc.getAdapter(SSRouteStep.class)).thenReturn(mockRouteStep);

        when(mockRouteElement.getElement()).thenReturn(mockRouteStep);
        when(mockRouteStep.getDocument()).thenReturn(mockDoc);
        when(mockRouteStep.isDone()).thenReturn(true);
        when(mockRouteStep.getValidationStatus())
            .thenReturn(SSFeuilleRouteConstant.ROUTING_TASK_VALIDATION_STATUS_AVIS_FAVORABLE_VALUE);
        when(mockRouteStep.getType()).thenReturn(VocabularyConstants.ROUTING_TASK_TYPE_TRANSMISSION_ASSEMBLEE);

        return mockRouteElement;
    }

    private void mockAndTest(boolean isAdmin, boolean hasUpdatableDossierLink, String newState, boolean isQE)
        throws CheckAllotissementException {
        when(mockPostesService.getPostesNodes(any())).thenReturn(Lists.newArrayList(buildFakePoste(isAdmin)));

        if (hasUpdatableDossierLink) {
            when(mockCorbeilleService.findUpdatableDossierLinkForDossier(mockSession, mockDocumentModel))
                .thenReturn(Collections.singletonList(mockDlDocModel));
        } else {
            when(mockCorbeilleService.findUpdatableDossierLinkForDossier(mockSession, mockDocumentModel))
                .thenReturn(new ArrayList<>());
        }
        when(mockQuestionEtatChange.getNewState()).thenReturn(newState);
        when(mockQuestion.isQuestionTypeEcrite()).thenReturn(isQE);

        service.checkQuestionCanBeAllotie(mockQuestion, mockSession);
    }

    @Test
    public void testIsQuestionElligibleToLot() {
        when(mockSession.hasPermission(mockQuestion.getDocument().getRef(), SecurityConstants.READ)).thenReturn(false);

        String sourceNumQuestion = "sourceNumeroQuestion";
        when(mockQuestion.getSourceNumeroQuestion()).thenReturn(sourceNumQuestion);

        assertThatThrownBy(() -> service.checkQuestionEligibilityInLot(mockQuestion, true, mockSession))
            .hasMessage(ResourceHelper.getString("reponses.dossier.connexe.droit", sourceNumQuestion));

        when(mockSession.hasPermission(mockQuestion.getDocument().getRef(), SecurityConstants.READ)).thenReturn(true);
        when(mockAllotissementService.isAllotit(mockQuestion, mockSession)).thenReturn(true);

        assertThatThrownBy(() -> service.checkQuestionEligibilityInLot(mockQuestion, false, mockSession))
            .hasMessage(ResourceHelper.getString("reponses.dossier.connexe.deja.allotit", sourceNumQuestion));

        assertThatCode(() -> service.checkQuestionEligibilityInLot(mockQuestion, true, mockSession))
            .doesNotThrowAnyException();
    }
}
