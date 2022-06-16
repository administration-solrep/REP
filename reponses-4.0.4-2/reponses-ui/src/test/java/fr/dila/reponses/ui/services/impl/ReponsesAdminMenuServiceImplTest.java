package fr.dila.reponses.ui.services.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.ui.bean.actions.STUserManagerActionsDTO;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.services.STUserManagerUIService;
import fr.dila.st.ui.services.impl.AbstractMenuServiceImpl;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ STUIServiceLocator.class, ReponsesAdminMenuServiceImpl.class })
@PowerMockIgnore("javax.management.*")
public class ReponsesAdminMenuServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    ReponsesAdminMenuServiceImpl service = new ReponsesAdminMenuServiceImpl();

    @Mock
    CoreSession session;

    @Mock
    STUserManagerUIService userManagerAction;

    @Mock
    SSPrincipal principal;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(STUIServiceLocator.class);
        Mockito.when(STUIServiceLocator.getSTUserManagerUIService()).thenReturn(userManagerAction);
        Mockito.when(session.getPrincipal()).thenReturn(principal);
    }

    @Test
    public void testGetData() {
        Mockito.when(userManagerAction.isCurrentUserPermanent(principal)).thenReturn(true);
        PowerMockito.suppress(PowerMockito.methods(AbstractMenuServiceImpl.class, "getData"));

        SpecificContext context = new SpecificContext();
        context.setSession(session);
        Map<String, Object> returnedMap = service.getData(context);

        // On vérifie que la map est null parce que la méthode super est supprimé
        assertNull(returnedMap);
        assertNotNull(context.getContextData().get("reponsesUserManagerActions"));

        STUserManagerActionsDTO userManagerDTO = (STUserManagerActionsDTO) context
            .getContextData()
            .get("reponsesUserManagerActions");
        assertTrue(userManagerDTO.getIsCurrentUserPermanent());

        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        STUIServiceLocator.getSTUserManagerUIService();
    }
}
