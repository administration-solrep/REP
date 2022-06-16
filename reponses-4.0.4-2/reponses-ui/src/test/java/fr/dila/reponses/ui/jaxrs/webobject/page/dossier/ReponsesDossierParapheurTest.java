package fr.dila.reponses.ui.jaxrs.webobject.page.dossier;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.reponses.ui.bean.ConsultDossierDTO;
import fr.dila.reponses.ui.bean.ParapheurDTO;
import fr.dila.reponses.ui.services.ParapheurUIService;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.actions.ParapheurActionService;
import fr.dila.reponses.ui.services.actions.ReponseActionService;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.ss.ui.enums.SSActionCategory;
import fr.dila.ss.ui.services.SSDossierUIService;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.services.actions.DossierLockActionService;
import fr.dila.st.ui.services.actions.STActionsServiceLocator;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
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
        ReponsesUIServiceLocator.class,
        STActionsServiceLocator.class,
        ReponsesActionsServiceLocator.class,
        UserSessionHelper.class
    }
)
@PowerMockIgnore("javax.management.*")
public class ReponsesDossierParapheurTest {

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
    ParapheurUIService service;

    @Mock
    DossierLockActionService lockActionService;

    @Mock
    ParapheurActionService parapheurActionService;

    @Mock
    SpecificContext specificContext;

    @Mock
    ThTemplate template;

    @Mock
    DocumentModel dossierDoc;

    @Mock
    SSDossierUIService<ConsultDossierDTO> dossierUIService;

    @Mock
    ReponseActionService reponseActionService;

    @Before
    public void before() {
        PowerMockito.mockStatic(WebEngine.class);
        PowerMockito.mockStatic(ReponsesUIServiceLocator.class);
        PowerMockito.mockStatic(ReponsesActionsServiceLocator.class);
        PowerMockito.mockStatic(STActionsServiceLocator.class);

        when(ReponsesUIServiceLocator.getParapheurUIService()).thenReturn(service);
        when(STActionsServiceLocator.getDossierLockActionService()).thenReturn(lockActionService);
        when(ReponsesActionsServiceLocator.getParapheurActionService()).thenReturn(parapheurActionService);
        when(ReponsesActionsServiceLocator.getReponseActionService()).thenReturn(reponseActionService);
        when(ReponsesUIServiceLocator.getDossierUIService()).thenReturn(dossierUIService);

        when(webcontext.getCoreSession()).thenReturn(session);
        when(WebEngine.getActiveContext()).thenReturn(webcontext);
        when(specificContext.getActions(SSActionCategory.DOSSIER_TAB_ACTIONS)).thenReturn(new ArrayList<>());
        when(specificContext.getMessageQueue()).thenReturn(mock(SolonAlertManager.class));

        PowerMockito.mockStatic(UserSessionHelper.class);
    }

    @Test
    public void testGetParapheur() {
        ParapheurDTO expectedDto = new ParapheurDTO();
        expectedDto.setTexteQuestion("this is the question");
        when(service.getParapheur(specificContext)).thenReturn(expectedDto);

        List<SelectValueDTO> versions = asList(
            new SelectValueDTO("idVersion2", "label2"),
            new SelectValueDTO("idVersion3", "label3"),
            new SelectValueDTO("idVersionEnCours", "labelEnCours")
        );
        when(service.getVersionDTOs(specificContext)).thenReturn(versions);
        when(dossierUIService.getDossierConsult(specificContext)).thenReturn(new ConsultDossierDTO());
        when(reponseActionService.canUserBriserReponse(specificContext)).thenReturn(true);

        ReponsesDossierParapheur controlleur = new ReponsesDossierParapheur();
        Whitebox.setInternalState(controlleur, "context", specificContext);
        ThTemplate template = controlleur.getParapheur();
        assertNotNull(template);
        assertTrue(template instanceof AjaxLayoutThTemplate);
        assertEquals("ajaxLayout", template.getLayout());
        assertEquals("fragments/dossier/onglets/parapheur", template.getName());
        assertNotNull(template.getContext());

        //Données ajoutées à la map
        assertEquals(9, template.getData().size());
        assertEquals(expectedDto, template.getData().get("parapheurDto"));
        assertNotNull(template.getData().get("tabActions"));
        // test formats (html/text/xml)
        assertNotNull(template.getData().get("formatListValues"));
        @SuppressWarnings("unchecked")
        List<SelectValueDTO> list = (List<SelectValueDTO>) template.getData().get("formatListValues");
        assertEquals(3, list.size());
        assertEquals("html", list.get(0).getId());
        assertEquals("text", list.get(1).getId());
        assertEquals("xml", list.get(2).getId());

        Object versionDTOs = template.getData().get("versions");
        assertNotNull(versionDTOs);

        assertEquals(3, versions.size());
        assertEquals("idVersion2", versions.get(0).getId());
        assertEquals("idVersion3", versions.get(1).getId());
        assertEquals("idVersionEnCours", versions.get(2).getId());

        Object format = template.getData().get("format");
        assertNotNull(format);
        assertEquals("html", format.toString());
    }

    @Test
    public void testSaveParapheur() {
        ReponsesDossierParapheur controlleur = new ReponsesDossierParapheur();
        Whitebox.setInternalState(controlleur, "context", specificContext);
        ParapheurDTO dto = new ParapheurDTO();

        //Appel de la méthode
        JsonResponse response = (JsonResponse) ((Response) controlleur.saveParapheur(dto)).getEntity();

        //Vérification MAj du context
        verify(parapheurActionService).saveDossier(specificContext, dto);
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
        ReponsesDossierParapheur controlleur = new ReponsesDossierParapheur();
        ThTemplate template = controlleur.getMyTemplate();
        assertNotNull(template);
        assertTrue(template instanceof AjaxLayoutThTemplate);
        assertEquals("ajaxLayout", template.getLayout());
        assertEquals("fragments/dossier/onglets/parapheur", template.getName());
        assertNotNull(template.getContext());
    }
}
