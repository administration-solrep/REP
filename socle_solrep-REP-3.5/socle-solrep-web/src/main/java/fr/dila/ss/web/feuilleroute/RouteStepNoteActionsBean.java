package fr.dila.ss.web.feuilleroute;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.comment.api.CommentableDocument;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.ecm.platform.routing.api.DocumentRouteElement;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.web.comment.SSCommentManagerActionsBean;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.web.context.NavigationContextBean;
import fr.dila.st.web.corbeille.CorbeilleActionsBean;
import fr.dila.st.web.lock.STLockActionsBean;

/**
 * WebBean permettant de gérer les notes d'étapes.
 *
 * @author jtremeaux
 */
@Name("routeStepNoteActions")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.FRAMEWORK)
public class RouteStepNoteActionsBean implements Serializable {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;
    
    @In(create = true, required = false)
    protected transient CoreSession documentManager;
    
    @In(required = true, create = true)
    protected SSPrincipal ssPrincipal;

    @In(required = true, create = true)
    protected NavigationContextBean navigationContext;
    
    @In(required = true, create = false)
    protected DocumentRoutingActionsBean routingActions;
    
    @In(required = true, create = true)
    protected SSCommentManagerActionsBean commentManagerActions;

    @In(create = true)
    protected transient ResourcesAccessor resourcesAccessor;

    @In(create = true, required = false)
    protected transient DocumentRoutingWebActionsBean routingWebActions;
    
    @In(create = true, required = false)
    protected transient STLockActionsBean stLockActions;
    
    @In(create = true, required = false)
    protected transient CorbeilleActionsBean corbeilleActions;
    
	@In(required = false, create = true)
	protected transient DocumentsListsManager			documentsListsManager;
	
	@In(create = true, required = false)
	protected FacesMessages				facesMessages;
    
    /**
     * Dossier courant (renseigné sur l'écran de détail des notes d'étapes).
     */
    protected DocumentModel dossierDoc;

    /**
     * Default constructor
     */
    public RouteStepNoteActionsBean(){
    	// do nothing
    }
    
    /**
     * Navigue vers les notes d'étape d'une étape en mode lecture.
     * 
     * @param routeStepDoc Étape de feuille de route
     * @return vue
     * @throws ClientException
     */
    public String viewRouteStepNote(DocumentModel routeStepDoc) throws ClientException {
        // Renseigne le document dossier pour le retour
        dossierDoc = navigationContext.getCurrentDocument();
        
        // Charge le document étape de feuille de route
        navigationContext.navigateToDocument(routeStepDoc);

        // Navigue vers la vue notes
        navigationContext.setCurrentTabAndNavigate(dossierDoc, "TAB_CASE_MANAGEMENT_VIEW_RELATED_ROUTE");
		
		commentManagerActions.setRouteStepDoc(routeStepDoc);
		
		return routingWebActions.getFeuilleRouteView();
    }
    
    /**
     * Navigue vers les notes d'étape d'une étape en mode lecture.
     * 
     * @param routeStepDoc Étape de feuille de route
     * @return vue
     * @throws ClientException
     */
    public String editRouteStepNote(DocumentModel routeStepDoc) throws ClientException {
        // Renseigne le document dossier pour le retour
        dossierDoc = navigationContext.getCurrentDocument();
        
        // Charge le document étape de feuille de route
        navigationContext.navigateToDocument(routeStepDoc);
        
        // Navigue vers la vue notes
        navigationContext.setCurrentTabAndNavigate(dossierDoc, "TAB_CASE_MANAGEMENT_VIEW_RELATED_ROUTE");
		
        // Affiche par défaut l'encart de création d'une note
        commentManagerActions.setShowCreateForm(true);
        commentManagerActions.setRouteStepDoc(routeStepDoc);
		commentManagerActions.setShared(false);
		commentManagerActions.setShowCommentWidget(true);
        
		return routingWebActions.getFeuilleRouteView();
    }
    
    /**
     * Retourne la liste des commentaires à la racine du document.
     * 
     * @param routeStepDoc Document
     * @return Liste des commentaires
     * @throws ClientException
     */
    public List<DocumentModel> getCommentList(DocumentModel routeStepDoc) throws ClientException {
        CommentableDocument commentableDoc = routeStepDoc.getAdapter(CommentableDocument.class);
        if (commentableDoc == null) {
            return new ArrayList<DocumentModel>();
        }
        return commentableDoc.getComments();
    }
    
    /**
     * Retourne si le document à des commentaires
     * @param routeStepDoc
     * @return
     * @throws ClientException
     */
    public boolean hasComment(DocumentModel routeStepDoc) throws ClientException {
        return !getCommentList(routeStepDoc).isEmpty();
    }
    
    /**
     * Retourne vrai si l'utilisateur a le droit de voir une note d'étape.
     * 
     * @param routeStepDoc Étape de feuille de route
     * @return Condition
     * @throws ClientException
     */
    public boolean isViewableNote(DocumentModel routeStepDoc) throws ClientException {
        // Vérifie l'état de l'étape
        final String currentLifeCycleState = routeStepDoc.getCurrentLifeCycleState();
        return "init".equals(currentLifeCycleState) ||currentLifeCycleState.equals(DocumentRouteElement.ElementLifeCycleState.running.name())
                || currentLifeCycleState.equals(DocumentRouteElement.ElementLifeCycleState.ready.name());
    }
    
    /**
     * Retourne vrai si l'utilisateur a le droit de modifier une note d'étape.
     * 
     * @param routeStepDoc Étape de feuille de route
     * @return Condition
     * @throws ClientException
     */
    public boolean isEditableNote(DocumentModel routeStepDoc) throws ClientException {
        // Vérifie l'état de l'étape
        if (!isViewableNote(routeStepDoc)) {
            return false;
        }
        
        // Vérifie si le DossierLink est chargé
        if (!corbeilleActions.isDossierLoadedInCorbeille()) {
            return false;
        }
        
        // Vérifie si le dossier est verrouillé
        final DocumentModel dossierDoc = getCurrentDossierDoc();
        return stLockActions.isDocumentLockedByCurrentUser(dossierDoc);
    }
    
    /**
     * Retourne le dossier courant associé à l'étape.
     * 
     * @return Dossier courant
     */
    public DocumentModel getCurrentDossierDoc() {
        // Si on est sur la liste des étapes, retourne le document courant
        final DocumentModel currentDoc = navigationContext.getCurrentDocument();
        if (currentDoc.hasFacet(STConstant.ROUTABLE_FACET)) {
            return currentDoc;
        }
        
        // Sinon on est sur l'écran de détail, retourne le dossier stocké dans ce WebBean
        return this.dossierDoc;
    }
    
    /**
     * Retourne au dossier.
     * 
     * @return Vue du dossier
     * @throws ClientException
     */
    public String goBackToRoute() throws ClientException {
    	commentManagerActions.setRouteStepDoc(null);
    	commentManagerActions.setShowCommentWidget(false);
		
        navigationContext.setCurrentTabAndNavigate(dossierDoc, "TAB_CASE_MANAGEMENT_VIEW_RELATED_ROUTE");
        return routingWebActions.getFeuilleRouteView();
    }
    
    /**
	 * Renvoie le nombre total de notes sur cette étape. Indépendamment des
	 * restrictions d'accès (on compte même celles auxquelles l'utilisateur n'a
	 * pas accès).
	 * 
	 * @param routeStepDoc DocumentModel route step
	 * @return un nombre supérieur ou égal à 0.
	 * @throws ClientException 
	 */
	public int getCommentNumber(DocumentModel routeStepDoc) throws ClientException {
		CommentableDocument routeStep = routeStepDoc.getAdapter(CommentableDocument.class);
		
		int total = 0;
		
		for (DocumentModel comment : routeStep.getComments()) {
			total += 1 + getSubCommentsNumber(routeStep, comment);
		}
		
		return total;
	}
	
	/**
	 * Calcule de manière récursive et renvoie le nombre de sous-commentaires d'un commentaire (jusqu'aux feuilles). 
	 * @param routeStep Route step principal
	 * @param parentCommentDoc commentaire parent.
	 * @return un nombre supérieur ou égal à 0
	 * @throws ClientException
	 */
	private int getSubCommentsNumber(CommentableDocument routeStep, DocumentModel parentCommentDoc) throws ClientException {
		List<DocumentModel> children = routeStep.getComments(parentCommentDoc);
		if(children.isEmpty()) {
			return 0;
		}
		int total = 0;
		
		for(DocumentModel child:children) {
			total += 1+getSubCommentsNumber(routeStep, child);
		}
		
		return total;
	}
	
	public String initCreateSharedNote() throws ClientException {
		ArrayList<DocumentModel> checkedRouteSteps = new ArrayList<DocumentModel>(
				documentsListsManager.getWorkingList(DocumentsListsManager.CURRENT_DOCUMENT_SELECTION));
		
		ArrayList<DocumentModel> noteRouteSteps = new ArrayList<DocumentModel>();
		
		// Vérification des étapes: si on ne peut pas faire de note partagée dessus, alors elle n'est pas prise en compte
		// et on avertit l'utilisateur
		String message = null;
		for (DocumentModel docModel : checkedRouteSteps) {
			if (!routingActions.isEditableRouteElement(docModel)) {
				message = resourcesAccessor.getMessages().get("feedback.ss.sharedNote.cannot.create.step");
			} else {
				noteRouteSteps.add(docModel);
			}
		}

		if (!documentsListsManager.isWorkingListEmpty(DocumentsListsManager.CURRENT_DOCUMENT_SELECTION)) {
			documentsListsManager.getWorkingList(DocumentsListsManager.CURRENT_DOCUMENT_SELECTION).clear();
		}
		
		// Si aucune étape ne permet la création d'une note d'étape partagée, on ne fait rien mais on prévient. 
		if (noteRouteSteps.isEmpty() && message != null) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("feedback.ss.sharedNote.cannot.create"));
			return null;
		}

		if (message != null) {
			facesMessages.add(StatusMessage.Severity.WARN, message);
		}
		
		return editSharedRouteStepNote(noteRouteSteps);
	}
	
	private String editSharedRouteStepNote(List<DocumentModel> routeStepDocs) throws ClientException {
		// Renseigne le document dossier pour le retour
		dossierDoc = navigationContext.getCurrentDocument();

		// Affiche par défaut l'encart de création d'une note
		commentManagerActions.setShowCreateForm(true);

		navigationContext.setCurrentTabAndNavigate(dossierDoc, "TAB_CASE_MANAGEMENT_VIEW_RELATED_ROUTE");
		
		commentManagerActions.setRouteStepDocs(routeStepDocs);
		commentManagerActions.setShowCreateForm(true);
		commentManagerActions.setShowCommentWidget(true);
		commentManagerActions.setShared(true);
		
		return routingWebActions.getFeuilleRouteView();
	}
}
