package fr.dila.reponses.ui.jaxrs.webobject.page;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Lists;
import fr.dila.reponses.ui.services.ReponsesSelectValueUIService;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.impl.StatistiquesUIServiceImpl;
import fr.dila.ss.api.birt.BirtReport;
import fr.dila.ss.api.birt.ReportProperty;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.ui.bean.BirtReportList;
import fr.dila.ss.ui.services.SSSelectValueUIService;
import fr.dila.ss.ui.services.SSStatistiquesUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.th.bean.BirtReportListForm;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.actions.ejb.ActionManager;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.session.UserSession;
import org.nuxeo.runtime.api.Framework;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(
    {
        SSUIServiceLocator.class,
        ReponsesUIServiceLocator.class,
        WebEngine.class,
        UserSessionHelper.class,
        Framework.class,
        SpecificContext.class,
        ReponsesStatistiques.class
    }
)
@PowerMockIgnore("javax.management.*")
public class ReponsesStatistiquesTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private StatistiquesUIServiceImpl service;

    @Mock
    private SSStatistiquesUIService ssService;

    @Mock
    private SSSelectValueUIService ssSelectValueUIService;

    @Mock
    private ReponsesSelectValueUIService selectService;

    @Mock
    private WebContext webContext;

    @Mock
    private UserSession userSession;

    @Mock
    private HttpServletRequest request;

    @Mock
    private BirtReport report;

    @Mock
    private ActionManager actionManager;

    @Mock
    private SpecificContext context;

    @Mock
    private CoreSession mockSession;

    @Mock
    private SpecificContext mockContext;

    @Mock
    private SSPrincipal principal;

    private final List<SelectValueDTO> lstMins = Lists.newArrayList(new SelectValueDTO("idMin", "Premier Ministre"));

    @Mock
    ReportProperty property;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(SSUIServiceLocator.class);
        PowerMockito.mockStatic(ReponsesUIServiceLocator.class);
        PowerMockito.mockStatic(WebEngine.class);
        PowerMockito.mockStatic(Framework.class);

        Mockito.when(WebEngine.getActiveContext()).thenReturn(webContext);
        Mockito.when(webContext.getUserSession()).thenReturn(userSession);
        Mockito.when(webContext.getPrincipal()).thenReturn(principal);
        Mockito.when(webContext.getRequest()).thenReturn(request);

        Mockito.when(request.getQueryString()).thenReturn("");
        Mockito.when(userSession.get(Breadcrumb.USER_SESSION_KEY)).thenReturn(new ArrayList<>());

        Mockito.when(Framework.getService(ActionManager.class)).thenReturn(actionManager);
        Mockito.when(actionManager.getActions(Mockito.anyString(), Mockito.any())).thenReturn(new ArrayList<>());

        Mockito.when(SSUIServiceLocator.getSSStatistiquesUIService()).thenReturn(service);
        Mockito.when(SSUIServiceLocator.getSSSelectValueUIService()).thenReturn(ssSelectValueUIService);
        Mockito.when(ReponsesUIServiceLocator.getStatistiquesUIService()).thenReturn(service);
        Mockito.when(ReponsesUIServiceLocator.getSelectValueUIService()).thenReturn(selectService);

        Mockito.when(service.getBirtReport(Mockito.anyString())).thenReturn(report);
        Mockito.when(property.getType()).thenReturn(ReportProperty.TYPE_SCALAR_ORGANIGRAMMESELECT_MIN);

        Mockito.when(service.getReportUrl(Mockito.any(), Mockito.any())).thenCallRealMethod();
        Mockito.when(service.hasRequiredParameters(Mockito.any(), Mockito.any())).thenCallRealMethod();

        Mockito.when(ssSelectValueUIService.getCurrentMinisteres()).thenReturn(lstMins);
    }

    @Test
    public void testEmptyStat() {
        Mockito.when(service.getScalarProperties(report)).thenReturn(new ArrayList<>());
        Mockito.when(service.generateReport(Mockito.any(BirtReport.class), Mockito.any())).thenReturn("/path/statXX");
        Mockito.when(service.hasRightToViewSggStat(Mockito.any())).thenReturn(true);
        Mockito.when(report.getId()).thenReturn("statXX");

        ReponsesStatistiques page = new ReponsesStatistiques();
        Object result = page.getStatistique("statXX", null, null, null);
        assertTrue(result instanceof ThTemplate);

        ThTemplate template = (ThTemplate) result;
        Map<String, Object> map = template.getData();
        assertNotNull(map);
        assertEquals(report, map.get(SSTemplateConstants.BIRT_REPORT));
        assertNull(map.get(ReponsesStatistiques.KEY_LIST_MINISTERES));
        assertTrue(StringUtils.isEmpty((String) map.get(STTemplateConstants.URL_PREVIOUS_PAGE)));
        assertFalse((Boolean) map.get(ReponsesStatistiques.KEY_DISPLAY_ORG_SELECT_DIR));
        assertFalse((Boolean) map.get(ReponsesStatistiques.KEY_DISPLAY_ORG_SELECT_MIN));
        assertFalse((Boolean) map.get(ReponsesStatistiques.KEY_DISPLAY_ORG_SELECT_POSTE));
        assertTrue((Boolean) map.get(SSTemplateConstants.DISPLAY_REPORT));
        assertEquals(report.getId(), map.get(ReponsesStatistiques.KEY_REPORT_URL));
        assertEquals("/path/" + report.getId(), map.get(SSTemplateConstants.GENERATED_REPORT_PATH));
    }

    @Test
    public void testStatExpectingParams() {
        Mockito.when(service.getScalarProperties(report)).thenReturn(new ArrayList<>());
        Mockito.when(service.generateReport(Mockito.any(BirtReport.class), Mockito.any())).thenReturn("/path/statXX");
        Mockito.when(service.hasRightToViewSggStat(Mockito.any())).thenReturn(true);

        Mockito.when(report.getId()).thenReturn("statXX");
        Map<String, ReportProperty> propMap = new HashMap<>();
        propMap.put("parameterValue", property);

        Mockito.when(report.getProperties()).thenReturn(propMap);
        Mockito.when(service.getScalarProperties(report)).thenReturn(Lists.newArrayList(property));

        ReponsesStatistiques page = new ReponsesStatistiques();
        Object result = page.getStatistique("statXX", null, null, null);
        assertTrue(result instanceof ThTemplate);

        ThTemplate template = (ThTemplate) result;
        Map<String, Object> map = template.getData();
        assertNotNull(map);
        assertEquals(report, map.get(SSTemplateConstants.BIRT_REPORT));
        assertEquals(lstMins, map.get(ReponsesStatistiques.KEY_LIST_MINISTERES));
        assertTrue(StringUtils.isEmpty((String) map.get(STTemplateConstants.URL_PREVIOUS_PAGE)));
        assertFalse((Boolean) map.get(ReponsesStatistiques.KEY_DISPLAY_ORG_SELECT_DIR));
        assertTrue((Boolean) map.get(ReponsesStatistiques.KEY_DISPLAY_ORG_SELECT_MIN));
        assertFalse((Boolean) map.get(ReponsesStatistiques.KEY_DISPLAY_ORG_SELECT_POSTE));
        assertFalse((Boolean) map.get(SSTemplateConstants.DISPLAY_REPORT));
        assertEquals(report.getId() + "?", map.get(ReponsesStatistiques.KEY_REPORT_URL));
        assertEquals("/path/" + report.getId(), map.get(SSTemplateConstants.GENERATED_REPORT_PATH));

        result = page.getStatistique("statXX", "isMin", null, null);
        assertTrue(result instanceof ThTemplate);

        template = (ThTemplate) result;
        map = template.getData();
        assertNotNull(map);
        assertEquals(report, map.get(SSTemplateConstants.BIRT_REPORT));
        assertEquals(lstMins, map.get(ReponsesStatistiques.KEY_LIST_MINISTERES));
        assertTrue(StringUtils.isEmpty((String) map.get(STTemplateConstants.URL_PREVIOUS_PAGE)));
        assertFalse((Boolean) map.get(ReponsesStatistiques.KEY_DISPLAY_ORG_SELECT_DIR));
        assertTrue((Boolean) map.get(ReponsesStatistiques.KEY_DISPLAY_ORG_SELECT_MIN));
        assertFalse((Boolean) map.get(ReponsesStatistiques.KEY_DISPLAY_ORG_SELECT_POSTE));
        assertTrue((Boolean) map.get(SSTemplateConstants.DISPLAY_REPORT));
        assertEquals(report.getId() + "?ministereId=isMin", map.get(ReponsesStatistiques.KEY_REPORT_URL));
        assertEquals("/path/" + report.getId(), map.get(SSTemplateConstants.GENERATED_REPORT_PATH));
    }

    @Test
    public void testGetBirtReport() {
        String reportDirectoryName = "reportDirectoryName";
        String htmlReportContent = "myContent";

        Mockito
            .when(service.getHtmlReportContent(Mockito.any(SpecificContext.class), Mockito.eq(reportDirectoryName)))
            .thenReturn(htmlReportContent);

        ReponsesStatistiques page = new ReponsesStatistiques();

        assertEquals(htmlReportContent, page.getBirtReport(reportDirectoryName, null));
        assertNull(page.getBirtReport("anyOtherValue", null));
    }

    @Test
    public void testGetHome() throws Exception {
        BirtReportListForm birtReportListForm = new BirtReportListForm();
        BirtReportList birtReportList = new BirtReportList();

        PowerMockito.whenNew(SpecificContext.class).withNoArguments().thenReturn(mockContext);

        assertEquals(mockContext, new SpecificContext());

        Mockito.when(mockContext.getSession()).thenReturn(mockSession);
        Mockito.when(mockSession.getPrincipal()).thenReturn(principal);
        Mockito.when(mockContext.getWebcontext()).thenReturn(webContext);

        Mockito
            .when(service.getStatistiqueList(Mockito.any(SSPrincipal.class), Mockito.eq(birtReportListForm)))
            .thenReturn(birtReportList);

        ReponsesStatistiques page = new ReponsesStatistiques();

        ThTemplate template = page.getHome(birtReportListForm);
        assertNotNull(template);

        Map<String, Object> map = template.getData();
        assertNotNull(map);

        assertEquals(birtReportList, map.get(STTemplateConstants.RESULT_LIST));
        assertEquals(birtReportListForm, map.get(STTemplateConstants.RESULT_FORM));
        assertEquals(ReponsesStatistiques.MAP_VALUE_DATAURL, map.get(STTemplateConstants.DATA_URL));
        assertEquals(ReponsesStatistiques.MAP_VALUE_DATAAJAXURL, map.get(STTemplateConstants.DATA_AJAX_URL));
    }
}
