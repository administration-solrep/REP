package fr.dila.reponses.ui.jaxrs.webobject.page;

import static fr.dila.st.ui.enums.STContextDataKey.BREADCRUMB_BASE_LEVEL;
import static fr.dila.st.ui.enums.STContextDataKey.BREADCRUMB_BASE_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.webengine.model.Resource;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.powermock.reflect.Whitebox;

@RunWith(MockitoJUnitRunner.class)
public class ReponsesUtilisateurTest {
    private ReponsesUtilisateur page;

    @Mock
    private SpecificContext context;

    @Mock
    private WebContext webContext;

    @Mock
    private Resource resource;

    @Before
    public void before() {
        page = new ReponsesUtilisateur();

        Whitebox.setInternalState(page, "context", context);
        Whitebox.setInternalState(page, "ctx", webContext);

        when(webContext.newObject(eq("TransverseUser"), eq(context), any(ThTemplate.class))).thenReturn(resource);
    }

    @Test
    public void testUserFiche() {
        Object returnedTemplate = page.getCompte();

        assertThat(returnedTemplate).isEqualTo(resource);

        verify(context).putInContextData(BREADCRUMB_BASE_URL, "/utilisateurs/compte");
        verify(context).putInContextData(BREADCRUMB_BASE_LEVEL, 0);
        verify(context, never()).setNavigationContextTitle(any(Breadcrumb.class));
    }
}
