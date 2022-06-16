package fr.dila.reponses.ui.jaxrs.webobject.ajax;

import fr.dila.reponses.ui.enums.ReponsesActionEnum;
import fr.dila.reponses.ui.services.ReponsesUIServiceLocator;
import fr.dila.reponses.ui.services.ReponsesUtilisateursUIService;
import fr.dila.st.core.exception.STAuthorizationException;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.pages.user.AbstractUserObject;
import fr.dila.st.ui.th.bean.UserForm;
import fr.dila.st.ui.th.model.SpecificContext;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "UtilisateursAjax")
public class ReponsesUtilisateursAjax extends AbstractUserObject {

    @POST
    @Path("/temporaire/creation")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTempUser(@SwBeanParam UserForm userForm) {
        ReponsesUtilisateursUIService reponsesUtilisateursUIService = ReponsesUIServiceLocator.getReponsesUtilisateursUIService();
        userForm.setTemporaire("oui");
        userForm.setOccasionnel(true);

        if (context.getAction(ReponsesActionEnum.UTILISATEUR_CREATE_USER) == null) {
            throw new STAuthorizationException("action création d'un nouvel utilisateur temporaire");
        }

        context.putInContextData(STContextDataKey.USER_FORM, userForm);
        reponsesUtilisateursUIService.createOccasionalUser(context);
        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }
}
