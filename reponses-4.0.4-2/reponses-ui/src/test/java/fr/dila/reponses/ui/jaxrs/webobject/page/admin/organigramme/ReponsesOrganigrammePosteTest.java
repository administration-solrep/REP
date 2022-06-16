package fr.dila.reponses.ui.jaxrs.webobject.page.admin.organigramme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import fr.dila.reponses.ui.jaxrs.webobject.page.admin.ReponsesOrganigramme;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.enums.STActionEnum;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.services.STOrganigrammeManagerService;
import fr.dila.st.ui.services.STPosteUIService;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.bean.PosteForm;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.session.UserSession;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ STUIServiceLocator.class, STServiceLocator.class, WebEngine.class, UserSessionHelper.class })
@PowerMockIgnore("javax.management.*")
public class ReponsesOrganigrammePosteTest {
    private String ID = "id";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    ReponsesOrganigrammePoste controller;

    @Mock
    SpecificContext context;

    @Mock
    STPosteUIService posteUIService;

    @Mock
    OrganigrammeService organigrammeService;

    @Mock
    ReponsesOrganigramme organigramme;

    @Mock
    SolonAlertManager alertManager;

    @Mock
    OrganigrammeNode usNode;

    @Mock
    PosteForm posteForm;

    @Mock
    WebContext webContext;

    @Mock
    NuxeoPrincipal principal;

    @Mock
    CoreSession session;

    @Mock
    UserSession userSession;

    @Mock
    SolonWebObject webObject;

    @Mock
    STOrganigrammeManagerService organigrammeManager;

    @Mock
    OrganigrammeNode poste;

    @Mock
    PosteNode posteNode;

    @Mock
    UniteStructurelleNode USNode;

    @Mock
    OrganigrammeNode uniteStructurelle;

    @Before
    public void before() throws Exception {
        controller = spy(new ReponsesOrganigrammePoste());

        PowerMockito.mockStatic(STUIServiceLocator.class);
        PowerMockito.mockStatic(STServiceLocator.class);
        PowerMockito.mockStatic(WebEngine.class);
        PowerMockito.mockStatic(UserSessionHelper.class);

        when(WebEngine.getActiveContext()).thenReturn(webContext);
        when(webContext.getPrincipal()).thenReturn(principal);
        when(webContext.getCoreSession()).thenReturn(session);
        when(webContext.getUserSession()).thenReturn(userSession);
        when(context.getWebcontext()).thenReturn(webContext);
        when(principal.isMemberOf("EspaceAdministrationReader")).thenReturn(true);

        when(STUIServiceLocator.getSTPosteUIService()).thenReturn(posteUIService);
        when(STServiceLocator.getOrganigrammeService()).thenReturn(organigrammeService);
        when(STUIServiceLocator.getSTOrganigrammeManagerService()).thenReturn(organigrammeManager);
    }

    @Test
    public void testDTO() throws Exception {
        String id = "identifiant";
        String libelle = "libelle";
        String dateDebut = "24/07/2020";

        PosteForm form = new PosteForm();
        assertNotNull(form);
        form.setId(id);
        form.setLibelle(libelle);
        form.setDateDebut(dateDebut);
        HashMap<String, String> uniteStructurelleRattachement = new HashMap<>();
        uniteStructurelleRattachement.put("1", "us1");
        uniteStructurelleRattachement.put("2", "us2");
        form.setMapUnitesStructurellesRattachement(uniteStructurelleRattachement);
        assertEquals(form.getId(), id);
        assertEquals(form.getLibelle(), libelle);
        assertEquals(form.getDateDebut(), dateDebut);
        assertFalse(form.getMapUnitesStructurellesRattachement().isEmpty());
        assertEquals(form.getMapUnitesStructurellesRattachement().size(), 2);
        assertEquals(form.getMapUnitesStructurellesRattachement().get("1"), "us1");
        assertEquals(form.getMapUnitesStructurellesRattachement().get("2"), "us2");
    }

    @Test
    public void testGetPosteModification() {
        when(posteUIService.getPosteForm(any())).thenReturn(posteForm);
        when(context.getAction(STActionEnum.MODIFY_POSTE)).thenReturn(new Action());
        when(organigrammeService.getOrganigrammeNodeById(ID, OrganigrammeType.POSTE)).thenReturn(posteNode);
        List<UniteStructurelleNode> listUS = Collections.singletonList(USNode);
        when(posteNode.getUniteStructurelleParentList()).thenReturn(listUS);
        when(USNode.getId()).thenReturn("usId");
        when(organigrammeService.getOrganigrammeNodeById("usId", OrganigrammeType.UNITE_STRUCTURELLE))
            .thenReturn(uniteStructurelle);

        when(posteNode.getId()).thenReturn("idPoste");
        when(posteNode.getLabelWithNor(null)).thenReturn("label");
        when(posteNode.getType()).thenReturn(OrganigrammeType.POSTE);
        when(posteNode.isActive()).thenReturn(true);
        when(posteNode.getLockDate()).thenReturn(new Date());
        when(posteNode.getLockUserName()).thenReturn("userName");

        when(uniteStructurelle.getId()).thenReturn("idPoste");
        when(uniteStructurelle.getLabelWithNor(null)).thenReturn("label");
        when(uniteStructurelle.getType()).thenReturn(OrganigrammeType.POSTE);
        when(uniteStructurelle.isActive()).thenReturn(true);
        when(uniteStructurelle.getLockDate()).thenReturn(new Date());
        when(uniteStructurelle.getLockUserName()).thenReturn("userName");

        Whitebox.setInternalState(controller, "context", context);

        ThTemplate template = controller.getPosteModification(ID, null);

        assertNotNull(template);
        assertNotNull(template.getData());
        assertEquals(posteForm, template.getData().get(ReponsesOrganigrammePoste.POSTE_FORM));
        assertEquals(template.getName(), "pages/organigramme/editPoste");
        assertEquals(template.getContext(), context);
    }
}
