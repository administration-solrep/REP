package fr.dila.reponses.web.administration;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.actions.Action;

import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.ReponsesViewConstant;
import fr.dila.reponses.web.administration.delegation.DelegationActionsBean;
import fr.dila.reponses.web.corbeille.CorbeilleActionsBean;
import fr.dila.reponses.web.supervision.SupervisionActionsBean;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.service.FeuilleRouteModelService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.web.admin.utilisateur.UserManagerActionsBean;
import fr.dila.st.api.constant.STViewConstant;

/**
 * Bean Seam de l'espace d'administration.
 * 
 * @author FEO
 */
@Name("administrationActions")
@Scope(ScopeType.SESSION)
@Install(precedence = Install.FRAMEWORK + 1)
public class AdministrationActionsBean extends fr.dila.ss.web.admin.AdministrationActionsBean implements Serializable {

	/**
	 * Serial UID.
	 */
	private static final long						serialVersionUID	= -6185249454872984281L;

	@In(create = true, required = false)
	private transient CorbeilleActionsBean			corbeilleActions;

	@In(create = true)
	private transient MigrationManagerActionsBean	migrationManagerActions;

	@In(create = true)
	private transient UserManagerActionsBean		userManagerActions;

	@In(required = true, create = true)
	private transient DelegationActionsBean			delegationActions;

	@In(required = true, create = true)
	private transient SSPrincipal					ssPrincipal;

	@In(create = true, required = false)
	private transient SupervisionActionsBean		supervisionActions;

	// ***********************************************************************
	// Fonctions de navigation
	// ***********************************************************************
	protected void initEspaceAdministration() throws ClientException {
		corbeilleActions.setCurrentView(ReponsesViewConstant.ARCHIVAGE_VIEW_MANAGE);
		navigationWebActions.setLeftPanelIsOpened(true);
		super.initEspaceAdministration();
	}

	/**
	 * Navigue vers l'écran Mon compte
	 * 
	 * @return Écran de gestion de compte
	 * @throws ClientException
	 */
	public String navigateToMonCompte() throws ClientException {
		// Initialise le contexte de l'espace d'administration
		initEspaceAdministration();
		navigationWebActions.setCurrentLeftMenuItemAction(actionManager.getAction("admin_mon_compte"));

		return userManagerActions.viewMonCompte(ssPrincipal.getName());
	}

	/**
	 * Navigue vers l'écran d'archivage
	 * 
	 * @return Écran d'archivage
	 * @throws ClientException
	 */
	public String navigateToArchivage() throws ClientException {
		// Initialise le contexte de l'espace d'administration
		initEspaceAdministration();
		navigationWebActions.setCurrentLeftMenuItemAction(actionManager.getAction("admin_archivage"));

		return ReponsesViewConstant.ARCHIVAGE_VIEW_MANAGE;
	}

	/**
	 * Navigue vers la recherche de modèles de feuille de route
	 * 
	 * @return Écran de recherche de modèles de feuille de route
	 * @throws ClientException
	 */
	public String navigateToModeleFeuilleRouteSearch() throws ClientException {
		// Initialise le contexte de l'espace d'administration
		initEspaceAdministration();
		navigationWebActions.setCurrentLeftMenuItemAction(actionManager.getAction("admin_feuille_route_modele_search"));

		return ReponsesViewConstant.RECHERCHE_MODELE_FDR_VIEW;
	}

	@Override
	public String navigateToModeleFeuilleRouteFolder() throws ClientException {
		// Retour vers la recherche si on viens d'une feuille de route
		Action action = navigationWebActions.getCurrentLeftMenuItemAction();
		if (action != null && action.getId().equals("admin_feuille_route_modele_search")
				&& navigationContext.getCurrentDocument() != null
				&& navigationContext.getCurrentDocument().getType().equals("FeuilleRoute")) {
			// Navigue vers la racine des modèles de feuilles de route
			final FeuilleRouteModelService feuilleRouteModelService = SSServiceLocator.getFeuilleRouteModelService();
			DocumentModel currentDocument = feuilleRouteModelService.getFeuilleRouteModelFolder(documentManager);
			navigationContext.setCurrentDocument(currentDocument);

			// Renseigne la vue de retour des feuilles de routes
			routingWebActions.setFeuilleRouteView(STViewConstant.MODELE_FEUILLE_ROUTE_VIEW);

			return ReponsesViewConstant.RECHERCHE_MODELE_FEUILLE_ROUTE_RESULTATS;
		}

		// Navigation vers l'administration des modèles de feuille de route
		return super.navigateToModeleFeuilleRouteFolder();
	}

	/**
	 * Navigue vers l'écran de gestion du journal d'administration.
	 * 
	 * @return Écran de gestion du journal d'administration
	 * @throws ClientException
	 */
	public String navigateToJournal() throws ClientException {
		// Initialise le contexte de l'espace d'administration
		initEspaceAdministration();
		navigationWebActions.setCurrentLeftMenuItemAction(actionManager.getAction("admin_journal_journal"));

		return STViewConstant.JOURNAL_VIEW;
	}

	/**
	 * Navigue vers l'écran de recherche des utilisateurs.
	 * 
	 * @return Écran de recherche des utilisateurs
	 * @throws ClientException
	 */
	public String navigateToRechercheUtilisateur() throws ClientException {
		// Initialise le contexte de l'espace d'administration
		initEspaceAdministration();
		userManagerActions.resetSearch();
		navigationWebActions.setCurrentLeftMenuItemAction(actionManager.getAction("admin_utilisateur_recherche"));

		return STViewConstant.RECHERCHE_UTILISATEUR_VIEW;
	}

	/**
	 * Navigue vers l'écran de délégation des droits.
	 * 
	 * @return Écran de délégation des droits
	 * @throws ClientException
	 */
	public String navigateToDelegation() throws ClientException {
		// Initialise le contexte de l'espace d'administration
		initEspaceAdministration();
		navigationWebActions.setCurrentLeftMenuItemAction(actionManager.getAction("admin_utilisateur_delegation"));

		// Navigue vers la racine des délégations
		DocumentModel delegationRoot = delegationActions.getDelegationRoot();

		return navigationContext.navigateToDocument(delegationRoot);
	}

	/**
	 * Navigue vers l'écran de Supervision des connexions utilisateurs.
	 * 
	 * @return Écran de Supervision des connexions utilisateurs
	 * @throws ClientException
	 */
	public String navigateToSupervision() throws ClientException {
		// Initialise le contexte de l'espace d'administration
		initEspaceAdministration();
		navigationWebActions.setCurrentLeftMenuItemAction(actionManager.getAction("admin_utilisateur_supervision"));
		// On fait un reset de la liste d'utilisateurs
		supervisionActions.resetUserList();
		// Et ensuite on renseigne de nouveau la liste d'utilisateurs -> Permet de rafraichir les données de la liste
		supervisionActions.getUserList();
		return ReponsesViewConstant.VIEW_SUPERVISION;
	}

	/**
	 * Navigue vers l'écran des logs des mises à jour des timbres
	 * 
	 * @return écran des logs des mises à jour des timbres
	 * @throws ClientException
	 */
	public String navigateToViewUpdateTimbre() throws ClientException {
		// Initialise le contexte de l'espace d'administration
		initEspaceAdministration();
		navigationWebActions.setCurrentLeftMenuItemAction(actionManager
				.getAction("admin_utilisateur_organigramme_viewlogs"));
		migrationManagerActions.setCurrentLog(null);
		return ReponsesViewConstant.VIEW_LOGS_TIMBRES;
	}

	/**
	 * Navigue vers l'écran des logs des mises à jour des timbres
	 * 
	 * @return écran des logs des mises à jour des timbres
	 * @throws ClientException
	 */
	public String navigateToViewTimbre() throws ClientException {
		// Initialise le contexte de l'espace d'administration
		initEspaceAdministration();
		navigationWebActions.setCurrentLeftMenuItemAction(actionManager
				.getAction("admin_utilisateur_organigramme_viewlogs"));
		return ReponsesViewConstant.VIEW_TIMBRES;
	}

	/**
	 * Navigue vers l'écran des logs d'exécution des batchs
	 * 
	 * @return écran des logs des batchs
	 * @throws ClientException
	 */
	public String navigateToViewBatchSuivi() throws ClientException {
		// Initialise le contexte de l'espace d'administration
		initEspaceAdministration();
		navigationWebActions.setCurrentLeftMenuItemAction(actionManager.getAction("admin_batch_viewSuivi"));
		return STViewConstant.BATCH_SUIVI_VIEW;
	}

	/**
	 * Navigue vers l'écran des planifications d'exécution des batchs
	 * 
	 * @return écran des planifications des batchs
	 * @throws ClientException
	 */
	public String navigateToViewBatchSuiviPlanification() throws ClientException {
		// Initialise le contexte de l'espace d'administration
		initEspaceAdministration();
		navigationWebActions
				.setCurrentLeftMenuItemAction(actionManager.getAction("admin_batch_viewSuiviPlanification"));
		return STViewConstant.BATCH_SUIVI_PLANIFICATION;
	}

	/**
	 * Navigue vers l'écran des notifications de suivi des batchs
	 * 
	 * @return écran des notifications
	 * @throws ClientException
	 */
	public String navigateToViewBatchSuiviNotification() throws ClientException {
		// Initialise le contexte de l'espace d'administration
		initEspaceAdministration();
		navigationWebActions.setCurrentLeftMenuItemAction(actionManager.getAction("admin_batch_viewSuiviNotification"));
		return STViewConstant.BATCH_SUIVI_NOTIFICATION;
	}

	/**
	 * Controle l'accès au paramétrage de l'application on utilise le droit de l'accès restreint
	 * */
	public boolean isAccessAuthorized() {
		SSPrincipal ssPrincipal = (SSPrincipal) documentManager.getPrincipal();
		return (ssPrincipal.isAdministrator() || ssPrincipal
				.isMemberOf(ReponsesBaseFunctionConstant.ADMIN_ACCESS_UNRESTRICTED));
	}

}
