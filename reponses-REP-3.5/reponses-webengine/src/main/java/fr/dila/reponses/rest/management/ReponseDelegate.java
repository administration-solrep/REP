package fr.dila.reponses.rest.management;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Allotissement;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.FondDeDossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.flux.RErratum;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.ReponsesConfigConstant;
import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.api.service.ReponseService;
import fr.dila.reponses.core.cases.flux.RErratumImpl;
import fr.dila.reponses.core.notification.WsUtils;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.rest.validator.EnvoyerReponseValidator;
import fr.dila.reponses.rest.validator.ValidatorResult;
import fr.dila.ss.api.exception.SSException;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.administration.EtatApplication;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.constant.STWebserviceConstant;
import fr.dila.st.api.jeton.JetonServiceDto;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.api.service.JetonService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.UnrestrictedGetDocumentRunner;
import fr.sword.xsd.reponses.ActionFile;
import fr.sword.xsd.reponses.ChercherErrataReponsesRequest;
import fr.sword.xsd.reponses.ChercherErrataReponsesResponse;
import fr.sword.xsd.reponses.ChercherReponsesRequest;
import fr.sword.xsd.reponses.ChercherReponsesResponse;
import fr.sword.xsd.reponses.EnvoyerReponseErrataRequest;
import fr.sword.xsd.reponses.EnvoyerReponseErrataResponse;
import fr.sword.xsd.reponses.EnvoyerReponsesRequest;
import fr.sword.xsd.reponses.EnvoyerReponsesResponse;
import fr.sword.xsd.reponses.ErratumReponse;
import fr.sword.xsd.reponses.Fichier;
import fr.sword.xsd.reponses.Ministre;
import fr.sword.xsd.reponses.QuestionId;
import fr.sword.xsd.reponses.QuestionSource;
import fr.sword.xsd.reponses.Reponse;
import fr.sword.xsd.reponses.ReponseQuestion;
import fr.sword.xsd.reponses.ResultatTraitement;
import fr.sword.xsd.reponses.TraitementStatut;
import fr.sword.xsd.reponses.Visibilite;

/**
 * Permet de gerer toutes les operations sur les questions
 */
public class ReponseDelegate extends AbstractDelegate {

	private static final STLogger	LOGGER	= STLogFactory.getLog(ReponseDelegate.class);

	/**
	 * Constructeur de ReponseDelegate.
	 * 
	 * @param session
	 *            Session
	 */
	public ReponseDelegate(CoreSession session) {
		super(session);
	}

	/**
	 * Webservice chercherReponses.
	 * 
	 * @param request
	 *            Requête
	 * @return Réponse
	 */
	public ChercherReponsesResponse chercherReponses(ChercherReponsesRequest request) {
		ChercherReponsesResponse response = new ChercherReponsesResponse();

		// Verification de l'accès à l'application
		SSPrincipal principal = (SSPrincipal) session.getPrincipal();
		JetonService jetonService = STServiceLocator.getJetonService();

		try {
			EtatApplication etatApplication = STServiceLocator.getEtatApplicationService().getEtatApplicationDocument(
					session);
			if (etatApplication.getRestrictionAcces()
					&& !principal.isMemberOf(ReponsesBaseFunctionConstant.ADMIN_ACCESS_UNRESTRICTED)) {
				response.setStatut(TraitementStatut.KO);
				response.setMessageErreur(ACCES_APPLI_RESTREINT + etatApplication.getDescriptionRestriction());
				return response;
			}
		} catch (ClientException e) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(INFO_ACCES_APPLI_KO);
			LOGGER.error(session, ReponsesLogEnumImpl.FAIL_ACCESS_WSREPONSE_TEC, e);
			return response;
		}

		response.setStatut(TraitementStatut.OK);
		response.setDernierRenvoi(true);
		response.setJetonReponses("");

		String webservice = STWebserviceConstant.CHERCHER_REPONSES;
		String jetonRecu = request.getJeton();

		QuestionSource origineUtilisateur = null;
		QuestionId dtoPourOrigineUtilisateur = new QuestionId();
		String idProfilUtilisateur = null;
		if (!hasRightAndOrigineUtilisateur(session, webservice, dtoPourOrigineUtilisateur)) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(USER_NON_AUTORISE);
			return response;
		} else if (dtoPourOrigineUtilisateur.getSource() == null) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(ORIGINE_UTILISATEUR_INCONNUE);
			return response;
		} else {
			origineUtilisateur = dtoPourOrigineUtilisateur.getSource();
			if (origineUtilisateur == QuestionSource.AN) {
				idProfilUtilisateur = VocabularyConstants.QUESTION_ORIGINE_AN;
			} else if (origineUtilisateur == QuestionSource.SENAT) {
				idProfilUtilisateur = VocabularyConstants.QUESTION_ORIGINE_SENAT;
			}
		}

		JetonServiceDto dto = null;
		try {
			dto = metaChercherDocument(idProfilUtilisateur, jetonRecu, webservice, request.getIdQuestions());
		} catch (NumberFormatException e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_GET_JETON_TEC, e);
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(JETON_NON_RECONNU);
			return response;
		} catch (IllegalArgumentException e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_GET_JETON_SERVICE_TEC, e);
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(e.getMessage());
			return response;
		} catch (ClientException e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_GET_JETON_SERVICE_TEC, e);
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(e.getMessage());
			return response;
		}
		if (dto == null) {
			response.setStatut(TraitementStatut.KO);
			
			Long numeroJetonSuivant = jetonService.getNumeroJetonMaxForWS(session, idProfilUtilisateur, webservice);
			if (numeroJetonSuivant < 0) {
				numeroJetonSuivant = 0L;
			}
			String message = JETON_NON_ADEQUAT + " Dernier jeton fourni : " + numeroJetonSuivant.toString();
			response.setMessageErreur(message);
			return response;
		}

		String jetonSortant = null;
		if (dto.getNextJetonNumber() != null) {
			jetonSortant = dto.getNextJetonNumber().toString();
			response.setJetonReponses(jetonSortant);
		}
		if (dto.isLastSending() != null) {
			response.setDernierRenvoi(dto.isLastSending());
		}
		List<DocumentModel> docList = dto.getDocumentList();
		if (docList == null) {
			return response;
		}

		List<String> lotsEnvoyes = new ArrayList<String>();
		List<ReponseQuestion> resultQuestions = new ArrayList<ReponseQuestion>();

		if (docList.size() > 0) {
			// Renvoi des questions/réponses
			for (DocumentModel documentModel : docList) {
				fr.dila.reponses.api.cases.Question appQuestion = documentModel
						.getAdapter(fr.dila.reponses.api.cases.Question.class);

				QuestionId qid = WsUtils.getQuestionIdFromQuestion(appQuestion);
				if (qid.getSource() != origineUtilisateur) {
					response.setStatut(TraitementStatut.KO);
					if (response.getMessageErreur() == null) {
						response.setMessageErreur("");
					}
					response.setMessageErreur(response.getMessageErreur() + "Erreur : La question "
							+ qid.getNumeroQuestion() + " n'est pas de la même origine que celle de l'utilisateur. \n ");
					break;
				}
				try {
					// met de coté le lot auquel la question appartient, pour ne plus le renvoyer
					UnrestrictedGetDocumentRunner uGet = new UnrestrictedGetDocumentRunner(session);
					DocumentModel dossierDoc = uGet.getByRef(appQuestion.getDossierRef());
					Dossier dossier = dossierDoc.getAdapter(Dossier.class);
					if (!lotsEnvoyes.contains(dossier.getDossierLot())) {
						resultQuestions.add(getXsdReponsesFromQuestion(appQuestion, webservice));
						if (dossier.getDossierLot() != null && !dossier.getDossierLot().isEmpty()) {
							lotsEnvoyes.add(dossier.getDossierLot());
						}
					}

				} catch (ClientException e) {
					response.setStatut(TraitementStatut.KO);
					response.setMessageErreur("Impossible de récupérer la question " + qid.getNumeroQuestion()
							+ " : Dossier introuvable.");
					LOGGER.error(session, ReponsesLogEnumImpl.FAIL_GET_QUESTION_TEC, e);
					break;
				}

				Question question = null;
				try {
					question = getQuestionFromQuestionId(qid);
				} catch (ClientException e) {
					response.setStatut(TraitementStatut.KO);
					response.setMessageErreur("Impossible de récupérer la question " + qid.getNumeroQuestion()
							+ " : Question introuvable.");
					LOGGER.error(session, ReponsesLogEnumImpl.FAIL_GET_QUESTION_TEC, e);
					break;
				}
				// Si une date de transmission aux assemblées existe déjà, on ne change pas la date précédente
				if (question.getDateTransmissionAssemblees() == null) {
					question.setDateTransmissionAssemblees(new GregorianCalendar());
				}

				try {
					session.saveDocument(question.getDocument());
					session.save();
				} catch (ClientException e) {
					LOGGER.error(session, ReponsesLogEnumImpl.FAIL_SAVE_QUESTION_TEC, question.getDocument(), e);
					response.setStatut(TraitementStatut.KO);
					response.setMessageErreur("Impossible d'enregistrer la question " + qid.getNumeroQuestion());
					break;
				}
			}
		}
		
		// Si réponse KO renvoi du jeton en cours / Si réponse OK ajout du résultat à la réponse
		if (response.getStatut().equals(TraitementStatut.KO)) {
			response.setJetonReponses("");
		} else {
			response.getReponses().addAll(resultQuestions);
		}

		// log l'action dans le journal d'administration
		logWebServiceAction(ReponsesEventConstant.WEBSERVICE_CHERCHER_REPONSES_EVENT,
				ReponsesEventConstant.WEBSERVICE_CHERCHER_REPONSES_COMMENT);

		return response;
	}

	// ////////////////////////////////////////////////////////////
	//
	// CHERCHER ERRRATA REPONSES
	//
	// ////////////////////////////////////////////////////////////
	/**
	 * Webservice chercherErrataReponses
	 * 
	 * @param request
	 * @return
	 */
	public ChercherErrataReponsesResponse chercherErrataReponses(ChercherErrataReponsesRequest request) {
		ChercherErrataReponsesResponse response = new ChercherErrataReponsesResponse();

		// Verification de l'accès à l'application
		SSPrincipal principal = (SSPrincipal) session.getPrincipal();

		try {
			EtatApplication etatApplication = STServiceLocator.getEtatApplicationService().getEtatApplicationDocument(
					session);
			if (etatApplication.getRestrictionAcces()
					&& !principal.isMemberOf(ReponsesBaseFunctionConstant.ADMIN_ACCESS_UNRESTRICTED)) {
				response.setStatut(TraitementStatut.KO);
				response.setMessageErreur(ACCES_APPLI_RESTREINT + etatApplication.getDescriptionRestriction());
				return response;
			}
		} catch (ClientException e) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(INFO_ACCES_APPLI_KO);
			LOGGER.error(session, ReponsesLogEnumImpl.FAIL_ACCESS_WSREPONSE_TEC, e);
			return response;
		}

		response.setStatut(TraitementStatut.OK);
		response.setDernierRenvoi(true);
		response.setJetonErrata("");

		String webservice = STWebserviceConstant.CHERCHER_ERRATA_REPONSES;
		String jetonRecu = request.getJeton();

		QuestionSource origineUtilisateur = null;
		QuestionId dtoPourOrigineUtilisateur = new QuestionId();
		if (!hasRightAndOrigineUtilisateur(session, webservice, dtoPourOrigineUtilisateur)) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(USER_NON_AUTORISE);
			return response;
		} else if (dtoPourOrigineUtilisateur.getSource() == null) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(ORIGINE_UTILISATEUR_INCONNUE);
			return response;
		} else {
			origineUtilisateur = dtoPourOrigineUtilisateur.getSource();
		}

		JetonServiceDto dto = null;
		try {
			dto = metaChercherDocument(origineUtilisateur.toString(), jetonRecu, webservice, request.getIdQuestions());
		} catch (NumberFormatException e) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(JETON_NON_RECONNU);
			LOGGER.error(session, STLogEnumImpl.FAIL_GET_JETON_TEC, e);
			return response;
		} catch (IllegalArgumentException e) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(e.getMessage());
			LOGGER.error(session, STLogEnumImpl.FAIL_GET_JETON_SERVICE_TEC, e);
			return response;
		} catch (ClientException e) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(e.getMessage());
			LOGGER.error(session, STLogEnumImpl.FAIL_GET_JETON_SERVICE_TEC, e);
			return response;
		}
		if (dto == null) {
			response.setStatut(TraitementStatut.KO);

			JetonService jetonService = STServiceLocator.getJetonService();
			Long numeroJetonSuivant = jetonService.getNumeroJetonMaxForWS(session, origineUtilisateur.toString(),
					webservice);

			if (numeroJetonSuivant < 0) {
				numeroJetonSuivant = 0L;
			}
			String message = JETON_NON_ADEQUAT + " Dernier jeton fourni : " + numeroJetonSuivant.toString();
			response.setMessageErreur(message);
			LOGGER.error(session, STLogEnumImpl.FAIL_GET_JETON_TEC);
			return response;
		}

		String jetonSortant = null;
		if (dto.getNextJetonNumber() != null) {
			jetonSortant = dto.getNextJetonNumber().toString();
			response.setJetonErrata(jetonSortant);
		}
		if (dto.isLastSending() != null) {
			response.setDernierRenvoi(dto.isLastSending());
		}
		List<DocumentModel> docList = dto.getDocumentList();
		if (docList == null) {
			return response;
		}
		List<ErratumReponse> resultQuestions = new ArrayList<ErratumReponse>();
		List<String> lotsEnvoyes = new ArrayList<String>();
		StringBuffer rapport = new StringBuffer();

		if (docList.size() > 0) {
			for (DocumentModel documentModel : docList) {
				fr.dila.reponses.api.cases.Question appQuestion = documentModel
						.getAdapter(fr.dila.reponses.api.cases.Question.class);
				QuestionId qid = WsUtils.getQuestionIdFromQuestion(appQuestion);

				// Vérification de la concordance entre l'origine de l'utilisateur et celle de la question
				if (qid.getSource() != origineUtilisateur) {
					response.setStatut(TraitementStatut.KO);
					rapport.append("Erreur : La question " + qid.getNumeroQuestion()
							+ " n'a pas la même origine que celle de l'utilisateur. \n ");
					continue;
				}

				// Récupère le dossier lié à la question
				Dossier dossier = null;
				try {
					dossier = appQuestion.getDossier(session);
				} catch (ClientException e) {
					rapport.append("Erreur lors de l'obtention du dossier de la question : " + qid.getNumeroQuestion()
							+ ". \n ");
					LOGGER.error(session, STLogEnumImpl.FAIL_GET_DOSSIER_TEC, e);
					continue;
				}

				// Suppression des lots envoyés en doublon
				if (!lotsEnvoyes.contains(dossier.getDossierLot())) {
					if (dossier.getDossierLot() != null && !dossier.getDossierLot().isEmpty()) {
						lotsEnvoyes.add(dossier.getDossierLot());
					}
				} else {
					// Le lot a déjà été modifié, on passe
					continue;
				}

				// Ajout des errata à la réponse WS
				try {
					resultQuestions.add(getErratumReponseFromQuestion(appQuestion, resultQuestions, webservice));
				} catch (ClientException e) {
					response.setStatut(TraitementStatut.KO);
					rapport.append("Erreur pour la question " + appQuestion.getNumeroQuestion());
					LOGGER.error(session, ReponsesLogEnumImpl.FAIL_GET_ERRATA_TEC, dossier.getDocument(), e);
				}
			}

		}
		if (response.getStatut() == TraitementStatut.KO) {
			response.setMessageErreur(rapport.toString());
		}
		response.getErrataReponses().addAll(resultQuestions);
		// log l'action dans le journal d'administration
		logWebServiceAction(ReponsesEventConstant.WEBSERVICE_CHERCHER_ERRATA_REPONSES_EVENT,
				ReponsesEventConstant.WEBSERVICE_CHERCHER_ERRATA_REPONSES_COMMENT);
		return response;
	}

	// ////////////////////////////////////////////////////////////
	//
	// ENVOYER REPONSES
	//
	// ////////////////////////////////////////////////////////////

	private boolean needToValidateReponse() {
		final ConfigService configService = STServiceLocator.getConfigService();
		try {
			boolean validateReponseContent = configService
					.getBooleanValue(ReponsesConfigConstant.VALIDATE_REPONSE_CONTENT_PARAMETER_NAME);
			return validateReponseContent;
		} catch (RuntimeException e) {
			LOGGER.error(session, ReponsesLogEnumImpl.FAIL_GET_PARAM_VAL_REP_TEC, e);
		}
		return false;
	}

	/**
	 * Webservice envoyerReponses
	 * 
	 * @param request
	 * @return
	 * @throws ClientException
	 */
	public EnvoyerReponsesResponse envoyerReponses(EnvoyerReponsesRequest request) throws ClientException {
		EnvoyerReponsesResponse response = new EnvoyerReponsesResponse();
		List<ResultatTraitement> listTraitement = response.getResultatTraitement();
		Set<ResultatTraitement> setTraitement = new HashSet<ResultatTraitement>();
		setTraitement.addAll(listTraitement);

		// Verification de l'accès à l'application
		SSPrincipal principal = (SSPrincipal) session.getPrincipal();

		try {
			EtatApplication etatApplication = STServiceLocator.getEtatApplicationService().getEtatApplicationDocument(
					session);
			if (etatApplication.getRestrictionAcces()
					&& !principal.isMemberOf(ReponsesBaseFunctionConstant.ADMIN_ACCESS_UNRESTRICTED)) {
				ResultatTraitement resultatTraitement = new ResultatTraitement();
				resultatTraitement.setStatut(TraitementStatut.KO);
				resultatTraitement
						.setMessageErreur(ACCES_APPLI_RESTREINT + etatApplication.getDescriptionRestriction());
				response.getResultatTraitement().add(resultatTraitement);
				return response;
			}
		} catch (ClientException e) {
			ResultatTraitement resultatTraitement = new ResultatTraitement();
			resultatTraitement.setStatut(TraitementStatut.KO);
			resultatTraitement.setMessageErreur(INFO_ACCES_APPLI_KO);
			response.getResultatTraitement().add(resultatTraitement);
			LOGGER.error(session, ReponsesLogEnumImpl.FAIL_ACCESS_WSREPONSE_TEC, e);
			return response;
		}

		Set<String> ministeresUtilisateur = getMinisteresIdSetFromLogin(session);

		if (!hasRightAndOrigineUtilisateur(session, STWebserviceConstant.ENVOYER_REPONSES, null)) {
			ResultatTraitement resultatTraitement = new ResultatTraitement();
			resultatTraitement.setStatut(TraitementStatut.KO);
			resultatTraitement.setMessageErreur(USER_NON_AUTORISE);
			response.getResultatTraitement().add(resultatTraitement);
			return response;
		}

		boolean validateReponseContent = needToValidateReponse();

		List<ReponseQuestion> listReponseQuestions = request.getReponseQuestion();

		// Chargement des services
		final ReponseService reponseService = ReponsesServiceLocator.getReponseService();
		final DossierDistributionService dds = ReponsesServiceLocator.getDossierDistributionService();
		for (ReponseQuestion reponseQuestion : listReponseQuestions) {
			Reponse reponse = reponseQuestion.getReponse();

			if (validateReponseContent) {
				ValidatorResult validResult = EnvoyerReponseValidator.getInstance().validateReponseContent(
						reponse.getTexteReponse());
				if (!validResult.isValid()) {
					ResultatTraitement resultatTraitement = new ResultatTraitement();
					resultatTraitement.setStatut(TraitementStatut.KO);
					resultatTraitement.setMessageErreur(validResult.getErrorMsg());
					setTraitement.add(resultatTraitement);
					continue;
				}
			}

			// sly : Récupération des questions auxquels seront attribués la réponse.
			List<QuestionId> listQuestions = reponseQuestion.getIdQuestions();
			if (listQuestions == null || listQuestions.isEmpty()) {
				ResultatTraitement resultatTraitement = new ResultatTraitement();
				resultatTraitement.setStatut(TraitementStatut.KO);
				resultatTraitement
						.setMessageErreur("Erreur : aucune question n'est associée à une des réponses soumises");
				setTraitement.add(resultatTraitement);
				continue;
			}

			for (QuestionId idQuestion : listQuestions) {
				ResultatTraitement resultatTraitement = new ResultatTraitement();
				resultatTraitement.setStatut(TraitementStatut.OK);
				resultatTraitement.setIdQuestion(idQuestion);
				setTraitement.add(resultatTraitement);
				if (!isValidIdQuestion(idQuestion)) {
					// sly : controle de l'identifiant de question
					resultatTraitement.setStatut(TraitementStatut.KO);
					resultatTraitement.setMessageErreur("Les paramètres d'identifications ne sont pas reconnus.");
					setTraitement.add(resultatTraitement);
					continue;
				}

				Dossier dossier = null;
				try {
					dossier = getDossierFromQuestionId(idQuestion);
				} catch (ClientException e) {
					resultatTraitement.setStatut(TraitementStatut.KO);
					resultatTraitement.setMessageErreur("Impossible de récupérer la question "
							+ idQuestion.getNumeroQuestion() + " : Dossier introuvable.");
					setTraitement.add(resultatTraitement);
					LOGGER.error(session, STLogEnumImpl.FAIL_GET_DOSSIER_TEC, e);
					continue;
				}

				if (!ministeresUtilisateur.contains(dossier.getIdMinistereAttributaireCourant())) {
					resultatTraitement.setStatut(TraitementStatut.KO);
					resultatTraitement.setMessageErreur(USER_ISNT_IN_MINISTERE);
					setTraitement.add(resultatTraitement);
					continue;
				}

				// récupération du DossierLink (pour validation)
				DossierLink dlink = null;
				try {
					dlink = getDossierLinkFromDossier(dossier,
							VocabularyConstants.ROUTING_TASK_TYPE_REDACTION_INTERFACEE);
				} catch (ClientException e) {
					LOGGER.error(session, STLogEnumImpl.FAIL_GET_DOSSIER_LINK_TEC, dossier.getDocument(), e);
					dlink = null;
				}
				if (dlink == null) {
					resultatTraitement.setStatut(TraitementStatut.KO);
					resultatTraitement
							.setMessageErreur("Impossible de répondre à une question n'étant pas à l'étape 'rédaction interfacée'");
					setTraitement.add(resultatTraitement);
					continue;
				}

				// La question a une réponse, on la récupère
				DocumentModel reponseModel = reponseService.getReponseFromDossier(session, dossier.getDocument());

				if (reponseModel == null) {
					reponseModel = dossier.createReponse(session, Long.valueOf(idQuestion.getNumeroQuestion()), null);
				}

				fr.dila.reponses.api.cases.Reponse reponseAdapter = reponseModel
						.getAdapter(fr.dila.reponses.api.cases.Reponse.class);
				assignDataToReponse(reponse, reponseAdapter);

				// Dénormalisation du booléen indiquant qu'une question à une réponse non-vide
				Question question = dossier.getQuestion(session);
				question.setHasReponseInitiee(!StringUtils.isBlank(reponse.getTexteReponse()));
				try {
					session.saveDocument(question.getDocument());
					session.save();
				} catch (ClientException e) {
					resultatTraitement.setStatut(TraitementStatut.KO);
					resultatTraitement.setMessageErreur("Impossible d'enregistrer la question "
							+ question.getNumeroQuestion());
					setTraitement.add(resultatTraitement);
					LOGGER.error(session, ReponsesLogEnumImpl.FAIL_SAVE_QUESTION_TEC, question.getDocument(), e);
					continue;
				}

				// Gestion des fichiers
				List<Fichier> fichiers = reponse.getFichiersJoints();
				if (fichiers != null) {
					ResultatTraitement rtFichiers = createFichiers(fichiers, dossier);
					if (rtFichiers != null) {
						resultatTraitement.setMessageErreur(rtFichiers.getMessageErreur());
						resultatTraitement.setStatut(rtFichiers.getStatut());
						setTraitement.add(resultatTraitement);
						continue;
					}
				}

				try {
					reponseService.saveReponseFromMinistere(session, reponseModel);
				} catch (ClientException e) {
					resultatTraitement.setStatut(TraitementStatut.KO);
					resultatTraitement
							.setMessageErreur("Une erreur est survenue lors de la sauvegarde de la réponse du ministère");
					setTraitement.add(resultatTraitement);
					LOGGER.error(session, ReponsesLogEnumImpl.FAIL_SAVE_REPONSE_TEC, reponseModel.getRef(), e);
					continue;
				}

				try {
					dds.validerEtape(session, dossier.getDocument(), dlink.getDocument());
				} catch (ClientException e) {
					resultatTraitement.setStatut(TraitementStatut.KO);
					resultatTraitement
							.setMessageErreur("Une erreur est survenue lors de la validation de l'étape 'pour rédaction interfacée'");
					setTraitement.add(resultatTraitement);
					LOGGER.error(session, SSLogEnumImpl.FAIL_UPDATE_STEP_TEC, dossier.getDocument(), e);
					continue;
				}

				// log l'action dans le journal d'administration
				logWebServiceActionDossier(ReponsesEventConstant.WEBSERVICE_ENVOYER_REPONSES_EVENT,
						ReponsesEventConstant.WEBSERVICE_ENVOYER_REPONSES_COMMENT, dossier);

			}
		}
		response.getResultatTraitement().addAll(setTraitement);
		return response;
	}

	// ////////////////////////////////////////////////////////////
	//
	// ENVOYER ERRRATA REPONSES
	//
	// ////////////////////////////////////////////////////////////

	/**
	 * Webservice envoyerErrataReponses
	 * 
	 * @param request
	 * @return
	 * @throws ClientException 
	 */
	public EnvoyerReponseErrataResponse envoyerReponseErrata(EnvoyerReponseErrataRequest request) throws ClientException {
		EnvoyerReponseErrataResponse response = new EnvoyerReponseErrataResponse();

		// Verification de l'accès à l'application
		SSPrincipal principal = (SSPrincipal) session.getPrincipal();

		try {
			EtatApplication etatApplication = STServiceLocator.getEtatApplicationService().getEtatApplicationDocument(
					session);
			if (etatApplication.getRestrictionAcces()
					&& !principal.isMemberOf(ReponsesBaseFunctionConstant.ADMIN_ACCESS_UNRESTRICTED)) {
				ResultatTraitement resultatTraitement = new ResultatTraitement();
				resultatTraitement.setStatut(TraitementStatut.KO);
				resultatTraitement
						.setMessageErreur(ACCES_APPLI_RESTREINT + etatApplication.getDescriptionRestriction());
				response.getResultatTraitement().add(resultatTraitement);
				return response;
			}
		} catch (ClientException e) {
			ResultatTraitement resultatTraitement = new ResultatTraitement();
			resultatTraitement.setStatut(TraitementStatut.KO);
			resultatTraitement.setMessageErreur(INFO_ACCES_APPLI_KO);
			response.getResultatTraitement().add(resultatTraitement);
			LOGGER.error(session, ReponsesLogEnumImpl.FAIL_ACCESS_WSREPONSE_TEC, e);
			return response;
		}

		Set<String> ministeresUtilisateur = getMinisteresIdSetFromLogin(session);

		if (!hasRightAndOrigineUtilisateur(session, STWebserviceConstant.ENVOYER_REPONSES_ERRATA, null)) {
			ResultatTraitement resultatTraitement = new ResultatTraitement();
			resultatTraitement.setStatut(TraitementStatut.KO);
			resultatTraitement.setMessageErreur(USER_NON_AUTORISE);
			response.getResultatTraitement().add(resultatTraitement);
			return response;
		}

		// Chargement des services
		final AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();
		final DossierDistributionService dds = ReponsesServiceLocator.getDossierDistributionService();
		List<ErratumReponse> erratumList = request.getErratum();

		for (ErratumReponse erratumReponse : erratumList) {
			ResultatTraitement resultatTraitement = new ResultatTraitement();
			resultatTraitement.setStatut(TraitementStatut.OK);

			String texteConsolide = erratumReponse.getTexteConsolide();
			String texteErratum = erratumReponse.getTexteErratum();
			List<QuestionId> questionsIdPourErratum = erratumReponse.getIdQuestion();
			if (questionsIdPourErratum == null) {
				resultatTraitement.setStatut(TraitementStatut.KO);
				resultatTraitement.setMessageErreur("Erreur : le flux ne permet pas d'identifier une réponse");
				response.getResultatTraitement().add(resultatTraitement);
				continue;
			}

			List<String> lotsModifies = new ArrayList<String>();

			for (QuestionId questionIdPourErratum : questionsIdPourErratum) {
				resultatTraitement.setIdQuestion(questionIdPourErratum);
				Dossier dossierQuestion = null;
				try {
					dossierQuestion = getDossierFromQuestionId(questionIdPourErratum);
				} catch (ClientException e) {
					resultatTraitement.setStatut(TraitementStatut.KO);
					resultatTraitement.setMessageErreur("Impossible de récupérer la question "
							+ questionIdPourErratum.getNumeroQuestion() + " : Dossier introuvable.");
					response.getResultatTraitement().add(resultatTraitement);
					LOGGER.error(session, STLogEnumImpl.FAIL_GET_DOSSIER_TEC, e);
					continue;
				}

				if (dossierQuestion == null) {
					resultatTraitement.setStatut(TraitementStatut.KO);
					resultatTraitement.setMessageErreur("Erreur lors de l'identification du dossier "
							+ questionIdPourErratum.getNumeroQuestion());
					response.getResultatTraitement().add(resultatTraitement);
					continue;
				}

				if (!ministeresUtilisateur.contains(dossierQuestion.getIdMinistereAttributaireCourant())) {
					resultatTraitement.setStatut(TraitementStatut.KO);
					resultatTraitement.setMessageErreur(USER_ISNT_IN_MINISTERE);
					response.getResultatTraitement().add(resultatTraitement);
					continue;
				}

				Question question = dossierQuestion.getQuestion(session);

				// récupération du DossierLink (pour validation)
				DossierLink dlink = null;
				try {
					dlink = getDossierLinkFromDossier(dossierQuestion,
							VocabularyConstants.ROUTING_TASK_TYPE_REDACTION_INTERFACEE);
				} catch (ClientException e) {
					LOGGER.error(session, STLogEnumImpl.FAIL_GET_DOSSIER_LINK_TEC, dossierQuestion.getDocument(), e);
					dlink = null;
				}
				if (dlink == null) {
					resultatTraitement.setStatut(TraitementStatut.KO);
					resultatTraitement
							.setMessageErreur("Impossible de répondre à une question n'étant pas à l'étape 'rédaction interfacée'");
					response.getResultatTraitement().add(resultatTraitement);
					continue;
				}

				if (!lotsModifies.contains(dossierQuestion.getDossierLot())) {
					if (dossierQuestion.getDossierLot() != null && !dossierQuestion.getDossierLot().isEmpty()) {
						lotsModifies.add(dossierQuestion.getDossierLot());
					}
				} else {
					// Le lot a déjà été modifié, on passe
					continue;
				}

				// Gestion de l'allotissement
				List<fr.dila.reponses.api.cases.Reponse> listAppReponses = new ArrayList<fr.dila.reponses.api.cases.Reponse>();
				try {
					if (allotissementService.isAllotit(question, session)) {
						final UnrestrictedGetDocumentRunner uGet = new UnrestrictedGetDocumentRunner(session);
						Allotissement allotissement = allotissementService.getAllotissement(
								dossierQuestion.getDossierLot(), session);
						if (allotissement != null) {
							for (String idDossier : allotissement.getIdDossiers()) {
								DocumentModel dossierDoc = uGet.getById(idDossier);
								Dossier dossier = dossierDoc.getAdapter(Dossier.class);
								listAppReponses.add(dossier.getReponse(session));
							}
						}
					} else {
						listAppReponses.add(dossierQuestion.getReponse(session));
					}
				} catch (ClientException e) {
					resultatTraitement.setStatut(TraitementStatut.KO);
					resultatTraitement.setMessageErreur("Erreur à l'erratum de la réponse  "
							+ questionIdPourErratum.getNumeroQuestion());
					response.getResultatTraitement().add(resultatTraitement);
					LOGGER.error(session, ReponsesLogEnumImpl.FAIL_GET_ERRATA_TEC, dossierQuestion.getDocument(), e);
					continue;
				}

				for (fr.dila.reponses.api.cases.Reponse appReponse : listAppReponses) {
					if (appReponse == null) {
						resultatTraitement.setStatut(TraitementStatut.KO);
						resultatTraitement.setMessageErreur("Erreur lors de l'identification de la réponse "
								+ questionIdPourErratum.getNumeroQuestion());
						response.getResultatTraitement().add(resultatTraitement);
						continue;
					}

					if (!appReponse.isPublished()) {
						resultatTraitement.setStatut(TraitementStatut.KO);
						resultatTraitement
								.setMessageErreur("Impossible d'émettre un erratum à une question n'étant pas répondue");
						response.getResultatTraitement().add(resultatTraitement);
						continue;
					}

					RErratum erratumAppReponse = new RErratumImpl();
					erratumAppReponse.setDatePublication(new GregorianCalendar());
					erratumAppReponse.setPageJo(0);
					erratumAppReponse.setTexteErratum(texteErratum);
					erratumAppReponse.setTexteConsolide(texteConsolide);
					List<RErratum> erList = appReponse.getErrata();

					erList.add(erratumAppReponse);
					appReponse.setErrata(erList);
					appReponse.setTexteReponse(texteConsolide);

					try {
						session.saveDocument(appReponse.getDocument());
						session.save();
					} catch (ClientException e) {
						resultatTraitement.setStatut(TraitementStatut.KO);
						resultatTraitement.setMessageErreur("Erreur à l'erratum de la réponse "
								+ questionIdPourErratum.getNumeroQuestion() + " \n" + e.getMessage());
						response.getResultatTraitement().add(resultatTraitement);
						LOGGER.error(session, ReponsesLogEnumImpl.FAIL_SAVE_ERRATA_TEC, appReponse.getDocument(), e);
						continue;
					}
				}

				try {
					dds.validerEtape(session, dossierQuestion.getDocument(), dlink.getDocument());
				} catch (ClientException e) {
					resultatTraitement.setStatut(TraitementStatut.KO);
					resultatTraitement.setMessageErreur("Erreur à l'erratum de la réponse "
							+ questionIdPourErratum.getNumeroQuestion() + " \n" + e.getMessage());
					LOGGER.error(session, SSLogEnumImpl.FAIL_UPDATE_STEP_TEC, dossierQuestion.getDocument(), e);
				}
				response.getResultatTraitement().add(resultatTraitement);

				// log l'action dans le journal d'administration
				logWebServiceActionDossier(ReponsesEventConstant.WEBSERVICE_ENVOYER_ERRATA_REPONSES_EVENT,
						ReponsesEventConstant.WEBSERVICE_ENVOYER_ERRATA_REPONSES_COMMENT, dossierQuestion);

			}
		}

		return response;
	}

	// ////////////////////////////////////////////////////////////
	//
	// TOOLS
	//
	// ////////////////////////////////////////////////////////////

	/**
	 * 
	 * @param question
	 * @param webservice
	 * @param errataReponseAdded
	 * 
	 * @return
	 * @throws ClientException
	 */
	private ErratumReponse getErratumReponseFromQuestion(fr.dila.reponses.api.cases.Question question,
			List<ErratumReponse> errataReponseAdded, String webservice) throws ClientException {
		final AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();

		DocumentModel reponseDoc = ReponsesServiceLocator.getReponseService().getReponseFromDossier(session,
				session.getDocument(question.getDossierRef()));

		fr.dila.reponses.api.cases.Reponse reponse = null;
		if (reponseDoc != null) {
			reponse = reponseDoc.getAdapter(fr.dila.reponses.api.cases.Reponse.class);
		}

		if (reponse != null) {

			List<RErratum> erList = reponse.getErrata();
			if (erList != null && erList.size() != 0) {
				QuestionId qid = WsUtils.getQuestionIdFromQuestion(question);

				// Si la réponse a plusieurs errata
				// position = 1 <=> on traite le dernier errata.
				// si dans les résultats on a deja un errata pour cette réponse on traite l'avant dernier
				int position = 1;
				for (ErratumReponse erratumReponse : errataReponseAdded) {
					List<QuestionId> eqIds = erratumReponse.getIdQuestion();
					for (QuestionId eqId : eqIds) {
						if (idQuestionsAreEquals(qid, eqId)) {
							position++;
						}
					}
				}

				int itemPosition = (erList.size() - position < 0) ? 0 : erList.size() - position;
				RErratum reponseErratum = reponse.getErrata().get(itemPosition);
				ErratumReponse erratumReponse = new ErratumReponse();

				erratumReponse.setTexteConsolide("");
				erratumReponse.setTexteErratum("");

				// Gestion de l'allotissement
				// Si la question est dans un lot, renseigner toutes les QuestionId du lot (en fonction de
				// l'utilisateur)
				if (allotissementService.isAllotit(question, session)) {
					final UnrestrictedGetDocumentRunner uGet = new UnrestrictedGetDocumentRunner(session);
					final DocumentModel docDossierDirecteur = uGet.getByRef(question.getDossierRef());
					final Dossier dossierDirecteur = docDossierDirecteur.getAdapter(Dossier.class);
					Allotissement allotissement = allotissementService.getAllotissement(
							dossierDirecteur.getDossierLot(), session);
					if (allotissement != null) {
						for (String idDossier : allotissement.getIdDossiers()) {
							DocumentModel dossierDoc = uGet.getById(idDossier);
							Dossier dossier = dossierDoc.getAdapter(Dossier.class);
							// on ajoute la question si l'utilisateur peut la récupérer
							QuestionId questionId = WsUtils.getQuestionIdFromQuestion(dossier.getQuestion(session));
							if (hasUserRightAndSameOrigineQuestion(session, webservice, questionId)) {
								erratumReponse.getIdQuestion().add(questionId);
							}
						}
					}
				} else {
					erratumReponse.getIdQuestion().add(qid);
				}

				// Ajout du texte consolidé et de l'erratum à la réponse WS
				if (reponseErratum.getTexteConsolide() != null) {
					erratumReponse.setTexteConsolide(reponseErratum.getTexteConsolide());
				}
				if (reponseErratum.getTexteErratum() != null) {
					erratumReponse.setTexteErratum(reponseErratum.getTexteErratum());
				}

				return erratumReponse;
			}
		}

		return null;
	}

	/**
	 * Assigne le texte de la reponse
	 * 
	 * @param texteReponse
	 * @param reponseSource
	 * @param reponseModel
	 */
	private void assignDataToReponse(Reponse reponseIn, fr.dila.reponses.api.cases.Reponse reponseOut) {
		reponseOut.setTexteReponse(reponseIn.getTexteReponse());
		Ministre ministreReponse = reponseIn.getMinistreReponse();
		reponseOut.setIdAuteurReponse(Integer.valueOf(ministreReponse.getId()).toString());
	}

	/**
	 * Assemble une {@link Question} en {@link ReponseQuestion}
	 * 
	 * @param appQuestion
	 * @param webservice
	 * @return
	 * @throws ClientException
	 */
	public ReponseQuestion getXsdReponsesFromQuestion(fr.dila.reponses.api.cases.Question appQuestion, String webservice)
			throws ClientException {
		// Chargement des services
		final AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();

		ReponseQuestion resultQuestionReponse = new ReponseQuestion();
		resultQuestionReponse.setReponse(new Reponse());

		// IdQuestion
		// ///////////////////

		QuestionId questionId = WsUtils.getQuestionIdFromQuestion(appQuestion);

		// L'allotissement n'est ajouté que si l'état est à répondu
		if (allotissementService.isAllotit(appQuestion, session) && appQuestion.isRepondue()) {
			final UnrestrictedGetDocumentRunner uGet = new UnrestrictedGetDocumentRunner(session);
			final DocumentModel docDossierDirecteur = uGet.getByRef(appQuestion.getDossierRef());
			final Dossier dossierDirecteur = docDossierDirecteur.getAdapter(Dossier.class);
			Allotissement allotissement = allotissementService.getAllotissement(dossierDirecteur.getDossierLot(),
					session);
			if (allotissement != null) {
				for (String idDossier : allotissement.getIdDossiers()) {
					DocumentModel dossierDoc = uGet.getById(idDossier);
					Dossier dossier = dossierDoc.getAdapter(Dossier.class);

					QuestionId qid = WsUtils.getQuestionIdFromQuestion(dossier.getQuestion(session));
					if (hasUserRightAndSameOrigineQuestion(session, webservice, qid)) {
						resultQuestionReponse.getIdQuestions().add(qid);
					}
				}
			}
		} else {
			resultQuestionReponse.getIdQuestions().add(questionId);
		}

		Reponse reponse = resultQuestionReponse.getReponse();
		reponse.setTexteReponse("");

		// Ministere qui a répondu;
		// ///////////////////
		Ministre ministreReponse = new Ministre();
		ministreReponse.setIntituleMinistere("");
		ministreReponse.setTitreJo("");

		Dossier dossierDeLaReponse = getDossierFromQuestionId(questionId);
		String idMinistereReponse = dossierDeLaReponse.getIdMinistereAttributaireCourant();
		EntiteNode entiteNode = null;
		if (idMinistereReponse != null) {
			ministreReponse.setId(Integer.parseInt(idMinistereReponse));
		}
		if (ministreReponse.getId() != 0) {
			try {
				entiteNode = STServiceLocator.getSTMinisteresService().getEntiteNode(idMinistereReponse);
			} catch (ClientException e) {
				LOGGER.error(session, STLogEnumImpl.FAIL_GET_ENTITE_NODE_TEC, e);
			}
		}
		if (entiteNode != null) {
			ministreReponse.setIntituleMinistere(entiteNode.getLabel());
			ministreReponse.setTitreJo(entiteNode.getEdition());
		}
		reponse.setMinistreReponse(ministreReponse);

		// Texte réponse;
		// Donnée portée par le dossier, du coup on récupere le dossier correspondant.
		// A ne renvoyer que si l'état du dossier est à répondu
		if (appQuestion.isRepondue()) {
			fr.dila.reponses.api.cases.Reponse appReponse = dossierDeLaReponse.getReponse(session);
			String txtReponse = appReponse.getTexteReponse();
			if (txtReponse != null && txtReponse.length() > 0) {
				reponse.setTexteReponse(txtReponse);
			}
		}

		// Récupération des pièces jointes
		// A ne renvoyer que si l'état du dossier est à répondu
		if (appQuestion.isRepondue()) {
			FondDeDossier fdd = dossierDeLaReponse.getFondDeDossier(session);
			if (fdd != null) {
				String parlementId = fdd.getRepertoireParlementId();
				DocumentRef parentDocRef = new IdRef(parlementId);

				if (parentDocRef != null) {
					List<DocumentModel> fichiersModel = session.getChildren(parentDocRef);
					for (DocumentModel elementFddModel : fichiersModel) {
						String fileName = (String) elementFddModel.getProperty(STSchemaConstant.FILE_SCHEMA,
								STSchemaConstant.FILE_FILENAME_PROPERTY);
						Blob fileBlob = (Blob) elementFddModel.getProperty(STSchemaConstant.FILE_SCHEMA,
								STSchemaConstant.FILE_CONTENT_PROPERTY);
						if ((fileName == null) || (fileName.length() == 0)) {
							continue;
						}
						if (fileBlob == null || fileBlob.getMimeType() == null || fileBlob.getMimeType().length() == 0) {
							continue;
						}

						Fichier outfile = new Fichier();
						outfile.setIdInterne(elementFddModel.getId());
						outfile.setIdExterne("");
						outfile.setADuContenu(true);
						outfile.setMimeType(fileBlob.getMimeType());
						outfile.setNom(fileName);
						outfile.setAction(ActionFile.AUCUN);
						outfile.setVisibilite(Visibilite.PARLEMENT);

						byte[] content;
						try {
							content = fileBlob.getByteArray();
							outfile.setTailleFichier(Long.valueOf(fileBlob.getLength()).intValue());
						} catch (IOException e) {
							continue;
						}

						outfile.setContenu(content);
						reponse.getFichiersJoints().add(outfile);

					}
				}
			}
		}
		resultQuestionReponse.setReponse(reponse);

		return resultQuestionReponse;
	}

	protected Boolean hasUserRightAndSameOrigineQuestion(CoreSession session, String webservice, QuestionId questionId) {

		List<String> userGroupList = ((SSPrincipal) session.getPrincipal()).getGroups();
		if (!userGroupList.contains(webservice)) {
			return false;
		}
		if (questionId == null) {
			return true;
		}
		if (userGroupList.contains(STWebserviceConstant.PROFIL_AN) && questionId.getSource().equals(QuestionSource.AN)) {
			return true;
		} else if (userGroupList.contains(STWebserviceConstant.PROFIL_SENAT)
				&& questionId.getSource().equals(QuestionSource.SENAT)) {
			return true;
		}
		return false;
	}
}
