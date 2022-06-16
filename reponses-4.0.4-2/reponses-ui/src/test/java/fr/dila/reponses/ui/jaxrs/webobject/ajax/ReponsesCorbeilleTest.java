package fr.dila.reponses.ui.jaxrs.webobject.ajax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import fr.dila.reponses.ui.bean.PlanClassementDTO;
import fr.dila.reponses.ui.enums.ReponsesActionEnum;
import fr.dila.reponses.ui.services.PlanClassementComponentService;
import fr.dila.reponses.ui.services.ReponsesMailboxListComponentService;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.impl.PlanClassementComponentServiceImpl;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.actions.ActionContext;
import org.nuxeo.ecm.platform.actions.ejb.ActionManager;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.runtime.api.Framework;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ ReponsesUIServiceLocator.class, WebEngine.class, Framework.class })
@PowerMockIgnore("javax.management.*")
public class ReponsesCorbeilleTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    ReponsesCorbeille page = new ReponsesCorbeille();

    @Mock
    PlanClassementComponentService service;

    @Mock
    ReponsesMailboxListComponentService mailboxService;

    @Mock
    WebContext webContext;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ActionManager actionService;

    @Before
    public void before() {
        PowerMockito.mockStatic(ReponsesUIServiceLocator.class);
        PowerMockito.mockStatic(WebEngine.class);
        PowerMockito.mockStatic(Framework.class);
        Mockito.when(WebEngine.getActiveContext()).thenReturn(webContext);
        Mockito.when(Framework.getService(ActionManager.class)).thenReturn(actionService);
        Mockito.when(webContext.getRequest()).thenReturn(request);
        Mockito.when(request.getQueryString()).thenReturn("");
    }

    @Test
    public void testPlan() {
        Mockito.when(ReponsesUIServiceLocator.getPlanClassementComponentService()).thenReturn(service);
        Map<String, Object> map = new HashMap<>();
        PlanClassementDTO dto = new PlanClassementDTO();
        map.put("planClassementMap", dto);
        map.put(PlanClassementComponentServiceImpl.ACTIVE_KEY, "test");
        Mockito.when(service.getData(Mockito.any())).thenReturn(map);
        String idAction = ReponsesActionEnum.ADD_FAVORIS_PC.getName();
        Action action = new Action(idAction, new String[] {});
        Mockito.when(actionService.getAction(eq(idAction), any(ActionContext.class), eq(true))).thenReturn(action);
        ThTemplate template = page.getPlan(null, null);
        assertNotNull(template);
        assertEquals("fragments/components/planClassementArbre", template.getName());
        assertEquals("ajaxLayout", template.getLayout());

        SpecificContext context = template.getContext();
        assertNotNull(context);
        assertNull(context.getSession());
        assertNotNull(context.getWebcontext());
        assertNotNull(context.getContextData());
        assertNotNull(context.getAction(ReponsesActionEnum.ADD_FAVORIS_PC));
        assertEquals(0, context.getContextData().size());

        Map<String, Object> datas = template.getData();
        assertNotNull(datas);
        assertEquals(8, datas.size());
        assertEquals(dto.getChilds(), datas.get(STTemplateConstants.TREE_LIST));
        assertEquals(1, datas.get(STTemplateConstants.LEVEL));
        assertEquals("", datas.get(SSTemplateConstants.MY_ID));
        assertEquals("", datas.get(SSTemplateConstants.TOGGLER_ID));
        assertEquals(true, datas.get(STTemplateConstants.IS_OPEN));
        assertEquals("test", datas.get(PlanClassementComponentServiceImpl.ACTIVE_KEY));
        assertEquals("plan de classement", datas.get(STTemplateConstants.TITLE));

        template = page.getPlan("typeTest", "keyTest");

        context = template.getContext();
        assertNotNull(context.getContextData());
        assertEquals(2, context.getContextData().size());
        assertEquals("typeTest", context.getContextData().get(PlanClassementComponentServiceImpl.ASSEMBLEE_KEY));
        assertEquals("keyTest", context.getContextData().get(PlanClassementComponentServiceImpl.SELECTED_KEY));
    }
}
