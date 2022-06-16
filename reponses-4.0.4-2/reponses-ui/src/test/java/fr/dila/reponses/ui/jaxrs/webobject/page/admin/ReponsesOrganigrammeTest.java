package fr.dila.reponses.ui.jaxrs.webobject.page.admin;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.ss.ui.services.SSOrganigrammeManagerService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.st.core.util.FileUtils;
import fr.dila.st.ui.bean.OrganigrammeElementDTO;
import fr.dila.st.ui.services.OrganigrammeTreeUIService;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.utils.FileDownloadUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.webengine.WebEngine;
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
        WebEngine.class,
        ReponsesUIServiceLocator.class,
        STUIServiceLocator.class,
        SSUIServiceLocator.class,
        FileUtils.class,
        FileDownloadUtils.class
    }
)
@PowerMockIgnore("javax.management.*")
public class ReponsesOrganigrammeTest {
    private static final String FILE_NAME = "fileName";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private ReponsesOrganigramme controller;

    @Mock
    private WebContext webContext;

    @Mock
    private NuxeoPrincipal principal;

    @Mock
    private CoreSession session;

    @Mock
    private UserSession userSession;

    @Mock
    private SpecificContext context;

    @Mock
    private OrganigrammeTreeUIService organigrammeTreeUIService;

    @Mock
    private SSOrganigrammeManagerService ssoManager;

    @Mock
    private List<OrganigrammeElementDTO> dtos;

    private File file;

    @Mock
    Response response;

    @Before
    public void before() throws Exception {
        controller = spy(new ReponsesOrganigramme());

        PowerMockito.mockStatic(WebEngine.class);
        PowerMockito.mockStatic(ReponsesUIServiceLocator.class);
        PowerMockito.mockStatic(STUIServiceLocator.class);
        PowerMockito.mockStatic(SSUIServiceLocator.class);
        PowerMockito.mockStatic(FileUtils.class);
        PowerMockito.mockStatic(FileDownloadUtils.class);

        when(STUIServiceLocator.getOrganigrammeTreeService()).thenReturn(organigrammeTreeUIService);
        when(SSUIServiceLocator.getSSOrganigrammeManagerService()).thenReturn(ssoManager);

        when(WebEngine.getActiveContext()).thenReturn(webContext);
        when(webContext.getPrincipal()).thenReturn(principal);
        when(webContext.getCoreSession()).thenReturn(session);
        when(webContext.getUserSession()).thenReturn(userSession);
        when(context.getWebcontext()).thenReturn(webContext);
        when(context.getSession()).thenReturn(session);
        when(principal.isMemberOf(anyString())).thenReturn(true);
        doCallRealMethod().when(context).setContextData(any());
        doCallRealMethod().when(context).getContextData();
        doCallRealMethod().when(context).setMessageQueue(any());
        doCallRealMethod().when(context).getMessageQueue();

        file = new File(FILE_NAME);

        when(FileUtils.getAppTmpFolder()).thenReturn(file);
        when(FileUtils.sanitizePathTraversal(FILE_NAME)).thenReturn(FILE_NAME);
        when(FileDownloadUtils.getResponse(any(File.class), anyString())).thenReturn(response);
        doReturn(null).when(controller).newObject(any());

        doReturn(null).when(controller).newObject(any(), any(), any());
    }

    @Test
    public void testUserOrganigramme() throws Exception {
        List<Action> baseActions = new ArrayList<>();
        List<Action> mainActions = new ArrayList<>();

        when(organigrammeTreeUIService.getOrganigramme(any(SpecificContext.class))).thenReturn(dtos);
        when(ssoManager.loadBaseAdminOrganigrammeActions(session)).thenReturn(baseActions);
        when(ssoManager.loadMainAdminOrganigrammeActions(session)).thenReturn(mainActions);

        Whitebox.setInternalState(controller, "context", context);
        controller.getUserOrganigramme();

        verify(controller).newObject(eq("OrganigrammeAjax"), eq(context), any());
    }

    @Test
    public void testDownload() {
        Response response = controller.download(FILE_NAME);

        assertNotNull(response);
        PowerMockito.verifyStatic();
        FileDownloadUtils.getResponse(file, FILE_NAME);
    }
}
