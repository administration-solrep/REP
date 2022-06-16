package fr.dila.reponses.ui.services.impl;

import static com.google.common.collect.Lists.newArrayList;
import static fr.dila.reponses.api.constant.ReponsesConstant.RECHERCHE_DOCUMENT_TYPE;
import static fr.dila.reponses.api.constant.RequeteConstants.CARACTERISTIQUE_QUESTION;
import static fr.dila.reponses.api.constant.RequeteConstants.REQUETE_COMPLEXE_SCHEMA;
import static fr.sword.naiad.nuxeo.ufnxql.core.query.FlexibleQueryMaker.COL_COUNT;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import fr.dila.reponses.api.enumeration.IndexModeEnum;
import fr.dila.reponses.api.recherche.ReponsesIndexableDocument;
import fr.dila.reponses.api.recherche.Requete;
import fr.dila.reponses.api.service.ProfilUtilisateurService;
import fr.dila.reponses.api.service.RechercheService;
import fr.dila.reponses.api.service.ReponsesVocabularyService;
import fr.dila.reponses.ui.bean.InitValueRechercheDTO;
import fr.dila.reponses.ui.bean.RechercheDTO;
import fr.dila.reponses.ui.bean.RepDossierList;
import fr.dila.reponses.ui.contentview.RechercheResultPageProvider;
import fr.dila.reponses.ui.services.RechercheUIService;
import fr.dila.reponses.ui.services.ReponsesSelectValueUIService;
import fr.dila.reponses.ui.th.bean.DossierListForm;
import fr.dila.reponses.ui.th.bean.recherche.BlocDatesForm;
import fr.dila.reponses.ui.th.bean.recherche.FeuilleRouteForm;
import fr.dila.reponses.ui.th.bean.recherche.MotsClesForm;
import fr.dila.reponses.ui.th.bean.recherche.RechercheGeneraleForm;
import fr.dila.reponses.ui.th.bean.recherche.TexteIntegralForm;
import fr.dila.st.core.feature.SolonMockitoFeature;
import fr.dila.st.ui.bean.SelectValueDTO;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.local.LocalSession;
import org.nuxeo.ecm.core.storage.sql.jdbc.ResultSetQueryResult;
import org.nuxeo.ecm.platform.query.api.PageProvider;
import org.nuxeo.ecm.platform.query.api.PageProviderDefinition;
import org.nuxeo.ecm.platform.query.api.PageProviderService;
import org.nuxeo.ecm.platform.query.nxql.CoreQueryDocumentPageProvider;
import org.nuxeo.runtime.mockito.RuntimeService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(SolonMockitoFeature.class)
public class RechercheUIServiceImplTest {
    private static final String SEARCH_QUERY =
        "ufnxql:SELECT DISTINCT q.ecm:uuid AS id " +
        "FROM Question AS q,Dossier AS d, FeuilleRoute AS f,RouteStep AS e2 " +
        "WHERE (((e2.rtsk:automaticValidation = 0 AND e2.rtsk:obligatoireMinistere = 0)) AND d.dos:idDocumentQuestion = q.ecm:uuid AND d.dos:lastDocumentRoute = f.ecm:uuid AND ufnxql_ministere:(d.dos:ministereAttributaireCourant) AND e2.rtsk:documentRouteId = f.ecm:uuid)";

    private RechercheUIService service;

    @Mock
    private LocalSession session;

    @Mock
    private DocumentModel requeteDoc;

    @Mock
    private ReponsesIndexableDocument indexableDoc;

    @Mock
    private DossierListForm listForm;

    @Mock
    private PageProviderDefinition descriptor;

    @Mock
    private ResultSetQueryResult resultSetQuery;

    @Mock
    @RuntimeService
    private ReponsesSelectValueUIService selectValueUIService;

    @Mock
    @RuntimeService
    private ReponsesVocabularyService reponsesVocabularyService;

    @Mock
    @RuntimeService
    private RechercheService rechercheService;

    @Mock
    @RuntimeService
    private PageProviderService providerService;

    @Mock
    @RuntimeService
    private ProfilUtilisateurService profilService;

    @Before
    public void setUp() {
        service = new RechercheUIServiceImpl();
    }

    @Test
    public void initRechercheDTOWithNullDTO() {
        List<SelectValueDTO> legislatures = singletonList(new SelectValueDTO("legislatureId", "legislatureLabel"));
        when(selectValueUIService.getLegislatures()).thenReturn(legislatures);

        List<SelectValueDTO> questionTypes = singletonList(new SelectValueDTO("questionTypeId", "questionTypeLabel"));
        when(selectValueUIService.getQuestionTypes()).thenReturn(questionTypes);

        List<SelectValueDTO> groupesPolitiques = singletonList(
            new SelectValueDTO("groupePolitiqueId", "groupePolitiqueLabel")
        );
        when(selectValueUIService.getGroupesPolitiques()).thenReturn(groupesPolitiques);

        List<SelectValueDTO> ministeresAttributaires = singletonList(
            new SelectValueDTO("ministeresAttributaireId", "ministeresAttributaireLabel")
        );
        when(selectValueUIService.getCurrentMinisteres()).thenReturn(ministeresAttributaires);

        List<SelectValueDTO> ministeresInterroges = singletonList(
            new SelectValueDTO("ministereInterrogeId", "ministereInterrogeLabel")
        );
        when(selectValueUIService.getAllMinisteres()).thenReturn(ministeresInterroges);

        List<SelectValueDTO> typesEtape = singletonList(new SelectValueDTO("typeEtapeId", "typeEtapeLabel"));
        when(selectValueUIService.getRoutingTaskTypes()).thenReturn(typesEtape);

        List<SelectValueDTO> statutsEtape = singletonList(new SelectValueDTO("statutEtapeId", "statutEtapeLabel"));
        when(selectValueUIService.getStatutsEtapeRecherche()).thenReturn(statutsEtape);

        RechercheDTO result = service.initRechercheDTO(null);

        assertThat(result.getRechercheGeneraleForm()).isNotNull();
        assertThat(result.getBlocDatesForm()).isNotNull();
        assertThat(result.getMotsClesForm()).isNotNull();
        assertThat(result.getFeuilleRouteForm()).isNotNull();
        assertThat(result.getTexteIntegralForm()).isNotNull();

        InitValueRechercheDTO initValueDto = result.getInitValueRechercheDto();
        assertThat(initValueDto.getLegislatures()).isEqualTo(legislatures);
        assertThat(initValueDto.getTypesQuestion()).isEqualTo(questionTypes);
        assertThat(initValueDto.getGroupesPolitiques()).isEqualTo(groupesPolitiques);
        assertThat(initValueDto.getMinisteresAttributaires()).isEqualTo(ministeresAttributaires);
        assertThat(initValueDto.getMinisteresInterroges()).isEqualTo(ministeresInterroges);
        assertThat(initValueDto.getTypesEtape()).isEqualTo(typesEtape);
        assertThat(initValueDto.getListStatus()).isEqualTo(statutsEtape);
    }

    @Test
    public void initRechercheDTO() {
        RechercheDTO dto = createRechercheDTO();

        RechercheDTO result = service.initRechercheDTO(dto);

        assertThat(result).isEqualTo(dto);
        assertThat(result.getInitValueRechercheDto()).isNotNull();
    }

    @Test
    public void getDossiersForRechercheAvanceeWithNoResults() {
        RechercheDTO dto = createRechercheDTO();
        long nbResults = 0L;

        mockRechercheAvancee();

        when(resultSetQuery.next()).thenReturn(ImmutableMap.of(COL_COUNT, nbResults));

        RepDossierList results = service.getDossiersForRechercheAvancee(session, dto, listForm);

        assertThat(results.getTitre()).isEqualTo("Résultat(s) de recherche");
        assertThat(results.getSousTitre()).isEqualTo(nbResults + " résultat(s) trouvé(s)");
        assertThat(results.getNbTotal()).isEqualTo(nbResults);
        assertThat(results.getListe()).isEmpty();
        assertThat(results.getListeColonnes()).isNotEmpty();
    }

    @Test
    public void getDossiersForRechercheAvanceeWithResults() {
        RechercheDTO dto = createRechercheDTO();
        long nbResults = 15L;

        mockRechercheAvancee();

        when(resultSetQuery.next()).thenReturn(ImmutableMap.of(COL_COUNT, nbResults));

        RepDossierList results = service.getDossiersForRechercheAvancee(session, dto, listForm);

        assertThat(results.getTitre()).isEqualTo("Résultat(s) de recherche");
        assertThat(results.getSousTitre()).isEqualTo(nbResults + " résultat(s) trouvé(s)");
        assertThat(results.getNbTotal()).isEqualTo(nbResults);
        assertThat(results.getListeColonnes()).isNotEmpty();
    }

    private static RechercheDTO createRechercheDTO() {
        RechercheDTO dto = new RechercheDTO();
        dto.setRechercheGeneraleForm(new RechercheGeneraleForm());
        dto.setBlocDatesForm(new BlocDatesForm());
        MotsClesForm motsClesForm = new MotsClesForm();
        motsClesForm.setRechercheSur(IndexModeEnum.INDEX_ORIG.name());
        dto.setMotsClesForm(motsClesForm);
        dto.setFeuilleRouteForm(new FeuilleRouteForm());
        dto.setTexteIntegralForm(new TexteIntegralForm());

        return dto;
    }

    private void mockRechercheAvancee() {
        when(session.createDocumentModel(RECHERCHE_DOCUMENT_TYPE)).thenReturn(requeteDoc);
        when(requeteDoc.getType()).thenReturn(RECHERCHE_DOCUMENT_TYPE);
        when(requeteDoc.getAdapter(ReponsesIndexableDocument.class)).thenReturn(indexableDoc);

        String vocabulaire1 = "vocabulaire 1";
        String vocabulaire2 = "vocabulaire 2";
        when(reponsesVocabularyService.getVocabularyList()).thenReturn(newArrayList(vocabulaire1, vocabulaire2));

        String entry1 = "entry1";
        String entry2 = "entry2";
        when(indexableDoc.getVocEntries(vocabulaire1)).thenReturn(singletonList(entry1));
        when(indexableDoc.getVocEntries(vocabulaire2)).thenReturn(singletonList(entry2));

        when(requeteDoc.getProperty(REQUETE_COMPLEXE_SCHEMA, CARACTERISTIQUE_QUESTION)).thenReturn(emptyList());

        when(rechercheService.getFullQuery(eq(session), any(Requete.class))).thenReturn(SEARCH_QUERY);

        when(providerService.getPageProviderDefinition("requeteCompositePageProvider")).thenReturn(descriptor);
        PageProvider pp = new RechercheResultPageProvider();
        pp.setDefinition(descriptor);
        pp.setProperties(new HashMap<>());
        when(
            providerService.getPageProvider(
                Mockito.anyString(),
                Mockito.eq(descriptor),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.anyVararg()
            )
        )
            .thenReturn(pp);
        when(profilService.getUserColumn(session)).thenReturn(newArrayList("colonne1", "colonne2"));

        when(descriptor.getPattern()).thenReturn(SEARCH_QUERY);

        when(listForm.getPageProvider(Mockito.eq(session), Mockito.anyString(), Mockito.anyString(), Mockito.anyList()))
            .thenReturn(pp);
        Map<String, Serializable> map = new HashMap<>();
        map.put(CoreQueryDocumentPageProvider.CORE_SESSION_PROPERTY, session);
        pp.setProperties(map);

        when(session.queryAndFetch(any(), any(), any())).thenReturn(resultSetQuery);
        when(resultSetQuery.iterator()).thenReturn(resultSetQuery);
        when(resultSetQuery.hasNext()).thenReturn(true, false);
    }
}
