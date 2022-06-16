package fr.dila.reponses.ui.jaxrs.webobject.page.dossier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.reponses.ui.bean.BordereauDTO;
import fr.dila.reponses.ui.bean.DossierSaveForm;
import fr.dila.reponses.ui.services.BordereauUIService;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.actions.DossierActionService;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.ss.ui.enums.SSActionCategory;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.ArrayList;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
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
@PrepareForTest(
    {
        SpecificContext.class,
        WebEngine.class,
        ReponsesActionsServiceLocator.class,
        ReponsesUIServiceLocator.class,
        UserSessionHelper.class
    }
)
@PowerMockIgnore("javax.management.*")
public class ReponsesDossierBordereauTest {

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
    BordereauUIService service;

    @Mock
    private DossierActionService dossierActionService;

    @Mock
    SpecificContext specificContext;

    @Mock
    SolonAlertManager msgQueue;

    @Mock
    ThTemplate template;

    @Before
    public void before() {
        webcontext = mock(WebContext.class);
        session = mock(CoreSession.class);
        specificContext = mock(SpecificContext.class);
        template = mock(ThTemplate.class);
        service = mock(BordereauUIService.class);
        PowerMockito.mockStatic(WebEngine.class);
        PowerMockito.mockStatic(ReponsesUIServiceLocator.class);
        PowerMockito.mockStatic(ReponsesActionsServiceLocator.class);
        when(ReponsesActionsServiceLocator.getDossierActionService()).thenReturn(dossierActionService);

        PowerMockito.mockStatic(UserSessionHelper.class);

        Mockito.when(ReponsesUIServiceLocator.getBordereauUIService()).thenReturn(service);

        Mockito.when(webcontext.getCoreSession()).thenReturn(session);
        Mockito.when(WebEngine.getActiveContext()).thenReturn(webcontext);
        Mockito.when(specificContext.getActions(SSActionCategory.DOSSIER_TAB_ACTIONS)).thenReturn(new ArrayList<>());
        Mockito.when(specificContext.getMessageQueue()).thenReturn(msgQueue);
    }

    @Test
    public void testGetBordereau() {
        BordereauDTO expectedDto = new BordereauDTO();
        Mockito.when(service.getBordereau(Mockito.any())).thenReturn(expectedDto);

        ReponsesDossierBordereau controlleur = new ReponsesDossierBordereau();
        Whitebox.setInternalState(controlleur, "context", specificContext);
        ThTemplate template = controlleur.getBordereau();
        assertNotNull(template);
        assertTrue(template instanceof AjaxLayoutThTemplate);
        assertEquals("ajaxLayout", template.getLayout());
        assertEquals("fragments/dossier/onglets/bordereau", template.getName());
        assertNotNull(template.getContext());

        //Données ajoutées à la map
        assertEquals(2, template.getData().size());
        assertEquals(expectedDto, template.getData().get("bordereauDto"));
    }

    @Test
    public void testSaveBordereau() throws Exception {
        ReponsesDossierBordereau controlleur = new ReponsesDossierBordereau();
        Whitebox.setInternalState(controlleur, "context", specificContext);
        DossierSaveForm data = new DossierSaveForm();

        //Appel de la méthode
        JsonResponse response = (JsonResponse) ((Response) controlleur.saveBordereau(data)).getEntity();

        //Vérification MAj du context
        verify(dossierActionService).saveIndexationComplementaire(specificContext, data);
        assertNotNull(response);
        assertEquals(SolonStatus.OK, response.getStatut());
        assertNull(response.getData());
        assertTrue(response.getMessages().getInfoMessageQueue().isEmpty());
        assertTrue(response.getMessages().getWarningMessageQueue().isEmpty());
        assertTrue(response.getMessages().getDangerMessageQueue().isEmpty());
        assertTrue(response.getMessages().getSuccessMessageQueue().isEmpty());

        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        UserSessionHelper.putUserSessionParameter(
            specificContext,
            SpecificContext.MESSAGE_QUEUE,
            specificContext.getMessageQueue()
        );
    }

    @Test
    public void testGetMyTemplate() {
        ReponsesDossierBordereau controlleur = new ReponsesDossierBordereau();
        ThTemplate template = controlleur.getMyTemplate();
        assertNotNull(template);
        assertTrue(template instanceof AjaxLayoutThTemplate);
        assertEquals("ajaxLayout", template.getLayout());
        assertEquals("fragments/dossier/onglets/bordereau", template.getName());
        assertNotNull(template.getContext());
    }
}
