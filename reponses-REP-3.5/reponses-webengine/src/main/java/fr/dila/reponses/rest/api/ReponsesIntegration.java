package fr.dila.reponses.rest.api;

import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.reporting.api.ReportInstance;
import org.nuxeo.ecm.platform.reporting.api.ReportService;
import org.nuxeo.ecm.webengine.WebException;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.exceptions.WebSecurityException;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;
import org.nuxeo.runtime.api.Framework;

import com.sun.jersey.api.NotFoundException;

import fr.dila.st.core.util.StringUtil;
import fr.dila.st.rest.api.WSNotification;

/**
 * Racine du module des Web Services pour Reponses.
 * 
 * @author sly
 */
@Path("reponses")
@WebObject(type = "ReponsesIntegration")
public class ReponsesIntegration extends ModuleRoot {

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

    @Path(value = "reports/{reportIdOrName}")
    public Object getReport(@PathParam("reportIdOrName") String reportIdOrName) throws ClientException {
        ReportInstance report = findReport(reportIdOrName);
        if (report == null) {
            return Response.status(404).build();
        }
        return ctx.newObject("report", report);
    }

    protected ReportInstance findReport(String idOrKey) throws ClientException {

        boolean useIdResolution = true;
        try {
            UUID.fromString(idOrKey);
        } catch (IllegalArgumentException e) {
            useIdResolution = false;
        }

        CoreSession session = getContext().getCoreSession();
        if (useIdResolution) {
            IdRef ref = new IdRef(idOrKey);
            if (!session.exists(ref)) {
                return null;
            }
            DocumentModel reportDoc = session.getDocument(ref);
            return reportDoc.getAdapter(ReportInstance.class);
        } else {
            ReportService rs = Framework.getLocalService(ReportService.class);
            return rs.getReportInstanceByKey(session, idOrKey);
        }
    }

    @GET
    @Produces("text/html;charset=UTF-8")
    public Object doGet() {
        return getView("index");
    }

    /**
     * Handle Errors
     * 
     * @see org.nuxeo.ecm.webengine.model.impl.ModuleRoot#handleError(javax.ws.rs.WebApplicationException)
     */
    @Override
    public Object handleError(WebApplicationException e) {
        if (e instanceof WebException) {
            Throwable cause = e.getCause();
            if (cause instanceof WebSecurityException) {
                return Response.status(((WebSecurityException) cause).getStatusCode())
                        .entity("Vous n'avez pas les droits nécessaires pour accèder à ce webservice : " + cause.toString()).build();
            }
            if (cause instanceof NotFoundException) {
                return Response.status(Status.NOT_FOUND).entity("application reponses : Webservice introuvable : " + cause.toString()).build();
            }

        }

        return Response.status(Status.BAD_REQUEST).entity("Erreur : " + StringUtil.getStackTrace(e)).build();
    }
}
