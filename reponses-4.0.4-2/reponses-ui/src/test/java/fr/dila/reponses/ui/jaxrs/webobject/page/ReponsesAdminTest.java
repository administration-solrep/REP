package fr.dila.reponses.ui.jaxrs.webobject.page;

import static fr.dila.st.ui.enums.STContextDataKey.BREADCRUMB_BASE_LEVEL;
import static fr.dila.st.ui.enums.STContextDataKey.BREADCRUMB_BASE_URL;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.helper.DtoJsonHelper;
import fr.dila.st.ui.th.model.SpecificContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.session.UserSession;
import org.powermock.api.mockito.PowerMockito;

@RunWith(MockitoJUnitRunner.class)
public class ReponsesAdminTest {
    @Spy
    private ReponsesAdmin page;

    @Mock
    private SpecificContext context;

    @Mock
    private WebContext webContext;

    @Mock
    private NuxeoPrincipal principal;

    @Mock
    private UserSession session;

    @Before
    public void before() {
        PowerMockito.mockStatic(DtoJsonHelper.class);
        Whitebox.setInternalState(page, "context", context);

        doReturn(null).when(page).newObject(any(), any(), any());
        when(context.getWebcontext()).thenReturn(webContext);
        when(webContext.getUserSession()).thenReturn(session);
        when((webContext.getPrincipal())).thenReturn(principal);
    }

    @Test
    public void testUserFiche() throws IllegalAccessException, InstantiationException {
        when(principal.isMemberOf(STBaseFunctionConstant.ESPACE_ADMINISTRATION_READER)).thenReturn(false);
        Object returnedTemplate = page.getUser();
        assertNull(returnedTemplate);

        verify(context).putInContextData(BREADCRUMB_BASE_URL, "/admin/user");
        verify(context).putInContextData(BREADCRUMB_BASE_LEVEL, Breadcrumb.TITLE_ORDER);
        verify(page).newObject(any(), any(), any());
        verify(context, never()).setNavigationContextTitle(any(Breadcrumb.class));
    }
}
