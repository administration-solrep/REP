package fr.dila.reponses.ui.jaxrs.webobject.ajax;

import static fr.dila.st.core.util.FileUtils.DEFAULT_APP_FOLDER_TMP;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.reponses.ui.services.actions.organigramme.ReponsesOrganigrammeInjectionActionService;
import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.ss.ui.services.SSOrganigrammeManagerService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.assertions.ResponseAssertions;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.ws.rs.core.Response;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.platform.actions.Action;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ STServiceLocator.class, ReponsesActionsServiceLocator.class, SSUIServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class ReponsesOrganigrammeAjaxTest {
    private static final String EXPORT_FILENAME = "gouvernement.xls";
    private static final String TMP_REPONSES_DIR = "/tmp/reponses/";
    private static final Path TMP_REPONSES_PATH = Paths.get(TMP_REPONSES_DIR);
    private static final Path SOURCE_EXPORT_FILE_PATH = Paths.get("src/test/resources/" + EXPORT_FILENAME);
    private static final Path TARGET_EXPORT_FILE_PATH = Paths.get(TMP_REPONSES_DIR + EXPORT_FILENAME);

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private ReponsesOrganigrammeAjax page;

    @Mock
    private ReponsesOrganigrammeInjectionActionService organigrammeInjectionActionService;

    @Mock
    private ConfigService configService;

    @Mock
    private SpecificContext context;

    @Mock
    private CoreSession session;

    @Mock
    private SSOrganigrammeManagerService organigrammeManager;

    @Before
    public void setUp() {
        page = new ReponsesOrganigrammeAjax();

        PowerMockito.mockStatic(STServiceLocator.class);
        when(STServiceLocator.getConfigService()).thenReturn(configService);

        PowerMockito.mockStatic(SSUIServiceLocator.class);
        when(SSUIServiceLocator.getSSOrganigrammeManagerService()).thenReturn(organigrammeManager);

        PowerMockito.mockStatic(ReponsesActionsServiceLocator.class);
        when(ReponsesActionsServiceLocator.getReponsesOrganigrammeInjectionActionService())
            .thenReturn(organigrammeInjectionActionService);

        when(context.getSession()).thenReturn(session);

        Whitebox.setInternalState(page, "context", context);
    }

    @AfterClass
    public static void afterClass() throws IOException {
        FileUtils.deleteDirectory(new File(TMP_REPONSES_DIR));
    }

    @Test
    public void testDownload() throws IOException {
        Files.createDirectories(TMP_REPONSES_PATH);

        Files.copy(SOURCE_EXPORT_FILE_PATH, TARGET_EXPORT_FILE_PATH, REPLACE_EXISTING);

        when(configService.getValue(STConfigConstants.APP_FOLDER_TMP, DEFAULT_APP_FOLDER_TMP))
            .thenReturn(TMP_REPONSES_DIR);
        when(context.getAction(SSActionEnum.EXPORT_GOUVERNEMENT)).thenReturn(new Action());

        Response response = page.exportGouvernement();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        assertThat(response.getEntity()).isEqualTo(TARGET_EXPORT_FILE_PATH.toFile());

        verify(organigrammeManager).computeOrganigrammeActions(context);
        verify(organigrammeInjectionActionService).exportGouvernement(session);
    }

    @Test
    public void testDownloadWithBadTmpDir() throws IOException {
        Files.createDirectories(TMP_REPONSES_PATH);

        Files.copy(SOURCE_EXPORT_FILE_PATH, TARGET_EXPORT_FILE_PATH, REPLACE_EXISTING);

        when(configService.getValue(STConfigConstants.APP_FOLDER_TMP, DEFAULT_APP_FOLDER_TMP))
            .thenReturn(TARGET_EXPORT_FILE_PATH.toString());
        when(context.getAction(SSActionEnum.EXPORT_GOUVERNEMENT)).thenReturn(new Action());

        Response response = page.exportGouvernement();

        ResponseAssertions.assertResponseWithBadRequest(response);

        verify(organigrammeManager).computeOrganigrammeActions(context);
        verify(organigrammeInjectionActionService).exportGouvernement(session);
    }

    @Test
    public void testDownloadWithoutFileToExport() {
        when(configService.getValue(STConfigConstants.APP_FOLDER_TMP, DEFAULT_APP_FOLDER_TMP))
            .thenReturn(TMP_REPONSES_DIR);
        when(context.getAction(SSActionEnum.EXPORT_GOUVERNEMENT)).thenReturn(new Action());

        Throwable throwable = catchThrowable(() -> page.exportGouvernement());
        assertThat(throwable)
            .isExactlyInstanceOf(NuxeoException.class)
            .hasMessage("Impossible de déterminer le mimetype du fichier : gouvernement.xls");

        verify(organigrammeManager).computeOrganigrammeActions(context);
        verify(organigrammeInjectionActionService).exportGouvernement(session);
    }
}
