package fr.dila.reponses.ui.jaxrs.webobject.page.dossier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import fr.dila.reponses.ui.services.actions.DossierConnexeActionService;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.ss.ui.enums.SSActionCategory;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ SpecificContext.class, ReponsesActionsServiceLocator.class, WebEngine.class })
@PowerMockIgnore("javax.management.*")
public class ReponsesDossierConnexeTest {

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
    SpecificContext specificContext;

    @Mock
    ThTemplate template;

    @Mock
    DossierConnexeActionService dossierConnexeActionService;

    @Before
    public void before() {
        webcontext = mock(WebContext.class);
        session = mock(CoreSession.class);
        specificContext = mock(SpecificContext.class);
        template = mock(ThTemplate.class);
        PowerMockito.mockStatic(WebEngine.class);
        PowerMockito.mockStatic(ReponsesActionsServiceLocator.class);

        when(ReponsesActionsServiceLocator.getDossierConnexeActionService()).thenReturn(dossierConnexeActionService);

        when(webcontext.getCoreSession()).thenReturn(session);
        when(WebEngine.getActiveContext()).thenReturn(webcontext);
        when(specificContext.getActions(SSActionCategory.DOSSIER_TAB_ACTIONS)).thenReturn(new ArrayList<>());
    }

    @Test
    public void testGetListeQuestionsConnexes() {
        ReponsesDossierConnexe controlleur = new ReponsesDossierConnexe();
        Whitebox.setInternalState(controlleur, "context", specificContext);
        ThTemplate template = controlleur.getListeQuestionsConnexes("1");
        assertNotNull(template);
        assertTrue(template instanceof AjaxLayoutThTemplate);
        assertEquals("ajaxLayout", template.getLayout());
        assertEquals("fragments/dossier/onglets/connexe/listeQuestionConnexeMinistere", template.getName());
        assertNotNull(template.getContext());

        //Données ajoutées à la map
        assertEquals(2, template.getData().size());
    }

    @Test
    public void testSaveDossierConnexe() {
        ReponsesDossierConnexe controlleur = new ReponsesDossierConnexe();
        JsonResponse response = (JsonResponse) controlleur.saveDossierConnexe().getEntity();
        assertNotNull(response);
        assertEquals(SolonStatus.OK, response.getStatut());
        assertNull(response.getData());
        assertTrue(response.getMessages().getInfoMessageQueue().isEmpty());
        assertTrue(response.getMessages().getWarningMessageQueue().isEmpty());
        assertTrue(response.getMessages().getDangerMessageQueue().isEmpty());
        assertTrue(response.getMessages().getSuccessMessageQueue().isEmpty());
    }

    @Test
    public void testGetMyTemplate() {
        ReponsesDossierConnexe controlleur = new ReponsesDossierConnexe();
        ThTemplate template = controlleur.getMyTemplate();
        assertNotNull(template);
        assertTrue(template instanceof AjaxLayoutThTemplate);
        assertEquals("ajaxLayout", template.getLayout());
        assertEquals("fragments/dossier/onglets/connexe/listeQuestionConnexeMinistere", template.getName());
        assertNotNull(template.getContext());
    }
}
