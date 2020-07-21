package fr.dila.reponses.web.planclassement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.actions.ejb.ActionManager;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManagerBean;

import fr.dila.reponses.api.constant.ReponsesActionConstant;
import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.reponses.api.constant.ReponsesViewConstant;
import fr.dila.reponses.api.service.CorbeilleService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.web.corbeille.CorbeilleActionsBean;
import fr.dila.reponses.web.corbeille.PlanClassementTreeNiveau1Node;
import fr.dila.ss.web.feuilleroute.DocumentRoutingWebActionsBean;
import fr.dila.st.web.action.NavigationWebActionsBean;
import fr.dila.st.web.context.NavigationContextBean;

/**
 * Bean Seam permettant de gérer le plan de classement.
 * 
 * @author jtremeaux
 */
@Name("planClassementActions")
@Scope(ScopeType.CONVERSATION)
public class PlanClassementActionsBean implements Serializable {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = -8870324536789519518L;

    @In(create = true, required = false)
    protected transient ActionManager actionManager;

    @In(create = true)
    protected transient NavigationWebActionsBean navigationWebActions;

    @In(create = true, required = false)
    protected transient CorbeilleActionsBean corbeilleActions;

    @In(create = true, required = false)
    protected transient DocumentRoutingWebActionsBean routingWebActions;

    @In(create = true, required = false)
    protected transient NavigationContextBean navigationContext;

    @In(create = true, required = true)
    protected transient CoreSession documentManager;

    @In(create = true)
    protected transient DocumentsListsManagerBean documentsListsManager;

    /**
     * Navigue vers l'espace plan de classement
     * 
     * @return Ecran d'accueil du plan de classement
     * @throws ClientException
     */
    public String navigateToEspacePlanClassement() throws ClientException {
        // Décharge le dossier courant
        navigationContext.resetCurrentDocument();

        // Renseigne le menu du haut
        Action mainMenuAction = actionManager.getAction(ReponsesActionConstant.ESPACE_PLAN_CLASSEMENT);
        navigationWebActions.setCurrentMainMenuAction(mainMenuAction);
        navigationWebActions.setLeftPanelIsOpened(true);
        // Charge le menu de gauche
        Action action = actionManager.getAction(ReponsesConstant.LEFT_MENU_PLAN_CLASSEMENT_ACTION);
        navigationWebActions.setCurrentLeftMenuAction(action);

        // Déplie le panneau des résultats de recherche
        navigationWebActions.setUpperPanelIsOpened(true);

        // Renseigne la vue de route des étapes de feuille de route vers les dossiers et la vue courante
        corbeilleActions.setCurrentView(ReponsesViewConstant.PLAN_CLASSEMENT_VIEW);
        routingWebActions.setFeuilleRouteView(ReponsesViewConstant.PLAN_CLASSEMENT_VIEW);

        documentsListsManager.resetWorkingList(ReponsesConstant.PLAN_CLASSEMENT_SELECTION);

        return ReponsesViewConstant.PLAN_CLASSEMENT_VIEW;
    }

    public List<PlanClassementTreeNiveau1Node> getPlanClassementNiveau1(String treeMode) throws ClientException {
        final CorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();

        ArrayList<PlanClassementTreeNiveau1Node> list = new ArrayList<PlanClassementTreeNiveau1Node>();
        Map<String, Integer> niveaux1 = corbeilleService.getPlanClassementNiveau1(documentManager, treeMode);

        for (Map.Entry<String, Integer> entry : niveaux1.entrySet()) {
            list.add(new PlanClassementTreeNiveau1Node(entry.getKey(), entry.getKey(), entry.getValue().toString()));
        }

        return list;
    }

    public Map<String, Integer> getPlanClassementNiveau2(String treeMode, String indexation) throws ClientException {
        final CorbeilleService corbeilleService = ReponsesServiceLocator.getCorbeilleService();

        Map<String, Integer> niveaux2 = corbeilleService.getPlanClassementNiveau2(documentManager, treeMode, indexation);

        return niveaux2;
    }
}
