package fr.dila.reponses.ui.jaxrs.webobject.page;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import fr.dila.reponses.ui.services.actions.AllotissementActionService;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.reponses.ui.th.model.ReponsesLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ SpecificContext.class, WebEngine.class, ReponsesActionsServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class ReponsesAllotissementTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    CoreSession session;

    @Mock
    WebContext webcontext;

    @Mock
    AllotissementActionService allotissementActionService;

    @Mock
    SpecificContext specificContext;

    @Mock
    ThTemplate template;

    ReponsesAllotissement controlleur;

    @Before
    public void before() {
        PowerMockito.mockStatic(WebEngine.class);
        PowerMockito.mockStatic(ReponsesActionsServiceLocator.class);

        Mockito
            .when(ReponsesActionsServiceLocator.getAllotissementActionService())
            .thenReturn(allotissementActionService);
    }

    @Test
    public void testGetMyTemplate() {
        ReponsesAllotissement controlleur = new ReponsesAllotissement();
        ThTemplate template = controlleur.getMyTemplate(specificContext);
        assertThat(template).isNotNull().isInstanceOf(ReponsesLayoutThTemplate.class);
        assertEquals("pageLayout", template.getLayout());
        assertEquals("pages/allotissementListQuestions", template.getName());
        assertThat(template.getContext()).isEqualTo(specificContext);
    }
}
