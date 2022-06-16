package fr.dila.reponses.ui.contentview;

import static com.google.common.collect.Lists.newArrayList;
import static fr.dila.reponses.api.constant.ReponsesConstant.LEGISLATURE_LABEL;
import static fr.dila.reponses.api.constant.ReponsesConstant.LEGISLATURE_SCHEMA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ProfilUtilisateurConstants;
import fr.dila.reponses.api.constant.ProfilUtilisateurConstants.UserColumnEnum;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.api.service.ReponseFeuilleRouteService;
import fr.dila.reponses.api.service.ReponsesCorbeilleService;
import fr.dila.reponses.api.service.ReponsesVocabularyService;
import fr.dila.reponses.core.recherche.RepDossierListingDTO;
import fr.dila.reponses.core.service.ReponseFeuilleRouteServiceImpl;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.helper.ReponsesProviderHelper;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.service.STLockService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.service.STUserService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.contentview.AbstractDTOPageProvider;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.mock.SerializableMode;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.platform.query.core.CoreQueryPageProviderDescriptor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(
    { ReponsesServiceLocator.class, STServiceLocator.class, QueryUtils.class, ReponsesProviderHelper.class }
)
@PowerMockIgnore("javax.management.*")
public class RechercheResultPageProviderTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Mock
    private CoreSession session;

    @Mock
    private ReponsesVocabularyService vocService;

    @Mock
    private AllotissementService lotService;

    @Mock
    private ReponsesCorbeilleService corbeilleService;

    @Mock
    private STParametreService paramService;

    @Mock
    private STLockService lockService;

    @Mock
    private STUserService userService;

    private RechercheResultPageProvider provider = new RechercheResultPageProvider();

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Before
    public void before() {
        PowerMockito.mockStatic(ReponsesServiceLocator.class);
        PowerMockito.mockStatic(STServiceLocator.class);
        PowerMockito.mockStatic(QueryUtils.class);

        session = mock(CoreSession.class, Mockito.withSettings().serializable(SerializableMode.ACROSS_CLASSLOADERS));
        when(STServiceLocator.getSTParametreService()).thenReturn(paramService);
        when(STServiceLocator.getSTLockService()).thenReturn(lockService);
        when(STServiceLocator.getSTUserService()).thenReturn(userService);
        when(ReponsesServiceLocator.getVocabularyService()).thenReturn(vocService);
        when(ReponsesServiceLocator.getAllotissementService()).thenReturn(lotService);
        when(ReponsesServiceLocator.getCorbeilleService()).thenReturn(corbeilleService);

        PowerMockito
            .stub(
                PowerMockito.method(
                    ReponsesProviderHelper.class,
                    "getIdsQuestionsWithErratum",
                    CoreSession.class,
                    Collection.class
                )
            )
            .toReturn(getIdsList());
    }

    private Set<String> getIdsList() {
        return Sets.newHashSet("1");
    }

    private DocumentModelList mockQuestions() {
        DocumentModelList lstDocuments = mock(DocumentModelList.class);
        Iterator<DocumentModel> docIterator = mock(Iterator.class);
        DocumentModel questDoc = mock(DocumentModel.class);
        DocumentModel questDoc2 = mock(DocumentModel.class);
        DocumentModel questDoc3 = mock(DocumentModel.class);

        Question quest = mock(Question.class);
        Question quest2 = mock(Question.class);
        Question quest3 = mock(Question.class);

        when(lstDocuments.iterator()).thenReturn(docIterator);
        when(docIterator.hasNext()).thenReturn(true, true, true, false);
        when(docIterator.next()).thenReturn(questDoc, questDoc2, questDoc3);
        when(questDoc.getId()).thenReturn("1");
        when(questDoc2.getId()).thenReturn("2");
        when(questDoc3.getId()).thenReturn("3");
        when(questDoc.getAdapter(Mockito.any())).thenReturn(quest);
        when(questDoc2.getAdapter(Mockito.any())).thenReturn(quest2);
        when(questDoc3.getAdapter(Mockito.any())).thenReturn(quest3);

        when(quest.getId()).thenReturn("1");
        when(quest.getDocument()).thenReturn(questDoc);
        when(quest.getLegislatureQuestion()).thenReturn(15L);
        when(quest.getSourceNumeroQuestion()).thenReturn("AN 1");
        when(quest.getDateSignalementQuestion()).thenReturn(Calendar.getInstance());
        when(quest.getDateRenouvellementQuestion()).thenReturn(Calendar.getInstance());
        when(quest.getEtatRenouvele()).thenReturn(true);
        when(quest.getEtatSignale()).thenReturn(true);
        DocumentRef doss1 = mock(DocumentRef.class);
        when(quest.getDossierRef()).thenReturn(doss1);
        when(doss1.reference()).thenReturn("Dossier 1");

        when(quest2.getId()).thenReturn("2");
        when(quest2.getDocument()).thenReturn(questDoc2);
        when(quest2.getLegislatureQuestion()).thenReturn(15L);
        when(quest2.getSourceNumeroQuestion()).thenReturn("SENAT 2");
        DocumentRef doss2 = mock(DocumentRef.class);
        when(quest2.getDossierRef()).thenReturn(doss2);
        when(doss2.reference()).thenReturn("Dossier 2");

        when(quest3.getId()).thenReturn("3");
        when(quest3.getDocument()).thenReturn(questDoc3);
        when(quest3.getLegislatureQuestion()).thenReturn(14L);
        when(quest3.getSourceNumeroQuestion()).thenReturn("AN 3");
        DocumentRef doss3 = mock(DocumentRef.class);
        when(quest3.getDossierRef()).thenReturn(doss3);
        when(doss3.reference()).thenReturn("Dossier 3");

        Dossier dossier = mock(Dossier.class);
        Dossier dossier2 = mock(Dossier.class);
        Dossier dossier3 = mock(Dossier.class);

        when(lstDocuments.stream()).thenReturn(Stream.of(questDoc, questDoc2, questDoc3));
        when(quest.getDossier(session)).thenReturn(dossier);
        when(quest2.getDossier(session)).thenReturn(dossier2);
        when(quest3.getDossier(session)).thenReturn(dossier3);
        when(dossier.getReponseId()).thenReturn("1");
        when(dossier2.getReponseId()).thenReturn("2");
        when(dossier3.getReponseId()).thenReturn("3");

        return lstDocuments;
    }

    private void mockVocData() {
        DocumentModel doc = mock(DocumentModel.class);
        DocumentModelList docs = new DocumentModelListImpl(newArrayList(doc));

        when(vocService.getAllEntry(VocabularyConstants.LEGISLATURE)).thenReturn(docs);

        when(doc.getId()).thenReturn("14");
        when(doc.getProperty(LEGISLATURE_SCHEMA, LEGISLATURE_LABEL)).thenReturn("XIV");
    }

    @Test
    public void testPopulateFromQuestionData() {
        DocumentModelList lstDocs = mockQuestions();

        mockVocData();
        when(
            QueryUtils.retrieveDocuments(
                session,
                DossierConstants.QUESTION_DOCUMENT_TYPE,
                newArrayList("1", "2", "3"),
                true
            )
        )
            .thenReturn(lstDocs);

        provider.setLstUserVisibleColumns(newArrayList(UserColumnEnum.LEGISLATURE.toString()));
        Map<String, RepDossierListingDTO> mapQuestions = new HashMap<>();
        provider.populateFromQuestionData(session, newArrayList("1", "2", "3"), 15, mapQuestions);

        assertNotNull(mapQuestions);
        assertEquals(3, mapQuestions.size());
        assertTrue(mapQuestions.containsKey("Dossier 1"));
        RepDossierListingDTO dossier1 = mapQuestions.get("Dossier 1");
        assertNotNull(dossier1.getDateSignalement());
        assertEquals("Err", dossier1.getErrata());
        assertEquals("15", dossier1.getLegislature());
        assertEquals("AN 1", dossier1.getQuestionLabelIhm());
        assertTrue(dossier1.isRenouvelle());
        assertTrue(dossier1.isSignale());

        assertTrue(mapQuestions.containsKey("Dossier 2"));
        RepDossierListingDTO dossier2 = mapQuestions.get("Dossier 2");
        assertNull(dossier2.getDateSignalement());
        assertNull(dossier2.getErrata());
        assertFalse(dossier2.isRenouvelle());
        assertFalse(dossier2.isSignale());
        assertEquals("15", dossier2.getLegislature());

        assertTrue(mapQuestions.containsKey("Dossier 3"));
        RepDossierListingDTO dossier3 = mapQuestions.get("Dossier 3");
        assertNull(dossier3.getDateSignalement());
        assertNull(dossier3.getErrata());
        assertFalse(dossier3.isRenouvelle());
        assertFalse(dossier3.isSignale());
        assertEquals("XIV", dossier3.getLegislature());
    }

    private static DocumentModelList mockDossiers() {
        DocumentModel dosDoc = mock(DocumentModel.class);
        DocumentModel dosDoc2 = mock(DocumentModel.class);
        DocumentModel dosDoc3 = mock(DocumentModel.class);
        DocumentModelList lstDocuments = mock(DocumentModelList.class);
        Iterator<DocumentModel> docIterator = mock(Iterator.class);

        Dossier dos = mock(Dossier.class);
        Dossier dos2 = mock(Dossier.class);
        Dossier dos3 = mock(Dossier.class);

        when(lstDocuments.iterator()).thenReturn(docIterator);
        when(docIterator.hasNext()).thenReturn(true, true, true, false);
        when(docIterator.next()).thenReturn(dosDoc, dosDoc2, dosDoc3);
        when(dosDoc.getId()).thenReturn("Dossier 1");
        when(dosDoc.getAdapter(Mockito.any())).thenReturn(dos);
        when(dos.getDocument()).thenReturn(dosDoc);
        when(dos.getQuestionId()).thenReturn("1");

        when(dosDoc2.getId()).thenReturn("Dossier 2");
        when(dosDoc2.getAdapter(Mockito.any())).thenReturn(dos2);
        when(dos2.getDocument()).thenReturn(dosDoc2);
        when(dos2.getQuestionId()).thenReturn("2");

        when(dosDoc3.getId()).thenReturn("Dossier 3");
        when(dosDoc3.getAdapter(Mockito.any())).thenReturn(dos3);
        when(dos3.getDocument()).thenReturn(dosDoc2);
        when(dos3.getQuestionId()).thenReturn("3");
        when(dos3.getLastDocumentRoute()).thenReturn("fdr");
        when(dos3.hasFeuilleRoute()).thenReturn(true);

        return lstDocuments;
    }

    private List<DocumentModel> mockFdrData() {
        ReponseFeuilleRouteService fdrService = Mockito.spy(new ReponseFeuilleRouteServiceImpl());
        Mockito.when(ReponsesServiceLocator.getFeuilleRouteService()).thenReturn(fdrService);
        DocumentModel doc = Mockito.mock(DocumentModel.class);
        SSRouteStep step = Mockito.mock(SSRouteStep.class);
        List<DocumentModel> lstDocuments = mock(List.class);

        Iterator<DocumentModel> docIterator = mock(Iterator.class);
        Mockito.when(lstDocuments.iterator()).thenReturn(docIterator);
        Mockito.when(docIterator.hasNext()).thenReturn(true, false);
        Mockito.when(docIterator.next()).thenReturn(doc);
        Mockito.when(doc.getAdapter(SSRouteStep.class)).thenReturn(step);
        Mockito.when(step.getDirectionLabel()).thenReturn("Direction 1");

        Mockito.doReturn(lstDocuments).when(fdrService).getRunningSteps(session, "fdr");

        return lstDocuments;
    }

    @Test
    public void testPopulateFromDossierData() {
        mockFdrData();
        DocumentModelList lstDocuments = mockDossiers();
        Map<String, RepDossierListingDTO> mapQuestions = new HashMap<>();

        mapQuestions.put("Dossier 1", new RepDossierListingDTO());
        mapQuestions.put("Dossier 2", new RepDossierListingDTO());
        mapQuestions.put("Dossier 3", new RepDossierListingDTO());

        when(
            QueryUtils.retrieveDocuments(
                session,
                DossierConstants.DOSSIER_DOCUMENT_TYPE,
                Sets.newHashSet("Dossier 1", "Dossier 2", "Dossier 3")
            )
        )
            .thenReturn(lstDocuments);

        when(lotService.extractDossiersDirecteurs(lstDocuments, session)).thenReturn(Sets.newHashSet("Dossier 1"));
        when(lockService.extractLockedInfo(session, Sets.newHashSet("Dossier 1", "Dossier 2", "Dossier 3")))
            .thenReturn(Maps.newHashMap(ImmutableMap.of("Dossier 1", "Lockowner")));
        when(userService.getUserFullName("Lockowner")).thenReturn("John Locke");

        provider.setLstUserVisibleColumns(
            newArrayList(ProfilUtilisateurConstants.UserColumnEnum.DIR_ETAPE_COURANTE.name())
        );
        provider.populateFromDossierData(session, mapQuestions);
        RepDossierListingDTO dossier1 = mapQuestions.get("Dossier 1");
        assertTrue(dossier1.isLocked());
        assertEquals(dossier1.getLockOwner(), "John Locke");
        assertEquals(" * ", dossier1.getDirecteur());
        assertNull(dossier1.getDirectionRunningStep());

        RepDossierListingDTO dossier2 = mapQuestions.get("Dossier 2");
        assertFalse(dossier2.isLocked());
        assertNull(dossier2.getDirecteur());
        assertNull(dossier2.getDirectionRunningStep());

        RepDossierListingDTO dossier3 = mapQuestions.get("Dossier 3");
        assertFalse(dossier3.isLocked());
        assertNull(dossier3.getDirecteur());
        assertEquals("Direction 1", dossier3.getDirectionRunningStep());
    }

    @Test
    public void testGetCurrentPage() {
        DocumentModelList lstQuestionsDocs = mockQuestions();
        DocumentModelList lstDossiersDocs = mockDossiers();
        List<DocumentModel> lstDossiersLinks = mockFdrData();
        mockVocData();
        when(paramService.getParametreValue(session, STParametreConstant.QUESTION_DUREE_TRAITEMENT)).thenReturn("15");

        when(
            QueryUtils.retrieveDocuments(
                session,
                DossierConstants.QUESTION_DOCUMENT_TYPE,
                newArrayList("1", "2", "3"),
                true
            )
        )
            .thenReturn(lstQuestionsDocs);
        when(
            QueryUtils.retrieveDocuments(
                session,
                DossierConstants.DOSSIER_DOCUMENT_TYPE,
                Sets.newHashSet("Dossier 1", "Dossier 2", "Dossier 3")
            )
        )
            .thenReturn(lstDossiersDocs);
        when(
            corbeilleService.findUpdatableDossierLinkForDossiers(
                session,
                newArrayList("Dossier 1", "Dossier 2", "Dossier 3")
            )
        )
            .thenReturn(lstDossiersLinks);

        Map<String, Serializable> props = new TreeMap<>();
        props.put(AbstractDTOPageProvider.CORE_SESSION_PROPERTY, (Serializable) session);
        props.put(AbstractDTOPageProvider.CHECK_QUERY_CACHE_PROPERTY, Boolean.FALSE);
        provider.setProperties(props);
        CoreQueryPageProviderDescriptor definition = new CoreQueryPageProviderDescriptor();
        definition.setPattern("Select distinct ecm:uuid as id From Question");
        DocumentModel searchDoc = mock(DocumentModel.class);
        provider.setDefinition(definition);
        provider.setSearchDocumentModel(searchDoc);
        provider.setPageSize(5L);
        provider.setCurrentPageOffset(0L);

        PowerMockito
            .when(QueryUtils.doCountQuery(session, "Select distinct ecm:uuid as id From Question"))
            .thenReturn(3L);
        PowerMockito
            .when(QueryUtils.doQueryForIds(session, "Select distinct ecm:uuid as id From Question", 5L, 0))
            .thenReturn(newArrayList("1", "2", "3"));

        List<Map<String, Serializable>> curPage = provider.getCurrentPage();
        assertThat(curPage)
            .isNotNull()
            .hasSize(3)
            .allSatisfy(dto -> assertThat(dto).isExactlyInstanceOf(RepDossierListingDTO.class));
        assertEquals("AN 1", ((RepDossierListingDTO) curPage.get(0)).getSourceNumeroQuestion());
    }
}
