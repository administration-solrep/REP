package fr.dila.reponses.ui.jaxrs.webobject.page;

import static fr.dila.reponses.ui.jaxrs.webobject.page.ReponsesSuivi.REPONSE_CHAMP_CONTRIB_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import fr.dila.ss.ui.bean.RequeteExperteDTO;
import fr.dila.ss.ui.enums.SSUserSessionKey;
import fr.dila.st.core.requete.recherchechamp.RechercheChampService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.session.UserSession;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ ServiceUtil.class, WebEngine.class, UserSessionHelper.class, ReponsesSuivi.class })
@PowerMockIgnore("javax.management.*")
public class ReponsesSuiviTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private WebContext webContext;

    @Mock
    private UserSession userSession;

    @Mock
    private HttpServletRequest request;

    @Mock
    private RechercheChampService champService;

    @Mock
    private SpecificContext context;

    @Spy
    private ThTemplate template;

    private ReponsesSuivi controlleur;

    @Before
    public void setUp() {
        controlleur = Mockito.spy(new ReponsesSuivi());
        Whitebox.setInternalState(controlleur, "context", context);
        Whitebox.setInternalState(controlleur, "template", template);
        PowerMockito.mockStatic(ServiceUtil.class);
        PowerMockito.mockStatic(WebEngine.class);
        PowerMockito.mockStatic(STServiceLocator.class);
        PowerMockito.mockStatic(UserSessionHelper.class);

        when(WebEngine.getActiveContext()).thenReturn(webContext);
        when(webContext.getUserSession()).thenReturn(userSession);
        when(webContext.getRequest()).thenReturn(request);
    }

    @Test
    public void testGetHomeSessionNotEmpty() {
        when(STServiceLocator.getRechercheChampService()).thenReturn(champService);
        when(champService.getChamps(REPONSE_CHAMP_CONTRIB_NAME)).thenReturn(new ArrayList<>());

        RequeteExperteDTO requeteExperteDTO = new RequeteExperteDTO();
        when(
            UserSessionHelper.getUserSessionParameter(
                context,
                SSUserSessionKey.REQUETE_EXPERTE_DTO.name() + "_REP",
                RequeteExperteDTO.class
            )
        )
            .thenReturn(requeteExperteDTO);

        controlleur.getHome();

        assertEquals(requeteExperteDTO, template.getData().get("requeteExperteDTO"));
        assertTrue((boolean) template.getData().get("isFirstChamp"));
    }

    @Test
    public void testGetHomeSessionEmpty() throws Exception {
        RequeteExperteDTO requeteExperteDTO = spy(new RequeteExperteDTO());

        when(STServiceLocator.getRechercheChampService()).thenReturn(champService);
        when(champService.getChamps(REPONSE_CHAMP_CONTRIB_NAME)).thenReturn(new ArrayList<>());

        PowerMockito.whenNew(RequeteExperteDTO.class).withAnyArguments().thenReturn(requeteExperteDTO);
        when(UserSessionHelper.getUserSessionParameter(any(), any(), any())).thenReturn(null);

        controlleur.getHome();

        PowerMockito.verifyStatic(Mockito.times(1));
        UserSessionHelper.putUserSessionParameter(
            eq(context),
            eq(SSUserSessionKey.REQUETE_EXPERTE_DTO.name() + "_REP"),
            any(RequeteExperteDTO.class)
        );

        assertEquals(requeteExperteDTO, template.getData().get("requeteExperteDTO"));
        assertTrue((boolean) template.getData().get("isFirstChamp"));
    }
}
