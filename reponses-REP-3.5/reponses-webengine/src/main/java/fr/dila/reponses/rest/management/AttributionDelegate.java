package fr.dila.reponses.rest.management;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.directory.Session;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.domain.JetonDoc;
import fr.dila.reponses.api.historique.HistoriqueAttribution;
import fr.dila.reponses.core.notification.WsUtils;
import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.api.constant.STWebserviceConstant;
import fr.dila.st.api.jeton.JetonServiceDto;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.xsd.reponses.Attributaire;
import fr.sword.xsd.reponses.Attribution;
import fr.sword.xsd.reponses.AttributionDate;
import fr.sword.xsd.reponses.AttributionType;
import fr.sword.xsd.reponses.ChercherAttributionsDateRequest;
import fr.sword.xsd.reponses.ChercherAttributionsDateResponse;
import fr.sword.xsd.reponses.ChercherAttributionsRequest;
import fr.sword.xsd.reponses.ChercherAttributionsResponse;
import fr.sword.xsd.reponses.ChercherLegislaturesResponse;
import fr.sword.xsd.reponses.ChercherMembresGouvernementRequest;
import fr.sword.xsd.reponses.ChercherMembresGouvernementResponse;
import fr.sword.xsd.reponses.Civilite;
import fr.sword.xsd.reponses.EnFonction;
import fr.sword.xsd.reponses.Legislature;
import fr.sword.xsd.reponses.MembreGouvernement;
import fr.sword.xsd.reponses.Ministre;
import fr.sword.xsd.reponses.QuestionId;
import fr.sword.xsd.reponses.QuestionSource;
import fr.sword.xsd.reponses.TraitementStatut;

/**
 * Permet de gerer toutes les operations sur les questions
 * 
 * @author sly
 * 
 */
public class AttributionDelegate extends AbstractDelegate {

	/**
	 * Logger.
	 */
	private static final Log	LOGGER	= LogFactory.getLog(AttributionDelegate.class);

	public AttributionDelegate(CoreSession documentManager) {
		super(documentManager);
	}

	private ChercherAttributionsDateResponse buildAttributionDateError(TraitementStatut statut, String errorMessage) {
		ChercherAttributionsDateResponse error = new ChercherAttributionsDateResponse();
		error.setStatut(statut);
		error.setMessageErreur(errorMessage);

		return error;
	}

	/**
	 * Webservice chercherAttributions
	 * 
	 * @param request
	 * @return
	 */
	public ChercherAttributionsResponse chercherAttributions(ChercherAttributionsRequest request) {
		// Chargement des services
		ChercherAttributionsResponse response = new ChercherAttributionsResponse();

		Map<String, Object> resultsResponseMap = checkWebserviceAccess();
		if (!resultsResponseMap.isEmpty()) {
			response.setStatut((TraitementStatut) resultsResponseMap.get(MAP_TRAITEMENT_STATUT));
			response.setMessageErreur((String) resultsResponseMap.get(MAP_MESSAGE_ERREUR));
			return response;
		}

		response.setStatut(TraitementStatut.OK);
		response.setJetonAttributions("");
		response.setDernierRenvoi(true);

		String webservice = STWebserviceConstant.CHERCHER_ATTRIBUTIONS;
		String jetonRecu = request.getJeton();

		QuestionSource origineUtilisateur = null;
		Boolean isMinistere = false;
		String idProfilUtilisateur = null;
		QuestionId dtoPourOrigineUtilisateur = new QuestionId();
		if (!hasRightAndOrigineUtilisateur(session, webservice, dtoPourOrigineUtilisateur)) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(USER_NON_AUTORISE);
			return response;
		} else if (dtoPourOrigineUtilisateur.getSource() == null) {
			// Est un ministere
			isMinistere = true;
			idProfilUtilisateur = getFirstMinistereLoginFromSession(session);
		} else {
			// Est un parlementaire
			origineUtilisateur = dtoPourOrigineUtilisateur.getSource();
			if (origineUtilisateur == QuestionSource.AN) {
				idProfilUtilisateur = VocabularyConstants.QUESTION_ORIGINE_AN;
			} else if (origineUtilisateur == QuestionSource.SENAT) {
				idProfilUtilisateur = VocabularyConstants.QUESTION_ORIGINE_SENAT;
			}
		}

		JetonServiceDto dto = getJetonDTOForWebservice(idProfilUtilisateur, jetonRecu, webservice,
				request.getIdQuestions(), resultsResponseMap);
		if (dto == null) {
			response.setStatut((TraitementStatut) resultsResponseMap.get(MAP_TRAITEMENT_STATUT));
			response.setMessageErreur((String) resultsResponseMap.get(MAP_MESSAGE_ERREUR));
			return response;
		}

		String jetonSortant = null;
		if (dto.getNextJetonNumber() != null) {
			jetonSortant = dto.getNextJetonNumber().toString();
			response.setJetonAttributions(jetonSortant);
		}
		if (dto.isLastSending() != null) {
			response.setDernierRenvoi(dto.isLastSending());
		}
		List<DocumentModel> docList = dto.getDocumentList();
		if (docList == null) {
			return response;
		}
		List<Attribution> attributions = new ArrayList<Attribution>();

		final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
		for (DocumentModel documentModel : docList) {
			fr.dila.reponses.api.cases.Question appQuestion = null;
			if (documentModel.hasSchema(DossierConstants.DOSSIER_SCHEMA)) {
				appQuestion = documentModel.getAdapter(Dossier.class).getQuestion(session);
			} else if (documentModel.hasSchema(DossierConstants.QUESTION_DOCUMENT_SCHEMA)) {
				appQuestion = documentModel.getAdapter(Question.class);
			}

			QuestionId qid = WsUtils.getQuestionIdFromQuestion(appQuestion);

			if (!isMinistere && qid.getSource() != origineUtilisateur) {
				response.setStatut(TraitementStatut.KO);
				response.setMessageErreur("Erreur : l'origine (AN/SENAT) de la  " + qid.getNumeroQuestion()
						+ " est différente de celle de l'utilisateur");
				return response;
			}

			Attribution newAttribution = new Attribution();
			Attributaire newAttributaire = new Attributaire();
			Ministre newMinistre = new Ministre();
			newMinistre.setId(0);
			newMinistre.setIntituleMinistere("");
			newMinistre.setTitreJo("");

			String idMinistere = "";
			AttributionType attributionType = AttributionType.REATTRIBUTION;

			Dossier dossier = null;
			try {
				dossier = getDossierFromQuestionId(qid);
			} catch (ClientException e) {
				if (response.getMessageErreur() == null) {
					response.setMessageErreur("");
				}
				response.setStatut(TraitementStatut.KO);
				response.setMessageErreur(response.getMessageErreur() + "Impossible de récupérer la question "
						+ qid.getNumeroQuestion() + " : Dossier introuvable.");
				LOGGER.error(RECUPERATION_DOS_ERROR, e);
				return response;
			}

			if (jetonSortant == null) {
				// MODE RECUPERATION ATTRIBUTION SUR LES QUESTIONS PROPOSEES

				idMinistere = dossier.getIdMinistereAttributaireCourant();
			} else {
				// MODE RECUPERATION SUR LES DERNIERS CHANGEMENTS

				List<HistoriqueAttribution> listHistoriqueAttribution = dossier.getHistoriqueAttribution(session);
				if (listHistoriqueAttribution.isEmpty()) {
					response.setStatut(TraitementStatut.KO);
					response.setMessageErreur("Attribution introuvable pour la question " + qid.getNumeroQuestion());
					return response;
				}

				// recuperation du dernier historique
				HistoriqueAttribution histo = listHistoriqueAttribution.get(listHistoriqueAttribution.size() - 1);
				idMinistere = histo.getMinAttribution();

				String typeAttribution = histo.getTypeAttribution();

				if (STVocabularyConstants.FEUILLE_ROUTE_TYPE_CREATION_REAFFECTATION.equals(typeAttribution)) {
					attributionType = AttributionType.REAFFECTATION;
				}
			}

			EntiteNode entiteNode = null;
			try {
				entiteNode = ministeresService.getEntiteNode(idMinistere);
			} catch (ClientException e) {
				response.setStatut(TraitementStatut.KO);
				if (response.getMessageErreur() == null) {
					response.setMessageErreur("");
				}
				response.setMessageErreur(response.getMessageErreur()
						+ "Impossible de récupérer l'intitulé ministère de la question " + qid.getNumeroQuestion()
						+ ".");
				LOGGER.error(RECUPERATION_ENTITE_ERROR, e);
			}

			if (!idMinistere.isEmpty()) {
				newMinistre.setId(Integer.parseInt(idMinistere));
			}
			if (entiteNode != null) {
				newMinistre.setIntituleMinistere(entiteNode.getLabel());
				newMinistre.setTitreJo(entiteNode.getEdition());
			}

			newAttributaire.setMinistre(newMinistre);
			newAttribution.setAttributaire(newAttributaire);
			newAttribution.setIdQuestion(qid);

			newAttribution.setType(attributionType);
			attributions.add(newAttribution);
		}

		response.getAttributions().addAll(attributions);

		// log dans le journal d'administration
		logWebServiceAction(ReponsesEventConstant.WEBSERVICE_CHERCHER_ATTRIBUTION_EVENT,
				ReponsesEventConstant.WEBSERVICE_CHERCHER_ATTRIBUTION_COMMENT);
		return response;
	}

	/**
	 * Webservice chercherAttributionsDate
	 * 
	 * @param request
	 * @return
	 */
	public ChercherAttributionsDateResponse chercherAttributionsDate(ChercherAttributionsDateRequest request) {
		ChercherAttributionsDateResponse response = new ChercherAttributionsDateResponse();

		Map<String, Object> resultsAccess = checkWebserviceAccess();
		if (!resultsAccess.isEmpty()) {

			return buildAttributionDateError((TraitementStatut) resultsAccess.get(MAP_TRAITEMENT_STATUT),
					(String) resultsAccess.get(MAP_MESSAGE_ERREUR));
		}

		response.setStatut(TraitementStatut.OK);
		boolean dernierRenvoi = false;

		String webservice = STWebserviceConstant.CHERCHER_ATTRIBUTIONS_DATE;
		String jetonRecu = request.getJeton();

		QuestionSource origineUtilisateur = null;
		Boolean isMinistere = false;
		String idProfilUtilisateur = null;
		QuestionId dtoPourOrigineUtilisateur = new QuestionId();
		if (!hasRightAndOrigineUtilisateur(session, webservice, dtoPourOrigineUtilisateur)) {

			return buildAttributionDateError(TraitementStatut.KO, USER_NON_AUTORISE);
		} else if (dtoPourOrigineUtilisateur.getSource() == null) {
			// Est un ministere
			isMinistere = true;
			idProfilUtilisateur = getFirstMinistereLoginFromSession(session);

		} else {
			// Est un parlementaire
			origineUtilisateur = dtoPourOrigineUtilisateur.getSource();
			if (origineUtilisateur == QuestionSource.AN) {
				idProfilUtilisateur = VocabularyConstants.QUESTION_ORIGINE_AN;
			} else if (origineUtilisateur == QuestionSource.SENAT) {
				idProfilUtilisateur = VocabularyConstants.QUESTION_ORIGINE_SENAT;
			}
		}

		JetonServiceDto dto = getJetonDTOForWebservice(idProfilUtilisateur, jetonRecu, webservice,
				request.getIdQuestions(), resultsAccess);
		if (dto == null) {

			return buildAttributionDateError((TraitementStatut) resultsAccess.get(MAP_TRAITEMENT_STATUT),
					(String) resultsAccess.get(MAP_MESSAGE_ERREUR));
		}

		String jetonSortant = null;
		if (dto.getNextJetonNumber() != null) {
			jetonSortant = dto.getNextJetonNumber().toString();
			response.setJetonAttributions(jetonSortant);
		}
		dernierRenvoi = dto.isLastSending() != null ? dto.isLastSending() : false;
		List<DocumentModel> docList = dto.getDocumentList();
		if (docList == null) {
			return response;
		}
		List<AttributionDate> attributionsDate = new ArrayList<AttributionDate>();

		if (jetonRecu == null) {
			// Si on n'a pas reçu de jeton, on est dans le cas d'une requête par id question
			for (DocumentModel documentModel : docList) {
				fr.dila.reponses.api.cases.Question appQuestion = null;
				if (documentModel.hasSchema(DossierConstants.DOSSIER_SCHEMA)) {
					appQuestion = documentModel.getAdapter(Dossier.class).getQuestion(session);
				} else if (documentModel.hasSchema(DossierConstants.QUESTION_DOCUMENT_SCHEMA)) {
					appQuestion = documentModel.getAdapter(Question.class);
				}

				QuestionId qid = WsUtils.getQuestionIdFromQuestion(appQuestion);

				if (!isMinistere && qid.getSource() != origineUtilisateur) {

					return buildAttributionDateError(TraitementStatut.KO,
							"Erreur : l'origine (AN/SENAT) de la  " + qid.getNumeroQuestion()
									+ " est différente de celle de l'utilisateur");
				}

				AttributionDate newAttribution = new AttributionDate();
				String idMinistere = "";
				AttributionType attributionType = AttributionType.REATTRIBUTION;
				Calendar dateAttribution = null;

				Dossier dossier = null;
				try {
					dossier = getDossierFromQuestionId(qid);
				} catch (ClientException e) {
					if (response.getMessageErreur() == null) {
						response.setMessageErreur("");
					}
					LOGGER.error(RECUPERATION_DOS_ERROR, e);
					return buildAttributionDateError(TraitementStatut.KO, response.getMessageErreur()
							+ "Impossible de récupérer la question " + qid.getNumeroQuestion()
							+ " : Dossier introuvable.");
				}

				if (jetonSortant == null) {
					// MODE RECUPERATION ATTRIBUTION SUR LES QUESTIONS PROPOSEES
					idMinistere = dossier.getIdMinistereAttributaireCourant();
				} else {
					// MODE RECUPERATION SUR LES DERNIERS CHANGEMENTS
					List<HistoriqueAttribution> listHistoriqueAttribution = dossier.getHistoriqueAttribution(session);
					if (listHistoriqueAttribution.isEmpty()) {

						return buildAttributionDateError(TraitementStatut.KO,
								"Attribution introuvable pour la question " + qid.getNumeroQuestion());
					}
					// recuperation du dernier historique
					HistoriqueAttribution histo = listHistoriqueAttribution.get(listHistoriqueAttribution.size() - 1);
					idMinistere = histo.getMinAttribution();
					String typeAttribution = histo.getTypeAttribution();
					dateAttribution = histo.getDateAttribution();

					if (STVocabularyConstants.FEUILLE_ROUTE_TYPE_CREATION_REAFFECTATION.equals(typeAttribution)) {
						attributionType = AttributionType.REAFFECTATION;
					}
				}

				if (!setAttributionDate(idMinistere, qid, attributionType, dateAttribution, newAttribution)) {

					if (response.getMessageErreur() == null) {
						response.setMessageErreur("");
					}
					return buildAttributionDateError(TraitementStatut.KO, response.getMessageErreur()
							+ "Impossible de récupérer l'intitulé ministère de la question " + qid.getNumeroQuestion()
							+ ".");
				}
				attributionsDate.add(newAttribution);
			}
		} else {
			// Si on a eu un numéro de jeton, on a donc des jetonDocs dans le dto
			for (DocumentModel jetonDocDoc : dto.getJetonDocDocList()) {
				JetonDoc jetonDoc = jetonDocDoc.getAdapter(JetonDoc.class);
				DocumentModel documentModel = null;
				try {
					documentModel = session.getDocument(new IdRef(jetonDoc.getIdDoc()));
				} catch (ClientException e) {
					LOGGER.error("Can't get document " + jetonDoc.getIdDoc() + " for jeton "
							+ jetonDoc.getDocument().getId(), e);
					continue;
				}

				if (documentModel != null) {
					fr.dila.reponses.api.cases.Question appQuestion = null;
					if (documentModel.hasSchema(DossierConstants.DOSSIER_SCHEMA)) {
						appQuestion = documentModel.getAdapter(Dossier.class).getQuestion(session);
					} else if (documentModel.hasSchema(DossierConstants.QUESTION_DOCUMENT_SCHEMA)) {
						appQuestion = documentModel.getAdapter(Question.class);
					}

					QuestionId qid = WsUtils.getQuestionIdFromQuestion(appQuestion);

					if (!isMinistere && qid.getSource() != origineUtilisateur) {

						return buildAttributionDateError(TraitementStatut.KO, "Erreur : l'origine (AN/SENAT) de la  "
								+ qid.getNumeroQuestion() + " est différente de celle de l'utilisateur");
					}

					AttributionDate newAttribution = new AttributionDate();
					if (jetonDoc != null
							&& !setAttributionDate(jetonDoc.getMinAttribution(), qid,
									AttributionType.valueOf(jetonDoc.getTypeAttribution().toUpperCase()),
									jetonDoc.getDateAttribution(), newAttribution)) {
						if (response.getMessageErreur() == null) {
							response.setMessageErreur("");
						}

						return buildAttributionDateError(
								TraitementStatut.KO,
								response.getMessageErreur()
										+ "Impossible de récupérer l'intitulé ministère de la question "
										+ qid.getNumeroQuestion() + ".");
					}
					attributionsDate.add(newAttribution);
				}
			}
		}

		response.setDernierRenvoi(dernierRenvoi);
		response.getAttributions().addAll(attributionsDate);

		// log dans le journal d'administration
		logWebServiceAction(ReponsesEventConstant.WEBSERVICE_CHERCHER_ATTRIBUTION_DATE_EVENT,
				ReponsesEventConstant.WEBSERVICE_CHERCHER_ATTRIBUTION_DATE_COMMENT);
		return response;

	}

	/**
	 * Webservice chercherMembresGouvernement
	 * 
	 * @param request
	 * @return
	 */
	public ChercherMembresGouvernementResponse chercherMembresGouvernement(ChercherMembresGouvernementRequest request) {
		ChercherMembresGouvernementResponse response = new ChercherMembresGouvernementResponse();
		response.setStatut(TraitementStatut.OK);

		Map<String, Object> resultsAccess = checkWebserviceAccess();
		if (!resultsAccess.isEmpty()) {
			response.setStatut((TraitementStatut) resultsAccess.get(MAP_TRAITEMENT_STATUT));
			response.setMessageErreur((String) resultsAccess.get(MAP_MESSAGE_ERREUR));
			return response;
		}

		EnFonction active = request.getEnFonction();
		if (active == null) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur("en_fonction doit prendre la valeur TRUE, FALSE ou ALL.");
			return response;
		}

		// récupération des ministères courants
		List<EntiteNode> ministeres = null;
		try {
			if (active.equals(EnFonction.TRUE)) {
				ministeres = STServiceLocator.getSTMinisteresService().getMinisteres(true);
			} else if (active.equals(EnFonction.FALSE)) {
				ministeres = STServiceLocator.getSTMinisteresService().getMinisteres(false);
			} else {
				ministeres = STServiceLocator.getSTMinisteresService().getAllMinisteres();
			}
		} catch (ClientException e) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(RECUPERATION_INFO_KO);
			LOGGER.error(RECUPERATION_INFO_KO, e);
			return response;
		}

		// ajout des ministères à la réponse
		for (OrganigrammeNode ministere : ministeres) {
			EntiteNode entite = (EntiteNode) ministere;

			MembreGouvernement mg = new MembreGouvernement();

			Ministre min = new Ministre();
			min.setId(Integer.parseInt(entite.getId()));
			min.setTitreMinistre(entite.getFormule());
			min.setIntituleMinistere(entite.getLabel());
			min.setTitreJo(entite.getEdition());
			min.setOrdreProto(entite.getOrdre().intValue());

			mg.setMinistre(min);
			mg.setEnFonction(entite.isActive());

			if (entite.getDateDebut() != null) {
				Calendar calDateDebut = new GregorianCalendar();
				calDateDebut.setTime(entite.getDateDebut());
				mg.setDateDebut(calendarToXMLGregorianCalendar(calDateDebut));
			}

			if (entite.getDateFin() != null) {
				Calendar calDateFin = new GregorianCalendar();
				calDateFin.setTime(entite.getDateFin());
				mg.setDateFin(calendarToXMLGregorianCalendar(calDateFin));
			}

			if ("Monsieur".equals(entite.getMembreGouvernementCivilite())) {
				mg.setCivilite(Civilite.M);
			} else if ("Madame".equals(entite.getMembreGouvernementCivilite())) {
				mg.setCivilite(Civilite.MME);
			} else {
				mg.setCivilite(Civilite.NON_RENSEIGNE);
			}

			mg.setNom(entite.getMembreGouvernementNom());
			mg.setPrenom(entite.getMembreGouvernementPrenom());

			response.getMembreGouvernement().add(mg);
		}

		// log dans le journal d'administration
		logWebServiceAction(ReponsesEventConstant.WEBSERVICE_CHERCHER_MEMBRES_GOUVERNEMENT_EVENT,
				ReponsesEventConstant.WEBSERVICE_CHERCHER_MEMBRES_GOUVERNEMENT_COMMENT);

		return response;
	}

	/**
	 * Webservice chercherLegislatures / Bouchon
	 * 
	 * @param request
	 * @return
	 */
	public ChercherLegislaturesResponse chercherLegislatures() {
		ChercherLegislaturesResponse response = new ChercherLegislaturesResponse();
		response.setStatut(TraitementStatut.OK);

		Map<String, Object> resultsAccess = checkWebserviceAccess();
		if (!resultsAccess.isEmpty()) {
			response.setStatut((TraitementStatut) resultsAccess.get(MAP_TRAITEMENT_STATUT));
			response.setMessageErreur((String) resultsAccess.get(MAP_MESSAGE_ERREUR));
			return response;
		}

		Session session = null;
		DocumentModelList legislatureModelList = null;
		try {
			session = STServiceLocator.getDirectoryService().open(VocabularyConstants.LEGISLATURE);
			legislatureModelList = session.getEntries();

		} catch (Exception e) {
			response.setMessageErreur(RECUPERATION_INFO_KO);
			response.setStatut(TraitementStatut.KO);
			LOGGER.error(RECUPERATION_INFO_KO, e);
			return response;
		}

		for (DocumentModel legislatureModel : legislatureModelList) {
			Integer legislatureId = null;
			Calendar legislatureDateDebut = null;
			Calendar legislatureDateFin = null;
			try {
				legislatureId = Integer.parseInt((String) legislatureModel.getProperty(
						ReponsesConstant.LEGISLATURE_SCHEMA, ReponsesConstant.LEGISLATURE_ID));
				legislatureDateDebut = (Calendar) legislatureModel.getProperty(ReponsesConstant.LEGISLATURE_SCHEMA,
						ReponsesConstant.LEGISLATURE_DATEDEBUT);
				legislatureDateFin = (Calendar) legislatureModel.getProperty(ReponsesConstant.LEGISLATURE_SCHEMA,
						ReponsesConstant.LEGISLATURE_DATEFIN);
			} catch (ClientException e1) {
				response.setMessageErreur(RECUPERATION_INFO_KO);
				response.setStatut(TraitementStatut.KO);
				LOGGER.error(RECUPERATION_INFO_KO, e1);
				return response;
			} catch (NumberFormatException e2) {
				response.setMessageErreur(RECUPERATION_INFO_KO);
				response.setStatut(TraitementStatut.KO);
				LOGGER.error(RECUPERATION_INFO_KO, e2);
				return response;
			}

			Legislature leg = new Legislature();
			leg.setId(legislatureId);
			leg.setDateDebut(calendarToXMLGregorianCalendar(legislatureDateDebut));
			leg.setDateFin(calendarToXMLGregorianCalendar(legislatureDateFin));
			response.getLegislatures().add(leg);
		}

		// log dans le journal d'administration
		logWebServiceAction(ReponsesEventConstant.WEBSERVICE_CHERCHER_LEGISLATURE_EVENT,
				ReponsesEventConstant.WEBSERVICE_CHERCHER_LEGISLATURE_COMMENT);

		return response;
	}

	// ********************************************************************** Méthodes utilitaires
	// *****************************************************
	private boolean setAttributionDate(String idMinistere, QuestionId questionId, AttributionType attributionType,
			Calendar dateAttribution, AttributionDate attribution) {
		Attributaire newAttributaire = new Attributaire();
		Ministre newMinistre = new Ministre();

		if (!idMinistere.isEmpty()) {
			newMinistre.setId(Integer.parseInt(idMinistere));
		}
		newMinistre.setIntituleMinistere("");
		newMinistre.setTitreJo("");
		EntiteNode entiteNode = null;
		try {
			entiteNode = STServiceLocator.getSTMinisteresService().getEntiteNode(idMinistere);
		} catch (ClientException e) {
			LOGGER.error(RECUPERATION_ENTITE_ERROR, e);
			return false;
		}

		if (entiteNode != null) {
			newMinistre.setIntituleMinistere(entiteNode.getLabel());
			newMinistre.setTitreJo(entiteNode.getEdition());
		}

		newAttributaire.setMinistre(newMinistre);
		attribution.setIdQuestion(questionId);
		attribution.setType(attributionType);
		if (dateAttribution != null) {
			attribution.setDateAttribution(calendarToXMLGregorianCalendar(dateAttribution));
		}
		attribution.setAttributaire(newAttributaire);

		return true;
	}

}
