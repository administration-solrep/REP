package fr.dila.reponses.core.event;

import static org.junit.Assert.assertNotNull;

import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.service.StatsService;
import fr.dila.reponses.api.stat.report.ReportStats;
import fr.dila.reponses.core.event.batch.AbstractBatchTester;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.runtime.mockito.RuntimeService;

public class TestGenerateReportListener extends AbstractBatchTester {
    @Mock
    @RuntimeService
    protected StatsService mockStatsService;

    private ReportStats reportStats = null;

    @Before
    public void setUp() {
        Mockito
            .doAnswer(
                invocation -> {
                    reportStats = new ReportStats();
                    return null;
                }
            )
            .when(mockStatsService)
            .generateReportStats(Mockito.any(CoreSession.class));
    }

    @Test
    public void testEvent() {
        launchEvent(ReponsesEventConstant.GENERATE_REPORT_EVENT);

        ServiceUtil.getRequiredService(EventService.class).waitForAsyncCompletion();

        assertNotNull(reportStats);
    }
}
