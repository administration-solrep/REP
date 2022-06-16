package fr.dila.reponses.ui.services.impl;

import static fr.dila.ss.ui.services.impl.SSMailboxListComponentServiceImpl.REFRESH_CORBEILLE_KEY;
import static java.lang.Boolean.FALSE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import fr.dila.reponses.api.constant.ProfilUtilisateurConstants;
import fr.dila.reponses.api.service.ProfilUtilisateurService;
import fr.dila.reponses.api.service.ReponsesCorbeilleTreeService;
import fr.dila.reponses.api.service.ReponsesCorbeilleTreeService.EtatSignalement;
import fr.dila.reponses.api.user.ProfilUtilisateur;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.bean.RepMailboxListDTO;
import fr.dila.ss.api.constant.SSProfilUtilisateurConstants;
import fr.dila.ss.api.enums.TypeRegroupement;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.service.STUserService;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.organigramme.EntiteNodeImpl;
import fr.dila.st.core.organigramme.PosteNodeImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.bean.TreeElementDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.session.UserSession;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(
    { ReponsesServiceLocator.class, STServiceLocator.class, SpecificContext.class, WebContext.class, UserSession.class }
)
@PowerMockIgnore("javax.management.*")
public class ReponsesMailboxListComponentServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private ReponsesMailboxListComponentServiceImpl service;

    @Mock
    private ReponsesCorbeilleTreeService corbeilleTreeService;

    @Mock
    private ProfilUtilisateurService profilUtilisateurService;

    @Mock
    private STMinisteresService ministeresService;

    @Mock
    private OrganigrammeService organigrammeService;

    @Mock
    private UserManager userManagerService;

    @Mock
    private STPostesService posteService;

    @Mock
    private STUserService userService;

    @Mock
    private SpecificContext context;

    @Mock
    private WebContext webContext;

    @Mock
    private UserSession userSession;

    @Mock
    private CoreSession session;

    @Mock
    private SSPrincipal principal;

    @Before
    public void before() {
        PowerMockito.spy(UserSession.class);
        service = new ReponsesMailboxListComponentServiceImpl();

        Mockito.when(context.getWebcontext()).thenReturn(webContext);
        Mockito.when(webContext.getUserSession()).thenReturn(userSession);
        Mockito.when(context.getSession()).thenReturn(session);
        Mockito.when(session.getPrincipal()).thenReturn(principal);
        Mockito.when(userSession.get(REFRESH_CORBEILLE_KEY)).thenReturn(FALSE);

        PowerMockito.mockStatic(ReponsesServiceLocator.class);
        PowerMockito.mockStatic(STServiceLocator.class);

        Mockito.when(ReponsesServiceLocator.getCorbeilleTreeService()).thenReturn(corbeilleTreeService);
        Mockito.when(ReponsesServiceLocator.getProfilUtilisateurService()).thenReturn(profilUtilisateurService);
        Mockito.when(STServiceLocator.getSTMinisteresService()).thenReturn(ministeresService);
        Mockito.when(STServiceLocator.getOrganigrammeService()).thenReturn(organigrammeService);
        Mockito.when(STServiceLocator.getUserManager()).thenReturn(userManagerService);
        Mockito.when(STServiceLocator.getSTPostesService()).thenReturn(posteService);
        Mockito.when(STServiceLocator.getSTUserService()).thenReturn(userService);

        Mockito
            .when(
                corbeilleTreeService.getCorbeilleTreeNiveau1(
                    Mockito.any(),
                    Mockito.eq(TypeRegroupement.PAR_MINISTERE),
                    Mockito.any(),
                    Mockito.any()
                )
            )
            .thenReturn(createMinistereMap());
        Mockito
            .when(
                corbeilleTreeService.getCorbeilleTreeNiveau1(
                    Mockito.any(),
                    Mockito.eq(TypeRegroupement.PAR_POSTE),
                    Mockito.any(),
                    Mockito.any()
                )
            )
            .thenReturn(createPosteMap());
        Mockito
            .when(
                corbeilleTreeService.getCorbeilleTreeNiveau1(
                    Mockito.any(),
                    Mockito.eq(TypeRegroupement.PAR_SIGNALE),
                    Mockito.any(),
                    Mockito.any()
                )
            )
            .thenReturn(createSignaleMap());
        Mockito
            .when(
                corbeilleTreeService.getCorbeilleTreeNiveau2(
                    Mockito.any(),
                    Mockito.eq(TypeRegroupement.PAR_MINISTERE),
                    Mockito.eq("elem1"),
                    Mockito.eq("poste"),
                    Mockito.eq("user")
                )
            )
            .thenReturn(createEtapeMap());

        ProfilUtilisateur profil = Mockito.mock(ProfilUtilisateur.class);
        DocumentModel profilDoc = Mockito.mock(DocumentModel.class);
        Mockito.when(profilUtilisateurService.getProfilUtilisateurForCurrentUser(Mockito.any())).thenReturn(profil);
        Mockito.when(profil.getDocument()).thenReturn(profilDoc);
        Mockito.when(profilDoc.getType()).thenReturn(SSProfilUtilisateurConstants.WORKSPACE_DOCUMENT_TYPE);
        Mockito
            .when(profilDoc.getPropertyValue(ProfilUtilisateurConstants.PROFIL_UTILISATEUR_MASQUER_CORBEILLES_XPATH))
            .thenReturn(false);

        Map<String, EntiteNode> ministeres = new HashMap<>();
        ministeres.put("min-1", buildEntite(1));
        ministeres.put("min-2", buildEntite(2));
        ministeres.put("min-3", buildEntite(3));
        List<EntiteNode> currentMinisteres = new ArrayList<>();
        currentMinisteres.add(ministeres.get("min-1"));
        currentMinisteres.add(ministeres.get("min-2"));
        Set<String> userMinisteres = new HashSet<>();
        userMinisteres.add("min-1");
        userMinisteres.add("min-2");
        Map<String, PosteNode> postes = new HashMap<>();
        postes.put("poste-1", buildPoste(1));
        postes.put("poste-2", buildPoste(2));
        postes.put("poste-3", buildPoste(3));
        Mockito.when(ministeresService.getCurrentMinisteres()).thenReturn(currentMinisteres);
        Mockito
            .when(corbeilleTreeService.getMinisteresParentsFromPostes(Mockito.any(), Mockito.any(), Mockito.any()))
            .thenReturn(userMinisteres);
        when(organigrammeService.getOrganigrammeNodeById(Mockito.anyString(), Mockito.eq(OrganigrammeType.MINISTERE)))
            .thenAnswer((Answer<EntiteNode>) invocation -> ministeres.get(invocation.getArgumentAt(0, String.class)));
        when(organigrammeService.getOrganigrammeNodeById(Mockito.anyString(), Mockito.eq(OrganigrammeType.POSTE)))
            .thenAnswer((Answer<PosteNode>) invocation -> postes.get(invocation.getArgumentAt(0, String.class)));

        PosteNode posteNode = new PosteNodeImpl();
        posteNode.setLabel("posteLabel");
        when(organigrammeService.getOrganigrammeNodeById("poste", OrganigrammeType.POSTE)).thenReturn(posteNode);
        when(posteService.getPosteLabel(Mockito.anyString())).thenReturn("");
        when(posteService.getPosteLabel("poste")).thenReturn("posteLabel");
        DocumentModel userDoc = Mockito.mock(DocumentModel.class);
        when(userManagerService.getUserModel("user")).thenReturn(userDoc);
        STUser stUser = Mockito.mock(STUser.class);
        when(userDoc.getAdapter(STUser.class)).thenReturn(stUser);
        when(stUser.getFirstName()).thenReturn("First");
        when(stUser.getLastName()).thenReturn("Last");
        when(userService.getUserFullNameOrEmpty(Mockito.anyString())).thenReturn("");
        when(userService.getUserFullNameOrEmpty("user")).thenReturn("First Last");
    }

    @Test
    public void testGetDataWithoutAnything() {
        // Sans aucun paramètre en entrée on doit avoir un DTO initialisé avec le 1er
        // niveau par ministère

        Map<String, Object> returnMap = service.getData(context);
        assertNotNull(returnMap);
        assertEquals(5, returnMap.size());
        assertNull(returnMap.get(ReponsesMailboxListComponentServiceImpl.ACTIVE_KEY));
        assertEquals("", returnMap.get(ReponsesMailboxListComponentServiceImpl.POSTE_LABEL_KEY));
        assertEquals("", returnMap.get(ReponsesMailboxListComponentServiceImpl.USER_LABEL_KEY));
        assertNotNull(returnMap.get("mailboxListMap"));
        assertTrue(returnMap.get("mailboxListMap") instanceof RepMailboxListDTO);
        RepMailboxListDTO mlbx = (RepMailboxListDTO) returnMap.get("mailboxListMap");
        assertEquals(TypeRegroupement.PAR_MINISTERE, mlbx.getModeTri());
        assertEquals("", mlbx.getSelectionPoste());
        assertEquals("", mlbx.getSelectionUser());
        assertEquals(false, mlbx.getMasquerCorbeilles());
        assertNotNull(mlbx.getChilds());
        assertEquals(3, mlbx.getChilds().size());

        // Vérifie que la session a bien été mise à jour
        Mockito.verify(userSession).put(ReponsesMailboxListComponentServiceImpl.ACTIVE_KEY, null);
        Mockito.verify(userSession).put("mailboxList", mlbx);
        Mockito.verify(userSession).put(REFRESH_CORBEILLE_KEY, FALSE);
    }

    @Test
    public void testGetDataWithModeTriKey() {
        // en passant le mode de tri via le contexte, le DTO en sortie doit être
        // initialisé pour le mode de tri indiqué

        Mockito
            .when(context.getFromContextData(ReponsesMailboxListComponentServiceImpl.MODE_TRI_KEY))
            .thenReturn(TypeRegroupement.PAR_SIGNALE);

        Map<String, Object> returnMap = service.getData(context);
        assertNotNull(returnMap);
        assertEquals(5, returnMap.size());
        assertNull(returnMap.get(ReponsesMailboxListComponentServiceImpl.ACTIVE_KEY));
        assertNotNull(returnMap.get("mailboxListMap"));
        assertTrue(returnMap.get("mailboxListMap") instanceof RepMailboxListDTO);
        RepMailboxListDTO mlbx = (RepMailboxListDTO) returnMap.get("mailboxListMap");
        assertEquals(TypeRegroupement.PAR_SIGNALE, mlbx.getModeTri());
        assertEquals("", mlbx.getSelectionPoste());
        assertEquals("", mlbx.getSelectionUser());
        assertNotNull(mlbx.getChilds());
        assertEquals(2, mlbx.getChilds().size());
        assertTrue(mlbx.getChilds().get(0).getLabel().startsWith("Signalées"));
        assertTrue(mlbx.getChilds().get(1).getLabel().startsWith("Non signalées"));

        // Vérifie que la session a bien été mise à jour
        Mockito.verify(userSession, Mockito.times(1)).put(ReponsesMailboxListComponentServiceImpl.ACTIVE_KEY, null);
        Mockito.verify(userSession, Mockito.times(1)).put("mailboxList", mlbx);
    }

    @Test
    public void testGetDataWithSelectionKeys() {
        // en passant le poste et user sélectionnés via le contexte sans passer la
        // valeur true pour selectionValidée, le DTO en sortie ne prend pas en compte
        // ces sélections

        Mockito.when(context.getFromContextData(ReponsesMailboxListComponentServiceImpl.POSTE_KEY)).thenReturn("poste");
        Mockito.when(context.getFromContextData(ReponsesMailboxListComponentServiceImpl.USER_KEY)).thenReturn("user");

        Map<String, Object> returnMap = service.getData(context);
        assertNotNull(returnMap);
        assertEquals(5, returnMap.size());
        assertNull(returnMap.get(ReponsesMailboxListComponentServiceImpl.ACTIVE_KEY));
        assertNotNull(returnMap.get("mailboxListMap"));
        assertTrue(returnMap.get("mailboxListMap") instanceof RepMailboxListDTO);
        RepMailboxListDTO mlbx = (RepMailboxListDTO) returnMap.get("mailboxListMap");
        assertEquals("", mlbx.getSelectionPoste());
        assertEquals("", mlbx.getSelectionUser());
        assertNotNull(mlbx.getChilds());
        assertEquals(3, mlbx.getChilds().size());

        // Vérifie que la session a bien été mise à jour
        Mockito.verify(userSession, Mockito.times(1)).put(ReponsesMailboxListComponentServiceImpl.ACTIVE_KEY, null);
        Mockito.verify(userSession, Mockito.times(1)).put("mailboxList", mlbx);
    }

    @Test
    public void testGetDataWithSelectionKeysAndSelectionValidee() {
        // en passant le poste et user sélectionnés via le contexte et la valeur true
        // pour selectionValidée, le DTO en sortie doit être initialisé pour les
        // selections indiquées

        Mockito.when(context.getFromContextData(ReponsesMailboxListComponentServiceImpl.POSTE_KEY)).thenReturn("poste");
        Mockito.when(context.getFromContextData(ReponsesMailboxListComponentServiceImpl.USER_KEY)).thenReturn("user");
        Mockito
            .when(context.getFromContextData(ReponsesMailboxListComponentServiceImpl.SELECTION_VALIDEE_KEY))
            .thenReturn(true);

        Map<String, Object> returnMap = service.getData(context);
        assertNotNull(returnMap);
        assertEquals(7, returnMap.size());
        assertNull(returnMap.get(ReponsesMailboxListComponentServiceImpl.ACTIVE_KEY));
        assertEquals("posteLabel", returnMap.get(ReponsesMailboxListComponentServiceImpl.POSTE_LABEL_KEY));
        assertEquals("First Last", returnMap.get(ReponsesMailboxListComponentServiceImpl.USER_LABEL_KEY));
        assertNotNull(returnMap.get("mailboxListMap"));
        assertTrue(returnMap.get("mailboxListMap") instanceof RepMailboxListDTO);
        RepMailboxListDTO mlbx = (RepMailboxListDTO) returnMap.get("mailboxListMap");
        assertEquals("poste", mlbx.getSelectionPoste());
        assertEquals("user", mlbx.getSelectionUser());
        assertNotNull(mlbx.getChilds());
        assertEquals(3, mlbx.getChilds().size());

        // Vérifie que la session a bien été mise à jour
        Mockito.verify(userSession, Mockito.times(1)).put(ReponsesMailboxListComponentServiceImpl.ACTIVE_KEY, null);
        Mockito.verify(userSession, Mockito.times(1)).put("mailboxList", mlbx);
    }

    @Test
    public void testGetDataWithMasquerCorbeillesKey() {
        // en passant le choix de masquage des corbeilles vides via le contexte,
        // le DTO en sortie doit être initialisé pour le choix indiqué

        Mockito
            .when(context.getFromContextData(ReponsesMailboxListComponentServiceImpl.MASQUER_CORBEILLES_KEY))
            .thenReturn(true);

        Map<String, Object> returnMap = service.getData(context);
        assertNotNull(returnMap);
        assertEquals(5, returnMap.size());
        assertNull(returnMap.get(ReponsesMailboxListComponentServiceImpl.ACTIVE_KEY));
        assertNotNull(returnMap.get("mailboxListMap"));
        assertTrue(returnMap.get("mailboxListMap") instanceof RepMailboxListDTO);
        RepMailboxListDTO mlbx = (RepMailboxListDTO) returnMap.get("mailboxListMap");
        assertEquals(true, mlbx.getMasquerCorbeilles());
        assertNotNull(mlbx.getChilds());
        assertEquals(2, mlbx.getChilds().size());

        // Vérifie que la session a bien été mise à jour
        Mockito.verify(userSession, Mockito.times(1)).put(ReponsesMailboxListComponentServiceImpl.ACTIVE_KEY, null);
        Mockito.verify(userSession, Mockito.times(1)).put("mailboxList", mlbx);
    }

    @Test
    public void testGetDataWithActiveKey() {
        // en passant l'item actif dans le contexte, on doit le récupérer en sortie (Pas
        // d'impact sur le DTO)

        Mockito
            .when(context.getFromContextData(ReponsesMailboxListComponentServiceImpl.ACTIVE_KEY))
            .thenReturn("elem1");

        Map<String, Object> returnMap = service.getData(context);
        assertNotNull(returnMap);
        assertEquals(5, returnMap.size());
        assertNotNull(returnMap.get(ReponsesMailboxListComponentServiceImpl.ACTIVE_KEY));
        assertEquals("elem1", returnMap.get(ReponsesMailboxListComponentServiceImpl.ACTIVE_KEY));

        // Vérifie que la session a bien été mise à jour
        Mockito.verify(userSession, Mockito.times(1)).put(ReponsesMailboxListComponentServiceImpl.ACTIVE_KEY, "elem1");
    }

    @Test
    public void testGetDataWithSessionDTO() {
        // Si un DTO existe en session il doit être récupéré

        RepMailboxListDTO dto = new RepMailboxListDTO();
        dto.setModeTri(TypeRegroupement.PAR_MINISTERE);
        List<TreeElementDTO> elements = new ArrayList<>();
        TreeElementDTO element = new TreeElementDTO();
        element.setKey("elem1");
        elements.add(element);
        dto.setChilds(elements);

        Mockito.when(userSession.get("mailboxList")).thenReturn(dto);
        Mockito.when(userSession.containsKey("mailboxList")).thenReturn(true);

        Map<String, Object> returnMap = service.getData(context);
        assertNotNull(returnMap);
        assertEquals(5, returnMap.size());
        assertNull(returnMap.get(ReponsesMailboxListComponentServiceImpl.ACTIVE_KEY));
        assertNotNull(returnMap.get("mailboxListMap"));
        assertTrue(returnMap.get("mailboxListMap") instanceof RepMailboxListDTO);
        RepMailboxListDTO mlbx = (RepMailboxListDTO) returnMap.get("mailboxListMap");
        assertEquals(dto, mlbx);
        assertEquals(1, mlbx.getChilds().size());
        assertEquals("elem1", mlbx.getChilds().get(0).getKey());

        // Vérifie que la session a bien été mise à jour
        Mockito.verify(userSession, Mockito.times(1)).put("mailboxList", dto);
    }

    @Test
    public void testGetDataWithSessionDTOAndNotSameModeTri() {
        // Si un DTO existe en session il doit être récupéré
        // Si le mode de tri dans le contexte est différent du DTO alors le DTO est
        // modifié pour représenter le mode de tri désiré

        RepMailboxListDTO dto = new RepMailboxListDTO();
        dto.setModeTri(TypeRegroupement.PAR_MINISTERE);
        List<TreeElementDTO> elements = new ArrayList<>();
        TreeElementDTO element = new TreeElementDTO();
        element.setKey("elem1");
        elements.add(element);
        dto.setChilds(elements);

        Mockito.when(userSession.get("mailboxList")).thenReturn(dto);
        Mockito.when(userSession.containsKey("mailboxList")).thenReturn(true);
        Mockito
            .when(context.getFromContextData(ReponsesMailboxListComponentServiceImpl.MODE_TRI_KEY))
            .thenReturn(TypeRegroupement.PAR_POSTE);

        Map<String, Object> returnMap = service.getData(context);
        assertNotNull(returnMap);
        assertEquals(5, returnMap.size());
        assertNotNull(returnMap.get("mailboxListMap"));
        assertTrue(returnMap.get("mailboxListMap") instanceof RepMailboxListDTO);
        RepMailboxListDTO mlbx = (RepMailboxListDTO) returnMap.get("mailboxListMap");
        assertEquals(TypeRegroupement.PAR_POSTE, mlbx.getModeTri());
        assertEquals("", mlbx.getSelectionPoste());
        assertEquals("", mlbx.getSelectionUser());
        assertNotNull(mlbx.getChilds());
        assertEquals(3, mlbx.getChilds().size());

        // Vérifie que la session a bien été mise à jour
        Mockito.verify(userSession, Mockito.times(1)).put("mailboxList", mlbx);
    }

    @Test
    public void testGetDataWithSessionDTOAndSelectedItemExistNotOpen() {
        // Si un DTO existe en session il doit être récupéré
        // Si l'item sélectionné est présent dans les éléments du DTO et que l'élément
        // n'est pas déjà ouvert et qu'il n'a pas d'enfant chargés
        // On charge les enfants de l'item sélectionné et on met à jour le DTO

        RepMailboxListDTO dto = new RepMailboxListDTO();
        dto.setModeTri(TypeRegroupement.PAR_MINISTERE);
        List<TreeElementDTO> elements = new ArrayList<>();
        TreeElementDTO element = new TreeElementDTO();
        element.setKey("elem1");
        element.setCompleteKey("__elem1");
        elements.add(element);
        dto.setChilds(elements);
        dto.setSelectionPoste("poste");
        dto.setSelectionUser("user");

        Mockito.when(userSession.get("mailboxList")).thenReturn(dto);
        Mockito.when(userSession.containsKey("mailboxList")).thenReturn(true);
        Mockito
            .when(context.getFromContextData(ReponsesMailboxListComponentServiceImpl.SELECTED_KEY))
            .thenReturn("__elem1");

        Map<String, Object> returnMap = service.getData(context);
        assertNotNull(returnMap);
        assertEquals(7, returnMap.size());
        assertNull(returnMap.get(ReponsesMailboxListComponentServiceImpl.ACTIVE_KEY));
        assertNotNull(returnMap.get("mailboxListMap"));
        assertTrue(returnMap.get("mailboxListMap") instanceof RepMailboxListDTO);
        RepMailboxListDTO plan = (RepMailboxListDTO) returnMap.get("mailboxListMap");
        assertEquals(1, plan.getChilds().size());

        assertEquals("elem1", plan.getChilds().get(0).getKey());
        assertFalse(plan.getChilds().get(0).getChilds().isEmpty());
        // La liste d'enfant n'existe pas donc on récupère la liste du service via le
        // mock
        assertEquals(5, plan.getChilds().get(0).getChilds().size());
        assertEquals(true, plan.getChilds().get(0).getIsOpen());

        // Vérifie que la session a bien été mise à jour
        Mockito.verify(userSession, Mockito.times(1)).put("mailboxList", dto);
    }

    @Test
    public void testGetDataWithSessionDTOAndSelectedItemExistNotOpenAndListExist() {
        // Si un DTO existe en session il doit être récupéré
        // Si l'item sélectionné est présent dans les éléments du DTO et que l'élément
        // n'est pas déjà ouvert mais qu'il a des enfants chargés
        // On ouvre simplement l'item sans recharger ses enfants

        RepMailboxListDTO dto = new RepMailboxListDTO();
        dto.setModeTri(TypeRegroupement.PAR_MINISTERE);
        List<TreeElementDTO> elements = new ArrayList<>();
        TreeElementDTO element = new TreeElementDTO();
        element.setKey("elem1");
        element.setCompleteKey("__elem1");

        List<TreeElementDTO> childs = new ArrayList<>();
        TreeElementDTO elementChild1 = new TreeElementDTO();
        elementChild1.setKey("child2");
        elementChild1.setCompleteKey("__child2");
        childs.add(elementChild1);

        TreeElementDTO elementChild2 = new TreeElementDTO();
        elementChild2.setKey("child2");
        elementChild2.setCompleteKey("__child2");
        childs.add(elementChild2);

        element.setChilds(childs);

        elements.add(element);
        dto.setChilds(elements);

        Mockito.when(userSession.get("mailboxList")).thenReturn(dto);
        Mockito.when(userSession.containsKey("mailboxList")).thenReturn(true);
        Mockito
            .when(context.getFromContextData(ReponsesMailboxListComponentServiceImpl.SELECTED_KEY))
            .thenReturn("__elem1");

        Map<String, Object> returnMap = service.getData(context);
        assertNotNull(returnMap);
        assertEquals(5, returnMap.size());
        assertNull(returnMap.get(ReponsesMailboxListComponentServiceImpl.ACTIVE_KEY));
        assertNotNull(returnMap.get("mailboxListMap"));
        assertTrue(returnMap.get("mailboxListMap") instanceof RepMailboxListDTO);
        RepMailboxListDTO plan = (RepMailboxListDTO) returnMap.get("mailboxListMap");
        assertEquals(1, plan.getChilds().size());
        assertEquals("elem1", plan.getChilds().get(0).getKey());
        assertFalse(plan.getChilds().get(0).getChilds().isEmpty());
        // La liste d'enfant est déjà présente donc on ne la recalcule pas on ouvre
        // juste le parent
        assertEquals(2, plan.getChilds().get(0).getChilds().size());
        assertEquals(true, plan.getChilds().get(0).getIsOpen());

        // Vérifie que la session a bien été mise à jour
        Mockito.verify(userSession, Mockito.times(1)).put("mailboxList", dto);
    }

    @Test
    public void testGetDataWithSessionDTOAndSelectedItemExistOpen() {
        // Si un DTO existe en session il doit être récupéré
        // Si l'item sélectionné est présent dans les éléments du DTO et que l'élément
        // est déjà ouvert
        // On ferme simplement l'item

        RepMailboxListDTO dto = new RepMailboxListDTO();
        dto.setModeTri(TypeRegroupement.PAR_MINISTERE);
        List<TreeElementDTO> elements = new ArrayList<>();
        TreeElementDTO element = new TreeElementDTO();
        element.setKey("elem1");
        element.setCompleteKey("__elem1");
        element.setIsOpen(true);
        elements.add(element);
        dto.setChilds(elements);

        Mockito.when(userSession.get("mailboxList")).thenReturn(dto);
        Mockito.when(userSession.containsKey("mailboxList")).thenReturn(true);
        Mockito
            .when(context.getFromContextData(ReponsesMailboxListComponentServiceImpl.SELECTED_KEY))
            .thenReturn("__elem1");

        Map<String, Object> returnMap = service.getData(context);
        assertNotNull(returnMap);
        assertEquals(5, returnMap.size());
        assertNull(returnMap.get(ReponsesMailboxListComponentServiceImpl.ACTIVE_KEY));
        assertNotNull(returnMap.get("mailboxListMap"));
        assertTrue(returnMap.get("mailboxListMap") instanceof RepMailboxListDTO);
        RepMailboxListDTO plan = (RepMailboxListDTO) returnMap.get("mailboxListMap");
        assertEquals(1, plan.getChilds().size());
        assertEquals("elem1", plan.getChilds().get(0).getKey());
        // L'élément est déjà ouvert donc on a pas besoin de récupérer sa liste d'enfant
        // et on le ferme simplement
        assertTrue(plan.getChilds().get(0).getChilds().isEmpty());
        assertEquals(false, plan.getChilds().get(0).getIsOpen());

        // Vérifie que la session a bien été mise à jour
        Mockito.verify(userSession, Mockito.times(1)).put("mailboxList", dto);
    }

    @Test
    public void testGetDataWithSessionDTOAndSelectedItemNotExist() {
        // Si un DTO existe en session il doit être récupéré
        // Si l'item sélectionné n'est pas présent dans les éléments du DTO
        // On renvoie le même DTO

        RepMailboxListDTO dto = new RepMailboxListDTO();
        dto.setModeTri(TypeRegroupement.PAR_MINISTERE);
        List<TreeElementDTO> elements = new ArrayList<>();
        TreeElementDTO element = new TreeElementDTO();
        element.setKey("elem1");
        element.setCompleteKey("__elem1");
        elements.add(element);
        dto.setChilds(elements);

        Mockito.when(userSession.get("mailboxList")).thenReturn(dto);
        Mockito.when(userSession.containsKey("mailboxList")).thenReturn(true);
        Mockito
            .when(context.getFromContextData(ReponsesMailboxListComponentServiceImpl.SELECTED_KEY))
            .thenReturn("elem2");

        Map<String, Object> returnMap = service.getData(context);
        assertNotNull(returnMap);
        assertEquals(5, returnMap.size());
        assertNull(returnMap.get(ReponsesMailboxListComponentServiceImpl.ACTIVE_KEY));
        assertNotNull(returnMap.get("mailboxListMap"));
        assertTrue(returnMap.get("mailboxListMap") instanceof RepMailboxListDTO);
        RepMailboxListDTO plan = (RepMailboxListDTO) returnMap.get("mailboxListMap");
        assertEquals(dto, plan);
        assertEquals(1, plan.getChilds().size());
        assertEquals("elem1", plan.getChilds().get(0).getKey());
        assertTrue(plan.getChilds().get(0).getChilds().isEmpty());

        // Vérifie que la session a bien été mise à jour
        Mockito.verify(userSession, Mockito.times(1)).put("mailboxList", dto);
    }

    @Test
    public void testSearchInTreeElement() {
        List<TreeElementDTO> emptyList = new ArrayList<>();
        List<TreeElementDTO> fullList = new ArrayList<>();

        TreeElementDTO elem1 = new TreeElementDTO();
        elem1.setKey("elem1Key");
        elem1.setCompleteKey("elem1Key");

        List<TreeElementDTO> elem1Child = new ArrayList<>();
        TreeElementDTO elem11 = new TreeElementDTO();
        elem11.setKey("elem11Key");
        elem11.setCompleteKey("elem1Key__elem11Key");
        elem1Child.add(elem11);
        TreeElementDTO elem12 = new TreeElementDTO();
        elem12.setKey("elem12Key");
        elem12.setCompleteKey("elem1Key__elem12Key");
        elem1Child.add(elem12);
        elem1.setChilds(elem1Child);

        fullList.add(elem1);

        TreeElementDTO elem2 = new TreeElementDTO();
        elem2.setKey("elem2Key");
        elem2.setCompleteKey("elem2Key");

        fullList.add(elem2);

        List<TreeElementDTO> elem3Child = new ArrayList<>();
        TreeElementDTO elem3 = new TreeElementDTO();
        elem3.setKey("elem3Key");
        elem3.setCompleteKey("elem3Key");
        TreeElementDTO elem31 = new TreeElementDTO();
        elem31.setKey("elem31Key");
        elem31.setCompleteKey("elem3Key__elem31Key");
        elem3Child.add(elem31);
        elem3.setChilds(elem3Child);

        fullList.add(elem3);

        // Si la liste est vide on retourne null
        TreeElementDTO element = service.searchInTreeElement(emptyList, "elem1Key");
        assertNull(element);

        // Si existe pas on le retourne

        // Test au niveau 1
        element = service.searchInTreeElement(fullList, "elem1Key");
        assertNotNull(element);
        assertEquals(elem1, element);

        // Test au niveau 2
        element = service.searchInTreeElement(fullList, "elem3Key__elem31Key");
        assertNotNull(element);
        assertEquals(elem31, element);

        // Si l'élément n'existe pas dans la liste on retourne null
        element = service.searchInTreeElement(fullList, "Unknown");
        assertNull(element);
    }

    @Test
    public void testConstructListElementFromMap() {
        RepMailboxListDTO dto = new RepMailboxListDTO();
        dto.setModeTri(TypeRegroupement.PAR_MINISTERE);
        dto.setMasquerCorbeilles(false);
        dto.setSelectionPoste(null);
        dto.setSelectionUser(null);

        // Si la map en entrée est null on renvoit une liste vide
        List<TreeElementDTO> returnList = service.constructListElementFromMap(null, false, "", dto, principal);
        assertNotNull(returnList);
        assertTrue(returnList.isEmpty());

        // Si la map en entrée est vide on renvoit une liste vide
        returnList = service.constructListElementFromMap(new TreeMap<>(), false, "", dto, principal);

        assertNotNull(returnList);
        assertTrue(returnList.isEmpty());

        Map<String, Integer> minMap = createMinistereMap();

        // Construction avec tri par ministère au niveau 1
        returnList = service.constructListElementFromMap(minMap, false, "", dto, principal);
        assertNotNull(returnList);
        assertEquals(3, returnList.size());
        assertEquals("min-2", returnList.get(1).getKey());
        assertEquals("__min-2", returnList.get(1).getCompleteKey());
        assertEquals("Ministère 2 - (7)", returnList.get(1).getLabel());
        assertNull(returnList.get(1).getLink());
        assertNotNull(returnList.get(1).getChilds());
        assertTrue(returnList.get(1).getChilds().isEmpty());
        assertFalse(returnList.get(1).getIsOpen());
        assertFalse(returnList.get(1).getIsLastLevel());
        assertTrue(returnList.get(1).getIsBold());
        assertEquals("onClickMailboxItem('__min-2')", returnList.get(1).getAction());
        // Vérification du label du ministère qui ne fait pas partie du gouvernement
        // actuel et qu'il n'est pas en gras parce qu'il n'appartient pas aux ministères de l'utilisateur
        assertEquals("Ministère 3 [Ancien gouvernement] - (156)", returnList.get(2).getLabel());
        assertFalse(returnList.get(2).getIsBold());

        Map<String, Integer> etapeMap = createEtapeMap();

        dto.setModeTri(TypeRegroupement.PAR_MINISTERE);
        dto.setMasquerCorbeilles(true);
        dto.setSelectionPoste(null);
        dto.setSelectionUser(null);
        // Construction avec tri par ministère au niveau 2 + pas de selectionPoste ni
        // selectionUser
        returnList = service.constructListElementFromMap(etapeMap, true, "min-2", dto, principal);
        assertNotNull(returnList);
        assertEquals(4, returnList.size());
        assertEquals("2", returnList.get(1).getKey());
        assertEquals("min-2__2", returnList.get(1).getCompleteKey());
        assertEquals("Pour attribution - (7)", returnList.get(1).getLabel());
        assertNull(returnList.get(1).getAction());
        assertNotNull(returnList.get(1).getChilds());
        assertTrue(returnList.get(1).getChilds().isEmpty());
        assertFalse(returnList.get(1).getIsOpen());
        assertTrue(returnList.get(1).getIsLastLevel());
        assertEquals("/travail/listeMin?minAttribId=min-2&routingTaskType=2", returnList.get(1).getLink());

        Map<String, Integer> posteMap = createPosteMap();

        dto.setModeTri(TypeRegroupement.PAR_POSTE);
        dto.setMasquerCorbeilles(false);
        dto.setSelectionPoste(null);
        dto.setSelectionUser(null);
        // Construction avec tri par poste au niveau 1
        returnList = service.constructListElementFromMap(posteMap, false, "", dto, principal);
        assertNotNull(returnList);
        assertEquals(3, returnList.size());
        assertEquals("poste-2", returnList.get(1).getKey());
        assertEquals("__poste-2", returnList.get(1).getCompleteKey());
        assertEquals("Poste 2 - (7)", returnList.get(1).getLabel());
        assertNull(returnList.get(1).getLink());
        assertNotNull(returnList.get(1).getChilds());
        assertTrue(returnList.get(1).getChilds().isEmpty());
        assertFalse(returnList.get(1).getIsOpen());
        assertFalse(returnList.get(1).getIsLastLevel());
        assertEquals("onClickMailboxItem('__poste-2')", returnList.get(1).getAction());

        dto.setModeTri(TypeRegroupement.PAR_POSTE);
        dto.setMasquerCorbeilles(false);
        dto.setSelectionPoste("poste");
        dto.setSelectionUser(null);
        // Construction avec tri par poste au niveau 2 + selectionPoste
        returnList = service.constructListElementFromMap(minMap, true, "poste-2", dto, principal);
        assertNotNull(returnList);
        assertEquals(3, returnList.size());
        assertEquals("min-2", returnList.get(1).getKey());
        assertEquals("poste-2__min-2", returnList.get(1).getCompleteKey());
        assertEquals("Ministère 2 - (7)", returnList.get(1).getLabel());
        assertNull(returnList.get(1).getAction());
        assertNotNull(returnList.get(1).getChilds());
        assertTrue(returnList.get(1).getChilds().isEmpty());
        assertFalse(returnList.get(1).getIsOpen());
        assertTrue(returnList.get(1).getIsLastLevel());
        assertEquals(
            "/travail/listePoste?posteId=poste-2&minAttribId=min-2&selectionPoste=poste",
            returnList.get(1).getLink()
        );

        Map<String, Integer> signaleMap = createSignaleMap();

        dto.setModeTri(TypeRegroupement.PAR_SIGNALE);
        dto.setMasquerCorbeilles(false);
        dto.setSelectionPoste(null);
        dto.setSelectionUser(null);
        // Construction avec tri par signalé au niveau 1
        returnList = service.constructListElementFromMap(signaleMap, false, "", dto, principal);
        assertNotNull(returnList);
        assertEquals(2, returnList.size());
        assertEquals("QUESTIONS_NON_SIGNALEES", returnList.get(1).getKey());
        assertEquals("__QUESTIONS_NON_SIGNALEES", returnList.get(1).getCompleteKey());
        assertEquals("Non signalées - (7)", returnList.get(1).getLabel());
        assertNull(returnList.get(1).getLink());
        assertNotNull(returnList.get(1).getChilds());
        assertTrue(returnList.get(1).getChilds().isEmpty());
        assertFalse(returnList.get(1).getIsOpen());
        assertFalse(returnList.get(1).getIsLastLevel());
        assertEquals("onClickMailboxItem('__QUESTIONS_NON_SIGNALEES')", returnList.get(1).getAction());

        dto.setModeTri(TypeRegroupement.PAR_SIGNALE);
        dto.setMasquerCorbeilles(false);
        dto.setSelectionPoste(null);
        dto.setSelectionUser("user");
        // Construction avec tri par signalé au niveau 2 + selectionUser
        returnList =
            service.constructListElementFromMap(
                minMap,
                true,
                EtatSignalement.QUESTIONS_NON_SIGNALEES.toString(),
                dto,
                principal
            );
        assertNotNull(returnList);
        assertEquals(3, returnList.size());
        assertEquals("min-2", returnList.get(1).getKey());
        assertEquals("QUESTIONS_NON_SIGNALEES__min-2", returnList.get(1).getCompleteKey());
        assertEquals("Ministère 2 - (7)", returnList.get(1).getLabel());
        assertNull(returnList.get(1).getAction());
        assertNotNull(returnList.get(1).getChilds());
        assertTrue(returnList.get(1).getChilds().isEmpty());
        assertFalse(returnList.get(1).getIsOpen());
        assertTrue(returnList.get(1).getIsLastLevel());
        assertEquals(
            "/travail/listeSignal?isSignale=false&minAttribId=min-2&selectionUser=user",
            returnList.get(1).getLink()
        );
    }

    private EntiteNode buildEntite(int id) {
        EntiteNode entite = new EntiteNodeImpl();

        entite.setId("min-" + id);
        entite.setEdition("Ministère " + id);

        return entite;
    }

    private PosteNode buildPoste(int id) {
        PosteNode poste = new PosteNodeImpl();

        poste.setId("poste-" + id);
        poste.setLabel("Poste " + id);

        return poste;
    }

    private Map<String, Integer> createMinistereMap() {
        Map<String, Integer> simpleMap = new LinkedHashMap<>();
        simpleMap.put("min-1", 0);
        simpleMap.put("min-2", 7);
        simpleMap.put("min-3", 156);
        return simpleMap;
    }

    private Map<String, Integer> createPosteMap() {
        Map<String, Integer> simpleMap = new LinkedHashMap<>();
        simpleMap.put("poste-1", 10);
        simpleMap.put("poste-2", 7);
        simpleMap.put("poste-3", 0);
        return simpleMap;
    }

    private Map<String, Integer> createSignaleMap() {
        Map<String, Integer> simpleMap = new LinkedHashMap<>();
        simpleMap.put(EtatSignalement.QUESTIONS_SIGNALEES.toString(), 10);
        simpleMap.put(EtatSignalement.QUESTIONS_NON_SIGNALEES.toString(), 7);
        return simpleMap;
    }

    private Map<String, Integer> createEtapeMap() {
        Map<String, Integer> simpleMap = new LinkedHashMap<>();
        simpleMap.put("1", 10);
        simpleMap.put("2", 7);
        simpleMap.put("3", 156);
        simpleMap.put("4", 0);
        simpleMap.put("5", 1);
        return simpleMap;
    }
}
