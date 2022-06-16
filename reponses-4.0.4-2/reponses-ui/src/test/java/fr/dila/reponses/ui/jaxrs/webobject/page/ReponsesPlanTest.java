package fr.dila.reponses.ui.jaxrs.webobject.page;

import static fr.dila.reponses.ui.enums.ReponsesActionCategory.PLAN_CLASSEMENT_DOSSIERS_ACTIONS_GENERAL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.bean.RepDossierList;
import fr.dila.reponses.ui.services.DossierListUIService;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.impl.PlanClassementComponentServiceImpl;
import fr.dila.reponses.ui.th.bean.DossierListForm;
import fr.dila.reponses.ui.th.model.ReponsesPlanClassementTemplate;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.cli.MissingArgumentException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.actions.ActionContext;
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
@PrepareForTest({ ReponsesUIServiceLocator.class, WebEngine.class, Framework.class, ReponsesServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class ReponsesPlanTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private ReponsesPlan page;

    @Mock
    DossierListUIService planService;

    @Mock
    CoreSession session;

    private WebContext webContext;

    @Mock
    private NuxeoPrincipal nuxeoPrincipal;

    @Mock
    private UserSession userSession;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ActionManager actionService;

    @Before
    public void before() {
        PowerMockito.mockStatic(ReponsesUIServiceLocator.class);
        PowerMockito.mockStatic(WebEngine.class);
        PowerMockito.mockStatic(ReponsesServiceLocator.class);
        PowerMockito.mockStatic(Framework.class);

        webContext = mock(WebContext.class);
        session = mock(CoreSession.class);

        Mockito.when(webContext.getCoreSession()).thenReturn(session);
        Mockito.when(WebEngine.getActiveContext()).thenReturn(webContext);
        Mockito.when(webContext.getUserSession()).thenReturn(userSession);
        Mockito.when(webContext.getPrincipal()).thenReturn(nuxeoPrincipal);
        Mockito.when(WebEngine.getActiveContext()).thenReturn(webContext);
        Mockito.when(webContext.getUserSession()).thenReturn(userSession);
        Mockito.when(userSession.get(Breadcrumb.USER_SESSION_KEY)).thenReturn(new ArrayList<>());
        Mockito.when(webContext.getRequest()).thenReturn(request);
        Mockito.when(request.getQueryString()).thenReturn("");
        when(Framework.getService(ActionManager.class)).thenReturn(actionService);
        List<Action> actions = new ArrayList<>();
        actions.add(new Action());
        when(actionService.getActions(eq(PLAN_CLASSEMENT_DOSSIERS_ACTIONS_GENERAL.name()), any(ActionContext.class)))
            .thenReturn(actions);

        page = new ReponsesPlan();
    }

    @Test
    public void testHome() {
        ThTemplate template = page.getHome();
        assertNotNull(template);
        assertTrue(template instanceof ReponsesPlanClassementTemplate);
        assertEquals("pages/planClassement", template.getName());
        assertEquals("pageLayout", template.getLayout());

        SpecificContext context = template.getContext();
        assertNotNull(context);
        assertNotNull(context.getSession());
        assertEquals(session, context.getSession());
        assertNotNull(context.getWebcontext());
        assertEquals(webContext, context.getWebcontext());
        assertNotNull(context.getContextData());
        assertEquals(1, context.getContextData().size());
        assertEquals("", context.getContextData().get(PlanClassementComponentServiceImpl.ACTIVE_KEY));

        assertNotNull(template.getData());
        assertTrue(template.getData().isEmpty());
    }

    @Test
    public void testEmptyListe() throws MissingArgumentException {
        when(ReponsesUIServiceLocator.getDossierListUIService()).thenReturn(planService);

        when(planService.getDossiersFromPlanClassement(Mockito.any())).thenReturn(new RepDossierList());

        ThTemplate template = page.getListe("key", "parentKey", "", new DossierListForm());
        assertNotNull(template);
        assertTrue(template instanceof ReponsesPlanClassementTemplate);
        assertEquals("pages/listeQuestionPlan", template.getName());
        assertEquals("pageLayout", template.getLayout());

        SpecificContext context = template.getContext();
        assertNotNull(context);
        assertNotNull(context.getContextData());
        assertEquals(5, context.getContextData().size());
        assertEquals("parentKey__key", context.getContextData().get(PlanClassementComponentServiceImpl.ACTIVE_KEY));
        assertEquals("parentKey", context.getContextData().get("cleParent"));
        assertEquals("key", context.getContextData().get("cle"));
        assertEquals("", context.getContextData().get("origine"));
        assertNotNull(context.getContextData().get("form"));

        assertNotNull(template.getData());
        assertNotNull(template.getData().get(STTemplateConstants.RESULT_LIST));
    }
}
