package fr.dila.reponses.ui.jaxrs.webobject.page.dossier;

import static fr.dila.ss.ui.enums.SSActionCategory.DOSSIER_TAB_ACTIONS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.reponses.ui.services.files.ReponsesFondDeDossierUIService;
import fr.dila.ss.ui.bean.FondDTO;
import fr.dila.st.ui.bean.ColonneInfo;
import fr.dila.st.ui.bean.DossierDTO;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.DtoJsonHelper;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.dila.st.ui.utils.FileDownloadUtils;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
        DtoJsonHelper.class,
        ReponsesActionsServiceLocator.class,
        ReponsesUIServiceLocator.class,
        FileDownloadUtils.class
    }
)
@PowerMockIgnore("javax.management.*")
public class ReponsesDossierFondTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    private static final String DOSSIER_ID = "dossierId";

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private ReponsesDossierFond controlleur;

    @Mock
    private CoreSession session;

    @Mock
    private WebContext webcontext;

    @Mock
    private SpecificContext specificContext;

    @Mock
    private ReponsesFondDeDossierUIService fddActionService;

    @Mock
    private DocumentModel dossier;

    @Before
    public void before() {
        controlleur = new ReponsesDossierFond();

        PowerMockito.mockStatic(WebEngine.class);
        PowerMockito.mockStatic(DtoJsonHelper.class);

        PowerMockito.mockStatic(ReponsesActionsServiceLocator.class);

        PowerMockito.mockStatic(FileDownloadUtils.class);

        PowerMockito.mockStatic(ReponsesUIServiceLocator.class);
        when(ReponsesUIServiceLocator.getFondDeDossierUIService()).thenReturn(fddActionService);

        when(webcontext.getCoreSession()).thenReturn(session);
        when(WebEngine.getActiveContext()).thenReturn(webcontext);
        when(specificContext.getActions(DOSSIER_TAB_ACTIONS)).thenReturn(new ArrayList<>());

        when(specificContext.getCurrentDocument()).thenReturn(dossier);
        when(dossier.getId()).thenReturn(DOSSIER_ID);
    }

    @Test
    public void testGetFondDeDossier() {
        Whitebox.setInternalState(controlleur, "context", specificContext);

        FondDTO expectedFondDto = new FondDTO(Boolean.TRUE);

        List<DossierDTO> listD = new ArrayList<>();

        DossierDTO dossierFondDTO = new DossierDTO();
        dossierFondDTO.setNom("Divers");

        listD.add(dossierFondDTO);

        expectedFondDto.setDossiers(listD);

        when(fddActionService.getFondDTO(specificContext)).thenReturn(expectedFondDto);

        ThTemplate template = controlleur.getFondDeDossier();

        assertThat(template).isNotNull().isInstanceOf(AjaxLayoutThTemplate.class);
        assertEquals("ajaxLayout", template.getLayout());
        assertEquals("fragments/dossier/onglets/fond", template.getName());
        assertNotNull(template.getContext());

        // Données ajoutées à la map
        assertEquals(5, template.getData().size());
        @SuppressWarnings("unchecked")
        List<ColonneInfo> cols = (List<ColonneInfo>) template.getData().get(STTemplateConstants.LST_COLONNES);
        assertFalse(cols.isEmpty());
        assertEquals(5, cols.size());
        assertEquals("fondDossier.column.header.fichiers", cols.get(0).getLabel());

        FondDTO fondDto = (FondDTO) template.getData().get("fondDto");
        assertThat(fondDto).isEqualTo(expectedFondDto);
        assertThat(fondDto.getDossiers()).extracting(DossierDTO::getNom).containsExactly("Divers");
    }

    @Test
    public void testSaveFondDeDossier() {
        JsonResponse response = (JsonResponse) controlleur.saveFondDeDossier().getEntity();
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
        ThTemplate template = controlleur.getMyTemplate();
        assertNotNull(template);
        assertTrue(template instanceof AjaxLayoutThTemplate);
        assertEquals("ajaxLayout", template.getLayout());
        assertEquals("fragments/dossier/onglets/fond", template.getName());
        assertNotNull(template.getContext());
    }
}
