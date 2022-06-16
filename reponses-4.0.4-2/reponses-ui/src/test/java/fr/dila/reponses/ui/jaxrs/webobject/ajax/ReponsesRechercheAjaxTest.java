package fr.dila.reponses.ui.jaxrs.webobject.ajax;

import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.AN_ANALYSE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.AN_RUBRIQUE;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_RENVOI;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.SE_THEME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.core.recherche.IndexationItem;
import fr.dila.reponses.core.recherche.IndexationProvider;
import fr.dila.reponses.ui.services.ReponsesModeleFdrListUIService;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.th.bean.DossierListForm;
import fr.dila.reponses.ui.th.bean.recherche.BlocDatesForm;
import fr.dila.reponses.ui.th.bean.recherche.FeuilleRouteForm;
import fr.dila.reponses.ui.th.bean.recherche.MotsClesForm;
import fr.dila.reponses.ui.th.bean.recherche.RechercheGeneraleForm;
import fr.dila.reponses.ui.th.bean.recherche.TexteIntegralForm;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.services.actions.suggestion.feuilleroute.FeuilleRouteSuggestionProviderService;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.services.actions.suggestion.nomauteur.NomAuteurSuggestionProviderService;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(
    {
        UserSessionHelper.class,
        ReponsesUIServiceLocator.class,
        IndexationProvider.class,
        STUIServiceLocator.class,
        SSUIServiceLocator.class,
        ReponsesRechercheAjax.class
    }
)
@PowerMockIgnore("javax.management.*")
public class ReponsesRechercheAjaxTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private NomAuteurSuggestionProviderService nomAuteurSuggestionProviderService;

    @Mock
    private ReponsesModeleFdrListUIService modeleFDRListUIService;

    @Mock
    private FeuilleRouteSuggestionProviderService feuilleRouteSuggestionProviderService;

    @Mock
    private WebContext webContext;

    @Mock
    private HttpServletRequest httpServletRequest;

    public static final String SEARCH_FORMS_KEY = "searchForms";

    @Before
    public void setUp() {
        PowerMockito.mockStatic(UserSessionHelper.class);
        PowerMockito.mockStatic(STUIServiceLocator.class);
        PowerMockito.mockStatic(SSUIServiceLocator.class);
        PowerMockito.mockStatic(ReponsesUIServiceLocator.class);

        when(UserSessionHelper.getUserSessionParameter(any(), any(), any())).thenReturn(new HashMap<String, Object>());

        when(STUIServiceLocator.getNomAuteurSuggestionProviderService()).thenReturn(nomAuteurSuggestionProviderService);

        when(SSUIServiceLocator.getFeuilleRouteSuggestionProviderService())
            .thenReturn(feuilleRouteSuggestionProviderService);

        when(ReponsesUIServiceLocator.getReponsesModeleFdrListUIService()).thenReturn(modeleFDRListUIService);
    }

    @Test
    public void testGetResultats() {
        ReponsesRechercheAjax reponseRechercheAjax = spy(new ReponsesRechercheAjax());
        SpecificContext context = spy(new SpecificContext());
        RechercheGeneraleForm general = new RechercheGeneraleForm();
        FeuilleRouteForm feuilleRoute = new FeuilleRouteForm();
        BlocDatesForm dates = new BlocDatesForm();
        TexteIntegralForm texteIntegral = new TexteIntegralForm();
        MotsClesForm motsCles = new MotsClesForm();
        DossierListForm results = new DossierListForm();

        doReturn(null).when(reponseRechercheAjax).newObject(any(), any());
        doReturn(context).when(reponseRechercheAjax).getMyContext();

        doReturn(webContext).when(context).getWebcontext();
        doReturn(httpServletRequest).when(webContext).getRequest();

        reponseRechercheAjax.getResults(general, dates, feuilleRoute, texteIntegral, motsCles, results);

        verify(reponseRechercheAjax, times(1)).newObject("AppliRechercheResultats", context);

        Map<String, Object> map = context.getContextData();

        PowerMockito.verifyStatic(Mockito.times(1));
        UserSessionHelper.putUserSessionParameter(context, SEARCH_FORMS_KEY, map);

        assertNotNull(map);
        assertEquals("/recherche/rechercher", map.get(STTemplateConstants.DATA_URL));
        assertEquals("/ajax/recherche/resultats", map.get(STTemplateConstants.DATA_AJAX_URL));
        assertEquals(general, map.get("general"));
        assertEquals(dates, map.get("dates"));
        assertEquals(feuilleRoute, map.get("feuilleRoute"));
        assertEquals(texteIntegral, map.get("texteIntegral"));
        assertEquals(motsCles, map.get("motsCles"));
        assertEquals(results, map.get("results"));
    }

    @Test
    public void testSuggestionsAuteurs() throws JsonProcessingException {
        ReponsesRechercheAjax reponseRechercheAjax = new ReponsesRechercheAjax();
        ObjectMapper mapper = new ObjectMapper();
        String input = "Jean";
        String typeSelection = "auteur";
        List<String> result = new ArrayList<>(Arrays.asList("Jean valjean"));

        when(nomAuteurSuggestionProviderService.getSuggestions(Mockito.eq(input), Mockito.any(SpecificContext.class)))
            .thenReturn(result);

        String suggest = reponseRechercheAjax.getSuggestions(input, typeSelection, false, null);

        assertEquals(mapper.writeValueAsString(result), suggest);
    }

    @Test
    public void testSuggestionsFdr() throws JsonProcessingException {
        ReponsesRechercheAjax reponseRechercheAjax = new ReponsesRechercheAjax();
        ObjectMapper mapper = new ObjectMapper();
        String input = "Test";
        String typeSelection = "intituleFdr";
        List<String> result = new ArrayList<>(Arrays.asList("Fdr test", "test fdr"));

        when(
            feuilleRouteSuggestionProviderService.getSuggestions(Mockito.eq(input), Mockito.any(SpecificContext.class))
        )
            .thenReturn(result);

        String suggest = reponseRechercheAjax.getSuggestions(input, typeSelection, false, null);

        assertEquals(mapper.writeValueAsString(result), suggest);
    }

    @Test
    public void testSuggestionsIndexAn() throws Exception {
        ReponsesRechercheAjax reponseRechercheAjax = new ReponsesRechercheAjax();
        ObjectMapper mapper = new ObjectMapper();
        String input = "Test";
        String typeSelection = VocabularyConstants.INDEXATION_ZONE_AN;
        List<String> result = new ArrayList<>(Arrays.asList("Rubrique : tests", "Titre : test"));
        IndexationItem item1 = new IndexationItem(AN_RUBRIQUE.getValue(), "tests");
        IndexationItem item2 = new IndexationItem(AN_ANALYSE.getValue(), "test");
        IndexationProvider provider = Mockito.mock(IndexationProvider.class);
        PowerMockito.whenNew(IndexationProvider.class).withArguments(typeSelection).thenReturn(provider);
        when(provider.getSuggestions(any())).thenReturn(Arrays.asList(item1, item2));

        String suggest = reponseRechercheAjax.getSuggestions(input, typeSelection, false, null);

        assertEquals(mapper.writeValueAsString(result), suggest);
    }

    @Test
    public void testSuggestionsIndexSenat() throws Exception {
        ReponsesRechercheAjax reponseRechercheAjax = new ReponsesRechercheAjax();
        ObjectMapper mapper = new ObjectMapper();
        String input = "Test";
        String typeSelection = VocabularyConstants.INDEXATION_ZONE_SENAT;
        List<String> result = new ArrayList<>(Arrays.asList("Thème : theme", "Renvoi : test renvoi"));
        IndexationItem item1 = new IndexationItem(SE_THEME.getValue(), "theme");
        IndexationItem item2 = new IndexationItem(SE_RENVOI.getValue(), "test renvoi");
        IndexationProvider provider = Mockito.mock(IndexationProvider.class);
        PowerMockito.whenNew(IndexationProvider.class).withArguments(typeSelection).thenReturn(provider);
        when(provider.getSuggestions(any())).thenReturn(Arrays.asList(item1, item2));

        String suggest = reponseRechercheAjax.getSuggestions(input, typeSelection, false, null);

        assertEquals(mapper.writeValueAsString(result), suggest);
    }

    @Test
    public void testSuggestionsWrongType() throws JsonProcessingException {
        ReponsesRechercheAjax reponseRechercheAjax = new ReponsesRechercheAjax();
        ObjectMapper mapper = new ObjectMapper();
        String input = "Test";
        String typeSelection = "inconnu";
        List<String> result = new ArrayList<>();

        String suggest = reponseRechercheAjax.getSuggestions(input, typeSelection, false, null);

        assertEquals(mapper.writeValueAsString(result), suggest);
    }
}
