package fr.dila.reponses.core.feuilleroute;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.service.ReponseFeuilleRouteService;
import fr.dila.reponses.core.service.ReponseFeuilleRouteServiceImpl;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.core.service.STServiceLocator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ ReponseFeuilleRouteServiceImpl.class, SSServiceLocator.class, STServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class TestFeuilleRouteLabelStep {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private ReponseFeuilleRouteService feuilleRouteService;

    @Mock
    private CloseableCoreSession session;

    @Mock
    private DocumentModel stepDoc;

    @Mock
    private SSRouteStep step;

    @Mock
    private DocumentModel stepDoc2;

    @Mock
    private SSRouteStep step2;

    @Mock
    private DocumentModel stepDoc3;

    @Mock
    private SSRouteStep step3;

    @Mock
    private MailboxPosteService mailboxPostService;

    @Mock
    private STPostesService posteService;

    @Mock
    private DossierLink dossierLink;

    @Mock
    private PosteNode poste;

    @Mock
    private IdRef stepDocRef;

    @Before
    public void setUp() throws Exception {
        feuilleRouteService = PowerMockito.spy(new ReponseFeuilleRouteServiceImpl());

        PowerMockito.mockStatic(SSServiceLocator.class);
        PowerMockito.mockStatic(STServiceLocator.class);

        when(SSServiceLocator.getMailboxPosteService()).thenReturn(mailboxPostService);
        when(STServiceLocator.getSTPostesService()).thenReturn(posteService);

        Mockito.when(stepDoc.getAdapter(SSRouteStep.class)).thenReturn(step);
        Mockito.when(step.getType()).thenReturn("1");
    }

    @Test
    public void testGetEtapesAVenir() throws Exception {
        List<DocumentModel> emptyList = new ArrayList<>();
        List<DocumentModel> stepList1 = new ArrayList<>();
        List<DocumentModel> stepList2 = new ArrayList<>();
        List<DocumentModel> stepList3 = new ArrayList<>();
        stepList1.add(stepDoc);
        stepList2.addAll(stepList1);
        stepList2.add(stepDoc2);
        stepList3.addAll(stepList2);
        stepList3.add(stepDoc3);

        Mockito.when(feuilleRouteService.getRunningSteps(Mockito.any(), Mockito.any())).thenReturn(stepList1);
        Mockito.when(stepDoc2.getAdapter(SSRouteStep.class)).thenReturn(step2);
        Mockito.when(stepDoc3.getAdapter(SSRouteStep.class)).thenReturn(step3);
        PowerMockito.doReturn(emptyList).when(feuilleRouteService, "getRunningSteps", any(), Mockito.eq("0"));
        PowerMockito.doReturn(stepList1).when(feuilleRouteService, "getRunningSteps", any(), Mockito.eq("1"));
        PowerMockito.doReturn(stepList1).when(feuilleRouteService, "getRunningSteps", any(), Mockito.eq("2"));
        PowerMockito.doReturn(stepList1).when(feuilleRouteService, "getRunningSteps", any(), Mockito.eq("3"));
        PowerMockito
            .doReturn(stepList1)
            .when(feuilleRouteService, "findNextSteps", any(), Mockito.eq("1"), any(), any());
        PowerMockito
            .doReturn(stepList2)
            .when(feuilleRouteService, "findNextSteps", any(), Mockito.eq("2"), any(), any());
        PowerMockito
            .doReturn(stepList3)
            .when(feuilleRouteService, "findNextSteps", any(), Mockito.eq("3"), any(), any());
        PowerMockito.doReturn(poste).when(feuilleRouteService, "getPosteByStep", any());
        Mockito.when(poste.getLabel()).thenReturn("Poste 1");
        Mockito.when(step2.getType()).thenReturn("2");
        Mockito.when(step3.getType()).thenReturn("3");
        Mockito.when(stepDoc3.isFolder()).thenReturn(true);

        Assert.assertTrue(feuilleRouteService.getEtapesAVenir(session, "0").isEmpty());
        Assert.assertEquals(
            Arrays.asList("Pour rédaction - Poste 1"),
            feuilleRouteService.getEtapesAVenir(session, "1")
        );
    }

    @Test
    public void testGetCurrentStep() throws Exception {
        PowerMockito.whenNew(IdRef.class).withAnyArguments().thenReturn(stepDocRef);
        Mockito.when(session.getDocument(stepDocRef)).thenReturn(stepDoc);
        Mockito.when(stepDoc.getAdapter(SSRouteStep.class)).thenReturn(step);
        Mockito.when(step.getDistributionMailboxId()).thenReturn("poste-00001");
        Mockito.when(mailboxPostService.getPosteIdFromMailboxId("poste-00001")).thenReturn("00001");
        Mockito.when(posteService.getPoste(Mockito.eq("00001"))).thenReturn(poste);
        Mockito.when(poste.getLabel()).thenReturn("Poste 1");
        Mockito.when(dossierLink.getRoutingTaskType()).thenReturn("1");

        Assert.assertEquals("Pour rédaction - Poste 1", feuilleRouteService.getCurrentStep(session, dossierLink));
    }

    @Test
    public void testGetLastStepDate() throws Exception {
        List<DocumentModel> emptyList = new ArrayList<>();
        List<DocumentModel> list = new ArrayList<>();
        Calendar date1 = Calendar.getInstance();
        date1.set(2019, 01, 01);
        Calendar date2 = Calendar.getInstance();
        date2.set(2020, 01, 01);
        list.add(stepDoc);
        list.add(stepDoc2);
        Mockito.when(step.getDateFinEtape()).thenReturn(date1);
        Mockito.when(stepDoc2.getAdapter(SSRouteStep.class)).thenReturn(step2);
        Mockito.when(step2.getDateFinEtape()).thenReturn(date2);
        PowerMockito.doReturn(emptyList).when(feuilleRouteService, "findAllSteps", any(), Mockito.eq("0"));
        PowerMockito.doReturn(list).when(feuilleRouteService, "findAllSteps", any(), Mockito.eq("1"));

        Assert.assertNull(feuilleRouteService.getLastStepDate(session, "0"));
        Assert.assertEquals(date2.getTime(), feuilleRouteService.getLastStepDate(session, "1"));
    }
}
