package fr.dila.ss.web.admin;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.actions.ejb.ActionManager;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;

import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.service.FeuilleRouteModelService;
import fr.dila.ss.core.security.principal.SSPrincipalImpl;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.web.admin.utilisateur.UserManagerActionsBean;
import fr.dila.ss.web.feuilleroute.DocumentRoutingWebActionsBean;
import fr.dila.st.api.administration.EtatApplication;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STContentViewConstant;
import fr.dila.st.api.constant.STViewConstant;
import fr.dila.st.api.service.EtatApplicationService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.web.action.NavigationWebActionsBean;
import fr.dila.st.web.administration.organigramme.OrganigrammeTreeBean;
import fr.dila.st.web.context.NavigationContextBean;

/**
 * Bean Seam de l'espace d'administration.
 * 
 * @author jtremeaux
 */
@Name("administrationActions")
@Scope(ScopeType.SESSION)
@Install(precedence = Install.FRAMEWORK)
public abstract class AdministrationActionsBean implements Serializable {

	/**
	 * Serial UID.
	 */
	private static final long							serialVersionUID		= 956631279709404171L;

	protected static final String						LEFT_MENU_ADMIN_ACTION	= "LEFT_MENU_ADMIN";

	protected static final String						MAIN_MENU_ADMIN_ACTION	= "espace_administration";

	@In(required = true, create = true)
	protected SSPrincipalImpl							ssPrincipal;

	@In(create = true)
	protected transient UserManagerActionsBean			userManagerActions;

	@In(required = true)
	protected transient NavigationContextBean			navigationContext;

	@In(required = false, create = true)
	protected transient CoreSession						documentManager;

	@In(create = true, required = false)
	protected transient ActionManager					actionManager;

	@In(create = true, required = false)
	protected transient ContentViewActions				contentViewActions;

	@In(create = true)
	protected transient NavigationWebActionsBean		navigationWebActions;

	@In(create = true, required = false)
	protected transient DocumentRoutingWebActionsBean	routingWebActions;

	@In(create = true)
	protected transient OrganigrammeTreeBean			organigrammeTree;

	/**
	 * Initialise le contexte de l'espace d'administration.
	 */
	protected void initEspaceAdministration() throws ClientException {
		// Renseigne le menu du haut
		Action mainMenuAction = actionManager.getAction(MAIN_MENU_ADMIN_ACTION);
		navigationWebActions.setCurrentMainMenuAction(mainMenuAction);

		// Renseigne le menu de gauche
		Action leftMenuAction = actionManager.getAction(LEFT_MENU_ADMIN_ACTION);
		navigationWebActions.setCurrentLeftMenuAction(leftMenuAction);

		// Réinitialise le document en cours
		navigationContext.resetCurrentDocument();
	}

	/**
	 * Détermine si l'utilisateur est dans l'espace d'administration.
	 * 
	 * @return Vrai si l'utilisateur est dans l'espace d'administration
	 * @throws ClientException
	 */
	public boolean isInEspaceAdministration() throws ClientException {
		Action currentMainMenuAction = navigationWebActions.getCurrentMainMenuAction();
		return currentMainMenuAction != null && MAIN_MENU_ADMIN_ACTION.equals(currentMainMenuAction.getId());
	}

	public boolean isAccessAuthorized() {
		SSPrincipal ssPrincipal = (SSPrincipal) documentManager.getPrincipal();
		return (ssPrincipal.isAdministrator() || ssPrincipal
				.isMemberOf(STBaseFunctionConstant.ADMINISTRATION_PARAMETRE_APPLICATION_READER));
	}

	// ***********************************************************************
	// Fonctions de navigation
	// ***********************************************************************
	/**
	 * Navigue vers l'espace d'administration.
	 * 
	 * @return Écran d'accueil de l'espace d'administration
	 * @throws ClientException
	 */
	public String navigateToEspaceAdministration() throws ClientException {
		// Initialise le contexte de l'espace d'administration
		initEspaceAdministration();
		navigationWebActions.setCurrentLeftMenuItemAction(null);
		return STViewConstant.EMPTY_VIEW;
	}

	/**
	 * Navigue vers l'écran de gestion des modèles de feuilles de route.
	 * 
	 * @return Écran de gestion des modèles de feuilles de route
	 * @throws ClientException
	 */
	public String navigateToModeleFeuilleRouteFolder() throws ClientException {
		// Initialise le contexte de l'espace d'administration
		initEspaceAdministration();
		navigationWebActions.setCurrentLeftMenuItemAction(actionManager.getAction("admin_feuille_route_modele"));

		// Navigue vers la racine des modèles de feuilles de route
		final FeuilleRouteModelService feuilleRouteModelService = SSServiceLocator.getFeuilleRouteModelService();
		DocumentModel currentDocument = feuilleRouteModelService.getFeuilleRouteModelFolder(documentManager);
		navigationContext.setCurrentDocument(currentDocument);

		// Renseigne la vue de retour des feuilles de routes
		routingWebActions.setFeuilleRouteView(STViewConstant.MODELE_FEUILLE_ROUTE_VIEW);

		return STViewConstant.MODELES_FEUILLE_ROUTE_VIEW;
	}

	/**
	 * Navigue vers l'écran de gestion de l'organigramme.
	 * 
	 * @return Écran de gestion de l'organigramme
	 * @throws ClientException
	 */
	public String navigateToOrganigramme() throws ClientException {
		// Initialise le contexte de l'espace d'administration
		initEspaceAdministration();

		navigationWebActions.setCurrentLeftMenuItemAction(actionManager.getAction("admin_utilisateur_organigramme"));
		organigrammeTree.cleanTree();
		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	/**
	 * Navigue vers l'écran de gestion des utilisateurs.
	 * 
	 * @return Écran de gestion de l'organigramme
	 * @throws ClientException
	 */
	public String navigateToUtilisateur() throws ClientException {
		// Initialise le contexte de l'espace d'administration
		initEspaceAdministration();

		navigationWebActions.setCurrentLeftMenuItemAction(actionManager.getAction("admin_utilisateur_utilisateur"));
		organigrammeTree.cleanTree();
		userManagerActions.clearSearch();
		return userManagerActions.viewUsers();
	}

	/**
	 * Navigue vers l'écran de gestion des paramètres.
	 * 
	 * @return Écran de gestion des paramètres
	 * @throws ClientException
	 */
	public String navigateToParametreFolder() throws ClientException {
		// Initialise le contexte de l'espace d'administration
		initEspaceAdministration();
		navigationWebActions.setCurrentLeftMenuItemAction(actionManager.getAction("admin_param_param"));

		// Définit la racine des modèles de feuilles de route comme document courant
		final STParametreService paramService = STServiceLocator.getSTParametreService();
		DocumentModel currentDocument = paramService.getParametreFolder(documentManager);
		// navigationContext.setCurrentDocument(currentDocument);

		// Invalide la liste des feuilles de route
		// contentViewActions.reset(STContentView.FEUILLE_ROUTE_MODEL_FOLDER_CONTENT_VIEW);

		return navigationContext.navigateToDocument(currentDocument);
	}
	
	/**
	 * Retourne vers l'écran de gestion des paramètres parent.
	 * 
	 * @return Écran de gestion des paramètres parent
	 * @throws ClientException
	 */
	public String navigateToParentFolder() throws ClientException {
		DocumentModel currentDocument = navigationContext.getCurrentDocument();
		DocumentModel parentDoc = documentManager.getParentDocument(currentDocument.getRef());
		return navigationContext.navigateToDocument(parentDoc);
	}

	public String navigateToEditEtatApp() throws ClientException {
		// Initialise le contexte de l'espace d'administration
		initEspaceAdministration();
		navigationWebActions.setCurrentLeftMenuItemAction(actionManager.getAction("admin_param_etat_app"));

		EtatApplicationService etatApplicationService = STServiceLocator.getEtatApplicationService();
		EtatApplication etatApplication = etatApplicationService.getEtatApplicationDocument(documentManager);
		// Navigue vers la racine des modèles de feuilles de route

		navigationContext.setCurrentDocument(etatApplication.getDocument());

		return STViewConstant.ETAT_APPLICATION_VIEW;
	}

	/**
	 * Navigue vers l'écran de gestion des utilisateurs.
	 * 
	 * @return Écran de gestion de l'organigramme
	 * @throws ClientException
	 */
	public String navigateToUtilisateurSendMail() throws ClientException {
		// Initialise le contexte de l'espace d'administration
		initEspaceAdministration();

		navigationWebActions.setCurrentLeftMenuItemAction(actionManager.getAction("admin_utilisateur_utilisateur"));
		organigrammeTree.cleanTree();
		return "view_recherche_send_mail_to_user";
	}

	/**
	 * Navigue vers l'écran de gestion des utilisateurs.
	 * 
	 * @return Écran de gestion de l'organigramme
	 * @throws ClientException
	 */
	public String navigateToDeleteUtilisateur() throws ClientException {
		// Initialise le contexte de l'espace d'administration
		initEspaceAdministration();

		organigrammeTree.cleanTree();
		userManagerActions.deleteSelectedUsers();
		Events.instance().raiseEvent("documentChanged");
		return navigationContext.getCurrentView();
	}
}
