package fr.dila.reponses.core.service;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;

import fr.dila.ecm.platform.routing.api.DocumentRouteElement;
import fr.dila.ecm.platform.routing.api.DocumentRouteStep;
import fr.dila.reponses.api.Exception.ReponsesException;
import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Allotissement;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.feuilleroute.ReponsesRouteStep;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.api.service.ReponsesArbitrageService;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.feuilleroute.STRouteStep;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.security.principal.STPrincipal;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringUtil;
import fr.dila.st.core.util.UnrestrictedGetDocumentRunner;

/**
 * Implémentation du service lié à l'arbitrage réponses
 * 
 * @author user
 * 
 */
public class ReponsesArbitrageServiceImpl implements ReponsesArbitrageService {

	private static final STLogger	LOGGER					= STLogFactory.getLog(ReponsesArbitrageServiceImpl.class);

	private static final String		ERROR_STEP_NOT_FOUND	= "Etape non trouvée";

	/**
	 * Default constructor
	 */
	public ReponsesArbitrageServiceImpl() {

	}

	@Override
	public DocumentRouteStep createStepArbitrageSGG(final CoreSession session) throws ClientException {
		final DocumentModel newStepModel = session.createDocumentModel(STConstant.ROUTE_STEP_DOCUMENT_TYPE);
		final ReponsesRouteStep newStep = newStepModel.getAdapter(ReponsesRouteStep.class);
		newStep.setType(VocabularyConstants.ROUTING_TASK_TYPE_ARBITRAGE);

		final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
		final String mailboxSggId = mailboxPosteService.getMailboxSggId();
		mailboxPosteService.getOrCreateMailboxPoste(session, mailboxPosteService.getPosteIdFromMailboxId(mailboxSggId));
		newStep.setDistributionMailboxId(mailboxSggId);

		return newStep;
	}

	@Override
	public boolean isStepPourArbitrage(STDossierLink dossierLink) throws ClientException {
		return VocabularyConstants.ROUTING_TASK_TYPE_ARBITRAGE.equals(dossierLink.getRoutingTaskType());
	}

	@Override
	public void addStepArbitrageSGG(final CoreSession session, final DocumentModel dossierDoc,
			final DocumentModel dossierLinkDoc) throws ClientException {

		if (canAddStepArbitrageSGG(dossierDoc)) {
			final DossierLink dossierLink = dossierLinkDoc.getAdapter(DossierLink.class);
			final DocumentModel etapeDoc = new UnrestrictedGetDocumentRunner(session).getById(dossierLink
					.getRoutingTaskId());
			final STRouteStep etape = etapeDoc.getAdapter(STRouteStep.class);

			final String docId = (String) etapeDoc.getParentRef().reference();
			final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
			final DocumentModelList steps = documentRoutingService.getOrderedRouteElement(docId, session);
			final int currentStepIndex = steps.indexOf(etapeDoc);

			// Ajout de l'étape arbitrage
			final DocumentRouteStep newCurrentStep = createStepArbitrageSGG(session);
			documentRoutingService.addRouteElementToRoute(etapeDoc.getParentRef(), currentStepIndex + 1,
					newCurrentStep, session);

			session.save();
			// Journalise l'action
			STServiceLocator.getJournalService().journaliserActionEtapeFDR(session, etape, dossierDoc,
					ReponsesEventConstant.DOSSIER_ARBITRAGE_SGG_EVENT,
					ReponsesEventConstant.COMMENT_DOSSIER_ARBITRAGE_SGG);
		} else {
			throw new ReponsesException(ReponsesLogEnumImpl.FAIL_ADD_STEP_ARBITRAGE_TEC.getText());
		}
	}

	@Override
	public boolean canAddStepArbitrageSGG(final DocumentModel dossierDoc) {
		Dossier dossier = dossierDoc.getAdapter(Dossier.class);
		if (dossier != null) {
			return !dossier.isArbitrated();
		}
		return false;
	}

	@Override
	public void updateDossierAfterArbitrage(CoreSession session, DocumentModel dossierDoc) throws ClientException {
		Dossier dossier = dossierDoc.getAdapter(Dossier.class);
		if (dossier == null) {
			throw new ReponsesException(STLogEnumImpl.FAIL_GET_DOSSIER_TEC.getText());
		}

		final AllotissementService allotServ = ReponsesServiceLocator.getAllotissementService();
		if (allotServ.isAllotit(dossier, session)) {
			Allotissement lot = allotServ.getAllotissement(dossier.getDossierLot(), session);
			for (String idDossier : lot.getIdDossiers()) {
				IdRef dossierRef = new IdRef(idDossier);
				if (session.exists(dossierRef)) {
					DocumentModel dossierDocLot = session.getDocument(dossierRef);
					setDossierArbitrated(session, dossierDocLot);
				}
			}
			// Ensuite on repasse le dossier dossierDoc au cas où l'arbitrage n'aurait pas été effectif dans la boucle
			// précédente - Cf M156324
			setDossierArbitrated(session, dossierDoc);
		} else {
			setDossierArbitrated(session, dossierDoc);
		}
	}

	@Override
	public boolean canUseReattributionDirecte(CoreSession session, STPrincipal principal) {
		if (principal.isMemberOf(ReponsesBaseFunctionConstant.DROIT_REATTRIBUTION_DIRECTE)) {
			return true;
		}
		return false;
	}

	@Override
	public void attributionAfterArbitrage(final CoreSession session, final DossierLink dossierLink,
			final DocumentModel dossierDoc, final String idMinistere, final String arbitrageObservations)
			throws ReponsesException {
		nonConcerneEtReattribution(session, dossierDoc, dossierLink, idMinistere, arbitrageObservations);
	}

	@Override
	public void reattributionDirecte(final CoreSession session, final DossierLink dossierLink,
			final DocumentModel dossierDoc, final String idMinistere, final String observations)
			throws ReponsesException {
		nonConcerneEtReattribution(session, dossierDoc, dossierLink, idMinistere, observations);
	}

	/**
	 * Met à jour le dossier et reattribue le dossier
	 * 
	 * @param session
	 * @param dossierDoc
	 * @param dossierLink
	 * @param idMinistere
	 * @param observations
	 * @throws ReponsesException
	 */
	private void nonConcerneEtReattribution(final CoreSession session, final DocumentModel dossierDoc,
			final DossierLink dossierLink, final String idMinistere, final String observations)
			throws ReponsesException {
		final DossierDistributionService dossierDistributionService = ReponsesServiceLocator
				.getDossierDistributionService();
		try {
			updateDossierAfterArbitrage(session, dossierDoc);
			// Récupération du dossier
			final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
			DocumentModel lastFeuilleRouteDoc = dossierDistributionService.reattribuerDossier(session, dossier,
					dossierLink, idMinistere, STSchemaConstant.ROUTING_TASK_VALIDATION_STATUS_NON_CONCERNE_VALUE);
			addNoteToStep(session, lastFeuilleRouteDoc.getId(), observations);
		} catch (final ClientException exc) {
			LOGGER.error(session, SSLogEnumImpl.FAIL_REATTR_DOSSIER_TEC, exc);
			throw new ReponsesException(exc);
		}
	}

	/**
	 * Ajoute les observations en note d'étape
	 * 
	 * @param session
	 * @param feuilleRouteId
	 * @param observations
	 * @throws ClientException
	 */
	private void addNoteToStep(CoreSession session, String feuilleRouteId, String observations) throws ClientException {
		// Ajout de commentaire à l'étape précédente celle en cours
		if (!StringUtil.isEmpty(observations)) {
			final DocumentModelList listStepsDoc = SSServiceLocator.getDocumentRoutingService().getOrderedRouteElement(
					feuilleRouteId, session);
			List<DocumentModel> listCurrentStepsDoc = new ArrayList<DocumentModel>();
			for (final DocumentModel stepDoc : listStepsDoc) {
				if (DocumentRouteElement.ElementLifeCycleState.running.name()
						.equals(stepDoc.getCurrentLifeCycleState())) {
					listCurrentStepsDoc.add(stepDoc);
				}
			}
			if (listCurrentStepsDoc.isEmpty()) {
				LOGGER.warn(session, SSLogEnumImpl.FAIL_ADD_NOTE_TEC, ERROR_STEP_NOT_FOUND);
			} else {
				DocumentModel routeStepDoc = ReponsesServiceLocator.getFeuilleRouteService().findPreviousStepInFolder(
						session, listCurrentStepsDoc.get(0), null, false);
				if (routeStepDoc == null) {
					LOGGER.warn(session, SSLogEnumImpl.FAIL_ADD_NOTE_TEC, ERROR_STEP_NOT_FOUND);
				} else {
					// Ajout de commentaire à l'étape
					ReponsesServiceLocator.getDossierDistributionService().addCommentToStep(session, routeStepDoc,
							observations);
				}
			}
		}
	}

	/**
	 * Marque un dossier comme étant arbitré
	 * 
	 * @param session
	 * @param dossierDoc
	 * @throws ClientException
	 */
	private void setDossierArbitrated(final CoreSession session, DocumentModel dossierDoc) throws ClientException {
		Dossier dossier = dossierDoc.getAdapter(Dossier.class);
		if (dossier == null) {
			throw new ReponsesException(STLogEnumImpl.FAIL_GET_DOSSIER_TEC.getText());
		}
		dossier.setIsArbitrated(true);
		dossier.save(session);
		session.saveDocument(dossierDoc);
		session.save();
	}
}
