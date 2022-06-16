package fr.dila.reponses.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import avro.shaded.com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.service.DossierService;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.organigramme.OrganigrammeNodeDeletionProblem;
import fr.dila.st.api.service.organigramme.OrganigrammeNodeDeletionProblem.ProblemScope;
import fr.dila.st.api.service.organigramme.OrganigrammeNodeDeletionProblem.ProblemType;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.api.service.organigramme.STUsAndDirectionService;
import fr.dila.st.core.organigramme.EntiteNodeImpl;
import fr.dila.st.core.organigramme.PosteNodeImpl;
import fr.dila.st.core.organigramme.UniteStructurelleNodeImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PowerMockIgnore("javax.management.*")
@PrepareForTest({ ReponsesServiceLocator.class, STServiceLocator.class })
public class ReponsesOrganigrammeServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private DossierService mockDossierService;

    @Mock
    private OrganigrammeService mockOrganigrammeService;

    @Mock
    private STUsAndDirectionService mockSTUsAndDirectionService;

    @Mock
    private STPostesService mockSTPostesService;

    @Mock
    private Dossier mockDossier;

    @Mock
    private CoreSession mockSession;

    @Mock
    private DocumentModel mockDocumentModel;

    @Mock
    private Question mockQuestion = mock(Question.class);

    private static final String sourceNumeroQuestion = "SOURCE 1234567";

    @Before
    public void setUp() {
        PowerMockito.mockStatic(ReponsesServiceLocator.class);
        when(ReponsesServiceLocator.getDossierService()).thenReturn(mockDossierService);

        when(mockSession.getDocument(Mockito.any())).thenReturn(mockDocumentModel);
        when(mockDossier.getQuestion(Mockito.any())).thenReturn(mockQuestion);
        when(mockDossier.getDocument()).thenReturn(mockDocumentModel);
        when(mockDocumentModel.getAdapter(Mockito.eq(Dossier.class))).thenReturn(mockDossier);
        when(mockQuestion.getSourceNumeroQuestion()).thenReturn(sourceNumeroQuestion);
    }

    @Test
    public void testGetAllUserInSubNode() {
        String nodeId1 = "60000518";
        String nodeId2 = "60002229";
        Set<String> ministereIds = ImmutableSet.of(nodeId1, nodeId2);

        PowerMockito.mockStatic(STServiceLocator.class);
        when(STServiceLocator.getOrganigrammeService()).thenReturn(mockOrganigrammeService);
        when(STServiceLocator.getSTUsAndDirectionService()).thenReturn(mockSTUsAndDirectionService);
        when(STServiceLocator.getSTPostesService()).thenReturn(mockSTPostesService);

        when(mockSTUsAndDirectionService.getUniteStructurelleEnfant(nodeId1, OrganigrammeType.MINISTERE))
            .thenReturn(Collections.emptyList());
        when(mockSTPostesService.getPosteNodeEnfant(nodeId2, OrganigrammeType.MINISTERE))
            .thenReturn(Collections.emptyList());

        EntiteNode ministere1 = new EntiteNodeImpl();
        ministere1.setId(nodeId1);
        ministere1.setLabel("Ministere 1");

        PosteNode poste1 = new PosteNodeImpl();
        poste1.setMembers(ImmutableList.of("user1", "user2", "user3"));
        poste1.setEntiteParentList(ImmutableList.of(ministere1));

        ministere1.setSubPostesList(ImmutableList.of(poste1));

        when(mockOrganigrammeService.getOrganigrammeNodeById(nodeId1, OrganigrammeType.MINISTERE))
            .thenReturn(ministere1);

        EntiteNode ministere2 = new EntiteNodeImpl();
        ministere2.setId(nodeId2);
        ministere2.setLabel("Ministere 2");

        UniteStructurelleNode us2 = new UniteStructurelleNodeImpl();
        us2.setId("60005001");
        us2.setLabel("Unite Structurelle 2");
        us2.setEntiteParentList(ImmutableList.of(ministere2));

        ministere2.setSubUnitesStructurellesList(ImmutableList.of(us2));

        PosteNode poste2 = new PosteNodeImpl();
        poste2.setMembers(ImmutableList.of("user2", "user4", "user5"));
        poste2.setUniteStructurelleParentList(ImmutableList.of(us2));

        us2.setSubPostesList(ImmutableList.of(poste2));

        when(mockOrganigrammeService.getOrganigrammeNodeById(nodeId2, OrganigrammeType.MINISTERE))
            .thenReturn(ministere2);

        OrganigrammeService service = new ReponsesOrganigrammeServiceImpl();
        List<String> usernames = service.getAllUserInSubNode(ministereIds);

        assertThat(usernames).containsExactlyInAnyOrder("user1", "user2", "user3", "user4", "user5");
    }

    @Test
    public void testGetDossierIdentification() {
        assertEquals(
            sourceNumeroQuestion,
            new ReponsesOrganigrammeServiceImpl().getDossierIdentification("1", mockSession)
        );
    }

    @Test
    public void testGetFeuilleRouteIdentification() {
        CoreSession mockSession = mock(CoreSession.class);
        String fdrName = "nom FDR";
        FeuilleRoute mockFdr = mock(FeuilleRoute.class);
        when(mockFdr.getName()).thenReturn(fdrName);
        assertEquals(
            fdrName,
            new ReponsesOrganigrammeServiceImpl().getFeuilleRouteIdentification(mockFdr, mockSession)
        );
    }

    @Test
    public void testValidateDeleteDirectionDossiers_noProblem() {
        when(mockDossierService.getDossierRattacheToDirection(Mockito.any(), Mockito.anyString()))
            .thenReturn(new ArrayList<>());

        UniteStructurelleNode node = new UniteStructurelleNodeImpl();

        assertTrue(new ReponsesOrganigrammeServiceImpl().validateDeleteDirectionDossiers(node, mockSession).isEmpty());
    }

    @Test
    public void testValidateDeleteDirectionDossiers_problems() {
        DocumentModel mockDocModel1 = mock(DocumentModel.class);
        DocumentModel mockDocModel2 = mock(DocumentModel.class);
        Question mockQuestion = mock(Question.class);
        Dossier mockDossier = mock(Dossier.class);

        String docModel2Id = "12345";

        when(mockDossierService.getDossierRattacheToDirection(Mockito.any(), Mockito.anyString()))
            .thenReturn(Lists.newArrayList(mockDocModel1));
        when(mockDocModel1.getAdapter(Mockito.any())).thenReturn(mockQuestion);
        when(mockQuestion.getDossier(Mockito.any())).thenReturn(mockDossier);
        when(mockDossier.getDocument()).thenReturn(mockDocModel2);
        when(mockDocModel2.getId()).thenReturn(docModel2Id);

        UniteStructurelleNode node = new UniteStructurelleNodeImpl();
        String label = "label unite structurelle";
        node.setLabel(label);

        Collection<OrganigrammeNodeDeletionProblem> problems = new ReponsesOrganigrammeServiceImpl()
        .validateDeleteDirectionDossiers(node, mockSession);
        assertFalse(problems.isEmpty());
        assertEquals(1, problems.size());
        OrganigrammeNodeDeletionProblem problem = new ArrayList<>(problems).get(0);
        assertEquals(label, problem.getBlockedObjIdentification());
        assertEquals(sourceNumeroQuestion, problem.getBlockingObjIdentification());
        assertEquals(ProblemScope.DOSSIER, problem.getBlockingObjType());
        assertNull(problem.getPosteInfo());
        assertEquals(ProblemType.DOSSIER_ATTACHED_TO_DIRECTION, problem.getProblemType());
    }
}
