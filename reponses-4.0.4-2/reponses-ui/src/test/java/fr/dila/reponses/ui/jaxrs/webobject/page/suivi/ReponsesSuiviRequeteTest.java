package fr.dila.reponses.ui.jaxrs.webobject.page.suivi;

import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_DIVERS;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_EDITION;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_FDR;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.DOSSIERS_MASS_ACTIONS_RENVOIS;
import static fr.dila.reponses.ui.enums.ReponsesActionCategory.RECHERCHE_DOSSIER_ACTIONS_GENERAL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import fr.dila.reponses.ui.bean.RepDossierList;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.RequeteUIService;
import fr.dila.reponses.ui.th.bean.DossierListForm;
import fr.dila.reponses.ui.th.constants.ReponsesTemplateConstants;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.Collections;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.session.UserSession;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ ServiceUtil.class, ReponsesUIServiceLocator.class, WebEngine.class, UserSessionHelper.class })
@PowerMockIgnore("javax.management.*")
public class ReponsesSuiviRequeteTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private RequeteUIService requeteUIService;

    @Mock
    private WebContext webContext;

    @Mock
    SpecificContext context;

    @Mock
    private UserSession userSession;

    ReponsesSuiviRequete controller;

    @Before
    public void setUp() {
        controller = new ReponsesSuiviRequete();
        PowerMockito.mockStatic(ServiceUtil.class);
        PowerMockito.mockStatic(ReponsesUIServiceLocator.class);
        PowerMockito.mockStatic(WebEngine.class);
        PowerMockito.mockStatic(UserSessionHelper.class);
        Mockito.when(WebEngine.getActiveContext()).thenReturn(webContext);
        Mockito.when(webContext.getUserSession()).thenReturn(userSession);
    }

    @Test
    public void testGetResultsFromRequete() {
        when(ReponsesUIServiceLocator.getRequeteUIService()).thenReturn(requeteUIService);

        RepDossierList dossierList = new RepDossierList();
        when(requeteUIService.getDossiersByRequeteExperte(any())).thenReturn(dossierList);
        when(requeteUIService.isRequeteGenerale(any(), any())).thenReturn(true);

        SpecificContext context = spy(new SpecificContext());
        doReturn(Collections.emptyList()).when(context).getActions(RECHERCHE_DOSSIER_ACTIONS_GENERAL);
        doReturn(Collections.emptyList()).when(context).getActions(DOSSIERS_MASS_ACTIONS_FDR);
        doReturn(Collections.emptyList()).when(context).getActions(DOSSIERS_MASS_ACTIONS_EDITION);
        doReturn(Collections.emptyList()).when(context).getActions(DOSSIERS_MASS_ACTIONS_RENVOIS);
        doReturn(Collections.emptyList()).when(context).getActions(DOSSIERS_MASS_ACTIONS_DIVERS);

        Whitebox.setInternalState(controller, "context", context);

        DossierListForm dossierListForm = new DossierListForm();
        ThTemplate template = controller.getResultsFromRequete(dossierListForm, "1");

        assertNotNull(template.getContext());
        assertNotNull(template.getData());
        assertEquals(17, template.getData().size());
        assertEquals(dossierList, template.getData().get(STTemplateConstants.RESULT_LIST));
        assertNotNull(template.getData().get(STTemplateConstants.OTHER_PARAMETER));
        Map<String, Object> otherParameter = (Map<String, Object>) template
            .getData()
            .get(STTemplateConstants.OTHER_PARAMETER);
        assertEquals("1", otherParameter.get("idRequete"));
        assertEquals(true, template.getData().get("isRequeteGenerale"));
        assertEquals(dossierListForm, template.getData().get(STTemplateConstants.RESULT_FORM));
        assertEquals(dossierList.getListeColonnes(), template.getData().get(STTemplateConstants.LST_COLONNES));
        assertEquals(dossierList.getTitre(), template.getData().get(STTemplateConstants.TITRE));
        assertEquals(dossierList.getSousTitre(), template.getData().get(STTemplateConstants.SOUS_TITRE));
        assertEquals("/suivi/requete", template.getData().get(STTemplateConstants.DATA_URL));
        assertEquals("/ajax/suivi/requete", template.getData().get(STTemplateConstants.DATA_AJAX_URL));
        assertTrue(template.getData().containsKey(STTemplateConstants.GENERALE_ACTIONS));
        assertTrue(template.getData().containsKey(SSTemplateConstants.FDR_ACTIONS));
        assertTrue(template.getData().containsKey(SSTemplateConstants.PRINT_ACTIONS));
        assertTrue(template.getData().containsKey(ReponsesTemplateConstants.RENVOI_ACTIONS));
        assertTrue(template.getData().containsKey(SSTemplateConstants.DIVERS_ACTIONS));
        assertFalse(dossierList.getNbTotal() > 0);
        assertEquals(false, template.getData().get(STTemplateConstants.DISPLAY_TABLE));
    }
}
