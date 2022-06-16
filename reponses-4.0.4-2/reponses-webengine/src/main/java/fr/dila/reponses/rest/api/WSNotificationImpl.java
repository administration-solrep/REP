package fr.dila.reponses.rest.api;

import fr.dila.reponses.rest.helper.PasswordHelper;
import fr.dila.reponses.rest.helper.VersionHelper;
import fr.dila.reponses.rest.management.NotificationDelegate;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.core.logger.AbstractLogger;
import fr.dila.st.rest.api.WSNotification;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.reponses.EnvoyerNotificationRequest;
import fr.sword.xsd.reponses.EnvoyerNotificationResponse;
import fr.sword.xsd.reponses.TraitementStatut;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.DefaultObject;

@WebObject(type = WSNotification.SERVICE_NAME)
@Produces("text/xml;charset=UTF-8")
public class WSNotificationImpl extends DefaultObject implements WSNotification {
    private static final Logger LOGGER = LogManager.getLogger(WSNotification.class);

    @Override
    @GET
    @Path(WSNotification.METHOD_NAME_TEST)
    @Produces("text/plain")
    public String test() {
        return SERVICE_NAME;
    }

    @Override
    @GET
    @Path(WSNotification.METHOD_NAME_VERSION)
    public VersionResponse version() throws Exception {
        return VersionHelper.getVersionForWSNotification();
    }

    @Override
    @POST
    @Path(WSNotification.METHOD_NAME_ENVOYER_NOTIFICATION)
    public EnvoyerNotificationResponse envoyerNotification(EnvoyerNotificationRequest request) throws Exception {
        long startTime = System.nanoTime();
        EnvoyerNotificationResponse response = new EnvoyerNotificationResponse();
        if (isPasswordOutdated() || isPasswordTemporary()) {
            response.setStatutTraitement(TraitementStatut.KO);
        } else {
            try {
                CoreSession documentManager = ctx.getCoreSession();
                NotificationDelegate delegate = new NotificationDelegate(documentManager);
                response = delegate.notify(request);
            } catch (Exception e) {
                LOGGER.info(
                    JaxBHelper.logInWsTransaction(
                        WSNotification.SERVICE_NAME,
                        WSNotification.METHOD_NAME_ENVOYER_NOTIFICATION,
                        ctx.getCoreSession().getPrincipal().getName(),
                        request,
                        EnvoyerNotificationRequest.class,
                        null,
                        null
                    )
                );
                throw e;
            }
        }
        if (LOGGER.isInfoEnabled()) {
            long duration = AbstractLogger.getDurationInMs(startTime);
            LOGGER.info(
                STConstant.LOG_MSG_DURATION,
                JaxBHelper.logInWsTransaction(
                    WSNotification.SERVICE_NAME,
                    WSNotification.METHOD_NAME_ENVOYER_NOTIFICATION,
                    ctx.getCoreSession().getPrincipal().getName(),
                    request,
                    EnvoyerNotificationRequest.class,
                    response,
                    EnvoyerNotificationResponse.class
                ),
                duration
            );
        }
        return response;
    }

    private boolean isPasswordOutdated() {
        CoreSession session = ctx.getCoreSession();
        return PasswordHelper.isPasswordOutdated(session, LOGGER);
    }

    private boolean isPasswordTemporary() {
        CoreSession session = ctx.getCoreSession();
        return PasswordHelper.isPasswordTemporary(session, LOGGER);
    }
}
