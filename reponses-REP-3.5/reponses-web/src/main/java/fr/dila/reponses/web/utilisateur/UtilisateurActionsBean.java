package fr.dila.reponses.web.utilisateur;

import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.actions.ejb.ActionManager;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;

import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.constant.STViewConstant;
import fr.dila.st.api.service.DelegationService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.web.action.NavigationWebActionsBean;
import fr.dila.st.web.administration.organigramme.OrganigrammeTreeBean;

/**
 * WebBean permettant de gérer l'espace utilisateur.
 * 
 * @author jtremeaux
 */
@Name("utilisateurActions")
@Scope(ScopeType.SESSION)
@Install(precedence = FRAMEWORK)
public class UtilisateurActionsBean implements Serializable {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 2765843431215967501L;

    protected static final String LEFT_MENU_UTILISATEUR_ACTION = "LEFT_MENU_UTILISATEUR";

    protected static final String MAIN_MENU_UTILISATEUR_ACTION = "espace_utilisateur";

    @In(create = true)
    protected transient UserManagerActionsBean userManagerActions;

    @In(required = true, create = true)
    protected transient CoreSession documentManager;

    @In(required = true, create = true)
    protected transient SSPrincipal ssPrincipal;

    @In(create = true)
    protected transient OrganigrammeTreeBean organigrammeTree;

    @In(required = true)
    protected transient NavigationContext navigationContext;

    @In(create = true, required = false)
    protected transient ActionManager actionManager;

    @In(create = true)
    protected transient NavigationWebActionsBean navigationWebActions;

    /**
     * Initialise le contexte de l'espace d'administration.
     */
    protected void initEspaceUtilisateur() {
        navigationWebActions.setLeftPanelIsOpened(true);
        Action action = actionManager.getAction(LEFT_MENU_UTILISATEUR_ACTION);
        navigationWebActions.setCurrentLeftMenuAction(action);
        action = actionManager.getAction(MAIN_MENU_UTILISATEUR_ACTION);
        navigationWebActions.setCurrentMainMenuAction(action);
        navigationWebActions.setCurrentLeftMenuItemAction(null);
    }

    // ***********************************************************************
    // Fonctions de navigation
    // ***********************************************************************
    /**
     * Navigue vers l'espace utilisateur.
     * 
     * @return Écran d'accueil de l'espace utilisateur
     * @throws ClientException
     */
    public String navigateToEspaceUtilisateur() throws ClientException {
        // Initialise le contexte de l'espace utilisateur
        initEspaceUtilisateur();

        return STViewConstant.EMPTY_VIEW;
    }

    /**
     * Navigue vers l'écran de l'organigramme.
     * 
     * @return Écran de l'organigramme
     * @throws ClientException
     */
    public String navigateToOrganigramme() throws ClientException {
        // Initialise le contexte de l'espace utilisateur
        initEspaceUtilisateur();
        navigationWebActions.setCurrentLeftMenuItemAction(actionManager.getAction("utilisateur_organigramme"));

        organigrammeTree.cleanTree();
        return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
    }

    /**
     * Navigue vers l'écran de gestion de profil.
     * 
     * @return Écran de profil
     * @throws ClientException
     */
    public String navigateToMonCompte() throws ClientException {
        // Initialise le contexte de l'espace utilisateur
        initEspaceUtilisateur();
        navigationWebActions.setCurrentLeftMenuItemAction(actionManager.getAction("utilisateur_mon_compte"));

        return userManagerActions.viewMonCompte(ssPrincipal.getName());
    }

    /**
     * Navigue vers l'écran de délégation des droits.
     * 
     * @return Écran de délégation des droits
     * @throws ClientException
     */
    public String navigateToDelegation() throws ClientException {
        // Initialise le contexte de l'espace d'administration
        initEspaceUtilisateur();
        navigationWebActions.setCurrentLeftMenuItemAction(actionManager.getAction("utilisateur_delegation"));

        // Navigue vers la racine des délégations
        final DelegationService delegationService = STServiceLocator.getDelegationService();
        DocumentModel delegationRoot = delegationService.getDelegationRoot(documentManager);
        return navigationContext.navigateToDocument(delegationRoot);
    }

    /**
     * Navigue vers l'écran de création d'un utilisateur occasionnel
     * 
     * @return Écran de profil
     * @throws ClientException
     */
    public String navigateToCreateUserOccasional() throws ClientException {
        // Initialise le contexte de l'espace utilisateur
        initEspaceUtilisateur();
        navigationWebActions.setCurrentLeftMenuItemAction(actionManager.getAction("utilisateur_creation"));

        return userManagerActions.getCreateUserOccasionalView();
    }

    /**
     * Navigue vers l'écran des utilisateurs.
     * 
     * @return Écran des utilisateurs
     * @throws ClientException
     */
    public String navigateToUtilisateur() throws ClientException {
        // Initialise le contexte de l'espace utilisateur
        initEspaceUtilisateur();
        navigationWebActions.setCurrentLeftMenuItemAction(actionManager.getAction("utilisateur_utilisateur"));
        userManagerActions.clearSearch();
        return userManagerActions.viewUsers();
    }
}
