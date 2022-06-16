package fr.dila.reponses.ui.jaxrs.webobject.page.admin;

import static fr.dila.reponses.ui.jaxrs.webobject.page.admin.ReponsesMigration.ACTIONS;
import static fr.dila.reponses.ui.jaxrs.webobject.page.admin.ReponsesMigration.MIGRATION_TYPES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.organigramme.ReponsesMigrationGouvernementUIService;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.ui.bean.MigrationDTO;
import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ ReponsesMigration.class, SpecificContext.class, ReponsesUIServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class ReponsesMigrationTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private ReponsesMigration controlleur;

    @Mock
    private SpecificContext context;

    @Mock
    private WebContext webContext;

    @Mock
    private SSPrincipal principal;

    @Mock
    private ReponsesMigrationGouvernementUIService reponsesMigrationGouvernementUIService;

    @Before
    public void before() throws Exception {
        controlleur = Mockito.spy(new ReponsesMigration());
        Whitebox.setInternalState(controlleur, "context", context);
        when(context.getUrlPreviousPage()).thenReturn("url");
        when(context.getWebcontext()).thenReturn(webContext);
        when(context.getAction(SSActionEnum.ADMIN_MIGRATION_MIGRATIONS)).thenReturn(new Action());
        when(webContext.getPrincipal()).thenReturn(principal);
        when(principal.isMemberOf("EspaceAdministrationReader")).thenReturn(true);

        PowerMockito.mockStatic(ReponsesUIServiceLocator.class);
        when(ReponsesUIServiceLocator.getReponsesMigrationGouvernementUIService())
            .thenReturn(reponsesMigrationGouvernementUIService);
        when(reponsesMigrationGouvernementUIService.getMigrationDTO(context)).thenReturn(new MigrationDTO());
    }

    @Test
    public void testGetMigrations() {
        ThTemplate result = controlleur.getMigrations();

        Map<String, Object> map = result.getData();
        assertNotNull(map);
        assertNotNull(map.get("migrationDto"));
        assertEquals(MIGRATION_TYPES, map.get("migrationTypes"));
        assertEquals(ACTIONS, map.get("actions"));
        assertEquals("url", map.get(STTemplateConstants.URL_PREVIOUS_PAGE));
    }
}
