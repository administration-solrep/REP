package fr.dila.reponses.ui.jaxrs.webobject.ajax;

import fr.dila.reponses.ui.enums.ReponsesContextDataKey;
import fr.dila.reponses.ui.services.ReponsesModeleFdrFicheUIService;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.th.bean.ReponsesModeleFdrForm;
import fr.dila.ss.ui.jaxrs.webobject.ajax.modele.SSModeleFeuilleRouteAjax;
import fr.dila.ss.ui.services.actions.ModeleFeuilleRouteActionService;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "ModeleFeuilleRouteAjax")
public class ReponsesModeleFeuilleRouteAjax extends SSModeleFeuilleRouteAjax {

    @Path("/modele/valider")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response validerModele(
        @SwBeanParam ReponsesModeleFdrForm form,
        @FormParam("sauvegarder") boolean sauvegarder
    ) {
        ModeleFeuilleRouteActionService modeleAction = SSActionsServiceLocator.getModeleFeuilleRouteActionService();
        ReponsesModeleFdrFicheUIService modeleFDRFicheUIService = ReponsesUIServiceLocator.getReponsesModeleFdrFicheUIService();
        context.setCurrentDocument(form.getId());
        context.putInContextData(ReponsesContextDataKey.MODELE_FDR_FORM, form);

        if (modeleAction.canValidateRoute(context)) {
            if (sauvegarder) {
                modeleFDRFicheUIService.updateModele(context, form);
            }
            modeleAction.validateRouteModel(context);
            modeleAction.libererVerrou(context);

            if (CollectionUtils.isEmpty(context.getMessageQueue().getErrorQueue())) {
                addMessageQueueInSession();
            }
        }

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }
}
