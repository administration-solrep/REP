package fr.dila.reponses.ui.jaxrs.webobject.ajax;

import static fr.dila.reponses.ui.enums.ReponsesContextDataKey.DOSSIER_IDS;

import fr.dila.reponses.ui.bean.EliminationDonneesList;
import fr.dila.reponses.ui.enums.ReponsesActionEnum;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.bean.PaginationForm;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.enums.AlertType;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "EliminationDonneesAjax")
public class ReponsesEliminationDonneesAjax extends SolonWebObject {
    public static final String DATA_URL = "/admin/eliminationDonnees/liste";

    public ReponsesEliminationDonneesAjax() {
        super();
    }

    @GET
    public ThTemplate getListe(@SwBeanParam PaginationForm eliminationDonneesListform) {
        verifyAction(ReponsesActionEnum.ADMIN_PARAM_ARCHIVAGE, DATA_URL);
        if (template.getData() == null) {
            Map<String, Object> map = new HashMap<>();
            template.setData(map);
        }

        template.setContext(context);

        context.putInContextData(STContextDataKey.PAGINATION_FORM, eliminationDonneesListform);
        EliminationDonneesList edList = ReponsesUIServiceLocator
            .getArchiveUIService()
            .getEliminationDonneesList(context);

        template.getData().put(STTemplateConstants.RESULT_LIST, edList);
        template.getData().put(STTemplateConstants.LST_COLONNES, edList.getListeColonnes());
        template.getData().put(STTemplateConstants.RESULT_FORM, eliminationDonneesListform);
        template.getData().put(STTemplateConstants.DATA_URL, DATA_URL);
        template.getData().put(STTemplateConstants.DATA_AJAX_URL, "/ajax/eliminationDonnees");

        return template;
    }

    @Path("/consultation")
    public Object getListeConsult() {
        return newObject("EliminationDonneesConsultationAjax");
    }

    @Path("abandonner")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response abandonnerListe(@FormParam("id") String idListeElimination) {
        verifyAction(ReponsesActionEnum.ADMIN_PARAM_ARCHIVAGE, DATA_URL + "/abandonner");

        context.setCurrentDocument(idListeElimination);
        ReponsesUIServiceLocator.getArchiveUIService().abandonListeElimination(context);
        context
            .getMessageQueue()
            .addMessageToQueue(
                ResourceHelper.getString("eliminationDonnees.action.abandonner.success"),
                AlertType.TOAST_SUCCESS
            );
        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("editerBordereau")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response editerBordereau(@FormParam("id") String idListeElimination) {
        verifyAction(ReponsesActionEnum.ADMIN_PARAM_ARCHIVAGE, DATA_URL + "/editerBordereau");

        context.setCurrentDocument(idListeElimination);
        String fileName = ReponsesUIServiceLocator.getArchiveUIService().editerBordereau(context);
        context
            .getMessageQueue()
            .addMessageToQueue(
                ResourceHelper.getString("eliminationDonnees.action.editerBordereau.success"),
                AlertType.TOAST_SUCCESS
            );
        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue(), "/consulterpdf/" + fileName).build();
    }

    @Path("eliminer")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminerListe(@FormParam("id") String idListeElimination) {
        verifyAction(ReponsesActionEnum.ADMIN_PARAM_ARCHIVAGE, DATA_URL + "/eliminer");

        context.setCurrentDocument(idListeElimination);
        ReponsesUIServiceLocator.getArchiveUIService().eliminerListe(context);
        context
            .getMessageQueue()
            .addMessageToQueue(
                ResourceHelper.getString("eliminationDonnees.action.eliminer.success"),
                AlertType.TOAST_SUCCESS
            );
        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("vider")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response viderListe(@FormParam("id") String idListeElimination) {
        verifyAction(ReponsesActionEnum.ADMIN_PARAM_ARCHIVAGE, DATA_URL + "/vider");

        context.setCurrentDocument(idListeElimination);
        ReponsesUIServiceLocator.getArchiveUIService().viderListeElimination(context);
        context
            .getMessageQueue()
            .addMessageToQueue(
                ResourceHelper.getString("eliminationDonnees.action.vider.success"),
                AlertType.TOAST_SUCCESS
            );
        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("retirer")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response retirerDossiers(@FormParam("id[]") List<String> idDossiers) {
        verifyAction(ReponsesActionEnum.ADMIN_PARAM_ARCHIVAGE, DATA_URL + "/retirer");

        context.putInContextData(DOSSIER_IDS, idDossiers);
        ReponsesUIServiceLocator.getArchiveUIService().retirerDossierListeElimination(context);
        context
            .getMessageQueue()
            .addMessageToQueue(
                ResourceHelper.getString("eliminationDonnees.action.retirer.success"),
                AlertType.TOAST_SUCCESS
            );
        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Override
    protected ThTemplate getMyTemplate() {
        ThTemplate myTemplate = new AjaxLayoutThTemplate("fragments/table/tableEliminationDonnees", getMyContext());
        myTemplate.setData(new HashMap<>());
        return myTemplate;
    }
}
