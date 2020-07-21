package fr.dila.reponses.web.feuilleroute;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.ui.web.model.SelectDataModelRow;

import fr.dila.ecm.platform.routing.api.DocumentRouteElement;
import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.feuilleroute.ReponsesRouteStep;
import fr.dila.reponses.api.service.DocumentRoutingService;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.web.corbeille.CorbeilleActionsBean;
import fr.dila.reponses.web.dossier.DossierActionsBean;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STViewConstant;
import fr.dila.st.api.feuilleroute.STFeuilleRoute;
import fr.dila.st.api.feuilleroute.STRouteStep;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.web.widget.RouteElementStateRenderDTO;

/**
 * WebBean qui surcharge et étend celui de Nuxeo.
 * 
 * @author jtremeaux
 */
@Scope(CONVERSATION)
@Name("routingActions")
@Install(precedence = Install.FRAMEWORK + 2)
public class DocumentRoutingActionsBean extends fr.dila.ss.web.feuilleroute.DocumentRoutingActionsBean implements
		Serializable {
	/**
	 * Serial UID.
	 */
	private static final long					serialVersionUID		= 1L;

	private static final Map<String, String>	LIFECYCLESTATE_TO_IMG	= new HashMap<String, String>();
	private static final Map<String, String>	LIFECYCLESTATE_TO_LABEL	= new HashMap<String, String>();

	private static final Map<String, String>	VALIDSTATUS_TO_IMG		= new HashMap<String, String>();
	private static final Map<String, String>	VALIDSTATUS_TO_LABEL	= new HashMap<String, String>();

	static {
		LIFECYCLESTATE_TO_IMG.put("draft", "/img/icons/bullet_ball_glass_grey_16.png");
		LIFECYCLESTATE_TO_LABEL.put("draft", "label.reponses.feuilleRoute.etape.draft");

		// État validé -->
		LIFECYCLESTATE_TO_IMG.put("validated", "/img/icons/bullet_ball_glass_green_16.png");
		LIFECYCLESTATE_TO_LABEL.put("validated", "label.reponses.feuilleRoute.etape.validated");

		// État à venir
		LIFECYCLESTATE_TO_IMG.put("ready", "/img/icons/bullet_ball_glass_grey_16.png");
		LIFECYCLESTATE_TO_LABEL.put("ready", "label.reponses.feuilleRoute.etape.ready");

		// État en cours
		LIFECYCLESTATE_TO_IMG.put("running", "/img/icons/bullet_ball_glass_yellow_16.png");
		LIFECYCLESTATE_TO_LABEL.put("running", "label.reponses.feuilleRoute.etape.running");

		// --- État effectué ---
		// État validée manuellement
		VALIDSTATUS_TO_IMG.put("1", "/img/icons/check_16.png");
		VALIDSTATUS_TO_LABEL.put("1", "label.reponses.feuilleRoute.etape.valide.manuellement");

		// État invalidée
		VALIDSTATUS_TO_IMG.put("2", "/img/icons/check_red_16.png");
		VALIDSTATUS_TO_LABEL.put("2", "label.reponses.feuilleRoute.etape.valide.refusValidation");

		// État validée automatiquement
		VALIDSTATUS_TO_IMG.put("3", "/img/icons/bullet_ball_glass_green_16.png");
		VALIDSTATUS_TO_LABEL.put("3", "label.reponses.feuilleRoute.etape.valide.automatiquement");

		// État avec sortie 'non concerné'
		VALIDSTATUS_TO_IMG.put("4", "/img/icons/non_concerne.png");
		VALIDSTATUS_TO_LABEL.put("4", "label.reponses.feuilleRoute.etape.nonConcerne");

	}

	@In(create = true)
	protected transient CorbeilleActionsBean	corbeilleActions;

	@In(required = true, create = true)
	protected SSPrincipal						ssPrincipal;

	@In(create = true, required = false)
	protected transient DossierActionsBean		dossierActions;

	@Override
	public boolean isEditableRouteElement(DocumentModel stepDoc) throws ClientException {
		if (!super.isEditableRouteElement(stepDoc)) {
			return false;
		}

		// À l'état "en cours", seul le destinataire et les administrateurs peuvent modifier la feuille de route
		final STFeuilleRoute route = getRelatedRoute();
		if (route.isRunning()) {
			boolean isDossierLoadedInCorbeille = corbeilleActions.isDossierLoadedInCorbeille();
			DocumentModel dossierDoc = getDossierAttachedToCurrentRoute();
			boolean isAdministrator = false;
			boolean isAdministratorMin = false;
			if (dossierDoc != null) {
				SSPrincipal principal = (SSPrincipal) currentUser;
				Dossier dossier = dossierDoc.getAdapter(Dossier.class);
				isAdministrator = currentUser.isMemberOf(ReponsesBaseFunctionConstant.FDR_INSTANCE_ADMIN_UPDATER);
				isAdministratorMin = currentUser
						.isMemberOf(ReponsesBaseFunctionConstant.FDR_INSTANCE_ADMIN_MIN_UPDATER)
						&& principal.getMinistereIdSet().contains(dossier.getIdMinistereAttributaireCourant());
			}
			if (!isDossierLoadedInCorbeille && !isAdministrator && !isAdministratorMin) {
				return false;
			}
		}

		// À l'état "terminé", seul l'administrateur peut modifier la feuille de route
		if (route.isDone() && !currentUser.isMemberOf(ReponsesBaseFunctionConstant.FDR_INSTANCE_SUBSTITUTOR)) {
			return false;
		}

		return true;
	}

	/**
	 * Détermine si l'instance de feuille de route peut être redémarrée.
	 * 
	 * @return Vrai si l'instance de feuille de route peut être redémarrée
	 * @throws ClientException
	 */
	@Override
	public boolean isFeuilleRouteRestartable() throws ClientException {
		relatedRouteElementsSelectModel = computeSelectDataModelRelatedRouteElements();
		if (relatedRouteElementsSelectModel == null) {
			return false;
		}

		// La dernière étape ne doit pas être à l'état "done"
		List<SelectDataModelRow> rows = relatedRouteElementsSelectModel.getRows();
		if (rows.size() < 1) {
			return false;
		}
		SelectDataModelRow row = rows.get(rows.size() - 1);
		fr.dila.ecm.platform.routing.api.DocumentRouteTableElement element = (fr.dila.ecm.platform.routing.api.DocumentRouteTableElement) row
				.getData();
		DocumentModel routeStepDoc = element.getDocument();
		if (routeStepDoc.getCurrentLifeCycleState().equals(DocumentRouteElement.ElementLifeCycleState.done.name())) {
			return false;
		}
		return true;
	}

	/**
	 * Retourne les étapes de feuille de route courantes
	 * 
	 * @return
	 * @throws ClientException
	 */
	public List<DocumentModel> getCurrentSteps() throws ClientException {
		final DocumentModel dossierDoc = navigationContext.getCurrentDocument();
		if (dossierDoc == null) {
			return new ArrayList<DocumentModel>();
		}
		final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
		return ReponsesServiceLocator.getFeuilleRouteService().getRunningSteps(documentManager,
				dossier.getLastDocumentRoute());
	}

	/**
	 * Détermine si l'utilisateur peut substituer la feuille de route.
	 * 
	 * @return Vrai si on peut substituer la feuille de route
	 * @throws ClientException
	 *             ClientException
	 */
	public boolean canUserSubstituerFeuilleRoute() throws ClientException {
		/*
		 * Apparemment, il semblerait que la règle métier pour autoriser la substitution est que l'utilisateur soit
		 * destinataire d'une étape "pour attribution".
		 */
		final DocumentRoutingService documentRoutingService = ReponsesServiceLocator.getDocumentRoutingService();
		final DocumentModel routeStepDoc = documentRoutingService.getCurrentEtape(
				documentManager, corbeilleActions.getCurrentDossierLink());
		if (routeStepDoc == null) {
			return false;
		}
		final STRouteStep routeStep = routeStepDoc.getAdapter(STRouteStep.class);
		final DocumentModelList steps = documentRoutingService.getOrderedRouteElement((String) routeStepDoc.getParentRef().reference(), documentManager);
		final STRouteStep firstStep = steps.get(0).getAdapter(STRouteStep.class);
		if (!firstStep.isRunning() || !VocabularyConstants.ROUTING_TASK_TYPE_ATTRIBUTION.equals(routeStep.getType())) {
			return false;
		}

		// Vérifie si l'utilisateur possède la fonction unitaire pour redémarrer une feuille de route
		if (!ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.FDR_INSTANCE_SUBSTITUTOR)) {
			return false;
		}

		return true;
	}

	public RouteElementStateRenderDTO getActionFeuilleRouteInfos(final String currentLifecycleState,
			final String validationStatus, final String routingTaskType) {

		if ("done".equals(currentLifecycleState)) {
			final String img = VALIDSTATUS_TO_IMG.get(validationStatus);
			if (img == null) {
				return null;
			} else {
				final String labelKey = VALIDSTATUS_TO_LABEL.get(validationStatus);
				final String label = resourcesAccessor.getMessages().get(labelKey);
				return new RouteElementStateRenderDTO(img, label);
			}
		} else {
			final String img = LIFECYCLESTATE_TO_IMG.get(currentLifecycleState);
			if (img == null) {
				return null;
			} else {
				final String labelKey = LIFECYCLESTATE_TO_LABEL.get(currentLifecycleState);
				final String label = resourcesAccessor.getMessages().get(labelKey);
				return new RouteElementStateRenderDTO(img, label);
			}
		}
	}

	public Boolean isFeuilleRouteVisible() throws ClientException {
		if (ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.FEUILLE_ROUTE_VIEWER)) {
			return Boolean.TRUE;
		}
		return dossierActions.isDossierContainsMinistere();
	}

	/**
	 * Vérifie si le dossier a une étape en cours dans le ministère de l'utilisateur
	 * 
	 * @return vrai si l'utilisateur dispose de droit spécifiques (rw sur tous les dossiers), si le dossierLink est
	 *         rattaché à une mailbox d'un ministere de l'utilisateur, faux sinon Si le dossierLink n'exsite pas (cas de
	 *         l'affichage du verrou en cas de clic sur un lien dans un mail), on récupère le document dossier en cours
	 *         pour parcourir ses étapes en cours et vérifier qu'il existe une étape rattachée au ministère de
	 *         l'utilisateur
	 * @throws ClientException
	 */
	public Boolean isStepInMinistere() throws ClientException {
		if (ssPrincipal.isAdministrator()
				|| ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_PARAPHEUR_ADMIN_UPDATER)) {
			// L'administrateur fonctionnel a le droit de lecture et d'ecriture
			// sur tous les dossiers
			return true;
		}

		final DossierLink dossierLink = corbeilleActions.getCurrentDossierLink();
		String mailboxId;

		if (dossierLink == null) {
			Dossier dossier = navigationContext.getChangeableDocument().getAdapter(Dossier.class);
			
			if (dossier != null && ssPrincipal.getMinistereIdSet().contains(dossier.getIdMinistereAttributaireCourant())
					&& ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_PARAPHEUR_ADMIN_MIN_UPDATER)) {
				// Si le dossier appartient à son Ministère, l'admin ministériel peut vérouiller le dossier
				return true;
			}
			
			List<DocumentModel> stepsDoc = ReponsesServiceLocator.getFeuilleRouteService().getRunningSteps(
					documentManager, dossier.getLastDocumentRoute());

			for (DocumentModel stepDoc : stepsDoc) {
				ReponsesRouteStep step = stepDoc.getAdapter(ReponsesRouteStep.class);
				mailboxId = step.getDistributionMailboxId();
				if (isMailboxIdInUserPostes(mailboxId)) {
					return true;
				}
			}
		} else {
			mailboxId = dossierLink.getDistributionMailbox();
			return isMailboxIdInUserPostes(mailboxId);
		}

		return false;
	}

	/**
	 * vérifie que l'id de mailbox passé en paramètre est une mailbox associée à l'utilisateur courant. S'il s'agit
	 * d'une mailbox d'un poste SGG on retourne faux, les utilisateurs n'ayant pas le droit de faire une action sur un
	 * dossier d'un poste SGG
	 * 
	 * @param mailboxId
	 * @return vrai si l'utilisateur courant dispose de la mailbox parmi ses postes
	 */
	private boolean isMailboxIdInUserPostes(String mailboxId) throws ClientException {
		if (mailboxId != null) {
			final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
			final String posteId = mailboxPosteService.getPosteIdFromMailboxId(mailboxId);
			final PosteNode posteNode = STServiceLocator.getSTPostesService().getPoste(posteId);
			if (posteNode.isChargeMissionSGG()) {
				// aucun utilisateur ne peut faire une action sur un dossier d'un poste SGG
				return false;
			}

			// on recupere le ministere du poste
			final List<EntiteNode> ministereNode = STServiceLocator.getSTMinisteresService()
					.getMinistereParentFromPoste(posteId);
			if (ministereNode != null) {
				for (final EntiteNode entiteNode : ministereNode) {
					if (ssPrincipal.getMinistereIdSet().contains(entiteNode.getId())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Renvoie true sur l'ont se trouve sur une étape "Pour signature"
	 * 
	 * @return
	 * @throws ClientException
	 */
	public boolean isStepSignature() throws ClientException {
		final DossierLink dossierLink = corbeilleActions.getCurrentDossierLink();
		return VocabularyConstants.ROUTING_TASK_TYPE_SIGNATURE.equals(dossierLink.getRoutingTaskType());
	}

	/**
	 * Renvoie true sur l'ont se trouve sur une étape "Pour Arbitrage"
	 * 
	 * @return
	 * @throws ClientException
	 */
	public boolean isStepPourArbitrage() throws ClientException {
		return ReponsesServiceLocator.getReponsesArbitrageService().isStepPourArbitrage(
				corbeilleActions.getCurrentDossierLink());
	}

	/**
	 * Renvoie true sur l'ont se trouve sur une étape "Pour réattribution"
	 * 
	 * @return
	 * @throws ClientException
	 */
	public boolean isStepPourReattribution() throws ClientException {
		final DossierLink dossierLink = corbeilleActions.getCurrentDossierLink();
		return VocabularyConstants.ROUTING_TASK_TYPE_REATTRIBUTION.equals(dossierLink.getRoutingTaskType());
	}

	/**
	 * Vérifie l'étape de feuille de route suivante, valide l'étape si c'est réattribution ou réorientation, dans le cas
	 * de la réattribution on test si le ministère du poste associé à l'étape réattribution est différent du ministère
	 * du poste de l'étape en cours.
	 * 
	 * @return true si réattribution ou réorientation
	 * @throws ClientException
	 */
	public Boolean isNextStepReorientationOrReattributionOrArbitrage() throws ClientException {
		final DossierLink dossierLink = corbeilleActions.getCurrentDossierLink();
		return ReponsesServiceLocator.getFeuilleRouteService().isNextStepReorientationOrReattributionOrArbitrage(
				documentManager, dossierLink.getRoutingTaskId());
	}

	/**
	 * Vérifie l'étape de feuille de route suivante si pour arbitrage
	 * 
	 * @return true si pour abitrage
	 * @throws ClientException
	 */
	public Boolean isNextStepArbitrage() throws ClientException {
		final DossierLink dossierLink = corbeilleActions.getCurrentDossierLink();
		return ReponsesServiceLocator.getFeuilleRouteService().isNextStepArbitrage(documentManager,
				dossierLink.getRoutingTaskId());
	}

	/**
	 * Renvoie true si on est dans la premiere étape ou dans une branche parallèle. Utilisé comme filtre dans les
	 * actions du dossier
	 * 
	 * @return
	 * @throws ClientException
	 */
	public boolean isFirstStepInBranchOrParallel() throws ClientException {
		final DossierLink dossierLink = corbeilleActions.getCurrentDossierLink();
		return ReponsesServiceLocator.getFeuilleRouteService().isFirstStepInBranchOrParallel(documentManager,
				dossierLink.getRoutingTaskId());
	}

	/**
	 * Renvoie true si l'étape du DossierLink courant est à la racine de la feuille de route Utilisé comme filtre dans
	 * les actions du dossier
	 * 
	 * @return
	 * @throws ClientException
	 */
	public boolean isRootStep() throws ClientException {
		final DossierLink dossierLink = corbeilleActions.getCurrentDossierLink();
		return ReponsesServiceLocator.getFeuilleRouteService().isRootStep(documentManager,
				dossierLink.getRoutingTaskId());
	}

	public String getNextStepLabel() {
		final Dossier dossier = navigationContext.getCurrentDocument().getAdapter(Dossier.class);
		return dossier.getLabelNextStep();
	}

	/**
	 * Historiquement, le sélecteur Reponses est basé sur 2 beans en fonction du type de document traité.
	 * Cette méthode renvoie le sélecteur adéquat.
	 * 
	 * Fiche Question/Réponse: relatedRouteElementsSelectModel
	 * Fiche Feuille de route: selectDataModel
	 */
	@Override
	protected List<SelectDataModelRow> getSelectableRows() {
		List<SelectDataModelRow> rows = null;

		if (STConstant.FEUILLE_ROUTE_DOCUMENT_TYPE.equals(navigationContext.getCurrentDocument().getType())) {
			// On est dans la page de gestion des modèles
			rows = selectDataModel.getRows();
		} else if (STConstant.DOSSIER_DOCUMENT_TYPE.equals(navigationContext.getCurrentDocument().getType())) {
			// On est dans un dossier
			rows = relatedRouteElementsSelectModel.getRows();
		}
		return rows;
	}
	
	@Override
	public String saveRouteElement() throws ClientException {

		if (checkValidityStep()) {
			return super.saveRouteElement();
		}
		return STViewConstant.ERROR_VIEW;
	}
	
	
	@Override
	public String updateRouteElement() throws ClientException {

		if (checkValidityStep()) {
			return super.updateRouteElement();
		}
		return STViewConstant.ERROR_VIEW;
	}
	
	
	/**
	 *
	 * Lors de l'ajout d'une étape à une FdR: Vérifie que le type d'étape soit valide par rapport au type d'acte
	 *
	 * @return
	 * @throws ClientException
	 */
	private boolean checkValidityStep() throws ClientException {
		DocumentModel newDocument = navigationContext.getChangeableDocument();
		
		return checkValidityStep(newDocument);
	}

	@Override
	protected boolean checkValidityStep(DocumentModel stepDoc) throws ClientException {
		final OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
		final DossierDistributionService dossierDistributionService = ReponsesServiceLocator.getDossierDistributionService();
		List<PosteNode> posteNodeList = new ArrayList<PosteNode>();
		List<String> posteIdList = new ArrayList<String>();

		STRouteStep routeStep = stepDoc.getAdapter(STRouteStep.class);

		// Si la nouvelle étape est de type "pour signature" ou "Transmission aux assemblées"
		// Vérifier que le poste de l'étape appartient au ministère attributaire du dossier
		if (!ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.FDR_INSTANCE_ADMIN_UPDATER) &&
				(routeStep.getType().equals(VocabularyConstants.ROUTING_TASK_TYPE_SIGNATURE) || 
				routeStep.getType().equals(VocabularyConstants.ROUTING_TASK_TYPE_TRANSMISSION_ASSEMBLEE))){
			String idPoste = routeStep.getDistributionMailboxId().substring(routeStep.getDistributionMailboxId().indexOf('-') + 1);
			Dossier dossier = navigationContext.getCurrentDocument().getAdapter(Dossier.class);
			if (dossier != null) {
				OrganigrammeNode minNode = organigrammeService.getOrganigrammeNodeById(dossier.getIdMinistereAttributaireCourant(), OrganigrammeType.MINISTERE);
				posteNodeList.addAll(organigrammeService.getAllSubPostes(minNode));
			} else {
				String documentRouteId = navigationContext.getCurrentDocument().getParentRef().toString();
				DocumentModel dossierDoc = dossierDistributionService.getDossierFromDocumentRouteId(documentManager, documentRouteId);
				if (dossierDoc == null) {
					return true;
				}
				dossier = dossierDoc.getAdapter(Dossier.class);
				if (dossier != null) {
					OrganigrammeNode minNode = organigrammeService.getOrganigrammeNodeById(dossier.getIdMinistereAttributaireCourant(), OrganigrammeType.MINISTERE);
					posteNodeList.addAll(organigrammeService.getAllSubPostes(minNode));
				} else {
					final String message = "Erreur lors de la récupération du document courant";
					facesMessages.add(StatusMessage.Severity.WARN, message);
					return false;
				}
			}
			for (PosteNode posteNode : posteNodeList) {
				posteIdList.add(posteNode.getId());
			}
			if (!posteIdList.contains(idPoste)) {
				final String message = resourcesAccessor.getMessages().get("feedback.reponses.dossier.info.refusAjoutStepHorsMinistère");
				facesMessages.add(StatusMessage.Severity.WARN, message);
				return false;
			}
		}
		return true;
	}
}
