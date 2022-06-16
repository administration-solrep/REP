package fr.dila.reponses.ui.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.service.PlanClassementService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.bean.PlanClassementDTO;
import fr.dila.st.ui.bean.TreeElementDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.session.UserSession;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ ReponsesServiceLocator.class, SpecificContext.class, WebContext.class, UserSession.class })
@PowerMockIgnore("javax.management.*")
public class PlanClassementComponentServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    PlanClassementComponentServiceImpl service = new PlanClassementComponentServiceImpl();

    @Mock
    PlanClassementService planService;

    @Mock
    SpecificContext context;

    @Mock
    WebContext webContext;

    @Mock
    UserSession userSession;

    @Mock
    Map<String, Object> contextData;

    @Mock
    CoreSession session;

    @Before
    public void before() {
        planService = Mockito.mock(PlanClassementService.class);
        context = Mockito.mock(SpecificContext.class);
        webContext = Mockito.mock(WebContext.class);
        userSession = PowerMockito.mock(UserSession.class);
        PowerMockito.spy(UserSession.class);

        Mockito.when(context.getWebcontext()).thenReturn(webContext);
        Mockito.when(context.getContextData()).thenReturn(contextData);
        Mockito.when(webContext.getUserSession()).thenReturn(userSession);
        Mockito.when(context.getSession()).thenReturn(session);

        PowerMockito.mockStatic(ReponsesServiceLocator.class);

        Mockito.when(ReponsesServiceLocator.getPlanClassementService()).thenReturn(planService);

        Mockito.when(planService.getPlanClassementNiveau1(Mockito.any(), Mockito.any())).thenReturn(createSimpleMap());
        Mockito
            .when(planService.getPlanClassementNiveau2(Mockito.any(), Mockito.any(), Mockito.any()))
            .thenReturn(createComplexeMap());
    }

    @Test
    public void testGetDataWithoutAnything() {
        // Sans aucun paramètre en entrée on doit avoir un DTO initialisé avec le 1er
        // niveau

        Map<String, Object> returnMap = service.getData(context);
        assertNotNull(returnMap);
        assertEquals(3, returnMap.size());
        assertNull(returnMap.get(PlanClassementComponentServiceImpl.ACTIVE_KEY));
        assertNotNull(returnMap.get("planClassementMap"));
        assertTrue(returnMap.get("planClassementMap") instanceof PlanClassementDTO);
        PlanClassementDTO plan = (PlanClassementDTO) returnMap.get("planClassementMap");
        assertEquals(DossierConstants.ORIGINE_QUESTION_AN, plan.getAssemblee());
        assertNotNull(plan.getChilds());
        assertEquals(3, plan.getChilds().size());

        // Vérifie que la session a bien été mise à jour
        Mockito.verify(userSession, Mockito.times(1)).put(PlanClassementComponentServiceImpl.ACTIVE_KEY, null);
        Mockito.verify(userSession, Mockito.times(1)).put("planClassement", plan);
    }

    @Test
    public void testGetDataWithAssembleeKey() {
        // en passant l'assemblee via le contexte, le DTO en sortie doit être initialisé
        // pour l'assemblée indiquée

        Mockito
            .when(contextData.get(PlanClassementComponentServiceImpl.ASSEMBLEE_KEY))
            .thenReturn(DossierConstants.ORIGINE_QUESTION_AN);

        Map<String, Object> returnMap = service.getData(context);
        assertNotNull(returnMap);
        assertEquals(3, returnMap.size());
        assertNull(returnMap.get(PlanClassementComponentServiceImpl.ACTIVE_KEY));
        assertNotNull(returnMap.get("planClassementMap"));
        assertTrue(returnMap.get("planClassementMap") instanceof PlanClassementDTO);
        PlanClassementDTO plan = (PlanClassementDTO) returnMap.get("planClassementMap");
        assertEquals(DossierConstants.ORIGINE_QUESTION_AN, plan.getAssemblee());
        assertNotNull(plan.getChilds());
        assertEquals(3, plan.getChilds().size());

        // Vérifie que la session a bien été mise à jour
        Mockito.verify(userSession, Mockito.times(1)).put(PlanClassementComponentServiceImpl.ACTIVE_KEY, null);
        Mockito.verify(userSession, Mockito.times(1)).put("planClassement", plan);
    }

    @Test
    public void testGetDataWithActiveKey() {
        // en passant l'item actif dans le contexte, on doit le récupérer en sortie (Pas
        // d'impact sur le DTO)

        Mockito.when(contextData.get(PlanClassementComponentServiceImpl.ACTIVE_KEY)).thenReturn("elem1");

        Map<String, Object> returnMap = service.getData(context);
        assertNotNull(returnMap);
        assertEquals(3, returnMap.size());
        assertNotNull(returnMap.get(PlanClassementComponentServiceImpl.ACTIVE_KEY));
        assertEquals("elem1", returnMap.get(PlanClassementComponentServiceImpl.ACTIVE_KEY));

        // Vérifie que la session a bien été mise à jour
        Mockito.verify(userSession, Mockito.times(1)).put(PlanClassementComponentServiceImpl.ACTIVE_KEY, "elem1");
    }

    @Test
    public void testGetDataWithSessionDTO() {
        // Si un DTO existe en session il doit être récupéré

        PlanClassementDTO dto = new PlanClassementDTO();
        List<TreeElementDTO> elements = new ArrayList<>();
        TreeElementDTO element = new TreeElementDTO();
        element.setKey("elem1");
        elements.add(element);
        dto.setChilds(elements);

        Mockito.when(userSession.get("planClassement")).thenReturn(dto);
        Mockito.when(userSession.containsKey("planClassement")).thenReturn(true);

        Map<String, Object> returnMap = service.getData(context);
        assertNotNull(returnMap);
        assertEquals(3, returnMap.size());
        assertNull(returnMap.get(PlanClassementComponentServiceImpl.ACTIVE_KEY));
        assertNotNull(returnMap.get("planClassementMap"));
        assertTrue(returnMap.get("planClassementMap") instanceof PlanClassementDTO);
        PlanClassementDTO plan = (PlanClassementDTO) returnMap.get("planClassementMap");
        assertEquals(dto, plan);
        assertEquals(1, plan.getChilds().size());
        assertEquals("elem1", plan.getChilds().get(0).getKey());

        // Vérifie que la session a bien été mise à jour
        Mockito.verify(userSession, Mockito.times(1)).put("planClassement", dto);
    }

    @Test
    public void testGetDataWithSessionDTOAndNotSameAssemblee() {
        // Si un DTO existe en session il doit être récupéré
        // Si l'assemblée dans le contexte est différente du DTO alors le DTO est
        // modifié pour représenter l'assemblée désirée

        PlanClassementDTO dto = new PlanClassementDTO();
        List<TreeElementDTO> elements = new ArrayList<>();
        TreeElementDTO element = new TreeElementDTO();
        element.setKey("elem1");
        elements.add(element);
        dto.setChilds(elements);

        Mockito.when(userSession.get("planClassement")).thenReturn(dto);
        Mockito.when(userSession.containsKey("planClassement")).thenReturn(true);
        Mockito
            .when(contextData.get(PlanClassementComponentServiceImpl.ASSEMBLEE_KEY))
            .thenReturn(DossierConstants.ORIGINE_QUESTION_AN);

        Map<String, Object> returnMap = service.getData(context);
        assertNotNull(returnMap);
        assertEquals(3, returnMap.size());
        assertNotNull(returnMap.get("planClassementMap"));
        assertTrue(returnMap.get("planClassementMap") instanceof PlanClassementDTO);
        PlanClassementDTO plan = (PlanClassementDTO) returnMap.get("planClassementMap");
        assertEquals(DossierConstants.ORIGINE_QUESTION_AN, plan.getAssemblee());
        assertNotNull(plan.getChilds());
        assertEquals(1, plan.getChilds().size());

        // Vérifie que la session a bien été mise à jour
        Mockito.verify(userSession, Mockito.times(1)).put("planClassement", plan);
    }

    @Test
    public void testGetDataWithSessionDTOAndSelectedItemExistNotOpen() {
        // Si un DTO existe en session il doit être récupéré
        // Si l'item sélectionné est présent dans les éléments du DTO et que l'élément
        // n'est pas déjà ouvert et qu'il n'a pas d'enfant chargés
        // On charge les enfants de l'item sélectionné et on met à jour le DTO

        PlanClassementDTO dto = new PlanClassementDTO();
        List<TreeElementDTO> elements = new ArrayList<>();
        TreeElementDTO element = new TreeElementDTO();
        element.setKey("elem1");
        element.setCompleteKey("__elem1");
        elements.add(element);
        dto.setChilds(elements);

        Mockito.when(userSession.get("planClassement")).thenReturn(dto);
        Mockito.when(userSession.containsKey("planClassement")).thenReturn(true);
        Mockito.when(contextData.get(PlanClassementComponentServiceImpl.SELECTED_KEY)).thenReturn("__elem1");

        Map<String, Object> returnMap = service.getData(context);
        assertNotNull(returnMap);
        assertEquals(3, returnMap.size());
        assertNull(returnMap.get(PlanClassementComponentServiceImpl.ACTIVE_KEY));
        assertNotNull(returnMap.get("planClassementMap"));
        assertTrue(returnMap.get("planClassementMap") instanceof PlanClassementDTO);
        PlanClassementDTO plan = (PlanClassementDTO) returnMap.get("planClassementMap");
        assertEquals(1, plan.getChilds().size());

        assertEquals("elem1", plan.getChilds().get(0).getKey());
        assertFalse(plan.getChilds().get(0).getChilds().isEmpty());
        // La liste d'enfant n'existe pas donc on récupère la liste du service via le
        // mock
        assertEquals(5, plan.getChilds().get(0).getChilds().size());
        assertEquals(true, plan.getChilds().get(0).getIsOpen());

        // Vérifie que la session a bien été mise à jour
        Mockito.verify(userSession, Mockito.times(1)).put("planClassement", dto);
    }

    @Test
    public void testGetDataWithSessionDTOAndSelectedItemExistNotOpenAndListExist() {
        // Si un DTO existe en session il doit être récupéré
        // Si l'item sélectionné est présent dans les éléments du DTO et que l'élément
        // n'est pas déjà ouvert mais qu'il a des enfants chargés
        // On ouvre simplement l'item sans recharger ses enfants

        PlanClassementDTO dto = new PlanClassementDTO();
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

        Mockito.when(userSession.get("planClassement")).thenReturn(dto);
        Mockito.when(userSession.containsKey("planClassement")).thenReturn(true);
        Mockito.when(contextData.get(PlanClassementComponentServiceImpl.SELECTED_KEY)).thenReturn("__elem1");

        Map<String, Object> returnMap = service.getData(context);
        assertNotNull(returnMap);
        assertEquals(3, returnMap.size());
        assertNull(returnMap.get(PlanClassementComponentServiceImpl.ACTIVE_KEY));
        assertNotNull(returnMap.get("planClassementMap"));
        assertTrue(returnMap.get("planClassementMap") instanceof PlanClassementDTO);
        PlanClassementDTO plan = (PlanClassementDTO) returnMap.get("planClassementMap");
        assertEquals(1, plan.getChilds().size());
        assertEquals("elem1", plan.getChilds().get(0).getKey());
        assertFalse(plan.getChilds().get(0).getChilds().isEmpty());
        // La liste d'enfant est déjà présente donc on ne la recalcule pas on ouvre
        // juste le parent
        assertEquals(2, plan.getChilds().get(0).getChilds().size());
        assertEquals(true, plan.getChilds().get(0).getIsOpen());

        // Vérifie que la session a bien été mise à jour
        Mockito.verify(userSession, Mockito.times(1)).put("planClassement", dto);
    }

    @Test
    public void testGetDataWithSessionDTOAndSelectedItemExistOpen() {
        // Si un DTO existe en session il doit être récupéré
        // Si l'item sélectionné est présent dans les éléments du DTO et que l'élément
        // est déjà ouvert
        // On ferme simplement l'item

        PlanClassementDTO dto = new PlanClassementDTO();
        List<TreeElementDTO> elements = new ArrayList<>();
        TreeElementDTO element = new TreeElementDTO();
        element.setKey("elem1");
        element.setCompleteKey("__elem1");
        element.setIsOpen(true);
        elements.add(element);
        dto.setChilds(elements);

        Mockito.when(userSession.get("planClassement")).thenReturn(dto);
        Mockito.when(userSession.containsKey("planClassement")).thenReturn(true);
        Mockito.when(contextData.get(PlanClassementComponentServiceImpl.SELECTED_KEY)).thenReturn("__elem1");

        Map<String, Object> returnMap = service.getData(context);
        assertNotNull(returnMap);
        assertEquals(3, returnMap.size());
        assertNull(returnMap.get(PlanClassementComponentServiceImpl.ACTIVE_KEY));
        assertNotNull(returnMap.get("planClassementMap"));
        assertTrue(returnMap.get("planClassementMap") instanceof PlanClassementDTO);
        PlanClassementDTO plan = (PlanClassementDTO) returnMap.get("planClassementMap");
        assertEquals(1, plan.getChilds().size());
        assertEquals("elem1", plan.getChilds().get(0).getKey());
        // L'élément est déjà ouvert donc on a pas besoin de récupérer sa liste d'enfant
        // et on le ferme simplement
        assertTrue(plan.getChilds().get(0).getChilds().isEmpty());
        assertEquals(false, plan.getChilds().get(0).getIsOpen());

        // Vérifie que la session a bien été mise à jour
        Mockito.verify(userSession, Mockito.times(1)).put("planClassement", dto);
    }

    @Test
    public void testGetDataWithSessionDTOAndSelectedItemNotExist() {
        // Si un DTO existe en session il doit être récupéré
        // Si l'item sélectionné n'est pas présent dans les éléments du DTO
        // On renvoie le même DTO

        PlanClassementDTO dto = new PlanClassementDTO();
        List<TreeElementDTO> elements = new ArrayList<>();
        TreeElementDTO element = new TreeElementDTO();
        element.setKey("elem1");
        element.setCompleteKey("__elem1");
        elements.add(element);
        dto.setChilds(elements);

        Mockito.when(userSession.get("planClassement")).thenReturn(dto);
        Mockito.when(userSession.containsKey("planClassement")).thenReturn(true);
        Mockito.when(contextData.get(PlanClassementComponentServiceImpl.SELECTED_KEY)).thenReturn("elem2");

        Map<String, Object> returnMap = service.getData(context);
        assertNotNull(returnMap);
        assertEquals(3, returnMap.size());
        assertNull(returnMap.get(PlanClassementComponentServiceImpl.ACTIVE_KEY));
        assertNotNull(returnMap.get("planClassementMap"));
        assertTrue(returnMap.get("planClassementMap") instanceof PlanClassementDTO);
        PlanClassementDTO plan = (PlanClassementDTO) returnMap.get("planClassementMap");
        assertEquals(dto, plan);
        assertEquals(1, plan.getChilds().size());
        assertEquals("elem1", plan.getChilds().get(0).getKey());
        assertTrue(plan.getChilds().get(0).getChilds().isEmpty());

        // Vérifie que la session a bien été mise à jour
        Mockito.verify(userSession, Mockito.times(1)).put("planClassement", dto);
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
        List<TreeElementDTO> elem12Child = new ArrayList<>();
        TreeElementDTO elem121 = new TreeElementDTO();
        elem121.setKey("elem121Key");
        elem121.setCompleteKey("elem1Key__elem12Key__elem121Key");
        elem12Child.add(elem121);
        elem12.setChilds(elem12Child);
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

        // Test au niveau 3
        element = service.searchInTreeElement(fullList, "elem1Key__elem12Key__elem121Key");
        assertNotNull(element);
        assertEquals(elem121, element);

        // Test au niveau 2
        element = service.searchInTreeElement(fullList, "elem3Key__elem31Key");
        assertNotNull(element);
        assertEquals(elem31, element);

        // Si l'élément n'existe pas dans la liste on retourne null
        element = service.searchInTreeElement(fullList, "Unknown");
        assertNull(element);
    }

    @Test
    public void testConstructListElementFromMap() throws NoSuchFieldException, SecurityException, Exception {
        // Modification de la limite définie dans la class
        setFinalStatic(service.getClass().getDeclaredField("SIZE_LIMIT"), 5);

        // Si la map en entrée est null on renvoit une liste vide
        List<TreeElementDTO> returnList = service.constructListElementFromMap(null, true, "parentKey", "senat");
        assertNotNull(returnList);
        assertTrue(returnList.isEmpty());

        // Si la map en entrée est vide on renvoit une liste vide
        returnList = service.constructListElementFromMap(new TreeMap<>(), true, "parentKey", "senat");
        assertNotNull(returnList);
        assertTrue(returnList.isEmpty());

        Map<String, Integer> simpleMap = createSimpleMap();

        // Construction avec une map en dessous de la taille limite + les éléments sont
        // le dernier niveau
        returnList = service.constructListElementFromMap(simpleMap, true, "parentKey", "senat");
        assertNotNull(returnList);
        assertEquals(3, returnList.size());
        assertEquals("elem2", returnList.get(1).getKey());
        assertEquals("parentKey__elem2", returnList.get(1).getCompleteKey());
        assertEquals("elem2 (7)", returnList.get(1).getLabel());
        assertNull(returnList.get(1).getAction());
        assertNotNull(returnList.get(1).getChilds());
        assertTrue(returnList.get(1).getChilds().isEmpty());
        assertFalse(returnList.get(1).getIsOpen());
        assertTrue(returnList.get(1).getIsLastLevel());
        assertEquals("/classement/liste?origine=senat&cle=elem2&cleParent=parentKey", returnList.get(1).getLink());

        // Construction avec une map en dessous de la taille limite + les éléments ne
        // sont pas le dernier niveau
        returnList = service.constructListElementFromMap(simpleMap, false, "parentKey", "senat");
        assertNotNull(returnList);
        assertEquals(3, returnList.size());
        assertEquals("elem3", returnList.get(2).getKey());
        assertEquals("parentKey__elem3", returnList.get(2).getCompleteKey());
        assertEquals("elem3 (156)", returnList.get(2).getLabel());
        assertNotNull(returnList.get(2).getAction());
        assertEquals("onClickPlanItem(\'parentKey__elem3\')", returnList.get(2).getAction());
        assertNotNull(returnList.get(2).getChilds());
        assertTrue(returnList.get(2).getChilds().isEmpty());
        assertFalse(returnList.get(2).getIsOpen());
        assertFalse(returnList.get(2).getIsLastLevel());
        assertNull(returnList.get(2).getLink());

        Map<String, Integer> complexeMap = createComplexeMap();

        // Construction avec une map au dessus de la taille limite + les éléments sont
        // le dernier niveau
        returnList = service.constructListElementFromMap(complexeMap, true, "parentKey", "senat");

        assertNotNull(returnList);
        assertEquals(5, returnList.size());

        assertEquals("A", returnList.get(0).getKey());
        assertEquals("parentKey__A", returnList.get(0).getCompleteKey());
        assertEquals("A (12)", returnList.get(0).getLabel());
        assertEquals("onClickPlanItem(\'parentKey__A\')", returnList.get(0).getAction());
        assertNotNull(returnList.get(0).getChilds());
        assertEquals(1, returnList.get(0).getChilds().size());
        assertFalse(returnList.get(0).getIsLastLevel());

        assertEquals("E", returnList.get(3).getKey());
        assertEquals("parentKey__E", returnList.get(3).getCompleteKey());
        assertEquals("E (173)", returnList.get(3).getLabel());
        assertEquals("onClickPlanItem(\'parentKey__E\')", returnList.get(3).getAction());
        assertNotNull(returnList.get(3).getChilds());
        assertEquals(3, returnList.get(3).getChilds().size());
        assertFalse(returnList.get(3).getIsLastLevel());

        TreeElementDTO child = returnList.get(3).getChilds().get(0);
        assertEquals("elem1", child.getKey());
        assertEquals("parentKey__elem1", child.getCompleteKey());
        assertEquals("elem1 (10)", child.getLabel());
        assertNull(child.getAction());
        assertEquals("/classement/liste?origine=senat&cle=elem1&cleParent=parentKey", child.getLink());
        assertNotNull(child.getChilds());
        assertTrue(child.getChilds().isEmpty());
        assertTrue(child.getIsLastLevel());

        assertEquals("V", returnList.get(4).getKey());
        assertEquals("parentKey__V", returnList.get(4).getCompleteKey());
        assertEquals("V (117)", returnList.get(4).getLabel());
        assertEquals("onClickPlanItem(\'parentKey__V\')", returnList.get(4).getAction());
        assertNotNull(returnList.get(4).getChilds());
        assertEquals(2, returnList.get(4).getChilds().size());
        assertFalse(returnList.get(4).getIsLastLevel());

        // Construction avec une map au dessus de la taille limite + les éléments ne
        // sont pas le dernier niveau
        returnList = service.constructListElementFromMap(complexeMap, false, "parentKey", "senat");

        assertNotNull(returnList);
        assertEquals(5, returnList.size());

        assertEquals("E", returnList.get(3).getKey());
        assertEquals("parentKey__E", returnList.get(3).getCompleteKey());
        assertEquals("E (173)", returnList.get(3).getLabel());
        assertEquals("onClickPlanItem(\'parentKey__E\')", returnList.get(3).getAction());
        assertNotNull(returnList.get(3).getChilds());
        assertEquals(3, returnList.get(3).getChilds().size());
        assertFalse(returnList.get(3).getIsLastLevel());

        child = returnList.get(3).getChilds().get(0);
        assertEquals("elem1", child.getKey());
        assertEquals("parentKey__elem1", child.getCompleteKey());
        assertEquals("elem1 (10)", child.getLabel());
        assertNull(child.getLink());
        assertEquals("onClickPlanItem(\'parentKey__elem1\')", child.getAction());
        assertNotNull(child.getChilds());
        assertTrue(child.getChilds().isEmpty());
        assertFalse(child.getIsLastLevel());
    }

    private Map<String, Integer> createSimpleMap() {
        Map<String, Integer> simpleMap = new TreeMap<>();
        simpleMap.put("elem1", 10);
        simpleMap.put("elem2", 7);
        simpleMap.put("elem3", 156);
        return simpleMap;
    }

    private Map<String, Integer> createComplexeMap() {
        Map<String, Integer> complexeMap = new TreeMap<>();
        complexeMap.put("a_elem", 12);
        complexeMap.put("b_elem", 98);
        complexeMap.put("c_elem", 1);
        complexeMap.put("elem1", 10);
        complexeMap.put("elem2", 7);
        complexeMap.put("elem3", 156);
        complexeMap.put("valeur1", 38);
        complexeMap.put("valeur2", 79);
        return complexeMap;
    }

    static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }
}
