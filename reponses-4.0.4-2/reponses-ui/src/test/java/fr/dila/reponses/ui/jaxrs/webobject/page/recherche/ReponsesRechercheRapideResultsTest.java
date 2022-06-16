package fr.dila.reponses.ui.jaxrs.webobject.page.recherche;

import static com.tngtech.jgiven.impl.util.AssertionUtil.assertNotNull;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import fr.dila.reponses.api.service.ReponsesCorbeilleService;
import fr.dila.reponses.core.recherche.RepDossierListingDTO;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.bean.RepDossierList;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.impl.RechercheUIServiceImpl;
import fr.dila.reponses.ui.th.bean.DossierListForm;
import fr.dila.ss.ui.enums.SSUserSessionKey;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.dila.st.ui.utils.URLUtils;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.session.UserSession;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(
    {
        ServiceUtil.class,
        UserSessionHelper.class,
        ReponsesUIServiceLocator.class,
        ReponsesServiceLocator.class,
        URLUtils.class
    }
)
@PowerMockIgnore("javax.management.*")
public class ReponsesRechercheRapideResultsTest {
    private static final int REDIRECT_STATUS = 303;
    private static final String BASE_URL = "http://test.fr/";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private RechercheUIServiceImpl rechercheUIService;

    @Mock
    private ReponsesCorbeilleService corbeilleService;

    @Mock
    private SpecificContext context;

    @Mock
    private WebContext webContext;

    @Mock
    private UserSession userSession;

    @Mock
    private CoreSession coreSession;

    @Mock
    private HttpServletRequest request;

    private DocumentModel newDocumentModel(String title, String id) {
        DocumentModel doc = Mockito.mock(DocumentModel.class);
        when(doc.getTitle()).thenReturn(title);
        when(doc.getId()).thenReturn(id);

        return doc;
    }

    private ReponsesRechercheRapideResults page;
    private ThTemplate thTemplate = new ThTemplate();

    @Before
    public void setup() {
        page = new ReponsesRechercheRapideResults();
        PowerMockito.mockStatic(ServiceUtil.class);
        PowerMockito.mockStatic(ReponsesUIServiceLocator.class);
        PowerMockito.mockStatic(ReponsesServiceLocator.class);
        PowerMockito.mockStatic(UserSessionHelper.class);

        Whitebox.setInternalState(page, "context", context);
        Whitebox.setInternalState(page, "template", thTemplate);

        when(context.getSession()).thenReturn(coreSession);
        when(context.getWebcontext()).thenReturn(webContext);
        when(webContext.getUserSession()).thenReturn(userSession);
        when(webContext.getRequest()).thenReturn(request);

        when(ReponsesUIServiceLocator.getRechercheUIService()).thenReturn(rechercheUIService);
        when(ReponsesServiceLocator.getCorbeilleService()).thenReturn(corbeilleService);

        mockStatic(URLUtils.class);
        PowerMockito.doAnswer(invocation -> BASE_URL + invocation.getArgumentAt(0, String.class)).when(URLUtils.class);
        URLUtils.generateRedirectPath(anyString(), any(HttpServletRequest.class));
    }

    @Test
    public void testRechercheRapideEmptyNor() {
        when(UserSessionHelper.getUserSessionParameter(context, SSUserSessionKey.NOR)).thenReturn("");

        ThTemplate template = (ThTemplate) page.doRecherche();

        Map<String, Object> map = template.getData();
        assertNotNull(map);

        assertNotNull(map.get("myForm"));

        DossierListForm resultForm = (DossierListForm) map.get(STTemplateConstants.RESULT_FORM);
        assertNotNull(resultForm);

        assertNotNull(map.get(STTemplateConstants.RESULT_LIST));
        assertNotNull(map.get(STTemplateConstants.LST_COLONNES));
        assertEquals("/recherche/rapide", map.get(STTemplateConstants.DATA_URL));
        assertEquals("/ajax/recherche/rapide", map.get(STTemplateConstants.DATA_AJAX_URL));
    }

    @Test
    public void testRechercheRapideOneResultDossiersLinksEmpty() {
        String dossierId = testCommonRechercheRapideOneResult();

        List<DocumentModel> dossiersLinks = new ArrayList<>();
        when(corbeilleService.findUpdatableDossierLinkForDossiers(coreSession, Collections.singletonList(dossierId)))
            .thenReturn(dossiersLinks);

        Response response = (Response) page.doRecherche();

        assertThat(response.getStatus()).isEqualTo(REDIRECT_STATUS);
        assertThat(getResponseUrl(response)).isEqualTo(BASE_URL + "dossier/" + dossierId + "/parapheur");
        PowerMockito.verifyStatic(Mockito.times(1));
    }

    @Test
    public void testRechercheRapideOneResultDossiersLinksNotEmpty() {
        String dossierId = testCommonRechercheRapideOneResult();

        String dossierLinkId = "0000";
        List<DocumentModel> dossiersLinks = asList(newDocumentModel("t1", dossierLinkId));
        when(corbeilleService.findUpdatableDossierLinkForDossiers(coreSession, Collections.singletonList(dossierId)))
            .thenReturn(dossiersLinks);

        Response response = (Response) page.doRecherche();

        assertThat(response.getStatus()).isEqualTo(REDIRECT_STATUS);
        assertThat(getResponseUrl(response))
            .isEqualTo(BASE_URL + "dossier/" + dossierId + "/parapheur?dossierLinkId=" + dossierLinkId);
        PowerMockito.verifyStatic(Mockito.times(1));
        UserSessionHelper.getUserSessionParameter(context, SSUserSessionKey.NOR);
        PowerMockito.verifyStatic(Mockito.times(1));
        UserSessionHelper.putUserSessionParameter(context, SpecificContext.LAST_TEMPLATE, ThTemplate.class);
    }

    @Test
    public void testRechercheRapideManyResults() {
        when(UserSessionHelper.getUserSessionParameter(context, SSUserSessionKey.NOR)).thenReturn("nor-test");

        RepDossierList dossierList = new RepDossierList();
        RepDossierListingDTO dto1 = new RepDossierListingDTO();
        RepDossierListingDTO dto2 = new RepDossierListingDTO();
        List<RepDossierListingDTO> list = new ArrayList<>();
        list.add(dto1);
        list.add(dto2);
        dossierList.setListe(list);
        when(rechercheUIService.getDossiersByOrigineNumero(Mockito.anyString(), any(), any())).thenReturn(dossierList);
        when(ReponsesUIServiceLocator.getRechercheUIService()).thenReturn(rechercheUIService);

        ThTemplate template = (ThTemplate) page.doRecherche();

        Map<String, Object> map = template.getData();
        assertNotNull(map);

        assertNotNull(map.get("myForm"));

        DossierListForm resultForm = (DossierListForm) map.get(STTemplateConstants.RESULT_FORM);
        assertNotNull(resultForm);

        assertNotNull(map.get(STTemplateConstants.RESULT_LIST));
        assertNotNull(map.get(STTemplateConstants.LST_COLONNES));
        assertEquals("/recherche/rapide", map.get(STTemplateConstants.DATA_URL));
        assertEquals("/ajax/recherche/rapide", map.get(STTemplateConstants.DATA_AJAX_URL));
    }

    private String testCommonRechercheRapideOneResult() {
        when(UserSessionHelper.getUserSessionParameter(context, SSUserSessionKey.NOR)).thenReturn("nor-test");

        RepDossierList dossierList = new RepDossierList();
        String dossierId = "dossierId";
        RepDossierListingDTO dto = new RepDossierListingDTO();
        dto.setDossierId(dossierId);
        List<RepDossierListingDTO> list = new ArrayList<>();
        list.add(dto);
        dossierList.setListe(list);
        dossierList.setNbTotal(1);
        when(rechercheUIService.getDossiersByOrigineNumero(Mockito.anyString(), any(), any())).thenReturn(dossierList);

        return dossierId;
    }

    private String getResponseUrl(Response response) {
        return response.getMetadata().get("Location").get(0).toString();
    }
}
