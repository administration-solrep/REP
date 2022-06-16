package fr.dila.reponses.ui.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import fr.dila.reponses.ui.services.StatistiquesUIService;
import fr.dila.solon.birt.common.BirtOutputFormat;
import fr.dila.ss.api.birt.BirtReport;
import fr.dila.ss.api.service.SSBirtService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.Blob;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ STServiceLocator.class, SSServiceLocator.class, StatistiquesUIService.class, Files.class })
@PowerMockIgnore("javax.management.*")
public class StatistiquesUIServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private ConfigService mockConfigService;

    @Mock
    private SSBirtService mockSSBirtService;

    @Mock
    private SpecificContext context;

    @Mock
    private BirtReport birtReport;

    private StatistiquesUIService service;

    @Before
    public void before() throws IOException {
        PowerMockito.mockStatic(STServiceLocator.class);
        PowerMockito.mockStatic(SSServiceLocator.class);
        PowerMockito.mockStatic(Files.class);

        when(STServiceLocator.getConfigService()).thenReturn(mockConfigService);
        when(SSServiceLocator.getSSBirtService()).thenReturn(mockSSBirtService);

        service = new StatistiquesUIServiceImpl();
    }

    @Test
    public void testGenerateReport() {
        String filename = "fileName.rptdesign";
        String parent_file = "parent_file";
        BirtReport mockBirtReport = Mockito.mock(BirtReport.class);
        when(mockBirtReport.getFile()).thenReturn(filename);
        when(mockBirtReport.getTitle()).thenReturn("report");

        File mockFile = Mockito.mock(File.class);
        Blob mockBlob = Mockito.mock(Blob.class);
        when(mockBlob.getFile()).thenReturn(mockFile);
        when(mockFile.getParent()).thenReturn(parent_file);

        when(
            mockSSBirtService.generateReportResults(
                Mockito.any(File.class),
                Mockito.any(File.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyMap(),
                Mockito.eq(BirtOutputFormat.HTML)
            )
        )
            .thenReturn(mockBlob);

        Map<String, String> params = new HashMap<>();
        params.put("param1", "value1");
        params.put("param2", "value2");
        String url = service.generateReport(mockBirtReport, params);

        assertNotNull(url);
        assertTrue(url.startsWith("report?reportDirectoryName=" + parent_file));
    }

    @Test
    public void testGetHmlReportContent_emptyDir() throws Exception {
        File mockFile = Mockito.mock(File.class);
        PowerMockito.whenNew(File.class).withArguments(String.class, String.class).thenReturn(mockFile);
        when(mockFile.exists()).thenReturn(true);

        when(Files.list(Mockito.any(Path.class))).thenReturn(new ArrayList<Path>().stream());

        assertNull(service.getHtmlReportContent(context, "test"));
    }

    @Test
    public void testGetReportUrlWithParameters() {
        String birtReportId = "birt-report-id";
        when(birtReport.getId()).thenReturn(birtReportId);

        Map<String, String> params = ImmutableMap.of("param1", "value1", "param2", "value2", "param3", "");

        String reportUrl = service.getReportUrl(birtReport, params);

        assertThat(reportUrl).isEqualTo(birtReportId + "?param1=value1&param2=value2");
    }

    @Test
    public void testGetReportUrlWithoutParameters() {
        String birtReportId = "birt-report-id";
        when(birtReport.getId()).thenReturn(birtReportId);

        String reportUrl = service.getReportUrl(birtReport, new HashMap<>());

        assertThat(reportUrl).isEqualTo(birtReportId);
    }
}
