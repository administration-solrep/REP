package fr.dila.reponses.ui.jaxrs.webobject.page.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import fr.dila.reponses.api.stat.report.ReportStats;
import fr.dila.reponses.api.stat.report.StatGlobale;
import fr.dila.reponses.api.stat.report.StatMinistere;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.StatistiquesUIService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.bean.ConfigDTO;
import fr.dila.st.ui.services.ConfigUIService;
import fr.dila.st.ui.services.EtatApplicationUIService;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.model.ThTemplate;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.webengine.WebEngine;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ ReponsesUIServiceLocator.class, STUIServiceLocator.class, STServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class LoginStatsObjectTest {
    private static final long NB_QUESTIONS = 500L;
    private static final long NB_REPONDU = 250L;
    private static final String URL_DILA = "test.fr";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private LoginStatsObject controlleur;

    @Mock
    private StatistiquesUIService mockStatistiquesUIService;

    @Mock
    private ConfigUIService configUIService;

    @Mock
    private EtatApplicationUIService etatAppUIService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private STParametreService mockSTParametreService;

    @Mock
    private HttpSession session;

    private ConfigDTO config = new ConfigDTO("", "", "", "", "");

    private Map<String, Object> mapEtatApp = new HashMap<>();

    private String expectedTaux;

    @Before
    public void before() throws LoginException {
        controlleur = new LoginStatsObject();

        PowerMockito.mockStatic(ReponsesUIServiceLocator.class);
        PowerMockito.mockStatic(STUIServiceLocator.class);
        PowerMockito.mockStatic(WebEngine.class);
        PowerMockito.mockStatic(STServiceLocator.class);

        when(ReponsesUIServiceLocator.getStatistiquesUIService()).thenReturn(mockStatistiquesUIService);

        when(STUIServiceLocator.getConfigUIService()).thenReturn(configUIService);
        when(configUIService.getConfig()).thenReturn(config);

        when(STUIServiceLocator.getEtatApplicationUIService()).thenReturn(etatAppUIService);
        when(etatAppUIService.getEtatApplicationDocumentUnrestricted()).thenReturn(mapEtatApp);

        when(STServiceLocator.getSTParametreService()).thenReturn(mockSTParametreService);
        when(mockSTParametreService.getParametreWithoutSession(Mockito.anyString())).thenReturn(URL_DILA);

        Whitebox.setInternalState(controlleur, "request", request);

        when(request.getSession(true)).thenReturn(session);
    }

    private ReportStats initComplete() {
        StatGlobale testStatGlobale = new StatGlobale();
        testStatGlobale.setNbQuestions(NB_QUESTIONS);
        testStatGlobale.setNbRepondu(NB_REPONDU);

        Double taux = Math.round(((float) NB_REPONDU / NB_QUESTIONS * 100.0) * 100.0) / 100.0;

        DecimalFormat df = new DecimalFormat("###0");
        expectedTaux = df.format(taux);

        List<StatMinistere> testStatMinisteres = new ArrayList<>();

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

        ReportStats origReportStats = new ReportStats();
        origReportStats.setGlobalStats(testStatGlobale);
        origReportStats.setMinistereStats(testStatMinisteres);

        return origReportStats;
    }

    @Test
    public void testGetLogin() throws LoginException {
        when(mockStatistiquesUIService.getReportStats()).thenReturn(new ReportStats());

        ThTemplate template = (ThTemplate) controlleur.getLogin(null, null, null);

        Map<String, Object> dataMap = template.getData();

        assertEquals(config, dataMap.get("config"));
    }

    @Test
    public void testGetLoginFullStats() throws LoginException {
        ReportStats orig = initComplete();
        when(mockStatistiquesUIService.getReportStats()).thenReturn(orig);

        ThTemplate template = (ThTemplate) controlleur.getLogin(null, null, null);

        Map<String, Object> dataMap = template.getData();

        String globalStats = (String) dataMap.get(LoginStatsObject.DATA_STATS_GLOBALES);
        assertThat(globalStats).startsWith(ResourceHelper.getString("login.stats.tauxGouvernement"));
        assertThat(globalStats).endsWith(expectedTaux + "%");

        List<StatMinistere> list = (List<StatMinistere>) dataMap.get(LoginStatsObject.DATA_STATS_MINISTERES);
        assertThat(list).isNotEmpty();
        assertEquals(orig.getMinistereStats().size(), list.size());

        for (int i = 0; i < list.size(); i++) {
            StatMinistere stat = orig.getMinistereStats().get(i);
            StatMinistere stat2 = list.get(i);

            assertEquals(stat.getMinistere(), stat2.getMinistere());
            assertEquals(stat.getNbQuestions(), stat2.getNbQuestions());
            assertEquals(stat.getTaux2mois(), stat2.getTaux2mois());
        }
    }

    @Test
    public void testGetLoginEmptyStats() throws LoginException {
        when(mockStatistiquesUIService.getReportStats()).thenReturn(new ReportStats());

        ThTemplate template = (ThTemplate) controlleur.getLogin(null, null, null);

        Map<String, Object> dataMap = template.getData();

        assertEquals(
            ResourceHelper.getString("login.stats.aucuneQuestion"),
            dataMap.get(LoginStatsObject.DATA_STATS_GLOBALES)
        );
        List<StatMinistere> list = (List<StatMinistere>) dataMap.get(LoginStatsObject.DATA_STATS_MINISTERES);
        assertThat(list).isEmpty();
    }
}
