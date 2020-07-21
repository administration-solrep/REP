package fr.dila.ss.web.feuilleroute;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.actions.ActionContext;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.webapp.helpers.EventNames;

import fr.dila.st.api.constant.STActionConstant;

/**
 * WebBean qui surcharge et étend celui de Nuxeo.
 *
 * @author jtremeaux
 */
@Name("routingWebActions")
@Scope(CONVERSATION)
@Install(precedence = Install.FRAMEWORK + 1)
public class DocumentRoutingWebActionsBean extends fr.dila.ecm.platform.routing.web.DocumentRoutingWebActionsBean implements Serializable {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    @In(create = true, required = false)
    protected transient NavigationContext navigationContext;

    /**
     * Liste des actions pour coller des étapes avant / après une étape.
     */
    protected List<Action> pasteStepActions;

    /**
     * Liste des actions pour coller des étapes dans un conteneur.
     */
    protected List<Action> pasteStepInForkActions;

    /**
     * Vue permettant d'afficher la feuille de route, permet le retour d'une étape vers le bon espace.
     */
    protected String feuilleRouteView;
    
    /**
	 * Default constructor
	 */
	public DocumentRoutingWebActionsBean(){
		super();
	}
    
    /**
     * Retourne la liste des actions pour coller des étapes avant / après une étape.
     * 
     * @param step Étape cible
     * @return Liste d'actions
     */
    public List<Action> getPasteStepActions(DocumentModel step) {
        ActionContext context = actionContextProvider.createActionContext();
        context.setCurrentDocument(step);
        addStepActions = webActions.getActionsList(STActionConstant.ROUTING_TASK_PASTE_STEP_ACTIONS_LIST, context);
        return addStepActions;
    }

    /**
     * Retourne la liste des actions pour coller des étapes dans un conteneur.
     * 
     * @param step Conteneur cible
     * @return Liste d'actions
     */
    public List<Action> getPasteStepInForkActions(DocumentModel step) {
        ActionContext context = actionContextProvider.createActionContext();
        context.setCurrentDocument(step);
        pasteStepInForkActions = webActions.getActionsList(STActionConstant.ROUTING_TASK_PASTE_STEP_IN_FORK_ACTIONS_LIST, context);
        return pasteStepInForkActions;
    }

    @Observer(value = { EventNames.USER_ALL_DOCUMENT_TYPES_SELECTION_CHANGED,
            EventNames.LOCATION_SELECTION_CHANGED }, create = false)
    @BypassInterceptors
    @Override
    public void resetTabList() {
        super.resetTabList();
        
        pasteStepActions = null;
        pasteStepInForkActions = null;
    }

    /**
     * Getter de feuilleRouteView.
     *
     * @return feuilleRouteView
     */
    public String getFeuilleRouteView() {
        return feuilleRouteView;
    }

    /**
     * Setter de feuilleRouteView.
     *
     * @param feuilleRouteView feuilleRouteView
     */
    public void setFeuilleRouteView(String feuilleRouteView) {
        this.feuilleRouteView = feuilleRouteView;
    }
}
