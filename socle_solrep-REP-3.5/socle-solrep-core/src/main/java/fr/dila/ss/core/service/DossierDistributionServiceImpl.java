package fr.dila.ss.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.DefaultComponent;

import fr.dila.cm.caselink.ActionableCaseLink;
import fr.dila.ecm.platform.routing.api.DocumentRoute;
import fr.dila.ecm.platform.routing.api.DocumentRouteElement;
import fr.dila.ss.api.exception.SSException;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.api.service.DossierDistributionService;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.constant.STLifeCycleConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.api.feuilleroute.STFeuilleRoute;
import fr.dila.st.api.feuilleroute.STRouteStep;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Implémentation du service de distribution des dossiers du socle transverse.
 * 
 * @author jtremeaux
 */
public abstract class DossierDistributionServiceImpl extends DefaultComponent implements DossierDistributionService {

	/**
	 * Serial version UID
	 */
	private static final long		serialVersionUID	= -7744835777616091844L;

	private static final STLogger	LOGGER				= STLogFactory.getLog(DossierDistributionServiceImpl.class);

	protected static final String	WARN_DL_VALIDATION	= "[REPARATION CL] - Case Link présent dans un état autre que 'todo' ; validation esquivée pour éviter 'Unable to follow transition' - caseLinkId : ";

	/**
	 * Default constructor
	 */
	public DossierDistributionServiceImpl() {
		super();
	}

	@Override
	public DocumentModel getLastDocumentRouteForDossier(final CoreSession session, final DocumentModel dossierDoc)
			throws ClientException {
		final STDossier dossier = dossierDoc.getAdapter(STDossier.class);
		final String routeInstanceDocId = dossier.getLastDocumentRoute();

		// Charge l'instance de feuille de route
		return session.getDocument(new IdRef(routeInstanceDocId));
	}

	@Override
	public List<DocumentModel> getDossierRoutes(final CoreSession session, final DocumentModel dossierDoc)
			throws ClientException {
		final StringBuilder sb = new StringBuilder("SELECT * FROM DocumentRoute WHERE docri:participatingDocuments = '");
		sb.append(dossierDoc.getId());
		sb.append("' and ecm:isProxy = 0 ");
		sb.append(" ORDER BY dc:modified ASC ");
		return session.query(sb.toString());
	}

	@Override
	public DocumentModel substituerFeuilleRoute(final CoreSession session, final DocumentModel dossierDoc,
			final DocumentModel oldRouteInstanceDoc, final DocumentModel newRouteModelDoc, final String typeCreation)
			throws ClientException {
		final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();

		// Annule l'ancienne feuille de route
		final List<String> docIds = new ArrayList<String>();
		if (oldRouteInstanceDoc == null) {
			docIds.add(dossierDoc.getId());
		} else {
			final DocumentRoute oldRouteInstance = oldRouteInstanceDoc.getAdapter(DocumentRoute.class);
			docIds.addAll(oldRouteInstance.getAttachedDocuments());
		}
		// Crée la nouvelle instance de feuille de route (l'instance n'est pas encore démarrée)
		final DocumentModel newRouteInstanceDoc = documentRoutingService.createNewInstance(session, newRouteModelDoc,
				docIds);

		deleteForbiddenSteps(session, dossierDoc, newRouteInstanceDoc);

		if (oldRouteInstanceDoc != null) {
			final STFeuilleRoute newFeuilleRoute = newRouteInstanceDoc.getAdapter(STFeuilleRoute.class);
			final DocumentModel firstStepDoc = session.getChildren(newRouteInstanceDoc.getRef()).get(0);
			documentRoutingService.lockDocumentRoute(newFeuilleRoute, session);

			// On récupère les étapes de l'ancienne fdr
			final List<DocumentModel> stepsOldFdr = documentRoutingService.getOrderedRouteElement(
					oldRouteInstanceDoc.getId(), session);

			for (final DocumentModel etapeDoc : stepsOldFdr) {
				if (DocumentRouteElement.ElementLifeCycleState.done.name().equals(etapeDoc.getCurrentLifeCycleState())) {
					final DocumentModel newStepDoc = session.copy(etapeDoc.getRef(), newRouteInstanceDoc.getRef(),
							etapeDoc.getName());
					session.orderBefore(newRouteInstanceDoc.getRef(), newStepDoc.getName(), firstStepDoc.getName());
				}
			}

			final List<DocumentModel> resultListDL = STServiceLocator.getCorbeilleService()
					.findDossierLinkUnrestricted(session, dossierDoc.getId());

			for (final DocumentModel dossierLinkDoc : resultListDL) {
				session.followTransition(dossierLinkDoc.getRef(), STLifeCycleConstant.TO_DONE_TRANSITION);
			}

			documentRoutingService.unlockDocumentRoute(newFeuilleRoute, session);

			final DocumentRoute oldRouteInstance = oldRouteInstanceDoc.getAdapter(DocumentRoute.class);
			oldRouteInstance.cancel(session);

		}

		// Lance l'évenement de substitution
		fireSubstitutionFeuilleDeRouteEvent(session, typeCreation, oldRouteInstanceDoc, newRouteInstanceDoc);
		if (oldRouteInstanceDoc != null) {
			session.followTransition(oldRouteInstanceDoc.getRef(), STLifeCycleConstant.TO_DELETE_TRANSITION);
		}

		return newRouteInstanceDoc;
	}

	/**
	 * Supprime les étapes interdites dans une instance de feuille de route Méthode surchargée sur EPG pour les textes
	 * non publiés au JO
	 * 
	 * @param session
	 * @param dossierDoc
	 * @param newRouteInstanceDoc
	 */
	protected void deleteForbiddenSteps(final CoreSession session, final DocumentModel dossierDoc,
			final DocumentModel routeInstanceDoc) throws ClientException {
		return;
	}

	/**
	 * 
	 * Lève un événement de substitution de feuille de route (démarre la feuille de route). Cet evenement est appelé en
	 * cas de réattribution de feuille de route, mais également dans le cadre d'une réattribution.
	 * 
	 * @param session
	 * @param oldRouteInstanceDoc
	 * @param typeCreation
	 *            : FEUILLE_ROUTE_TYPE_CREATION_REATTRIBUTION pour une réattribution,
	 *            FEUILLE_ROUTE_TYPE_CREATION_SUBSTITUTION pour une substitution
	 * @param newRouteInstanceDoc
	 * @throws ClientException
	 */
	protected void fireSubstitutionFeuilleDeRouteEvent(final CoreSession session, final String typeCreation,
			final DocumentModel oldRouteInstanceDoc, final DocumentModel newRouteInstanceDoc) throws ClientException {
		final EventProducer eventProducer = STServiceLocator.getEventProducer();
		final Map<String, Serializable> eventProperties = new HashMap<String, Serializable>();
		eventProperties.put(STEventConstant.DOSSIER_DISTRIBUTION_SUBSTITUTION_ROUTE_TYPE_EVENT_PARAM, typeCreation);
		eventProperties.put(STEventConstant.DOSSIER_DISTRIBUTION_OLD_ROUTE_EVENT_PARAM, oldRouteInstanceDoc);
		eventProperties.put(STEventConstant.DOSSIER_DISTRIBUTION_NEW_ROUTE_EVENT_PARAM, newRouteInstanceDoc);
		final InlineEventContext inlineEventContext = new InlineEventContext(session, session.getPrincipal(),
				eventProperties);
		eventProducer.fireEvent(inlineEventContext.newEvent(STEventConstant.AFTER_SUBSTITUTION_FEUILLE_ROUTE));
	}

	@Override
	public void validerEtape(final CoreSession session, final DocumentModel dossierDoc,
			final DocumentModel dossierLinkDoc) throws ClientException {
		// Met à jour l'étape en cours avec l'avis favorable
		final STDossierLink dossierLink = dossierLinkDoc.getAdapter(STDossierLink.class);
		final DocumentModel etapeDoc = session.getDocument(new IdRef(dossierLink.getRoutingTaskId()));
		validerEtape(session, dossierDoc, dossierLinkDoc, etapeDoc);
	}
	
	/**
	 * Permet de valider l'étape passée en paramètre (avis favorable). Le dossier link est validé et une entrée est faite dans le journal technique 
	 * @param session
	 * @param dossierDoc
	 * @param dossierLinkDoc
	 * @param stepDoc
	 * @throws ClientException
	 */
	protected void validerEtape(final CoreSession session, final DocumentModel dossierDoc, final DocumentModel dossierLinkDoc, final DocumentModel stepDoc) throws ClientException {
		final STRouteStep etape = stepDoc.getAdapter(STRouteStep.class);
		LOGGER.info(session, SSLogEnumImpl.UPDATE_STEP_TEC, "Avis favorable pour l'étape <" + stepDoc.getId() + ">");

		updateStepValidationStatus(session, stepDoc,
				STSchemaConstant.ROUTING_TASK_VALIDATION_STATUS_AVIS_FAVORABLE_VALUE, dossierDoc);

		// Valide le DossierLink
		final ActionableCaseLink acl = dossierLinkDoc.getAdapter(ActionableCaseLink.class);
		if (acl.isActionnable() && acl.isTodo()) {
			acl.validate(session);
		} else {
			LOGGER.debug(session, STLogEnumImpl.FAIL_VALIDATE_DL_TEC, dossierDoc, WARN_DL_VALIDATION + acl.getId());
			LOGGER.warn(session, STLogEnumImpl.FAIL_VALIDATE_DL_TEC, WARN_DL_VALIDATION + acl.getId());
		}

		final JournalService journalService = STServiceLocator.getJournalService();
		// Journalise l'action
		journalService.journaliserActionEtapeFDR(session, etape, dossierDoc, STEventConstant.DOSSIER_AVIS_FAVORABLE,
				STEventConstant.COMMENT_AVIS_FAVORABLE);
	}

	@Override
	public void validerEtapeRefus(final CoreSession session, final DocumentModel dossierDoc,
			final DocumentModel dossierLinkDoc) throws ClientException {
		final JournalService journalService = STServiceLocator.getJournalService();

		LOGGER.info(session, SSLogEnumImpl.UPDATE_STEP_TEC,
				"Avis défavorable pour l'étape <" + dossierLinkDoc.getName() + ">");

		// Met à jour l'étape en cours avec l'avis défavorable
		final STDossierLink dossierLink = dossierLinkDoc.getAdapter(STDossierLink.class);
		final DocumentModel etapeDoc = session.getDocument(new IdRef(dossierLink.getRoutingTaskId()));
		final STRouteStep etape = etapeDoc.getAdapter(STRouteStep.class);
		updateStepValidationStatus(session, etapeDoc,
				STSchemaConstant.ROUTING_TASK_VALIDATION_STATUS_AVIS_DEFAVORABLE_VALUE, dossierDoc);

		// Valide le DossierLink
		final ActionableCaseLink acl = dossierLinkDoc.getAdapter(ActionableCaseLink.class);
		acl.refuse(session);

		// Journalise l'action
		journalService.journaliserActionEtapeFDR(session, etape, dossierDoc, STEventConstant.DOSSIER_AVIS_DEFAVORABLE,
				STEventConstant.COMMENT_AVIS_DEFAVORABLE);
	}

	@Override
	public void rejeterDossierLink(final CoreSession session, final DocumentModel dossierDoc,
			final DocumentModel dossierLinkDoc) throws ClientException {
		final JournalService journalService = STServiceLocator.getJournalService();

		LOGGER.info(session, SSLogEnumImpl.UPDATE_STEP_TEC, "Refus du DossierLink <" + dossierLinkDoc.getName() + ">");

		// Met à jour l'étape en cours avec l'avis défavorable
		final STDossierLink dossierLink = dossierLinkDoc.getAdapter(STDossierLink.class);
		final DocumentModel etapeDoc = session.getDocument(new IdRef(dossierLink.getRoutingTaskId()));
		final STRouteStep etape = etapeDoc.getAdapter(STRouteStep.class);
		updateStepValidationStatus(session, etapeDoc,
				STSchemaConstant.ROUTING_TASK_VALIDATION_STATUS_AVIS_DEFAVORABLE_VALUE, dossierDoc);

		// Refuse le DossierLink
		final ActionableCaseLink acl = dossierLinkDoc.getAdapter(ActionableCaseLink.class);
		acl.refuse(session);

		// Journalise l'action
		journalService.journaliserActionEtapeFDR(session, etape, dossierDoc, STEventConstant.DOSSIER_AVIS_DEFAVORABLE,
				STEventConstant.COMMENT_AVIS_DEFAVORABLE);
	}

	@Override
	public void validerEtapeNonConcerne(final CoreSession session, final DocumentModel dossierDoc,
			final DocumentModel dossierLinkDoc) throws ClientException {
		final JournalService journalService = STServiceLocator.getJournalService();

		LOGGER.info(session, SSLogEnumImpl.UPDATE_STEP_TEC, "Non concerné pour l'étape <" + dossierLinkDoc.getName()
				+ ">");

		// Met à jour l'étape en cours avec l'avis non concerné
		final STDossierLink dossierLink = dossierLinkDoc.getAdapter(STDossierLink.class);
		final DocumentModel etapeDoc = session.getDocument(new IdRef(dossierLink.getRoutingTaskId()));
		final STRouteStep etape = etapeDoc.getAdapter(STRouteStep.class);
		updateStepValidationStatus(session, etapeDoc,
				STSchemaConstant.ROUTING_TASK_VALIDATION_STATUS_NON_CONCERNE_VALUE, dossierDoc);

		// Valide le DossierLink
		final ActionableCaseLink acl = dossierLinkDoc.getAdapter(ActionableCaseLink.class);
		if (acl.isActionnable() && acl.isTodo()) {
			acl.validate(session);
		} else {
			LOGGER.debug(session, STLogEnumImpl.FAIL_VALIDATE_DL_TEC, dossierDoc, WARN_DL_VALIDATION + acl.getId());
			LOGGER.warn(session, STLogEnumImpl.FAIL_VALIDATE_DL_TEC, WARN_DL_VALIDATION + acl.getId());
		}

		// Journalise l'action
		journalService.journaliserActionEtapeFDR(session, etape, dossierDoc,
				STEventConstant.DOSSIER_VALIDER_NON_CONCERNE_EVENT,
				STEventConstant.DOSSIER_VALIDER_NON_CONCERNE_COMMENT_PARAM);
	}

	/**
	 * Met l'étape courante à l'état passé en param
	 * 
	 * @param dossierDoc
	 *            le dossier courant
	 * 
	 * @param dossierLinkDoc
	 * @throws ClientException
	 */
	@Override
	public void updateStepValidationStatus(final CoreSession session, final DocumentModel etapeDoc,
			final String validationStatus, final DocumentModel dossierDoc) throws ClientException {
		// Cas de la validation automatique d'étape lors du changement d'état de question par web services. On tombe
		// parfois sur des conteneurs parallèles,
		// il faut donc les visiter pour changer les statuts des étapes qu'ils contiennent
		if (etapeDoc.getType().equals(STConstant.ROUTE_STEP_DOCUMENT_TYPE)) {
			final STRouteStep etape = etapeDoc.getAdapter(STRouteStep.class);
			etape.setAutomaticValidated(false);
			etape.setValidationStatus(validationStatus);
			session.saveDocument(etape.getDocument());
		} else if (etapeDoc.getType().equals(STConstant.STEP_FOLDER_DOCUMENT_TYPE)) {
			final DocumentModelList dml = session.getChildren(etapeDoc.getRef());
			for (final DocumentModel dm : dml) {
				updateStepValidationStatus(session, dm, validationStatus, dossierDoc);
			}
		}
	}

	@Override
	public void restartDossier(final CoreSession session, final DocumentModel dossierDoc) throws ClientException {
		LOGGER.info(session, STLogEnumImpl.UPDATE_DOSSIER_TEC, "Redémarrage du dossier <" + dossierDoc.getName() + ">");

		// Vérifie que le dossier est à l'état terminé
		if (!dossierDoc.getCurrentLifeCycleState().equals(STDossier.DossierState.done.name())) {
			throw new SSException("Le dossier doit être à l'état done");
		}

		// Vérifie que la feuille de route est à l'état terminé
		final DocumentModel routeInstanceDoc = getLastDocumentRouteForDossier(session, dossierDoc);
		if (!routeInstanceDoc.getCurrentLifeCycleState().equals(STDossier.DossierState.done.name())) {
			throw new SSException("La feuille de route doit être à l'état done");
		}

		new UnrestrictedSessionRunner(session) {
			@Override
			public void run() throws ClientException {
				dossierDoc.followTransition(STDossier.DossierTransition.backToRunning.name());

				// Redémarre la feuille de route
				final STFeuilleRoute feuilleRouteInstance = routeInstanceDoc.getAdapter(STFeuilleRoute.class);
				feuilleRouteInstance.backToReady(session);
				// TODO à vérifier session.save();

				Framework.getLocalService(EventService.class).waitForAsyncCompletion();
				feuilleRouteInstance.run(session);
			}
		}.runUnrestricted();
	}

	/**
	 * valider une etape pour la reprise
	 * 
	 * @param session
	 *            session
	 * @param dossierLinkDoc
	 * @throws ClientException
	 */
	@Override
	public void validerEtapePourReprise(final CoreSession session, final DocumentModel dossierLinkDoc)
			throws ClientException {
		// Met à jour l'étape en cours avec l'avis favorable
		final STDossierLink dossierLink = dossierLinkDoc.getAdapter(STDossierLink.class);
		final DocumentModel dossierDoc = dossierLink.getCase(session).getDocument();
		final DocumentModel etapeDoc = session.getDocument(new IdRef(dossierLink.getRoutingTaskId()));
		updateStepValidationStatus(session, etapeDoc,
				STSchemaConstant.ROUTING_TASK_VALIDATION_STATUS_AVIS_FAVORABLE_VALUE, dossierDoc);

		// Valide le DossierLink
		final ActionableCaseLink acl = dossierLinkDoc.getAdapter(ActionableCaseLink.class);
		if (acl.isActionnable() && acl.isTodo()) {
			acl.validate(session);
		} else {
			LOGGER.debug(session, STLogEnumImpl.FAIL_VALIDATE_DL_TEC, dossierDoc, WARN_DL_VALIDATION + acl.getId());
			LOGGER.warn(session, STLogEnumImpl.FAIL_VALIDATE_DL_TEC, WARN_DL_VALIDATION + acl.getId());
		}
	}
}
