package fr.dila.reponses.web.recherche;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;
import org.nuxeo.ecm.platform.userworkspace.web.ejb.UserWorkspaceManagerActionsBean;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.ecm.platform.routing.api.DocumentRouteElement.ElementLifeCycleState;
import fr.dila.reponses.api.constant.ReponsesViewConstant;
import fr.dila.reponses.api.constant.VocabularyConstants;
import fr.dila.reponses.core.dao.FeuilleRouteDao;
import fr.dila.ss.api.criteria.FeuilleRouteCriteria;
import fr.dila.ss.api.service.FeuilleRouteModelService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.web.feuilleroute.DocumentRoutingWebActionsBean;
import fr.dila.st.api.constant.STViewConstant;
import fr.dila.st.web.context.NavigationContextBean;

/**
 * Bean Seam permettant de gérer les recherches de feuilles de route.
 * 
 * @author jtremeaux
 */
@Name("rechercheModeleFeuilleRouteActions")
@Scope(ScopeType.CONVERSATION)
public class RechercheModeleFeuilleRouteActionsBean implements Serializable {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;
	
    @In(create = true, required = true)
    protected transient CoreSession documentManager;

    @In(create = true)
    protected transient ResourcesAccessor resourcesAccessor;

    @In(create = true, required = false)
    protected transient ContentViewActions contentViewActions;

    @In(create = true, required = false)
    protected transient DocumentRoutingWebActionsBean routingWebActions;

    @In(create = true, required = false)
    protected transient NavigationContextBean navigationContext;

    @In(create = true, required = false)
    protected transient UserWorkspaceManagerActionsBean userWorkspaceManagerActions;

    @In(create = true)
    protected transient DocumentsListsManager documentsListsManager;

    // Critères de recherche
    private String feuilleRouteTitle;
    private String feuilleRouteMinistere;
    private String feuilleRouteCreationUtilisateur;
    private Date feuilleRouteCreationDateMin;
    private Date feuilleRouteCreationDateMax;
    private String feuilleRouteValidee;
    private String routeStepRoutingTaskType;
    private String routeStepDistributionMailboxId;
    private Long routeStepEcheanceIndicative;
    private String routeStepAutomaticValidation;
    private String routeStepObligatoireSgg;
    private String routeStepObligatoireMinistere;
    
    /**
     * Exécute la recherche des modèles de feuille de route.
     * 
     * @return Vue
     * @throws ClientException
     */
    public String search() throws ClientException {
        // Invalide la content view des résultats de recherche
        contentViewActions.reset("recherche_fdr_resultats");
        
        // Navigue vers la racine des modèles de feuilles de route
        final FeuilleRouteModelService feuilleRouteModelService = SSServiceLocator.getFeuilleRouteModelService();
        DocumentModel currentDocument = feuilleRouteModelService.getFeuilleRouteModelFolder(documentManager);
        navigationContext.setCurrentDocument(currentDocument);
        
        // Renseigne la vue de retour des feuilles de routes
        routingWebActions.setFeuilleRouteView(STViewConstant.MODELE_FEUILLE_ROUTE_VIEW);

        return ReponsesViewConstant.RECHERCHE_MODELE_FEUILLE_ROUTE_RESULTATS;
    }
    
    /**
     * Retourne les critères de recherche des modèles de feuille de route sous forme textuelle.
     * 
     * @return Critères de recherche des modèles de feuille de route sous forme textuelle
     * @throws ClientException
     */
    public String getSearchQueryString() throws ClientException {
        FeuilleRouteCriteria criteria = getFeuilleRouteCriteria();
        FeuilleRouteDao feuilleRouteDao = new FeuilleRouteDao(documentManager, criteria);
        
        return feuilleRouteDao.getQueryString();
    }

    /**
     * Retourne les critères de recherche des modèles de feuille de route sous forme textuelle.
     * 
     * @return Critères de recherche des modèles de feuille de route sous forme textuelle
     * @throws ClientException
     */
    public List<Object> getSearchQueryParameter() throws ClientException {
        FeuilleRouteCriteria criteria = getFeuilleRouteCriteria();
        FeuilleRouteDao feuilleRouteDao = new FeuilleRouteDao(documentManager, criteria);
        
        return feuilleRouteDao.getParamList();
    }
    
    /**
     * Retourne les critères de recherche des modèles de feuille de route.
     * @return
     */
    private FeuilleRouteCriteria getFeuilleRouteCriteria() {
        FeuilleRouteCriteria criteria = new FeuilleRouteCriteria();
        criteria.setCheckReadPermission(true);
        criteria.setIntituleLike(getFeuilleRouteTitle());
        criteria.setMinistere(getFeuilleRouteMinistere());
        criteria.setCreationUtilisateur(getFeuilleRouteCreationUtilisateur());
        if (getFeuilleRouteCreationDateMin() != null) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(getFeuilleRouteCreationDateMin());
            criteria.setCreationDateMin(calendar);
        }
        if (getFeuilleRouteCreationDateMax() != null) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(getFeuilleRouteCreationDateMax());
            criteria.setCreationDateMax(calendar);
        }
        if (StringUtils.isNotBlank(getFeuilleRouteValidee())) {
            if (VocabularyConstants.BOOLEAN_TRUE.equals(getFeuilleRouteValidee())) {
                criteria.setCurrentLifeCycleState(ElementLifeCycleState.validated.name());
            } else {
                criteria.setCurrentLifeCycleState(ElementLifeCycleState.draft.name());
            }
        }
        criteria.setRoutingTaskType(getRouteStepRoutingTaskType());
        criteria.setDistributionMailboxId(getRouteStepDistributionMailboxId());
        criteria.setDeadline(getRouteStepEcheanceIndicative());
        if (StringUtils.isNotBlank(getRouteStepAutomaticValidation())) {
            if (VocabularyConstants.BOOLEAN_TRUE.equals(getRouteStepAutomaticValidation())) {
                criteria.setAutomaticValidation(true);
            } else {
                criteria.setAutomaticValidation(false);
            }
        }
        if (StringUtils.isNotBlank(getRouteStepObligatoireSgg())) {
            if (VocabularyConstants.BOOLEAN_TRUE.equals(getRouteStepObligatoireSgg())) {
                criteria.setObligatoireSGG(true);
            } else {
                criteria.setObligatoireSGG(false);
            }
        }
        if (StringUtils.isNotBlank(getRouteStepObligatoireMinistere())) {
            if (VocabularyConstants.BOOLEAN_TRUE.equals(getRouteStepObligatoireMinistere())) {
                criteria.setObligatoireMinistere(true);
            } else {
                criteria.setObligatoireMinistere(false);
            }
        }

        return criteria;
    }

    /**
     * Réinitialise l'écran de saisie des critères de recherche.
     * 
     * @return Vue
     * @throws ClientException
     */
    public String reset() throws ClientException {
        // Invalide la content view des résultats de recherche
        contentViewActions.reset("recherche_fdr_resultats");

        // Invalide le modèle de feuille de route affiché
        navigationContext.resetCurrentDocument();
        
        // Reset les critères de recherche
        feuilleRouteTitle = null;
        feuilleRouteMinistere = null;
        feuilleRouteCreationUtilisateur = null;
        feuilleRouteCreationDateMin = null;
        feuilleRouteCreationDateMax = null;
        feuilleRouteValidee = null;
        routeStepRoutingTaskType = null;
        routeStepDistributionMailboxId = null;
        routeStepEcheanceIndicative = null;
        routeStepAutomaticValidation = null;
        routeStepObligatoireSgg = null;
        routeStepObligatoireMinistere = null;
        
        return ReponsesViewConstant.RECHERCHE_MODELE_FDR_VIEW;
    }
    
    /**
     * Navigue vers un modèle de feuille de route à partir des résultats de recherche ou des favoris
     * de recherche / consultation.
     * 
     * @param feuilleRouteDoc Modèle de feuille de route à charger
     * @return Vue
     * @throws ClientException
     */
    public String loadFeuilleRoute(DocumentModel feuilleRouteDoc) throws ClientException {
        // Charge la feuille de route comme document courant
        navigationContext.navigateToDocument(feuilleRouteDoc);

        return routingWebActions.getFeuilleRouteView();
    }

    public void setFeuilleRouteTitle(String feuilleRouteTitle) {
        this.feuilleRouteTitle = feuilleRouteTitle;
    }

    public String getFeuilleRouteTitle() {
        return feuilleRouteTitle;
    }

    public void setFeuilleRouteMinistere(String feuilleRouteMinistere) {
        this.feuilleRouteMinistere = feuilleRouteMinistere;
    }

    public String getFeuilleRouteMinistere() {
        return feuilleRouteMinistere;
    }

    public void setFeuilleRouteCreationUtilisateur(String feuilleRouteCreationUtilisateur) {
        this.feuilleRouteCreationUtilisateur = feuilleRouteCreationUtilisateur;
    }

    public String getFeuilleRouteCreationUtilisateur() {
        return feuilleRouteCreationUtilisateur;
    }

    public void setFeuilleRouteCreationDateMin(Date feuilleRouteCreationDateMin) {
        this.feuilleRouteCreationDateMin = feuilleRouteCreationDateMin;
    }

    public Date getFeuilleRouteCreationDateMin() {
        return feuilleRouteCreationDateMin;
    }

    public void setFeuilleRouteCreationDateMax(Date feuilleRouteCreationDateMax) {
        this.feuilleRouteCreationDateMax = feuilleRouteCreationDateMax;
    }

    public Date getFeuilleRouteCreationDateMax() {
        return feuilleRouteCreationDateMax;
    }

    public void setFeuilleRouteValidee(String feuilleRouteValidee) {
        this.feuilleRouteValidee = feuilleRouteValidee;
    }

    public String getFeuilleRouteValidee() {
        return feuilleRouteValidee;
    }

    public void setRouteStepRoutingTaskType(String routeStepRoutingTaskType) {
        this.routeStepRoutingTaskType = routeStepRoutingTaskType;
    }

    public String getRouteStepRoutingTaskType() {
        return routeStepRoutingTaskType;
    }

    public void setRouteStepDistributionMailboxId(String routeStepDistributionMailboxId) {
        this.routeStepDistributionMailboxId = routeStepDistributionMailboxId;
    }

    public String getRouteStepDistributionMailboxId() {
        return routeStepDistributionMailboxId;
    }

    public void setRouteStepEcheanceIndicative(Long routeStepEcheanceIndicative) {
        this.routeStepEcheanceIndicative = routeStepEcheanceIndicative;
    }

    public Long getRouteStepEcheanceIndicative() {
        return routeStepEcheanceIndicative;
    }

    public void setRouteStepAutomaticValidation(String routeStepAutomaticValidation) {
        this.routeStepAutomaticValidation = routeStepAutomaticValidation;
    }

    public String getRouteStepAutomaticValidation() {
        return routeStepAutomaticValidation;
    }

    public void setRouteStepObligatoireSgg(String routeStepObligatoireSgg) {
        this.routeStepObligatoireSgg = routeStepObligatoireSgg;
    }

    public String getRouteStepObligatoireSgg() {
        return routeStepObligatoireSgg;
    }

    public void setRouteStepObligatoireMinistere(String routeStepObligatoireMinistere) {
        this.routeStepObligatoireMinistere = routeStepObligatoireMinistere;
    }

    public String getRouteStepObligatoireMinistere() {
        return routeStepObligatoireMinistere;
    }
    
    
}