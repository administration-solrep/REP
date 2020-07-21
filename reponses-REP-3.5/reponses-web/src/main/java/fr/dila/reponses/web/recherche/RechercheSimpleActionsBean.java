package fr.dila.reponses.web.recherche;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.actions.ejb.ActionManager;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManagerBean;

import fr.dila.reponses.api.constant.ReponsesActionConstant;
import fr.dila.reponses.api.constant.ReponsesContentView;
import fr.dila.reponses.api.constant.ReponsesParametreConstant;
import fr.dila.reponses.api.constant.ReponsesViewConstant;
import fr.dila.reponses.core.recherche.query.SimpleSearchQueryParser;
import fr.dila.reponses.web.corbeille.CorbeilleActionsBean;
import fr.dila.ss.web.feuilleroute.DocumentRoutingWebActionsBean;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.web.action.NavigationWebActionsBean;
import fr.dila.st.web.context.NavigationContextBean;

/**
 * Une classe bean pour les traitements spécifiques à la recherche simple.
 * 
 * @author jgomez
 */
@Name("rechercheSimpleActions")
@Scope(ScopeType.CONVERSATION)
public class RechercheSimpleActionsBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(RechercheSimpleActionsBean.class);

    @In(create = true, required = true)
    protected ContentViewActions contentViewActions;

    @In(create = true, required = true)
    protected transient ActionManager actionManager;

    @In(create = true, required = false)
    protected transient FacesMessages facesMessages;

    @In(create = true, required = true)
    protected transient RechercheWebActionsBean rechercheWebActions;

    @In(create = true, required = true)
    protected transient CorbeilleActionsBean corbeilleActions;

    @In(create = true, required = false)
    protected transient DocumentRoutingWebActionsBean routingWebActions;

    @In(create = true, required = false)
    protected transient NavigationContextBean navigationContext;

    @In(create = true, required = true)
    protected transient NavigationWebActionsBean navigationWebActions;

    @In(create = true, required = false)
    protected transient DocumentsListsManagerBean documentsListsManager;

    @In(create = true, required = true)
    protected transient CoreSession documentManager;

    /**
     * Critère de recherche simple.
     */
    protected String simpleSearch;

    /**
     * Navigue vers l'espace de recherche et effectue la recherche simple.
     * 
     * @return Vue
     * @throws ClientException
     */
    public String navigateToRechercheSimple() throws ClientException {
        if (StringUtils.isEmpty(getSimpleSearch())) {
            return "";
        }

        // Renseigne le menu du haut
        Action mainMenuAction = actionManager.getAction(ReponsesActionConstant.ESPACE_RECHERCHE);
        navigationWebActions.setCurrentMainMenuAction(mainMenuAction);

        // Renseigne le menu de gauche
        rechercheWebActions.setContentViewName(ReponsesContentView.RECHERCHE_SIMPLE_CONTENT_VIEW);
        navigationWebActions.setCurrentLeftMenuAction(null);

        // Déplie le panneau des résultats de recherche
        navigationWebActions.setUpperPanelIsOpened(true);
        navigationWebActions.setLeftPanelIsOpened(false);

        // Initialise la corbeille
        corbeilleActions.setCurrentView(ReponsesViewConstant.VIEW_REQUETE_RESULTS);
        
        // Si la recherche ne retourne qu'un dossier, on l'ouvre directement
        rechercheWebActions.openOrResetSearchContext(ReponsesContentView.RECHERCHE_SIMPLE_CONTENT_VIEW);
        
        // Renseigne la vue de route des étapes de feuille de route vers les dossiers
        routingWebActions.setFeuilleRouteView(ReponsesViewConstant.VIEW_REQUETE_RESULTS);

        documentsListsManager.resetWorkingList(DocumentsListsManager.CURRENT_DOCUMENT_SELECTION);

        return corbeilleActions.getCurrentView();
    }

    public String getSimpleSearch() {
        return simpleSearch;
    }

    public void setSimpleSearch(String simpleSearch) {
        this.simpleSearch = simpleSearch;
    }

    public String getOrigineToSearch() {
        return SimpleSearchQueryParser.getOrigineQuestionToSearch(simpleSearch);
    }

    public String getNumberToSearch() {
        return SimpleSearchQueryParser.getNumberQuestionToSearch(simpleSearch);
    }

    public Long getLegislatureCourante() throws ClientException {
        String legislatureValue = STServiceLocator.getSTParametreService().getParametreValue(documentManager,
                ReponsesParametreConstant.LEGISLATURE_COURANTE);

        Long legislatureCourante = null;
        try {
            legislatureCourante = Long.parseLong(legislatureValue);
        } catch (Exception e) {
            log.warn(e);
            throw new ClientException("La législature courante n'est pas paramétrée correctement.");
        }
        return legislatureCourante;
    }
}