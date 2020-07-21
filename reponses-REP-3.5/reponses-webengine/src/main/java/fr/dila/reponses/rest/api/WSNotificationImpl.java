package fr.dila.reponses.rest.api;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.DefaultObject;

import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.rest.helper.VersionHelper;
import fr.dila.reponses.rest.management.NotificationDelegate;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.rest.api.WSNotification;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.reponses.EnvoyerNotificationRequest;
import fr.sword.xsd.reponses.EnvoyerNotificationResponse;
import fr.sword.xsd.reponses.TraitementStatut;

@WebObject(type = WSNotification.SERVICE_NAME)
@Produces("text/xml;charset=UTF-8")
public class WSNotificationImpl extends DefaultObject implements WSNotification {

	private static final Logger	LOGGER	= Logger.getLogger(WSNotification.class);

	@GET
	@Path(WSNotification.METHOD_NAME_TEST)
	@Produces("text/plain")
	public String test() {
		return SERVICE_NAME;
	}

	@GET
	@Path(WSNotification.METHOD_NAME_VERSION)
	public VersionResponse version() throws Exception {
		return VersionHelper.getVersionForWSNotification();
	}

	@POST
	@Path(WSNotification.METHOD_NAME_ENVOYER_NOTIFICATION)
	public EnvoyerNotificationResponse envoyerNotification(EnvoyerNotificationRequest request) throws Exception {
		long startTime = System.nanoTime();
		EnvoyerNotificationResponse response = new EnvoyerNotificationResponse();
		if (isPasswordOutdated()) {
			response.setStatutTraitement(TraitementStatut.KO);
		} else if (isPasswordTemporary()) {
			response.setStatutTraitement(TraitementStatut.KO);
		} else {
			try {
				CoreSession documentManager = ctx.getCoreSession();
				NotificationDelegate delegate = new NotificationDelegate(documentManager);
				response = delegate.notify(request);
			} catch (Exception e) {
				LOGGER.info(JaxBHelper.logInWsTransaction(WSNotification.SERVICE_NAME,
						WSNotification.METHOD_NAME_ENVOYER_NOTIFICATION, ctx.getCoreSession().getPrincipal().getName(),
						request, EnvoyerNotificationRequest.class, null, null));
				throw e;
			}
		}
		long duration = (System.nanoTime() - startTime) / 1000000;
		LOGGER.info(JaxBHelper.logInWsTransaction(WSNotification.SERVICE_NAME,
				WSNotification.METHOD_NAME_ENVOYER_NOTIFICATION, ctx.getCoreSession().getPrincipal().getName(),
				request, EnvoyerNotificationRequest.class, response, EnvoyerNotificationResponse.class)
				+ "---DURATION : " + duration + "ms ---\n");
		return response;
	}

	private boolean isPasswordOutdated() {
		CoreSession session = ctx.getCoreSession();
		try {
			if (ReponsesServiceLocator.getProfilUtilisateurService().isUserPasswordOutdated(session,
					session.getPrincipal().getName())) {
				STServiceLocator.getSTUserService().forceChangeOutdatedPassword(session.getPrincipal().getName());
				return true;
			}
			return false;
		} catch (ClientException e) {
			LOGGER.warn("Impossible de vérifier la validité de la date de changement de mot de passe", e);
			return false;
		}
	}

	private boolean isPasswordTemporary() {
		CoreSession session = ctx.getCoreSession();
		try {
			if (STServiceLocator.getSTUserService().isUserPasswordResetNeeded(session.getPrincipal().getName())) {
				return true;
			}
			return false;
		} catch (ClientException e) {
			LOGGER.warn("Impossible de vérifier si le mot de passe est temporaire", e);
			return false;
		}

	}

}
