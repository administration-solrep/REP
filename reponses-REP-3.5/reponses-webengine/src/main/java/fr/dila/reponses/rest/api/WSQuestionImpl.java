package fr.dila.reponses.rest.api;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.usermanager.NuxeoPrincipalImpl;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.DefaultObject;

import com.sun.jersey.spi.inject.Inject;

import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.rest.helper.VersionHelper;
import fr.dila.reponses.rest.management.QuestionDelegate;
import fr.dila.st.api.constant.STProfilUtilisateurConstants;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.reponses.ChangerEtatQuestionsRequest;
import fr.sword.xsd.reponses.ChangerEtatQuestionsResponse;
import fr.sword.xsd.reponses.ChercherChangementDEtatQuestionsRequest;
import fr.sword.xsd.reponses.ChercherChangementDEtatQuestionsResponse;
import fr.sword.xsd.reponses.ChercherErrataQuestionsRequest;
import fr.sword.xsd.reponses.ChercherErrataQuestionsResponse;
import fr.sword.xsd.reponses.ChercherQuestionsRequest;
import fr.sword.xsd.reponses.ChercherQuestionsResponse;
import fr.sword.xsd.reponses.EnvoyerQuestionsErrataRequest;
import fr.sword.xsd.reponses.EnvoyerQuestionsErrataResponse;
import fr.sword.xsd.reponses.EnvoyerQuestionsRequest;
import fr.sword.xsd.reponses.EnvoyerQuestionsResponse;
import fr.sword.xsd.reponses.RechercherDossierRequest;
import fr.sword.xsd.reponses.RechercherDossierResponse;
import fr.sword.xsd.reponses.ResultatTraitement;
import fr.sword.xsd.reponses.TraitementStatut;

@WebObject(type = WSQuestion.SERVICE_NAME)
@Produces("text/xml;charset=UTF-8")
public class WSQuestionImpl extends DefaultObject implements WSQuestion {

	private static final Logger	LOGGER	= Logger.getLogger(WSQuestion.class);

	@Inject
	NuxeoPrincipalImpl			principal;

	@Override
	@GET
	@Path(WSQuestion.METHOD_NAME_TEST)
	@Produces("text/plain")
	public String test() {
		return SERVICE_NAME;
	}

	@Override
	@GET
	@Path(WSQuestion.METHOD_NAME_VERSION)
	public VersionResponse version() throws Exception {
		return VersionHelper.getVersionForWSQuestion();
	}

	@Override
	@POST
	@Path(WSQuestion.METHOD_NAME_CHERCHER_QUESTIONS)
	public ChercherQuestionsResponse chercherQuestions(ChercherQuestionsRequest request) throws Exception {
		long startTime = System.nanoTime();
		ChercherQuestionsResponse response = new ChercherQuestionsResponse();
		if (isPasswordOutdated()) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
		} else if (isPasswordTemporary()) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
		} else {
			try {
				QuestionDelegate delegate = new QuestionDelegate(ctx.getCoreSession());
				response = delegate.chercherQuestions(request);
			} catch (Exception e) {
				if (response.getJetonQuestions() == null) {
					if (request.getJetonQuestions() == null) {
						response.setJetonQuestions("");
					} else {
						response.setJetonQuestions(request.getJetonQuestions());
					}
				}
				response.setStatut(TraitementStatut.KO);
				response.setMessageErreur(e.getMessage());
				LOGGER.error("Erreur dans la recherche de question", e);
			}
		}
		long duration = (System.nanoTime() - startTime) / 1000000;
		LOGGER.info(JaxBHelper.logInWsTransaction(WSQuestion.SERVICE_NAME, WSQuestion.METHOD_NAME_CHERCHER_QUESTIONS,
				ctx.getCoreSession().getPrincipal().getName(), request, ChercherQuestionsRequest.class, response,
				ChercherQuestionsResponse.class)
				+ "---DURATION : " + duration + "ms ---\n");
		return response;
	}

	@Override
	@POST
	@Path(WSQuestion.METHOD_NAME_CHERCHER_ERRATA_QUESTIONS)
	public ChercherErrataQuestionsResponse chercherErrataQuestions(ChercherErrataQuestionsRequest request)
			throws Exception {
		long startTime = System.nanoTime();
		ChercherErrataQuestionsResponse response = new ChercherErrataQuestionsResponse();
		if (isPasswordOutdated()) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
		} else if (isPasswordTemporary()) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
		} else {
			try {
				QuestionDelegate delegate = new QuestionDelegate(ctx.getCoreSession());
				response = delegate.chercherErrataQuestions(request);
			} catch (Exception e) {
				if (response.getJetonErrata() == null) {
					if (request.getJetonErrata() == null) {
						response.setJetonErrata("");
					} else {
						response.setJetonErrata(request.getJetonErrata());
					}
				}
				response.setStatut(TraitementStatut.KO);
				response.setMessageErreur(e.getMessage());
				LOGGER.error("Erreur dans la recherche d'errata de question", e);
			}
		}
		long duration = (System.nanoTime() - startTime) / 1000000;
		LOGGER.info(JaxBHelper.logInWsTransaction(WSQuestion.SERVICE_NAME,
				WSQuestion.METHOD_NAME_CHERCHER_ERRATA_QUESTIONS, ctx.getCoreSession().getPrincipal().getName(),
				request, ChercherErrataQuestionsRequest.class, response, ChercherErrataQuestionsResponse.class)
				+ "---DURATION : " + duration + "ms ---\n");
		return response;
	}

	@Override
	@POST
	@Path(WSQuestion.METHOD_NAME_CHERCHER_CHANGEMENT_ETAT_QUESTIONS)
	public ChercherChangementDEtatQuestionsResponse chercherChangementDEtatQuestions(
			ChercherChangementDEtatQuestionsRequest request) throws Exception {
		long startTime = System.nanoTime();
		ChercherChangementDEtatQuestionsResponse response = new ChercherChangementDEtatQuestionsResponse();
		if (isPasswordOutdated()) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
		} else if (isPasswordTemporary()) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
		} else {
			try {
				QuestionDelegate delegate = new QuestionDelegate(ctx.getCoreSession());
				response = delegate.chercherChangementDEtatQuestions(request);
			} catch (Exception e) {
				if (response.getJetonChangementsEtat() == null) {
					if (request.getJetonChangementsEtat() == null) {
						response.setJetonChangementsEtat("");
					} else {
						response.setJetonChangementsEtat(request.getJetonChangementsEtat());
					}
				}
				response.setStatut(TraitementStatut.KO);
				response.setMessageErreur(e.getMessage());
				LOGGER.error("Erreur dans la recherche de changement d'état de question", e);
			}
		}
		long duration = (System.nanoTime() - startTime) / 1000000;
		LOGGER.info(JaxBHelper.logInWsTransaction(WSQuestion.SERVICE_NAME,
				WSQuestion.METHOD_NAME_CHERCHER_CHANGEMENT_ETAT_QUESTIONS, ctx.getCoreSession().getPrincipal()
						.getName(), request, ChercherChangementDEtatQuestionsRequest.class, response,
				ChercherChangementDEtatQuestionsResponse.class)
				+ "---DURATION : " + duration + "ms ---\n");
		return response;
	}

	@Override
	@POST
	@Path(WSQuestion.METHOD_NAME_ENVOYER_QUESTIONS)
	public EnvoyerQuestionsResponse envoyerQuestions(EnvoyerQuestionsRequest request) throws Exception {
		long startTime = System.nanoTime();
		EnvoyerQuestionsResponse response = new EnvoyerQuestionsResponse();
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
				QuestionDelegate delegate = new QuestionDelegate(ctx.getCoreSession());
				response = delegate.envoyerQuestions(request);
			} catch (Exception e) {
				ResultatTraitement rt = new ResultatTraitement();
				rt.setStatut(TraitementStatut.KO);
				rt.setMessageErreur(e.getMessage());
				response.getResultatTraitement().add(rt);
				LOGGER.error("Erreur dans l'envoi de question", e);
			}
		}
		long duration = (System.nanoTime() - startTime) / 1000000;
		LOGGER.info(JaxBHelper.logInWsTransaction(WSQuestion.SERVICE_NAME, WSQuestion.METHOD_NAME_ENVOYER_QUESTIONS,
				ctx.getCoreSession().getPrincipal().getName(), request, EnvoyerQuestionsRequest.class, response,
				EnvoyerQuestionsResponse.class)
				+ "---DURATION : " + duration + "ms ---\n");
		return response;
	}

	@Override
	@POST
	@Path(WSQuestion.METHOD_NAME_ENVOYER_QUESTIONS_ERRATA)
	public EnvoyerQuestionsErrataResponse envoyerQuestionsErrata(EnvoyerQuestionsErrataRequest request)
			throws Exception {
		long startTime = System.nanoTime();
		EnvoyerQuestionsErrataResponse response = new EnvoyerQuestionsErrataResponse();
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
				QuestionDelegate delegate = new QuestionDelegate(ctx.getCoreSession());
				response = delegate.envoyerQuestionsErrata(request);
			} catch (Exception e) {
				ResultatTraitement rt = new ResultatTraitement();
				rt.setStatut(TraitementStatut.KO);
				rt.setMessageErreur(e.getMessage());
				response.getResultatTraitement().add(rt);
				LOGGER.error("Erreur dans l'envoi d'errata de question", e);
			}
		}
		long duration = (System.nanoTime() - startTime) / 1000000;
		LOGGER.info(JaxBHelper.logInWsTransaction(WSQuestion.SERVICE_NAME,
				WSQuestion.METHOD_NAME_ENVOYER_QUESTIONS_ERRATA, ctx.getCoreSession().getPrincipal().getName(),
				request, EnvoyerQuestionsErrataRequest.class, response, EnvoyerQuestionsErrataResponse.class)
				+ "---DURATION : " + duration + "ms ---\n");
		return response;
	}

	@Override
	@POST
	@Path(WSQuestion.METHOD_NAME_CHANGER_ETAT_QUESTION)
	public ChangerEtatQuestionsResponse changerEtatQuestions(ChangerEtatQuestionsRequest request) throws Exception {
		long startTime = System.nanoTime();
		ChangerEtatQuestionsResponse response = new ChangerEtatQuestionsResponse();
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
				QuestionDelegate delegate = new QuestionDelegate(ctx.getCoreSession());
				response = delegate.changerEtatQuestions(request);
			} catch (Exception e) {
				ResultatTraitement rt = new ResultatTraitement();
				rt.setStatut(TraitementStatut.KO);
				rt.setMessageErreur(e.getMessage());
				response.getResultatTraitement().add(rt);
				LOGGER.error("Erreur dans le changement d'état de question", e);
			}
		}
		long duration = (System.nanoTime() - startTime) / 1000000;
		LOGGER.info(JaxBHelper.logInWsTransaction(WSQuestion.SERVICE_NAME,
				WSQuestion.METHOD_NAME_CHANGER_ETAT_QUESTION, ctx.getCoreSession().getPrincipal().getName(), request,
				ChangerEtatQuestionsRequest.class, response, ChangerEtatQuestionsResponse.class)
				+ "---DURATION : " + duration + "ms ---\n");
		return response;
	}

	@Override
	@POST
	@Path(WSQuestion.METHOD_NAME_RECHERCHER_DOSSIER)
	public RechercherDossierResponse rechercherDossier(RechercherDossierRequest request) throws Exception {
		long startTime = System.nanoTime();
		RechercherDossierResponse response = new RechercherDossierResponse();
		if (isPasswordOutdated()) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
		} else if (isPasswordTemporary()) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
		} else {
			try {
				QuestionDelegate delegate = new QuestionDelegate(ctx.getCoreSession());
				response = delegate.rechercherDossier(request);
			} catch (Exception e) {
				response.setStatut(TraitementStatut.KO);
				response.setMessageErreur(e.getMessage());
				LOGGER.error("Erreur dans la recherche de dossier", e);
			}
		}
		long duration = (System.nanoTime() - startTime) / 1000000;
		LOGGER.info(JaxBHelper.logInWsTransaction(WSQuestion.SERVICE_NAME, WSQuestion.METHOD_NAME_RECHERCHER_DOSSIER,
				ctx.getCoreSession().getPrincipal().getName(), request, RechercherDossierRequest.class, response,
				RechercherDossierResponse.class)
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
