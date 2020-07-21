package fr.dila.reponses.core.service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.common.utils.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;

import fr.dila.reponses.api.cases.Allotissement;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.domain.JetonDoc;
import fr.dila.reponses.api.historique.HistoriqueAttribution;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.api.service.JetonService;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.constant.STWebserviceConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Implémentation du service permettant de gérer les jetons de l'application Réponses.
 * 
 * @author jtremeaux
 */
public class JetonServiceImpl extends fr.dila.st.core.service.JetonServiceImpl implements JetonService {

	/**
	 * STLogger
	 */
	private static final STLogger	LOGGER				= STLogFactory.getLog(JetonServiceImpl.class);

	/**
	 * Serial UID.
	 */
	private static final long		serialVersionUID	= 2953725552023632500L;

	/**
	 * Default constructor
	 */
	public JetonServiceImpl() {
		super();
	}

	@Override
	public void createJetonTransmissionsAssemblees(CoreSession session, Question question, String webService)
			throws ClientException {
		final AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();

		Dossier dossier = question.getDossier(session);
		if (allotissementService.isAllotit(dossier, session)) {
			Allotissement allotissement = allotissementService.getAllotissement(dossier.getDossierLot(), session);
			List<Question> questionAlloties = allotissementService.getQuestionAlloties(session, allotissement);

			for (Question quest : questionAlloties) {
				createJetonForQuestionAndSetEtatRepondu(session, quest, webService);
			}
		} else {
			createJetonForQuestionAndSetEtatRepondu(session, question, webService);
		}
	}

	private void createJetonForQuestionAndSetEtatRepondu(CoreSession session, Question question, String webService)
			throws ClientException {
		final STParametreService parametreService = STServiceLocator.getSTParametreService();
		String delaiQuestionSignalee = parametreService.getParametreValue(session, STParametreConstant.DELAI_QUESTION_SIGNALEE);
		
		addDocumentInBasket(session, webService, question.getOrigineQuestion(), question.getDocument(), question
				.getNumeroQuestion().toString(), null, null);
		question.setEtatQuestion(session, VocabularyConstants.ETAT_QUESTION_REPONDU, Calendar.getInstance(), delaiQuestionSignalee);
		session.saveDocument(question.getDocument());
		session.save();
	}

	@Override
	protected boolean proceedHighNumberOfJetonDoc(final CoreSession session, final List<DocumentModel> jetonsDocPanier,
			final Long jetonResultSize, final List<DocumentModel> jetonsDocList,
			final List<DocumentModel> dossiersDocResult, final Long numeroJeton) {
		final AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();
		final Map<String, DocumentModel> mapJetonDossiers = new HashMap<String, DocumentModel>();
		// Pour les cas des dossiers allotis, on doit s'assurer qu'ils sont tous renvoyés dans le même résultat

		// On stocke une map avec les jetons et les id des dossiers qu'ils representent
		// M157257; cette map est utilisée pour vérifier la présence des jetons associés au dossiers allotis
		// (pour le cas de jeton associé à un dossier alloti). Il est donc nécessaire que cette map retourne
		// le premier (chronologiquement) jeton d'événement associé au dossier recherché.
		for (DocumentModel jetonDocDoc : jetonsDocPanier) {
			JetonDoc jeton = jetonDocDoc.getAdapter(JetonDoc.class);
			if (! mapJetonDossiers.containsKey(jeton.getIdDoc())) {
				mapJetonDossiers.put(jeton.getIdDoc(), jetonDocDoc);
			}
		}

		// On parcourt la liste de jeton pour construire la liste de dossiers retour
		// On gère l'allotissement des dossiers
		int jetonSize = 0;
		while (jetonSize < jetonResultSize.intValue() && !jetonsDocPanier.isEmpty()) {
			// Comme les jetons sont sortis de la liste après traitement, on récupère toujours le premier element
			DocumentModel jetonDocDoc = jetonsDocPanier.get(0);
			JetonDoc jeton = jetonDocDoc.getAdapter(JetonDoc.class);
			try {
				DocumentModel documentModel = session.getDocument(new IdRef(jeton.getIdDoc()));
				Question appQuestion = null;
				if (documentModel.hasSchema(DossierConstants.DOSSIER_SCHEMA)) {
					appQuestion = documentModel.getAdapter(Dossier.class).getQuestion(session);
				} else if (documentModel.hasSchema(DossierConstants.QUESTION_DOCUMENT_SCHEMA)) {
					appQuestion = documentModel.getAdapter(Question.class);
				}
				if (appQuestion == null) {
					LOGGER.error(session, STLogEnumImpl.UNEXPECTED_SCHEMA_TEC, documentModel);
				} else {
					// Si le dossier est alloti, il faut traiter spécifiquement
					if (allotissementService.isAllotit(appQuestion, session)) {
						final Allotissement allot = allotissementService.getAllotissement(
								appQuestion.getDossier(session).getDossierLot(), session);
						final List<String> dossiersIdsAllotis = allot.getIdDossiers();
						// Si on vient tout juste de débuter le jeton, on ajoute le lot sans se soucier d'une taille
						// minimale pour les retours
						if (jetonSize == 0) {
							gestionDossiersAllotis(session, jetonsDocPanier, dossiersIdsAllotis, mapJetonDossiers,
									jetonsDocList, dossiersDocResult, numeroJeton);
						} else {
							// Si la taille restante dans le jeton est suffisante pour ajouter l'intégralité du lot
							// On l'ajoute, sinon on ferme le jeton
							if (dossiersIdsAllotis.size() <= (jetonResultSize.intValue() - jetonSize)) {
								gestionDossiersAllotis(session, jetonsDocPanier, dossiersIdsAllotis, mapJetonDossiers,
										jetonsDocList, dossiersDocResult, numeroJeton);
							} else {
								// Il n'y a plus de place pour ces dossiers allotis, on sort de la boucle pour terminer
								// le retour de résultats
								// sans sortir ces dossiers du panier
								break;
							}
						}
						// On incrémente i du nombre de dossiers traités
						jetonSize += dossiersIdsAllotis.size();
					} else {
						// Cas d'un dossier normal, non alloti, on le traite classiquement
						setDocumentsAndJetonNumber(session, jetonDocDoc, jetonsDocList, dossiersDocResult, numeroJeton);
						jetonsDocPanier.remove(jetonDocDoc);
						++jetonSize;
					}
				}
			} catch (ClientException e1) {
				LOGGER.error(session, ReponsesLogEnumImpl.FAIL_GET_ALLOT_TEC, e1);
				break;
			}
		}

		return jetonsDocPanier.isEmpty();
	}

	/**
	 * Parcourt les dossiers d'un lot pour les ajouter à la liste de retour des dossiers (des questions pour
	 * réponses...) et pour supprimer le jetonDoc associé au dossier de la liste des jetonsDocPanier
	 * 
	 * @param session
	 * @param jetonsDocPanier
	 *            liste des jetonsDoc présent dans le panier
	 * @param dossiersIdsAllotis
	 *            liste des ids des dossiers du lot
	 * @param mapJetonDossiers
	 *            map idDossier-JetonDoc associé
	 * @param dossiersDocResult
	 *            liste des documentsModel de dossiers qu'on retourne à l'utilisateur
	 * @param numeroJeton
	 *            le numéro de jeton requêté pour la mise à jour des jetons du panier
	 * @throws ClientException
	 */
	private void gestionDossiersAllotis(final CoreSession session, final List<DocumentModel> jetonsDocPanier,
			final List<String> dossiersIdsAllotis, final Map<String, DocumentModel> mapJetonDossiers,
			final List<DocumentModel> jetonsDocList, final List<DocumentModel> dossiersDocResult, final Long numeroJeton)
			throws ClientException {
		String owner = "";
		String typeWs = "";
		if (!jetonsDocPanier.isEmpty()) {
			DocumentModel jetonDocDoc = jetonsDocPanier.get(0);
			JetonDoc jetonDoc = jetonDocDoc.getAdapter(JetonDoc.class);
			owner = jetonDoc.getIdOwner();
			typeWs = jetonDoc.getTypeWebservice();
		}

		for (String dossierId : dossiersIdsAllotis) {
			DocumentModel dossierDocLot = session.getDocument(new IdRef(dossierId));
			if (dossierDocLot != null) {
				Dossier dossier = dossierDocLot.getAdapter(Dossier.class);
				DocumentModel questionDoc = session.getDocument(new IdRef(dossier.getQuestionId()));
				DocumentModel jetonDossierDoc = null;
				// On peut avoir soit une question en id doc, ou un dossier
				// Si on a pas de jeton par id de question, on essaie par id de dossier
				jetonDossierDoc = mapJetonDossiers.get(dossier.getQuestionId());
				if (jetonDossierDoc == null) {
					jetonDossierDoc = mapJetonDossiers.get(dossier.getDocument().getId());
				}

				if (questionDoc != null) {
					// Si on n'a pas de jetonDoc il faut vérifier qu'il n'a pas été non sélectionné du panier pour les
					// perfs
					if (jetonDossierDoc == null) {
						// Les jetons contiennent des question ou ds dossiers, on essaie avec la question
						StringBuilder query = new StringBuilder("select * FROM JetonDoc WHERE jtd:id_doc = '")
								.append(questionDoc.getId()).append("' and jtd:id_owner = '").append(owner)
								.append("' and jtd:type_webservice = '").append(typeWs)
								.append("' and jtd:id_jeton = '").append(NUMERO_PANIER)
								.append("' order by jtd:created");
						List<String> resultsIds = QueryUtils.doQueryForIds(session, query.toString(), 1, 0);
						if (resultsIds.isEmpty()) {
							// Pas de résultat pour la question, on essaie avec le dossier
							query = new StringBuilder("select * FROM JetonDoc WHERE jtd:id_doc = '")
									.append(dossierDocLot.getId()).append("' and jtd:id_owner = '").append(owner)
									.append("' and jtd:type_webservice = '").append(typeWs)
									.append("' and jtd:id_jeton = '").append(NUMERO_PANIER)
									.append("' order by jtd:created");
							resultsIds = QueryUtils.doQueryForIds(session, query.toString(), 1, 0);
							if (resultsIds.isEmpty()) {
								// Pas de jeton pour ce type de ws et propriétaire aussi bien pour question que pour
								// dossier
								// On indique en warn ce dossier, et on continue de dérouler le lot
								LOGGER.warn(session, STLogEnumImpl.UNEXPECTED_JETON_TEC);
								LOGGER.info(
										session,
										STLogEnumImpl.FAIL_GET_DOCUMENT_TEC,
										"Problème avec un dossier de l'allotissement. Dossier id : " + dossierId
												+ ". Dossiers ids de l'allotissement : "
												+ StringUtils.join(dossiersIdsAllotis, ", "));
								if (questionDoc != null && jetonDossierDoc == null) {
									Question question = questionDoc.getAdapter(Question.class);
									LOGGER.info(session, ReponsesLogEnumImpl.GET_ATTR_QUEST_TEC, "Question id : "
											+ questionDoc.getId() + ". Origine : " + question.getOrigineQuestion());
								}
								continue;
							}
						}
						// On a trouvé un résultat qui match. On récupère son jeton pour le traiter avec les autres
						jetonDossierDoc = session.getDocument(new IdRef(resultsIds.get(0)));
					}
					jetonsDocPanier.remove(jetonDossierDoc);
					setDocumentsAndJetonNumber(session, jetonDossierDoc, questionDoc, jetonsDocList, dossiersDocResult,
							numeroJeton);
				}
			} else {
				LOGGER.error(session, STLogEnumImpl.FAIL_GET_DOSSIER_TEC, "id : " + dossierId);
			}
		}
	}

	/**
	 * mets à jour les attributs du jetonDoc avec le typeWS, owner, id du document à mettre dans le jeton, et le numéro
	 * du panier
	 * 
	 * @param session
	 *            Session
	 * @param typeWebservice
	 *            le type de webservice auquel appartient le jeton
	 * @param owner
	 *            Identifiant du propriétaire du jeton (noeud de l'organigramme)
	 * @param documentId
	 *            Id du document auquel est rattaché le jeton
	 * @param jetonDocDoc
	 *            le documentModel du jetonDoc nouvellement créé
	 * @param typeModification
	 *            : Type de modification. Non utilisé pour le moment dans réponse
	 * 
	 * @param id_Complementaire
	 *            : id complémentaire : Non utilisé pour le moment dans réponse
	 * @throws ClientException
	 */
	@Override
	protected void setJetonDocInBasket(final CoreSession session, final String typeWebservice, final String owner,
			final String documentId, final DocumentModel jetonDocDoc, final String typeModification,
			final List<String> idsComplementaires) throws ClientException {
		final JetonDoc jetonDoc = jetonDocDoc.getAdapter(JetonDoc.class);

		DocumentModel doc = session.getDocument(new IdRef(documentId));
		Dossier dossier = null;
		if (doc != null) {
			if (doc.hasSchema(DossierConstants.DOSSIER_SCHEMA)) {
				dossier = doc.getAdapter(Dossier.class);
			} else if (doc.hasSchema(DossierConstants.QUESTION_DOCUMENT_SCHEMA)) {
				dossier = doc.getAdapter(Question.class).getDossier(session);
			}
		}

		jetonDoc.setIdDoc(documentId);
		jetonDoc.setTypeWebservice(typeWebservice);
		jetonDoc.setIdOwner(owner);
		jetonDoc.setNumeroJeton(Long.valueOf(NUMERO_PANIER));

		// La gestion des attributions n'est utile que pour le WS chercherAttributionsDate.
		// Donc inutile de faire ces requêtes pour chaque création de jeton pour les autres types de WS.
		if (dossier != null && STWebserviceConstant.CHERCHER_ATTRIBUTIONS_DATE.equals(typeWebservice)) {
			List<HistoriqueAttribution> listHistoriqueAttribution = dossier.getHistoriqueAttribution(session);
			if (!listHistoriqueAttribution.isEmpty()) {
				// recuperation du dernier historique
				HistoriqueAttribution histo = listHistoriqueAttribution.get(listHistoriqueAttribution.size() - 1);
				jetonDoc.setDateAttribution(histo.getDateAttribution());
				jetonDoc.setMinAttribution(histo.getMinAttribution());
				jetonDoc.setTypeAttribution(histo.getTypeAttribution());
			}
		}

		session.saveDocument(jetonDoc.getDocument());
		session.save();
	}
}
