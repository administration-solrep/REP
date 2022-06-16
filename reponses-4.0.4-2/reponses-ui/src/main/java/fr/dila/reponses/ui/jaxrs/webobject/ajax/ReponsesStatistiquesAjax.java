package fr.dila.reponses.ui.jaxrs.webobject.ajax;

import static fr.dila.reponses.ui.jaxrs.webobject.page.ReponsesStatistiques.KEY_REPORT_URL;
import static fr.dila.reponses.ui.jaxrs.webobject.page.ReponsesStatistiques.MAP_VALUE_DATAAJAXURL;
import static fr.dila.reponses.ui.jaxrs.webobject.page.ReponsesStatistiques.MAP_VALUE_DATAURL;
import static fr.dila.reponses.ui.jaxrs.webobject.page.ReponsesStatistiques.ORGANIGRAMME_ID;
import static fr.dila.reponses.ui.jaxrs.webobject.page.ReponsesStatistiques.QUERY_PARAM_MINISTERE_ID;
import static fr.dila.reponses.ui.jaxrs.webobject.page.ReponsesStatistiques.QUERY_PARAM_POSTE_ID;
import static fr.dila.reponses.ui.jaxrs.webobject.page.ReponsesStatistiques.QUERY_PARAM_UNITE_ID;

import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.StatistiquesUIService;
import fr.dila.ss.api.birt.BirtReport;
import fr.dila.ss.api.birt.ReportProperty;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.ui.bean.BirtReportList;
import fr.dila.ss.ui.enums.SSActionCategory;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.th.bean.BirtReportListForm;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "StatistiquesAjax")
public class ReponsesStatistiquesAjax extends SolonWebObject {

    @GET
    @Path("/liste")
    public ThTemplate getListeStatistiques(@SwBeanParam BirtReportListForm form) {
        ThTemplate template = new AjaxLayoutThTemplate();
        template.setName("fragments/table/tableBirtReports");
        SpecificContext context = getMyContext();
        template.setContext(context);

        StatistiquesUIService service = ReponsesUIServiceLocator.getStatistiquesUIService();
        BirtReportList statList = service.getStatistiqueList((SSPrincipal) context.getSession().getPrincipal(), form);

        // Map pour mon contenu spécifique
        Map<String, Object> map = new HashMap<>();
        map.put(STTemplateConstants.RESULT_LIST, statList);
        map.put(STTemplateConstants.RESULT_FORM, form);
        map.put(STTemplateConstants.DATA_URL, MAP_VALUE_DATAURL);
        map.put(STTemplateConstants.DATA_AJAX_URL, MAP_VALUE_DATAAJAXURL);
        template.setData(map);
        return template;
    }

    @GET
    @Path("/display/{statId}")
    public ThTemplate displayReport(
        @PathParam("statId") String statId,
        @QueryParam(QUERY_PARAM_MINISTERE_ID) String ministereId,
        @QueryParam(QUERY_PARAM_POSTE_ID) String posteId,
        @QueryParam(QUERY_PARAM_UNITE_ID) String uniteId
    ) {
        ThTemplate template = new AjaxLayoutThTemplate();
        template.setName("fragments/statistiques/statDetail");
        SpecificContext context = getMyContext();
        template.setContext(context);

        StatistiquesUIService statistiquesUIService = ReponsesUIServiceLocator.getStatistiquesUIService();
        BirtReport birtReport = statistiquesUIService.getBirtReport(statId);

        // Map pour mon contenu spécifique
        Map<String, Object> map = new HashMap<>();
        Map<String, String> params = new HashMap<>();

        map.put(
            ORGANIGRAMME_ID,
            Stream.of(ministereId, uniteId, posteId).filter(Objects::nonNull).findFirst().orElse(null)
        );
        params.put(QUERY_PARAM_MINISTERE_ID, ministereId);
        params.put(QUERY_PARAM_UNITE_ID, uniteId);
        params.put(QUERY_PARAM_POSTE_ID, posteId);

        map.put(SSTemplateConstants.BIRT_REPORT, birtReport);
        map.put(SSTemplateConstants.GENERATED_REPORT_PATH, statistiquesUIService.generateReport(birtReport, params));
        Collection<ReportProperty> scalarProperties = statistiquesUIService.getScalarProperties(birtReport);
        context.putInContextData(SSContextDataKey.IS_MULTI, CollectionUtils.isNotEmpty(scalarProperties));
        context.putInContextData(SSContextDataKey.HAS_RIGHT_TO_EXPORT, statistiquesUIService.hasRightToExport(context));
        map.put(SSTemplateConstants.DOWNLOAD_ACTIONS, context.getActions(SSActionCategory.STAT_ACTIONS_DOWNLOAD));
        map.put(SSTemplateConstants.DISPLAY_REPORT, true);
        map.put(KEY_REPORT_URL, statistiquesUIService.getReportUrl(birtReport, params));

        template.setData(map);
        return template;
    }

    @POST
    @Path("/envoyer/excel")
    @Produces(MediaType.APPLICATION_JSON)
    public Response envoyerMailExcel(@FormParam("stat") String stat) {
        context.putInContextData(SSContextDataKey.STAT_ID, stat);

        StatistiquesUIService statistiquesUIService = ReponsesUIServiceLocator.getStatistiquesUIService();
        statistiquesUIService.generateAllExcel(context);

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Path("/envoyer/pdf")
    @Produces(MediaType.APPLICATION_JSON)
    public Response envoyerMailPdf(@FormParam("stat") String stat) {
        context.putInContextData(SSContextDataKey.STAT_ID, stat);

        StatistiquesUIService statistiquesUIService = ReponsesUIServiceLocator.getStatistiquesUIService();
        statistiquesUIService.generateAllPdf(context);

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Path("/exporter/zip")
    @Produces(MediaType.APPLICATION_JSON)
    public Response exporterZip(@FormParam("ids[]") List<String> ids) {
        context.putInContextData(STContextDataKey.IDS, ids);

        StatistiquesUIService statistiquesUIService = ReponsesUIServiceLocator.getStatistiquesUIService();
        statistiquesUIService.doZipExport(context);

        // Comme je recharge la page si pas d'erreur je met en session les
        // messages pour les afficher au rechargement
        if (CollectionUtils.isEmpty(context.getMessageQueue().getErrorQueue())) {
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
        }

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Path("/archive/supprimer")
    @Produces(MediaType.APPLICATION_JSON)
    public Response supprimerZip(@FormParam("fileName") String file) {
        StatistiquesUIService statistiquesUIService = ReponsesUIServiceLocator.getStatistiquesUIService();
        statistiquesUIService.deleteOldExportedStats(context);

        // Comme je recharge la page si pas d'erreur je met en session les
        // messages pour les afficher au rechargement
        if (CollectionUtils.isEmpty(context.getMessageQueue().getErrorQueue())) {
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
        }

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Override
    protected SpecificContext getMyContext() {
        return new SpecificContext();
    }
}
