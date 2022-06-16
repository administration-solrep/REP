package fr.dila.reponses.ui.jaxrs.webobject.page;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.reponses.api.enumeration.StatutReponseEnum;
import fr.dila.reponses.ui.bean.RechercheDTO;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.impl.RechercheUIServiceImpl;
import fr.dila.reponses.ui.th.model.ReponsesRechercheTemplate;
import fr.dila.ss.ui.jaxrs.webobject.page.SSRecherche;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
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
    { ServiceUtil.class, ReponsesUIServiceLocator.class, WebEngine.class, UserSessionHelper.class, Framework.class }
)
@PowerMockIgnore("javax.management.*")
public class ReponsesRechercheTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private RechercheUIServiceImpl rechercheUIService;

    @Mock
    private ActionManager actionManager;

    @Mock
    private WebContext webContext;

    @Mock
    private UserSession userSession;

    @Mock
    private HttpServletRequest request;

    @Mock
    private SSRecherche ssRecherche;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(ServiceUtil.class);
        PowerMockito.mockStatic(ReponsesUIServiceLocator.class);
        PowerMockito.mockStatic(WebEngine.class);
        PowerMockito.mockStatic(UserSessionHelper.class);
        PowerMockito.mockStatic(Framework.class);

        when(Framework.getService(ActionManager.class)).thenReturn(actionManager);

        when(WebEngine.getActiveContext()).thenReturn(webContext);
        when(webContext.getUserSession()).thenReturn(userSession);

        when(webContext.getRequest()).thenReturn(request);
        when(request.getQueryString()).thenReturn("");
        when(userSession.get(Breadcrumb.USER_SESSION_KEY)).thenReturn(new ArrayList<>());

        when(UserSessionHelper.getUserSessionParameter(any(), any(), any())).thenReturn(new HashMap<String, Object>());
    }

    @Test
    public void testRecherche() {
        ReponsesRechercheTemplate template = spy(new ReponsesRechercheTemplate());
        ReponsesRecherche reponseRecherche = spy(new ReponsesRecherche());
        RechercheDTO rechercheDTO = new RechercheDTO();
        when(rechercheUIService.initRechercheDTO(any())).thenReturn(rechercheDTO);
        when(ReponsesUIServiceLocator.getRechercheUIService()).thenReturn(rechercheUIService);
        doReturn(null).when(reponseRecherche).newObject(any(), any(), any());
        doReturn(template).when(reponseRecherche).getMyTemplate();

        reponseRecherche.getRecherche();

        verify(reponseRecherche).newObject(Mockito.eq("AppliRechercheResultats"), any(), Mockito.eq(template));
        assertEquals(template.getData().get("rechercheDto"), rechercheDTO);
        assertEquals(
            template.getData().get("statusReponse"),
            ResourceHelper.getStrings(StatutReponseEnum.getLabelKeys())
        );
        assertEquals(7, template.getData().size());
    }

    @Test
    public void testRechercheRapide() {
        ReponsesRechercheTemplate template = spy(new ReponsesRechercheTemplate());
        ReponsesRecherche reponseRecherche = spy(new ReponsesRecherche());

        doReturn(null).when(reponseRecherche).newObject(any(), any(), any());
        Whitebox.setInternalState(reponseRecherche, "template", template);

        reponseRecherche.doRechercheRapide("nor-test");

        verify(reponseRecherche).newObject(Mockito.eq("AppliRechercheRapideResultats"), any(), Mockito.eq(template));
    }
}
