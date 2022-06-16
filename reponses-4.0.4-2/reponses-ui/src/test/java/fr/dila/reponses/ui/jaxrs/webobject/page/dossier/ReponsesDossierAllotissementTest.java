package fr.dila.reponses.ui.jaxrs.webobject.page.dossier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.ui.bean.AllotissementListDTO;
import fr.dila.reponses.ui.services.actions.AllotissementActionService;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.ss.ui.enums.SSActionCategory;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
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
@PrepareForTest({ SpecificContext.class, WebEngine.class, ReponsesActionsServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class ReponsesDossierAllotissementTest {

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
    AllotissementActionService service;

    @Mock
    SpecificContext specificContext;

    @Mock
    ThTemplate template;

    @Mock
    DocumentModel currentDocument;

    @Mock
    Dossier dossier;

    @Mock
    Question question;

    ReponsesDossierAllotissement controlleur;

    @Before
    public void before() {
        webcontext = mock(WebContext.class);
        session = mock(CoreSession.class);
        specificContext = mock(SpecificContext.class);
        template = mock(ThTemplate.class);
        PowerMockito.mockStatic(WebEngine.class);
        PowerMockito.mockStatic(ReponsesActionsServiceLocator.class);
        controlleur = new ReponsesDossierAllotissement();
    }

    @Test
    public void testGetAllotissement() {
        AllotissementListDTO expectedDto = new AllotissementListDTO("test");

        Mockito.when(specificContext.getCurrentDocument()).thenReturn(currentDocument);
        Mockito.when(currentDocument.getAdapter(Dossier.class)).thenReturn(dossier);

        Mockito.when(ReponsesActionsServiceLocator.getAllotissementActionService()).thenReturn(service);
        Mockito.when(service.getListQuestionsAllotis(Mockito.any())).thenReturn(expectedDto);
        Mockito.when(dossier.getQuestion(session)).thenReturn(question);
        Mockito.when(specificContext.getSession()).thenReturn(session);
        Mockito.when(specificContext.getActions(SSActionCategory.DOSSIER_TAB_ACTIONS)).thenReturn(new ArrayList<>());

        Whitebox.setInternalState(controlleur, "context", specificContext);
        ThTemplate template = controlleur.getAllotissement();
        assertNotNull(template);
        assertTrue(template instanceof AjaxLayoutThTemplate);
        assertEquals("ajaxLayout", template.getLayout());
        assertEquals("fragments/dossier/onglets/allotissement", template.getName());
        assertNotNull(template.getContext());

        //Données ajoutées à la map
        assertEquals(5, template.getData().size());
        assertEquals(expectedDto.getLstColonnes(), template.getData().get(STTemplateConstants.LST_COLONNES));
        assertEquals(expectedDto.getListQuestionsAlloties(), template.getData().get(STTemplateConstants.RESULT_LIST));
        assertEquals(expectedDto.getNomDossierDirecteur(), template.getData().get("dossierDirecteur"));
        SpecificContext context = Whitebox.getInternalState(controlleur, "context");
        assertEquals(context.getActions(SSActionCategory.DOSSIER_TAB_ACTIONS), template.getData().get("tabActions"));
    }

    @Test
    public void testGetMyTemplate() {
        ReponsesDossierAllotissement controlleur = new ReponsesDossierAllotissement();
        ThTemplate template = controlleur.getMyTemplate();
        assertNotNull(template);
        assertTrue(template instanceof AjaxLayoutThTemplate);
        assertEquals("ajaxLayout", template.getLayout());
        assertEquals("fragments/dossier/onglets/allotissement", template.getName());
        assertNotNull(template.getContext());
    }
}
