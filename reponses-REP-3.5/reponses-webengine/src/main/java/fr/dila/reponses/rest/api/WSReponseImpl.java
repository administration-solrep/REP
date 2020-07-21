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
import fr.dila.reponses.rest.management.ReponseDelegate;
import fr.dila.st.api.constant.STProfilUtilisateurConstants;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.reponses.ChercherErrataReponsesRequest;
import fr.sword.xsd.reponses.ChercherErrataReponsesResponse;
import fr.sword.xsd.reponses.ChercherReponsesRequest;
import fr.sword.xsd.reponses.ChercherReponsesResponse;
import fr.sword.xsd.reponses.EnvoyerReponseErrataRequest;
import fr.sword.xsd.reponses.EnvoyerReponseErrataResponse;
import fr.sword.xsd.reponses.EnvoyerReponsesRequest;
import fr.sword.xsd.reponses.EnvoyerReponsesResponse;
import fr.sword.xsd.reponses.ResultatTraitement;
import fr.sword.xsd.reponses.TraitementStatut;

@WebObject(type = WSReponse.SERVICE_NAME)
@Produces("text/xml;charset=UTF-8")
public class WSReponseImpl extends DefaultObject implements WSReponse {

	private static final Logger	LOGGER	= Logger.getLogger(WSReponse.class);

	@Override
	@GET
	@Path(WSReponse.METHOD_NAME_TEST)
	@Produces("text/plain")
	public String test() {
		return SERVICE_NAME;
	}

	@Override
	@GET
	@Path(WSReponse.METHOD_NAME_VERSION)
	public VersionResponse version() throws Exception {
		return VersionHelper.getVersionForWSReponse();
	}

	@Override
	@POST
	@Path(WSReponse.METHOD_NAME_CHERCHER_REPONSES)
	public ChercherReponsesResponse chercherReponses(ChercherReponsesRequest request) throws Exception {
		long startTime = System.nanoTime();
		ChercherReponsesResponse response = new ChercherReponsesResponse();
		if (isPasswordOutdated()) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
		} else if (isPasswordTemporary()) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
		} else {
			try {
				ReponseDelegate delegate = new ReponseDelegate(ctx.getCoreSession());
				response = delegate.chercherReponses(request);
			} catch (Exception e) {
				if (response.getJetonReponses() == null) {
					if (request.getJeton() == null) {
						response.setJetonReponses("");
					} else {
						response.setJetonReponses(request.getJeton());
					}
				}
				response.setStatut(TraitementStatut.KO);
				response.setMessageErreur(e.getMessage());
				LOGGER.error("Erreur dans la recherche de réponses", e);
			}
		}
		long duration = (System.nanoTime() - startTime) / 1000000;
		LOGGER.info(JaxBHelper.logInWsTransaction(WSQuestion.SERVICE_NAME, WSReponse.METHOD_NAME_CHERCHER_REPONSES, ctx
				.getCoreSession().getPrincipal().getName(), request, ChercherReponsesRequest.class, response,
				ChercherReponsesResponse.class)
				+ "---DURATION : " + duration + "ms ---\n");
		return response;
	}

	@Override
	@POST
	@Path(WSReponse.METHOD_NAME_CHERCHER_ERRATA_REPONSES)
	public ChercherErrataReponsesResponse chercherErrataReponses(ChercherErrataReponsesRequest request)
			throws Exception {
		long startTime = System.nanoTime();
		ChercherErrataReponsesResponse response = new ChercherErrataReponsesResponse();
		if (isPasswordOutdated()) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
		} else if (isPasswordTemporary()) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
		} else {
			try {
				ReponseDelegate delegate = new ReponseDelegate(ctx.getCoreSession());
				response = delegate.chercherErrataReponses(request);
			} catch (Exception e) {
				if (response.getJetonErrata() == null) {
					if (request.getJeton() == null) {
						response.setJetonErrata("");
					} else {
						response.setJetonErrata(request.getJeton());
					}
				}
				response.setStatut(TraitementStatut.KO);
				response.setMessageErreur(e.getMessage());
				LOGGER.error("Erreur dans la recherche d'errata de réponses", e);
			}
		}
		long duration = (System.nanoTime() - startTime) / 1000000;
		LOGGER.info(JaxBHelper.logInWsTransaction(WSQuestion.SERVICE_NAME,
				WSReponse.METHOD_NAME_CHERCHER_ERRATA_REPONSES, ctx.getCoreSession().getPrincipal().getName(), request,
				ChercherErrataReponsesRequest.class, response, ChercherErrataReponsesResponse.class)
				+ "---DURATION : " + duration + "ms ---\n");
		return response;
	}

	@Override
	@POST
	@Path(WSReponse.METHOD_NAME_ENVOYER_REPONSES)
	public EnvoyerReponsesResponse envoyerReponses(EnvoyerReponsesRequest request) throws Exception {
		long startTime = System.nanoTime();
		EnvoyerReponsesResponse response = new EnvoyerReponsesResponse();
		if (isPasswordOutdated()) {
			ResultatTraitement rt = new ResultatTraitement();
			rt.setStatut(TraitementStatut.KO);
			rt.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
			response.getResultatTraitement().add(rt);
		} else if (isPasswordTemporary()) {
			ResultatTraitement rt = new ResultatTraitement();
			rt.setStatut(TraitementStatut.KO);
			rt.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
			response.getResultatTraitement().add(rt);
		} else {
			try {
				ReponseDelegate delegate = new ReponseDelegate(ctx.getCoreSession());
				response = delegate.envoyerReponses(request);
			} catch (Exception e) {
				ResultatTraitement rt = new ResultatTraitement();
				rt.setStatut(TraitementStatut.KO);
				rt.setMessageErreur(e.getMessage());
				response.getResultatTraitement().add(rt);
				LOGGER.error("Erreur dans l'envoi de réponses", e);
			}
		}
		long duration = (System.nanoTime() - startTime) / 1000000;
		LOGGER.info(JaxBHelper.logInWsTransaction(WSQuestion.SERVICE_NAME, WSReponse.METHOD_NAME_ENVOYER_REPONSES, ctx
				.getCoreSession().getPrincipal().getName(), request, EnvoyerReponsesRequest.class, response,
				EnvoyerReponsesResponse.class)
				+ "---DURATION : " + duration + "ms ---\n");
		return response;
	}

	@Override
	@POST
	@Path(WSReponse.METHOD_NAME_ENVOYER_REPONSE_ERRATA)
	public EnvoyerReponseErrataResponse envoyerReponseErrata(EnvoyerReponseErrataRequest request) throws Exception {
		long startTime = System.nanoTime();
		EnvoyerReponseErrataResponse response = new EnvoyerReponseErrataResponse();
		if (isPasswordOutdated()) {
			ResultatTraitement rt = new ResultatTraitement();
			rt.setStatut(TraitementStatut.KO);
			rt.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
			response.getResultatTraitement().add(rt);
		} else if (isPasswordTemporary()) {
			ResultatTraitement rt = new ResultatTraitement();
			rt.setStatut(TraitementStatut.KO);
			rt.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
			response.getResultatTraitement().add(rt);
		} else {
			try {
				ReponseDelegate delegate = new ReponseDelegate(ctx.getCoreSession());
				response = delegate.envoyerReponseErrata(request);
			} catch (Exception e) {
				ResultatTraitement rt = new ResultatTraitement();
				rt.setStatut(TraitementStatut.KO);
				rt.setMessageErreur(e.getMessage());
				response.getResultatTraitement().add(rt);
				LOGGER.error("Erreur dans l'envoi d'errata de la réponse", e);
			}
		}
		long duration = (System.nanoTime() - startTime) / 1000000;
		LOGGER.info(JaxBHelper.logInWsTransaction(WSQuestion.SERVICE_NAME,
				WSReponse.METHOD_NAME_ENVOYER_REPONSE_ERRATA, ctx.getCoreSession().getPrincipal().getName(), request,
				EnvoyerReponseErrataRequest.class, response, EnvoyerReponseErrataResponse.class)
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
