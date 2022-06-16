package fr.dila.reponses.ui.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import fr.dila.reponses.api.service.StatsService;
import fr.dila.reponses.api.stat.report.ReportStats;
import fr.dila.reponses.ui.jaxrs.webobject.page.ReponsesStatistiques;
import fr.dila.ss.api.birt.BirtReport;
import fr.dila.ss.api.birt.ReportProperty;
import fr.dila.st.core.feature.SolonMockitoFeature;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.nuxeo.runtime.mockito.RuntimeService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(SolonMockitoFeature.class)
public class StatistiquesUIServiceImplIT {
    @Mock
    @RuntimeService
    protected StatsService mockStatsServive;

    private StatistiquesUIServiceImpl service;

    private ReportStats origReportStats = new ReportStats();

    private static final String MINISTEREID = "60005975";
    private static final String DIRECTIONID = "idDirection";
    private static final String POSTEID = "idPoste";

    @Before
    public void before() throws IOException {
        service = new StatistiquesUIServiceImpl();

        Mockito.when(mockStatsServive.getDailyReportStats()).thenReturn(origReportStats);
    }

    @Test
    public void testGetTableauDeBordStats() throws IOException {
        ReportStats reportStats = service.getReportStats();
        assertEquals(origReportStats, reportStats);
    }

    @Test
    public void testGetReportUrl() {
        String birtReportId = "statXX";

        BirtReport mockBirtReport = Mockito.mock(BirtReport.class);
        Mockito.when(mockBirtReport.getId()).thenReturn(birtReportId);

        assertEquals(birtReportId, service.getReportUrl(mockBirtReport, null));
        assertEquals(
            birtReportId + "?ministereId=" + MINISTEREID,
            service.getReportUrl(mockBirtReport, buildParams(MINISTEREID, null, null))
        );
        assertEquals(
            birtReportId + "?uniteId-key=" + DIRECTIONID,
            service.getReportUrl(mockBirtReport, buildParams(null, null, DIRECTIONID))
        );
        assertEquals(
            birtReportId + "?posteId-key=" + POSTEID,
            service.getReportUrl(mockBirtReport, buildParams(null, POSTEID, null))
        );
        assertEquals(
            birtReportId + "?ministereId=" + MINISTEREID + "&posteId-key=" + POSTEID + "&uniteId-key=" + DIRECTIONID,
            service.getReportUrl(mockBirtReport, buildParams(MINISTEREID, POSTEID, DIRECTIONID))
        );
        assertEquals(birtReportId, service.getReportUrl(mockBirtReport, buildParams(null, null, null)));
    }

    @Test
    public void testHasRequiredParams() {
        BirtReport report = Mockito.mock(BirtReport.class);
        ReportProperty prop = Mockito.mock(ReportProperty.class);
        Map<String, ReportProperty> birtProps = new TreeMap<>();

        Mockito.when(prop.isScalar()).thenReturn(true);

        // Vérifie que tout va bien quand il n'y a pas de propriété définie dans le rapport
        Mockito.when(report.getProperties()).thenReturn(birtProps);
        assertTrue(service.hasRequiredParameters(report, buildParams(null, null, null)));
        assertTrue(service.hasRequiredParameters(report, buildParams(MINISTEREID, POSTEID, DIRECTIONID)));

        // Vérifie que le contrôle est bien effectué pour les ministères
        birtProps.put("min", prop);
        Mockito.when(report.getProperties()).thenReturn(birtProps);
        Mockito.when(prop.getType()).thenReturn(ReportProperty.TYPE_SCALAR_ORGANIGRAMMESELECT_MIN);

        assertFalse(service.hasRequiredParameters(report, buildParams(null, null, null)));
        assertTrue(service.hasRequiredParameters(report, buildParams(MINISTEREID, null, null)));
        assertTrue(service.hasRequiredParameters(report, buildParams(MINISTEREID, POSTEID, DIRECTIONID)));

        birtProps.clear();

        // Vérifie que le contrôle est bien effectué pour les postes
        birtProps.put("poste", prop);
        Mockito.when(report.getProperties()).thenReturn(birtProps);
        Mockito.when(prop.getType()).thenReturn(ReportProperty.TYPE_SCALAR_ORGANIGRAMMESELECT_PST);
        assertFalse(service.hasRequiredParameters(report, buildParams(null, null, null)));
        assertTrue(service.hasRequiredParameters(report, buildParams(null, POSTEID, null)));
        assertTrue(service.hasRequiredParameters(report, buildParams(MINISTEREID, POSTEID, DIRECTIONID)));

        birtProps.clear();

        // Vérifie que le contrôle est bien effectué pour les directions
        birtProps.put("unite", prop);
        Mockito.when(report.getProperties()).thenReturn(birtProps);
        Mockito.when(prop.getType()).thenReturn(ReportProperty.TYPE_SCALAR_ORGANIGRAMMESELECT_UST);
        assertFalse(service.hasRequiredParameters(report, buildParams(null, null, null)));
        assertTrue(service.hasRequiredParameters(report, buildParams(null, null, DIRECTIONID)));
        assertTrue(service.hasRequiredParameters(report, buildParams(MINISTEREID, POSTEID, DIRECTIONID)));

        birtProps.clear();

        // Vérifie que le contrôle est bien effectué pour l'intégralité des paramètres
        ReportProperty unitProp = Mockito.mock(ReportProperty.class);
        birtProps.put("unite", unitProp);
        Mockito.when(unitProp.getType()).thenReturn(ReportProperty.TYPE_SCALAR_ORGANIGRAMMESELECT_UST);
        Mockito.when(unitProp.isScalar()).thenReturn(true);

        ReportProperty posteProp = Mockito.mock(ReportProperty.class);
        birtProps.put("poste", posteProp);
        Mockito.when(posteProp.getType()).thenReturn(ReportProperty.TYPE_SCALAR_ORGANIGRAMMESELECT_PST);
        Mockito.when(posteProp.isScalar()).thenReturn(true);

        ReportProperty minProp = Mockito.mock(ReportProperty.class);
        birtProps.put("min", minProp);
        Mockito.when(minProp.getType()).thenReturn(ReportProperty.TYPE_SCALAR_ORGANIGRAMMESELECT_MIN);
        Mockito.when(minProp.isScalar()).thenReturn(true);

        assertFalse(service.hasRequiredParameters(report, buildParams(null, null, null)));
        assertFalse(service.hasRequiredParameters(report, buildParams(null, null, DIRECTIONID)));
        assertFalse(service.hasRequiredParameters(report, buildParams(null, POSTEID, null)));
        assertFalse(service.hasRequiredParameters(report, buildParams(MINISTEREID, null, null)));
        assertTrue(service.hasRequiredParameters(report, buildParams(MINISTEREID, POSTEID, DIRECTIONID)));
    }

    private Map<String, String> buildParams(String ministereId, String posteId, String directionId) {
        Map<String, String> params = new TreeMap<>();

        if (StringUtils.isNotBlank(ministereId)) {
            params.put(ReponsesStatistiques.QUERY_PARAM_MINISTERE_ID, ministereId);
        }

        if (StringUtils.isNotBlank(directionId)) {
            params.put(ReponsesStatistiques.QUERY_PARAM_UNITE_ID, directionId);
        }

        if (StringUtils.isNotBlank(posteId)) {
            params.put(ReponsesStatistiques.QUERY_PARAM_POSTE_ID, posteId);
        }

        return params;
    }
}
