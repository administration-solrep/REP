package fr.dila.reponses.rest.api;

import com.sun.jersey.api.NotFoundException;
import fr.dila.st.core.util.StringHelper;
import fr.dila.st.rest.api.WSNotification;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.exceptions.WebSecurityException;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;

/**
 * Racine du module des Web Services pour Reponses.
 *
 * @author sly
 */
@Path("reponses")
@WebObject(type = "ReponsesIntegration")
public class ReponsesIntegration extends ModuleRoot {

    @GET
    @Path("ping")
    public String getPong() {
        return "pong";
    }

    @GET
    @Produces("text/html;charset=UTF-8")
    public Object doGet() {
        return getView("index");
    }

    @Path(WSNotification.SERVICE_NAME)
    public WSNotificationImpl getWSNotification() {
        return (WSNotificationImpl) newObject(WSNotification.SERVICE_NAME);
    }

    @Path(WSQuestion.SERVICE_NAME)
    public WSQuestionImpl getWSQuestions() {
        return (WSQuestionImpl) newObject(WSQuestion.SERVICE_NAME);
    }

    @Path(WSReponse.SERVICE_NAME)
    public WSReponseImpl getWSReponses() {
        return (WSReponseImpl) newObject(WSReponse.SERVICE_NAME);
    }

    @Path(WSControle.SERVICE_NAME)
    public WSControleImpl getWSControles() {
        return (WSControleImpl) newObject(WSControle.SERVICE_NAME);
    }

    @Path(WSAttribution.SERVICE_NAME)
    public WSAttributionImpl getWSAttributions() {
        return (WSAttributionImpl) newObject(WSAttribution.SERVICE_NAME);
    }

    /**
     * Handle Errors
     *
     * @see org.nuxeo.ecm.webengine.model.impl.ModuleRoot#handleError(javax.ws.rs.WebApplicationException)
     */
    @Override
    public Object handleError(Throwable e) {
        if (e instanceof NuxeoException) {
            Throwable cause = e.getCause();
            if (cause instanceof WebSecurityException) {
                return Response
                    .status(((WebSecurityException) cause).getStatusCode())
                    .entity("Vous n'avez pas les droits nécessaires pour accèder à ce webservice : " + cause.toString())
                    .build();
            }
            if (cause instanceof NotFoundException) {
                return Response
                    .status(Status.NOT_FOUND)
                    .entity("application reponses : Webservice introuvable : " + cause.toString())
                    .build();
            }
        }

        return Response.status(Status.BAD_REQUEST).entity("Erreur : " + StringHelper.getStackTrace(e)).build();
    }
}
