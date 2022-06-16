package fr.dila.reponses.ui.jaxrs.webobject.ajax;

import fr.dila.ss.ui.bean.AlerteForm;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.jaxrs.webobject.ajax.SSSuiviAlerteAjax;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.model.SpecificContext;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AjaxAlertes")
public class ReponsesSuiviAlerteAjax extends SSSuiviAlerteAjax {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("sauvegarder")
    public Response sauvegarderAlerte(@SwBeanParam AlerteForm form) {
        context.putInContextData(SSContextDataKey.ALERTE_FORM, form);
        context.setCurrentDocument(form.getId());

        SSActionsServiceLocator.getAlertActionService().saveAlert(context);

        return getJsonResponseWithMessagesInSessionIfSuccess();
    }
}
