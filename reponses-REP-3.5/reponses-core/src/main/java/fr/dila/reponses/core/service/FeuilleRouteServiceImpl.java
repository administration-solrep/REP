package fr.dila.reponses.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.nuxeo.common.utils.i18n.I18NUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

import fr.dila.cm.caselink.ActionableCaseLink;
import fr.dila.cm.caselink.CaseLink;
import fr.dila.ecm.platform.routing.api.DocumentRouteStep;
import fr.dila.ecm.platform.routing.api.DocumentRoutingConstants;
import fr.dila.reponses.api.Exception.CopieStepException;
import fr.dila.reponses.api.Exception.ReponsesException;
import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ProfilUtilisateurConstants;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.ReponsesParametreConstant;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.feuilleroute.ReponsesFeuilleRoute;
import fr.dila.reponses.api.feuilleroute.ReponsesRouteStep;
import fr.dila.reponses.api.service.CorbeilleService;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.api.service.FeuilleRouteService;
import fr.dila.reponses.api.service.ProfilUtilisateurService;
import fr.dila.ss.api.domain.feuilleroute.StepFolder;
import fr.dila.ss.api.exception.SSException;
import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STDossierLinkConstant;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.api.feuilleroute.DocumentRouteTableElement;
import fr.dila.st.api.feuilleroute.STFeuilleRoute;
import fr.dila.st.api.feuilleroute.STRouteStep;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.api.service.STLockService;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.api.service.organigramme.STUsAndDirectionService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.exception.PosteNotFoundException;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.StringUtil;
import fr.dila.st.core.util.UnrestrictedGetDocumentRunner;

/**
 * Implémentation du service permettant d'effectuer des actions spécifiques sur les instances de feuille de route dans
 * l'application Réponses.
 * 
 * @author jtremeaux
 */
public class FeuilleRouteServiceImpl extends fr.dila.ss.core.service.FeuilleRouteServiceImpl implements
		FeuilleRouteService {
	/**
	 * UID.
	 */
	private static final long	serialVersionUID	= -2392698015083550568L;

	/**
	 * Logger.
	 */
	private static final Log	LOGGER					= LogFactory.getLog(FeuilleRouteServiceImpl.class);
	private static final STLogger	STLOGGER			= STLogFactory.getLog(FeuilleRouteServiceImpl.class);

	@Override
	public DocumentModel getValidationPMStep(final CoreSession session, final String feuilleRouteInstanceId)
			throws ClientException {
		final DocumentRoutingService service = SSServiceLocator.getDocumentRoutingService();

		// Récupération de toutes les étapes de feuille de route
		final DocumentModel feuilleRouteDoc = session.getDocument(new IdRef(feuilleRouteInstanceId));
		final STFeuilleRoute feuilleRoute = feuilleRouteDoc.getAdapter(STFeuilleRoute.class);
		final List<DocumentRouteTableElement> listRouteTableElement = service.getFeuilleRouteElements(feuilleRoute,
				session);

		// Recherche de l'étape Validation premier ministre
		for (final DocumentRouteTableElement routeTableElement : listRouteTableElement) {
			if (!routeTableElement.getDocument().isFolder()) {
				final STRouteStep step = routeTableElement.getRouteStep();
				if (VocabularyConstants.ROUTING_TASK_TYPE_VALIDATION_PM.equals(step.getType())) {
					return step.getDocument();
				}
			}
		}

		return null;
	}

	@Override
	public void addStepAttente(final CoreSession session, final DocumentModel etapeDoc) throws ClientException {
		final String mailboxId = etapeDoc.getAdapter(STRouteStep.class).getDistributionMailboxId();
		final String docId = (String) etapeDoc.getParentRef().reference();
		final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
		final DocumentModelList steps = documentRoutingService.getOrderedRouteElement(docId, session);
		final int currentStepIndex = steps.indexOf(etapeDoc);

		// Ajout de l'étape pour attente
		final DocumentRouteStep newCurrentStep = createStepAttente(session, mailboxId);
		documentRoutingService.addRouteElementToRoute(etapeDoc.getParentRef(), currentStepIndex + 1, newCurrentStep,
				session);
	}

	/**
	 * Création d'une étape Pour attente.
	 * 
	 * @param session
	 *            Session
	 * @return Nouvelle étape Pour attente
	 * @throws ClientException
	 */
	protected DocumentRouteStep createStepAttente(final CoreSession session, final String mailboxId)
			throws ClientException {
		final DocumentModel newStepModel = session.createDocumentModel(STConstant.ROUTE_STEP_DOCUMENT_TYPE);
		final ReponsesRouteStep newStep = newStepModel.getAdapter(ReponsesRouteStep.class);
		newStep.setType(VocabularyConstants.ROUTING_TASK_TYPE_ATTENTE);

		newStep.setDistributionMailboxId(mailboxId);

		return newStep;
	}

	@Override
	public void addStepAfterReorientation(final CoreSession session, final DocumentModel etapeDoc)
			throws ClientException {
		final ReponsesRouteStep step = getPreviousStep(session, etapeDoc);
		if (isStepInMinistereAttributaire(step, etapeDoc.getAdapter(ReponsesRouteStep.class))) {
			addStepAfterReorientation(session, etapeDoc, step.getDistributionMailboxId());
		} else {
			throw new PosteNotFoundException("l'étape précédente n'est pas dans le même ministère");
		}
	}

	private boolean isStepInMinistereAttributaire(ReponsesRouteStep step, ReponsesRouteStep currentStep) {
		if (currentStep != null
				&& step != null
				&& (currentStep.getMinistereId().contains(step.getMinistereId())
						|| step.getMinistereId().contains(currentStep.getMinistereId()) || step.getMinistereId()
						.equals(currentStep.getMinistereId()))) {
			return true;
		}
		return false;
	}

	@Override
	public void addStepAfterReorientation(final CoreSession session, final DocumentModel etapeDoc,
			final String mailboxId) throws ClientException {

		// création de la mailbox si nécéssaire
		final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
		mailboxPosteService.getOrCreateMailboxPoste(session, mailboxPosteService.getPosteIdFromMailboxId(mailboxId));

		// Récupere le step courant
		final String parentDocId = (String) etapeDoc.getParentRef().reference();

		// récupération des étapes contenues dans parent
		final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
		final DocumentModelList steps = documentRoutingService.getOrderedRouteElement(parentDocId, session);

		final int currentStepIndex = steps.indexOf(etapeDoc);

		// Ajout du step réorientation avec le poste de du step précédemment
		// trouvé
		final DocumentRouteStep newStep = documentRoutingService.createNewRouteStep(session, mailboxId,
				VocabularyConstants.ROUTING_TASK_TYPE_REORIENTATION);
		documentRoutingService.addRouteElementToRoute(etapeDoc.getParentRef(), currentStepIndex + 1, newStep, session);
	}

	@Override
	public void addStepValidationRetourPM(final CoreSession session, final DocumentModel etapeDoc,
			final DocumentModel dossierDoc) throws ClientException {
		final STRouteStep step = etapeDoc.getAdapter(STRouteStep.class);
		final String mailboxId = step.getDistributionMailboxId();
		final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
		final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
		final JournalService journalService = STServiceLocator.getJournalService();

		// Récupération du BDC du ministère attributaire
		final String documentRouteId = step.getDocumentRouteId();
		String idMinistereAttributaire = getIdMinistereAttributaireCourant(session, new IdRef(documentRouteId));
		final PosteNode posteNodeBDC = STServiceLocator.getSTPostesService().getPosteBdcInEntite(
				idMinistereAttributaire);
		if (posteNodeBDC != null && mailboxPosteService != null) {
			mailboxPosteService.getOrCreateMailboxPoste(session, posteNodeBDC.getId());
			final String mailboxIdBdC = mailboxPosteService.getPosteMailboxId(posteNodeBDC.getId());

			final String docId = (String) etapeDoc.getParentRef().reference();
			final DocumentModelList steps = documentRoutingService.getOrderedRouteElement(docId, session);
			final int currentStepIndex = steps.indexOf(etapeDoc);

			// Ajout de l'étape pour validation retour PM
			final DocumentRouteStep newStepValidationRetourPM = createStepValidationRetourPM(session, mailboxId);
			documentRoutingService.addRouteElementToRoute(etapeDoc.getParentRef(), currentStepIndex + 1,
					newStepValidationRetourPM, session);

			// Ajout de l'étape pour signature
			final DocumentRouteStep newStepSignature = createStepSignature(session, mailboxIdBdC);
			documentRoutingService.addRouteElementToRoute(etapeDoc.getParentRef(), currentStepIndex + 1,
					newStepSignature, session);

			// Ajout de l'étape pour retour
			final DocumentRouteStep newStepRetour = createStepRetour(session, mailboxIdBdC);
			documentRoutingService.addRouteElementToRoute(etapeDoc.getParentRef(), currentStepIndex + 1, newStepRetour,
					session);
			journalService.journaliserActionEtapeFDR(session, step, dossierDoc,
					STEventConstant.DOSSIER_POUR_VALIDATION_PM, STEventConstant.COMMENT_DOSSIER_POUR_VALIDATION_PM);
		} else {
			throw new ReponsesException(
					"Ajout étape validation pour retour Premier ministre : postNodeBDC ou mailboxPosteService nulls");
		}
	}

	/**
	 * Création d'une étape Pour retour.
	 * 
	 * @param session
	 *            Session
	 * @return Nouvelle étape Pour retour
	 * @throws ClientException
	 */
	protected DocumentRouteStep createStepRetour(final CoreSession session, final String mailboxId)
			throws ClientException {
		final DocumentModel newStepModel = session.createDocumentModel(STConstant.ROUTE_STEP_DOCUMENT_TYPE);
		final ReponsesRouteStep newStep = newStepModel.getAdapter(ReponsesRouteStep.class);
		newStep.setType(VocabularyConstants.ROUTING_TASK_TYPE_RETOUR);

		final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
		mailboxPosteService.getOrCreateMailboxPoste(session, mailboxPosteService.getPosteIdFromMailboxId(mailboxId));
		newStep.setDistributionMailboxId(mailboxId);

		return newStep;

	}

	/**
	 * Création d'une étape Pour validation retour Premier ministre.
	 * 
	 * @param session
	 *            Session
	 * @return Nouvelle étape Pour validation retour Premier ministre
	 * @throws ClientException
	 */
	protected DocumentRouteStep createStepValidationRetourPM(final CoreSession session, final String mailboxId)
			throws ClientException {
		final DocumentModel newStepModel = session.createDocumentModel(STConstant.ROUTE_STEP_DOCUMENT_TYPE);
		final ReponsesRouteStep newStep = newStepModel.getAdapter(ReponsesRouteStep.class);
		newStep.setType(VocabularyConstants.ROUTING_TASK_TYPE_RETOUR_VALIDATION_PM);

		final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
		mailboxPosteService.getOrCreateMailboxPoste(session, mailboxPosteService.getPosteIdFromMailboxId(mailboxId));
		newStep.setDistributionMailboxId(mailboxId);

		return newStep;

	}

	/**
	 * Création d'une étape Pour signature.
	 * 
	 * @param session
	 *            Session
	 * @return Nouvelle étape Pour signature
	 * @throws ClientException
	 */
	protected DocumentRouteStep createStepSignature(final CoreSession session, final String mailboxId)
			throws ClientException {
		final DocumentModel newStepModel = session.createDocumentModel(STConstant.ROUTE_STEP_DOCUMENT_TYPE);
		final ReponsesRouteStep newStep = newStepModel.getAdapter(ReponsesRouteStep.class);
		newStep.setType(VocabularyConstants.ROUTING_TASK_TYPE_SIGNATURE);

		final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
		mailboxPosteService.getOrCreateMailboxPoste(session, mailboxPosteService.getPosteIdFromMailboxId(mailboxId));
		newStep.setDistributionMailboxId(mailboxId);

		return newStep;

	}

	private ReponsesRouteStep getPreviousStep(final CoreSession session, final DocumentModel etapeDoc)
			throws ClientException {
		// Récupere le step courant
		final String parentDocId = (String) etapeDoc.getParentRef().reference();

		final DocumentModel parentRouteElement = session.getParentDocument(etapeDoc.getRef());
		// on ajoute pas d'étape si on est dans une branche parallèle
		if (STConstant.STEP_FOLDER_DOCUMENT_TYPE.equals(parentRouteElement.getType())) {
			final StepFolder stepFolder = parentRouteElement.getAdapter(StepFolder.class);
			if (stepFolder.isParallel()) {
				// Le parent est un conteneur de tâches parallèles copie de
				// tâche impossible
				throw new ClientException("FeuilleRouteService : Insertion d'étape impossible");
			}
		}

		// Pour vérification des droits
		final NuxeoPrincipal principal = (NuxeoPrincipal) session.getPrincipal();

		// récupération des étapes contenues dans parent
		final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
		final DocumentModelList steps = documentRoutingService.getOrderedRouteElement(parentDocId, session);

		final int currentStepIndex = steps.indexOf(etapeDoc);
		int loopCurrentIndex = currentStepIndex;

		DocumentModel stepModel = null;
		ReponsesRouteStep step = null;
		while (loopCurrentIndex > 0) {

			// Recherche d'un step précédent valide pour la copie
			stepModel = steps.get(loopCurrentIndex - 1);
			if (stepModel.hasFacet("RouteStep")) {
				step = stepModel.getAdapter(ReponsesRouteStep.class);

				final String type = step.getType();

				// on passe les étapes impression, information ou réattribution
				if (!VocabularyConstants.ROUTING_TASK_TYPE_IMPRESSION.equals(type)
						&& !VocabularyConstants.ROUTING_TASK_TYPE_INFORMATION.equals(type)) {
					// si on a pas la fct ReorientationRedacteur on a trouvé
					// l'étape
					if (!principal.isMemberOf(ReponsesBaseFunctionConstant.NC_REORIENTATION_REDACTEUR_READER)) {
						// étape copiable
						break;
						// Sinon si l'étape n'est pas réattribution on a trouvé
						// l'étape
					} else if (!VocabularyConstants.ROUTING_TASK_TYPE_REATTRIBUTION.equals(type)) {
						// étape copiable
						break;
					}
				}
			}

			loopCurrentIndex--;
		}

		if (loopCurrentIndex == 0 || step == null) {
			// Retour KO si insertion impossible et message à user
			throw new ClientException("FeuilleRouteService : Insertion d'étape impossible");
		} else if (!isPosteActive(step)) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Le poste correspondant à l'étape n'est plus actif");
			}
			throw new PosteNotFoundException();
		}

		return step;
	}

	private boolean isPosteActive(final ReponsesRouteStep step) throws ClientException {
		// Vérifie que le poste correspondant à la dernière étape n'a pas été
		// supprimé
		final String distributionMailboxId = step.getDistributionMailboxId();
		final String posteId = SSServiceLocator.getMailboxPosteService().getPosteIdFromMailboxId(distributionMailboxId);
		final PosteNode poste = STServiceLocator.getSTPostesService().getPoste(posteId);
		if (poste.getDeleted() || !poste.isActive()) {
			return false;
		}
		return true;
	}

	@Override
	public void addStepAfterReattribution(final CoreSession session, final DocumentModel etapeDoc,
			final String mailboxId) throws ClientException {
		// Récupère le conteneur de l'étape
		final String parentDocId = (String) etapeDoc.getParentRef().reference();

		// on ajoute pas d'étape si on est dans une branche parallèle
		if (checkStepIsParallel(session, etapeDoc)) {
			throw new ClientException("FeuilleRouteService : Insertion d'étape impossible");
		}

		// Récupération des étapes contenues dans parent
		final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
		final DocumentModelList steps = documentRoutingService.getOrderedRouteElement(parentDocId, session);

		final int currentStepIndex = steps.indexOf(etapeDoc);

		// Ajout du step réattribution avec le poste passé en paramètre
		final DocumentRouteStep newStep = documentRoutingService.createNewRouteStep(session, mailboxId,
				VocabularyConstants.ROUTING_TASK_TYPE_REATTRIBUTION);
		documentRoutingService.addRouteElementToRoute(etapeDoc.getParentRef(), currentStepIndex + 1, newStep, session);
	}

	@Override
	public void addStepsSignatureAndTransmissionAssemblees(final CoreSession session, final DocumentModel dossierDoc,
			final STRouteStep routeStep) throws ClientException {
		final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
		final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
		final STLockService lockService = STServiceLocator.getSTLockService();
		final JournalService journalService = STServiceLocator.getJournalService();
		final Dossier dossier = dossierDoc.getAdapter(Dossier.class);

		// Récupération de la feuille de route
		final DocumentModel feuilleRouteDoc = session.getDocument(new IdRef(dossier.getLastDocumentRoute()));

		// Récupération de l'étape transmission aux assemblées
		DocumentModel transmissionStepDoc = null;
		if (VocabularyConstants.ROUTING_TASK_TYPE_TRANSMISSION_ASSEMBLEE.equals(routeStep.getType())) {
			transmissionStepDoc = routeStep.getDocument();
		} else {
			final List<DocumentModel> nextSteps = findNextSteps(session, dossier.getLastDocumentRoute(),
					routeStep.getDocument(), null);
			for (final DocumentModel nextStepDoc : nextSteps) {
				final STRouteStep nextStep = nextStepDoc.getAdapter(STRouteStep.class);
				if (VocabularyConstants.ROUTING_TASK_TYPE_TRANSMISSION_ASSEMBLEE.equals(nextStep.getType())) {
					transmissionStepDoc = nextStepDoc;
				}
			}
		}
		if (transmissionStepDoc == null) {
			throw new ReponsesException(
					"La prochaine étape de feuille de route n'est pas Pour transmission aux assemblées");
		}

		final DocumentModelList steps = documentRoutingService.getOrderedRouteElement(transmissionStepDoc
				.getParentRef().toString(), session);
		final int transmissionStepIndex = steps.indexOf(transmissionStepDoc);

		// Si la feuille de route possède déjà des étapes Pour signature et Pour transmission au assemblée 
		// après l'étape Pour transmission en cours, On ne les recrée pas 
		if (steps != null && steps.size() > transmissionStepIndex + 2 && 
				VocabularyConstants.ROUTING_TASK_TYPE_SIGNATURE.equals(steps.get(transmissionStepIndex+1)
						.getAdapter(STRouteStep.class).getType()) && 
				VocabularyConstants.ROUTING_TASK_TYPE_TRANSMISSION_ASSEMBLEE.equals(steps.get(transmissionStepIndex+2)
						.getAdapter(STRouteStep.class).getType())) {
			return;
		}

		STRouteStep stepSignature = null;
		STRouteStep stepTransmission = null;

		// Récupération de la mailbox de l'étape Pour signature et Pour
		// transmission aux assemblées de l'instance de feuille de route du
		// dossier courant
		//final List<DocumentRouteTableElement> routeElements = documentRoutingService.getFeuilleRouteElements(feuilleRoute, session);
		Integer index = 0;
		String stepSignatureMailboxId = null;
		String stepTransmissionAssembleesMailboxId = null;
		for (final DocumentModel stepDoc : steps) {
			if (index <= transmissionStepIndex) {
				final STRouteStep step = stepDoc.getAdapter(STRouteStep.class);
				if (VocabularyConstants.ROUTING_TASK_TYPE_SIGNATURE.equals(step.getType())) {
					stepSignature = step;
					stepSignatureMailboxId = step.getDistributionMailboxId();
				}
				if (VocabularyConstants.ROUTING_TASK_TYPE_TRANSMISSION_ASSEMBLEE.equals(step.getType())) {
					stepTransmission = step;
					stepTransmissionAssembleesMailboxId = step.getDistributionMailboxId();
				}
				index++;
			}
		}

		// Si les étapes ne sont pas trouvés on met le poste BDC par défaut
		if (stepSignatureMailboxId == null || stepTransmissionAssembleesMailboxId == null) {
			final OrganigrammeNode posteBdc = STServiceLocator.getSTPostesService().getPosteBdcInEntite(
					dossier.getIdMinistereAttributaireCourant());
			if (posteBdc != null) {
				final String mailboxId = mailboxPosteService.getPosteMailboxId(posteBdc.getId());
				mailboxPosteService.getOrCreateMailboxPoste(session,
						mailboxPosteService.getPosteIdFromMailboxId(mailboxId));
				if (stepSignatureMailboxId == null) {
					stepSignatureMailboxId = mailboxId;
				}
				if (stepTransmissionAssembleesMailboxId == null) {
					stepTransmissionAssembleesMailboxId = mailboxId;
				}
			}
		}

		// Sans poste BDC, impossible de continuer
		if (stepSignatureMailboxId == null || stepTransmissionAssembleesMailboxId == null) {
			throw new ReponsesException(
					"La feuille de route ne comporte pas d'étapes Pour signature et/ou Pour transmission aux assemblées, et le ministère attributaire ne comporte pas de poste BDC");
		} else {
			// maj dossier avec label next step
			final OrganigrammeNode posteBdc = STServiceLocator.getSTPostesService().getPoste(mailboxPosteService.getPosteIdFromMailboxId(stepSignatureMailboxId));
			dossier.setLabelNextStep(posteBdc.getLabel());
		}

		if (stepSignature != null && stepTransmission != null) {
			OrganigrammeService organigrammeService = SSServiceLocator.getOrganigrammeService();
			List<String> minStepPostIdList = new ArrayList<String>();
			OrganigrammeNode minStepNode = organigrammeService.getOrganigrammeNodeById(stepSignature.getMinistereId(), OrganigrammeType.MINISTERE);
			String curentStepPostId = stepTransmission.getDistributionMailboxId().substring(stepTransmission.getDistributionMailboxId().indexOf('-')+1);

			for (PosteNode posteNode : organigrammeService.getAllSubPostes(minStepNode)){
				minStepPostIdList.add(posteNode.getId());
			}

			if (!stepSignature.getMinistereId().equals(stepTransmission.getMinistereId()) || !minStepPostIdList.contains(curentStepPostId)) {
				throw new CopieStepException("feedback.reponses.dossier.error.copie.etape.posteInconnue.transmission");
			}
		}

		lockService.lockDoc(session, feuilleRouteDoc);

		// Ajout des étapes signature et transmission aux assemblées
		final DocumentRouteStep newStepSignature = documentRoutingService.createNewRouteStep(session,
				stepSignatureMailboxId, VocabularyConstants.ROUTING_TASK_TYPE_SIGNATURE);
		final DocumentRouteStep newStepTransmissionAssemblees = documentRoutingService.createNewRouteStep(session,
				stepTransmissionAssembleesMailboxId, VocabularyConstants.ROUTING_TASK_TYPE_TRANSMISSION_ASSEMBLEE);
		documentRoutingService.addRouteElementToRoute(transmissionStepDoc.getParentRef(), transmissionStepIndex + 1,
				newStepSignature, session);
		documentRoutingService.addRouteElementToRoute(transmissionStepDoc.getParentRef(), transmissionStepIndex + 2,
				newStepTransmissionAssemblees, session);

		// Journalise l'ajout des deux étapes
		journalService.journaliserActionEtapeFDR(session, (STRouteStep) newStepSignature, dossierDoc,
				STEventConstant.EVENT_FEUILLE_ROUTE_STEP_CREATE, STEventConstant.COMMENT_FEUILLE_ROUTE_STEP_CREATE);
		journalService.journaliserActionEtapeFDR(session, (STRouteStep) newStepTransmissionAssemblees, dossierDoc,
				STEventConstant.EVENT_FEUILLE_ROUTE_STEP_CREATE, STEventConstant.COMMENT_FEUILLE_ROUTE_STEP_CREATE);

		lockService.unlockDoc(session, feuilleRouteDoc);
	}
	
	@Override
	public boolean addStepAfterReject(final CoreSession session, final String routingTaskId, Dossier dossier) throws ClientException {

		final DocumentModel currentStepDoc = session.getDocument(new IdRef(routingTaskId));
		final DocumentRouteStep currentStepDocRoute = currentStepDoc.getAdapter(DocumentRouteStep.class);
		final String docId = (String) currentStepDoc.getParentRef().reference();

		final DocumentModel parentRouteElementDoc = session.getParentDocument(currentStepDoc.getRef());

		if (STConstant.STEP_FOLDER_DOCUMENT_TYPE.equals(parentRouteElementDoc.getType())) {
			final StepFolder stepFolder = parentRouteElementDoc.getAdapter(StepFolder.class);
			if (stepFolder.isParallel()) {
				// Le parent est un conteneur de tâches parallèles copie de tâche impossible
				throw new SSException("feedback.reponses.route.error.retour");
			}
		}

		final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();

		final DocumentModelList stepsDoc = documentRoutingService.getOrderedRouteElement(docId, session);
		final int currentStepIndex = stepsDoc.indexOf(currentStepDoc);
		
		STRouteStep currentStep = currentStepDoc.getAdapter(STRouteStep.class);
		if (currentStep.getType().equals(VocabularyConstants.ROUTING_TASK_TYPE_TRANSMISSION_ASSEMBLEE)) {
			addStepsSignatureAndTransmissionAssemblees(session, dossier.getDocument(), currentStep);
			return true;
		}

		int loopCurrentIndex = currentStepIndex;
		STRouteStep step = null;

		DocumentModel stepModel = null;
		while (loopCurrentIndex > 0) {

			// Recherche d'un step précédent valide pour la copie
			stepModel = stepsDoc.get(loopCurrentIndex - 1);
			if (stepModel.hasFacet(DocumentRoutingConstants.ROUTE_STEP_FACET)) {
				step = stepModel.getAdapter(STRouteStep.class);
				final String validation = step.getValidationStatus();
				final String type = step.getType();
				checkArbitrage(type);
				final boolean alreadyDuplicated = step.isAlreadyDuplicated();
				// On cherche parmis les étapes validées
				if (STSchemaConstant.ROUTING_TASK_VALIDATION_STATUS_VALIDE_AUTOMATIQUEMENT_VALUE.equals(validation)
						|| STSchemaConstant.ROUTING_TASK_VALIDATION_STATUS_AVIS_FAVORABLE_VALUE.equals(validation)) {
					// on passe les étapes impression, information ou réattribution non déjà dupliquée
					if (!getTypeEtapeImpression().equals(type) && !getTypeEtapeInformation().equals(type)
							&& !isTypeEtapeReattribution(type) && !alreadyDuplicated) {
						// étape copiable
						break;
					}
				}
			}
			--loopCurrentIndex;
		}

		if (loopCurrentIndex == 0) {
			// Retour KO si insertion impossible et message à user
			throw new SSException("feedback.reponses.route.error.retour");
		} else {
			// Copie du step précédemment trouvé
			final DocumentRouteStep stepDoc = stepModel.getAdapter(DocumentRouteStep.class);
			OrganigrammeService organigrammeService = SSServiceLocator.getOrganigrammeService();
			List<String> minStepPostIdList = new ArrayList<String>();
			OrganigrammeNode minStepNode = organigrammeService.getOrganigrammeNodeById(step.getMinistereId(), OrganigrammeType.MINISTERE);
			if (minStepNode != null && !minStepNode.getDeleted()) {
				String stepPostId = step.getDistributionMailboxId().substring(step.getDistributionMailboxId().indexOf('-')+1);
				OrganigrammeNode posteStepNode = organigrammeService.getOrganigrammeNodeById(stepPostId ,OrganigrammeType.POSTE);
				if (posteStepNode != null && !posteStepNode.getDeleted()) {
					final DocumentRouteStep newStep = copyStep(session, stepDoc);
					if (newStep != null) {
						documentRoutingService.addRouteElementToRoute(currentStepDoc.getParentRef(), currentStepIndex + 1, newStep,
								session);
					}
					// Copie du step courant
					final DocumentRouteStep newCurrentStep = copyStep(session, currentStepDocRoute);
					documentRoutingService.addRouteElementToRoute(currentStepDoc.getParentRef(), currentStepIndex + 2,
							newCurrentStep, session);
				} else {
					throw new CopieStepException("feedback.reponses.dossier.error.copie.etape.posteInconnue");
				}
			} else {
				throw new CopieStepException("feedback.reponses.dossier.error.copie.etape.ministereInconnue");
			}
		}
		return true;
	}

	@Override
	public boolean isNextStepReorientationOrReattributionOrArbitrage(final CoreSession session,
			final String routingTaskId) throws ClientException {
		final DocumentModel currentStepDoc = session.getDocument(new IdRef(routingTaskId));
		DocumentModel nextStepDoc;
		final String docId = (String) currentStepDoc.getParentRef().reference();

		DocumentRoutingService service;
		service = SSServiceLocator.getDocumentRoutingService();
		final DocumentModelList stepList = service.getOrderedRouteElement(docId, session);

		final int index = stepList.indexOf(currentStepDoc);

		if (stepList.size() > index + 1) {
			nextStepDoc = stepList.get(index + 1);
			if (nextStepDoc.hasFacet(DocumentRoutingConstants.ROUTE_STEP_FACET)) {
				final ReponsesRouteStep nextStep = nextStepDoc.getAdapter(ReponsesRouteStep.class);

				if (VocabularyConstants.ROUTING_TASK_TYPE_REORIENTATION.equals(nextStep.getType())
						|| VocabularyConstants.ROUTING_TASK_TYPE_REATTRIBUTION.equals(nextStep.getType())
						|| VocabularyConstants.ROUTING_TASK_TYPE_ARBITRAGE.equals(nextStep.getType())) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean isNextStepArbitrage(final CoreSession session, final String routingTaskId) throws ClientException {
		final DocumentModel currentStepDoc = session.getDocument(new IdRef(routingTaskId));
		DocumentModel nextStepDoc;
		final String docId = (String) currentStepDoc.getParentRef().reference();

		DocumentRoutingService service;
		service = SSServiceLocator.getDocumentRoutingService();
		final DocumentModelList stepList = service.getOrderedRouteElement(docId, session);

		final int index = stepList.indexOf(currentStepDoc);

		if (stepList.size() > index + 1) {
			nextStepDoc = stepList.get(index + 1);
			if (nextStepDoc.hasFacet(DocumentRoutingConstants.ROUTE_STEP_FACET)) {
				final ReponsesRouteStep nextStep = nextStepDoc.getAdapter(ReponsesRouteStep.class);

				if (VocabularyConstants.ROUTING_TASK_TYPE_ARBITRAGE.equals(nextStep.getType())) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean isNextStepTransmissionAssemblees(final CoreSession session, final String feuilleRouteDocId,
			final DocumentModel stepDoc) throws ClientException {
		final List<DocumentModel> nextSteps = findNextSteps(session, feuilleRouteDocId, stepDoc, null);
		for (final DocumentModel nextStepDoc : nextSteps) {
			final STRouteStep nextStep = nextStepDoc.getAdapter(STRouteStep.class);
			if (VocabularyConstants.ROUTING_TASK_TYPE_TRANSMISSION_ASSEMBLEE.equals(nextStep.getType())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Vérifie si l'étape passée en paramètre est une étape parallèle
	 * 
	 * @param session
	 * @param l
	 *            'étape
	 * @return vrai si l'étape est parallèle
	 * @throws ClientException
	 */
	private boolean checkStepIsParallel(CoreSession session, DocumentModel etapeDoc) throws ClientException {
		DocumentModel parentRouteElement = session.getParentDocument(etapeDoc.getRef());
		if (STConstant.STEP_FOLDER_DOCUMENT_TYPE.equals(parentRouteElement.getType())) {
			final StepFolder stepFolder = parentRouteElement.getAdapter(StepFolder.class);
			if (stepFolder.isParallel()) {
				// Le parent est un conteneur de tâches parallèles copie de
				// tâche impossible
				return true;
			}
		}
		return false;
	}

	/**
	 * Permet de retourner l'identifiant du ministère attributaire de la feuille de route
	 * 
	 * @param session
	 *            : La session en cours
	 * @param documentRouteId
	 *            : l'identifiant de la feuille de route
	 * @return : l'identifiant du ministère attributaire au format String
	 * @throws ClientException
	 */
	private String getIdMinistereAttributaireCourant(CoreSession session, IdRef documentRouteId) throws ClientException {
		String idMinistereAttributaireCourant = null;
		if (session.exists(documentRouteId)) {
			DocumentModel routeDoc = session.getDocument(documentRouteId);
			STFeuilleRoute route = routeDoc.getAdapter(STFeuilleRoute.class);
			List<String> dossiersIds = route.getAttachedDocuments();
			String dossierId = dossiersIds.get(0);
			IdRef dossierRef = new IdRef(dossierId);
			if (session.exists(dossierRef)) {
				DocumentModel dossierDoc = session.getDocument(dossierRef);
				Dossier dossier = dossierDoc.getAdapter(Dossier.class);
				idMinistereAttributaireCourant = dossier.getIdMinistereAttributaireCourant();
			}
		}

		return idMinistereAttributaireCourant;
	}

	@Override
	public void addStepAfterRejectReattribution(final CoreSession session, final String routingTaskId)
			throws ClientException {

		// Récupere le step courant
		final DocumentModel currentStepDoc = new UnrestrictedGetDocumentRunner(session).getById(routingTaskId);
		final String parentDocId = (String) currentStepDoc.getParentRef().reference();

		// on ajoute pas d'étape si on est dans une branche parallèle
		if (checkStepIsParallel(session, currentStepDoc)) {
			throw new ReponsesException("FeuilleRouteService : Insertion d'étape impossible");
		}

		ReponsesRouteStep currentStep = currentStepDoc.getAdapter(ReponsesRouteStep.class);
		IdRef routeRef = new IdRef(currentStep.getDocumentRouteId());
		String idMinistereAttributaireCourant = getIdMinistereAttributaireCourant(session, routeRef);

		if (idMinistereAttributaireCourant == null) {
			throw new ReponsesException("Le ministère attributaire n'a pas pu être retrouvé");
		} else {
			// récupération des étapes contenues dans parent
			final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
			final DocumentModelList steps = documentRoutingService.getOrderedRouteElement(parentDocId, session);

			final int currentStepIndex = steps.indexOf(currentStepDoc);

			final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
			final OrganigrammeNode posteBdc = STServiceLocator.getSTPostesService().getPosteBdcInEntite(
					idMinistereAttributaireCourant);
			String stepMailboxId = null;
			if (posteBdc != null) {
				final String mailboxId = mailboxPosteService.getPosteMailboxId(posteBdc.getId());
				mailboxPosteService.getOrCreateMailboxPoste(session,
						mailboxPosteService.getPosteIdFromMailboxId(mailboxId));
				stepMailboxId = mailboxId;
			}
			if (stepMailboxId == null) {
				throw new ReponsesException("Le ministère attributaire ne comporte pas de poste BDC");
			}

			// Ajout du step reattribution avec le poste BDC
			final DocumentRouteStep newStep = documentRoutingService.createNewRouteStep(session, stepMailboxId,
					getTypeEtapeAttribution());
			documentRoutingService.addRouteElementToRoute(currentStepDoc.getParentRef(), currentStepIndex + 1, newStep,
					session);
		}
	}

	@Override
	public Calendar getDateDebutEcheance(final CoreSession session, final STDossier dossier) {
		// la date de début pour le calcul des échéances est la date de
		// publication JO de la question.
		final Dossier dossierDoc = dossier.getDocument().getAdapter(Dossier.class);
		final Calendar datePublicationJO = dossierDoc.getQuestion(session).getDatePublicationJO();
		return datePublicationJO;
	}

	/**
	 * 
	 * Retourne la liste des dossiers ayant été validé depuis moins de 7 jours sur les poste en paramètre.
	 * 
	 * @param session
	 *            la session de l'utilisateur. L'utilsateur courant doit avoir les postes accédés
	 * @param poste
	 *            ou rechercher les validations
	 * @return une liste des dossiers validés (sans doublon)
	 * @throws ClientException
	 */
	@Override
	public List<Dossier> getLastWeekValidatedDossiers(final CoreSession session, final Collection<String> posteIds)
			throws ClientException {
		final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();

		final Set<String> mailboxIds = mailboxPosteService.getMailboxPosteIdSetFromPosteIdSet(posteIds);
		if (mailboxIds == null || mailboxIds.size() == 0) {
			throw new ClientException("les mailbox des postes" + StringUtils.join(posteIds, ",")
					+ " n'ont pas été trouvées");
		}

		DateTime dateExpiration = new DateTime();
		dateExpiration = dateExpiration.minusDays(7);
		final StringBuffer selectEtapeValide = new StringBuffer()
				.append("select r.ecm:uuid AS id from RouteStep AS r WHERE r.rtsk:dateFinEtape > DATE ")
				.append(DateUtil.convert(dateExpiration)).append(" AND r.ecm:currentLifeCycleState IN ('done')")
				.append(" AND r.rtsk:distributionMailboxId IN (").append(StringUtil.getQuestionMark(mailboxIds.size()))
				.append(")");
		final List<DocumentModel> etapes = QueryUtils.doUnrestrictedUFNXQLQueryAndFetchForDocuments(session,
				"RouteStep", selectEtapeValide.toString(), mailboxIds.toArray(new String[mailboxIds.size()]));

		// Récupération des dossiers associés à cette étape.
		final Set<String> idDossiers = new HashSet<String>();
		final List<Dossier> dossiers = new ArrayList<Dossier>();
		for (final DocumentModel etapeDoc : etapes) {
			final DocumentRouteStep etape = etapeDoc.getAdapter(DocumentRouteStep.class);
			for (final DocumentModel doc : etape.getAttachedDocuments(session)) {
				if (idDossiers.add(doc.getId())) {
					dossiers.add(doc.getAdapter(Dossier.class));
				}
			}
		}
		return dossiers;
	}

	@Override
	public List<STRouteStep> getLastDayValidatedSteps(final CoreSession session) throws ClientException {
		DateTime dateExpiration = new DateTime();
		dateExpiration = dateExpiration.minusDays(1);
		final StringBuffer selectEtapeValide = new StringBuffer()
				.append("select r.ecm:uuid AS id from DocumentRouteStep AS r WHERE r.rtsk:dateDebutEtape > DATE ")
				.append(DateUtil.convert(dateExpiration)).append(" AND r.ecm:currentLifeCycleState IN ('running')");
		final List<DocumentModel> etapes = QueryUtils.doUnrestrictedUFNXQLQueryAndFetchForDocuments(session,
				"DocumentRouteStep", selectEtapeValide.toString(), new Object[] {});

		// Récupération des dossiers associés à cette étape.
		final List<STRouteStep> steps = new ArrayList<STRouteStep>();
		for (final DocumentModel stepDoc : etapes) {
			final STRouteStep step = stepDoc.getAdapter(STRouteStep.class);
			steps.add(step);
		}
		return steps;
	}

	@Override
	public String getTypeEtapeAttribution() {
		return VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION;
	}

	@Override
	public String getTypeEtapeInformation() {
		return VocabularyConstants.ROUTING_TASK_TYPE_INFORMATION;
	}

	@Override
	public String getTypeEtapeImpression() {
		return VocabularyConstants.ROUTING_TASK_TYPE_IMPRESSION;
	}

	@Override
	protected Boolean isTypeEtapeReattribution(final String typeEtape) {
		return VocabularyConstants.ROUTING_TASK_TYPE_REATTRIBUTION.equals(typeEtape);
	}

	@Override
	public boolean canDistributeStep(final CoreSession session, final STRouteStep routeStep,
			final List<DocumentModel> docs) {
		if (VocabularyConstants.ROUTING_TASK_TYPE_INFORMATION.equals(routeStep.getType())) {
			return false;
		}
		return true;
	}

	@Override
	public void doValidationAutomatiqueOperation(final CoreSession session, final STRouteStep routeStep,
			final List<DocumentModel> docs) throws ClientException {

		final JournalService journalService = STServiceLocator.getJournalService();
		updateRouteStepFieldAfterValidation(session, routeStep, docs, null);

		// s'exécute uniquement si les DossierLink n'ont pas été distribués
		if (!canDistributeStep(session, routeStep, docs)) {
			if (VocabularyConstants.ROUTING_TASK_TYPE_INFORMATION.equals(routeStep.getType())) {
				routeStep
						.setValidationStatus(STSchemaConstant.ROUTING_TASK_VALIDATION_STATUS_VALIDE_AUTOMATIQUEMENT_VALUE);
				routeStep.setDateFinEtape(Calendar.getInstance());
				// Journalisation de l'évènement
				for (final DocumentModel doc : docs) {
					journalService.journaliserActionEtapeFDR(session, routeStep, doc,
							STEventConstant.DOSSIER_AVIS_FAVORABLE, STEventConstant.COMMENT_AVIS_FAVORABLE);
				}
			}
		}

		// S'exécute dans tous les cas
		if (VocabularyConstants.ROUTING_TASK_TYPE_REATTRIBUTION.equals(routeStep.getType())) {
			final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
			final DossierDistributionService dossierDistributionService = ReponsesServiceLocator
					.getDossierDistributionService();
			final CorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();
			// Changement de ministère
			for (final DocumentModel doc : docs) {
				final Dossier dossier = doc.getAdapter(Dossier.class);
				final String idMinistereReattribution = dossier.getIdMinistereReattribution();

				if (idMinistereReattribution != null && !idMinistereReattribution.isEmpty()) {
					// Commentaire setNouveauMinistereCourant : On ne met à jour
					// qu'au moment de la validation de l'étape de validation de
					// réattribution
					// dossierDistributionService.setNouveauMinistereCourant(session,
					// doc, idMinistereReattribution);
					final String ministerePrec = dossier.getIdMinistereAttributairePrecedent();
					// Changement du ministère dans le champ dénormalisé des
					// DossierLink
					final List<DocumentModel> listDossiersLink = corbeilleService.findDossierLink(session, doc.getId());
					for (final DocumentModel dossierLinkDoc : listDossiersLink) {
						final DossierLink dossierLink = dossierLinkDoc.getAdapter(DossierLink.class);

						// Renseigne l'ID et le label du ministère interpellé
						// courant
						dossierLink.setIdMinistereAttributaire(idMinistereReattribution);
						final OrganigrammeNode ministereNode = ministeresService
								.getEntiteNode(idMinistereReattribution);
						String intituleMinistere = "";
						if (ministereNode != null) {
							intituleMinistere = ministereNode.getLabel();
						}
						dossierLink.setIntituleMinistere(intituleMinistere);
						session.saveDocument(dossierLinkDoc);

						dossierDistributionService.correctCounterAfterChangeOnMinistereAttributaire(session,
								dossierLink, ministerePrec);
					}
				}
			}
		}
	}

	@Override
	public boolean isRootStep(final CoreSession session, final String routingTaskId) throws ClientException {
		final DocumentModel stepDoc = new UnrestrictedGetDocumentRunner(session).getById(routingTaskId);
		final DocumentModel parentDoc = session.getDocument(stepDoc.getParentRef());
		return parentDoc.getType().equals(STConstant.FEUILLE_ROUTE_DOCUMENT_TYPE);
	}

	@Override
	public void updateApplicationFieldsAfterValidation(final CoreSession session, final DocumentModel routeStepDoc,
			final List<DocumentModel> dossierDocList, final List<CaseLink> caseLinkList) {
		// Il n'y a pas de champ spécifique à l'application Réponses sur les
		// étapes de feuille de route
	}

	@Override
	public void sendMailAfterDistribution(final CoreSession session, final STRouteStep routeStep)
			throws ClientException {
		final STParametreService paramService = STServiceLocator.getSTParametreService();
		final String texte = paramService.getParametreValue(session,
				STParametreConstant.TEXTE_MAIL_NOTIFICATION_CREATION_TACHE);
		final String objet = paramService.getParametreValue(session,
				STParametreConstant.OBJET_MAIL_NOTIFICATION_CREATION_TACHE);

		final List<String> listIdDossiers = routeStep.getDocumentRoute(session).getAttachedDocuments();

		// Détermine la liste des utilisateurs du poste

		final String mailboxId = routeStep.getDistributionMailboxId();
		final String posteId = SSServiceLocator.getMailboxPosteService().getPosteIdFromMailboxId(mailboxId);
		final PosteNode posteNode = STServiceLocator.getSTPostesService().getPoste(posteId);
		List<STUser> userList = posteNode.getUserList();

		// Filtrage, sauf pour les étapes "Pour information"
		if (!routeStep.getType().equals(VocabularyConstants.ROUTING_TASK_TYPE_INFORMATION)) {
			userList = ReponsesServiceLocator.getProfilUtilisateurService().getFilteredUserList(session, userList);
		}

		// Envoi du mail
		final STMailService mailService = STServiceLocator.getSTMailService();
		mailService.sendHtmlMailToUserListWithLinkToDossiers(session, userList, objet, texte, listIdDossiers);
	}

	@Override
	public void sendDailyDistributionMail(final CoreSession session) throws ClientException {
		final STParametreService paramService = STServiceLocator.getSTParametreService();
		final STMailService mailService = STServiceLocator.getSTMailService();
		final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
		final STPostesService postesService = STServiceLocator.getSTPostesService();
		final ProfilUtilisateurService profilUtilisateurService = ReponsesServiceLocator.getProfilUtilisateurService();
		final String bundle = "messages";
		final Locale locale = Locale.FRENCH;
		final String objet = paramService
				.getParametreValue(session, ReponsesParametreConstant.OBJET_DAILY_DISTRIBUTION);
		final String texte = paramService
				.getParametreValue(session, ReponsesParametreConstant.TEXTE_DAILY_DISTRIBUTION);

		final List<DossierLink> linkList = getLastDayDossierLink(session);
		final Map<String, List<DossierLink>> posteToLink = new HashMap<String, List<DossierLink>>();

		for (final DossierLink link : linkList) {
			final String mailboxId = link.getDistributionMailbox();
			final String posteId = mailboxPosteService.getPosteIdFromMailboxId(mailboxId);

			List<DossierLink> listDossierLinkPoste = posteToLink.get(posteId);
			if (listDossierLinkPoste == null) {
				listDossierLinkPoste = new ArrayList<DossierLink>();
			}
			listDossierLinkPoste.add(link);
			posteToLink.put(posteId, listDossierLinkPoste);
		}

		for (final Entry<String, List<DossierLink>> entry : posteToLink.entrySet()) {

			final Map<String, Object> mailParams = new HashMap<String, Object>();

			final PosteNode poste = postesService.getPoste(entry.getKey());

			mailParams.put("poste", poste.getLabel());

			List<STUser> userList = poste.getUserList();
			userList = profilUtilisateurService.getFilteredUserList(session, userList,
					ProfilUtilisateurConstants.PROFIL_UTILISATEUR_PARAMETRE_MAIL_VALUE_JOURNALIER);

			if (!userList.isEmpty()) {
				// liste des dossiers concernés
				final List<Map<String, String>> dossierList = new ArrayList<Map<String, String>>();
				for (final DossierLink link : entry.getValue()) {
					final Map<String, String> questionMap = new HashMap<String, String>();
					questionMap.put("question", link.getSourceNumeroQuestion());
					final String corbeille = I18NUtils.getMessageString(bundle, link.getRoutingTaskLabel(), null,
							locale);
					questionMap.put("corbeille", corbeille);
					dossierList.add(questionMap);
				}
				mailParams.put("liste_question", dossierList);

				// Envoi du mail
				mailService.sendTemplateHtmlMailToUserList(session, userList, objet, texte, mailParams);
			}
		}
	}

	/**
	 * Récupère les dossiers link dont la date est après la date de la veille
	 * 
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	private List<DossierLink> getLastDayDossierLink(final CoreSession session) throws ClientException {
		final Calendar dateExpiration = Calendar.getInstance();
		dateExpiration.add(Calendar.DAY_OF_MONTH, -1);
		final StringBuffer selectLinkValide = new StringBuffer().append(
				"select l.ecm:uuid AS id from DossierLink AS l WHERE l.cslk:date > ? ").append(
				" AND l.ecm:currentLifeCycleState IN ('todo')");
		final List<DocumentModel> dossierLinks = QueryUtils.doUnrestrictedUFNXQLQueryAndFetchForDocuments(session,
				"DossierLink", selectLinkValide.toString(), new Object[] { dateExpiration });

		// Récupération des dossiers links
		final List<DossierLink> linkList = new ArrayList<DossierLink>();
		for (final DocumentModel linkDoc : dossierLinks) {
			final DossierLink link = linkDoc.getAdapter(DossierLink.class);
			linkList.add(link);
		}
		return linkList;
	}

	@Override
	public String getDossierLinkListToEmailForAutomaticValidationQuery() {
		/**
		 * Requête récupérant les dossiers link dont l'option validation automatique n'a pas été sélectionné, dont la
		 * date d'échéance est dépassé et qui n'a pas déjà été validée.
		 */
		final StringBuilder getDossierLinkListToValidateQuery = new StringBuilder("SELECT * FROM ")
				.append(STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE).append(" WHERE ")
				.append(STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH).append(" = 'todo' AND  ")
				.append(STSchemaConstant.ACTIONABLE_CASE_LINK_SCHEMA_PREFIX).append(":")
				.append(STSchemaConstant.ACTIONABLE_CASE_LINK_AUTOMATIC_VALIDATION_PROPERTY).append(" = 0 AND ")
				.append(DossierConstants.DOSSIER_REPONSES_LINK_PREFIX).append(":")
				.append(STDossierLinkConstant.DOSSIER_LINK_IS_MAIL_SEND_PROPERTY).append(" = 0 AND ")
				.append(STSchemaConstant.ACTIONABLE_CASE_LINK_SCHEMA_PREFIX).append(":")
				.append(STSchemaConstant.ACTIONABLE_CASE_LINK_DUE_DATE_PROPERTY).append(" < TIMESTAMP '%s' ");
		return getDossierLinkListToValidateQuery.toString();
	}

	@Override
	protected void sendMailInfoEcheanceAtteinte(final CoreSession session, final ActionableCaseLink acl)
			throws ClientException {
		try {
			// récupère l'étape courante du dossier lié au dossier link
			final DocumentModel etapeDoc = SSServiceLocator.getDocumentRoutingService().getDocumentRouteStep(session,
					acl);
			final STRouteStep etapeCourante = etapeDoc.getAdapter(STRouteStep.class);

			// on récupère le poste des destinataires
			final String mailboxId = etapeCourante.getDistributionMailboxId();
			if (mailboxId == null) {
				return;
			}
			final String posteId = SSServiceLocator.getMailboxPosteService().getPosteIdFromMailboxId(mailboxId);
			if (posteId == null) {
				return;
			}

			final PosteNode posteNode = STServiceLocator.getSTPostesService().getPoste(posteId);
			final List<STUser> userList = posteNode.getUserList();
			final List<String> mailUserList = new ArrayList<String>();
			if (userList == null || userList.size() < 0) {
				LOGGER.info("le poste ne contient pas d'utilisateur!");
				return;
			}
			for (final STUser user : userList) {
				final String mail = user.getEmail();
				if (mail != null) {
					mailUserList.add(mail);
				}
			}
			// on récupère le message et l'objet du mail
			final STParametreService paramService = STServiceLocator.getSTParametreService();
			final String message = paramService.getParametreValue(session,
					STParametreConstant.MAIL_EXPIRATION_NO_VAL_AUTOMATIQUE_TEXT);
			final String objet = paramService.getParametreValue(session,
					STParametreConstant.MAIL_EXPIRATION_NO_VAL_AUTOMATIQUE_OBJET);

			// Récuperation parametres du template
			final Map<String, Object> params = new HashMap<String, Object>();
			final DossierLink dossierLink = acl.getDocument().getAdapter(DossierLink.class);

			if (dossierLink != null) {
				params.put("numero_question", dossierLink.getNumeroQuestion());
			} else {
				LOGGER.warn("Erreur de récupération du numero de question");
			}

			// envoi du mail
			STServiceLocator.getSTMailService().sendTemplateMail(mailUserList, objet, message, params);
		} catch (final Exception e1) {
			LOGGER.error("erreur lors de l'envoi de mail lors de la validation automatique", e1);
		}
	}

	@Override
	public void initDirectionFdr(final CoreSession session) throws ClientException {
		final String feuilleRouteModelFolderId = ReponsesServiceLocator.getFeuilleRouteModelService()
				.getFeuilleRouteModelFolderId(session);
		final STUsAndDirectionService usService = STServiceLocator.getSTUsAndDirectionService();
		final String query = "SELECT * FROM FeuilleRoute WHERE ecm:parentId='" + feuilleRouteModelFolderId + "'";
		final List<DocumentModel> feuilleRouteModelList = session.query(query);
		for (final DocumentModel feuilleRouteDoc : feuilleRouteModelList) {
			final ReponsesFeuilleRoute fdr = feuilleRouteDoc.getAdapter(ReponsesFeuilleRoute.class);
			final String ministere = fdr.getMinistere();
			final STFeuilleRoute feuilleRoute = feuilleRouteDoc.getAdapter(STFeuilleRoute.class);
			final List<DocumentRouteTableElement> listRouteTableElement = SSServiceLocator.getDocumentRoutingService()
					.getFeuilleRouteElements(feuilleRoute, session);
			boolean directionTrouve = false;
			for (int i = listRouteTableElement.size() - 1; i >= 0; i--) {
				final STRouteStep routeStep = listRouteTableElement.get(i).getRouteStep();
				if (STConstant.ROUTE_STEP_DOCUMENT_TYPE.equals(routeStep.getDocument().getType())) {
					final PosteNode poste = getPosteByStep(routeStep);
					final List<OrganigrammeNode> listDirections = usService.getDirectionFromPoste(poste.getId());
					directionTrouve = initDirectionPiloteInFdr(session, listDirections, ministere, fdr);
				}
				if (directionTrouve) {
					break;
				}
			}
		}
	}

	/**
	 * Parcourt la liste des entités parentes des directions pour trouver la direction associée au ministere
	 * 
	 * @param session
	 * @param listDirections
	 * @param ministereId
	 * @param fdr
	 * @return
	 * @throws ClientException
	 */
	private boolean initDirectionPiloteInFdr(final CoreSession session, final List<OrganigrammeNode> listDirections,
			final String ministereId, final ReponsesFeuilleRoute fdr) throws ClientException {

		for (final OrganigrammeNode direction : listDirections) {
			final UniteStructurelleNode usNode = (UniteStructurelleNode) direction;
			final List<String> ministeres = usNode.getParentEntiteIds();
			if (ministeres.contains(ministereId)) {
				fdr.setIdDirectionPilote(direction.getId().toString());
				fdr.setIntituleDirectionPilote(direction.getLabel());
				fdr.save(session);
				return true;
			}
		}
		return false;

	}

	/**
	 * Recupère le poste associé à l'étape passée en paramètre
	 * 
	 * @param step
	 * @return
	 * @throws ClientException
	 */
	private PosteNode getPosteByStep(final STRouteStep step) throws ClientException {
		final String distributionMailboxId = step.getDistributionMailboxId();
		final String posteId = SSServiceLocator.getMailboxPosteService().getPosteIdFromMailboxId(distributionMailboxId);
		final PosteNode poste = STServiceLocator.getSTPostesService().getPoste(posteId);

		return poste;
	}

	@Override
	protected void checkArbitrage(String type) throws ReponsesException {
		if (VocabularyConstants.ROUTING_TASK_TYPE_ARBITRAGE.equals(type)) {
			throw new ReponsesException("feedback.reponses.route.error.retour.arbitrage");
		}
	}

	@Override
	public void updateRouteStepFieldAfterValidation(CoreSession session, STRouteStep routeStep,
			List<DocumentModel> dossierDocList, List<CaseLink> caseLinkList) throws ClientException {
		// Détermine le poste destinataire de l'étape
		final String mailboxId = routeStep.getDistributionMailboxId();
		final String posteId = SSServiceLocator.getMailboxPosteService().getPosteIdFromMailboxId(mailboxId);

		// Renseigne le libellé du poste à la validation de l'étape
		PosteNode posteNode = null;
		try {
			posteNode = STServiceLocator.getSTPostesService().getPoste(posteId);
		} catch (final Exception exc) {
			STLOGGER.warn(session, STLogEnumImpl.FAIL_GET_POSTE_TEC, exc);
		}
		if (posteNode != null) {

			routeStep.setPosteLabel(posteNode.getLabel());

			// Renseigne les ministères destinataires de l'étape
			final List<EntiteNode> entiteList = STServiceLocator.getSTMinisteresService()
					.getMinistereParentFromPoste(posteId);
			String ministereLabel = "";
			String ministereId = "";
			if (entiteList != null && !entiteList.isEmpty()) {
				final List<String> ministereList = new ArrayList<String>();
				final List<String> ministereIdList = new ArrayList<String>();
				for (final EntiteNode node : entiteList) {
					ministereList.add(node.getEdition());
					ministereIdList.add(node.getId());
				}
				ministereLabel = StringUtils.join(ministereList, ", ");
				ministereId = StringUtils.join(ministereIdList, ", ");
			}
			routeStep.setMinistereLabel(ministereLabel);
			routeStep.setMinistereId(ministereId);

			// Renseigne la direction destinataire de l'étape
			final List<OrganigrammeNode> uniteStructurelleList = STServiceLocator.getSTUsAndDirectionService()
					.getUniteStructurelleFromPoste(posteId);
			String directionLabel = "";
			if (uniteStructurelleList != null && !uniteStructurelleList.isEmpty()) {
				final List<String> directionList = new ArrayList<String>();
				for (final OrganigrammeNode node : uniteStructurelleList) {
					directionList.add(node.getLabel());
				}
				directionLabel = StringUtils.join(directionList, ", ");
				routeStep.setDirectionId(uniteStructurelleList.get(0).getId().toString());
			}
			routeStep.setDirectionLabel(directionLabel);
		}

		// Renseigne le nom de l'agent qui a validé l'étape
		if (!getTypeEtapeInformation().equals(routeStep.getType())) {
			final NuxeoPrincipal principal = (NuxeoPrincipal) session.getPrincipal();
			String userId = principal.getOriginatingUser();
			if (userId == null) {
				userId = principal.getName();
			}
			final String userFullName = STServiceLocator.getSTUserService().getUserFullNameAndCivilite(userId);
			routeStep.setValidationUserLabel(userFullName);
			routeStep.setValidationUserId(userId);
		}

		// note : la date de fin doit être définie lorsque l'étape est terminée
		// pas lorsqu'elle est running

		updateApplicationFieldsAfterValidation(session, routeStep.getDocument(), dossierDocList, caseLinkList);

		routeStep.save(session);
	}

}