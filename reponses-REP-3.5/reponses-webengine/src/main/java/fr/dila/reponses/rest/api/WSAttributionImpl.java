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
import fr.dila.reponses.rest.management.AttributionDelegate;
import fr.dila.st.api.constant.STProfilUtilisateurConstants;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.reponses.ChercherAttributionsDateRequest;
import fr.sword.xsd.reponses.ChercherAttributionsDateResponse;
import fr.sword.xsd.reponses.ChercherAttributionsRequest;
import fr.sword.xsd.reponses.ChercherAttributionsResponse;
import fr.sword.xsd.reponses.ChercherLegislaturesResponse;
import fr.sword.xsd.reponses.ChercherMembresGouvernementRequest;
import fr.sword.xsd.reponses.ChercherMembresGouvernementResponse;
import fr.sword.xsd.reponses.TraitementStatut;

@WebObject(type = WSAttribution.SERVICE_NAME)
@Produces("text/xml;charset=UTF-8")
public class WSAttributionImpl extends DefaultObject implements WSAttribution {

	private static final Logger	LOGGER	= Logger.getLogger(WSAttribution.class);

	@Override
	@GET
	@Path(WSAttribution.METHOD_NAME_TEST)
	@Produces("text/plain")
	public String test() throws Exception {
		return SERVICE_NAME;
	}

	@Override
	@GET
	@Path(WSAttribution.METHOD_NAME_VERSION)
	public VersionResponse version() throws Exception {
		return VersionHelper.getVersionForWSAttribution();
	}

	@Override
	@POST
	@Path(WSAttribution.METHOD_NAME_CHERCHER_ATTRIBUTIONS)
	public ChercherAttributionsResponse chercherAttributions(ChercherAttributionsRequest request) throws Exception {
		long startTime = System.nanoTime();
		ChercherAttributionsResponse response = new ChercherAttributionsResponse();
		if (isPasswordOutdated()) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
		} else if (isPasswordTemporary()) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
		} else {
			// Delegation du traitement
			AttributionDelegate delegate = new AttributionDelegate(ctx.getCoreSession());
			try {
				response = delegate.chercherAttributions(request);
			} catch (Exception e) {
				if (response.getJetonAttributions() == null) {
					if (request.getJeton() == null) {
						response.setJetonAttributions("");
					} else {
						response.setJetonAttributions(request.getJeton());
					}
				}
				response.setStatut(TraitementStatut.KO);
				response.setMessageErreur(e.getMessage());
				LOGGER.error("Erreur dans la recherche d'attributions", e);
			}
		}
		long duration = (System.nanoTime() - startTime) / 1000000;
		LOGGER.info(JaxBHelper.logInWsTransaction(WSAttribution.SERVICE_NAME,
				WSAttribution.METHOD_NAME_CHERCHER_ATTRIBUTIONS, ctx.getCoreSession().getPrincipal().getName(),
				request, ChercherAttributionsRequest.class, response, ChercherAttributionsResponse.class)
				+ "---DURATION : " + duration + "ms ---\n");

		return response;
	}

	@Override
	@POST
	@Path(WSAttribution.METHOD_NAME_CHERCHER_ATTRIBUTIONS_DATE)
	public ChercherAttributionsDateResponse chercherAttributionsDate(ChercherAttributionsDateRequest request)
			throws Exception {
		long startTime = System.nanoTime();
		ChercherAttributionsDateResponse response = new ChercherAttributionsDateResponse();
		if (isPasswordOutdated()) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
		} else if (isPasswordTemporary()) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
		} else {
			// Delegation du traitement
			AttributionDelegate delegate = new AttributionDelegate(ctx.getCoreSession());
			try {
				response = delegate.chercherAttributionsDate(request);
			} catch (Exception e) {
				response.setStatut(TraitementStatut.KO);
				response.setMessageErreur(e.getMessage());
				LOGGER.error("Erreur dans la recherche d'attributions", e);
			}
		}
		long duration = (System.nanoTime() - startTime) / 1000000;
		LOGGER.info(JaxBHelper.logInWsTransaction(WSAttribution.SERVICE_NAME,
				WSAttribution.METHOD_NAME_CHERCHER_ATTRIBUTIONS_DATE, ctx.getCoreSession().getPrincipal().getName(),
				request, ChercherAttributionsDateRequest.class, response, ChercherAttributionsDateResponse.class)
				+ "---DURATION : " + duration + "ms ---\n");

		return response;
	}

	@Override
	@POST
	@Path(WSAttribution.METHOD_NAME_CHERCHER_MEMBRES_GOUVERNEMENT)
	public ChercherMembresGouvernementResponse chercherMembresGouvernement(ChercherMembresGouvernementRequest request)
			throws Exception {
		long startTime = System.nanoTime();
		ChercherMembresGouvernementResponse response = new ChercherMembresGouvernementResponse();
		if (isPasswordOutdated()) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
		} else if (isPasswordTemporary()) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
		} else {
			try {
				AttributionDelegate delegate = new AttributionDelegate(ctx.getCoreSession());
				response = delegate.chercherMembresGouvernement(request);
			} catch (Exception e) {
				response.setStatut(TraitementStatut.KO);
				response.setMessageErreur(e.getMessage());
				LOGGER.error("Erreur dans la recherche de membres du gouvernement", e);
			}
		}
		long duration = (System.nanoTime() - startTime) / 1000000;
		LOGGER.info(JaxBHelper.logInWsTransaction(WSAttribution.SERVICE_NAME,
				WSAttribution.METHOD_NAME_CHERCHER_MEMBRES_GOUVERNEMENT, ctx.getCoreSession().getPrincipal().getName(),
				request, ChercherMembresGouvernementRequest.class, response, ChercherMembresGouvernementResponse.class)
				+ "---DURATION : " + duration + "ms ---\n");

		return response;
	}

	@Override
	@POST
	@Path(WSAttribution.METHOD_NAME_CHERCHER_LEGISLATURES)
	public ChercherLegislaturesResponse chercherLegislatures() throws Exception {
		long startTime = System.nanoTime();
		ChercherLegislaturesResponse response = new ChercherLegislaturesResponse();
		if (isPasswordOutdated()) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
		} else if (isPasswordTemporary()) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
		} else {
			// Delegation du traitement
			try {
				AttributionDelegate delegate = new AttributionDelegate(ctx.getCoreSession());
				response = delegate.chercherLegislatures();
			} catch (Exception e) {
				response.setStatut(TraitementStatut.KO);
				response.setMessageErreur(e.getMessage());
				LOGGER.error("Erreur dans la recherche de législatures", e);
			}
		}
		long duration = (System.nanoTime() - startTime) / 1000000;
		LOGGER.info(JaxBHelper.logInWsTransaction(WSAttribution.SERVICE_NAME,
				WSAttribution.METHOD_NAME_CHERCHER_LEGISLATURES, ctx.getCoreSession().getPrincipal().getName(), null,
				null, response, ChercherLegislaturesResponse.class)
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
