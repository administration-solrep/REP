package fr.dila.reponses.ui.jaxrs.webobject.ajax;

import static fr.dila.reponses.ui.jaxrs.webobject.page.admin.ReponsesMigration.ACTIONS;
import static fr.dila.reponses.ui.jaxrs.webobject.page.admin.ReponsesMigration.MIGRATION_TYPES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.organigramme.ReponsesMigrationGouvernementUIService;
import fr.dila.ss.ui.bean.MigrationDTO;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.impl.SolonAlertManager;
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
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ ReponsesMigrationAjax.class, UserSessionHelper.class, ReponsesUIServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class ReponsesMigrationAjaxTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private ReponsesMigrationAjax controlleur;

    @Mock
    private SpecificContext context;

    @Mock
    private ReponsesMigrationGouvernementUIService reponsesMigrationGouvernementUIService;

    @Before
    public void before() throws Exception {
        controlleur = Mockito.spy(new ReponsesMigrationAjax());
        Whitebox.setInternalState(controlleur, "context", context);
        when(context.getMessageQueue()).thenReturn(new SolonAlertManager());
        when(context.getUrlPreviousPage()).thenReturn("url");
        mockStatic(UserSessionHelper.class);

        PowerMockito.mockStatic(ReponsesUIServiceLocator.class);
        when(ReponsesUIServiceLocator.getReponsesMigrationGouvernementUIService())
            .thenReturn(reponsesMigrationGouvernementUIService);
        MigrationDTO migrationDto = new MigrationDTO();
        migrationDto.setStatus(ReponsesConstant.EN_COURS_STATUS);
        when(reponsesMigrationGouvernementUIService.getMigrationDTO(context)).thenReturn(migrationDto);
    }

    @Test
    public void testSwitchType() {
        ThTemplate result = controlleur.switchType(OrganigrammeType.DIRECTION.getValue(), 0);

        Map<String, Object> map = result.getData();

        assertNotNull(map.get(SSTemplateConstants.MIGRATION));
        assertEquals(0, map.get(STTemplateConstants.INDEX));
        assertFalse((Boolean) map.get(SSTemplateConstants.IS_RUNNING));
        assertEquals(ACTIONS, map.get(SSTemplateConstants.ACTIONS));
        assertEquals(MIGRATION_TYPES, map.get(SSTemplateConstants.MIGRATION_TYPES));
        assertEquals("/admin/migrations", map.get(STTemplateConstants.DATA_URL));
        assertEquals("/ajax/migrations/switchType", map.get(STTemplateConstants.DATA_AJAX_URL));
    }

    @Test
    public void testAddMigration() {
        ThTemplate result = controlleur.addMigration(1);

        Map<String, Object> map = result.getData();

        assertNotNull(map.get(SSTemplateConstants.MIGRATION));
        assertEquals(1, map.get(STTemplateConstants.INDEX));
        assertFalse((Boolean) map.get(SSTemplateConstants.IS_RUNNING));
        assertEquals(ACTIONS, map.get(SSTemplateConstants.ACTIONS));
        assertEquals(MIGRATION_TYPES, map.get(SSTemplateConstants.MIGRATION_TYPES));
        assertEquals("/admin/migrations", map.get(STTemplateConstants.DATA_URL));
        assertEquals("/ajax/migrations/add", map.get(STTemplateConstants.DATA_AJAX_URL));
    }

    @Test
    public void testLaunch() {
        MigrationDTO dto = new MigrationDTO();

        JsonResponse reponse = (JsonResponse) controlleur.launchMigration(dto).getEntity();

        verify(context).putInContextData("migrationDTO", dto);
        assertEquals(SolonStatus.OK, reponse.getStatut());
    }

    @Test
    public void testRefresh() {
        ThTemplate result = controlleur.refreshMigrations();

        Map<String, Object> map = result.getData();
        assertNotNull(map);
        assertNotNull(map.get(SSTemplateConstants.MIGRATION_DTO));
        assertEquals(MIGRATION_TYPES, map.get(SSTemplateConstants.MIGRATION_TYPES));
        assertEquals(ACTIONS, map.get(SSTemplateConstants.ACTIONS));
        assertEquals(true, map.get(SSTemplateConstants.IS_RUNNING));
    }
}
