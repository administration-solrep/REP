package fr.dila.reponses.ui.jaxrs.webobject.ajax.dossier;

import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.services.actions.ReponsesActionsServiceLocator;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AjaxAllotissement")
public class ReponsesDossierAllotissementAjax extends SolonWebObject {

    @Path("ajouter")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addToLot(
        @FormParam("searchedQuestion") String searchedQuestion,
        @FormParam("dossierId") String dossierId
    ) {
        context.setCurrentDocument(dossierId);
        context.putInContextData(ReponsesContextDataKey.SEARCHED_QUESTION, searchedQuestion);

        ReponsesActionsServiceLocator.getAllotissementActionService().addToLot(context);

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("retirer")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeLot(
        @FormParam("dossierId") String dossierId,
        @FormParam("selectedFolders[]") List<String> selectedFolders
    ) {
        context.setCurrentDocument(dossierId);
        context.putInContextData(ReponsesContextDataKey.SELECTED_DOSSIERS, selectedFolders);

        ReponsesActionsServiceLocator.getAllotissementActionService().removeFromLot(context);

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("creer")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createLot(
        @FormParam("dossierDirecteurId") String dossierDirecteurId,
        @FormParam("selectedFolders[]") List<String> selectedFolders
    ) {
        context.putInContextData(ReponsesContextDataKey.QUESTION_DIR, dossierDirecteurId);
        context.putInContextData(ReponsesContextDataKey.SELECTED_DOSSIERS, selectedFolders);

        ReponsesActionsServiceLocator.getAllotissementActionService().createOrUpdateLot(context);

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }
}
