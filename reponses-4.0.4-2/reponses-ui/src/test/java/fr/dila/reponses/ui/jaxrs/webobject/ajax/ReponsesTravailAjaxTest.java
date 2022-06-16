package fr.dila.reponses.ui.jaxrs.webobject.ajax;

import static fr.dila.reponses.ui.enums.ReponsesActionCategory.CORBEILLE_DOSSIERS_ACTIONS_NOTE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import avro.shaded.com.google.common.collect.Lists;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.bean.RepDossierList;
import fr.dila.reponses.ui.services.DossierListUIService;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.th.bean.DossierListForm;
import fr.dila.reponses.ui.th.bean.DossierTravailListForm;
import fr.dila.ss.ui.services.actions.NavigationActionService;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.cli.MissingArgumentException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
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
        ReponsesServiceLocator.class,
        WebEngine.class,
        SSActionsServiceLocator.class,
        Framework.class
    }
)
@PowerMockIgnore("javax.management.*")
public class ReponsesTravailAjaxTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    ReponsesTravailAjax page = new ReponsesTravailAjax();

    @Mock
    DossierListUIService service;

    @Mock
    WebContext webcontext;

    @Mock
    SpecificContext context;

    @Mock
    HttpServletRequest request;

    @Mock
    private NavigationActionService navigationAction;

    @Mock
    UserSession userSession;

    @Mock
    private ActionManager actionService;

    @Before
    public void before() throws MissingArgumentException {
        service = Mockito.mock(DossierListUIService.class);
        PowerMockito.mockStatic(ReponsesUIServiceLocator.class);
        PowerMockito.mockStatic(WebEngine.class);
        PowerMockito.mockStatic(ReponsesServiceLocator.class);
        PowerMockito.mockStatic(SSActionsServiceLocator.class);
        PowerMockito.mockStatic(Framework.class);

        Mockito.when(ReponsesUIServiceLocator.getDossierListUIService()).thenReturn(service);

        RepDossierList lstDossiers = new RepDossierList();

        Mockito.when(service.getDossiersFromMinCorbeille(Mockito.any())).thenReturn(lstDossiers);
        Mockito.when(service.getDossiersFromPosteCorbeille(Mockito.any())).thenReturn(lstDossiers);
        Mockito.when(service.getDossiersFromSignaleCorbeille(Mockito.any())).thenReturn(lstDossiers);
        Mockito.when(WebEngine.getActiveContext()).thenReturn(webcontext);
        Mockito.when(context.getWebcontext()).thenReturn(webcontext);
        Mockito.when(webcontext.getRequest()).thenReturn(request);
        Mockito.when(webcontext.getUserSession()).thenReturn(userSession);
        when(userSession.get(Breadcrumb.USER_SESSION_KEY)).thenReturn(new ArrayList<>());
        Mockito.when(request.getQueryString()).thenReturn("");
        when(SSActionsServiceLocator.getNavigationActionService()).thenReturn(navigationAction);
        when(Framework.getService(ActionManager.class)).thenReturn(actionService);
        List<Action> actions = new ArrayList<>();
        actions.add(new Action());
        when(actionService.getActions(eq(CORBEILLE_DOSSIERS_ACTIONS_NOTE.name()), any(ActionContext.class)))
            .thenReturn(actions);
        Whitebox.setInternalState(page, "context", context);
    }

    @Test
    public void testMinResults() throws MissingArgumentException {
        ThTemplate template = page.getListeMin("idMin", "idTask", null, null, new DossierTravailListForm());

        assertNotNull(template);
        assertNotNull(template.getData());
        Map<String, Object> mapData = template.getData();
        assertNotNull(mapData.get(STTemplateConstants.RESULT_FORM));
        DossierListForm form = (DossierListForm) mapData.get(STTemplateConstants.RESULT_FORM);
        assertThat(form.getPage()).isEqualTo(1);
        assertThat(form.getSize()).isEqualTo(10);
        assertNull(form.getQuestion());
        assertNull(form.getAuteur());
        assertNull(form.getDate());
        assertNull(form.getOrigineQuestion());
        assertNull(form.getDateSignal());
        assertNull(form.getDelai());
        assertNull(form.getEtat());
        assertNull(form.getIndexPrinc());
        assertNull(form.getMinAttr());
        assertNull(form.getMinInter());
        assertNull(form.getNature());

        assertEquals(0, form.getStartElement());
        assertEquals(0, form.getNbPage(0));

        assertEquals("/travail/listeMin", mapData.get(STTemplateConstants.DATA_URL));
        assertEquals("/ajax/travail/listeMin", mapData.get(STTemplateConstants.DATA_AJAX_URL));

        assertNotNull(mapData.get(STTemplateConstants.RESULT_LIST));
        RepDossierList list = (RepDossierList) mapData.get(STTemplateConstants.RESULT_LIST);
        assertEquals(new Integer(0), list.getNbTotal());
        assertEquals(Lists.newArrayList(), list.getListe());
    }

    @Test
    public void testPosteResults() throws MissingArgumentException {
        ThTemplate template = page.getListePoste("idMin", "idPoste", null, null, new DossierTravailListForm());

        assertNotNull(template);
        assertNotNull(template.getData());
        Map<String, Object> mapData = template.getData();
        assertNotNull(mapData.get(STTemplateConstants.RESULT_FORM));
        DossierListForm form = (DossierListForm) mapData.get(STTemplateConstants.RESULT_FORM);
        assertThat(form.getPage()).isEqualTo(1);
        assertThat(form.getSize()).isEqualTo(10);
        assertNull(form.getQuestion());
        assertNull(form.getAuteur());
        assertNull(form.getDate());
        assertNull(form.getOrigineQuestion());
        assertNull(form.getDateSignal());
        assertNull(form.getDelai());
        assertNull(form.getEtat());
        assertNull(form.getIndexPrinc());
        assertNull(form.getMinAttr());
        assertNull(form.getMinInter());
        assertNull(form.getNature());

        assertEquals(0, form.getStartElement());
        assertEquals(0, form.getNbPage(0));

        assertEquals("/travail/listePoste", mapData.get(STTemplateConstants.DATA_URL));
        assertEquals("/ajax/travail/listePoste", mapData.get(STTemplateConstants.DATA_AJAX_URL));

        assertNotNull(mapData.get(STTemplateConstants.RESULT_LIST));
        RepDossierList list = (RepDossierList) mapData.get(STTemplateConstants.RESULT_LIST);
        assertEquals(new Integer(0), list.getNbTotal());
        assertEquals(Lists.newArrayList(), list.getListe());
    }

    @Test
    public void testSignalResults() throws MissingArgumentException {
        ThTemplate template = page.getListeSignal(Boolean.TRUE, "idMin", null, null, new DossierTravailListForm());

        assertNotNull(template);
        assertNotNull(template.getData());
        Map<String, Object> mapData = template.getData();
        assertNotNull(mapData.get(STTemplateConstants.RESULT_FORM));
        DossierListForm form = (DossierListForm) mapData.get(STTemplateConstants.RESULT_FORM);
        assertThat(form.getPage()).isEqualTo(1);
        assertThat(form.getSize()).isEqualTo(10);
        assertNull(form.getQuestion());
        assertNull(form.getAuteur());
        assertNull(form.getDate());
        assertNull(form.getOrigineQuestion());
        assertNull(form.getDateSignal());
        assertNull(form.getDelai());
        assertNull(form.getEtat());
        assertNull(form.getIndexPrinc());
        assertNull(form.getMinAttr());
        assertNull(form.getMinInter());
        assertNull(form.getNature());

        assertEquals(0, form.getStartElement());
        assertEquals(0, form.getNbPage(0));

        assertEquals("/travail/listeSignal", mapData.get(STTemplateConstants.DATA_URL));
        assertEquals("/ajax/travail/listeSignal", mapData.get(STTemplateConstants.DATA_AJAX_URL));

        assertNotNull(mapData.get(STTemplateConstants.RESULT_LIST));
        RepDossierList list = (RepDossierList) mapData.get(STTemplateConstants.RESULT_LIST);
        assertEquals(new Integer(0), list.getNbTotal());
        assertEquals(Lists.newArrayList(), list.getListe());
    }
}
