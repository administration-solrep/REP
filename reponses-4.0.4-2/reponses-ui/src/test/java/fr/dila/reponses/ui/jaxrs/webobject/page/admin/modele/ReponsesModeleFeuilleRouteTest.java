package fr.dila.reponses.ui.jaxrs.webobject.page.admin.modele;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.reponses.ui.bean.IndexationDTO;
import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.services.ReponsesModeleFdrFicheUIService;
import fr.dila.reponses.ui.services.ReponsesModeleFdrListUIService;
import fr.dila.reponses.ui.services.ReponsesSelectValueUIService;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.actions.IndexActionService;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.reponses.ui.services.actions.ReponsesModeleFeuilleRouteActionService;
import fr.dila.reponses.ui.th.bean.ReponsesModeleFdrForm;
import fr.dila.reponses.ui.th.model.ReponsesAdminTemplate;
import fr.dila.ss.core.enumeration.StatutModeleFDR;
import fr.dila.ss.ui.enums.SSActionCategory;
import fr.dila.ss.ui.services.SSModeleFdrFicheUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.services.actions.impl.NavigationActionServiceImpl;
import fr.dila.ss.ui.th.bean.ModeleFdrForm;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.core.util.PermissionHelper;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.ArrayList;
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
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.actions.Action;
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
        ReponsesUIServiceLocator.class,
        SpecificContext.class,
        UserSessionHelper.class,
        PermissionHelper.class,
        ReponsesActionsServiceLocator.class,
        ReponsesModeleFdrForm.class,
        ReponsesModeleFeuilleRoute.class,
        SSUIServiceLocator.class
    }
)
@PowerMockIgnore("javax.management.*")
public class ReponsesModeleFeuilleRouteTest {
    private static final String IDENTIFIANT = "identifiant";
    private static final String INTITULE = "intitulé";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private ReponsesModeleFeuilleRoute page;

    @Mock
    private WebContext webContext;

    @Mock
    private SpecificContext context;

    @Mock
    private ReponsesModeleFdrListUIService modeleFDRListUIService;

    @Mock
    private ReponsesModeleFdrFicheUIService modeleFDRFicheUIService;

    @Mock
    private SSModeleFdrFicheUIService ssModeleFDRFicheUIService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private NuxeoPrincipal principal;

    @Mock
    private CoreSession session;

    @Mock
    private DocumentModel documentModel;

    @Mock
    private ReponsesModeleFeuilleRouteActionService modeleService;

    @Mock
    private IndexActionService indexActionService;

    @Mock
    private ReponsesModeleFdrForm modeleForm;

    @Mock
    private ReponsesSelectValueUIService selectValueService;

    @Before
    public void before() throws Exception {
        page = Mockito.spy(new ReponsesModeleFeuilleRoute());
        PowerMockito.spy(UserSession.class);

        Whitebox.setInternalState(page, "context", context);

        when(context.getWebcontext()).thenReturn(webContext);
        when(webContext.getRequest()).thenReturn(request);
        when(webContext.getPrincipal()).thenReturn(principal);
        PowerMockito.doCallRealMethod().when(context).setContextData(Mockito.anyMap());
        context.setContextData(new HashMap<>());
        PowerMockito.doCallRealMethod().when(context).setNavigationContextTitle(any(Breadcrumb.class));
        when(context.getContextData()).thenReturn(new HashMap<>());
        when(context.getFromContextData(STContextDataKey.BREADCRUMB_BASE_URL)).thenReturn("/admin/fdr");
        when(context.getSession()).thenReturn(session);
        when(session.getPrincipal()).thenReturn(principal);

        Breadcrumb breadcrumb = new Breadcrumb(NavigationActionServiceImpl.ESPACE_ADMIN, null, Breadcrumb.TITLE_ORDER);
        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(breadcrumb);
        Mockito.when(context.getNavigationContext()).thenReturn(breadcrumbs);

        PowerMockito.mockStatic(UserSessionHelper.class);
        PowerMockito.mockStatic(ReponsesUIServiceLocator.class);
        PowerMockito.mockStatic(ReponsesActionsServiceLocator.class);
        PowerMockito.mockStatic(SSUIServiceLocator.class);
        when(SSUIServiceLocator.getSSModeleFdrFicheUIService()).thenReturn(ssModeleFDRFicheUIService);
        when(ReponsesActionsServiceLocator.getReponsesModeleFeuilleRouteActionService()).thenReturn(modeleService);
        when(ReponsesActionsServiceLocator.getIndexActionService()).thenReturn(indexActionService);
        when(ReponsesUIServiceLocator.getReponsesModeleFdrFicheUIService()).thenReturn(modeleFDRFicheUIService);
        when(ReponsesUIServiceLocator.getSelectValueUIService()).thenReturn(selectValueService);
        when(SSUIServiceLocator.getSSSelectValueUIService()).thenReturn(selectValueService);

        PowerMockito.mockStatic(PermissionHelper.class);
        when(PermissionHelper.isAdminFonctionnel(principal)).thenReturn(true);
        when(PermissionHelper.isAdminMinisteriel(principal)).thenReturn(true);
    }

    @Test
    public void testGetModeleFdrModification() throws Exception {
        when(modeleForm.getIntitule()).thenReturn(INTITULE);
        when(modeleForm.getEtat()).thenReturn(StatutModeleFDR.BROUILLON.name());
        when(modeleForm.getIsLockByCurrentUser()).thenReturn(true);

        List<Action> leftActions = new ArrayList<>();
        leftActions.add(new Action());
        List<Action> rightActions = new ArrayList<>();
        rightActions.add(new Action());

        when(context.getActions(SSActionCategory.MODELE_FICHE_LEFT_ACTIONS)).thenReturn(leftActions);
        when(context.getActions(SSActionCategory.MODELE_FICHE_RIGHT_ACTIONS)).thenReturn(rightActions);

        when(modeleService.canUserReadRoute(context)).thenReturn(true);
        PowerMockito.whenNew(ReponsesModeleFdrForm.class).withNoArguments().thenReturn(modeleForm);

        ThTemplate template = page.getModeleFdrModification(IDENTIFIANT);

        assertThat(template).isNotNull().isExactlyInstanceOf(ReponsesAdminTemplate.class);
        assertEquals("pages/admin/modele/editModeleFDR", template.getName());

        Breadcrumb breadcrum = new Breadcrumb(
            modeleForm.getIntitule(),
            "/admin/fdr/modele/modification?id=" + IDENTIFIANT,
            Breadcrumb.SUBTITLE_ORDER + 1,
            context.getWebcontext().getRequest()
        );
        verify(context).setNavigationContextTitle(breadcrum);
        verify(context).setCurrentDocument(IDENTIFIANT);
        verify(modeleFDRFicheUIService).getModeleFdrForm(context, modeleForm);
        verify(selectValueService).getRoutingTaskTypesFiltered();

        assertNotNull(template.getData());
        Map<String, Object> map = template.getData();
        assertNotNull(map.get(SSTemplateConstants.MODELE_FORM));
        assertEquals(modeleForm, map.get(SSTemplateConstants.MODELE_FORM));
        assertNotNull(map.get(SSTemplateConstants.IS_ADMIN_FONCTIONNEL));
        assertTrue((Boolean) map.get(SSTemplateConstants.IS_ADMIN_MINISTERIEL));
        assertNotNull(map.get(SSTemplateConstants.MODELE_LEFT_ACTIONS));
        assertEquals(leftActions, map.get(SSTemplateConstants.MODELE_LEFT_ACTIONS));
        assertNotNull(map.get(SSTemplateConstants.MODELE_RIGHT_ACTIONS));
        assertEquals(rightActions, map.get(SSTemplateConstants.MODELE_RIGHT_ACTIONS));
        assertNotNull(map.get(STTemplateConstants.URL_PREVIOUS_PAGE));
        assertEquals("", map.get(STTemplateConstants.URL_PREVIOUS_PAGE));
    }

    @Test
    public void testGetModeleFdrConsultation() throws Exception {
        when(modeleForm.getIntitule()).thenReturn(INTITULE);
        when(modeleForm.getEtat()).thenReturn(StatutModeleFDR.VALIDE.name());

        List<Action> leftActions = new ArrayList<>();
        leftActions.add(new Action());
        List<Action> rightActions = new ArrayList<>();
        rightActions.add(new Action());

        when(context.getActions(SSActionCategory.MODELE_FICHE_LEFT_ACTIONS)).thenReturn(leftActions);
        when(context.getActions(SSActionCategory.MODELE_FICHE_RIGHT_ACTIONS)).thenReturn(rightActions);

        when(modeleService.canUserReadRoute(context)).thenReturn(true);
        PowerMockito.whenNew(ReponsesModeleFdrForm.class).withNoArguments().thenReturn(modeleForm);
        when(modeleFDRFicheUIService.getModeleFdrForm(context, new ReponsesModeleFdrForm())).thenReturn(modeleForm);

        ThTemplate template = page.getModeleFdrModification(IDENTIFIANT);

        assertThat(template).isNotNull().isExactlyInstanceOf(ReponsesAdminTemplate.class);
        assertEquals("pages/admin/modele/consultModeleFDR", template.getName());
        verify(modeleFDRFicheUIService).getModeleFdrForm(context, modeleForm);
    }

    @Test
    public void testGetModeleFdrCreation() {
        when(modeleService.canUserReadRoute(context)).thenReturn(true);
        when(modeleService.canUserCreateRoute(context)).thenReturn(true);

        ThTemplate template = page.getModeleFdrCreation();

        assertThat(template).isNotNull().isExactlyInstanceOf(ReponsesAdminTemplate.class);
        assertEquals("pages/admin/modele/createModeleFDR", template.getName());

        Breadcrumb breadcrum = new Breadcrumb(
            "Créer un modèle de feuilles de route",
            "/admin/fdr/modele/creation",
            Breadcrumb.SUBTITLE_ORDER + 1,
            context.getWebcontext().getRequest()
        );
        verify(context).setNavigationContextTitle(breadcrum);
        verify(context).putInContextData(any(ReponsesContextDataKey.class), any(ModeleFdrForm.class));

        assertNotNull(template.getData());
        Map<String, Object> map = template.getData();
        assertNotNull(map.get(SSTemplateConstants.MODELE_FORM));
        assertNotNull(map.get(STTemplateConstants.URL_PREVIOUS_PAGE));
        assertEquals("", map.get(STTemplateConstants.URL_PREVIOUS_PAGE));

        verify(indexActionService).initIndexationDtoDirectories(any(IndexationDTO.class));
    }

    @Test
    public void testSaveFormModeleUpdate() {
        ReponsesModeleFdrForm modeleForm = new ReponsesModeleFdrForm();
        modeleForm.setId(IDENTIFIANT);

        SolonAlertManager alertManager = new SolonAlertManager();
        alertManager.addSuccessToQueue("message success");

        when(context.getMessageQueue()).thenReturn(alertManager);
        doReturn(null).when(page).redirect(any(String.class));

        page.saveFormModele(modeleForm);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, alertManager);
        verify(modeleFDRFicheUIService).updateModele(context, modeleForm);
        verify(page).redirect("/admin/fdr/modele/modification?id=identifiant#main_content");
    }

    @Test
    public void testSaveFormModeleCreation() {
        ReponsesModeleFdrForm modeleForm = new ReponsesModeleFdrForm();

        SolonAlertManager alertManager = new SolonAlertManager();
        alertManager.addSuccessToQueue("message success");

        when(context.getMessageQueue()).thenReturn(alertManager);
        doReturn(null).when(page).redirect(any(String.class));

        doAnswer(
                invocation -> {
                    modeleForm.setId(IDENTIFIANT);
                    return null;
                }
            )
            .when(modeleFDRFicheUIService)
            .createModele(context, modeleForm);

        page.saveFormModele(modeleForm);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, alertManager);
        verify(modeleFDRFicheUIService).createModele(context, modeleForm);
        verify(page).redirect("/admin/fdr/modele/modification?id=identifiant#main_content");
    }
}
