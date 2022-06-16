package fr.dila.reponses.ui.jaxrs.webobject.page;

import static fr.dila.ss.ui.enums.SSContextDataKey.BIRT_OUTPUT_FORMAT;
import static fr.dila.ss.ui.enums.SSContextDataKey.BIRT_REPORT;
import static fr.dila.ss.ui.enums.SSContextDataKey.HAS_RIGHT_TO_EXPORT;
import static fr.dila.ss.ui.enums.SSContextDataKey.IS_MULTI;
import static fr.dila.ss.ui.enums.SSContextDataKey.STAT_ID;

import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.StatistiquesUIService;
import fr.dila.reponses.ui.th.model.ReponsesStatistiquesTemplate;
import fr.dila.solon.birt.common.BirtOutputFormat;
import fr.dila.ss.api.birt.BirtReport;
import fr.dila.ss.api.birt.ReportProperty;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.ui.bean.BirtReportList;
import fr.dila.ss.ui.enums.SSActionCategory;
import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.ss.ui.jaxrs.webobject.page.AbstractSSStatistiques;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.th.bean.BirtReportListForm;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.core.exception.STAuthorizationException;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.dila.st.ui.utils.FileDownloadUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliStats")
public class ReponsesStatistiques extends AbstractSSStatistiques {
    public static final String MAP_VALUE_DATAAJAXURL = "/ajax/stats/liste";
    public static final String MAP_VALUE_DATAURL = "/stats";
    public static final String KEY_PARAMS = "params";
    /** URL d'accès direct à cette stat (et ses paramètres) */
    public static final String KEY_REPORT_URL = "reportUrl";

    public static final String QUERY_PARAM_MINISTERE_ID = "ministereId";
    public static final String QUERY_PARAM_POSTE_ID = "posteId-key";
    public static final String QUERY_PARAM_UNITE_ID = "uniteId-key";
    public static final String ORGANIGRAMME_ID = "organigrammeId";

    public ReponsesStatistiques() {
        super();
    }

    @GET
    public ThTemplate getHome(@SwBeanParam BirtReportListForm form) {
        ThTemplate template = getMyTemplate();
        template.setName("pages/stats");
        context.removeNavigationContextTitle();
        template.setContext(context);

        StatistiquesUIService statistiquesUIService = ReponsesUIServiceLocator.getStatistiquesUIService();
        BirtReportList statList = statistiquesUIService.getStatistiqueList(
            (SSPrincipal) context.getSession().getPrincipal(),
            form
        );

        String filename = statistiquesUIService.getGeneratedExportName(context);

        // Map pour mon contenu spécifique
        Map<String, Object> map = new HashMap<>();
        map.put(STTemplateConstants.RESULT_LIST, statList);
        map.put(STTemplateConstants.RESULT_FORM, form);
        map.put(STTemplateConstants.DATA_URL, MAP_VALUE_DATAURL);
        map.put(STTemplateConstants.DATA_AJAX_URL, MAP_VALUE_DATAAJAXURL);
        context.putInContextData(HAS_RIGHT_TO_EXPORT, statistiquesUIService.hasRightToExport(context));
        context.putInContextData(ReponsesContextDataKey.ZIP_FILENAME_STATS, filename);
        map.put(ZIP_FILENAME, filename);
        map.put(ZIP_LINK, ZIP_DOWNLOAD_URL);
        map.put("zipAction", context.getAction(SSActionEnum.EXPORT_ZIP_STATS));
        map.put("removeZipAction", context.getAction(SSActionEnum.REMOVE_ZIP_STATS));
        template.setData(map);
        return template;
    }

    /**
     * Commande la génération du rapport et affiche la page résultante
     *
     * @param id
     *            id de la statistique
     * @return
     */
    @GET
    @Path("{id}")
    public Object getStatistique(
        @PathParam("id") String id,
        @QueryParam(QUERY_PARAM_MINISTERE_ID) String ministereId,
        @QueryParam(QUERY_PARAM_POSTE_ID) String posteId,
        @QueryParam(QUERY_PARAM_UNITE_ID) String uniteId
    ) {
        ThTemplate template = getMyTemplate();
        template.setName("pages/birtReport");
        template.setContext(context);

        StatistiquesUIService statistiquesUIService = ReponsesUIServiceLocator.getStatistiquesUIService();
        BirtReport birtReport = statistiquesUIService.getBirtReport(id);

        // Vérification des droits de consultation du rapport
        context.putInContextData(BIRT_REPORT, birtReport);
        if (!statistiquesUIService.hasRightToViewSggStat(context)) {
            throw new STAuthorizationException(ResourceHelper.getString("message.stats.erreur.droit"));
        }

        context.setNavigationContextTitle(
            new Breadcrumb(
                birtReport.getTitle(),
                "/stats/" + id,
                Breadcrumb.SUBTITLE_ORDER,
                context.getWebcontext().getRequest()
            )
        );

        Collection<ReportProperty> scalarProperties = statistiquesUIService.getScalarProperties(birtReport);
        Map<String, Object> map = new HashMap<>();
        map.put(SSTemplateConstants.BIRT_REPORT, birtReport);
        map.put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());
        context.putInContextData(IS_MULTI, CollectionUtils.isNotEmpty(scalarProperties));
        context.putInContextData(HAS_RIGHT_TO_EXPORT, statistiquesUIService.hasRightToExport(context));
        map.put(SSTemplateConstants.DOWNLOAD_ACTIONS, context.getActions(SSActionCategory.STAT_ACTIONS_DOWNLOAD));

        // Gérer les accès directs avec l'ensemble des paramètres
        if (scalarProperties.isEmpty()) {
            // Pas besoin de properties supplémentaires => Génération du rapport
            // Birt
            String generatedReportPath = statistiquesUIService.generateReport(birtReport, Collections.emptyMap());

            // Map pour mon contenu spécifique

            map.put(SSTemplateConstants.GENERATED_REPORT_PATH, generatedReportPath);
            map.put(SSTemplateConstants.DISPLAY_REPORT, true);
            map.put(KEY_DISPLAY_ORG_SELECT_MIN, false);
            map.put(KEY_DISPLAY_ORG_SELECT_DIR, false);
            map.put(KEY_DISPLAY_ORG_SELECT_POSTE, false);
            map.put(KEY_REPORT_URL, statistiquesUIService.getReportUrl(birtReport, null));
        } else {
            // Liste des ministères dans le menu déroulant
            map.put(KEY_LIST_MINISTERES, SSUIServiceLocator.getSSSelectValueUIService().getCurrentMinisteres());
            Map<String, String> params = new HashMap<>();

            params.put(ReponsesStatistiques.QUERY_PARAM_MINISTERE_ID, ministereId);
            params.put(ReponsesStatistiques.QUERY_PARAM_UNITE_ID, uniteId);
            params.put(ReponsesStatistiques.QUERY_PARAM_POSTE_ID, posteId);

            map.put(
                ORGANIGRAMME_ID,
                Stream.of(ministereId, uniteId, posteId).filter(Objects::nonNull).findFirst().orElse(null)
            );

            // On a tous les paramètres nécessaires, on peut générer le rapport
            String generatedReportPath = statistiquesUIService.generateReport(birtReport, params);
            map.put(SSTemplateConstants.GENERATED_REPORT_PATH, generatedReportPath);
            map.put(KEY_REPORT_URL, statistiquesUIService.getReportUrl(birtReport, params));

            map.put(
                SSTemplateConstants.DISPLAY_REPORT,
                statistiquesUIService.hasRequiredParameters(birtReport, params)
            );
            map.put(KEY_PARAMS, params);
            map.put(
                KEY_DISPLAY_ORG_SELECT_MIN,
                hasScalarTypeProperty(ReportProperty.TYPE_SCALAR_ORGANIGRAMMESELECT_MIN, scalarProperties)
            );
            map.put(
                KEY_DISPLAY_ORG_SELECT_DIR,
                hasScalarTypeProperty(ReportProperty.TYPE_SCALAR_ORGANIGRAMMESELECT_UST, scalarProperties)
            );
            map.put(
                KEY_DISPLAY_ORG_SELECT_POSTE,
                hasScalarTypeProperty(ReportProperty.TYPE_SCALAR_ORGANIGRAMMESELECT_PST, scalarProperties)
            );
        }

        template.setData(map);

        return template;
    }

    @GET
    @Path("telecharger/pdf")
    @Produces("application/pdf")
    public Response telechargerPdf(@QueryParam("stat") String stat, @QueryParam("organigramme") String organigramme) {
        context.putInContextData(STAT_ID, stat);
        context.putInContextData(ReponsesContextDataKey.ORGANIGRAMME_ID, organigramme);
        context.putInContextData(BIRT_OUTPUT_FORMAT, BirtOutputFormat.PDF);

        Blob blob = ReponsesUIServiceLocator.getStatistiquesUIService().generateReport(context);

        return telechargerFichier(context, blob);
    }

    @GET
    @Path("telecharger/excel")
    @Produces("application/vnd.ms-excel")
    public Response telechargerExcel(@QueryParam("stat") String stat, @QueryParam("organigramme") String organigramme) {
        context.putInContextData(STAT_ID, stat);
        context.putInContextData(ReponsesContextDataKey.ORGANIGRAMME_ID, organigramme);
        context.putInContextData(BIRT_OUTPUT_FORMAT, BirtOutputFormat.XLS);

        Blob blob = ReponsesUIServiceLocator.getStatistiquesUIService().generateReport(context);

        return telechargerFichier(context, blob);
    }

    @GET
    @Path("telecharger/zip")
    @Produces("application/zip")
    public Response telechargerZip() {
        StatistiquesUIService statistiquesUIService = ReponsesUIServiceLocator.getStatistiquesUIService();
        Blob blob = statistiquesUIService.getGeneratedExport(context);

        return FileDownloadUtils.getZipResponse(blob.getFile(), blob.getFilename());
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new ReponsesStatistiquesTemplate();
    }
}
