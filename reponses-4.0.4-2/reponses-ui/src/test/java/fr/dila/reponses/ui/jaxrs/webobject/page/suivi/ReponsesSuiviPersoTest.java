package fr.dila.reponses.ui.jaxrs.webobject.page.suivi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import fr.dila.reponses.ui.bean.RequetePersoForm;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.RequeteUIService;
import fr.dila.ss.ui.bean.RequeteExperteDTO;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.th.model.ThTemplate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ ReponsesSuiviPerso.class, UserSessionHelper.class, ReponsesUIServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class ReponsesSuiviPersoTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    ReponsesSuiviPerso controller;

    @Mock
    RequeteUIService requeteUIService;

    @Before
    public void setUp() {
        controller = new ReponsesSuiviPerso();
        PowerMockito.mockStatic(UserSessionHelper.class);
        PowerMockito.mockStatic(ReponsesUIServiceLocator.class);
        when(UserSessionHelper.getUserSessionParameter(any(), any(), any())).thenReturn(new RequeteExperteDTO());
        when(ReponsesUIServiceLocator.getRequeteUIService()).thenReturn(requeteUIService);
    }

    @Test
    public void testGetRequetePersoCreation() {
        RequetePersoForm form = new RequetePersoForm();
        form.setRequete("requete");

        when(requeteUIService.getRequetePersoForm(any())).thenReturn(form);

        ThTemplate result = controller.getRequetePersoCreation();

        RequetePersoForm resultForm = (RequetePersoForm) result.getData().get("form");
        assertNotNull(resultForm);
        assertEquals("requete", resultForm.getRequete());
    }
}
