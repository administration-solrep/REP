package fr.dila.reponses.ui.jaxrs.webobject.page;

import static fr.dila.reponses.ui.enums.ReponsesActionCategory.CORBEILLE_DOSSIERS_ACTIONS_NOTE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.bean.RepDossierList;
import fr.dila.reponses.ui.services.DossierListUIService;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.impl.ReponsesMailboxListComponentServiceImpl;
import fr.dila.reponses.ui.th.bean.DossierListForm;
import fr.dila.reponses.ui.th.bean.DossierTravailListForm;
import fr.dila.reponses.ui.th.constants.ReponsesTemplateConstants;
import fr.dila.reponses.ui.th.model.ReponsesTravailTemplate;
import fr.dila.ss.ui.services.ActualiteUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.services.actions.NavigationActionService;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
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
@PrepareForTest(
    {
        ReponsesUIServiceLocator.class,
        WebEngine.class,
        Framework.class,
        ReponsesServiceLocator.class,
        SSActionsServiceLocator.class,
        SSUIServiceLocator.class
    }
)
@PowerMockIgnore("javax.management.*")
public class ReponsesTravailTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private ReponsesTravail page;

    @Mock
    CoreSession session;

    @Mock
    DossierListUIService planService;

    @Mock
    private WebContext webContext;

    @Mock
    private UserSession userSession;

    @Mock
    private NuxeoPrincipal nuxeoPrincipal;

    @Mock
    private HttpServletRequest request;

    @Mock
    private NavigationActionService navigationAction;

    @Mock
    private ActionManager actionService;

    @Mock
    ActualiteUIService actualiteService;

    @Before
    public void before() {
        page = Mockito.spy(new ReponsesTravail());

        Mockito.when(webContext.getUserSession()).thenReturn(userSession);

        PowerMockito.mockStatic(ReponsesUIServiceLocator.class);
        PowerMockito.mockStatic(WebEngine.class);
        PowerMockito.mockStatic(ReponsesServiceLocator.class);
        PowerMockito.mockStatic(SSActionsServiceLocator.class);
        PowerMockito.mockStatic(SSUIServiceLocator.class);
        PowerMockito.mockStatic(Framework.class);
        PowerMockito.mockStatic(DossierListForm.class);

        when(SSActionsServiceLocator.getNavigationActionService()).thenReturn(navigationAction);
        when(SSUIServiceLocator.getActualiteUIService()).thenReturn(actualiteService);
        when(webContext.getCoreSession()).thenReturn(session);
        when(webContext.getPrincipal()).thenReturn(nuxeoPrincipal);
        when(WebEngine.getActiveContext()).thenReturn(webContext);
        when(webContext.getRequest()).thenReturn(request);
        when(userSession.get(Breadcrumb.USER_SESSION_KEY)).thenReturn(new ArrayList<>());
        when(request.getQueryString()).thenReturn("");
        when(Framework.getService(ActionManager.class)).thenReturn(actionService);
        List<Action> actions = new ArrayList<>();
        actions.add(new Action());
        when(actionService.getActions(eq(CORBEILLE_DOSSIERS_ACTIONS_NOTE.name()), any(ActionContext.class)))
            .thenReturn(actions);

        page = new ReponsesTravail();
    }

    @Test
    public void testHome() {
        ThTemplate template = page.getHome();
        assertNotNull(template);
        assertTrue(template instanceof ReponsesTravailTemplate);
        assertEquals("pages/espaceTravailHome", template.getName());
        assertEquals("pageLayout", template.getLayout());

        SpecificContext context = template.getContext();
        assertNotNull(context);
        assertNotNull(context.getWebcontext());
        assertEquals(webContext, context.getWebcontext());
        assertNotNull(context.getSession());
        assertEquals(session, context.getSession());
        assertNotNull(context.getContextData());
        assertEquals(1, context.getContextData().size());
        assertEquals("", context.getContextData().get(ReponsesMailboxListComponentServiceImpl.ACTIVE_KEY));

        assertNotNull(template.getData());
        assertTrue((Boolean) template.getData().get(STTemplateConstants.IS_FROM_TRAVAIL));
    }

    @Test
    public void testEmptyMinListe() throws MissingArgumentException {
        when(ReponsesUIServiceLocator.getDossierListUIService()).thenReturn(planService);

        when(planService.getDossiersFromMinCorbeille(any())).thenReturn(new RepDossierList());

        ThTemplate template = page.getListeMin(null, null, null, null, new DossierTravailListForm());
        assertNotNull(template);
        assertTrue(template instanceof ReponsesTravailTemplate);
        assertTemplateData((ReponsesTravailTemplate) template);
        assertEquals("pages/listeQuestionCorbeille", template.getName());
        assertEquals("pageLayout", template.getLayout());

        SpecificContext context = template.getContext();
        assertNotNull(context);
        assertNotNull(context.getContextData());
    }

    @Test
    public void testEmptyPosteListe() throws MissingArgumentException {
        when(ReponsesUIServiceLocator.getDossierListUIService()).thenReturn(planService);

        when(planService.getDossiersFromPosteCorbeille(any())).thenReturn(new RepDossierList());

        ThTemplate template = page.getListePoste(null, null, null, null, new DossierTravailListForm());
        assertNotNull(template);
        assertTrue(template instanceof ReponsesTravailTemplate);
        assertTemplateData((ReponsesTravailTemplate) template);
        assertEquals("pages/listeQuestionCorbeille", template.getName());
        assertEquals("pageLayout", template.getLayout());

        SpecificContext context = template.getContext();
        assertNotNull(context);
        assertNotNull(context.getContextData());
    }

    @Test
    public void testEmptySignalListe() throws MissingArgumentException {
        when(ReponsesUIServiceLocator.getDossierListUIService()).thenReturn(planService);

        when(planService.getDossiersFromSignaleCorbeille(any())).thenReturn(new RepDossierList());

        ThTemplate template = page.getListeSignal(null, null, null, null, new DossierTravailListForm());
        assertNotNull(template);
        assertTrue(template instanceof ReponsesTravailTemplate);
        assertTemplateData((ReponsesTravailTemplate) template);
        assertEquals("pages/listeQuestionCorbeille", template.getName());
        assertEquals("pageLayout", template.getLayout());

        SpecificContext context = template.getContext();
        assertNotNull(context);
        assertNotNull(context.getContextData());
    }

    private void assertTemplateData(ReponsesTravailTemplate template) {
        assertNotNull(template.getData());
        assertEquals(20, template.getData().size());
        assertNotNull(template.getData().get(STTemplateConstants.RESULT_LIST));
        assertTrue(template.getData().containsKey(STTemplateConstants.TITRE));
        assertTrue(template.getData().containsKey(STTemplateConstants.SOUS_TITRE));
        assertTrue(template.getData().containsKey(ReponsesTemplateConstants.NB_DOSSIER));
        assertTrue(template.getData().containsKey(STTemplateConstants.RESULT_FORM));
        assertTrue(template.getData().containsKey(STTemplateConstants.LST_COLONNES));
        assertTrue(template.getData().containsKey(STTemplateConstants.OTHER_PARAMETER));
        assertTrue(template.getData().containsKey(STTemplateConstants.GENERALE_ACTIONS));
        assertTrue(template.getData().containsKey(SSTemplateConstants.FDR_ACTIONS));
        assertTrue(template.getData().containsKey(SSTemplateConstants.NOTE_ACTIONS));
        assertTrue(template.getData().containsKey(SSTemplateConstants.PRINT_ACTIONS));
        assertTrue(template.getData().containsKey(ReponsesTemplateConstants.RENVOI_ACTIONS));
        assertTrue(template.getData().containsKey(SSTemplateConstants.DIVERS_ACTIONS));
        assertTrue(template.getData().containsKey(STTemplateConstants.DATA_URL));
        assertTrue(template.getData().containsKey(STTemplateConstants.DATA_AJAX_URL));
        assertTrue(template.getData().containsKey(SSTemplateConstants.NAVIGATION_ACTIONS));
        assertTrue((Boolean) template.getData().get(STTemplateConstants.IS_FROM_TRAVAIL));
        assertTrue(template.getData().containsKey("activeKey"));
    }
}
