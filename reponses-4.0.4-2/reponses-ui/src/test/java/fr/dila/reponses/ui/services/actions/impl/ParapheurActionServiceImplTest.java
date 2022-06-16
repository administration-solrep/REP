package fr.dila.reponses.ui.services.actions.impl;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.service.ReponseService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.bean.ParapheurDTO;
import fr.dila.reponses.ui.services.actions.ReponseActionService;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.ss.ui.services.actions.SSCorbeilleActionService;
import fr.dila.st.core.feature.SolonMockitoFeature;
import fr.dila.st.ui.services.actions.DossierLockActionService;
import fr.dila.st.ui.services.actions.STActionsServiceLocator;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.HashSet;
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
import org.nuxeo.runtime.test.runner.Features;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(
    {
        STActionsServiceLocator.class,
        SSActionsServiceLocator.class,
        ReponsesActionsServiceLocator.class,
        ReponsesServiceLocator.class
    }
)
@PowerMockIgnore("javax.management.*")
@Features(SolonMockitoFeature.class)
public class ParapheurActionServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private ParapheurActionServiceImpl parapheurService = new ParapheurActionServiceImpl();

    private static final String ID_MIN = "500";

    @Mock
    private SpecificContext context;

    @Mock
    private CoreSession session;

    @Mock
    private ReponseActionService reponseActionService;

    @Mock
    private DossierLockActionService dossierLockActionService;

    @Mock
    private SSCorbeilleActionService ssCorbeilleActionService;

    @Mock
    private DossierLink dossierLink;

    @Mock
    private SolonAlertManager msgQueue;

    @Mock
    private Dossier dossier;

    @Mock
    private DocumentModel dossierDoc;

    @Mock
    private Reponse reponse;

    @Mock
    private DocumentModel reponseDoc;

    @Mock
    private Question question;

    @Mock
    private DocumentModel questionDoc;

    @Mock
    private ParapheurDTO parapheurDTO;

    @Mock
    private SSPrincipal ssPrincipal;

    @Mock
    private ReponseService reponseService;

    @Before
    public void before() {
        PowerMockito.mockStatic(STActionsServiceLocator.class);
        when(STActionsServiceLocator.getDossierLockActionService()).thenReturn(dossierLockActionService);

        PowerMockito.mockStatic(ReponsesActionsServiceLocator.class);
        when(ReponsesActionsServiceLocator.getReponseActionService()).thenReturn(reponseActionService);

        PowerMockito.mockStatic(SSActionsServiceLocator.class);
        when(SSActionsServiceLocator.getSSCorbeilleActionService()).thenReturn(ssCorbeilleActionService);

        PowerMockito.mockStatic(ReponsesServiceLocator.class);
        when(ReponsesServiceLocator.getReponseService()).thenReturn(reponseService);

        when(context.getCurrentDocument()).thenReturn(dossierDoc);
        when(context.getSession()).thenReturn(session);
        when(context.getMessageQueue()).thenReturn(msgQueue);

        when(dossierDoc.getAdapter(Dossier.class)).thenReturn(dossier);
        when(dossier.getIdMinistereAttributaireCourant()).thenReturn(ID_MIN);
        // dossier is running and is active
        when(dossier.isRunning()).thenReturn(TRUE);
        when(dossier.isActive(session)).thenReturn(TRUE);

        when(dossier.getQuestion(session)).thenReturn(question);
        when(question.getDocument()).thenReturn(questionDoc);
        when(questionDoc.getType()).thenReturn(DossierConstants.QUESTION_DOCUMENT_TYPE);

        when(dossier.getReponse(session)).thenReturn(reponse);
        when(reponse.getDocument()).thenReturn(reponseDoc);
        when(reponseDoc.getType()).thenReturn(DossierConstants.REPONSE_DOCUMENT_TYPE);

        when(session.getPrincipal()).thenReturn(ssPrincipal);

        Set<String> ministereIdSet = new HashSet<>();
        ministereIdSet.add(ID_MIN);
        when(ssPrincipal.getMinistereIdSet()).thenReturn(ministereIdSet);
    }

    // if dossierlink is present, ok
    @Test
    public void testSaveReponseIfDossierLink() {
        when(dossierLockActionService.getCanUnlockCurrentDossier(context)).thenReturn(TRUE);
        when(reponseActionService.isReponseAtLastVersion(session, dossierDoc)).thenReturn(TRUE);
        when(ssCorbeilleActionService.getCurrentDossierLink(context)).thenReturn(dossierLink);

        parapheurService.saveDossier(context, parapheurDTO);

        checkSaveDossier(true);
    }

    // if dossierlink is not present, further tests
    @Test
    public void testSaveReponseIfAdminUpdater() {
        when(dossierLockActionService.getCanUnlockCurrentDossier(context)).thenReturn(TRUE);
        when(reponseActionService.isReponseAtLastVersion(session, dossierDoc)).thenReturn(TRUE);
        when(ssCorbeilleActionService.getCurrentDossierLink(context)).thenReturn(null);
        when(ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_PARAPHEUR_ADMIN_UPDATER)).thenReturn(TRUE);

        parapheurService.saveDossier(context, parapheurDTO);

        checkSaveDossier(true);
    }

    @Test
    public void testSaveReponseIfPrincipalNotAdminUpdater() {
        when(dossierLockActionService.getCanUnlockCurrentDossier(context)).thenReturn(TRUE);
        when(reponseActionService.isReponseAtLastVersion(session, dossierDoc)).thenReturn(TRUE);
        when(ssCorbeilleActionService.getCurrentDossierLink(context)).thenReturn(null);
        when(ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_PARAPHEUR_ADMIN_UPDATER)).thenReturn(FALSE);

        parapheurService.saveDossier(context, parapheurDTO);

        checkSaveDossier(false);
    }

    private void checkSaveDossier(boolean shouldSaveReponse) {
        Mockito
            .verify(reponseActionService, shouldSaveReponse ? times(1) : never())
            .saveReponse(context, reponseDoc, dossierDoc);

        // On ne doit jamais sauvegarder la question !
        Mockito.verify(session, never()).saveDocument(questionDoc);
    }

    @Test
    public void testSaveReponseIfDossierNotRunning() {
        when(dossierLockActionService.getCanUnlockCurrentDossier(context)).thenReturn(TRUE);
        when(reponseActionService.isReponseAtLastVersion(session, dossierDoc)).thenReturn(TRUE);
        when(ssCorbeilleActionService.getCurrentDossierLink(context)).thenReturn(null);
        when(ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_PARAPHEUR_ADMIN_UPDATER)).thenReturn(TRUE);
        when(dossier.isRunning()).thenReturn(FALSE);

        parapheurService.saveDossier(context, parapheurDTO);

        checkSaveDossier(false);
    }

    @Test
    public void testSaveReponseIfDossierNotActive() {
        when(dossierLockActionService.getCanUnlockCurrentDossier(context)).thenReturn(TRUE);
        when(reponseActionService.isReponseAtLastVersion(session, dossierDoc)).thenReturn(TRUE);
        when(ssCorbeilleActionService.getCurrentDossierLink(context)).thenReturn(null);
        when(ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_PARAPHEUR_ADMIN_UPDATER)).thenReturn(TRUE);
        when(dossier.isActive(session)).thenReturn(FALSE);

        parapheurService.saveDossier(context, parapheurDTO);

        checkSaveDossier(false);
    }

    @Test
    public void testSaveReponseIfAdminMinUpdater() {
        when(dossierLockActionService.getCanUnlockCurrentDossier(context)).thenReturn(TRUE);
        when(reponseActionService.isReponseAtLastVersion(session, dossierDoc)).thenReturn(TRUE);
        when(ssCorbeilleActionService.getCurrentDossierLink(context)).thenReturn(null);
        when(ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_PARAPHEUR_ADMIN_MIN_UPDATER)).thenReturn(TRUE);

        parapheurService.saveDossier(context, parapheurDTO);

        checkSaveDossier(true);
    }

    @Test
    public void testSaveReponseIfNotMinistereAttrCourant() {
        when(dossierLockActionService.getCanUnlockCurrentDossier(context)).thenReturn(TRUE);
        when(reponseActionService.isReponseAtLastVersion(session, dossierDoc)).thenReturn(TRUE);
        when(ssCorbeilleActionService.getCurrentDossierLink(context)).thenReturn(null);
        when(ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_PARAPHEUR_ADMIN_MIN_UPDATER)).thenReturn(TRUE);

        Set<String> ministereIdSet = new HashSet<>();
        when(ssPrincipal.getMinistereIdSet()).thenReturn(ministereIdSet);

        parapheurService.saveDossier(context, parapheurDTO);

        checkSaveDossier(false);
    }
}
