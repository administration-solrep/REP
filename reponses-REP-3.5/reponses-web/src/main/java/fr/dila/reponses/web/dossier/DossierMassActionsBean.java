package fr.dila.reponses.web.dossier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.ejb.Remove;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.remoting.WebRemote;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;
import org.nuxeo.runtime.transaction.TransactionHelper;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Allotissement;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.cases.DossierCommon;
import fr.dila.reponses.api.cases.Question;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.api.service.CorbeilleService;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.api.service.ReponsesArbitrageService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.web.corbeille.CorbeilleActionsBean;
import fr.dila.reponses.web.feuilleroute.DocumentRoutingActionsBean;
import fr.dila.reponses.web.remote.DossierMassActions;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringUtil;
import fr.dila.st.web.context.NavigationContextBean;
import fr.dila.st.web.dossier.DossierLockActionsBean;
import fr.dila.st.web.lock.STLockActionsBean;

/**
 * Bean de gestion des actions de masse
 * 
 */
@Name("dossierMassActions")
@Scope(ScopeType.CONVERSATION)
public class DossierMassActionsBean implements DossierMassActions, Serializable {

	/**
	 * 
	 */
	private static final long							serialVersionUID		= -3068999788468446504L;

	private static STLogger								LOGGER					= STLogFactory
																						.getLog(DossierMassActionsBean.class);
	protected static String								ERROR_ETAPE				= "feedback.reponses.dossier.error.etape";
	protected static String								ERROR_OCCURRED			= "feedback.reponses.traitement.error";

	@In(create = true, required = false)
	protected transient CoreSession						documentManager;

	@In(create = true, required = false)
	protected SSPrincipal								ssPrincipal;

	@In(create = true, required = false)
	protected transient NavigationContextBean			navigationContext;

	@In(create = true, required = false)
	protected transient DocumentsListsManager			documentsListsManager;

	@In(create = true, required = false)
	protected transient FacesMessages					facesMessages;

	@In(create = true, required = false)
	protected transient ResourcesAccessor				resourcesAccessor;

	@In(create = true, required = false)
	protected transient CorbeilleActionsBean			corbeilleActions;

	@In(create = true, required = false)
	protected transient DocumentRoutingActionsBean		routingActions;

	@In(create = true, required = false)
	protected transient DossierLockActionsBean			dossierLockActions;

	@In(create = true, required = false)
	protected transient STLockActionsBean				stLockActions;

	@In(create = true, required = false)
	protected transient DossierActionsBean				dossierActions;

	@In(create = true, required = false)
	protected transient ReponseActionsBean				reponseActions;

	@In(create = true, required = false)
	protected transient DossierDistributionActionsBean	dossierDistributionActions;

	@In(create = true, required = false)
	protected transient ContentViewActions				contentViewActions;

	private int											totalMassDocument		= -1;
	protected int										doneMassDocument		= 0;
	protected MassActionType							massActionType;
	private boolean										massActionStarted;
	private boolean										massModalVisible		= false;
	protected final Set<Dossier>						dossiersOk				= new HashSet<Dossier>();
	protected final Set<Dossier>						dossiersEnErreur		= new HashSet<Dossier>();
	protected final Set<String>							infosErreursMassActions	= new HashSet<String>();
	private String										selectedMinForReattribution;
	private String										reattributionObservations;
	private String										reattributionMinistere;
	private String										selectedMinForRattachement;
	private String										selectedDirectionPilote;

	// Services
	protected CorbeilleService							corbeilleService;
	protected ReponsesArbitrageService					arbitrageService;
	protected DossierDistributionService				dossierDistributionService;
	protected AllotissementService						allotissementService;

	/**
	 * Les actions de masses disponibles
	 * 
	 */
	protected enum MassActionType {
		MASSE_ACTION_AVIS_FAVORABLE, MASSE_ACTION_AVIS_DEFAVORABLE_ET_INSERER_TACHES, MASSE_ACTION_AVIS_DEFAVORABLE_ET_POURSUIVRE, MASSE_ACTION_NON_CONCERNE_REATTRIBUTION, MASSE_ACTION_NON_CONCERNE_REORIENTATION, MASSE_ACTION_NON_CONCERNE_REAFFECTATION, MASSE_ACTION_DEMANDE_ARBITRAGE_SGG, MASSE_ACTION_NON_CONCERNE_REATTRIBUTION_PLAN_CLASSEMENT, MASSE_ACTION_MODIFICATION_MINISTERE_RATTACHEMENT, MASSE_ACTION_MODIFICATION_DIRECTION_PILOTE, MASSE_ACTION_REATTRIBUTION_DIRECTE;
	};

	/*
	 * ########################################################################## ################# GETTER / SETTER #
	 * ####################################### ####################################################
	 */

	protected CoreSession getDocumentManager() {
		return documentManager;
	}

	protected void setDocumentManager(CoreSession documentManager) {
		this.documentManager = documentManager;
	}

	protected SSPrincipal getSsPrincipal() {
		return ssPrincipal;
	}

	protected void setSsPrincipal(SSPrincipal ssPrincipal) {
		this.ssPrincipal = ssPrincipal;
	}

	protected NavigationContextBean getNavigationContext() {
		return navigationContext;
	}

	protected void setNavigationContext(NavigationContextBean navigationContext) {
		this.navigationContext = navigationContext;
	}

	protected FacesMessages getFacesMessages() {
		return facesMessages;
	}

	protected void setFacesMessages(FacesMessages facesMessages) {
		this.facesMessages = facesMessages;
	}

	protected ResourcesAccessor getResourcesAccessor() {
		return resourcesAccessor;
	}

	protected void setResourcesAccessor(ResourcesAccessor resourcesAccessor) {
		this.resourcesAccessor = resourcesAccessor;
	}

	protected CorbeilleActionsBean getCorbeilleActions() {
		return corbeilleActions;
	}

	protected void setCorbeilleActions(CorbeilleActionsBean corbeilleActions) {
		this.corbeilleActions = corbeilleActions;
	}

	protected DocumentRoutingActionsBean getRoutingActions() {
		return routingActions;
	}

	protected void setRoutingActions(DocumentRoutingActionsBean routingActions) {
		this.routingActions = routingActions;
	}

	protected DossierLockActionsBean getDossierLockActions() {
		return dossierLockActions;
	}

	protected void setDossierLockActions(DossierLockActionsBean dossierLockActions) {
		this.dossierLockActions = dossierLockActions;
	}

	protected STLockActionsBean getStLockActions() {
		return stLockActions;
	}

	protected void setStLockActions(STLockActionsBean stLockActions) {
		this.stLockActions = stLockActions;
	}

	protected DossierActionsBean getDossierActions() {
		return dossierActions;
	}

	protected void setDossierActions(DossierActionsBean dossierActions) {
		this.dossierActions = dossierActions;
	}

	protected ReponseActionsBean getReponseActions() {
		return reponseActions;
	}

	protected void setReponseActions(ReponseActionsBean reponseActions) {
		this.reponseActions = reponseActions;
	}

	protected DossierDistributionActionsBean getDossierDistributionActions() {
		return dossierDistributionActions;
	}

	protected void setDossierDistributionActions(DossierDistributionActionsBean dossierDistributionActions) {
		this.dossierDistributionActions = dossierDistributionActions;
	}

	public String getSelectedMinForReattribution() {
		return selectedMinForReattribution;
	}

	public void setSelectedMinForReattribution(String selectedMinForReattribution) {
		this.selectedMinForReattribution = selectedMinForReattribution;
	}

	public String getReattributionObservations() {
		return reattributionObservations;
	}

	public void setReattributionObservations(String reattributionObservations) {
		this.reattributionObservations = reattributionObservations;
	}

	public String getReattributionMinistere() {
		return reattributionMinistere;
	}

	public void setReattributionMinistere(String reattributionMinistere) {
		this.reattributionMinistere = reattributionMinistere;
	}

	public String getSelectedMinForRattachement() {
		return selectedMinForRattachement;
	}

	public void setSelectedMinForRattachement(String selectedMinForRattachement) {
		this.selectedMinForRattachement = selectedMinForRattachement;
	}

	public String getSelectedDirectionPilote() {
		return selectedDirectionPilote;
	}

	public void setSelectedDirectionPilote(String selectedDirectionPilote) {
		this.selectedDirectionPilote = selectedDirectionPilote;
	}

	/*
	 * ########################################################################## ################# PUBLICS METHOD #
	 * ######################################## ###################################################
	 */

	public void masseFdrActionDonnerAvisFavorable() {
		processMassAction(MassActionType.MASSE_ACTION_AVIS_FAVORABLE);
	}

	public void masseFdrActionDonnerAvisDefavorableEtInsererTaches() {
		processMassAction(MassActionType.MASSE_ACTION_AVIS_DEFAVORABLE_ET_INSERER_TACHES);
	}

	public void masseFdrActionDonnerAvisDefavorableEtPoursuivre() {
		processMassAction(MassActionType.MASSE_ACTION_AVIS_DEFAVORABLE_ET_POURSUIVRE);
	}

	public void masseFdrActionNonConcerneReorientation() {
		processMassAction(MassActionType.MASSE_ACTION_NON_CONCERNE_REORIENTATION);
	}

	public void masseFdrActionNonConcerneReattribution() {
		processMassAction(MassActionType.MASSE_ACTION_NON_CONCERNE_REATTRIBUTION);
	}

	public void masseFdrActionDemandeArbitrageSGG() {
		processMassAction(MassActionType.MASSE_ACTION_DEMANDE_ARBITRAGE_SGG);
	}

	public void masseFdrActionReattributionDirecte() {
		processMassAction(MassActionType.MASSE_ACTION_REATTRIBUTION_DIRECTE);
	}

	public void masseFdrActionNonConcerneReaffectation() {
		processMassAction(MassActionType.MASSE_ACTION_NON_CONCERNE_REAFFECTATION);
	}

	public void masseActionModificationMinistereRattachement() {
		processMassAction(MassActionType.MASSE_ACTION_MODIFICATION_MINISTERE_RATTACHEMENT);
	}

	public void masseActionModificationDirectionPilote() {
		processMassAction(MassActionType.MASSE_ACTION_MODIFICATION_DIRECTION_PILOTE);
	}

	/**
	 * Annule la sélection du ministère. Utilisé dans masse_unconcerned_task_panel
	 */
	public void resetMinSelection() {
		selectedMinForReattribution = null;
		selectedMinForRattachement = null;
		selectedDirectionPilote = null;
	}

	public void start(final String id) {
		cancelMassAction();
		String corectValue = id;
		if (id.endsWith("_PLAN_CLASSEMENT")) {
			corectValue = corectValue.replace("_PLAN_CLASSEMENT", "");
		} else if (id.endsWith("_RECHERCHE")) {
			corectValue = corectValue.replace("_RECHERCHE", "");
		}

		massActionType = MassActionType.valueOf(corectValue);
		String selectionList = null;
		try {
			selectionList = initMasseFdrAction(isCheckAllotNecessary(massActionType));
		} catch (ClientException exc) {
			LOGGER.error(documentManager, STLogEnumImpl.FAIL_PROCESS_MASS_FONC, exc);
			cleanAfterMassAction(selectionList);
			rollbackMassAction(exc);
			return;
		}

		massModalVisible = true;
	}

	@Override
	@WebRemote
	public void confirmMassAction(final int index) {
		final String currentView = corbeilleActions.getCurrentView();
		final String selectionList = DossierDistributionActionsBean.retrieveSelectionListAccordingView(currentView);
		boolean errorInit = false;
		try {
			if (index == 0 && dossiersOk.isEmpty()) {
				initDossierList(selectionList, isCheckAllotNecessary(massActionType));
			}
			massActionStarted = Boolean.TRUE;
		} catch (Exception e) {
			errorInit = true;
			LOGGER.error(documentManager, STLogEnumImpl.FAIL_PROCESS_MASS_FONC, e);
			facesMessages.add(StatusMessage.Severity.ERROR, resourcesAccessor.getMessages().get(ERROR_OCCURRED));
		}

		if (!errorInit) {

			if (index < dossiersOk.size()) {
				Dossier dossier = dossiersOk.toArray(new Dossier[dossiersOk.size()])[index];
				try {
					if (checkFdrDossier(massActionType, dossier) && unlockDocIfLocked(dossier)) {
						// Set du contexte courant sur les documents de travail
						navigationContext.setCurrentDocument(dossier.getDocument());
						List<DocumentModel> lstUpdatableDossier = getCorbeilleService()
								.findUpdatableDossierLinkForDossier(documentManager, dossier.getDocument());
						for (final DocumentModel dossierLinkDoc : lstUpdatableDossier) {
							// On charge le dossier link
							final DossierLink dossierLink = dossierLinkDoc.getAdapter(DossierLink.class);
							corbeilleActions.setCurrentDossierLink(dossierLink);
							if (dossierDistributionActions.isDossierLinkLoaded()
									&& isActionCompatibleWithStep(massActionType, dossierLink)) {
								processSpecificMassAction(massActionType, dossier, dossierLinkDoc);
							} else {
								addDossierInErrorList(dossier, ERROR_OCCURRED);
								LOGGER.info(documentManager, STLogEnumImpl.FAIL_PROCESS_MASS_FONC, dossierLinkDoc);
								break;
							}
						}
						corbeilleActions.dechargerDossierLink();
						navigationContext.resetCurrentDocument();
					} else {
						dossiersEnErreur.add(dossier);
					}
					doneMassDocument++;

				} catch (final Exception exc) {
					// On incrémente le la masse de document traitée car elle n'a pas été incrémentée avant l'exception
					doneMassDocument++;
					LOGGER.error(documentManager, STLogEnumImpl.FAIL_PROCESS_MASS_FONC, exc);
					dossiersEnErreur.add(dossier);
					infosErreursMassActions.add(ERROR_OCCURRED);

				}
			} else {
				// finish
				endMassAction(selectionList);
				cleanAfterMassAction(selectionList);
				// Message spécifique à la réattribution
				if (MassActionType.MASSE_ACTION_NON_CONCERNE_REATTRIBUTION.equals(massActionType)
						&& selectedMinForReattribution != null) {
					OrganigrammeNode posteBdc = null;
					try {
						posteBdc = STServiceLocator.getSTPostesService().getPosteBdcInEntite(
								selectedMinForReattribution);
					} catch (ClientException e) {
						LOGGER.debug(documentManager, STLogEnumImpl.DEFAULT,
								"Impossible de récupérer le poste BDC après une action de masse", null, e);
					}
					if (posteBdc == null) {
						infosErreursMassActions.add("feedback.reponses.dossier.error.reattributionNoBdc");
					}
				}
			}
		}
	}

	/**
	 * Vérifie que l'action est compatible avec l'étape en cours
	 * 
	 * @param massActionType
	 * @param dossierLink
	 * @return
	 */
	private boolean isActionCompatibleWithStep(MassActionType massActionType, DossierLink dossierLink) {
		String type = dossierLink.getRoutingTaskType();

		// Si l'étape est "pour arbitrage" seule l'action de masse "valider" est
		// compatible
		if (VocabularyConstants.ROUTING_TASK_TYPE_ARBITRAGE.equals(type)
				&& !MassActionType.MASSE_ACTION_AVIS_FAVORABLE.equals(massActionType)) {
			return false;
		}

		// Si l'étape est "pour réattribution", seules les actions de masse
		// "valider" et "demande arbitrage sgg" sont compatibles
		if (VocabularyConstants.ROUTING_TASK_TYPE_REATTRIBUTION.equals(type)) {
			if (!MassActionType.MASSE_ACTION_AVIS_FAVORABLE.equals(massActionType)
					&& !MassActionType.MASSE_ACTION_DEMANDE_ARBITRAGE_SGG.equals(massActionType)) {
				return false;
			}
		}

		return true;
	}

	public void cancelMassAction() {
		massActionType = null;
		totalMassDocument = -1;
		doneMassDocument = 0;
		massActionStarted = Boolean.FALSE;
		massModalVisible = false;
		dossiersOk.clear();
		dossiersEnErreur.clear();
		infosErreursMassActions.clear();
		// Reset du formulaire de réattribution
		resetMinSelection();
		// SPL on raffraichit
		try {
			corbeilleActions.dechargerDossierLink();
			navigationContext.resetCurrentDocument();
		} catch (ClientException exc) {
			facesMessages.add(StatusMessage.Severity.WARN, resourcesAccessor.getMessages().get(ERROR_OCCURRED));
			LOGGER.error(documentManager, STLogEnumImpl.FAIL_PROCESS_MASS_FONC, exc);
		}
	}

	public boolean isMassActionStarted() {
		return massActionStarted;
	}

	public String getMassCount() {
		if (totalMassDocument < 0) {
			return "";
		} else if (totalMassDocument > 1) {
			return totalMassDocument + " éléments à traiter";
		} else {
			return totalMassDocument + " élément à traiter";
		}
	}

	public boolean isMassModalVisible() {
		return massModalVisible;
	}

	@Override
	@WebRemote
	public boolean hasNext() {
		return massActionType != null;
	}

	public boolean isMassReattributionOrReaffectation() {
		return massActionType != null
				&& (massActionType.equals(MassActionType.MASSE_ACTION_NON_CONCERNE_REATTRIBUTION) || massActionType
						.equals(MassActionType.MASSE_ACTION_NON_CONCERNE_REAFFECTATION));
	}

	public boolean isMassReattributionDirecte() {
		return massActionType != null && massActionType.equals(MassActionType.MASSE_ACTION_REATTRIBUTION_DIRECTE);
	}

	public boolean isMassRattachementMinistere() {
		return massActionType != null
				&& (massActionType.equals(MassActionType.MASSE_ACTION_MODIFICATION_MINISTERE_RATTACHEMENT));
	}

	public boolean isMassDirectionPilote() {
		return massActionType != null
				&& (massActionType.equals(MassActionType.MASSE_ACTION_MODIFICATION_DIRECTION_PILOTE));
	}

	@Override
	@WebRemote
	public int getProgressPercent() {
		if (totalMassDocument <= 0) {
			return 0;
		} else {
			return (int) ((float) doneMassDocument / (float) totalMassDocument * 100L);
		}
	}

	// @Create
	@Override
	public void initialize() {
		LOGGER.debug(STLogEnumImpl.INIT_BEAN_TEC, DossierMassActionsBean.class.getSimpleName());
	}

	@Override
	@Destroy
	@Remove
	@PermitAll
	public void destroy() {
		LOGGER.debug(STLogEnumImpl.DESTROY_BEAN_TEC, DossierMassActionsBean.class.getSimpleName());
	}

	@Override
	@PrePassivate
	public void saveState() {
		LOGGER.debug(STLogEnumImpl.SAVE_BEAN_TEC, DossierMassActionsBean.class.getSimpleName());
	}

	@Override
	@PostActivate
	public void readState() {
		LOGGER.debug(STLogEnumImpl.READ_BEAN_TEC, DossierMassActionsBean.class.getSimpleName());
	}

	/*
	 * ########################################################################## ################# Méthodes privées #
	 * ###################################### #####################################################
	 */

	/**
	 * Lance la procédure d'action de masse : intialise la liste des dossiers sélectionnés, vérifie qu'elle n'est pas
	 * null lance le parcourt des dossier, vérifie la feuille de route (au besoin ; voir checkFdrDossier) déverrouille
	 * le dossier (au besoin ; voir unlockDocIfLocked) puis parcourt les dossier link accessibles l'utilisateur et liés
	 * au dossier pour procéder à l'action de masse selectionnée (voir processSpecificMassAction)
	 * 
	 * @param action
	 */
	private void processMassAction(final MassActionType action) {
		String selectionList = null;
		try {
			// Initialisation de la liste des dossiers à reattribuer
			selectionList = initMasseFdrAction(isCheckAllotNecessary(action));
			if (selectionList == null) {
				return;
			}
			// Si l'utilisateur peut utiliser la fonctionnalité
			for (final Dossier dossier : dossiersOk) {
				// Set du contexte courant sur les documents de travail
				navigationContext.setCurrentDocument(dossier.getDocument());
				// Vérifie la feuille de route pour le dossier et le
				// déverrouille si besoin
				if (!checkFdrDossier(action, dossier) || !unlockDocIfLocked(dossier)) {
					addDossierInErrorList(dossier, ERROR_OCCURRED);
					doneMassDocument++;
					LOGGER.info(documentManager, STLogEnumImpl.FAIL_PROCESS_MASS_FONC, dossier.getDocument());
					continue;
				}
				for (final DocumentModel dossierLinkDoc : getCorbeilleService().findUpdatableDossierLinkForDossier(
						documentManager, dossier.getDocument())) {
					// On charge le dossier link
					final DossierLink dossierLink = dossierLinkDoc.getAdapter(DossierLink.class);
					corbeilleActions.setCurrentDossierLink(dossierLink);
					if (dossierDistributionActions.isDossierLinkLoaded()
							&& isActionCompatibleWithStep(massActionType, dossierLink)) {
						processSpecificMassAction(action, dossier, dossierLinkDoc);
					} else {
						addDossierInErrorList(dossier, ERROR_OCCURRED);
						LOGGER.info(documentManager, STLogEnumImpl.FAIL_PROCESS_MASS_FONC, dossierLinkDoc);
						break;
					}
				}
				doneMassDocument++;
			}
			endMassAction(selectionList);
		} catch (final Exception exc) {
			rollbackMassAction(exc);
		} finally {
			cleanAfterMassAction(selectionList);
		}
	}

	/**
	 * Lance l'action sur le dossier et dossier link en paramètre
	 * 
	 * @param action
	 * @param dossier
	 * @param dossierLinkDoc
	 * @throws ClientException
	 */
	private void processSpecificMassAction(final MassActionType action, final Dossier dossier,
			final DocumentModel dossierLinkDoc) throws ClientException {
		if (MassActionType.MASSE_ACTION_AVIS_FAVORABLE.equals(action)) {
			dossierDistributionActions.donnerAvisFavorable();
		} else if (MassActionType.MASSE_ACTION_NON_CONCERNE_REATTRIBUTION.equals(action)) {
			if (!routingActions.isNextStepReorientationOrReattributionOrArbitrage() && routingActions.isRootStep()) {
				// On ne valide l'étape que si la réattribution a pu se faire
				if (dossierDistributionActions.nonConcerneReattribution(selectedMinForReattribution)) {
					getDossierDistributionService().validerEtapeNonConcerne(documentManager, dossier.getDocument(),
							dossierLinkDoc);
				} else {
					addDossierInErrorList(dossier, ERROR_OCCURRED);
				}
			} else {
				addDossierInErrorList(dossier, ERROR_ETAPE);
			}
		} else if (MassActionType.MASSE_ACTION_MODIFICATION_MINISTERE_RATTACHEMENT.equals(action)
				|| MassActionType.MASSE_ACTION_MODIFICATION_DIRECTION_PILOTE.equals(action)) {
			if (!processModifyMinistereOrDirection(action)) {
				addDossierInErrorList(dossier, ERROR_OCCURRED);
			}
		} else if (MassActionType.MASSE_ACTION_NON_CONCERNE_REAFFECTATION.equals(action)) {
			if (!routingActions.isNextStepReorientationOrReattributionOrArbitrage() && routingActions.isRootStep()) {
				// On ne valide l'étape que si la réattribution a pu se faire
				if (dossierDistributionActions.nonConcerneReattribution(selectedMinForReattribution)) {
					getDossierDistributionService().validerEtapeNonConcerne(documentManager, dossier.getDocument(),
							dossierLinkDoc);
					getDossierDistributionService().addCommentAndStepForReaffectation(documentManager,
							dossier.getDocument());
				} else {
					addDossierInErrorList(dossier, ERROR_OCCURRED);
				}
			} else {
				addDossierInErrorList(dossier, ERROR_ETAPE);
			}
		} else if (MassActionType.MASSE_ACTION_NON_CONCERNE_REORIENTATION.equals(action)) {
			if (!routingActions.isNextStepReorientationOrReattributionOrArbitrage()
					&& !routingActions.isFirstStepInBranchOrParallel()) {
				dossierDistributionActions.nonConcerneReorientation();
				getDossierDistributionService().validerEtapeNonConcerne(documentManager, dossier.getDocument(),
						dossierLinkDoc);
			} else {
				addDossierInErrorList(dossier, ERROR_ETAPE);
			}
		} else if (MassActionType.MASSE_ACTION_DEMANDE_ARBITRAGE_SGG.equals(action)) {
			if (dossierDistributionActions.demandeArbitrageSGG()) {
				getDossierDistributionService().validerEtapeNonConcerne(documentManager, dossier.getDocument(),
						dossierLinkDoc);
			} else {
				addDossierInErrorList(dossier, ERROR_OCCURRED);
			}
		} else if (MassActionType.MASSE_ACTION_AVIS_DEFAVORABLE_ET_INSERER_TACHES.equals(action)) {
			if (!routingActions.isFirstStepInBranchOrParallel()) {
				dossierDistributionActions.donnerAvisDefavorableEtInsererTaches();
			} else {
				addDossierInErrorList(dossier, ERROR_ETAPE);
			}
		} else if (MassActionType.MASSE_ACTION_AVIS_DEFAVORABLE_ET_POURSUIVRE.equals(action)) {
			dossierDistributionActions.donnerAvisDefavorableEtPoursuivre();
		} else if (MassActionType.MASSE_ACTION_REATTRIBUTION_DIRECTE.equals(action)) {
			DossierLink dossierLink = dossierLinkDoc.getAdapter(DossierLink.class);
			if (!getReponsesArbitrageService().isStepPourArbitrage(dossierLink)) {
				getReponsesArbitrageService().reattributionDirecte(documentManager, dossierLink, dossier.getDocument(),
						reattributionMinistere, reattributionObservations);
			} else {
				addDossierInErrorList(dossier, ERROR_ETAPE);
			}
		}
	}

	private boolean processModifyMinistereOrDirection(MassActionType action) throws ClientException {
		reponseActions.resetReponse();
		dossierLockActions.lockCurrentDossier();
		Question question = getQuestionFromCurrentDossier();
		try {
			if (MassActionType.MASSE_ACTION_MODIFICATION_MINISTERE_RATTACHEMENT.equals(action)) {
				if (!checkObligatoryStringParameters(selectedMinForRattachement)) {
					infosErreursMassActions.add("feedback.reponses.dossier.error.selectMin");
					return false;
				}
				getDossierDistributionService().setMinistereRattachement(documentManager, question,
						selectedMinForRattachement);
			} else if (MassActionType.MASSE_ACTION_MODIFICATION_DIRECTION_PILOTE.equals(action)) {
				if (!checkObligatoryStringParameters(selectedDirectionPilote)) {
					infosErreursMassActions.add("feedback.reponses.dossier.error.selectDirectionPilote");
					return false;
				}
				getDossierDistributionService().setDirectionPilote(documentManager, question, selectedDirectionPilote);
			}
			documentManager.saveDocument(question.getDocument());
		} finally {
			dossierLockActions.unlockCurrentDossier();
		}
		return true;
	}

	/**
	 * En cas d'erreur dans l'action de masse, marque la transaction en rollback, affiche un message à l'utilisateur, et
	 * log l'exception
	 * 
	 * @param exc
	 */
	private void rollbackMassAction(Exception exc) {
		TransactionHelper.setTransactionRollbackOnly();
		facesMessages.add(StatusMessage.Severity.ERROR, resourcesAccessor.getMessages().get(ERROR_OCCURRED));
		LOGGER.error(documentManager, STLogEnumImpl.FAIL_PROCESS_MASS_FONC, exc);
	}

	/**
	 * Nettoyage des variables utilisées pour les actions de masse + déchargement du dossier link
	 * 
	 * @throws ClientException
	 */
	private void cleanAfterMassAction(String selectionList) {
		cancelMassAction();
		if (selectionList != null) {
			documentsListsManager.resetWorkingList(selectionList);
		}
	}

	/**
	 * Initialise les variables avant une action de masse
	 * 
	 * @return la selectionList utilisée
	 * @throws ClientException
	 */
	private String initMasseFdrAction(boolean checkAllotissement) throws ClientException {
		totalMassDocument = 0;
		doneMassDocument = 0;

		final String currentView = corbeilleActions.getCurrentView();
		// Choix de la liste de selection
		final String selectionList = DossierDistributionActionsBean.retrieveSelectionListAccordingView(currentView);
		if (selectionList == null) {
			infosErreursMassActions.add("feedback.reponses.dossier.error.selectedDossier");
			return null;
		}
		initDossierList(selectionList, checkAllotissement);
		return selectionList;
	}

	/**
	 * finalise l'action de masse. Reinitialise la workinglist, reset du document courant, et affiche un message
	 * d'erreur à l'utilisateur pour les dossiers qui ont été en erreur. Vide les listes des dossiers traités et en
	 * erreur
	 * 
	 * @param selectionList
	 */
	private void endMassAction(final String selectionList) {
		// Message de compte rendu en cas d'erreur
		if (!dossiersEnErreur.isEmpty()) {
			final List<String> messages = new ArrayList<String>();
			for (final Dossier dossier : dossiersEnErreur) {
				messages.add(dossier.getQuestion(documentManager).getSourceNumeroQuestion());
			}
			facesMessages.add(StatusMessage.Severity.WARN, "Dossiers en erreur : " + StringUtil.join(messages, ", "));
		}

		if (!infosErreursMassActions.isEmpty()) {
			StringBuilder messagesErreur = new StringBuilder();
			for (final String erreur : infosErreursMassActions) {
				messagesErreur.append(resourcesAccessor.getMessages().get(erreur)).append("\n");
			}
			facesMessages.add(StatusMessage.Severity.WARN, messagesErreur.toString());
		}
	}

	/**
	 * Vérifie la présence d'une feuille de route dans un dossier si l'action est différente d'une modification de
	 * ministère de rattachement ou direction pilote (ces deux actions ne nécessitent pas une feuille de route)
	 * 
	 * @param action
	 * @param dossier
	 * @return
	 */
	private boolean checkFdrDossier(MassActionType action, Dossier dossier) {
		if (!action.equals(MassActionType.MASSE_ACTION_MODIFICATION_MINISTERE_RATTACHEMENT)
				&& !action.equals(MassActionType.MASSE_ACTION_MODIFICATION_DIRECTION_PILOTE)
				&& !dossier.hasFeuilleRoute()) {
			LOGGER.warn(documentManager, SSLogEnumImpl.FAIL_GET_FDR_FONC, dossier.getDocument());
			infosErreursMassActions.add("feedback.reponses.dossier.error.noRouteFound");
			return false;
		}

		// Un dossier arbitré ne peut pas faire l'objet d'un nouvel arbitrage ou
		// reattribution
		if (dossier.isArbitrated()
				&& (action.equals(MassActionType.MASSE_ACTION_DEMANDE_ARBITRAGE_SGG)
						|| action.equals(MassActionType.MASSE_ACTION_NON_CONCERNE_REATTRIBUTION) || action
							.equals(MassActionType.MASSE_ACTION_NON_CONCERNE_REATTRIBUTION_PLAN_CLASSEMENT))) {
			LOGGER.warn(documentManager, ReponsesLogEnumImpl.FAIL_ADD_STEP_ARBITRAGE_FONC, dossier.getDocument());
			infosErreursMassActions.add("feedback.reponses.dossier.error.cannotAddStepToRoute");
			return false;
		}

		// Un dossier non arbitré ne peut pas faire l'objet d'une réattribution
		// directe
		if (!dossier.isArbitrated() && action.equals(MassActionType.MASSE_ACTION_REATTRIBUTION_DIRECTE)) {
			LOGGER.warn(documentManager, SSLogEnumImpl.FAIL_REATTR_DOSSIER_FONC, dossier.getDocument());
			infosErreursMassActions.add("feedback.reponses.dossier.error.cannotReattributionDirecte");
			return false;
		}

		return true;
	}

	/**
	 * Déverrouille un dossier s'il est verrouillé
	 * 
	 * @param dossier
	 * @return
	 * @throws ClientException
	 */
	private boolean unlockDocIfLocked(Dossier dossier) throws ClientException {
		if (dossier.getDocument().isLocked()) {
			if (stLockActions.currentDocIsLockActionnableByCurrentUser()
					|| ssPrincipal.isMemberOf(ReponsesBaseFunctionConstant.DOSSIER_ADMIN_UNLOCKER)) {
				dossierLockActions.unlockCurrentDossier();
			}
			if (!dossierLockActions.getCanLockCurrentDossier()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * initialise la liste des dossiers à traiter pour l'action de masse.
	 * 
	 * @param selectionList
	 * @throws ClientException
	 */
	protected void initDossierList(final String selectionList, boolean checkAllotissement) throws ClientException {
		final AllotissementService allotissementService = getAllotissementService();
		final List<DocumentModel> selection = documentsListsManager.getWorkingList(selectionList);

		final Set<String> idDossiers = new HashSet<String>();

		for (final DocumentModel selectedDoc : selection) {
			final DossierCommon dossierCommon = selectedDoc.getAdapter(DossierCommon.class);
			final Dossier dossier = dossierCommon.getDossier(documentManager);
			if (checkAllotissement) {
				if (!idDossiers.contains(dossier.getDocument().getId())) {
					final Allotissement allotissement = allotissementService.getAllotissement(dossier.getDossierLot(),
							documentManager);
					if (allotissement != null) {
						idDossiers.addAll(allotissement.getIdDossiers());
					} else {
						idDossiers.add(dossier.getDocument().getId());
					}
					dossiersOk.add(dossier);
				}
			} else {
				idDossiers.add(dossier.getDocument().getId());
				dossiersOk.add(dossier);
			}
		}
		totalMassDocument = idDossiers.size();
	}

	/**
	 * Vérifie les paramètres String obligatoires (l'id ministère par exemple)
	 * 
	 * @return
	 */
	private boolean checkObligatoryStringParameters(String... parameters) {
		for (String parameter : parameters) {
			if (StringUtil.isEmpty(parameter)) {
				infosErreursMassActions.add("feedback.reponses.error.missing.parameter");
				return false;
			}
		}
		return true;
	}

	/**
	 * Ajoute un dossier à la liste des dossiers en erreur et l'origine de l'erreur
	 * 
	 * @param dossier
	 * @param resourcesMessage
	 */
	private void addDossierInErrorList(Dossier dossier, String resourcesMessage) {
		dossiersEnErreur.add(dossier);
		infosErreursMassActions.add(resourcesMessage);
	}

	private boolean isCheckAllotNecessary(MassActionType massActionType) {
		if (MassActionType.MASSE_ACTION_MODIFICATION_DIRECTION_PILOTE.equals(massActionType)
				|| MassActionType.MASSE_ACTION_MODIFICATION_MINISTERE_RATTACHEMENT.equals(massActionType)) {
			return false;
		}
		return true;
	}

	/**
	 * Récupère la Question du currentDossier
	 * 
	 * @return
	 */
	private Question getQuestionFromCurrentDossier() {
		final DocumentModel dossierDoc = navigationContext.getCurrentDocument();
		Dossier dossier = dossierDoc.getAdapter(Dossier.class);
		return dossier.getQuestion(documentManager);
	}

	private ReponsesArbitrageService getReponsesArbitrageService() {
		if (arbitrageService == null) {
			arbitrageService = ReponsesServiceLocator.getReponsesArbitrageService();
		}
		return arbitrageService;
	}

	private DossierDistributionService getDossierDistributionService() {
		if (dossierDistributionService == null) {
			dossierDistributionService = ReponsesServiceLocator.getDossierDistributionService();
		}
		return dossierDistributionService;
	}

	private CorbeilleService getCorbeilleService() {
		if (corbeilleService == null) {
			corbeilleService = ReponsesServiceLocator.getCorbeilleService();
		}
		return corbeilleService;
	}

	private AllotissementService getAllotissementService() {
		if (allotissementService == null) {
			allotissementService = ReponsesServiceLocator.getAllotissementService();
		}
		return allotissementService;
	}
}
