package fr.dila.reponses.rest.management;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.transaction.TransactionHelper;

import fr.dila.reponses.api.Exception.ReponsesException;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.core.notification.WsUtils;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.administration.EtatApplication;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.constant.STWebserviceConstant;
import fr.dila.st.api.jeton.JetonServiceDto;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.EtatApplicationService;
import fr.dila.st.api.service.JetonService;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.xsd.reponses.ChercherRetourPublicationRequest;
import fr.sword.xsd.reponses.ChercherRetourPublicationResponse;
import fr.sword.xsd.reponses.ControleErratumQuestion;
import fr.sword.xsd.reponses.ControleErratumReponse;
import fr.sword.xsd.reponses.ControlePublicationRequest;
import fr.sword.xsd.reponses.ControlePublicationResponse;
import fr.sword.xsd.reponses.ControleQuestion;
import fr.sword.xsd.reponses.ControleQuestionReponses;
import fr.sword.xsd.reponses.MinistrePublication;
import fr.sword.xsd.reponses.QuestionId;
import fr.sword.xsd.reponses.ReferencePublication;
import fr.sword.xsd.reponses.ResultatControlePublication;
import fr.sword.xsd.reponses.ResultatControlePublicationQR;
import fr.sword.xsd.reponses.RetourPublication;
import fr.sword.xsd.reponses.TraitementStatut;

/**
 * Permet de gérer les webservices controlePublication et chercherRetourPublication
 * 
 * @author sly
 * @author bgd
 */
public class ControleDelegate extends AbstractDelegate {
	/**
	 * Logger.
	 */
	private static final STLogger	LOGGER					= STLogFactory.getLog(ControleDelegate.class);

	private static final String		IMPOSSIBLE_RECUP_QUEST	= "Impossible de récupérer la question ";

	public ControleDelegate(final CoreSession documentManager) {
		super(documentManager);
	}

	/**
	 * Webservice controlePublication Lors de l'appel de controle publication, on crée des jetons de retour publication
	 * à récupérer avec le webservice chercherRetourPublication.
	 * 
	 * @param request
	 * @return response
	 */
	public ControlePublicationResponse controlePublication(final ControlePublicationRequest request) {
		// Chargement des services
		final EtatApplicationService etatApplicationService = STServiceLocator.getEtatApplicationService();

		final ControlePublicationResponse response = new ControlePublicationResponse();

		final SSPrincipal principal = (SSPrincipal) session.getPrincipal();
		try {
			final EtatApplication etatApplication = etatApplicationService.getEtatApplicationDocument(session);
			if (etatApplication.getRestrictionAcces()
					&& !principal.isMemberOf(ReponsesBaseFunctionConstant.ADMIN_ACCESS_UNRESTRICTED)) {
				final ResultatControlePublication rcq = new ResultatControlePublication();
				rcq.setStatut(TraitementStatut.KO);
				rcq.setMessageErreur(ACCES_APPLI_RESTREINT + etatApplication.getDescriptionRestriction());
				response.getResultatControleQuestion().add(rcq);
				return response;
			}
		} catch (final ClientException e) {
			final ResultatControlePublication rcq = new ResultatControlePublication();
			rcq.setStatut(TraitementStatut.KO);
			rcq.setMessageErreur(INFO_ACCES_APPLI_KO);
			response.getResultatControleQuestion().add(rcq);
			LOGGER.error(session, ReponsesLogEnumImpl.FAIL_ACCESS_WSCONTROLE_TEC, e);
			return response;
		}

		final String webservice = STWebserviceConstant.CONTROLE_PUBLICATION;

		// /!\ hasRightAndOrigineUtilisateur renseigne questionId.source par effet de bord
		final QuestionId questionId = new QuestionId();
		if (!hasRightAndOrigineUtilisateur(session, webservice, questionId)) {
			final ResultatControlePublication rcq = new ResultatControlePublication();
			rcq.setStatut(TraitementStatut.KO);
			rcq.setMessageErreur(USER_NON_AUTORISE);
			response.getResultatControleQuestion().add(rcq);
			return response;
		}
		final String source = questionId.getSource().toString();

		// Controle question
		for (final ControleQuestion controleQuestion : request.getQuestion()) {
			if (!isTransactionAlive()) {
				TransactionHelper.startTransaction();
			}
			final ResultatControlePublication resultatControleQuestion = new ResultatControlePublication();

			resultatControleQuestion.setIdQuestion(controleQuestion.getIdQuestion());
			resultatControleQuestion.setStatut(TraitementStatut.OK);
			try {
				publierQuestion(source, controleQuestion.getIdQuestion(), controleQuestion.getReferencePublication());
			} catch (final ClientException e) {
				resultatControleQuestion.setStatut(TraitementStatut.KO);
				resultatControleQuestion.setMessageErreur(e.getMessage());
				LOGGER.error(session, ReponsesLogEnumImpl.FAIL_PUBLISH_QUESTION_TEC, e);
			}
			TransactionHelper.commitOrRollbackTransaction();
			response.getResultatControleQuestion().add(resultatControleQuestion);
		}

		// Controle question réponses
		for (final ControleQuestionReponses controleQuestionReponse : request.getQuestionReponse()) {
			if (!isTransactionAlive()) {
				TransactionHelper.startTransaction();
			}
			final ResultatControlePublicationQR resultatControlePublicationQR = new ResultatControlePublicationQR();
			resultatControlePublicationQR.setStatut(TraitementStatut.OK);

			try {
				for (final ControleQuestion controleQuestion : controleQuestionReponse.getQuestion()) {
					publierReponse(source, controleQuestion.getIdQuestion(), controleQuestionReponse.getReponses()
							.getReferencePublication());
					resultatControlePublicationQR.getIdQuestion().add(controleQuestion.getIdQuestion());
					checkMinistereReponse(controleQuestion.getIdQuestion(), controleQuestionReponse.getReponses()
							.getMinistreJo());
				}
			} catch (final ClientException e) {
				resultatControlePublicationQR.setStatut(TraitementStatut.KO);
				resultatControlePublicationQR.setMessageErreur(e.getMessage());
				LOGGER.error(session, ReponsesLogEnumImpl.FAIL_VALIDATE_QUESTION_TEC, e);
			}
			TransactionHelper.commitOrRollbackTransaction();
			response.getResultatControleQuestionReponse().add(resultatControlePublicationQR);
		}

		// Controle erratum question
		for (final ControleErratumQuestion controleErratumQuestion : request.getErratumQuestion()) {
			if (!isTransactionAlive()) {
				TransactionHelper.startTransaction();
			}
			final ResultatControlePublication resultatControleQuestion = new ResultatControlePublication();
			resultatControleQuestion.setIdQuestion(controleErratumQuestion.getIdQuestion());
			resultatControleQuestion.setStatut(TraitementStatut.OK);

			// try {
			// // publierQuestion(source, controleErratumQuestion.getIdQuestion(),
			// controleErratumQuestion.getReferencePublicationErratum());
			// } catch(ClientException e) {
			// resultatControleQuestion.setStatut(TraitementStatut.KO);
			// resultatControleQuestion.setMessageErreur(e.getMessage());
			// }
			TransactionHelper.commitOrRollbackTransaction();
			response.getResultatControleErratumQuestion().add(resultatControleQuestion);
		}

		// Controle erratum réponse
		for (final ControleErratumReponse controleErratumReponse : request.getErratumReponse()) {
			if (!isTransactionAlive()) {
				TransactionHelper.startTransaction();
			}
			final ResultatControlePublication resultatControleQuestion = new ResultatControlePublication();
			resultatControleQuestion.setIdQuestion(controleErratumReponse.getIdQuestion());
			resultatControleQuestion.setStatut(TraitementStatut.OK);

			// try {
			// publierQuestion(source, controleErratumReponse.getIdQuestion(),
			// controleErratumReponse.getReferencePublicationErratum());
			// } catch (ClientException e) {
			// resultatControleQuestion.setStatut(TraitementStatut.KO);
			// resultatControleQuestion.setMessageErreur(e.getMessage());
			// }
			TransactionHelper.commitOrRollbackTransaction();
			response.getResultatControleErratumReponse().add(resultatControleQuestion);
		}

		// log dans le journal d'administration
		logWebServiceAction(ReponsesEventConstant.WEBSERVICE_CONTROLE_PUBLICATION_EVENT,
				ReponsesEventConstant.WEBSERVICE_CONTROLE_PUBLICATION_COMMENT);

		return response;
	}

	private void publierQuestion(final String source, final QuestionId questionId,
			final ReferencePublication referencePublication) throws ClientException {
		final Dossier dossier = getDossierFromQuestionId(questionId);
		final Question question = dossier.getQuestion(session);

		// Vérifier qu'une étape Pour transmission aux assemblées est présente et validée
		if (!dossier.hasFeuilleRoute()) {
			throw new ReponsesException("Le dossier ne comporte pas de feuille de route");
		}

		// Mettre à jour les informations de publication si elles sont vides ou differentes et envoie de mail admin
		if (question.getDatePublicationJO() == null
				|| !question.getDatePublicationJO().equals(
						referencePublication.getDatePublication().toGregorianCalendar())) {
			question.setDatePublicationJO(referencePublication.getDatePublication().toGregorianCalendar());
		}
		// Remplacement du no page systematique
		question.setPageJO(String.valueOf(referencePublication.getPageJo()));

		// Créer un jeton pour WsChercherRetourPublication
		// JetonService jetonService = ReponsesServiceLocator.getJetonService();
		// jetonService.createNewJetonDocWithLastJetonMaitre(session, dossier.getDocument().getId(), source,
		// STWebserviceConstant.CHERCHER_RETOUR_PUBLICATION);

		// Sauvegarder le tout
		session.saveDocument(question.getDocument());
		session.save();
	}

	private void publierReponse(final String source, final QuestionId questionId,
			final ReferencePublication referencePublication) throws ClientException {
		// Chargement des services
		final JetonService jetonService = ReponsesServiceLocator.getJetonService();

		final Dossier dossier = getDossierFromQuestionId(questionId);
		final Question question = dossier.getQuestion(session);
		final Reponse reponse = dossier.getReponse(session);

		// Vérifier qu'une date de transmission aux assemblées est présente
		if (question.getDateTransmissionAssemblees() == null) {
			throw new ReponsesException("Pas de date de transmission aux assemblées sur ce dossier");
		}

		// Mettre à jour les informations de publication si elles sont vides
		if (reponse.getDateJOreponse() == null) {
			reponse.setDateJOreponse(referencePublication.getDatePublication().toGregorianCalendar());
		}
		if (reponse.getNumeroJOreponse() == null) {
			reponse.setNumeroJOreponse(Long.valueOf(referencePublication.getNoPublication()));
		}
		if (reponse.getPageJOreponse() == null) {
			reponse.setPageJOreponse(Long.valueOf(referencePublication.getPageJo()));
		}

		// Créer un jeton pour WsChercherRetourPublication
		jetonService.addDocumentInBasket(session, STWebserviceConstant.CHERCHER_RETOUR_PUBLICATION, source,
				dossier.getDocument(), dossier.getNumeroQuestion().toString(), null, null);

		// Sauvegarder le tout
		session.saveDocument(dossier.getDocument());
		session.saveDocument(question.getDocument());
		session.saveDocument(reponse.getDocument());
		session.save();
	}

	/**
	 * Vérification et envoi de mail lorsque le ministère de la réponse ne correspondent pas
	 * 
	 * @param questionId
	 * @param ministre
	 *            JO publication de la réponse
	 * 
	 * @throws ClientException
	 */
	private void checkMinistereReponse(final QuestionId questionId, final MinistrePublication ministre)
			throws ClientException {
		final Dossier dossier = getDossierFromQuestionId(questionId);

		if (ministre == null || ministre.getId() == null
				|| !ministre.getId().toString().equals(dossier.getIdMinistereAttributaireCourant())) {
			sendMailToAdminTechnique(questionId);
		}
	}

	/**
	 * Webservice chercherRetourPublication
	 * 
	 * @param request
	 * @return response
	 */
	public ChercherRetourPublicationResponse chercherRetourPublication(final ChercherRetourPublicationRequest request) {
		// Chargement des services
		final EtatApplicationService etatApplicationService = STServiceLocator.getEtatApplicationService();

		final ChercherRetourPublicationResponse response = new ChercherRetourPublicationResponse();

		// Verification de l'accès à l'application
		final SSPrincipal principal = (SSPrincipal) session.getPrincipal();
		try {
			final EtatApplication etatApplication = etatApplicationService.getEtatApplicationDocument(session);
			if (etatApplication.getRestrictionAcces()
					&& !principal.isMemberOf(ReponsesBaseFunctionConstant.ADMIN_ACCESS_UNRESTRICTED)) {
				response.setStatut(TraitementStatut.KO);
				response.setMessageErreur("L'accès a l'application est restreint : "
						+ etatApplication.getDescriptionRestriction());
				return response;
			}
		} catch (final ClientException e) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(RECUPERATION_INFO_KO);
			LOGGER.error(session, ReponsesLogEnumImpl.FAIL_ACCESS_WSCONTROLE_TEC, e);
			return response;
		}

		response.setStatut(TraitementStatut.OK);
		response.setDernierRenvoi(true);
		response.setMessageErreur("");
		final String webservice = STWebserviceConstant.CHERCHER_RETOUR_PUBLICATION;
		final String jetonRecu = request.getJetonRetourPublication();

		final String idMinistereUtilisateur = getFirstMinistereLoginFromSession(session);

		if (!hasRightAndOrigineUtilisateur(session, webservice, null)) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(USER_NON_AUTORISE);
			return response;
		}

		JetonServiceDto dto = null;
		try {
			dto = metaChercherDocument(idMinistereUtilisateur, jetonRecu, webservice, request.getIdQuestion());
		} catch (final NumberFormatException e) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(JETON_NON_RECONNU);
			LOGGER.error(session, STLogEnumImpl.FAIL_GET_JETON_TEC, e);
			return response;
		} catch (final IllegalArgumentException e) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(e.getMessage());
			LOGGER.error(session, STLogEnumImpl.FAIL_GET_JETON_SERVICE_TEC, e);
			return response;
		} catch (final ClientException e) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(e.getMessage());
			LOGGER.error(session, STLogEnumImpl.FAIL_GET_JETON_SERVICE_TEC, e);
			return response;
		}

		if (dto == null) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(JETON_NON_ADEQUAT);
			return response;
		}

		String jetonSortant = null;
		if (dto.getNextJetonNumber() != null) {
			jetonSortant = dto.getNextJetonNumber().toString();
			response.setJetonRetourPublication(jetonSortant);
		}

		if (dto.isLastSending() != null) {
			response.setDernierRenvoi(dto.isLastSending());
		}

		final List<DocumentModel> docList = dto.getDocumentList();
		if (docList == null) {
			return response;
		}

		final List<RetourPublication> resultQuestions = new ArrayList<RetourPublication>();

		if (!docList.isEmpty()) {
			for (final DocumentModel documentModel : docList) {
				final fr.dila.reponses.api.cases.Question appQuestion = documentModel
						.getAdapter(fr.dila.reponses.api.cases.Question.class);
				final QuestionId qid = WsUtils.getQuestionIdFromQuestion(appQuestion);
				Dossier dossier = null;
				try {
					dossier = getDossierFromQuestionId(qid);
				} catch (final ClientException e) {
					response.setStatut(TraitementStatut.KO);
					response.setMessageErreur(response.getMessageErreur() + IMPOSSIBLE_RECUP_QUEST
							+ qid.getNumeroQuestion() + " : Dossier introuvable.");
					LOGGER.error(session, STLogEnumImpl.FAIL_GET_DOSSIER_TEC, "id : " + qid.getNumeroQuestion(), e);
					continue;
				}

				if (!dossier.getIdMinistereAttributaireCourant().equals(idMinistereUtilisateur)) {
					response.setStatut(TraitementStatut.KO);
					response.setMessageErreur(response.getMessageErreur() + IMPOSSIBLE_RECUP_QUEST
							+ qid.getNumeroQuestion()
							+ " : l'utilisateur n'appartient pas au ministere courant de la question. \n");
					continue;
				}
				// Convertir en retour Publication
				resultQuestions.add(getRetourPublicationFromQuestion(appQuestion));
			}

		}
		response.getRetourPublication().addAll(resultQuestions);

		// log dans le journal d'administration
		logWebServiceAction(ReponsesEventConstant.WEBSERVICE_CHERCHER_RETOUR_PUBLICATION_EVENT,
				ReponsesEventConstant.WEBSERVICE_CHERCHER_RETOUR_PUBLICATION_COMMENT);

		return response;
	}

	private RetourPublication getRetourPublicationFromQuestion(final Question question) {
		final RetourPublication retPubli = new RetourPublication();
		Dossier dossier = null;
		try {
			dossier = session.getDocument(question.getDossierRef()).getAdapter(Dossier.class);
		} catch (final ClientException e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_GET_DOCUMENT_TEC);
			return null;
		}
		if (dossier == null) {
			return null;
		}
		final Reponse reponse = dossier.getReponse(session);

		final Calendar date = reponse.getDateJOreponse();
		final Long pageJo = reponse.getPageJOreponse();

		if (date == null || pageJo == null || pageJo.equals(0L)) {
			return null;
		}

		retPubli.setDatePublicationJo(calendarToXMLGregorianCalendar(date));
		retPubli.setIdQuestion(WsUtils.getQuestionIdFromQuestion(question));
		retPubli.setPageJo(pageJo.intValue());

		return retPubli;
	}

	/**
	 * envoie du mail contrôle publication
	 */
	private void sendMailToAdminTechnique(final QuestionId questionId) {
		// Chargement des services
		final STParametreService parametreService = STServiceLocator.getSTParametreService();
		final STMailService mailService = STServiceLocator.getSTMailService();

		try {
			// Détermine l'objet et le corps du mail
			final String mailObjet = parametreService.getParametreValue(session,
					STParametreConstant.OBJET_ALERTE_CONTROLE_PUBLICATION);
			final String mailTexte = parametreService.getParametreValue(session,
					STParametreConstant.TEXTE_ALERTE_CONTROLE_PUBLICATION);

			// Envoi de l'email
			final String adminMail = parametreService.getParametreValue(session,
					STParametreConstant.MAIL_ADMIN_TECHNIQUE);
			final Map<String, Object> params = new HashMap<String, Object>();
			params.put("numero_question", questionId.getSource().toString() + questionId.getNumeroQuestion());

			mailService.sendTemplateMail(adminMail, mailObjet, mailTexte, params);
		} catch (final Exception e) {
			LOGGER.error(
					session,
					STLogEnumImpl.FAIL_SEND_MAIL_TEC,
					"Impossible d'envoyer le mail d'alerte de différence de données (ministère) aux administrateurs fonctionnels",
					e);
		}
	}
}
