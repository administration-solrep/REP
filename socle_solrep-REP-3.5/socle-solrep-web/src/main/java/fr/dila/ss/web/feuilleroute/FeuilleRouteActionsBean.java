package fr.dila.ss.web.feuilleroute;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.api.WebActions;
import org.nuxeo.ecm.webapp.edit.lock.LockActions;
import org.nuxeo.ecm.webapp.helpers.EventNames;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.ecm.platform.routing.api.DocumentRoute;
import fr.dila.ecm.platform.routing.api.DocumentRouteElement;
import fr.dila.ecm.platform.routing.api.exception.DocumentRouteAlredayLockedException;
import fr.dila.ecm.platform.routing.api.exception.DocumentRouteNotLockedException;
import fr.dila.ss.core.service.SSServiceLocator;

/**
 * Actions permettant de gérer une feuille de route.
 *
 * @author jtremeaux
 */
@Scope(CONVERSATION)
@Name("stFeuilleRouteActions")
@Install(precedence = Install.FRAMEWORK)
public class FeuilleRouteActionsBean implements Serializable {
    private static final long serialVersionUID = 1L;

//    private static final Log log = LogFactory.getLog(STFeuilleRouteActionsBean.class);

    @In(required = true, create = true)
    protected NavigationContext navigationContext;

    @In(create = true, required = false)
    protected CoreSession documentManager;

    @In(create = true, required = false)
    protected FacesMessages facesMessages;

    @In(create = true)
    protected WebActions webActions;

    @In(create = true)
    protected LockActions lockActions;

    @In(create = true)
    protected ResourcesAccessor resourcesAccessor;

    @In(required = true, create = true)
    protected NuxeoPrincipal currentUser;

    @In(create = true)
    protected List<DocumentModel> relatedRoutes;

    protected String relatedRouteModelDocumentId;

    /**
     * Default constructor
     */
    public FeuilleRouteActionsBean(){
    	// do nothing
    }
    
    @Observer(value = { EventNames.DOCUMENT_CHANGED })
    public void resetRelatedRouteDocumentId() {
        relatedRouteModelDocumentId = null;
    }

    /**
     * Retourne la feuille de route chargée.
     * 
     * @return Feuille de route
     */
    public DocumentRoute getRelatedRoute() {
        // try to see if actually the current document is a route
        DocumentModel currentDocument = navigationContext.getCurrentDocument();
        DocumentRoute relatedRoute = currentDocument.getAdapter(DocumentRoute.class);
        if (relatedRoute != null) {
            return relatedRoute;
        }
        // try to see if the current document is a routeElement
        DocumentRouteElement relatedRouteElement = currentDocument.getAdapter(DocumentRouteElement.class);
        if (relatedRouteElement != null) {
            return relatedRouteElement.getDocumentRoute(documentManager);
        }
        // else we must be in a document attached to a route
        String relatedRouteModelDocumentId;
        if (relatedRoutes.size() <= 0) {
            return null;
        }
        relatedRouteModelDocumentId = relatedRoutes.get(0).getId();
        DocumentModel docRoute;
        try {
            docRoute = documentManager.getDocument(new IdRef(relatedRouteModelDocumentId));
        } catch (ClientException e) {
            return null;
        }
        return docRoute.getAdapter(DocumentRoute.class);
    }

    /**
     * Invalide le modèle de feuille de route en cours.
     * 
     * @return Vue
     * @throws ClientException
     */
    public String invalidateRouteModel() throws ClientException {
        DocumentRoute currentRouteModel = getRelatedRoute();
        try {
            SSServiceLocator.getDocumentRoutingService().lockDocumentRoute(currentRouteModel, documentManager);
        } catch (DocumentRouteAlredayLockedException e) {
            facesMessages.add(
                    StatusMessage.Severity.WARN,
                    resourcesAccessor.getMessages().get(
                            "feedback.casemanagement.document.route.already.locked"));
            return null;
        }
        try {
            SSServiceLocator.getDocumentRoutingService().invalidateRouteModel(currentRouteModel, documentManager);
        } catch (DocumentRouteNotLockedException e) {
            facesMessages.add(
                    StatusMessage.Severity.WARN,
                    resourcesAccessor.getMessages().get(
                            "feedback.casemanagement.document.route.not.locked"));
            return null;
        }
        Events.instance().raiseEvent(EventNames.DOCUMENT_CHILDREN_CHANGED, currentRouteModel.getDocument());
        SSServiceLocator.getDocumentRoutingService().unlockDocumentRoute(currentRouteModel, documentManager);
        
        return null;
    }
}
