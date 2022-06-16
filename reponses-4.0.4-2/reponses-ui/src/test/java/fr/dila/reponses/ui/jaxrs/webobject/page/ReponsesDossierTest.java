package fr.dila.reponses.ui.jaxrs.webobject.page;

import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.reponses.ui.bean.ConsultDossierDTO;
import fr.dila.reponses.ui.bean.QuestionHeaderDTO;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.th.model.ReponsesLayoutThTemplate;
import fr.dila.ss.api.tree.SSTreeFile;
import fr.dila.ss.ui.bean.SSConsultDossierDTO;
import fr.dila.ss.ui.services.SSDossierDistributionUIService;
import fr.dila.ss.ui.services.SSDossierUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.ss.ui.services.actions.SSCorbeilleActionService;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.ui.bean.Onglet;
import fr.dila.st.ui.bean.OngletConteneur;
import fr.dila.st.ui.enums.STActionCategory;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.webengine.model.Resource;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.session.UserSession;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(
    { SpecificContext.class, ReponsesUIServiceLocator.class, SSUIServiceLocator.class, SSActionsServiceLocator.class }
)
@PowerMockIgnore("javax.management.*")
public class ReponsesDossierTest {
    private static final String DOSSIER_ID = "idDossier";
    private static final String DOSSIER_LINK_ID = "monDossierLink";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private ReponsesDossier controlleur;

    @Mock
    private CoreSession session;

    @Mock
    private WebContext webContext;

    @Mock
    private SpecificContext specificContext;

    @Mock
    private SSDossierUIService<ConsultDossierDTO> dossierService;

    @Mock
    private SSDossierUIService<SSConsultDossierDTO> ssDossierUIService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private SSDossierDistributionUIService distributionActionService;

    @Mock
    private UserSession userSession;

    @Mock
    private Resource resource;

    @Mock
    private QuestionHeaderDTO questionInfo;

    @Mock
    private ConsultDossierDTO dossierDto;

    @Mock
    SSCorbeilleActionService corbeilleActionService;

    @Mock
    private DocumentModel dossier;

    @Mock
    private STDossierLink dossierLink;

    @Captor
    private ArgumentCaptor<ReponsesLayoutThTemplate> templateCaptor;

    @Before
    public void before() {
        controlleur = new ReponsesDossier();

        Whitebox.setInternalState(controlleur, "context", specificContext);

        PowerMockito.mockStatic(ReponsesUIServiceLocator.class);
        when(ReponsesUIServiceLocator.getDossierUIService()).thenReturn(dossierService);

        PowerMockito.mockStatic(SSUIServiceLocator.class);
        when(SSUIServiceLocator.getSSDossierDistributionUIService()).thenReturn(distributionActionService);
        when(SSUIServiceLocator.getSSDossierUIService()).thenReturn(ssDossierUIService);

        PowerMockito.mockStatic(SSActionsServiceLocator.class);
        when(SSActionsServiceLocator.getSSCorbeilleActionService()).thenReturn(corbeilleActionService);

        when(webContext.getCoreSession()).thenReturn(session);
        when(dossierDto.getId()).thenReturn(DOSSIER_ID);
        when(dossierDto.getQuestionInfo()).thenReturn(questionInfo);
        when(questionInfo.getTypeQuestion()).thenReturn("AN");
        when(questionInfo.getNumeroQuestion()).thenReturn(10L);
        when(webContext.getRequest()).thenReturn(request);
        when(request.getQueryString()).thenReturn("");
        when(specificContext.getSession()).thenReturn(session);

        when(dossier.getId()).thenReturn(DOSSIER_ID);
    }

    @Test
    public void testGetDossier() throws Exception {
        Whitebox.setInternalState(controlleur, "ctx", webContext);

        List<Action> onglets = new ArrayList<>();

        Action action1 = new Action("onglet1", null);
        Map<String, Serializable> properties = new HashMap<>();
        properties.put("name", "onglet1");
        properties.put("fragmentFile", "fragment1");
        properties.put("fragmentName", "fragmentName");
        properties.put("script", "monScript1");
        action1.setProperties(properties);
        onglets.add(action1);

        Action actionCurrent = new Action("ongletCurrent", null);
        properties = new HashMap<>();
        properties.put("name", "ongletCurrent");
        properties.put("fragmentFile", "fragmentCurrent");
        properties.put("fragmentName", "nameCurrent");
        properties.put("script", "monScriptCurrent");
        actionCurrent.setProperties(properties);
        onglets.add(actionCurrent);

        when(specificContext.getActions(STActionCategory.VIEW_ACTION_LIST)).thenReturn(onglets);
        when(specificContext.getWebcontext()).thenReturn(webContext);
        when(webContext.getUserSession()).thenReturn(userSession);
        when(webContext.getBaseURL()).thenReturn("http://localhost:8180");
        when(webContext.head()).thenReturn(resource);
        when(resource.getPath()).thenReturn("/reponses/site/app-ui");
        when(userSession.get(SpecificContext.LAST_TEMPLATE)).thenReturn(ThTemplate.class);
        when(session.getDocument(new IdRef(DOSSIER_ID))).thenReturn(dossier);
        when(corbeilleActionService.getCurrentDossierLink(specificContext)).thenReturn(dossierLink);
        when(dossierLink.getId()).thenReturn(DOSSIER_LINK_ID);
        when(
            webContext.newObject(
                eq("AppliDossierOngletCurrent"),
                eq(specificContext),
                any(ReponsesLayoutThTemplate.class)
            )
        )
            .thenReturn(resource);
        when(ssDossierUIService.getDossierConsult(specificContext)).thenReturn(dossierDto);

        //Appel de la méthode
        controlleur.getDossier(DOSSIER_ID, "ongletCurrent", DOSSIER_LINK_ID, null);

        //Vérification du context
        // verify(template).setContext(specificContext);

        //Vérification MAj du context
        verify(specificContext).putInContextData(STContextDataKey.CURRENT_TAB, "ongletCurrent");
        verify(specificContext).setCurrentDocument(DOSSIER_ID);
        verify(specificContext).putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, DOSSIER_LINK_ID);

        verify(webContext).newObject(anyString(), templateCaptor.capture());

        ReponsesLayoutThTemplate template = templateCaptor.getValue();

        //Vérification de MAJ de la MAP du template
        assertThat(template.getName()).isEqualTo("pages/dossier/consult");
        assertThat(template.getContext()).isEqualTo(specificContext);
        assertThat(template.getData()).hasSize(5);
        assertThat(template.getData()).containsKeys("monDossier", "myTabs");

        //Vérification du dossier
        ConsultDossierDTO dossierDto = (ConsultDossierDTO) template.getData().get("monDossier");
        assertThat(dossierDto.getId()).isEqualTo(DOSSIER_ID);

        //Onglet standard
        Onglet ongletStandard = new Onglet();
        ongletStandard.setId("onglet1");
        ongletStandard.setIsActif(false);
        ongletStandard.setScript("monScript1");

        //Onglet actif
        Onglet ongletActif = new Onglet();
        ongletActif.setId("ongletCurrent");
        ongletActif.setIsActif(true);
        ongletActif.setFragmentFile("fragmentCurrent");
        ongletActif.setFragmentName("nameCurrent");
        ongletActif.setScript("monScriptCurrent");

        //Vérification des onglets
        OngletConteneur conteneur = (OngletConteneur) template.getData().get("myTabs");
        assertThat(conteneur.getOnglets())
            .usingElementComparatorOnFields("id", "isActif", "fragmentFile", "fragmentName", "script")
            .containsExactly(ongletStandard, ongletActif);

        //Vérification appel mise à jour statut de lecture
        verify(distributionActionService).changeReadStateDossierLink(specificContext);
        verify(dossierService).loadDossierActions(specificContext, template);
        verify(corbeilleActionService).getCurrentDossierLink(specificContext);
    }

    @Test
    public void testDownloadFile() {
        String fileId = "testId";
        String mimeType = "application/pdf";
        String filename = "testFilename";

        File file = mock(File.class);
        when(file.isFile()).thenReturn(true);

        Blob blob = mock(Blob.class);
        when(blob.getFile()).thenReturn(file);

        SSTreeFile ssTreeFile = mock(SSTreeFile.class);
        when(ssTreeFile.getContent()).thenReturn(blob);
        when(ssTreeFile.getFileMimeType()).thenReturn(mimeType);
        when(ssTreeFile.getFilename()).thenReturn(filename);

        when(session.getDocument(new IdRef(fileId))).thenReturn(dossier);
        when(dossier.getAdapter(any())).thenReturn(ssTreeFile);

        Response response = controlleur.downloadFile(fileId);

        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
        assertThat(response.getMetadata().getFirst("Content-Type")).hasToString(mimeType);
        assertThat(response.getMetadata().getFirst("Content-Disposition"))
            .hasToString(String.format("attachment; filename=\"%s\"", filename));
    }
}
