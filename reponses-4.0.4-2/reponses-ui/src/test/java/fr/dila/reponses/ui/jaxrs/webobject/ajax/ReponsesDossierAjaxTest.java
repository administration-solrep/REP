package fr.dila.reponses.ui.jaxrs.webobject.ajax;

import static com.google.common.collect.Lists.newArrayList;
import static fr.dila.reponses.api.enumeration.IndexationTypeEnum.AN_RUBRIQUE;
import static fr.dila.st.ui.enums.STContextDataKey.FILE_CONTENT;
import static fr.dila.st.ui.enums.STContextDataKey.FILE_DETAILS;
import static fr.dila.st.ui.enums.STContextDataKey.ID;
import static fr.dila.st.ui.th.model.SpecificContext.MESSAGE_QUEUE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.BodyPartEntity;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.service.ReponseService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.ui.bean.CompareVersionDTO;
import fr.dila.reponses.ui.bean.ConsultDossierDTO;
import fr.dila.reponses.ui.bean.ParapheurDTO;
import fr.dila.reponses.ui.bean.VocSugUI;
import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.services.ParapheurUIService;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.actions.ComparateurActionService;
import fr.dila.reponses.ui.services.actions.IndexActionService;
import fr.dila.reponses.ui.services.actions.ReponseActionService;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.reponses.ui.services.actions.ReponsesDossierDistributionActionService;
import fr.dila.reponses.ui.services.files.ReponsesFondDeDossierUIService;
import fr.dila.ss.api.service.SSProfilUtilisateurService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.ui.bean.SSConsultDossierDTO;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.SSDossierDistributionUIService;
import fr.dila.ss.ui.services.SSDossierUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.ss.ui.services.actions.SSCorbeilleActionService;
import fr.dila.st.ui.assertions.ResponseAssertions;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.enums.STActionCategory;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.services.actions.DossierLockActionService;
import fr.dila.st.ui.services.actions.STActionsServiceLocator;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.io.InputStream;
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
import org.mockito.Spy;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.actions.Action;
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
        ReponsesDossierAjax.class,
        ReponsesServiceLocator.class,
        SpecificContext.class,
        STActionsServiceLocator.class,
        ReponsesActionsServiceLocator.class,
        WebEngine.class,
        UserSessionHelper.class,
        ReponsesUIServiceLocator.class,
        SSServiceLocator.class,
        SSUIServiceLocator.class,
        SSActionsServiceLocator.class
    }
)
@PowerMockIgnore("javax.management.*")
public class ReponsesDossierAjaxTest {
    private static final String DOSSIER_ID_VALUE = "dossier-id-value";
    private static final String DOSSIER_LINK_ID_VALUE = "dossier-link-id-value";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Spy
    private ReponsesDossierAjax controlleur = new ReponsesDossierAjax();

    @Mock
    private CoreSession session;

    @Mock
    private WebContext webcontext;

    @Mock
    private SpecificContext specificContext;

    @Mock
    private DocumentModel document;

    @Mock
    private VocSugUI rubriqueANVocSug;

    @Mock
    private DossierLockActionService lockActionService;

    @Mock
    private IndexActionService indexActionService;

    @Mock
    private ReponsesDossierDistributionActionService distributionActionService;

    @Mock
    private SSDossierDistributionUIService dossierDistributionUIService;

    @Mock
    private ParapheurUIService service;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Map<String, Object> contextData;

    @Mock
    private InputStream uploadedInputStream;

    @Mock
    private FormDataContentDisposition fileDetail;

    @Mock
    private ComparateurActionService comparateurActionService;

    @Mock
    private SSDossierUIService<ConsultDossierDTO> dossierUIService;

    @Mock
    private SSDossierUIService<SSConsultDossierDTO> ssDossierUIService;

    @Mock
    private ReponsesFondDeDossierUIService fondDeDossierUIService;

    @Mock
    private ReponseService reponsesService;

    @Mock
    private ReponseActionService reponseActionService;

    @Mock
    private SSProfilUtilisateurService profilUtilisateurService;

    @Mock
    private SSCorbeilleActionService corbeilleActionService;

    @Mock
    private SSConsultDossierDTO consultDossierDTO;

    @Captor
    private ArgumentCaptor<Object> captor;

    @Before
    public void before() {
        // Fake document
        when(document.getId()).thenReturn("identifiantDocument");
        when(session.getDocument(any())).thenReturn(document);
        when(specificContext.getSession()).thenReturn(session);
        when(specificContext.getMessageQueue()).thenReturn(new SolonAlertManager());
        when(specificContext.getContextData()).thenReturn(contextData);
        doReturn(dossierUIService).when(controlleur).getDossierUIService();

        mockStatic(STActionsServiceLocator.class);
        when(STActionsServiceLocator.getDossierLockActionService()).thenReturn(lockActionService);

        mockStatic(ReponsesActionsServiceLocator.class);
        when(ReponsesActionsServiceLocator.getIndexActionService()).thenReturn(indexActionService);
        when(ReponsesActionsServiceLocator.getReponsesDossierDistributionActionService())
            .thenReturn(distributionActionService);
        when(ReponsesActionsServiceLocator.getComparateurActionService()).thenReturn(comparateurActionService);
        when(ReponsesActionsServiceLocator.getReponseActionService()).thenReturn(reponseActionService);

        mockStatic(WebEngine.class);
        when(WebEngine.getActiveContext()).thenReturn(webcontext);

        mockStatic(UserSessionHelper.class);

        mockStatic(ReponsesUIServiceLocator.class);
        when(ReponsesUIServiceLocator.getParapheurUIService()).thenReturn(service);
        when(ReponsesUIServiceLocator.getFondDeDossierUIService()).thenReturn(fondDeDossierUIService);

        mockStatic(ReponsesServiceLocator.class);
        when(ReponsesServiceLocator.getReponseService()).thenReturn(reponsesService);

        when(webcontext.getRequest()).thenReturn(request);
        when(request.getQueryString()).thenReturn("");

        mockStatic(SSServiceLocator.class);
        when(SSServiceLocator.getSsProfilUtilisateurService()).thenReturn(profilUtilisateurService);

        mockStatic(SSUIServiceLocator.class);
        when(SSUIServiceLocator.getSSDossierDistributionUIService()).thenReturn(dossierDistributionUIService);
        when(SSUIServiceLocator.getSSDossierUIService()).thenReturn(ssDossierUIService);

        mockStatic(SSActionsServiceLocator.class);
        when(SSActionsServiceLocator.getSSCorbeilleActionService()).thenReturn(corbeilleActionService);
        when(corbeilleActionService.getCurrentDossierLink(specificContext)).thenReturn(null);

        Whitebox.setInternalState(controlleur, "context", specificContext);
        Whitebox.setInternalState(controlleur, "ctx", webcontext);
    }

    @Test
    public void testGetDossier() {
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

        when(ssDossierUIService.getDossierConsult(specificContext)).thenReturn(consultDossierDTO);

        controlleur.getDossier(DOSSIER_ID_VALUE, "ongletCurrent", DOSSIER_LINK_ID_VALUE, false);

        verify(specificContext).setCurrentDocument(DOSSIER_ID_VALUE);
        verify(specificContext).putInContextData(STContextDataKey.CURRENT_TAB, "ongletCurrent");
        verify(specificContext).putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, DOSSIER_LINK_ID_VALUE);
        verify(webcontext).newObject(eq("AppliDossierOngletCurrent"), captor.capture());

        List<Object> args = captor.getAllValues();
        assertThat((SpecificContext) args.get(0)).isEqualTo(specificContext);
        AjaxLayoutThTemplate template = (AjaxLayoutThTemplate) args.get(1);
        assertThat(template.getName()).isEqualTo("pages/dossier/consult-dossier-content");
        assertThat(template.getContext()).isEqualTo(specificContext);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetDossierByVersion() {
        ParapheurDTO expectedDto = new ParapheurDTO();
        when(service.getParapheur(specificContext)).thenReturn(expectedDto);

        List<SelectValueDTO> versions = asList(
            new SelectValueDTO("idVersion2", "label2"),
            new SelectValueDTO("idVersion3", "label3"),
            new SelectValueDTO("idVersionEnCours", "labelEnCours")
        );
        when(service.getVersionDTOs(specificContext)).thenReturn(versions);

        DocumentModel reponseVersionDoc = mock(DocumentModel.class);
        when(reponseVersionDoc.isVersion()).thenReturn(TRUE);
        when(specificContext.getSession().getDocument(new IdRef("idVersion"))).thenReturn(reponseVersionDoc);
        Reponse reponseVersion = mock(Reponse.class);
        when(reponseVersionDoc.getAdapter(Reponse.class)).thenReturn(reponseVersion);
        when(reponseVersion.getTexteReponse()).thenReturn("texte Version");

        ThTemplate template = controlleur.getDossierByVersion("monid", "monDossierLink", "idVersion", "xml");
        assertNotNull(template);
        assertTrue(template instanceof AjaxLayoutThTemplate);
        assertEquals("ajaxLayout", template.getLayout());
        assertEquals("fragments/dossier/onglets/parapheur", template.getName());
        assertNotNull(template.getContext());

        // Données ajoutées à la map
        assertEquals(6, template.getData().size());
        ParapheurDTO pDto = (ParapheurDTO) template.getData().get("parapheurDto");
        assertEquals(expectedDto, pDto);
        assertFalse(pDto.getIsEdit());
        assertEquals(pDto.getReponseNote(), "texte Version");
        assertNotNull(template.getData().get("tabActions"));
        assertNotNull(template.getData().get("formatListValues"));
        // test formats (html/text/xml)
        List<SelectValueDTO> formats = (List<SelectValueDTO>) template.getData().get("formatListValues");
        assertEquals(3, formats.size());
        assertEquals("html", formats.get(0).getId());
        assertEquals("text", formats.get(1).getId());
        assertEquals("xml", formats.get(2).getId());

        assertNotNull(template.getData().get("versions"));
        List<SelectValueDTO> versionDTOs = (List<SelectValueDTO>) template.getData().get("versions");
        assertEquals(3, versionDTOs.size());
        assertEquals("idVersion2", versionDTOs.get(0).getId());
        assertEquals("idVersion3", versionDTOs.get(1).getId());
        assertEquals("idVersionEnCours", versionDTOs.get(2).getId());

        Object format = template.getData().get("format");
        assertNotNull(format);
        assertEquals("xml", format.toString());
    }

    @Test
    public void testVerrouille() throws Exception {
        PowerMockito.whenNew(SpecificContext.class).withNoArguments().thenReturn(specificContext);

        // Appel de la méthode
        Response response = controlleur.verouilleDossier(DOSSIER_ID_VALUE);

        // Vérification MAj du context
        ResponseAssertions.assertResponseWithoutMessages(response);

        verify(specificContext).setCurrentDocument(DOSSIER_ID_VALUE);
        verify(lockActionService).lockCurrentDossier(specificContext);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(specificContext, MESSAGE_QUEUE, specificContext.getMessageQueue());
    }

    @Test
    public void testDeverrouille() throws Exception {
        PowerMockito.whenNew(SpecificContext.class).withNoArguments().thenReturn(specificContext);

        // Appel de la méthode
        Response response = controlleur.deverouilleDossier(DOSSIER_ID_VALUE);

        // Vérification MAj du context
        ResponseAssertions.assertResponseWithoutMessages(response);

        verify(specificContext).setCurrentDocument(DOSSIER_ID_VALUE);
        verify(lockActionService).unlockCurrentDossier(specificContext);

        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        UserSessionHelper.putUserSessionParameter(specificContext, MESSAGE_QUEUE, specificContext.getMessageQueue());
    }

    @Test
    public void testGetIndexationSuggestions() throws Exception {
        String input = "min";

        Map<String, VocSugUI> vocabularies = new HashMap<>(1);
        vocabularies.put(AN_RUBRIQUE.getValue(), rubriqueANVocSug);
        when(indexActionService.getVocMap()).thenReturn(vocabularies);

        when(rubriqueANVocSug.getSuggestions(input))
            .thenReturn(newArrayList("mines et carrières", "ministères et secrétariats d'État"));

        PowerMockito.whenNew(SpecificContext.class).withNoArguments().thenReturn(specificContext);

        String result = controlleur.getIndexationSuggestions(AN_RUBRIQUE.getValue(), input);

        assertThat(result).isEqualTo("[\"mines et carrières\",\"ministères et secrétariats d'État\"]");
    }

    @Test
    public void testSaveDossier() {
        controlleur.saveDossier(DOSSIER_ID_VALUE, DOSSIER_LINK_ID_VALUE, "bordereau");

        verify(specificContext).setCurrentDocument(DOSSIER_ID_VALUE);
        verify(specificContext).putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, DOSSIER_LINK_ID_VALUE);
        verify(webcontext).newObject("AppliDossierBordereau", specificContext);
    }

    @Test
    public void testUnread() throws Exception {
        PowerMockito.whenNew(SpecificContext.class).withNoArguments().thenReturn(specificContext);

        // Appel de la méthode
        String returnStr = controlleur.unreadDossier(DOSSIER_ID_VALUE, DOSSIER_LINK_ID_VALUE);

        // Vérification MAj du context
        verify(specificContext).setCurrentDocument(DOSSIER_ID_VALUE);
        verify(specificContext).putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, DOSSIER_LINK_ID_VALUE);
        verify(specificContext).putInContextData(SSContextDataKey.DOSSIER_IS_READ, false);
        verify(dossierDistributionUIService).changeReadStateDossierLink(specificContext);
        verify(lockActionService).unlockCurrentDossier(specificContext);
        assertEquals("result : OK", returnStr);
    }

    @Test
    public void testDoEtapeSuivante() {
        // When
        Response response = controlleur.etapeSuivante(DOSSIER_ID_VALUE, DOSSIER_LINK_ID_VALUE);

        // Then
        ResponseAssertions.assertResponseWithoutMessages(response);

        verify(specificContext).setCurrentDocument(DOSSIER_ID_VALUE);
        verify(specificContext).putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, DOSSIER_LINK_ID_VALUE);
        verify(distributionActionService).donnerAvisFavorable(specificContext);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(specificContext, MESSAGE_QUEUE, specificContext.getMessageQueue());
    }

    @Test
    public void testDoMettreEnAttente() {
        Response response = controlleur.mettreEnAttente(DOSSIER_ID_VALUE, DOSSIER_LINK_ID_VALUE);

        ResponseAssertions.assertResponseWithoutMessages(response);
        verify(distributionActionService).mettreEnAttente(specificContext);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(specificContext, MESSAGE_QUEUE, specificContext.getMessageQueue());
    }

    @Test
    public void testDoSigneEtapeSuivante() {
        // When
        Response response = controlleur.signeEtapeSuivante(DOSSIER_ID_VALUE, DOSSIER_LINK_ID_VALUE);

        // Then
        ResponseAssertions.assertResponseWithoutMessages(response);

        verify(specificContext).setCurrentDocument(DOSSIER_ID_VALUE);
        verify(specificContext).putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, DOSSIER_LINK_ID_VALUE);
        verify(distributionActionService).donnerAvisFavorable(specificContext);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(specificContext, MESSAGE_QUEUE, specificContext.getMessageQueue());
    }

    @Test
    public void testDoRefusDeSignature() {
        Response response = controlleur.refusDeSignature(DOSSIER_ID_VALUE, DOSSIER_LINK_ID_VALUE);

        ResponseAssertions.assertResponseWithoutMessages(response);

        verify(specificContext).setCurrentDocument(DOSSIER_ID_VALUE);
        verify(specificContext).putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, DOSSIER_LINK_ID_VALUE);
        verify(distributionActionService).donnerAvisDefavorableEtPoursuivre(specificContext);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(specificContext, MESSAGE_QUEUE, specificContext.getMessageQueue());
    }

    @Test
    public void testDoRejetDossierRetour() {
        Response response = controlleur.rejetDossierRetour(DOSSIER_ID_VALUE, DOSSIER_LINK_ID_VALUE);

        ResponseAssertions.assertResponseWithoutMessages(response);

        verify(specificContext).setCurrentDocument(DOSSIER_ID_VALUE);
        verify(specificContext).putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, DOSSIER_LINK_ID_VALUE);
        verify(distributionActionService).donnerAvisDefavorableEtInsererTaches(specificContext);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(specificContext, MESSAGE_QUEUE, specificContext.getMessageQueue());
    }

    @Test
    public void testDoArbitrageSgg() {
        Response response = controlleur.doArbitrageSgg(DOSSIER_ID_VALUE, DOSSIER_LINK_ID_VALUE);

        ResponseAssertions.assertResponseWithoutMessages(response);

        verify(specificContext).setCurrentDocument(DOSSIER_ID_VALUE);

        verify(specificContext).putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, DOSSIER_LINK_ID_VALUE);
        verify(distributionActionService).demandeArbitrageSGG(specificContext);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(specificContext, MESSAGE_QUEUE, specificContext.getMessageQueue());
    }

    @Test
    public void testDoArbitrageDossier() {
        Response response = controlleur.doArbitrageDossier(
            DOSSIER_ID_VALUE,
            DOSSIER_LINK_ID_VALUE,
            "11",
            "observationsId"
        );

        ResponseAssertions.assertResponseWithoutMessages(response);

        verify(specificContext).putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, DOSSIER_LINK_ID_VALUE);
        verify(specificContext).putInContextData(STContextDataKey.MINISTERE_ID, "11");
        verify(specificContext).putInContextData(ReponsesContextDataKey.OBSERVATIONS, "observationsId");

        verify(distributionActionService).attributionApresArbitrage(specificContext);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(specificContext, MESSAGE_QUEUE, specificContext.getMessageQueue());
    }

    @Test
    public void testDoRejetDossierPoursuivre() {
        Response response = controlleur.rejetDossierPoursuivre(DOSSIER_ID_VALUE, DOSSIER_LINK_ID_VALUE);

        ResponseAssertions.assertResponseWithoutMessages(response);

        verify(specificContext).setCurrentDocument(DOSSIER_ID_VALUE);
        verify(specificContext).putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, DOSSIER_LINK_ID_VALUE);
        verify(distributionActionService).donnerAvisDefavorableEtPoursuivre(specificContext);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(specificContext, MESSAGE_QUEUE, specificContext.getMessageQueue());
    }

    @Test
    public void testReorientationQuestion() {
        // When
        Response response = controlleur.reorientationQuestion(DOSSIER_ID_VALUE, DOSSIER_LINK_ID_VALUE);

        // Then
        ResponseAssertions.assertResponseWithoutMessages(response);

        verify(specificContext).setCurrentDocument(DOSSIER_ID_VALUE);
        verify(specificContext).putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, DOSSIER_LINK_ID_VALUE);
        verify(distributionActionService).nonConcerneReorientation(specificContext);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(specificContext, MESSAGE_QUEUE, specificContext.getMessageQueue());
    }

    @Test
    public void testDoReattributionDirect() {
        Response response = controlleur.doReattributionDirect(
            DOSSIER_ID_VALUE,
            DOSSIER_LINK_ID_VALUE,
            "ministereId",
            "observationsId"
        );

        ResponseAssertions.assertResponseWithoutMessages(response);

        verify(specificContext).putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, DOSSIER_LINK_ID_VALUE);
        verify(specificContext).putInContextData(STContextDataKey.MINISTERE_ID, "ministereId");
        verify(specificContext).putInContextData(ReponsesContextDataKey.OBSERVATIONS, "observationsId");

        verify(distributionActionService).reattributionDirecte(specificContext);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(specificContext, MESSAGE_QUEUE, specificContext.getMessageQueue());
    }

    @Test
    public void testRefusReattribution() {
        Response response = controlleur.refusReattribution(DOSSIER_ID_VALUE, "11");

        ResponseAssertions.assertResponseWithoutMessages(response);

        verify(specificContext).setCurrentDocument(DOSSIER_ID_VALUE);
        verify(specificContext).putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, "11");
        verify(distributionActionService).donnerAvisDefavorableEtRetourBdcAttributaire(specificContext);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(specificContext, MESSAGE_QUEUE, specificContext.getMessageQueue());
    }

    @Test
    public void testAddDocumentFondDeDossier() {
        String fondDeDossierFileId = "fond-dossier-file-id";

        FormDataMultiPart multipart = mock(FormDataMultiPart.class);
        FormDataBodyPart body = mock(FormDataBodyPart.class);
        BodyPartEntity ent = mock(BodyPartEntity.class);

        List<FormDataBodyPart> liste = new ArrayList<>();
        liste.add(body);

        when(multipart.getFields("documentFile")).thenReturn(liste);
        when(body.getFormDataContentDisposition()).thenReturn(fileDetail);
        when(body.getEntity()).thenReturn(ent);
        when(ent.getInputStream()).thenReturn(uploadedInputStream);

        Response response = controlleur.addDocumentFondDossier(DOSSIER_ID_VALUE, fondDeDossierFileId, multipart);

        ResponseAssertions.assertResponseWithoutMessages(response);

        verify(specificContext).setCurrentDocument(DOSSIER_ID_VALUE);
        verify(specificContext).putInContextData(ID, fondDeDossierFileId);
        verify(specificContext).putInContextData(FILE_CONTENT, uploadedInputStream);
        verify(specificContext).putInContextData(FILE_DETAILS, fileDetail);
        verify(fondDeDossierUIService).addFile(specificContext);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(specificContext, MESSAGE_QUEUE, specificContext.getMessageQueue());
    }

    @Test
    public void testEditDocumentFondDossier() {
        String fondDeDossierFileId = "fond-dossier-file-id";

        Response response = controlleur.editDocumentFondDossier(
            DOSSIER_ID_VALUE,
            fondDeDossierFileId,
            uploadedInputStream,
            fileDetail
        );

        ResponseAssertions.assertResponseWithoutMessages(response);

        verify(specificContext).setCurrentDocument(DOSSIER_ID_VALUE);
        verify(specificContext).putInContextData(ID, fondDeDossierFileId);
        verify(specificContext).putInContextData(FILE_CONTENT, uploadedInputStream);
        verify(specificContext).putInContextData(FILE_DETAILS, fileDetail);
        verify(fondDeDossierUIService).editFile(specificContext);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(specificContext, MESSAGE_QUEUE, specificContext.getMessageQueue());
    }

    @Test
    public void testDeleteDocumentFondDossier() {
        String fondDeDossierFileId = "fond-dossier-file-id";

        Response response = controlleur.deleteDocumentFondDossier(DOSSIER_ID_VALUE, fondDeDossierFileId);

        ResponseAssertions.assertResponseWithoutMessages(response);

        verify(specificContext).setCurrentDocument(DOSSIER_ID_VALUE);
        verify(specificContext).putInContextData(ID, fondDeDossierFileId);
        verify(fondDeDossierUIService).deleteFile(specificContext);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(specificContext, MESSAGE_QUEUE, specificContext.getMessageQueue());
    }

    @Test
    public void testCompareVersions() {
        DocumentModel dossierLinkDoc = mock(DocumentModel.class);
        DossierLink dossierLink = mock(DossierLink.class);
        DocumentModel dossierDoc = mock(DocumentModel.class);
        Dossier dossier = mock(Dossier.class);
        when(specificContext.getSession().getDocument(new IdRef(DOSSIER_LINK_ID_VALUE))).thenReturn(dossierLinkDoc);
        when(dossierLinkDoc.getAdapter(DossierLink.class)).thenReturn(dossierLink);
        when(dossierLink.getDossier(specificContext.getSession())).thenReturn(dossier);
        when(dossier.getDocument()).thenReturn(dossierDoc);
        when(specificContext.getWebcontext()).thenReturn(webcontext);

        CompareVersionDTO dto = new CompareVersionDTO();
        dto.setTextFirst("premier texte");
        dto.setTextLast("deuxieme texte");

        when(comparateurActionService.getVersionTexts(specificContext, "idVersion1", "idVersion2")).thenReturn(dto);

        ThTemplate result = controlleur.compareVersions(
            DOSSIER_ID_VALUE,
            DOSSIER_LINK_ID_VALUE,
            "idVersion1",
            "idVersion2"
        );

        assertNotNull(result);
        assertEquals(dto, result.getData().get("dto"));
    }

    @Test
    public void testCasserCachetServeur() {
        Response response = controlleur.casserCachetServeur(DOSSIER_ID_VALUE);

        verify(specificContext).setCurrentDocument(DOSSIER_ID_VALUE);
        verify(reponseActionService).briserReponse(specificContext);

        ResponseAssertions.assertResponseWithoutMessages(response);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(specificContext, MESSAGE_QUEUE, specificContext.getMessageQueue());
    }
}
