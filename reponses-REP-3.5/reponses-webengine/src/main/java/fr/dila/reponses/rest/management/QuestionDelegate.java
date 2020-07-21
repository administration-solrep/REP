package fr.dila.reponses.rest.management;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;
import org.nuxeo.runtime.transaction.TransactionHelper;

import fr.dila.reponses.api.Exception.AllotissementException;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.cases.flux.QErratum;
import fr.dila.reponses.api.cases.flux.QuestionStateChange;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.api.service.JetonService;
import fr.dila.reponses.api.service.ReponsesMailService;
import fr.dila.reponses.core.cases.flux.QErratumImpl;
import fr.dila.reponses.core.notification.WsUtils;
import fr.dila.reponses.core.recherche.ReponsesMinimalEscaper;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.rest.helper.QueryHelper;
import fr.dila.reponses.rest.validator.EnvoyerQuestionValidator;
import fr.dila.reponses.rest.validator.ValidatorResult;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.administration.EtatApplication;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.api.constant.STWebserviceConstant;
import fr.dila.st.api.feuilleroute.STRouteStep;
import fr.dila.st.api.jeton.JetonServiceDto;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.api.service.STLockService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.PropertyUtil;
import fr.dila.st.core.util.StringUtil;
import fr.dila.st.core.util.UnrestrictedQueryRunner;
import fr.sword.xsd.reponses.Auteur;
import fr.sword.xsd.reponses.ChangementEtatQuestion;
import fr.sword.xsd.reponses.ChangerEtatQuestionsRequest;
import fr.sword.xsd.reponses.ChangerEtatQuestionsResponse;
import fr.sword.xsd.reponses.ChercherChangementDEtatQuestionsRequest;
import fr.sword.xsd.reponses.ChercherChangementDEtatQuestionsResponse;
import fr.sword.xsd.reponses.ChercherErrataQuestionsRequest;
import fr.sword.xsd.reponses.ChercherErrataQuestionsResponse;
import fr.sword.xsd.reponses.ChercherQuestionsRequest;
import fr.sword.xsd.reponses.ChercherQuestionsResponse;
import fr.sword.xsd.reponses.ChercherQuestionsResponse.Questions;
import fr.sword.xsd.reponses.EnvoyerQuestionsErrataRequest;
import fr.sword.xsd.reponses.EnvoyerQuestionsErrataResponse;
import fr.sword.xsd.reponses.EnvoyerQuestionsRequest;
import fr.sword.xsd.reponses.EnvoyerQuestionsResponse;
import fr.sword.xsd.reponses.ErratumQuestion;
import fr.sword.xsd.reponses.ErratumType;
import fr.sword.xsd.reponses.EtatQuestion;
import fr.sword.xsd.reponses.Fichier;
import fr.sword.xsd.reponses.IndexationAn;
import fr.sword.xsd.reponses.IndexationSenat;
import fr.sword.xsd.reponses.Ministre;
import fr.sword.xsd.reponses.Question;
import fr.sword.xsd.reponses.QuestionId;
import fr.sword.xsd.reponses.QuestionReponse;
import fr.sword.xsd.reponses.QuestionSource;
import fr.sword.xsd.reponses.QuestionType;
import fr.sword.xsd.reponses.RechercherDossierRequest;
import fr.sword.xsd.reponses.RechercherDossierResponse;
import fr.sword.xsd.reponses.ReponsePubliee;
import fr.sword.xsd.reponses.ResultatTraitement;
import fr.sword.xsd.reponses.TraitementStatut;

/**
 * Permet de gerer toutes les operations sur les questions
 */
public class QuestionDelegate extends AbstractDelegate {

	private static final String					DOSSIER_INTROUVABLE					= " : Dossier introuvable.";

	private static final String					RECUPERATION_QUESTION_IMPOSSIBLE	= "Impossible de récupérer la question ";

	private static final ReponsesMinimalEscaper	SANITIZER							= new ReponsesMinimalEscaper();

	private static final List<String>			RECHERCHER_DOSSIER_EQ_PARLEMENTAIRE	= new ArrayList<String>();

	static {
		RECHERCHER_DOSSIER_EQ_PARLEMENTAIRE.add(VocabularyConstants.ETAT_QUESTION_REPONDU);
		RECHERCHER_DOSSIER_EQ_PARLEMENTAIRE.add(VocabularyConstants.ETAT_QUESTION_EN_COURS);
		RECHERCHER_DOSSIER_EQ_PARLEMENTAIRE.add(VocabularyConstants.ETAT_QUESTION_CLOTURE_AUTRE);
		RECHERCHER_DOSSIER_EQ_PARLEMENTAIRE.add(VocabularyConstants.ETAT_QUESTION_RETIREE);
	}

	/**
	 * Logger.
	 */
	private static final STLogger				LOGGER								= STLogFactory
																							.getLog(Question.class);

	public QuestionDelegate(CoreSession documentManager) {
		super(documentManager);
	}

	// WEBSERVICES
	// ////////////////////////////////////////////////////////////
	//
	// CHERCHER QUESTIONS
	//
	// ////////////////////////////////////////////////////////////

	/**
	 * Webservice chercherQuestions
	 * 
	 * @param request
	 * @return
	 */
	public ChercherQuestionsResponse chercherQuestions(ChercherQuestionsRequest request) {
		ChercherQuestionsResponse response = new ChercherQuestionsResponse();

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
			LOGGER.error(session, ReponsesLogEnumImpl.FAIL_ACCESS_WSQUESTION_TEC, e);
			return response;
		}

		response.setStatut(TraitementStatut.OK);
		response.setDernierRenvoi(true);
		response.setJetonQuestions("");

		String webservice = STWebserviceConstant.CHERCHER_QUESTIONS;
		String jetonRecu = request.getJetonQuestions();

		String idMinistereUtilisateur = getFirstMinistereLoginFromSession(session);
		Set<String> setIdMinistereUtilisateur = getMinisteresIdSetFromLogin(session);

		if (!hasRightAndOrigineUtilisateur(session, webservice, null)) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(USER_NON_AUTORISE);
			return response;
		}

		JetonServiceDto dto = null;
		try {
			dto = metaChercherDocument(idMinistereUtilisateur, jetonRecu, webservice, request.getIdQuestions());
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

			JetonService jetonService = ReponsesServiceLocator.getJetonService();
			Long numeroJetonSuivant = jetonService.getNumeroJetonMaxForWS(session, idMinistereUtilisateur, webservice)
					.longValue();

			if (numeroJetonSuivant < 0) {
				numeroJetonSuivant = 0L;
			}
			String message = JETON_NON_ADEQUAT + " Dernier jeton fourni : " + numeroJetonSuivant.toString();
			response.setMessageErreur(message);
			LOGGER.error(session, STLogEnumImpl.FAIL_GET_JETON_TEC);
			return response;
		}

		if (dto.getNextJetonNumber() != null) {
			String jetonSortant = dto.getNextJetonNumber().toString();
			response.setJetonQuestions(jetonSortant);
		}

		if (dto.isLastSending() != null) {
			response.setDernierRenvoi(dto.isLastSending());
		}

		List<DocumentModel> docList = dto.getDocumentList();

		if (docList == null) {
			// log l'action dans le journal d'administration
			logWebServiceAction(ReponsesEventConstant.WEBSERVICE_CHERCHER_QUESTION_EVENT,
					ReponsesEventConstant.WEBSERVICE_CHERCHER_QUESTION_COMMENT);
			return response;
		}
		List<Questions> resultQuestions = new ArrayList<Questions>();

		if (!docList.isEmpty()) {
			Set<String> forbiddenDossiers = new HashSet<String>();
			for (DocumentModel documentModel : docList) {
				fr.dila.reponses.api.cases.Question appQuestion = documentModel
						.getAdapter(fr.dila.reponses.api.cases.Question.class);
				QuestionId qid = WsUtils.getQuestionIdFromQuestion(appQuestion);
				Dossier dossier = null;
				try {
					dossier = getDossierFromQuestionId(qid);
				} catch (ClientException e) {
					LOGGER.error(session, STLogEnumImpl.FAIL_GET_DOSSIER_TEC, e);
					response.setStatut(TraitementStatut.KO);
					if (response.getMessageErreur() == null) {
						response.setMessageErreur("");
					}
					response.setMessageErreur(response.getMessageErreur() + RECUPERATION_QUESTION_IMPOSSIBLE
							+ qid.getNumeroQuestion() + DOSSIER_INTROUVABLE);
					continue;
				}

				if (!setIdMinistereUtilisateur.contains(dossier.getIdMinistereAttributaireCourant())) {
					LOGGER.error(session, ReponsesLogEnumImpl.FAIL_GET_QUESTION_TEC, dossier);
					forbiddenDossiers.add(appQuestion.getSourceNumeroQuestion());
					continue;
				}
				resultQuestions.add(getXsdQuestionsFromQuestion(dossier, appQuestion));
				// log dans le journal technique
				logWebServiceActionDossier(ReponsesEventConstant.WEBSERVICE_CHERCHER_QUESTION_EVENT,
						ReponsesEventConstant.WEBSERVICE_CHERCHER_QUESTION_COMMENT, dossier);
			}
			if (!forbiddenDossiers.isEmpty()) {
				final StringBuilder responseError = new StringBuilder();
				if (response.getMessageErreur() != null) {
					responseError.append(response.getMessageErreur());
				}
				responseError.append(" - ");
				responseError.append("Vous ne disposez pas de droits de visibilité sur les questions suivantes : ");
				responseError.append(StringUtil.join(forbiddenDossiers, ", ", ""));
				response.setMessageErreur(responseError.toString());
			}
			response.getQuestions().addAll(resultQuestions);
		}

		// log l'action dans le journal d'administration
		if (resultQuestions.isEmpty()) {
			logWebServiceAction(ReponsesEventConstant.WEBSERVICE_CHERCHER_QUESTION_EVENT,
					ReponsesEventConstant.WEBSERVICE_CHERCHER_QUESTION_COMMENT);
		}

		return response;
	}

	// ////////////////////////////////////////////////////////////
	//
	// CHERCHER ERRATA QUESTIONS
	// chercherQuestions
	// ////////////////////////////////////////////////////////////

	/**
	 * Webservice chercherErrataQuestions
	 * 
	 * @param request
	 * @return
	 */
	public ChercherErrataQuestionsResponse chercherErrataQuestions(ChercherErrataQuestionsRequest request) {
		ChercherErrataQuestionsResponse response = new ChercherErrataQuestionsResponse();

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
			LOGGER.error(session, ReponsesLogEnumImpl.FAIL_ACCESS_WSQUESTION_TEC, e);
			return response;
		}

		response.setStatut(TraitementStatut.OK);
		response.setDernierRenvoi(true);
		response.setJetonErrata("");

		String idMinistereUtilisateur = getFirstMinistereLoginFromSession(session);
		Set<String> setIdMinistereUtilisateur = getMinisteresIdSetFromLogin(session);
		String webservice = STWebserviceConstant.CHERCHER_ERRATA_QUESTIONS;
		String jetonRecu = request.getJetonErrata();

		if (!hasRightAndOrigineUtilisateur(session, webservice, null)) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(USER_NON_AUTORISE);
			return response;
		}

		JetonServiceDto dto = null;
		try {
			dto = metaChercherDocument(idMinistereUtilisateur, jetonRecu, webservice, request.getIdQuestions());
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

			JetonService jetonService = ReponsesServiceLocator.getJetonService();
			Long numeroJetonSuivant = jetonService.getNumeroJetonMaxForWS(session, idMinistereUtilisateur, webservice)
					.longValue();

			if (numeroJetonSuivant < 0) {
				numeroJetonSuivant = 0L;
			}
			String message = JETON_NON_ADEQUAT + " Dernier jeton fourni : " + numeroJetonSuivant.toString();
			response.setMessageErreur(message);
			LOGGER.error(session, STLogEnumImpl.FAIL_GET_JETON_TEC);
			return response;
		}

		if (dto.getNextJetonNumber() != null) {
			String jetonSortant = dto.getNextJetonNumber().toString();
			response.setJetonErrata(jetonSortant);
		}
		if (dto.isLastSending() != null) {
			response.setDernierRenvoi(dto.isLastSending());
		}

		List<DocumentModel> docList = dto.getDocumentList();

		if (docList == null) {
			// log l'action dans le journal d'administration
			logWebServiceAction(ReponsesEventConstant.WEBSERVICE_CHERCHER_ERRATA_QUESTION_EVENT,
					ReponsesEventConstant.WEBSERVICE_CHERCHER_ERRATA_QUESTION_COMMENT);
			return response;
		}
		List<ErratumQuestion> resultQuestions = new ArrayList<ErratumQuestion>();
		if (docList.size() > 0) {
			for (DocumentModel documentModel : docList) {
				fr.dila.reponses.api.cases.Question appQuestion = documentModel
						.getAdapter(fr.dila.reponses.api.cases.Question.class);
				QuestionId qid = WsUtils.getQuestionIdFromQuestion(appQuestion);
				Dossier dossier = null;
				try {
					dossier = getDossierFromQuestionId(qid);
				} catch (ClientException e) {
					response.setStatut(TraitementStatut.KO);
					if (response.getMessageErreur() == null) {
						response.setMessageErreur("");
					}
					response.setMessageErreur(response.getMessageErreur() + RECUPERATION_QUESTION_IMPOSSIBLE
							+ qid.getNumeroQuestion() + DOSSIER_INTROUVABLE);
					LOGGER.error(session, STLogEnumImpl.FAIL_GET_DOSSIER_TEC, e);
					continue;
				}
				if (!setIdMinistereUtilisateur.contains(dossier.getIdMinistereAttributaireCourant())) {
					response.setStatut(TraitementStatut.KO);
					if (response.getMessageErreur() == null) {
						response.setMessageErreur("");
					}
					response.setMessageErreur(response.getMessageErreur()
							+ "Impossible de récupérer l'errata de la question " + qid.getNumeroQuestion()
							+ " : le dossier appartient à un autre ministere.\n");
					continue;
				}
				resultQuestions.add(getErratumQuestionFromQuestion(appQuestion, resultQuestions));
			}
			response.getErrata().addAll(resultQuestions);
		}
		// log l'action dans le journal d'administration
		logWebServiceAction(ReponsesEventConstant.WEBSERVICE_CHERCHER_ERRATA_QUESTION_EVENT,
				ReponsesEventConstant.WEBSERVICE_CHERCHER_ERRATA_QUESTION_COMMENT);
		return response;
	}

	// ////////////////////////////////////////////////////////////
	//
	// CHERCHER CHANGEMENT D ETAT QUESTIONS
	//
	// ////////////////////////////////////////////////////////////
	/**
	 * Webservice chercherChangementDEtatQuestions / Bouchon
	 * 
	 * @param request
	 * @return
	 */
	public ChercherChangementDEtatQuestionsResponse chercherChangementDEtatQuestions(
			ChercherChangementDEtatQuestionsRequest request) {
		ChercherChangementDEtatQuestionsResponse response = new ChercherChangementDEtatQuestionsResponse();

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
			LOGGER.error(session, ReponsesLogEnumImpl.FAIL_ACCESS_WSQUESTION_TEC, e);
			return response;
		}

		response.setStatut(TraitementStatut.OK);
		response.setDernierRenvoi(true);
		response.setJetonChangementsEtat("");

		String webservice = STWebserviceConstant.CHERCHER_CHANGEMENT_ETAT_QUESTION;
		String jetonRecu = request.getJetonChangementsEtat();

		String idEntiteStructurelle = getFirstMinistereLoginFromSession(session);
		Set<String> setIdMinistereUtilisateur = getMinisteresIdSetFromLogin(session);

		QuestionId dtoPourOrigineUtilisateur = new QuestionId();
		if (!hasRightAndOrigineUtilisateur(session, webservice, dtoPourOrigineUtilisateur)) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(USER_NON_AUTORISE);
			return response;
		}

		JetonServiceDto dto = null;
		try {
			dto = metaChercherDocument(idEntiteStructurelle, jetonRecu, webservice, request.getIdQuestions());
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

			JetonService jetonService = ReponsesServiceLocator.getJetonService();
			Long numeroJetonSuivant = jetonService.getNumeroJetonMaxForWS(session, idEntiteStructurelle, webservice);

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
			response.setJetonChangementsEtat(jetonSortant);
		}
		if (dto.isLastSending() != null) {
			response.setDernierRenvoi(dto.isLastSending());
		}
		List<DocumentModel> docList = dto.getDocumentList();

		if (docList != null && !docList.isEmpty()) {
			List<ChangementEtatQuestion> listeChangementEtatQuestion = new ArrayList<ChangementEtatQuestion>();
			response.setMessageErreur("");
			for (DocumentModel documentModel : docList) {
				fr.dila.reponses.api.cases.Question appQuestion = documentModel
						.getAdapter(fr.dila.reponses.api.cases.Question.class);
				QuestionId qid = WsUtils.getQuestionIdFromQuestion(appQuestion);
				Dossier dossier = null;
				try {
					dossier = getDossierFromQuestionId(qid);
				} catch (ClientException e) {
					response.setStatut(TraitementStatut.KO);
					if (response.getMessageErreur() == null) {
						response.setMessageErreur("");
					}
					response.setMessageErreur(response.getMessageErreur() + " Impossible de récupérer la question "
							+ qid.getNumeroQuestion() + DOSSIER_INTROUVABLE);
					LOGGER.error(session, STLogEnumImpl.FAIL_GET_DOSSIER_TEC, e);
					continue;
				}

				ChangementEtatQuestion ceq = new ChangementEtatQuestion();
				ceq.setIdQuestion(qid);

				if (!setIdMinistereUtilisateur.contains(dossier.getIdMinistereAttributaireCourant())) {
					response.setStatut(TraitementStatut.KO);
					if (response.getMessageErreur() == null) {
						response.setMessageErreur("");
					}
					response.setMessageErreur(response.getMessageErreur().toString()
							+ " Impossible de rechercher La question" + qid.getNumeroQuestion()
							+ " : origine (AN/SENAT) différente de celle de l'utilisateur\n");
					continue;
				}

				QuestionStateChange selectedEtatQuestion = appQuestion.getEtatQuestion(session);
				if (jetonSortant != null) {
					// MODE RECUPERATION SUR LES DERNIERS CHANGEMENTS
					List<QuestionStateChange> qscList = appQuestion.getEtatQuestionHistorique(session);
					if (qscList != null && !qscList.isEmpty()) {

						// Si une question a plusieurs chgt d'etat elle peut être listée plusieurs fois dans la liste
						// des questions
						// position = 1 <=> on traite le dernier errata.
						// si dans les résultats on a deja N changement pour cette question on ajoute le precedent
						int position = 1;
						for (ChangementEtatQuestion changementEtatQuestion : listeChangementEtatQuestion) {
							QuestionId eqId = changementEtatQuestion.getIdQuestion();
							if (idQuestionsAreEquals(qid, eqId)) {
								position++;
							}
						}

						int itemPosition = qscList.size() - position < 0 ? 0 : qscList.size() - position;
						selectedEtatQuestion = qscList.get(itemPosition);
					}
				}

				ceq.setDateModif(calendarToXMLGregorianCalendar(selectedEtatQuestion.getChangeDate()));
				ceq.setTypeModif(getXsdVersionFromEtatQuestion(selectedEtatQuestion.getNewState()));
				listeChangementEtatQuestion.add(ceq);
			}
			response.getChangementsEtat().addAll(listeChangementEtatQuestion);
		}

		// log l'action dans le journal d'administration
		logWebServiceAction(ReponsesEventConstant.WEBSERVICE_CHERCHER_CHANGEMENT_ETAT_QUESTION_EVENT,
				ReponsesEventConstant.WEBSERVICE_CHERCHER_CHANGEMENT_ETAT_QUESTION_COMMENT);
		return response;
	}

	// ////////////////////////////////////////////////////////////
	//
	// ENVOYER QUESTION
	//
	// ////////////////////////////////////////////////////////////

	/**
	 * Webservice envoyerQuestions / Fonctionnel
	 * 
	 * @param request
	 * @return
	 */
	public EnvoyerQuestionsResponse envoyerQuestions(EnvoyerQuestionsRequest request) {
		EnvoyerQuestionsResponse response = new EnvoyerQuestionsResponse();

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
		} catch (Exception e) {
			ResultatTraitement resultatTraitement = new ResultatTraitement();
			resultatTraitement.setStatut(TraitementStatut.KO);
			resultatTraitement.setMessageErreur(INFO_ACCES_APPLI_KO);
			response.getResultatTraitement().add(resultatTraitement);
			LOGGER.error(session, ReponsesLogEnumImpl.FAIL_ACCESS_WSQUESTION_TEC, e);
			return response;
		}

		try {
			List<ResultatTraitement> resultats = importQuestions(request.getQuestionReponse());
			response.getResultatTraitement().addAll(resultats);
		} catch (Exception exc) {
			ResultatTraitement resultatTraitement = new ResultatTraitement();
			resultatTraitement.setStatut(TraitementStatut.KO);
			resultatTraitement.setMessageErreur("Echec de l'import des questions, cause :" + exc.getMessage());
			response.getResultatTraitement().add(resultatTraitement);
			LOGGER.error(session, ReponsesLogEnumImpl.FAIL_SAVE_QUESTION_TEC, exc);
			return response;
		}

		// log dans le journal d'administration
		logWebServiceAction(ReponsesEventConstant.WEBSERVICE_ENVOYER_QUESTIONS_EVENT,
				ReponsesEventConstant.WEBSERVICE_ENVOYER_QUESTIONS_COMMENT);

		return response;
	}

	protected List<ResultatTraitement> importQuestions(List<QuestionReponse> questionReponses) {
		List<ResultatTraitement> resultats = new ArrayList<ResultatTraitement>();

		List<String> userGroupList = ((SSPrincipal) session.getPrincipal()).getGroups();
		if (!userGroupList.contains(STWebserviceConstant.ENVOYER_QUESTIONS)) {
			ResultatTraitement resultat = new ResultatTraitement();
			resultat.setStatut(TraitementStatut.KO);
			resultat.setMessageErreur("Vous n'êtes pas autorisé à utiliser ce service d'envoi de questions.");
			resultats.add(resultat);
			return resultats;
		}

		QuestionSource origineUtilisateur = null;
		Boolean pourInjection = false;

		if (userGroupList.contains(STWebserviceConstant.PROFIL_AN)
				&& userGroupList.contains(STWebserviceConstant.PROFIL_SENAT)) {
			pourInjection = true;
		} else if (userGroupList.contains(STWebserviceConstant.PROFIL_AN)) {
			origineUtilisateur = QuestionSource.AN;
		} else if (userGroupList.contains(STWebserviceConstant.PROFIL_SENAT)) {
			origineUtilisateur = QuestionSource.SENAT;
		} else {
			ResultatTraitement resultat = new ResultatTraitement();
			resultat.setStatut(TraitementStatut.KO);
			resultat.setMessageErreur(ORIGINE_UTILISATEUR_INCONNUE);
			resultats.add(resultat);
			return resultats;
		}

		resultats = importQuestions(questionReponses, pourInjection, origineUtilisateur);
		return resultats;
	}

	/**
	 * Extrait la reponse eventuelle et le fichier associé de l'objet questionReponse
	 * 
	 * @param questionReponse
	 * @param fichiers
	 *            recoit les fichier de la reponse
	 * @return la reponse s'il y a
	 */
	protected Reponse reponseFromQuestionReponse(QuestionReponse questionReponse, List<Fichier> fichiers) {

		ReponsePubliee reponse = questionReponse.getReponse();

		Reponse reponseAdapted = null;

		if (reponse != null && reponse.getTexteReponse() != null) {

			String texteReponse = reponse.getTexteReponse();
			reponseAdapted = createReponseModel();
			reponseAdapted.setTexteReponse(texteReponse.trim());
			reponseAdapted.setIdAuteurReponse(Integer.valueOf(reponse.getMinistreReponse().getId()).toString().trim());
			if (reponse.getDateJo() != null) {
				reponseAdapted.setDateJOreponse(reponse.getDateJo().toGregorianCalendar());
			}
			if (reponse.getPageJo() != null) {
				reponseAdapted.setPageJOreponse(reponse.getPageJo().longValue());
			}
			fichiers.addAll(reponse.getFichiersJoints());
		}

		return reponseAdapted;
	}

	protected List<ResultatTraitement> importQuestions(List<QuestionReponse> questionReponses, boolean pourInjection,
			QuestionSource origineUtilisateur) {
		// Chargement des services
		final AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();
		final DossierDistributionService dossierDistributionservice = ReponsesServiceLocator
				.getDossierDistributionService();
		final VocabularyService vocabularyService = STServiceLocator.getVocabularyService();
		final STParametreService paramService = STServiceLocator.getSTParametreService();

		List<ResultatTraitement> resultats = new ArrayList<ResultatTraitement>();
		final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
		
		for (QuestionReponse questionReponse : questionReponses) {

			if (!isTransactionAlive()) {
				TransactionHelper.startTransaction();
			}

			List<Fichier> fichiers = new ArrayList<Fichier>();
			Reponse reponseAdapted = reponseFromQuestionReponse(questionReponse, fichiers);

			// On récupère toutes les question alloties
			List<fr.dila.reponses.api.cases.Question> questionAlloties = new ArrayList<fr.dila.reponses.api.cases.Question>();
			for (Question question : questionReponse.getQuestion()) {

				// initialisation de l'accusé de réception
				ResultatTraitement resultat = new ResultatTraitement();
				resultat.setStatut(TraitementStatut.OK);
				resultats.add(resultat);

				boolean hasError = false;
				QuestionId idQuestion = question.getIdQuestion();
				resultat.setIdQuestion(idQuestion);

				try {

					String delaiQuestionSignalee = paramService.getParametreValue(session,
							STParametreConstant.DELAI_QUESTION_SIGNALEE);
					
					
					if (!isValidIdQuestion(idQuestion)) {
						throw new IllegalArgumentException("La question ayant l'identifiant " + idQuestion
								+ " n'a pas un identifiant valide");
					}

					if (!pourInjection && idQuestion.getSource() != origineUtilisateur) {
						resultat.setStatut(TraitementStatut.KO);
						resultat.setMessageErreur("Vous n'êtes pas autorisé à créer des questions d'une autre origine (AN/SENAT) que celle associée à votre poste");
						hasError = true;
						continue;
					}

					ValidatorResult validResult = EnvoyerQuestionValidator.validateQuestionData(question);
					if (!validResult.isValid()) {
						resultat.setStatut(TraitementStatut.KO);
						resultat.setMessageErreur(validResult.getErrorMsg());
						continue;
					}

					Integer numeroQuestion = idQuestion.getNumeroQuestion();

					// La question existe ?
					if (dossierDistributionservice.isExistingQuestion(session, numeroQuestion, idQuestion.getType()
							.toString(), idQuestion.getSource().toString(), idQuestion.getLegislature())) {
						resultat.setStatut(TraitementStatut.KO);
						StringBuffer strBuffer = new StringBuffer("La question [");
						strBuffer.append(idQuestion.getType().toString()).append("-")
								.append(idQuestion.getSource().toString()).append("-")
								.append(idQuestion.getLegislature()).append("-").append(numeroQuestion)
								.append("] existe deja");
						resultat.setMessageErreur(strBuffer.toString());
						hasError = true;
						continue;
					}

					// creation de la question
					fr.dila.reponses.api.cases.Question appQuestion = createQuestionModel();
					appQuestion.setNumeroQuestion(numeroQuestion.longValue());
					appQuestion.setTypeQuestion(idQuestion.getType().value());
					appQuestion.setOrigineQuestion(idQuestion.getSource().toString().trim());
					appQuestion.setLegislatureQuestion(Long.valueOf(idQuestion.getLegislature()));
					appQuestion.setTexteJoint(stripCDATA(question.getTexteJoint()));

					String etatQuestion = getEtatQuestionFromXsd(question.getEtatQuestion());

					Calendar dateReceptionQuestion = GregorianCalendar.getInstance();
					appQuestion.setDateReceptionQuestion(dateReceptionQuestion);

					if (question.getPageJo() != null) {
						appQuestion.setPageJO(Integer.valueOf(question.getPageJo()).toString());
					}

					if (question.getDatePublicationJo() != null) {
						appQuestion.setDatePublicationJO(question.getDatePublicationJo().toGregorianCalendar());
					}

					// Ministère : par défaut le ministère attributaire, sinon le ministère de dépot
					Ministre ministreAttributaire = question.getMinistreAttributaire();
					Ministre ministreUtilise = ministreAttributaire;
					if (ministreAttributaire == null) {
						ministreUtilise = question.getMinistreDepot();
					}

					// Si la question est écrite et en cours, on vérifie l'existence du ministère dans Réponses
					if (QuestionType.QE.equals(idQuestion.getType())
							&& (etatQuestion.equals(VocabularyConstants.ETAT_QUESTION_EN_COURS)
									|| etatQuestion.equals(VocabularyConstants.ETAT_QUESTION_SIGNALEE)
									|| etatQuestion.equals(VocabularyConstants.ETAT_QUESTION_RAPPELE) || etatQuestion
									.equals(VocabularyConstants.ETAT_QUESTION_RENOUVELEE))) {
						EntiteNode entiteNode = ministeresService.getEntiteNode(Integer.toString(ministreUtilise
								.getId()));
						if (entiteNode == null || !entiteNode.isActive()) {
							resultat.setStatut(TraitementStatut.KO);
							resultat.setMessageErreur("Ministère inconnu ou inactif dans l'application Réponses");
							continue;
						}
					}

					appQuestion
							.setIdMinistereInterroge(Integer.valueOf(question.getMinistreDepot().getId()).toString());
					appQuestion.setTitreJOMinistere(SANITIZER.escape(question.getMinistreDepot().getTitreJo().trim()));
					appQuestion.setIntituleMinistere(SANITIZER.escape(question.getMinistreDepot()
							.getIntituleMinistere().trim()));

					// Auteur
					Auteur auteur = question.getAuteur();
					appQuestion.setCiviliteAuteur(SANITIZER.escape(auteur.getCivilite().name().trim()));
					appQuestion.setNomAuteur(SANITIZER.escape(auteur.getNom().trim()));
					appQuestion.setPrenomAuteur(SANITIZER.escape(auteur.getPrenom().trim()));
					appQuestion.setIdMandat(SANITIZER.escape(auteur.getIdMandat().trim()));
					appQuestion.setCirconscriptionAuteur(SANITIZER.escape(auteur.getCirconscription().trim()));

					// Metadonnee Dictionnaire

					String groupePol = SANITIZER.escape(auteur.getGrpPol().trim()) + " ("
							+ idQuestion.getSource().toString().trim() + ")";
					appQuestion.setGroupePolitique(groupePol);

					try {
						if (!vocabularyService.hasDirectoryEntry(VocabularyConstants.GROUPE_POLITIQUE, groupePol)) {
							DocumentModel newGroupePol = vocabularyService
									.getNewEntry(VocabularyConstants.GROUPE_POLITIQUE);
							PropertyUtil.setProperty(newGroupePol, STVocabularyConstants.VOCABULARY,
									STVocabularyConstants.COLUMN_ID, groupePol);
							PropertyUtil.setProperty(newGroupePol, STVocabularyConstants.VOCABULARY,
									STVocabularyConstants.COLUMN_LABEL, groupePol);
							vocabularyService.createDirectoryEntry(VocabularyConstants.GROUPE_POLITIQUE, newGroupePol);
						}
					} catch (Exception e) {
						LOGGER.error(session, SSLogEnumImpl.FAIL_CREATE_GROUPE_POLITIQUE_TEC, e);
					}

					// gestion du choice entre les deux type de métadonnées d'indexation

					// Recuperation des informations d'indexation AN

					if (QuestionSource.AN.equals(idQuestion.getSource()) && !hasError) {
						try {
							IndexationAn indexationAn = question.getIndexationAn();
							ValidatorResult validIndexation = EnvoyerQuestionValidator
									.validateIndexationAN(indexationAn);
							if (!validIndexation.isValid()) {
								resultat.setStatut(TraitementStatut.KO);
								resultat.setMessageErreur(validIndexation.getErrorMsg());
								continue;
							}
							// indexationAn != null (testé par validateIndexationAN)
							List<String> analyses = indexationAn.getAnalyse();
							if (analyses != null && !analyses.isEmpty()) {
								appQuestion.setAssNatAnalyses(WsUtils.deduplicateIndexation(analyses));
							}
							String rubrique = indexationAn.getRubrique();
							// rubrique != null (testé par validateIndexationAN)
							appQuestion.setAssNatRubrique(Collections.singletonList(rubrique));

							String rubriqueTa = indexationAn.getRubriqueTa();
							if (rubriqueTa != null) {
								appQuestion.setAssNatTeteAnalyse(Collections.singletonList(rubriqueTa));
							}
						} catch (Exception e) {
							resultat.setStatut(TraitementStatut.KO);
							resultat.setMessageErreur("Assemblee Nationale : Probleme lors du traitement de l'indexation sur les analyses");
							hasError = true;
							LOGGER.error(session, ReponsesLogEnumImpl.FAIL_PROCESS_INDEX_ANALYSE_TEC, e);
							continue;
						}
					}

					// Recuperation des informations d'indexation SENAT
					if (QuestionSource.SENAT.equals(idQuestion.getSource()) && !hasError) {
						try {

							String titreSenat = question.getTitreSenat();
							IndexationSenat indexationSenat = question.getIndexationSenat();
							ValidatorResult validIndexation = EnvoyerQuestionValidator.validateIndexatioSenat(
									titreSenat, indexationSenat);
							if (!validIndexation.isValid()) {
								resultat.setStatut(TraitementStatut.KO);
								resultat.setMessageErreur(validIndexation.getErrorMsg());
								continue;
							}

							// titreSenat != null (testé par validateIndexatioSenat)
							appQuestion.setSenatQuestionTitre(question.getTitreSenat().trim());

							// indexationSenat.getTheme() != null (testé par validateIndexatioSenat)
							appQuestion
									.setSenatQuestionThemes(WsUtils.deduplicateIndexation(indexationSenat.getTheme()));

							// indexationSenat.getRubrique() != null (testé par validateIndexatioSenat)
							appQuestion.setSenatQuestionRubrique(WsUtils.deduplicateIndexation(indexationSenat
									.getRubrique()));

							if (indexationSenat.getRenvois() != null) {
								List<String> renvois = indexationSenat.getRenvois().getRenvoi();
								// size < limit (testé par validateIndexatioSenat) limit = 3
								appQuestion.setSenatQuestionRenvois(WsUtils.deduplicateIndexation(renvois));
							}
						} catch (Exception e) {
							resultat.setStatut(TraitementStatut.KO);
							resultat.setMessageErreur("SENAT : Problème de traitement des indexations et des rubriques");
							hasError = true;
							LOGGER.error(session, ReponsesLogEnumImpl.FAIL_PROCESS_INDEX_ANALYSE_TEC, e);
							continue;
						}
					}

					// Mise à jour des vocabulaires d'indexation
					try {
						WsUtils.synchronizeVocabulary(appQuestion);
					} catch (Exception e) {
						LOGGER.error(session, ReponsesLogEnumImpl.FAIL_CREATE_INDEX_ANALYSE_TEC, e);
					}

					// Texte de la question
					appQuestion.setTexteQuestion(stripCDATA(question.getTexte()));

					// Gestion des rappels
					QuestionId idQuestionRappelee = question.getRappel();
					Dossier dossierRappele = null;

					if (idQuestionRappelee != null) {
						if (!isValidIdQuestion(idQuestionRappelee)) {
							resultat.setStatut(TraitementStatut.KO);
							resultat.setMessageErreur("Impossible de rappeler cette question, flux mal formé");
							continue;
						}
						if (idQuestionRappelee.getSource() != idQuestion.getSource()) {
							resultat.setStatut(TraitementStatut.KO);
							resultat.setMessageErreur("Impossible de rappeler une question d'un autre parlement (AN/SENAT) que celui de l'utilisateur.");
							continue;
						}
						try {
							dossierRappele = getDossierFromQuestionId(idQuestionRappelee);

							if (!dossierRappele.hasFeuilleRoute()) {
								resultat.setStatut(TraitementStatut.KO);
								resultat.setMessageErreur("Le dossier rappelé n'est pas conforme");
								continue;
							}

							fr.dila.reponses.api.cases.Question questionDossier = dossierRappele.getQuestion(session);
							if (questionDossier != null) {
								QuestionStateChange quState = questionDossier.getEtatQuestion(session);
								if (quState == null
										|| quState.getNewState().equals(VocabularyConstants.ETAT_QUESTION_RETIREE)
										|| quState.getNewState().equals(VocabularyConstants.ETAT_QUESTION_CADUQUE)
										|| quState.getNewState()
												.equals(VocabularyConstants.ETAT_QUESTION_CLOTURE_AUTRE)) {
									resultat.setStatut(TraitementStatut.KO);
									resultat.setMessageErreur("Etat dossier rappelé invalide");
									continue;
								}
							} else {
								resultat.setStatut(TraitementStatut.KO);
								resultat.setMessageErreur("Etat dossier rappelé invalide");
								continue;
							}

							if (!allotissementService.validateDossierRappel(session, dossierRappele,
									Integer.valueOf(ministreUtilise.getId()).toString(), appQuestion)) {
								// Le dossier n'est pas valide pour le rappel, pas de lot, mais pas d'erreur
								dossierRappele = null;
							}
						} catch (ClientException e) {
							resultat.setStatut(TraitementStatut.KO);
							resultat.setMessageErreur("Dossier rappelé introuvable.");
							LOGGER.error(session, STLogEnumImpl.FAIL_GET_DOSSIER_TEC, e);
							continue;
						}
					}

					Dossier savedDossier = null;
					fr.dila.reponses.api.cases.Question savedQuestion = null;
					// CREATION DU DOSSIER
					if (!hasError) {
						// Le ministère attributaire du dossier est passé en paramètre
						savedDossier = createAndSaveDossier(appQuestion, reponseAdapted,
								Integer.valueOf(ministreUtilise.getId()).toString(), dossierRappele == null,
								etatQuestion).getAdapter(Dossier.class);
						session.save();
					}

					// Gestion des fichiers joints
					if (fichiers != null && !fichiers.isEmpty()) {
						ResultatTraitement rtFichiers = createFichiers(fichiers, savedDossier);
						if (rtFichiers != null) {
							resultat.setMessageErreur(rtFichiers.getMessageErreur());
							resultat.setStatut(rtFichiers.getStatut());
							continue;
						}
					}

					if (savedDossier != null) {
						savedQuestion = savedDossier.getQuestion(session);
					}
					// état en cours + autre que QE => état répondu
					if (etatQuestion != null && savedQuestion != null) {
						if (savedQuestion.getDocument().getRef() == null
								|| !session.exists(savedQuestion.getDocument().getRef())) {
							session.createDocument(savedQuestion.getDocument());
						}
						if (etatQuestion.equals(VocabularyConstants.ETAT_QUESTION_EN_COURS)
								&& !QuestionType.QE.equals(idQuestion.getType())) {
							etatQuestion = VocabularyConstants.ETAT_QUESTION_REPONDU;
						}
						savedQuestion.setEtatQuestion(session, etatQuestion, new GregorianCalendar(), delaiQuestionSignalee);
						session.saveDocument(savedQuestion.getDocument());

						// Gestion du retrait
						if (question.getDateRetrait() != null
								&& (VocabularyConstants.ETAT_QUESTION_REPONDU.equals(etatQuestion)
										|| VocabularyConstants.ETAT_QUESTION_RETIREE.equals(etatQuestion)
										|| VocabularyConstants.ETAT_QUESTION_CADUQUE.equals(etatQuestion) || VocabularyConstants.ETAT_QUESTION_CLOTURE_AUTRE
										.equals(etatQuestion))) {
							dossierDistributionservice.retirerFeuilleRoute(session, savedDossier);

							savedQuestion.setEtatQuestion(session, etatQuestion, question.getDateRetrait()
									.toGregorianCalendar(), delaiQuestionSignalee);
							session.saveDocument(savedQuestion.getDocument());
						}
					}

					// On commit pour enregister la question avant la gestion des rappels
					TransactionHelper.commitOrRollbackTransaction();
					if (!isTransactionAlive()) {
						TransactionHelper.startTransaction();
					}

					fr.dila.reponses.api.cases.Question questionRappel = null;

					// Gestion de l'allotissement
					if (EtatQuestion.EN_COURS.equals(question.getEtatQuestion())) {
						questionRappel = savedDossier.getQuestion(session);
						questionAlloties.add(questionRappel);
					}

					// Gestion des rappels
					if (dossierRappele != null && dossierRappele.hasFeuilleRoute()) {
						fr.dila.reponses.api.cases.Question questionRappelee = dossierRappele.getQuestion(session);

						questionRappelee.setEtatQuestion(session, VocabularyConstants.ETAT_QUESTION_RAPPELE,
								Calendar.getInstance(), delaiQuestionSignalee);
						session.saveDocument(questionRappelee.getDocument());

						if (questionRappel == null) {
							questionRappel = savedDossier.getQuestion(session);
						}

						// creation ou mise a jour du lot
						allotissementService.createOrAddToLotRappel(questionRappelee, questionRappel, session);

						// Mise à jour de l'historique des attributions
						savedDossier.addHistoriqueAttribution(session,
								STVocabularyConstants.FEUILLE_ROUTE_TYPE_CREATION_INSTANCIATION,
								savedDossier.getIdMinistereAttributaireCourant());
						session.saveDocument(savedDossier.getDocument());

						session.save();

						// Envoi de mail pour signaler les changements d'états
						final ReponsesMailService rms = ReponsesServiceLocator.getReponsesMailService();
						try {
							rms.sendMailAfterStateChangedQuestion(session,
									session.getDocument(savedDossier.getDocument().getRef()),
									VocabularyConstants.ETAT_QUESTION_RAPPELE);
						} catch (ClientException exc) {
							LOGGER.error(session, STLogEnumImpl.FAIL_GET_DOCUMENT_TEC, savedDossier, exc);
						}
					}

				} catch (Exception exc) {
					resultat.setStatut(TraitementStatut.KO);
					resultat.setMessageErreur("Erreur lors de l'injection d'une question");
					TransactionHelper.setTransactionRollbackOnly();
					LOGGER.error(session, ReponsesLogEnumImpl.FAIL_SAVE_QUESTION_TEC, exc);
				}
			} // end for (question)

			// Création de l'allotissement
			if (questionAlloties.size() > 1) {
				fr.dila.reponses.api.cases.Question questionDirectrice = questionAlloties.remove(0);
				allotissementService.createLotWS(questionDirectrice, questionAlloties, session);
			}

			TransactionHelper.commitOrRollbackTransaction();
		} // end for (questionReponse)

		return resultats;
	}

	// ////////////////////////////////////////////////////////////
	//
	// ENVOYER QUESTIONS ERRATA
	//
	// ////////////////////////////////////////////////////////////
	/**
	 * Webservice envoyerQuestionsErrata /
	 * 
	 * @param request
	 * @return
	 */
	public EnvoyerQuestionsErrataResponse envoyerQuestionsErrata(EnvoyerQuestionsErrataRequest request) {
		EnvoyerQuestionsErrataResponse response = new EnvoyerQuestionsErrataResponse();

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
			LOGGER.error(session, ReponsesLogEnumImpl.FAIL_ACCESS_WSQUESTION_TEC, e);
			return response;
		}

		List<String> userGroupList = ((SSPrincipal) session.getPrincipal()).getGroups();
		if (!userGroupList.contains(STWebserviceConstant.ENVOYER_QUESTIONS_ERRATA)) {
			ResultatTraitement resultatTraitement = new ResultatTraitement();
			resultatTraitement.setStatut(TraitementStatut.KO);
			resultatTraitement.setMessageErreur("Utilisateur non autorisé à envoyer des errata de question");
			response.getResultatTraitement().add(resultatTraitement);
			return response;
		}

		QuestionSource origineUtilisateur = null;
		if (userGroupList.contains(STWebserviceConstant.PROFIL_AN)) {
			origineUtilisateur = QuestionSource.AN;
		} else if (userGroupList.contains(STWebserviceConstant.PROFIL_SENAT)) {
			origineUtilisateur = QuestionSource.SENAT;
		} else {
			ResultatTraitement resultatTraitement = new ResultatTraitement();
			resultatTraitement.setStatut(TraitementStatut.KO);
			resultatTraitement.setMessageErreur(ORIGINE_UTILISATEUR_INCONNUE);
			response.getResultatTraitement().add(resultatTraitement);
			return response;
		}

		List<ErratumQuestion> erratumList = request.getErratum();
		if (erratumList == null || erratumList.size() == 0) {
			ResultatTraitement resultatTraitement = new ResultatTraitement();
			resultatTraitement.setStatut(TraitementStatut.KO);
			resultatTraitement.setMessageErreur("Errata absents.");
			response.getResultatTraitement().add(resultatTraitement);
			return response;
		}

		for (ErratumQuestion erratumQuestion : erratumList) {
			ResultatTraitement resultatTraitement = new ResultatTraitement();
			response.getResultatTraitement().add(resultatTraitement);
			resultatTraitement.setStatut(TraitementStatut.OK);

			String texteConsolide = erratumQuestion.getTexteConsolide();
			QuestionId questionIdPourErratum = erratumQuestion.getIdQuestion();
			resultatTraitement.setIdQuestion(questionIdPourErratum);

			if (questionIdPourErratum.getSource() != origineUtilisateur) {
				resultatTraitement.setStatut(TraitementStatut.KO);
				resultatTraitement
						.setMessageErreur("L'utilisateur du webservice n'a pas la même origine (AN/SENAT) que la question");
				continue;
			}

			Integer pageJOErratum = erratumQuestion.getPageJoErratum();
			XMLGregorianCalendar datePublicationErratum = erratumQuestion.getDatePublicationErratum();
			String texteErratum = erratumQuestion.getTexteErratum();
			ErratumType erratumType = erratumQuestion.getType();

			// Test sur les champs
			if (texteConsolide == null || texteConsolide.length() == 0) {
				resultatTraitement.setStatut(TraitementStatut.KO);
				resultatTraitement.setMessageErreur("Veuillez saisir le texte de l'erratum.");
				continue;
			}
			if (erratumType == null) {
				resultatTraitement.setStatut(TraitementStatut.KO);
				resultatTraitement.setMessageErreur("Veuillez saisir un type d'erratum.");
				continue;
			}

			Dossier dossier = null;
			try {
				dossier = getDossierFromQuestionId(questionIdPourErratum);
			} catch (ClientException e) {
				resultatTraitement.setStatut(TraitementStatut.KO);
				resultatTraitement.setMessageErreur(RECUPERATION_QUESTION_IMPOSSIBLE
						+ questionIdPourErratum.getNumeroQuestion() + DOSSIER_INTROUVABLE);
				LOGGER.error(session, STLogEnumImpl.FAIL_GET_DOSSIER_TEC, e);
				continue;
			}

			fr.dila.reponses.api.cases.Question appQuestion = dossier.getQuestion(session);

			if (appQuestion == null) {
				resultatTraitement.setStatut(TraitementStatut.KO);
				resultatTraitement.setMessageErreur("Erreur lors de l'identification de la question "
						+ questionIdPourErratum.getNumeroQuestion());
				continue;
			}
			String etatQuestion = appQuestion.getEtatQuestion(session).getNewState();

			if (VocabularyConstants.ETAT_QUESTION_CADUQUE.equals(etatQuestion)
					|| VocabularyConstants.ETAT_QUESTION_CLOTURE_AUTRE.equals(etatQuestion)
					|| VocabularyConstants.ETAT_QUESTION_REPONDU.equals(etatQuestion)
					|| VocabularyConstants.ETAT_QUESTION_RETIREE.equals(etatQuestion)) {
				resultatTraitement.setStatut(TraitementStatut.KO);
				resultatTraitement
						.setMessageErreur("Impossible d'envoyer un errata aux questions qui sont à l'état : CADUQUE, CLOTURE_AUTRE, REPONDU, RETIREE");
				continue;
			}

			String texteQuestion = appQuestion.getTexteQuestion();

			if (erratumType == ErratumType.CORRECTION) {
				appQuestion.setTexteQuestion(texteConsolide);
			} else if (erratumType == ErratumType.ERRATUM) {
				appQuestion.setTexteQuestion(texteConsolide + "\n\n" + "Texte consolidé "
						+ datePublicationErratum.toString() + " suite à l'erratum   : " + texteErratum
						+ " \n\n Version précédente :" + texteQuestion);
			}

			QErratum erratumAppQuestion = new QErratumImpl();
			if (datePublicationErratum != null) {
				erratumAppQuestion.setDatePublication(datePublicationErratum.toGregorianCalendar());
			}
			if (pageJOErratum != null) {
				erratumAppQuestion.setPageJo(pageJOErratum);
			}
			erratumAppQuestion.setTexteErratum(texteErratum);
			erratumAppQuestion.setTexteConsolide(texteConsolide);
			List<QErratum> erList = appQuestion.getErrata();
			erList.add(erratumAppQuestion);
			appQuestion.setErrata(erList);
			try {
				session.saveDocument(appQuestion.getDocument());
				session.save();

				// Alimentation du service de jeton
				ReponsesServiceLocator.getJetonService().addDocumentInBasket(session,
						STWebserviceConstant.CHERCHER_ERRATA_QUESTIONS, dossier.getIdMinistereAttributaireCourant(),
						appQuestion.getDocument(), appQuestion.getNumeroQuestion().toString(), null, null);

				// Envoi de mail au possesseur de la question

				// On récupere les instances de feuille de route
				final List<DocumentModel> routeInstances = ReponsesServiceLocator.getDossierDistributionService()
						.getDossierRoutes(session, dossier.getDocument());
				final Set<String> mailboxIds = new HashSet<String>();
				for (DocumentModel instance : routeInstances) {

					// pour chaque étape active de la feuille de route
					final List<DocumentModel> routeStepModels = ReponsesServiceLocator.getFeuilleRouteService()
							.getRunningSteps(session, instance.getId());

					// il peut y avoir plusieurs mailboxs.
					for (DocumentModel routeStepModel : routeStepModels) {
						final STRouteStep drs = routeStepModel.getAdapter(STRouteStep.class);
						final String mailboxId = drs.getDistributionMailboxId();
						mailboxIds.add(mailboxId);
					}

				}

				final STParametreService paramService = STServiceLocator.getSTParametreService();
				String texte = paramService.getParametreValue(session,
						STParametreConstant.NOTIFICATION_MAIL_ERRATUM_QUESTION_TEXTE);
				String objet = paramService.getParametreValue(session,
						STParametreConstant.NOTIFICATION_MAIL_ERRATUM_QUESTION_OBJET);
				EventProducer eventProducer = STServiceLocator.getEventProducer();

				for (String mailboxId : mailboxIds) {
					Map<String, Serializable> eventProperties = new HashMap<String, Serializable>();
					eventProperties.put(STEventConstant.SEND_MAIL_NOTIFICATION_MAILBOX_ID, mailboxId);
					eventProperties.put(STEventConstant.SEND_MAIL_NOTIFICATION_OBJET, objet);
					eventProperties.put(STEventConstant.SEND_MAIL_NOTIFICATION_TEXTE, texte);
					eventProperties.put("numero_question", dossier.getNumeroQuestion().toString());
					InlineEventContext inlineEventContext = new InlineEventContext(session, session.getPrincipal(),
							eventProperties);
					eventProducer.fireEvent(inlineEventContext.newEvent(STEventConstant.SEND_MAIL_NOTIFICATION));
				}

			} catch (ClientException e) {
				resultatTraitement.setStatut(TraitementStatut.KO);
				resultatTraitement.setMessageErreur("Erreur à l'erratum de la question  "
						+ questionIdPourErratum.getNumeroQuestion());
				TransactionHelper.setTransactionRollbackOnly();
				LOGGER.error(session, ReponsesLogEnumImpl.FAIL_SAVE_ERRATA_TEC, e);
			}

			logWebServiceActionDossier(ReponsesEventConstant.WEBSERVICE_ENVOYER_QUESTIONS_ERRATA_EVENT,
					ReponsesEventConstant.WEBSERVICE_ENVOYER_QUESTIONS_ERRATA_COMMENT, dossier);
		}

		return response;
	}

	// ////////////////////////////////////////////////////////////
	//
	// CHANGER ETAT QUESTION
	//
	// ////////////////////////////////////////////////////////////

	/**
	 * Webservice changerEtatQuestions
	 * 
	 * @param request
	 * @return
	 */
	public ChangerEtatQuestionsResponse changerEtatQuestions(ChangerEtatQuestionsRequest request) {

		ChangerEtatQuestionsResponse response = new ChangerEtatQuestionsResponse();

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
			LOGGER.error(session, ReponsesLogEnumImpl.FAIL_ACCESS_WSQUESTION_TEC, e);
			return response;
		}

		List<String> userGroupList = ((SSPrincipal) session.getPrincipal()).getGroups();
		if (!userGroupList.contains(STWebserviceConstant.ENVOYER_CHANGEMENT_ETAT)) {
			ResultatTraitement resultatTraitement = new ResultatTraitement();
			resultatTraitement.setStatut(TraitementStatut.KO);
			resultatTraitement.setMessageErreur("Utilisateur non autorisé à changer les états d'une question");
			response.getResultatTraitement().add(resultatTraitement);
			return response;
		}

		QuestionSource origineUtilisateur = null;

		if (userGroupList.contains(STWebserviceConstant.PROFIL_AN)) {
			origineUtilisateur = QuestionSource.AN;
		} else if (userGroupList.contains(STWebserviceConstant.PROFIL_SENAT)) {
			origineUtilisateur = QuestionSource.SENAT;
		} else {
			ResultatTraitement resultatTraitement = new ResultatTraitement();
			resultatTraitement.setStatut(TraitementStatut.KO);
			resultatTraitement.setMessageErreur(ORIGINE_UTILISATEUR_INCONNUE);
			response.getResultatTraitement().add(resultatTraitement);
			return response;
		}

		List<ChangementEtatQuestion> lceq = request.getNouvelEtat();
		if (lceq == null || lceq.size() == 0) {
			ResultatTraitement resultatTraitement = new ResultatTraitement();
			resultatTraitement.setStatut(TraitementStatut.KO);
			resultatTraitement.setMessageErreur("Changement(s) d'état absent(s).");
			response.getResultatTraitement().add(resultatTraitement);
			return response;
		}

		// Chargement des services
		final STLockService lockService = STServiceLocator.getSTLockService();
		final AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();
		final DossierDistributionService dossierDistributionService = ReponsesServiceLocator
				.getDossierDistributionService();
		final JournalService journalService = STServiceLocator.getJournalService();
		final JetonService jetonService = ReponsesServiceLocator.getJetonService();
		final STParametreService paramService = STServiceLocator.getSTParametreService();
		for (ChangementEtatQuestion ceq : lceq) {

			if (!isTransactionAlive()) {
				TransactionHelper.startTransaction();
			}

			ResultatTraitement resultatTraitement = new ResultatTraitement();
			QuestionId idQuestionConcernee = ceq.getIdQuestion();
			if (idQuestionConcernee != null) {
				resultatTraitement.setIdQuestion(idQuestionConcernee);

				if (idQuestionConcernee.getSource() != origineUtilisateur) {
					resultatTraitement.setStatut(TraitementStatut.KO);
					resultatTraitement
							.setMessageErreur("L'utilisateur du webservice n'a pas la même origine (AN/SENAT) que la question");
					response.getResultatTraitement().add(resultatTraitement);
					continue;
				}
			}

			resultatTraitement.setStatut(TraitementStatut.OK);

			EtatQuestion nouvelEtatQuestion = ceq.getTypeModif();
			if (nouvelEtatQuestion == null) {
				resultatTraitement.setStatut(TraitementStatut.KO);
				resultatTraitement.setMessageErreur("Type de changement absent.");
				response.getResultatTraitement().add(resultatTraitement);
				continue;
			}

			GregorianCalendar dateEffet = ceq.getDateModif().toGregorianCalendar();
			if (dateEffet == null) {
				resultatTraitement.setStatut(TraitementStatut.KO);
				resultatTraitement.setMessageErreur("Date de changement absent.");
				response.getResultatTraitement().add(resultatTraitement);
				continue;
			}

			Dossier dossier = null;
			try {
				dossier = getDossierFromQuestionId(idQuestionConcernee);
			} catch (ClientException e) {
				resultatTraitement.setStatut(TraitementStatut.KO);
				resultatTraitement.setMessageErreur(RECUPERATION_QUESTION_IMPOSSIBLE
						+ idQuestionConcernee.getNumeroQuestion() + DOSSIER_INTROUVABLE);
				response.getResultatTraitement().add(resultatTraitement);
				LOGGER.error(session, STLogEnumImpl.FAIL_GET_DOSSIER_TEC, e);
				continue;
			}
			fr.dila.reponses.api.cases.Question appQuestion = dossier.getQuestion(session);

			if (appQuestion == null) {
				resultatTraitement.setStatut(TraitementStatut.KO);
				resultatTraitement.setMessageErreur("La question " + idQuestionConcernee.getNumeroQuestion()
						+ " n'existe pas.");
				response.getResultatTraitement().add(resultatTraitement);
				continue;
			}

			QuestionStateChange ancienEtat = appQuestion.getEtatQuestion(session);
			String appAncienEtatQuestion = ancienEtat != null ? ancienEtat.getNewState() : null;
			String appNouvelEtatQuestion = getEtatQuestionFromXsd(nouvelEtatQuestion);

			if (appNouvelEtatQuestion == null) {
				resultatTraitement.setStatut(TraitementStatut.KO);
				resultatTraitement
						.setMessageErreur("La question n'a aucun état défini, ou l'état soumis est incorrect");
				response.getResultatTraitement().add(resultatTraitement);
				continue;
			}
			if (VocabularyConstants.ETAT_QUESTION_REPONDU.equals(appAncienEtatQuestion)) {
				resultatTraitement.setStatut(TraitementStatut.KO);
				resultatTraitement
						.setMessageErreur("Impossible de changer l'état d'une question dont la réponse a déjà été publiée.");
				response.getResultatTraitement().add(resultatTraitement);
				continue;
			}

			if (VocabularyConstants.ETAT_QUESTION_REPONDU.equals(appAncienEtatQuestion)
					|| VocabularyConstants.ETAT_QUESTION_CADUQUE.equals(appAncienEtatQuestion)
					|| VocabularyConstants.ETAT_QUESTION_CLOTURE_AUTRE.equals(appAncienEtatQuestion)
					|| VocabularyConstants.ETAT_QUESTION_RETIREE.equals(appAncienEtatQuestion)) {
				resultatTraitement.setStatut(TraitementStatut.KO);
				resultatTraitement
						.setMessageErreur("Impossible de changer l'état d'une question dont la réponse est à l'état : "
								+ appAncienEtatQuestion);
				response.getResultatTraitement().add(resultatTraitement);
				continue;
			}

			if (VocabularyConstants.ETAT_QUESTION_REATTRIBUEEE.equals(appAncienEtatQuestion)) {
				resultatTraitement.setStatut(TraitementStatut.KO);
				resultatTraitement
						.setMessageErreur("Impossible d'indiquer une réattribution par le biais des webservices.");
				response.getResultatTraitement().add(resultatTraitement);
				continue;
			}

			if (VocabularyConstants.ETAT_QUESTION_SIGNALEE.equals(appNouvelEtatQuestion)
					&& appQuestion.getEtatSignale()) {
				resultatTraitement.setStatut(TraitementStatut.KO);
				resultatTraitement.setMessageErreur("Question déjà signalée.");
				response.getResultatTraitement().add(resultatTraitement);
				continue;
			}

			// pour ces états : supprimer les étapes futures de la fdr et valider automatiquement l'étape courante
			if (VocabularyConstants.ETAT_QUESTION_REPONDU.equals(appNouvelEtatQuestion)
					|| VocabularyConstants.ETAT_QUESTION_RETIREE.equals(appNouvelEtatQuestion)
					|| VocabularyConstants.ETAT_QUESTION_CADUQUE.equals(appNouvelEtatQuestion)
					|| VocabularyConstants.ETAT_QUESTION_CLOTURE_AUTRE.equals(appNouvelEtatQuestion)) {
				try {

					DocumentModel dossierDoc = dossier.getDocument();

					// Unlock du dossier et de la route si nécéssaire
					Map<String, String> dossierLockInfo = lockService.getLockDetails(session, dossierDoc);
					if (dossierLockInfo.get(STLockService.LOCKER) != null) {
						lockService.unlockDocUnrestricted(session, dossierDoc);
					}
					String idRoute = dossier.getLastDocumentRoute();
					if (idRoute != null) {
						DocumentModel routeDoc = session.getDocument(new IdRef(idRoute));
						Map<String, String> routeLockInfo = lockService.getLockDetails(session, routeDoc);
						if (routeLockInfo.get(STLockService.LOCKER) != null) {
							lockService.unlockDocUnrestricted(session, routeDoc);
						}
					}
					allotissementService.removeDossierFromLotIfNeeded(session, dossier);

					// on recharge le dossier car il a été modifié
					dossierDoc = session.getDocument(dossier.getDocument().getRef());
					dossier = dossierDoc.getAdapter(Dossier.class);
					dossierDistributionService.retirerFeuilleRoute(session, dossier);

					// Journalise l'action
					String comment = "";
					if (VocabularyConstants.ETAT_QUESTION_REPONDU.equals(appNouvelEtatQuestion)) {
						comment = " : question répondue";
					} else if (VocabularyConstants.ETAT_QUESTION_RETIREE.equals(appNouvelEtatQuestion)) {
						comment = " : question retirée";
					} else if (VocabularyConstants.ETAT_QUESTION_CADUQUE.equals(appNouvelEtatQuestion)) {
						comment = " : question caduque";
					} else if (VocabularyConstants.ETAT_QUESTION_CLOTURE_AUTRE.equals(appNouvelEtatQuestion)) {
						comment = " : question cloturée";
					}
					journalService.journaliserActionFDR(session, dossierDoc,
							STEventConstant.EVENT_FEUILLE_ROUTE_STEP_UPDATE,
							ReponsesEventConstant.COMMENT_QUESTION_CHANGEMENT + comment);

				} catch (AllotissementException e) {
					resultatTraitement.setStatut(TraitementStatut.KO);
					resultatTraitement
							.setMessageErreur("Une erreur est survenue lors du retrait du dossier de son lot.");
					response.getResultatTraitement().add(resultatTraitement);
					LOGGER.error(session, ReponsesLogEnumImpl.FAIL_GET_ALLOT_TEC, e);
					continue;
				} catch (Exception e) {
					resultatTraitement.setStatut(TraitementStatut.KO);
					resultatTraitement
							.setMessageErreur("Une erreur est survenue lors de la modification de la feuille de route.");
					response.getResultatTraitement().add(resultatTraitement);
					LOGGER.error(session, SSLogEnumImpl.FAIL_UPDATE_FDR_TEC, e);
					continue;
				}
			}

			try {

				appQuestion.setEtatQuestion(session, appNouvelEtatQuestion, dateEffet,
						paramService.getParametreValue(session, STParametreConstant.DELAI_QUESTION_SIGNALEE));

				session.saveDocument(appQuestion.getDocument());
				session.save();

			} catch (ClientException e1) {
				resultatTraitement.setStatut(TraitementStatut.KO);
				resultatTraitement.setMessageErreur("Une erreur est survenue lors de la sauvegarde de la question : "
						+ e1.getMessage());
				response.getResultatTraitement().add(resultatTraitement);
				LOGGER.error(session, ReponsesLogEnumImpl.FAIL_SAVE_QUESTION_TEC, appQuestion.getDocument(), e1);
				continue;
			}

			try {
				jetonService.addDocumentInBasket(session, STWebserviceConstant.CHERCHER_CHANGEMENT_ETAT_QUESTION,
						dossier.getIdMinistereAttributaireCourant(), appQuestion.getDocument(), appQuestion
								.getNumeroQuestion().toString(), null, null);
			} catch (ClientException e) {
				resultatTraitement.setStatut(TraitementStatut.KO);
				resultatTraitement.setMessageErreur("Une erreur est survenue lors de la création du jeton document.");
				response.getResultatTraitement().add(resultatTraitement);
				LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_JETON_TEC, e);
				continue;
			}

			response.getResultatTraitement().add(resultatTraitement);

			TransactionHelper.commitOrRollbackTransaction();
			TransactionHelper.startTransaction();

			// Envoi de mail pour signaler les changements d'états
			if (VocabularyConstants.ETAT_QUESTION_SIGNALEE.equals(appNouvelEtatQuestion)
					|| VocabularyConstants.ETAT_QUESTION_RENOUVELEE.equals(appNouvelEtatQuestion)) {
				final ReponsesMailService rms = ReponsesServiceLocator.getReponsesMailService();
				try {
					rms.sendMailAfterStateChangedQuestion(session, session.getDocument(dossier.getDocument().getRef()),
							appNouvelEtatQuestion);
				} catch (ClientException e) {
					LOGGER.error(session, STLogEnumImpl.FAIL_GET_DOCUMENT_TEC, dossier);
				}
			}

			// log dans le journal d'administration
			logWebServiceActionDossier(ReponsesEventConstant.WEBSERVICE_CHANGER_ETAT_QUESTION_EVENT,
					ReponsesEventConstant.WEBSERVICE_CHANGER_ETAT_QUESTION_COMMENT, dossier);
		}

		return response;
	}

	// ////////////////////////////////////////////////////////////
	//
	// Rechercher Dossier
	//
	// ////////////////////////////////////////////////////////////

	/**
	 * Webservice chercherDossier
	 * 
	 * @param request
	 * @return
	 */
	public RechercherDossierResponse rechercherDossier(RechercherDossierRequest request) {
		RechercherDossierResponse response = new RechercherDossierResponse();

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
			LOGGER.error(session, ReponsesLogEnumImpl.FAIL_ACCESS_WSQUESTION_TEC, e);
			return response;
		}

		response.setStatut(TraitementStatut.OK);

		List<String> userGroupList = ((SSPrincipal) session.getPrincipal()).getGroups();
		if (!userGroupList.contains(STWebserviceConstant.RECHERCHER_DOSSIER)) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur("Utilisateur non autorisé à rechercher un dossier");
			return response;
		}

		String idEntiteUtilisateur = null;
		boolean isAn = false;
		boolean isSenat = false;
		if (userGroupList.contains(STWebserviceConstant.PROFIL_AN)) {
			isAn = true;
		}
		if (userGroupList.contains(STWebserviceConstant.PROFIL_SENAT)) {
			isSenat = true;
		}

		if (!isAn && !isSenat) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur("L'utilisateur n'a accès ni aux questions AN, ni aux questions SENAT. Vérifiez le profil.");
			return response;
		}

		// besoin de filtrer sur le ministère si l'utilisateur peut lire dossiers AN et SENAT et non sgg
		if (isAn && isSenat && !principal.isMemberOf(ReponsesBaseFunctionConstant.CORBEILLE_SGG_READER)) {
			idEntiteUtilisateur = getFirstMinistereLoginFromSession(session);
		}

		// Construction d'une requête
		StringBuilder query = QueryHelper.initNxqlQuery(DossierConstants.QUESTION_DOCUMENT_TYPE);

		boolean hasPreviousClause = false;

		// Clause legislature
		List<Integer> legislaturesParams = request.getLegislature();
		if (legislaturesParams != null && !legislaturesParams.isEmpty()) {
			QueryHelper.appendListParams(query, legislaturesParams, DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX,
					DossierConstants.DOSSIER_LEGISLATURE_QUESTION, hasPreviousClause, false);
			hasPreviousClause = true;
		}

		// Clause Type
		List<QuestionType> qTypeList = request.getType();
		if (qTypeList != null && !qTypeList.isEmpty()) {
			QueryHelper.appendListParams(query, qTypeList, DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX,
					DossierConstants.DOSSIER_TYPE_QUESTION, hasPreviousClause, true);
			hasPreviousClause = true;
		}

		// Clause Numéro question
		List<String> numeroQuestionList = request.getNumeroQuestion();
		if (numeroQuestionList != null && !numeroQuestionList.isEmpty()) {
			QueryHelper.appendListParams(query, numeroQuestionList, DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX,
					DossierConstants.DOSSIER_NUMERO_QUESTION, hasPreviousClause, false);
			hasPreviousClause = true;
		}

		// Clause Source
		List<QuestionSource> sources = request.getSource();
		if (sources != null && !sources.isEmpty() || isAn != isSenat) {
			// On vérifie si l'utilisateur a renseigné des sources correspondantes à ses droits
			if (isAn != isSenat && sources != null && !sources.isEmpty()) {
				if (isAn && sources.contains(QuestionSource.AN)) {
					QueryHelper.appendParam(query, QuestionSource.AN, DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX,
							DossierConstants.DOSSIER_ORIGINE_QUESTION, hasPreviousClause, true);
				} else if (isSenat && sources.contains(QuestionSource.SENAT)) {
					QueryHelper.appendParam(query, QuestionSource.SENAT,
							DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX,
							DossierConstants.DOSSIER_ORIGINE_QUESTION, hasPreviousClause, true);
				} else {
					// L'utilisateur veut filtrer sur des ressources auxquelles il n'a pas accès
					response.setMessageErreur("Vous n'avez pas accès à ces questions");
					response.setStatut(TraitementStatut.KO);
					return response;
				}
			} else if (isAn != isSenat) {
				// Pas de sources donc on récupère uniquement les questions du profil
				if (isAn) {
					QueryHelper.appendParam(query, QuestionSource.AN, DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX,
							DossierConstants.DOSSIER_ORIGINE_QUESTION, hasPreviousClause, true);
				} else if (isSenat) {
					QueryHelper.appendParam(query, QuestionSource.SENAT,
							DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX,
							DossierConstants.DOSSIER_ORIGINE_QUESTION, hasPreviousClause, true);
				}
			} else if (sources != null && !sources.isEmpty()) {
				// AN et SENAT, mais filtre donc on ajoute le filtre
				QueryHelper.appendListParams(query, sources, DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX,
						DossierConstants.DOSSIER_ORIGINE_QUESTION, hasPreviousClause, true);
			}
			hasPreviousClause = true;
		}

		// Clause EtatQuestion
		List<String> eqList = getEtatQuestionValuesFromList(request.getEtatQuestion());
		if (isAn == isSenat && eqList != null && !eqList.isEmpty()) {
			QueryHelper.appendListParams(query, eqList, DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX,
					DossierConstants.DOSSIER_ETAT_QUESTION_ITEM_PROPERTY, hasPreviousClause, true);
			hasPreviousClause = true;
		} else if (isAn != isSenat) {
			// l'utilisateur est un parlementaire, on ne recherche que les questions à l'état répondu, cloturé ou retiré
			QueryHelper.appendListParams(query, RECHERCHER_DOSSIER_EQ_PARLEMENTAIRE,
					DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX,
					DossierConstants.DOSSIER_ETAT_QUESTION_ITEM_PROPERTY, hasPreviousClause, true);
			hasPreviousClause = true;
		}

		// Clause Date
		XMLGregorianCalendar dateDebut = request.getDateDebut();
		if (dateDebut != null) {
			String nxqlDateDebut = "DATE '" + getNXQLDateFromXMLGregorianCalender(dateDebut) + "'";
			QueryHelper.appendParam(query, nxqlDateDebut, DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX,
					DossierConstants.DOSSIER_DATE_PUBLICATION_JO_QUESTION, hasPreviousClause, false);

			XMLGregorianCalendar dateFin = request.getDateFin();
			if (dateFin != null) {
				String nxqlDateFin = "AND DATE '" + getNXQLDateFromXMLGregorianCalender(dateFin) + "'";
				QueryHelper.appendParam(query, nxqlDateFin, DossierConstants.QUESTION_DOCUMENT_SCHEMA_PREFIX,
						DossierConstants.DOSSIER_DATE_PUBLICATION_JO_QUESTION, hasPreviousClause, false);
			}
		}

		DocumentModelList questionModelList = null;
		try {
			questionModelList = new UnrestrictedQueryRunner(session, query.toString()).findAll();
		} catch (ClientException e) {
			response.setMessageErreur("La recherche a échoué");
			response.setStatut(TraitementStatut.KO);
			LOGGER.error(session, STLogEnumImpl.FAIL_GET_RECHERCHE_TEC, e);
			return response;
		}

		for (DocumentModel questionModel : questionModelList) {
			QuestionReponse questionReponse = getQuestionReponseFromQuestion(
					questionModel.getAdapter(fr.dila.reponses.api.cases.Question.class), idEntiteUtilisateur,
					isAn != isSenat);
			if (questionReponse != null) {
				response.getDossier().add(questionReponse);
			}
		}

		// log dans le journal d'administration
		logWebServiceAction(ReponsesEventConstant.WEBSERVICE_CHERCHER_DOSSIER_EVENT,
				ReponsesEventConstant.WEBSERVICE_CHERCHER_DOSSIER_COMMENT);
		return response;
	}

	// ////////////////////////////////////////////////////////////
	//
	// TOOLS
	//
	// ////////////////////////////////////////////////////////////
	private QuestionReponse getQuestionReponseFromQuestion(fr.dila.reponses.api.cases.Question appQuestion,
			String filtreMinistere, boolean isParlementaire) {
		QuestionReponse questionReponse = new QuestionReponse();

		Question question = new Question();

		question.setTexte("");

		Dossier dossier;
		try {
			dossier = appQuestion.getDossier(session);
		} catch (ClientException e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_GET_DOSSIER_TEC, e);
			return null;
		}

		// AUTEUR
		// ///////////////////
		Auteur auteur = getAuteurFromQuestion(appQuestion);
		question.setAuteur(auteur);

		// Date publication JO
		// ///////////////////
		Calendar datePublication = appQuestion.getDatePublicationJO();
		if (datePublication != null) {
			XMLGregorianCalendar xmlDate = calendarToXMLGregorianCalendar(datePublication);
			if (xmlDate != null) {
				question.setDatePublicationJo(xmlDate);
			}
		}

		// IdQuestion
		// ///////////////////
		QuestionId questionId = WsUtils.getQuestionIdFromQuestion(appQuestion);
		question.setIdQuestion(questionId);

		if (QuestionSource.AN.equals(questionId.getSource())) {
			question.setIndexationAn(getIndexationAnFromQuestion(appQuestion));
		} else if (QuestionSource.SENAT.equals(questionId.getSource())) {
			question.setIndexationSenat(getIndexationSenatFromQuestion(appQuestion));
			// Titre Senat
			// //////////////////
			question.setTitreSenat(appQuestion.getSenatQuestionTitre());
		}

		// Ministere de depot;
		// ///////////////////
		Ministre ministreDepot = getMinistereDepotFromQuestion(appQuestion);

		question.setMinistreDepot(ministreDepot);

		// Ministere attributaire
		// ///////////////////////
		Ministre ministereAttributaire = new Ministre();
		String idMinistereAttributaire = dossier.getIdMinistereAttributaireCourant();
		if (idMinistereAttributaire != null) {
			ministereAttributaire.setId(Integer.parseInt(idMinistereAttributaire));
		}
		EntiteNode entiteNode = null;
		try {
			entiteNode = STServiceLocator.getSTMinisteresService().getEntiteNode(idMinistereAttributaire);
		} catch (ClientException e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_GET_ENTITE_NODE_TEC, e);
		}

		if (entiteNode != null) {
			ministereAttributaire.setIntituleMinistere(entiteNode.getLabel());
			ministereAttributaire.setTitreJo(entiteNode.getEdition());
		}

		question.setMinistreAttributaire(ministereAttributaire);

		// Page JO
		// ///////////////////
		try {
			if (appQuestion.getPageJO() != null) {
				question.setPageJo(Integer.parseInt(appQuestion.getPageJO()));
			}
		} catch (NumberFormatException e) {
			LOGGER.warn(session, ReponsesLogEnumImpl.FAIL_UPDATE_QUESTION_TEC, appQuestion.getDocument(), e);
		}

		// Rappel
		// ///////////////////
		// resultQuestion.setRappel(value);

		// TexteQuestion
		// ///////////////////
		if (appQuestion.getTexteQuestion() != null && appQuestion.getTexteQuestion().length() > 0) {
			question.setTexte(stripCDATA(appQuestion.getTexteQuestion()));
		}

		// TexteJoint
		// ///////////////////

		question.setTexteJoint(stripCDATA(appQuestion.getTexteJoint()));

		questionReponse.getQuestion().add(question);

		// //////////////////
		// REPONSE
		// ///////////////////////////
		ReponsePubliee reponse = new ReponsePubliee();
		Reponse appReponse = dossier.getReponse(session);

		if (filtreMinistere != null
				&& (dossier.getIdMinistereAttributaireCourant() != filtreMinistere || isParlementaire)
				&& appReponse.getDateJOreponse() == null) {
			// pour les réponses non publiées d'un autre ministere on ne renvoit pas la question.
			return questionReponse;
		}

		reponse.setMinistreReponse(ministereAttributaire);
		reponse.setTexteReponse(appReponse.getTexteReponse());

		if (appReponse.getPageJOreponse() != null && appReponse.getDateJOreponse() != null) {
			reponse.setPageJo(appReponse.getPageJOreponse().intValue());
			reponse.setDateJo(calendarToXMLGregorianCalendar(appReponse.getDateJOreponse()));
		}
		questionReponse.setReponse(reponse);

		return questionReponse;
	}

	private ErratumQuestion getErratumQuestionFromQuestion(fr.dila.reponses.api.cases.Question appQuestion,
			List<ErratumQuestion> erratumQuestionAdded) {
		List<QErratum> erList = appQuestion.getErrata();
		if (erList.size() != 0) {
			QuestionId qid = WsUtils.getQuestionIdFromQuestion(appQuestion);

			// Si une question a plusieurs errata
			// position = 1 <=> on traite le dernier errata.
			// si dans les résultats on a deja un errata pour cette question on traite l'avant dernier
			int position = 1;
			for (ErratumQuestion erratumQuestion : erratumQuestionAdded) {
				QuestionId eqId = erratumQuestion.getIdQuestion();
				if (idQuestionsAreEquals(qid, eqId)) {
					position++;
				}
			}

			int itemPosition = erList.size() - position < 0 ? 0 : erList.size() - position;
			QErratum questionErratum = appQuestion.getErrata().get(itemPosition);
			ErratumQuestion erratumQuestion = new ErratumQuestion();

			erratumQuestion.setIdQuestion(qid);
			if (questionErratum.getPageJo() != null) {
				erratumQuestion.setPageJoErratum(questionErratum.getPageJo());
			}
			if (questionErratum.getTexteErratum() != null && questionErratum.getTexteErratum().length() > 0) {
				erratumQuestion.setTexteErratum(questionErratum.getTexteErratum());
			}
			if (questionErratum.getTexteConsolide() != null && questionErratum.getTexteConsolide().length() > 0) {
				erratumQuestion.setTexteConsolide(questionErratum.getTexteConsolide());
			} else {
				erratumQuestion.setTexteConsolide("");
			}

			if (questionErratum.getDatePublication() != null) {
				erratumQuestion.setDatePublicationErratum(calendarToXMLGregorianCalendar(questionErratum
						.getDatePublication()));
			}

			if (erratumQuestion.getTexteConsolide().contains("Texte consolidé")) {
				erratumQuestion.setType(ErratumType.ERRATUM);
			} else {
				erratumQuestion.setType(ErratumType.CORRECTION);
			}

			return erratumQuestion;
		}

		return null;
	}

	private Questions getXsdQuestionsFromQuestion(Dossier dossier, fr.dila.reponses.api.cases.Question appQuestion) {
		Questions resultQuestion = new Questions();

		// AUTEUR
		// ///////////////////
		Auteur auteur = getAuteurFromQuestion(appQuestion);
		resultQuestion.setAuteur(auteur);

		// Date publication JO
		// ///////////////////
		Calendar datePublication = appQuestion.getDatePublicationJO();
		if (datePublication != null) {
			XMLGregorianCalendar xmlDate = calendarToXMLGregorianCalendar(datePublication);
			if (xmlDate != null) {
				resultQuestion.setDatePublicationJo(xmlDate);
			}
		}

		// IdQuestion
		// ///////////////////
		QuestionId questionId = WsUtils.getQuestionIdFromQuestion(appQuestion);
		resultQuestion.setIdQuestion(questionId);

		if (QuestionSource.AN.equals(questionId.getSource())) {
			resultQuestion.setIndexationAn(getIndexationAnFromQuestion(appQuestion));
		} else if (QuestionSource.SENAT.equals(questionId.getSource())) {
			resultQuestion.setIndexationSenat(getIndexationSenatFromQuestion(appQuestion));
			// Titre Senat
			// //////////////////
			resultQuestion.setTitreSenat(appQuestion.getSenatQuestionTitre());
		}

		// Ministere de depot;
		// ///////////////////
		Ministre ministreDepot = getMinistereDepotFromQuestion(appQuestion);
		resultQuestion.setMinistreDepot(ministreDepot);

		// Page JO
		// ///////////////////
		try {
			Integer pageJO = Integer.parseInt(appQuestion.getPageJO());
			resultQuestion.setPageJo(pageJO);
		} catch (NumberFormatException e) {
			LOGGER.error(session, ReponsesLogEnumImpl.FAIL_UPDATE_QUESTION_TEC, appQuestion.getDocument(), e);
		}

		// TexteQuestion
		// ///////////////////
		resultQuestion.setTexte(stripCDATA(appQuestion.getTexteQuestion()));

		// TexteJoint
		// ///////////////////
		String texteJoint = appQuestion.getTexteJoint();
		if (texteJoint != null && texteJoint.length() > 0) {
			resultQuestion.setTexteJoint(stripCDATA(appQuestion.getTexteJoint()));
		}

		// Ministere Attributaire
		// ///////////////////////
		Ministre ministreAttributaire = getMinistereAttributaireFromDossier(dossier);
		resultQuestion.setMinistreAttributaire(ministreAttributaire);

		return resultQuestion;
	}

	private String getEtatQuestionFromXsd(EtatQuestion etatQuestionLu) {
		String appEtatQuestion = null;
		if (etatQuestionLu == null) {
			return null;
		}
		if (etatQuestionLu.equals(EtatQuestion.CADUQUE)) {
			appEtatQuestion = VocabularyConstants.ETAT_QUESTION_CADUQUE;
		} else if (etatQuestionLu.equals(EtatQuestion.CLOTURE_AUTRE)) {
			appEtatQuestion = VocabularyConstants.ETAT_QUESTION_CLOTURE_AUTRE;
		} else if (etatQuestionLu.equals(EtatQuestion.EN_COURS)) {
			appEtatQuestion = VocabularyConstants.ETAT_QUESTION_EN_COURS;
		} else if (etatQuestionLu.equals(EtatQuestion.RENOUVELLE)) {
			appEtatQuestion = VocabularyConstants.ETAT_QUESTION_RENOUVELEE;
		} else if (etatQuestionLu.equals(EtatQuestion.REPONDU)) {
			appEtatQuestion = VocabularyConstants.ETAT_QUESTION_REPONDU;
		} else if (etatQuestionLu.equals(EtatQuestion.RETIRE)) {
			appEtatQuestion = VocabularyConstants.ETAT_QUESTION_RETIREE;
		} else if (etatQuestionLu.equals(EtatQuestion.SIGNALE)) {
			appEtatQuestion = VocabularyConstants.ETAT_QUESTION_SIGNALEE;
		}

		return appEtatQuestion;
	}

	private EtatQuestion getXsdVersionFromEtatQuestion(String etatQuestionLu) {
		EtatQuestion xsdEtatQuestion = null;
		if (etatQuestionLu.equals(VocabularyConstants.ETAT_QUESTION_CADUQUE)) {
			xsdEtatQuestion = EtatQuestion.CADUQUE;

		} else if (etatQuestionLu.equals(VocabularyConstants.ETAT_QUESTION_CLOTURE_AUTRE)) {
			xsdEtatQuestion = EtatQuestion.CLOTURE_AUTRE;

		} else if (etatQuestionLu.equals(VocabularyConstants.ETAT_QUESTION_EN_COURS)) {
			xsdEtatQuestion = EtatQuestion.EN_COURS;

		} else if (etatQuestionLu.equals(VocabularyConstants.ETAT_QUESTION_RENOUVELEE)) {
			xsdEtatQuestion = EtatQuestion.RENOUVELLE;

		} else if (etatQuestionLu.equals(VocabularyConstants.ETAT_QUESTION_REPONDU)) {
			xsdEtatQuestion = EtatQuestion.REPONDU;
		} else if (etatQuestionLu.equals(VocabularyConstants.ETAT_QUESTION_RETIREE)) {
			xsdEtatQuestion = EtatQuestion.RETIRE;

		} else if (etatQuestionLu.equals(VocabularyConstants.ETAT_QUESTION_SIGNALEE)) {
			xsdEtatQuestion = EtatQuestion.SIGNALE;
		}

		return xsdEtatQuestion;
	}

	private String getNXQLDateFromXMLGregorianCalender(XMLGregorianCalendar xdate) {
		return xdate.getYear() + "-" + xdate.getMonth() + "-" + xdate.getDay();
	}

	private List<String> getEtatQuestionValuesFromList(List<EtatQuestion> eqList) {
		List<String> result = new ArrayList<String>();
		if (eqList != null && !eqList.isEmpty()) {
			for (EtatQuestion eq : eqList) {
				result.add(getEtatQuestionFromXsd(eq));
			}
		}
		return result;
	}

}
