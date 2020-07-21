package fr.dila.reponses.core.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.VersioningOption;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.versioning.VersioningService;
import org.nuxeo.runtime.transaction.TransactionHelper;

import fr.dila.cm.caselink.ActionableCaseLink;
import fr.dila.ecm.platform.routing.api.DocumentRoute;
import fr.dila.ecm.platform.routing.api.DocumentRouteElement;
import fr.dila.ecm.platform.routing.api.exception.DocumentRouteAlredayLockedException;
import fr.dila.reponses.api.Exception.AllotissementException;
import fr.dila.reponses.api.Exception.AllotissementException.AllotissementExceptionRaison;
import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Allotissement;
import fr.dila.reponses.api.cases.Allotissement.TypeAllotissement;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.cases.Reponse;
import fr.dila.reponses.api.cases.flux.QuestionStateChange;
import fr.dila.reponses.api.cases.flux.RErratum;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.feuilleroute.ReponsesRouteStep;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.api.service.CorbeilleService;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.api.service.FeuilleRouteModelService;
import fr.dila.reponses.api.service.ReponseService;
import fr.dila.reponses.core.cases.AllotissementImpl;
import fr.dila.reponses.core.cases.flux.RErratumImpl;
import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.constant.STLifeCycleConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.api.service.ProfileService;
import fr.dila.st.api.service.STLockService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DocUtil;
import fr.dila.st.core.util.StringUtil;
import fr.dila.st.core.util.UnrestrictedGetDocumentRunner;

public class AllotissementServiceImpl implements AllotissementService {

	private static final Log		log		= LogFactory.getLog(AllotissementServiceImpl.class);
	/**
	 * Logger formalisé en surcouche du logger apache/log4j
	 */
	private static final STLogger	LOGGER	= STLogFactory.getLog(AllotissementServiceImpl.class);

	@Override
	public Boolean isAllotit(final Question question, final CoreSession session) throws ClientException {

		final StringBuilder query = new StringBuilder(" SELECT d.ecm:uuid as id ");
		query.append(" FROM ");
		query.append(DossierConstants.DOSSIER_DOCUMENT_TYPE);
		query.append(" as d WHERE d.dos:");
		query.append(DossierConstants.DOSSIER_DOCUMENT_QUESTION_ID);
		query.append(" = ? AND d.dos:");
		query.append(DossierConstants.DOSSIER_NOM_DOSSIER_LOT);
		query.append(" IS NOT NULL ");

		final Long count = QueryUtils.doCountQuery(session, QueryUtils.ufnxqlToFnxqlQuery(query.toString()),
				new Object[] { question.getDocument().getId() });

		return count > 0;

	}

	@Override
	public Boolean isAllotit(final Dossier dossier, final CoreSession session) {
		return StringUtils.isNotEmpty(dossier.getDossierLot());
	}

	private Boolean createLotUnRestricted(final Question questionDirectrice, final List<Question> questions,
			final CoreSession session, final Principal realUser) throws ClientException {

		final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
		final ReponseService reponseService = ReponsesServiceLocator.getReponseService();
		final JournalService journalService = STServiceLocator.getJournalService();

		final DocumentModel docDossierDirecteur = session.getDocument(questionDirectrice.getDossierRef());
		final Dossier dossierDirecteur = docDossierDirecteur.getAdapter(Dossier.class);

		final List<String> listIdDossier = new ArrayList<String>();
		listIdDossier.add(docDossierDirecteur.getId());

		if (!dossierDirecteur.hasFeuilleRoute()) {
			throw new AllotissementException(AllotissementExceptionRaison.ERROR_FDR, "Le dossier "
					+ dossierDirecteur.getNumeroQuestion() + " n'a pas de feuille de route, allotissement impossible.");
		}

		final DocumentModel routeDirectriceDocumentModel = session.getDocument(new IdRef(dossierDirecteur
				.getLastDocumentRoute()));
		final DocumentRoute docRouteDirectrice = routeDirectriceDocumentModel.getAdapter(DocumentRoute.class);
		try {
			documentRoutingService.lockDocumentRoute(docRouteDirectrice, session);
		} catch (final DocumentRouteAlredayLockedException e) {
			throw new AllotissementException(AllotissementExceptionRaison.ERROR_LOCK, "Le dossier "
					+ dossierDirecteur.getNumeroQuestion() + " est verrouillé, allotissement impossible.");
		}

		final List<Dossier> dossiers = new ArrayList<Dossier>();
		dossiers.add(dossierDirecteur);

		final DossierLink dossierLink = getLastStepDirecteurInfo(session, docDossierDirecteur);

		final Allotissement allotissement = createAllotissementDoc(session, listIdDossier);
		for (final Question question : questions) {
			
			DocumentModel docDossier = session.getDocument(question.getDossierRef());
			Dossier dossier = docDossier.getAdapter(Dossier.class);
			if (dossierDirecteur.getIdMinistereAttributaireCourant().equals(dossier.getIdMinistereAttributaireCourant())) {
				addQuestionToRouteDirectrice(session, listIdDossier, dossiers, dossierLink, question,
						routeDirectriceDocumentModel.getId(), allotissement.getNom(), dossierDirecteur.isArbitrated());
			} else {
				throw new AllotissementException(AllotissementExceptionRaison.ERROR_MINISTERE, "Allotissement du dossier "
						+ dossierDirecteur.getNumeroQuestion() + " impossible. Il ne possède pas le même ministère attributaire que le dossier directeur."
								+ "Les dossiers d'un même lot doivent appartenir au même ministère");
			}
		}
		final String comment = "Création de l'allotissement "
				+ prepareIdsDossierForLog(session, listIdDossier).toString();
		journalService.journaliserActionForUser(session, realUser, docDossierDirecteur,
				ReponsesEventConstant.DOSSIER_REPONSE_ALLOTISSEMENT, comment, STEventConstant.CATEGORY_PARAPHEUR);

		// on link tous les dossiers à la fdr directrice
		docRouteDirectrice.setAttachedDocuments(listIdDossier);
		session.saveDocument(routeDirectriceDocumentModel);

		allotissement.setIdDossiers(listIdDossier);
		session.saveDocument(allotissement.getDocument());
		session.save();

		reponseService.saveReponse(session, dossierDirecteur.getReponse(session).getDocument(),
				dossierDirecteur.getDocument());
		documentRoutingService.unlockDocumentRoute(docRouteDirectrice, session);

		session.save();
		return true;
	}

	private Allotissement createAllotissementDoc(final CoreSession session, final List<String> listIdDossier)
			throws ClientException {

		final String title = String.valueOf(Calendar.getInstance().getTimeInMillis());

		DocumentModel modelDesired = new DocumentModelImpl("/case-management/allotissements-root", title,
				DossierConstants.ALLOTISSEMENT_DOCUMENT_TYPE);
		final Allotissement allotissement = modelDesired.getAdapter(Allotissement.class);
		allotissement.setIdDossiers(listIdDossier);
		allotissement.setNom(title);
		modelDesired = session.createDocument(allotissement.getDocument());

		for (final String idDoc : listIdDossier) {
			final DocumentModel dossierDoc = session.getDocument(new IdRef(idDoc));
			final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
			dossier.setDossierLot(title);
			session.saveDocument(dossierDoc);
		}

		session.save();

		return modelDesired.getAdapter(Allotissement.class);
	}

	private StringBuilder prepareIdsDossierForLog(final CoreSession session, final List<String> idDossiers)
			throws ClientException {
		final StringBuilder idsDossier = new StringBuilder(" [");
		int index = 0;
		for (final String idDossier : idDossiers) {
			final DocumentModel doc = session.getDocument(new IdRef(idDossier));
			final Dossier dossier = doc.getAdapter(Dossier.class);
			idsDossier.append(dossier.getQuestion(session).getSourceNumeroQuestion());
			if (++index < idDossiers.size()) {
				idsDossier.append(", ");
			}
		}
		idsDossier.append("]");
		return idsDossier;
	}

	/**
	 * Parcourt les étapes de la feuille de route du dossier à allotir pour vérifier que l'étape en cours se trouve à un
	 * poste du ministère attributaire
	 * 
	 * @param routeStepsToCheck
	 * @param dossierToCheck
	 * @throws AllotissementException
	 * @throws ClientException
	 */
	private void checkCurrentStepMin(final List<DocumentModel> routeStepsToCheck, final Dossier dossierToCheck)
			throws AllotissementException, ClientException {
		final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
		ReponsesRouteStep step = null;
		final String running = DocumentRouteElement.ElementLifeCycleState.running.name();
		final Set<String> ministereIds = new HashSet<String>();
		for (final DocumentModel stepModel : routeStepsToCheck) {
			if (stepModel.getCurrentLifeCycleState().equals(running)) {
				ministereIds.clear();
				// Allotissement possible ssi le poste de l'etape courante est relié au ministere attributaire
				step = stepModel.getAdapter(ReponsesRouteStep.class);
				final List<EntiteNode> organigrammeList = mailboxPosteService.getMinistereListFromMailbox(step
						.getDistributionMailboxId());

				for (final EntiteNode entiteNode : organigrammeList) {
					ministereIds.add(entiteNode.getId().toString());
				}

				final String ministereAttributaire = dossierToCheck.getIdMinistereAttributaireCourant();

				if (StringUtils.isBlank(ministereAttributaire) || !ministereIds.contains(ministereAttributaire)) {
					throw new AllotissementException(AllotissementExceptionRaison.ERROR_FDR,
							"L'étape courante du dossier " + dossierToCheck.getNumeroQuestion()
									+ " n'est pas liée à son ministère attributaire, allotissement impossible.");
				}
			}
		}
		if (step == null) {
			throw new AllotissementException(AllotissementExceptionRaison.ERROR_FDR, "Le dossier "
					+ dossierToCheck.getNumeroQuestion() + " n'a pas d'étape en cours, allotissement impossible.");
		}
	}

	/**
	 * Ajoute une question à la route directrice Vérifie qu'une feuille de route est présente pour la question à
	 * ajouter, qu'il n'est pas verrouillé et qu'il a une étape en cours
	 * 
	 * JBT : on passe à l'état canceled les anciennes feuilles de route des questions du lot
	 * 
	 * @param session
	 *            CoreSession - la session en cours
	 * @param listIdDossier
	 *            List&lt;String&gt; - la liste des ids des dossiers présents dans le lot
	 * @param listDossier
	 *            List&lt;Dossier&gt; - la liste des dossiers présents dans le lot
	 * @param dossierLinkDirecteur
	 *            DossierLink - le dossierLink du dossier directeur du lot
	 * @param question
	 *            Question - la question à ajouter au lot
	 * @param routeDirectriceId
	 *            String - id de la route du dossier directeur
	 * @param nomAllotissement
	 *            String - le nom de l'allotissement pour lequel ajouter le dossier de la question
	 * @throws ClientException
	 */
	private void addQuestionToRouteDirectrice(final CoreSession session, final List<String> listIdDossier,
			final List<Dossier> listDossier, final DossierLink dossierLinkDirecteur, final Question question,
			final String routeDirectriceId, final String nomAllotissement, final boolean isArbitrated)
			throws ClientException {

		// Chargement des service
		final DossierDistributionService dossierDistributionService = ReponsesServiceLocator
				.getDossierDistributionService();
		final CorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();
		final FeuilleRouteModelService fdrModelService = ReponsesServiceLocator.getFeuilleRouteModelService();
		final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();

		DocumentModel dossierDoc = session.getDocument(question.getDossierRef());
		final Dossier dossier = dossierDoc.getAdapter(Dossier.class);

		if (!dossier.hasFeuilleRoute()) {
			throw new AllotissementException(AllotissementExceptionRaison.ERROR_FDR, "Le dossier "
					+ dossier.getNumeroQuestion() + " n'a pas de feuille de route, allotissement impossible.");
		}

		final DocumentModel routeDocumentModel = session.getDocument(new IdRef(dossier.getLastDocumentRoute()));
		final DocumentRoute docRoute = routeDocumentModel.getAdapter(DocumentRoute.class);

		final List<DocumentModel> routeSteps = fdrModelService.getAllRouteElement(dossier.getLastDocumentRoute(),
				session);
		// Parcourt les étapes de la feuille de route du dossier à allotir
		// pour vérifier que l'étape en cours est dans un poste identique au ministère attributaire
		// du dossier directeur du lot
		checkCurrentStepMin(routeSteps, dossier);

		try {
			documentRoutingService.lockDocumentRoute(docRoute, session);
		} catch (final DocumentRouteAlredayLockedException e) {
			throw new AllotissementException(AllotissementExceptionRaison.ERROR_LOCK, "Le dossier "
					+ dossier.getNumeroQuestion() + " est verrouillé, allotissement impossible.");
		}

		// on supprime les steps de la feuille de route de la question qui sont à venir
		final String done = DocumentRouteElement.ElementLifeCycleState.done.name();
		for (final DocumentModel stepModel : routeSteps) {
			if (!stepModel.getCurrentLifeCycleState().equals(done)) {
				documentRoutingService.softDeleteStep(session, stepModel);
			}
		}
		session.save();

		final List<DocumentModel> listDossierLink = corbeilleService.findDossierLink(session, dossierDoc.getId());
		String name = "";

		if (listDossierLink == null || listDossierLink.isEmpty()) {
			throw new AllotissementException(AllotissementExceptionRaison.ERROR_FDR, "Le dossier "
					+ dossier.getNumeroQuestion() + " n'a pas d'étape en cours, allotissement impossible.");
		}

		for (final DocumentModel documentModel : listDossierLink) {
			name = documentModel.getId();
			dossierDistributionService.deleteDossierLink(session, documentModel.getAdapter(DossierLink.class));
		}

		// on passe à l'état canceled les anciennes feuille de route, puis on la change
		if (dossier.getLastDocumentRoute() != routeDirectriceId) {
			final DocumentModel feuilleDoc = session.getDocument(new IdRef(dossier.getLastDocumentRoute()));
			final DocumentRoute feuilleRoute = feuilleDoc.getAdapter(DocumentRoute.class);
			feuilleRoute.cancel(session);
		}
		dossier.setLastDocumentRoute(routeDirectriceId);
		session.saveDocument(dossier.getDocument());

		// on duplique les droits du dossier directeur dans le dossier
		final ACP acpDirecteur = dossierLinkDirecteur.getCase(session).getDocument().getACP();
		session.setACP(dossier.getDocument().getRef(), acpDirecteur, true);
		session.save();

		// on duplique le caseLinkDirecteur et on le rattache au dossier courant
		DocumentModel docCopy = session.copy(dossierLinkDirecteur.getDocument().getRef(), dossierLinkDirecteur
				.getDocument().getParentRef(), name);
		docCopy.setProperty("case_link", "caseDocumentId", dossierDoc.getId());
		final Calendar now = Calendar.getInstance();
		docCopy.setProperty("case_link", "date", now);
		DublincoreSchemaUtils.setModifiedDate(docCopy, now);
		DublincoreSchemaUtils.setTitle(docCopy, "Dossier n°" + dossier.getNumeroQuestion());
		docCopy = session.saveDocument(docCopy);

		// re-set all fields
		dossierDistributionService.setDossierLinksFields(session, docCopy);

		// on duplique les droits du caseLinkDirecteur directeur dans le caseLink
		final ACP acpDLDirecteur = dossierLinkDirecteur.getDocument().getACP();
		session.setACP(docCopy.getRef(), acpDLDirecteur, true);
		session.saveDocument(docCopy);

		// on ajoute le dossier sur la feuille de route du dossier directeur
		listIdDossier.add(dossierDoc.getId());
		listDossier.add(dossier);
		session.save();

		// On met à jour l'arbitrage
		dossier.setIsArbitrated(isArbitrated);
		// On met à jour la signature de ce dossier avec la signature du dossier directeur
		dossierDoc = dossier.getDocument();
		dossier.setDossierLot(nomAllotissement);
		updateSignatureFor(session, dossierDoc, dossierLinkDirecteur.getDossier(session).getReponse(session));
		session.saveDocument(dossierDoc);

		documentRoutingService.unlockDocumentRoute(docRoute, session);
		session.save();
	}

	@Override
	public Boolean updateLot(final Question questionDirectrice, final List<Question> listQuestions,
			final CoreSession session, final TypeAllotissement type) throws ClientException {
		final Principal currentUser = session.getPrincipal();
		new UnrestrictedSessionRunner(session) {
			@Override
			public void run() throws ClientException {
				try {
					switch (type) {
						case SUPPR:
							removeFromLot(questionDirectrice, listQuestions, session, currentUser);
							break;
						case AJOUT:
							addToLot(questionDirectrice, listQuestions, session, currentUser);
							break;
						default:
							break;
					}
				} catch (final ClientException e) {
					TransactionHelper.setTransactionRollbackOnly();
					log.error("Erreur dans la mise à jour du lot ", e);
					throw e;
				}

			}
		}.runUnrestricted();
		return true;

	}

	@Override
	public Boolean createLot(final Question questionDirectrice, final List<Question> listQuestions,
			final CoreSession session) throws ClientException {

		final Principal currentUser = session.getPrincipal();
		new UnrestrictedSessionRunner(session) {
			@Override
			public void run() throws ClientException {
				try {
					createLotUnRestricted(questionDirectrice, listQuestions, session, currentUser);
				} catch (final ClientException e) {
					TransactionHelper.setTransactionRollbackOnly();
					throw e;
				}
			}
		}.runUnrestricted();
		return true;

	}

	@Override
	public Allotissement getAllotissement(final String nom, final CoreSession session) throws ClientException {
		if (nom == null || nom.isEmpty()) {
			return null;
		}
		final StringBuilder query = new StringBuilder("SELECT a.ecm:uuid as id FROM ");
		query.append(DossierConstants.ALLOTISSEMENT_DOCUMENT_TYPE);
		query.append(" as a WHERE a.allot:nom = ? ");

		final List<DocumentModel> docs = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session,
				DossierConstants.ALLOTISSEMENT_DOCUMENT_TYPE, query.toString(), new Object[] { nom });
		if (docs == null || docs.isEmpty()) {
			return null;
		} else {
			return new AllotissementImpl(docs.get(0));
		}
	}

	/**
	 * methode appelee avec une session unrestricted
	 * 
	 * @param questionDirectrice
	 * @param listQuestions
	 * @param session
	 * @throws ClientException
	 */
	private void removeFromLot(final Question questionDirectrice, final List<Question> listQuestions,
			final CoreSession session, final Principal realUser) throws ClientException {

		final DocumentModel docDossierDirecteur = session.getDocument(questionDirectrice.getDossierRef());
		final Dossier dossierDirecteur = docDossierDirecteur.getAdapter(Dossier.class);

		final CorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();
		final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
		final JournalService journalService = STServiceLocator.getJournalService();

		if (dossierDirecteur.getLastDocumentRoute() == null) {
			throw new AllotissementException(AllotissementExceptionRaison.ERROR_FDR, "Le dossier "
					+ dossierDirecteur.getNumeroQuestion()
					+ " n'a pas de feuille de route, désallotissement impossible.");
		}

		final DocumentModel routeDirectriceDocumentModel = session.getDocument(new IdRef(dossierDirecteur
				.getLastDocumentRoute()));
		final DocumentRoute docRouteDirectrice = routeDirectriceDocumentModel.getAdapter(DocumentRoute.class);
		try {
			documentRoutingService.lockDocumentRoute(docRouteDirectrice, session);
		} catch (final DocumentRouteAlredayLockedException e) {
			throw new AllotissementException(AllotissementExceptionRaison.ERROR_LOCK, "Le dossier "
					+ dossierDirecteur.getNumeroQuestion() + " est verrouillé, désallotissement impossible.");
		}

		final List<String> listIdDossier = docRouteDirectrice.getAttachedDocuments();
		final Allotissement allotissement = getAllotissement(dossierDirecteur.getDossierLot(), session);
		final StringBuilder idsDossier = prepareIdsDossierForLog(session, allotissement.getIdDossiers());
		final List<String> questionRetirees = new ArrayList<String>();
		String srcNumQuestion = "";
		for (final Question q : listQuestions) {
			final DocumentModel dossierDoc = session.getDocument(q.getDossierRef());
			final Dossier dossier = dossierDoc.getAdapter(Dossier.class);

			if (!listIdDossier.contains(dossierDoc.getId())) {
				// si le dossier est pas lié on le passe
				continue;
			}

			// on duplique la feuille de route du dossier directeur dans le répertoire des fdr
			final DocumentModel docCopy = session.copy(routeDirectriceDocumentModel.getRef(),
					routeDirectriceDocumentModel.getParentRef(), routeDirectriceDocumentModel.getName());
			final DocumentRoute docRouteCopy = docCopy.getAdapter(DocumentRoute.class);

			// initialise le champs denormalisé "documentRouteId dans les étapes de la fdr
			documentRoutingService.initDocumentRouteStep(session, docCopy);

			// on rattache que ce dossier a la fdr
			final List<String> idDossiers = new ArrayList<String>();
			idDossiers.add(dossierDoc.getId());
			docRouteCopy.setAttachedDocuments(idDossiers);
			session.saveDocument(docCopy);

			// on duplique les droits de la feuille de route directrice dans la copie
			final ACP acpDirecteur = routeDirectriceDocumentModel.getACP();
			session.setACP(docCopy.getRef(), acpDirecteur, true);
			session.saveDocument(docCopy);

			session.save();

			// modification de la fdr dans le dossier : les traces sont là pour valider la présence d'un bug
			log.info("Feuille de route du dossier avant suppression du lot " + dossier.getNumeroQuestion() + " :"
					+ dossier.getDocument().getId() + " : " + dossier.getLastDocumentRoute());
			dossier.setLastDocumentRoute(docCopy.getId());
			log.info("Feuille de route du dossier après suppression du lot " + dossier.getNumeroQuestion() + " :"
					+ dossier.getDocument().getId() + " : " + dossier.getLastDocumentRoute());
			// delinkage de la reference du lot dans le dossier
			dossier.setDossierLot(null);
			session.saveDocument(dossier.getDocument());
			session.save();
			final DocumentModelList stepTable = documentRoutingService.getOrderedRouteElement(
					routeDirectriceDocumentModel.getId(), session);

			final Map<String, String> mapOldToNew = new HashMap<String, String>();
			// on recupere l'id du step en cours pour mettre le dossierLink a jour
			final DocumentModelList stepTableCopy = documentRoutingService.getOrderedRouteElement(docCopy.getId(),
					session);
			int i = 0;
			for (final DocumentModel stepDoc : stepTableCopy) {
				mapAllRunningStep(mapOldToNew, stepDoc, stepTable.get(i), session);
				i++;
			}

			if (!mapOldToNew.isEmpty()) {
				// on met le dossierLink à jour
				final List<DocumentModel> listDossierLink = corbeilleService.findDossierLink(session,
						dossierDoc.getId());
				DossierLink dossierLink = null;
				for (final DocumentModel documentModel : listDossierLink) {
					dossierLink = documentModel.getAdapter(DossierLink.class);
					if (dossierLink.isActionnable()) {
						final ActionableCaseLink acl = dossierLink.getDocument().getAdapter(ActionableCaseLink.class);
						final String routingTaskId = mapOldToNew.get(acl.getStepId());
						if (StringUtils.isEmpty(routingTaskId)) {
							throw new AllotissementException(AllotissementExceptionRaison.ERROR_FDR,
									"Impossible de distribuer le dossier dans la corbeille");
						}
						acl.setStepId(routingTaskId);
						dossierLink.setRoutingTaskId(routingTaskId);
						session.saveDocument(dossierLink.getDocument());
					}
				}
			} else if (!q.isRepondue()) {
				// On n'a pas trouvé de dossier link, et si la question n'est pas répondue, elle doit être dans une
				// corbeille
				throw new AllotissementException(AllotissementExceptionRaison.ERROR_FDR,
						"Impossible de distribuer le dossier dans la corbeille");
			}

			// on enleve le dossier sur la feuille de route du dossier directeur
			listIdDossier.remove(dossierDoc.getId());

			// journalisation du dossier retiré
			srcNumQuestion = q.getSourceNumeroQuestion();
			questionRetirees.add(srcNumQuestion);
			final String comment = "Dossier " + srcNumQuestion + " retiré du lot " + idsDossier.toString();
			journalService.journaliserActionForUser(session, realUser, dossierDoc,
					ReponsesEventConstant.DOSSIER_REPONSE_ALLOTISSEMENT, comment, STEventConstant.CATEGORY_PARAPHEUR);

			session.save();
		}
		// on save tous les dossiers encore liés à la fdr directrice
		docRouteDirectrice.setAttachedDocuments(listIdDossier);
		session.saveDocument(routeDirectriceDocumentModel);
		session.save();
		final String pluriel = questionRetirees.size() > 1 ? "s" : "";
		final String comment = "Dossier" + pluriel + " désalloti" + pluriel + " : "
				+ StringUtils.join(questionRetirees, ", ");
		if (listIdDossier.size() > 1) {
			final DocumentModel dossierDoc = session.getDocument(new IdRef(listIdDossier.get(0)));
			journalService.journaliserActionForUser(session, realUser, dossierDoc,
					ReponsesEventConstant.DOSSIER_REPONSE_ALLOTISSEMENT, comment, STEventConstant.CATEGORY_PARAPHEUR);
			allotissement.setIdDossiers(listIdDossier);
			session.saveDocument(allotissement.getDocument());
		} else {
			for (final String dossierId : allotissement.getIdDossiers()) {
				final DocumentModel doc = session.getDocument(new IdRef(dossierId));
				doc.getAdapter(Dossier.class).setDossierLot(null);
				session.saveDocument(doc);
			}
			// on delete l'allotissement
			LOGGER.info(session, ReponsesLogEnumImpl.DEL_ALLOT_TEC, allotissement.getDocument());
			DocumentModel allotissementDoc = allotissement.getDocument();
			DocumentRef allotRef = allotissementDoc.getRef();
			session.followTransition(allotRef, STLifeCycleConstant.TO_DELETE_TRANSITION);
		}

		documentRoutingService.unlockDocumentRoute(docRouteDirectrice, session);
		session.save();
	}

	private void mapAllRunningStep(final Map<String, String> mapOldToNew, final DocumentModel stepDoc,
			final DocumentModel oldStep, final CoreSession session) throws ClientException {
		if (DocumentRouteElement.ElementLifeCycleState.running.name().equals(stepDoc.getCurrentLifeCycleState())) {
			if (STConstant.STEP_FOLDER_DOCUMENT_TYPE.equals(stepDoc.getType())) {
				final DocumentModelList listOld = session.getChildren(oldStep.getRef());
				final DocumentModelList listNew = session.getChildren(stepDoc.getRef());
				int stepIndex = 0;
				for (final DocumentModel stepDocNew : listNew) {
					mapAllRunningStep(mapOldToNew, stepDocNew, listOld.get(stepIndex), session);
					stepIndex++;
				}

			} else {
				mapOldToNew.put(oldStep.getId(), stepDoc.getId());
			}
		}

	}

	/**
	 * Methode appelée avec une session unrestricted
	 * 
	 * @param questionDirectrice
	 * @param listQuestions
	 * @param session
	 * @throws ClientException
	 */
	private void addToLot(final Question questionDirectrice, final List<Question> listQuestions,
			final CoreSession session, final Principal realUser) throws ClientException {
		// Chargement des services
		final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
		final ReponseService reponseService = ReponsesServiceLocator.getReponseService();
		final JournalService journalService = STServiceLocator.getJournalService();

		final DocumentModel docDossierDirecteur = session.getDocument(questionDirectrice.getDossierRef());
		final Dossier dossierDirecteur = docDossierDirecteur.getAdapter(Dossier.class);

		final String idMinistereAttributaireDirecteur = dossierDirecteur.getIdMinistereAttributaireCourant();

		if (!dossierDirecteur.hasFeuilleRoute()) {
			throw new AllotissementException(AllotissementExceptionRaison.ERROR_FDR, "Le dossier "
					+ dossierDirecteur.getNumeroQuestion() + " n'a pas de feuille de route, allotissement impossible.");
		}

		final DocumentModel routeDirectriceDocumentModel = session.getDocument(new IdRef(dossierDirecteur
				.getLastDocumentRoute()));
		final DocumentRoute docRouteDirectrice = routeDirectriceDocumentModel.getAdapter(DocumentRoute.class);

		try {
			documentRoutingService.lockDocumentRoute(docRouteDirectrice, session);
		} catch (final DocumentRouteAlredayLockedException e) {
			throw new AllotissementException(AllotissementExceptionRaison.ERROR_LOCK, "Le dossier "
					+ dossierDirecteur.getNumeroQuestion() + " est verrouillé, allotissement impossible.");
		}

		final Allotissement allotissement = getAllotissement(dossierDirecteur.getDossierLot(), session);
		final List<String> listIdDossier = docRouteDirectrice.getAttachedDocuments();

		final DossierLink dossierLink = getLastStepDirecteurInfo(session, docDossierDirecteur);

		final List<Dossier> listDossier = new ArrayList<Dossier>();
		final String comment = " ajouté au lot" + prepareIdsDossierForLog(session, listIdDossier).toString();
		for (final Question q : listQuestions) {
			if (q.getIdMinistereAttributaire().equals(idMinistereAttributaireDirecteur)) {
				addQuestionToRouteDirectrice(session, listIdDossier, listDossier, dossierLink, q,
						routeDirectriceDocumentModel.getId(), allotissement.getNom(), dossierDirecteur.isArbitrated());

				journalService.journaliserActionForUser(session, realUser, q.getDossier(session).getDocument(),
						ReponsesEventConstant.DOSSIER_REPONSE_ALLOTISSEMENT, "Dossier " + q.getSourceNumeroQuestion()
								+ comment, STEventConstant.CATEGORY_PARAPHEUR);
				session.save();
			} else {
				throw new AllotissementException(AllotissementExceptionRaison.ERROR_FDR, "La question N° "
						+ q.getNumeroQuestion()
						+ " n'est pas liée au même ministère attributaire, allotissement impossible.");
			}
		}

		// on link tous les dossiers à la fdr directrice
		docRouteDirectrice.setAttachedDocuments(listIdDossier);
		session.saveDocument(routeDirectriceDocumentModel);

		allotissement.setIdDossiers(listIdDossier);
		session.saveDocument(allotissement.getDocument());
		session.save();

		reponseService.saveReponse(session, dossierDirecteur.getReponse(session).getDocument(),
				dossierDirecteur.getDocument());

		documentRoutingService.unlockDocumentRoute(docRouteDirectrice, session);

		session.save();
	}

	private DossierLink getLastStepDirecteurInfo(final CoreSession session, final DocumentModel docDossierDirecteur)
			throws ClientException, PropertyException {

		final CorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();
		final List<DocumentModel> listDossierLinkDirecteur = corbeilleService.findDossierLink(session,
				docDossierDirecteur.getId());

		DossierLink dossierLinkDirecteur = null;
		DossierLink dossierLink = null;
		for (final DocumentModel documentModel : listDossierLinkDirecteur) {
			dossierLinkDirecteur = documentModel.getAdapter(DossierLink.class);
			if (dossierLinkDirecteur.isActionnable()) {
				if (dossierLink == null) {
					dossierLink = dossierLinkDirecteur;
				} else {
					throw new AllotissementException(AllotissementExceptionRaison.ERROR_FDR,
							"La feuille de route directrice n'est pas allotissable.");
				}

			}
		}

		if (dossierLink == null) {
			throw new AllotissementException(AllotissementExceptionRaison.ERROR_FDR,
					"La feuille de route directrice est vide ou terminée, allotissement impossible.");
		}

		// Allotissement possible ssi le poste de l'etape courante est relié au ministere attributaire

		final DocumentModel currentStep = session.getDocument(new IdRef(dossierLink.getRoutingTaskId()));
		final ReponsesRouteStep stepDirecteur = currentStep.getAdapter(ReponsesRouteStep.class);

		final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
		final List<EntiteNode> organigrammeList = mailboxPosteService.getMinistereListFromMailbox(stepDirecteur
				.getDistributionMailboxId());
		final Set<String> ministereIds = new HashSet<String>();

		for (final EntiteNode entiteNode : organigrammeList) {
			ministereIds.add(entiteNode.getId().toString());
		}

		final Dossier dossier = docDossierDirecteur.getAdapter(Dossier.class);
		final String ministereAttributaireDirecteur = dossier.getIdMinistereAttributaireCourant();

		if (StringUtils.isBlank(ministereAttributaireDirecteur)
				|| !ministereIds.contains(ministereAttributaireDirecteur)) {
			throw new AllotissementException(AllotissementExceptionRaison.ERROR_FDR, "L'étape courante du dossier "
					+ dossier.getNumeroQuestion()
					+ " n'est pas lié à son ministère attributaire, allotissement impossible.");
		}

		return dossierLink;
	}

	@Override
	public void updateTexteLinkedReponses(final CoreSession session, final DocumentModel repDoc) throws ClientException {
		final UnrestrictedGetDocumentRunner uGet = new UnrestrictedGetDocumentRunner(session);

		final Reponse reponse = repDoc.getAdapter(Reponse.class);
		final DocumentRoute docRouteDirectrice = getLastRoutefromReponse(session, reponse);

		for (final String idDoc : docRouteDirectrice.getAttachedDocuments()) {
			final DocumentModel dossierDocLinked = uGet.getById(idDoc);
			final Dossier dossierLinked = dossierDocLinked.getAdapter(Dossier.class);
			final Reponse repLinked = dossierLinked.getReponse(session);
			if (!repLinked.getDocument().getId().equals(repDoc.getId())) {
				repLinked.setTexteReponse(reponse.getTexteReponse());

				// copie de l'erratum
				if (reponse.isPublished()) {
					final RErratum currentErratum = new RErratumImpl();
					currentErratum.setPageJo(0);
					currentErratum.setTexteErratum(reponse.getCurrentErratum());
					currentErratum.setTexteConsolide(reponse.getTexteReponse());
					final List<RErratum> erratums = repLinked.getErrata();
					erratums.add(currentErratum);
					repLinked.setErrata(erratums);
					repLinked.setCurrentErratum(reponse.getCurrentErratum());
				}

				session.saveDocument(repLinked.getDocument());
			}
		}

		session.save();

	}

	@Override
	public void updateVersionLinkedReponses(final CoreSession session, final DocumentModel repDoc)
			throws ClientException {
		final UnrestrictedGetDocumentRunner uGet = new UnrestrictedGetDocumentRunner(session);

		final Reponse reponse = repDoc.getAdapter(Reponse.class);
		final DocumentRoute docRouteDirectrice = getLastRoutefromReponse(session, reponse);

		if (docRouteDirectrice != null) {
			for (final String idDoc : docRouteDirectrice.getAttachedDocuments()) {
				final DocumentModel dossierDocLinked = uGet.getById(idDoc);
				final Dossier dossierLinked = dossierDocLinked.getAdapter(Dossier.class);
				final Reponse repLinked = dossierLinked.getReponse(session);
				if (!repLinked.getDocument().getId().equals(repDoc.getId())) {
					repLinked.getDocument().putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.MAJOR);
					session.saveDocument(repLinked.getDocument());
					session.save();
				}
			}
		}
	}

	@Override
	public void updateSignatureLinkedReponses(final CoreSession session, final Reponse reponse) throws ClientException {
		final DocumentRoute docRouteDirectrice = getLastRoutefromReponse(session, reponse);
		for (final String dossierId : docRouteDirectrice.getAttachedDocuments()) {
			final DocumentModel dossierAllotiDoc = session.getDocument(new IdRef(dossierId));
			updateSignatureFor(session, dossierAllotiDoc, reponse);
		}
		session.save();
	}

	/**
	 * Met à jour la signature pour un dossier donné
	 * 
	 * @param session
	 * @param dossier
	 *            DocumentModel - Document pour lequel la signature est à mettre à jour
	 * @param reponse
	 *            Reponse - La réponse existante
	 * @throws ClientException
	 */
	private void updateSignatureFor(final CoreSession session, final DocumentModel dossierDoc, final Reponse reponse)
			throws ClientException {
		final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
		final Reponse reponseDossier = dossier.getReponse(session);
		if (reponseDossier != null) {
			reponseDossier.setSignature(reponse.getSignature());
			reponseDossier.setIsSignatureValide(reponse.getIsSignatureValide());
			// Ajout de l'auteur du retrait de la signature le cas échéant
			reponseDossier.setAuthorRemoveSignature(reponse.getAuthorRemoveSignature());
			session.saveDocument(reponseDossier.getDocument());
		}
	}

	private DocumentRoute getLastRoutefromReponse(final CoreSession session, final Reponse reponse)
			throws ClientException {
		final UnrestrictedGetDocumentRunner uGet = new UnrestrictedGetDocumentRunner(session);
		final DocumentModel dossierDoc = uGet.getByRef(reponse.getDocument().getParentRef());
		final Dossier dossier = dossierDoc.getAdapter(Dossier.class);

		if (!dossier.hasFeuilleRoute()) {
			return null;
		}

		final DocumentModel routeDirectriceDocumentModel = uGet.getByRef(new IdRef(dossier.getLastDocumentRoute()));
		final DocumentRoute docRouteDirectrice = routeDirectriceDocumentModel.getAdapter(DocumentRoute.class);

		return docRouteDirectrice;
	}

	@Override
	public List<Question> getQuestionAlloties(final CoreSession session, final Allotissement allotissement)
			throws ClientException {
		final List<String> idDossiers = allotissement.getIdDossiers();
		if (idDossiers == null || idDossiers.isEmpty()) {
			return Collections.emptyList();
		}
		final List<String> idQuestions = new ArrayList<String>();
		List<DocumentModel> dml = QueryUtils.retrieveDocuments(session, DossierConstants.DOSSIER_DOCUMENT_TYPE,
				idDossiers);
		for (final DocumentModel doc : dml) {
			final Dossier dossier = doc.getAdapter(Dossier.class);
			idQuestions.add(dossier.getQuestionId());
		}

		final List<Question> questionList = new ArrayList<Question>();
		if (!idQuestions.isEmpty()) {
			dml = QueryUtils.retrieveDocuments(session, DossierConstants.QUESTION_DOCUMENT_TYPE, idQuestions);
			for (final DocumentModel doc : dml) {
				final Question question = doc.getAdapter(Question.class);
				questionList.add(question);
			}
		}
		return questionList;
	}

	@Override
	public List<Question> getQuestionAllotiesWithOrder(final CoreSession session, final Allotissement allotissement)
			throws ClientException {
		final List<String> idDossiers = allotissement.getIdDossiers();
		if (idDossiers == null || idDossiers.isEmpty()) {
			return Collections.emptyList();
		}
		final List<String> idQuestions = new ArrayList<String>();
		final List<DocumentModel> dml = new ArrayList<DocumentModel>();
		for (final String idDossier : idDossiers) {
			dml.add(session.getDocument(new IdRef(idDossier)));
		}
		for (final DocumentModel doc : dml) {
			final Dossier dossier = doc.getAdapter(Dossier.class);
			idQuestions.add(dossier.getQuestionId());
		}

		final List<Question> questionList = new ArrayList<Question>();
		final List<DocumentModel> dml2 = new ArrayList<DocumentModel>();
		if (!idQuestions.isEmpty()) {
			for (final String idQuestion : idQuestions) {
				dml2.add(session.getDocument(new IdRef(idQuestion)));
			}
			for (final DocumentModel doc : dml2) {
				final Question question = doc.getAdapter(Question.class);
				questionList.add(question);
			}
		}
		return questionList;
	}

	@Override
	public List<Question> getQuestionAllotiesWithOrderOrigineNumero(final CoreSession session, final Allotissement allotissement)
			throws ClientException {
		final List<String> idDossiers = allotissement.getIdDossiers();
		if (idDossiers == null || idDossiers.isEmpty()) {
			return Collections.emptyList();
		}

		final List<DocumentModel> dossierDocs = QueryUtils.retrieveDocuments(session,
				DossierConstants.DOSSIER_DOCUMENT_TYPE, idDossiers);
		final List<Dossier> dossiers;
		final List<DocumentModel> questionDocs;
		if (dossierDocs == null || dossierDocs.isEmpty()) {
			return Collections.emptyList();
		} else {
			dossiers = DocUtil.adapt(dossierDocs, Dossier.class);
		}
		
		final List<String> idQuestions = new ArrayList<String>();
		for (final Dossier dossier : dossiers) {
			idQuestions.add(dossier.getQuestionId());
		}

		final StringBuilder questionQuery = new StringBuilder("SELECT q.ecm:uuid AS id FROM ");
		questionQuery.append(DossierConstants.QUESTION_DOCUMENT_TYPE);
		questionQuery.append(String.format(" AS q "));
		questionQuery.append(String.format(" WHERE q.ecm:uuid IN (%s) ", StringUtil.getQuestionMark(idQuestions.size())));
		questionQuery.append(" ORDER BY q.qu:origineQuestion ASC, q.qu:numeroQuestion ASC ");
		questionDocs = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, DossierConstants.QUESTION_DOCUMENT_SCHEMA,
				questionQuery.toString(), idQuestions.toArray(new Object[0]));

		return DocUtil.adapt(questionDocs, Question.class);
	}

	@Override
	public void removeDossierFromLotIfNeeded(final CoreSession session, final Dossier dossier)
			throws AllotissementException {
		if (dossier == null) {
			return;
		}
		if (isAllotit(dossier, session)) {
			try {
				final Question question = dossier.getQuestion(session);
				final List<Question> questions = new ArrayList<Question>();
				questions.add(question);
				updateLot(question, questions, session, TypeAllotissement.SUPPR);
				log.info("Dossier " + dossier.getDocument().getId() + " :  " + dossier.getNumeroQuestion() + " : "
						+ " retiré de son lot, nouvelle feuille de route : " + dossier.getLastDocumentRoute());
			} catch (final ClientException e) {
				log.error(String.format("Impossible de retirer le dossier %s de son lot %s", dossier.getDocument()
						.getId(), dossier.getDossierLot()));
				throw new AllotissementException(AllotissementExceptionRaison.ERROR_FDR, "Retrait dossier impossible");
			}
		}
	}

	@Override
	public Boolean createLotWS(final Question question, final List<Question> listQuestions, final CoreSession session) {
		final Principal currentUser = session.getPrincipal();
		try {
			new UnrestrictedSessionRunner(session) {
				@Override
				public void run() throws ClientException {
					try {
						createLotUnRestricted(question, listQuestions, session, currentUser);
					} catch (final Exception e) {
						sendErrorMailLotToAdmin(session, question, listQuestions, e);
					}
				}
			}.runUnrestricted();
		} catch (final ClientException e) {
			log.error(e);
		}
		return true;
	}

	/**
	 * Envoie du mail d'erreur aux admin fonctionnels
	 * 
	 * @param session
	 * @param questionDirectrice
	 * @param listQuestions
	 * @param exceptionMessage
	 */
	protected void sendErrorMailLotToAdmin(final CoreSession session, final Question questionDirectrice,
			final List<Question> listQuestions, final Throwable throwable) {
		final StringBuilder email = new StringBuilder("Question : ");
		email.append(questionDirectrice.getTypeQuestion()).append(" ").append(questionDirectrice.getOrigineQuestion())
				.append(" ").append(questionDirectrice.getNumeroQuestion());
		email.append("\nQuestion de rappel : ");
		for (final Question question : listQuestions) {
			email.append(question.getTypeQuestion()).append(" ").append(question.getOrigineQuestion()).append(" ")
					.append(question.getNumeroQuestion()).append("\n");
		}

		log.info("Send mail : \n" + email.toString(), throwable);

		try {
			final ProfileService profileService = STServiceLocator.getProfileService();
			final List<STUser> users = profileService
					.getUsersFromBaseFunction(STBaseFunctionConstant.ADMIN_FONCTIONNEL_EMAIL_RECEIVER);

			final String text = email.toString() + "\n" + throwable.getMessage();
			final String object = "[Réponses] Allotissement de question impossible";

			STServiceLocator.getSTMailService().sendMailToUserList(session, users, object, text);
		} catch (final Exception e1) {
			log.error("Erreur d'envoi du mail lors de l'allotissement", e1);
		}
	}

	@Override
	public boolean validateDossierRappel(final CoreSession session, final Dossier dossierRappele, final String idMinistereQuestion,
			final Question questionRappel) throws ClientException {

		final CorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();
		final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();

		try {
			// Test de l'état de la question rappelée
			final Question questionRappele = dossierRappele.getQuestion(session);
			if (questionRappele != null) {
				final QuestionStateChange quState = questionRappele.getEtatQuestion(session);
				if (quState != null && quState.getNewState().equals(VocabularyConstants.ETAT_QUESTION_REPONDU)) {
					throw new AllotissementException(AllotissementExceptionRaison.ERROR_ETAT_DOSSIER, "Le dossier "
							+ dossierRappele.getNumeroQuestion() + " est à l'état répondu, allotissement impossible.");
				}
			}

			// Test de l'état de la feuille de route
			new UnrestrictedSessionRunner(session) {
				@Override
				public void run() throws ClientException {
					// Test de la présence de la feuille de route
					if (!dossierRappele.hasFeuilleRoute()) {
						throw new AllotissementException(AllotissementExceptionRaison.ERROR_FDR, "Le dossier "
								+ dossierRappele.getNumeroQuestion()
								+ " n'a pas de feuille de route, allotissement impossible.");
					}

					// Allotissement possible si le poste de l'etape courante est relié au ministere attributaire
					final List<DocumentModel> listDossierLinkDirecteurDoc = corbeilleService.findDossierLink(session,
							dossierRappele.getDocument().getId());
					
					final String ministereAttributaireDirecteur = dossierRappele
							.getIdMinistereAttributaireCourant();

					DossierLink dossierLink = null;
					for (final DocumentModel documentModel : listDossierLinkDirecteurDoc) {
						dossierLink = documentModel.getAdapter(DossierLink.class);
						if (dossierLink.isActionnable()) {

							final DocumentModel currentStepDoc = session.getDocument(new IdRef(dossierLink
									.getRoutingTaskId()));
							final ReponsesRouteStep currentStep = currentStepDoc.getAdapter(ReponsesRouteStep.class);

							final List<EntiteNode> organigrammeList = mailboxPosteService
									.getMinistereListFromMailbox(currentStep.getDistributionMailboxId());

							final Set<String> ministereIds = new HashSet<String>();
							for (final EntiteNode entiteNode : organigrammeList) {
								ministereIds.add(entiteNode.getId().toString());
							}

							if (StringUtils.isBlank(ministereAttributaireDirecteur)
									|| !ministereIds.contains(ministereAttributaireDirecteur)) {
								throw new AllotissementException(
										AllotissementExceptionRaison.ERROR_FDR,
										"L'étape courante du dossier "
												+ dossierRappele.getNumeroQuestion()
												+ " n'est pas lié à son ministère attributaire, allotissement impossible.");
							}
						}
					}

					// Test de la présence d'une étape active
					if (dossierLink == null) {
						throw new AllotissementException(AllotissementExceptionRaison.ERROR_FDR,
								"La feuille de route directrice est vide ou terminée, allotissement impossible.");
					}
					
					// Allotissement possible si la question et le dossier rappelé ont le même ministère attributaire					
					if (StringUtils.isBlank(ministereAttributaireDirecteur)
							|| !ministereAttributaireDirecteur.equals(idMinistereQuestion)) {
						throw new AllotissementException(
								AllotissementExceptionRaison.ERROR_MINISTERE,
								"Le dossier rappelé "
										+ dossierRappele.getNumeroQuestion()
										+ " n'est pas attribué au même ministère que la question créée, allotissement impossible.");
					}
				}
			}.runUnrestricted();
		} catch (final Exception e) {
			sendErrorMailLotToAdmin(session, dossierRappele.getQuestion(session),
					Collections.singletonList(questionRappel), e);
			return false;
		}

		return true;
	}

	@Override
	public void createOrAddToLotRappel(final Question questionDirectrice, final Question questionRappel,
			final CoreSession session) {
		final STLockService stLockService = STServiceLocator.getSTLockService();
		final JournalService journalService = STServiceLocator.getJournalService();
		final CorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();
		final ReponseService reponseService = ReponsesServiceLocator.getReponseService();
		final DossierDistributionService dossierDistributionService = ReponsesServiceLocator
				.getDossierDistributionService();

		try {
			new UnrestrictedSessionRunner(session) {
				@Override
				public void run() throws ClientException {

					DocumentModel dossierDirecteurDoc = null;
					DocumentModel routeDirectriceDoc = null;
					try {
						dossierDirecteurDoc = session.getDocument(questionDirectrice.getDossierRef());
						Dossier dossierDirecteur = dossierDirecteurDoc.getAdapter(Dossier.class);

						routeDirectriceDoc = session.getDocument(new IdRef(dossierDirecteur.getLastDocumentRoute()));
						final DocumentRoute routeDirectrice = routeDirectriceDoc.getAdapter(DocumentRoute.class);

						// Allotissement possible si le poste de l'etape courante est relié au ministere attributaire
						final List<DocumentModel> listDossierLinkDirecteurDoc = corbeilleService.findDossierLink(
								session, dossierDirecteurDoc.getId());

						// force unlock
						stLockService.unlockDoc(session, routeDirectriceDoc);
						stLockService.unlockDoc(session, dossierDirecteurDoc);

						stLockService.lockDoc(session, routeDirectriceDoc);
						stLockService.lockDoc(session, dossierDirecteurDoc);

						// Nouvelle feuille de route pour la question de rappel, celle du lot
						final DocumentModel dossierRappelDoc = session.getDocument(questionRappel.getDossierRef());
						final Dossier dossierRappel = dossierRappelDoc.getAdapter(Dossier.class);
						dossierRappel.setLastDocumentRoute(routeDirectriceDoc.getId());
						session.saveDocument(dossierRappel.getDocument());

						// On duplique les droits du dossier directeur dans le dossier
						final ACP dossierDirecteurAcp = dossierDirecteur.getDocument().getACP();
						session.setACP(dossierRappel.getDocument().getRef(), dossierDirecteurAcp, true);
						session.save();

						for (final DocumentModel dossierLinkDirecteurDoc : listDossierLinkDirecteurDoc) {
							final DossierLink dossierLinkDirecteur = dossierLinkDirecteurDoc
									.getAdapter(DossierLink.class);

							// On duplique le caseLinkDirecteur et on le rattache au dossier courant
							DocumentModel dossierLinkDirecteurCopyDoc = session.copy(dossierLinkDirecteur.getDocument()
									.getRef(), dossierLinkDirecteur.getDocument().getParentRef(),
									dossierLinkDirecteurDoc.getName());
							dossierLinkDirecteurCopyDoc.setProperty("case_link", "caseDocumentId",
									dossierRappelDoc.getId());
							final Calendar now = Calendar.getInstance();
							dossierLinkDirecteurCopyDoc.setProperty("case_link", "date", now);
							DublincoreSchemaUtils.setModifiedDate(dossierLinkDirecteurCopyDoc, now);
							DublincoreSchemaUtils.setTitle(dossierLinkDirecteurCopyDoc,
									"Dossier n°" + dossierRappel.getNumeroQuestion());
							dossierLinkDirecteurCopyDoc = session.saveDocument(dossierLinkDirecteurCopyDoc);

							// Mise à jour des champs du dossierLink
							dossierDistributionService.setDossierLinksFields(session, dossierLinkDirecteurCopyDoc);

							// on duplique les droits du dossierLink directeur dans le dossierLink copié
							final ACP dossierLinkDirecteurAcp = dossierLinkDirecteur.getDocument().getACP();
							session.setACP(dossierLinkDirecteurCopyDoc.getRef(), dossierLinkDirecteurAcp, true);
							session.saveDocument(dossierLinkDirecteurCopyDoc);

						}

						// Gestion allotissement
						Allotissement allotissement = getAllotissement(dossierDirecteur.getDossierLot(), session);
						// On ajoute le dossier sur la feuille de route du dossier directeur
						final List<String> listDossierIds = new ArrayList<String>(
								routeDirectrice.getAttachedDocuments());
						if (listDossierIds != null && !listDossierIds.contains(dossierRappelDoc.getId())) {
							listDossierIds.add(dossierRappelDoc.getId());
						}
						// Si l'allotissement est null, on en créé un nouveau
						if (allotissement == null) {
							// creation de l'allotssement
							allotissement = createAllotissementDoc(session, listDossierIds);
							dossierDirecteur.setDossierLot(allotissement.getNom());
							dossierDirecteurDoc = session.saveDocument(dossierDirecteurDoc);
							dossierDirecteur = dossierDirecteurDoc.getAdapter(Dossier.class);
						} else {
							// Sinon, on met à jour l'existant avec le dossier de rappel
							dossierRappel.setDossierLot(allotissement.getNom());
							session.saveDocument(dossierRappel.getDocument());
							allotissement.setIdDossiers(listDossierIds);
							session.saveDocument(allotissement.getDocument());
							session.save();
						}

						final StringBuilder sb = prepareIdsDossierForLog(session, allotissement.getIdDossiers());
						journalService.journaliserActionParapheur(session, dossierRappel.getDocument(),
								ReponsesEventConstant.DOSSIER_REPONSE_ALLOTISSEMENT,
								"Dossier ajouté au lot" + sb.toString());

						session.save();

						// On link tous les dossiers à la feuille de route directrice
						routeDirectrice.setAttachedDocuments(listDossierIds);
						session.saveDocument(routeDirectriceDoc);

						reponseService.saveReponse(session, dossierDirecteur.getReponse(session).getDocument(),
								dossierDirecteur.getDocument());

						session.save();

					} catch (final Exception e) {
						LOGGER.error(session, ReponsesLogEnumImpl.FAIL_CREATE_ALLOT_TEC, e);
						sendErrorMailLotToAdmin(session, questionDirectrice, Collections.singletonList(questionRappel),
								e);
					} finally {
						if (routeDirectriceDoc != null) {
							stLockService.unlockDoc(session, routeDirectriceDoc);
						}
						if (dossierDirecteurDoc != null) {
							stLockService.unlockDoc(session, dossierDirecteurDoc);
						}
					}
				}
			}.runUnrestricted();
		} catch (final ClientException e) {
			LOGGER.error(session, ReponsesLogEnumImpl.FAIL_CREATE_ALLOT_TEC, e);
		}
	}

	@Override
	public Set<String> extractDossiersDirecteurs(List<DocumentModel> listDossiersDoc, CoreSession session)
			throws ClientException {
		Set<String> lstDossiersDirecteurs = new TreeSet<String>();
		Set<String> nomAllotissement = new TreeSet<String>();
		List<DocumentModel> lstAllotissementModel = null;

		for (DocumentModel dossierDoc : listDossiersDoc) {
			Dossier doss = dossierDoc.getAdapter(Dossier.class);
			if (doss != null && StringUtils.isNotBlank(doss.getDossierLot())) {
				nomAllotissement.add(doss.getDossierLot());
			}
		}

		// Si on a bien des dossiers allotis alors on va récupérer le premier dossier de chaque lot qui est donc le
		// directeur
		// Pas besoin de faire quoi que ce soit si on n'a pas de dossier allotis
		if (!nomAllotissement.isEmpty()) {
			StringBuilder query = new StringBuilder("SELECT a.ecm:uuid as id FROM ");
			query.append(DossierConstants.ALLOTISSEMENT_DOCUMENT_TYPE);
			query.append(String.format(" as a WHERE a.allot:nom IN (%s) ", StringUtil.join(nomAllotissement, ",", "'")));
			Object[] params = new Object[] {};
			lstAllotissementModel = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session,
					DossierConstants.ALLOTISSEMENT_DOCUMENT_TYPE, query.toString(), params);

			for (DocumentModel allotDoc : lstAllotissementModel) {
				Allotissement allot = allotDoc.getAdapter(Allotissement.class);

				// On vérifie qd même qu'on a bien des dossiers
				if (allot.getIdDossiers() != null && !allot.getIdDossiers().isEmpty()) {
					lstDossiersDirecteurs.add(allot.getIdDossiers().get(0));
				}
			}
		}

		return lstDossiersDirecteurs;

	}
}
