package fr.dila.reponses.ui.jaxrs.webobject.page.login;

import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.LOGIN_WAIT;

import fr.dila.reponses.api.stat.report.ReportStats;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.StatistiquesUIService;
import fr.dila.ss.ui.jaxrs.webobject.page.SSLogin;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.th.model.ThTemplate;
import java.text.DecimalFormat;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.nuxeo.ecm.webengine.model.WebObject;

@Produces("text/html;charset=UTF-8")
@WebObject(type = "AppliLogin")
public class LoginStatsObject extends SSLogin {
    private static final double ONE_HUNDRED = 100.0;
    public static final String DATA_STATS_GLOBALES = "statsGlobales";
    public static final String DATA_STATS_MINISTERES = "statsMinisteres";

    @Override
    @GET
    public Object getLogin(
        @QueryParam("failed") Boolean failed,
        @QueryParam(LOGIN_WAIT) Boolean wait,
        @QueryParam("resetpwd") Boolean resetpwd
    ) {
        Object response = super.getLogin(failed, wait, resetpwd);
        if (response instanceof ThTemplate) {
            ThTemplate template = (ThTemplate) response;

            Map<String, Object> data = template.getData();

            StatistiquesUIService statService = ReponsesUIServiceLocator.getStatistiquesUIService();

            ReportStats reportStats = statService.getReportStats();

            long nbRepondu = reportStats.getGlobalStats().getNbRepondu();
            long nbQuestions = reportStats.getGlobalStats().getNbQuestions();
            if (nbQuestions == 0) {
                data.put(DATA_STATS_GLOBALES, ResourceHelper.getString("login.stats.aucuneQuestion"));
            } else {
                Double taux =
                    Math.round((float) nbRepondu / (float) nbQuestions * ONE_HUNDRED * ONE_HUNDRED) / ONE_HUNDRED;
                DecimalFormat df = new DecimalFormat("###0");
                String tauxStr = df.format(taux);
                data.put(
                    DATA_STATS_GLOBALES,
                    ResourceHelper.getString("login.stats.tauxGouvernement") + " " + tauxStr + "%"
                );
            }

            data.put(DATA_STATS_MINISTERES, reportStats.getMinistereStats());
        }

        return response;
    }

    @Override
    protected String getHomePagePath() {
        return "travail";
    }
}
