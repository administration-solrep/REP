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
import fr.dila.reponses.rest.management.ControleDelegate;
import fr.dila.st.api.constant.STProfilUtilisateurConstants;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.reponses.ChercherRetourPublicationRequest;
import fr.sword.xsd.reponses.ChercherRetourPublicationResponse;
import fr.sword.xsd.reponses.ControlePublicationRequest;
import fr.sword.xsd.reponses.ControlePublicationResponse;
import fr.sword.xsd.reponses.ResultatControlePublication;
import fr.sword.xsd.reponses.TraitementStatut;

@WebObject(type = WSControle.SERVICE_NAME)
@Produces("text/xml;charset=UTF-8")
public class WSControleImpl extends DefaultObject implements WSControle {

	private static final Logger	LOGGER	= Logger.getLogger(WSControle.class);

	@GET
	@Path(WSControle.METHOD_NAME_TEST)
	@Produces("text/plain")
	public String test() {
		return SERVICE_NAME;
	}

	@GET
	@Path(WSControle.METHOD_NAME_VERSION)
	public VersionResponse version() throws Exception {
		return VersionHelper.getVersionForWSControle();
	}

	@POST
	@Path(WSControle.METHOD_NAME_CONTROLE_PUBLICATION)
	public ControlePublicationResponse controlePublication(ControlePublicationRequest request) throws Exception {
		long startTime = System.nanoTime();
		ControlePublicationResponse response = new ControlePublicationResponse();
		if (isPasswordOutdated()) {
			ResultatControlePublication rt = new ResultatControlePublication();
			rt.setStatut(TraitementStatut.KO);
			rt.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
			response.getResultatControleQuestion().add(rt);
		} else if (isPasswordTemporary()) {
			ResultatControlePublication rt = new ResultatControlePublication();
			rt.setStatut(TraitementStatut.KO);
			rt.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
			response.getResultatControleQuestion().add(rt);
		} else {
			try {
				ControleDelegate delegate = new ControleDelegate(ctx.getCoreSession());
				response = delegate.controlePublication(request);

			} catch (Exception e) {
				LOGGER.info(JaxBHelper.logInWsTransaction(WSControle.SERVICE_NAME,
						WSControle.METHOD_NAME_CONTROLE_PUBLICATION, ctx.getCoreSession().getPrincipal().getName(),
						request, ControlePublicationRequest.class, null, null));
				throw e;
			}
		}
		long duration = (System.nanoTime() - startTime) / 1000000;
		LOGGER.info(JaxBHelper.logInWsTransaction(WSControle.SERVICE_NAME, WSControle.METHOD_NAME_CONTROLE_PUBLICATION,
				ctx.getCoreSession().getPrincipal().getName(), request, ControlePublicationRequest.class, response,
				ControlePublicationResponse.class)
				+ "---DURATION : " + duration + "ms ---\n");
		return response;
	}

	@POST
	@Path(WSControle.METHOD_NAME_CHERCHER_RETOUR_PUBLICATION)
	public ChercherRetourPublicationResponse chercherRetourPublication(ChercherRetourPublicationRequest request)
			throws Exception {
		long startTime = System.nanoTime();
		ChercherRetourPublicationResponse response = new ChercherRetourPublicationResponse();
		if (isPasswordOutdated()) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
		} else if (isPasswordTemporary()) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
		} else {
			try {
				ControleDelegate delegate = new ControleDelegate(ctx.getCoreSession());
				response = delegate.chercherRetourPublication(request);
			} catch (Exception e) {
				LOGGER.info(JaxBHelper.logInWsTransaction(WSControle.SERVICE_NAME,
						WSControle.METHOD_NAME_CHERCHER_RETOUR_PUBLICATION, ctx.getCoreSession().getPrincipal()
								.getName(), request, ChercherRetourPublicationRequest.class, null, null));
				throw e;
			}
		}
		long duration = (System.nanoTime() - startTime) / 1000000;
		LOGGER.info(JaxBHelper.logInWsTransaction(WSControle.SERVICE_NAME,
				WSControle.METHOD_NAME_CHERCHER_RETOUR_PUBLICATION, ctx.getCoreSession().getPrincipal().getName(),
				request, ChercherRetourPublicationRequest.class, response, ChercherRetourPublicationResponse.class)
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
