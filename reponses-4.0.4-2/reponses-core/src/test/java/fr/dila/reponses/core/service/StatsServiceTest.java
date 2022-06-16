package fr.dila.reponses.core.service;

import static org.junit.Assert.assertEquals;

import fr.dila.reponses.api.constant.ReponsesConfigConstant;
import fr.dila.reponses.api.stat.report.ReportStats;
import fr.dila.reponses.api.stat.report.StatGlobale;
import fr.dila.reponses.api.stat.report.StatMinistere;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.core.feature.SolonMockitoFeature;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.nuxeo.runtime.mockito.RuntimeService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ SolonMockitoFeature.class })
public class StatsServiceTest {
    @Mock
    @RuntimeService
    private ConfigService mockConfigService;

    private StatGlobale testStatGlobale;
    private List<StatMinistere> testStatMinisteres;

    private ReportStats reportStats = null;

    @Before
    public void setUp() {
        testStatGlobale = new StatGlobale();
        testStatGlobale.setNbQuestions(500L);
        testStatGlobale.setNbRepondu(250L);

        testStatMinisteres = new ArrayList<>();

        StatMinistere stat = new StatMinistere();
        stat.setMinistere("Min1");
        stat.setNbQuestions(300L);
        stat.setTaux2mois("80");
        testStatMinisteres.add(stat);

        stat = new StatMinistere();
        stat.setMinistere("Min2");
        stat.setNbQuestions(200L);
        stat.setTaux2mois("60");
        testStatMinisteres.add(stat);

        reportStats = new ReportStats();
        reportStats.setGlobalStats(testStatGlobale);
        reportStats.setMinistereStats(testStatMinisteres);

        Mockito
            .when(mockConfigService.getValue(Matchers.eq(ReponsesConfigConstant.REPONSES_GENERATED_REPORT_DIRECTORY)))
            .thenReturn("/tmp");
    }

    @After
    public void tearDown() {
        File testFile = new File("/tmp/stats.json");
        if (testFile != null && testFile.exists()) {
            testFile.delete();
        }
    }

    @Test
    public void testStatSerialization() {
        StatsServiceImpl statsService = new StatsServiceImpl();
        statsService.serializeReportStats(reportStats);

        ReportStats reportStats2 = statsService.deserializeReportStats();

        assertEquals(reportStats.getGlobalStats().getNbQuestions(), reportStats2.getGlobalStats().getNbQuestions());
        assertEquals(reportStats.getGlobalStats().getNbRepondu(), reportStats2.getGlobalStats().getNbRepondu());

        List<StatMinistere> minStat = reportStats.getMinistereStats();
        List<StatMinistere> minStat2 = reportStats2.getMinistereStats();

        assertEquals(minStat.size(), minStat2.size());

        for (int i = 0; i < minStat2.size(); i++) {
            StatMinistere stat = minStat.get(i);
            StatMinistere stat2 = minStat2.get(i);

            assertEquals(stat.getMinistere(), stat2.getMinistere());
            assertEquals(stat.getNbQuestions(), stat2.getNbQuestions());
            assertEquals(stat.getTaux2mois(), stat2.getTaux2mois());
        }
    }
}
