package fr.dila.ss.web.comment;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.platform.comment.api.CommentableDocument;
import org.nuxeo.ecm.platform.comment.web.ThreadEntry;
import org.nuxeo.ecm.platform.comment.web.UIComment;
import org.nuxeo.ecm.platform.comment.workflow.utils.CommentsConstants;

import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.domain.comment.STComment;
import fr.dila.st.api.feuilleroute.STRouteStep;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.CommentManagerImpl;
import fr.dila.st.core.util.PropertyUtil;
import fr.dila.st.web.comment.CommentManagerActionsBean;

@Name("commentManagerActions")
@Scope(CONVERSATION)
@Install(precedence = Install.APPLICATION + 2)
public class SSCommentManagerActionsBean extends CommentManagerActionsBean {
	private static final long serialVersionUID = 1L;

	private static final STLogger LOGGER = STLogFactory.getLog(CommentManagerActionsBean.class);

	private static final Log log = LogFactory.getLog(CommentManagerActionsBean.class);

	/**
	 * Route step envoyée au bean.
	 */
	private DocumentModel routeStepDoc;

	/**
	 * Route steps envoyées au bean. (dans le cas d'une note partagée)
	 */
	private List<DocumentModel> routeStepDocs;

	/** Affiche-t-on le widget d'affichage des notes d'étapes. */
	private boolean showCommentWidget;

	/**
	 * Est-on en train d'éditer une note d'étape partagée ?
	 */
	private boolean shared;

	/**
	 * On reste en mode showWidgetComment sur le même routeStep
	 */
	@Override
	public DocumentModel addComment(DocumentModel comment, DocumentModel docToComment) throws ClientException {
		DocumentModel rsd = routeStepDoc;
		// On sort du mode widget si on affiche une note partagée.
		boolean scw = shared ? false : showCommentWidget;

		DocumentModel createdComment = null;
		if (!isShared()) {
			createdComment = super.addComment(comment, getRouteStepDoc());
		} else {
			List<DocumentModel> dmList = new ArrayList<DocumentModel>();
			dmList.addAll(getRouteStepDocs());
			createdComment = addSharedComment(comment, getRouteStepDocs());
		}

		routeStepDoc = rsd;
		showCommentWidget = scw;

		// Dans tous les cas, on sort du mode shared
		shared = false;

		return createdComment;
	}

	private DocumentModel addSharedComment(DocumentModel comment, List<DocumentModel> docsToCommentList)
			throws ClientException {
		try {
			comment = initializeComment(comment);

			if (docsToCommentList == null || docsToCommentList.isEmpty()) {
				throw new ClientException("Can't comment on null document");
			}

			DocumentModel firstDm = docsToCommentList.remove(0);
			CommentableDocument lDocumentableDoc = firstDm.getAdapter(CommentableDocument.class);

			DocumentModel newComment = lDocumentableDoc.addComment(comment);

			// Rattachement aux autres commentableDocs
			for (DocumentModel remaining : docsToCommentList) {
				List<String> commentIds = PropertyUtil.getStringListProperty(remaining,
						STSchemaConstant.ROUTING_TASK_SCHEMA, STSchemaConstant.ROUTING_TASK_COMMENTS_PROPERTY);
				commentIds.add(newComment.getId());
				remaining.setProperty(STSchemaConstant.ROUTING_TASK_SCHEMA,
						STSchemaConstant.ROUTING_TASK_COMMENTS_PROPERTY, commentIds);
				remaining.getAdapter(STRouteStep.class).save(documentManager);
				documentManager.save();
			}

			// automatically validate the comments
			if (CommentsConstants.COMMENT_LIFECYCLE.equals(newComment.getLifeCyclePolicy())) {
				new FollowTransitionUnrestricted(documentManager, newComment.getRef(),
						CommentsConstants.TRANSITION_TO_PUBLISHED_STATE).runUnrestricted();
			}

			cleanContextVariable();

			return newComment;

		} catch (Throwable t) {
			log.error("failed to add comment", t);
			throw ClientException.wrap(t);
		}
	}

	/**
	 * On reste en mode showWidgetComment sur le même routeStep
	 */
	@Override
	public String deleteComment(String commentId) throws ClientException {
		DocumentModel rsd = routeStepDoc;
		boolean scw = showCommentWidget;

		String toReturn = internalDeleteComment(commentId);

		routeStepDoc = rsd;
		showCommentWidget = scw;

		return toReturn;
	}

	public String internalDeleteComment(String commentId) throws ClientException {
		if ("".equals(commentId)) {
			log.error("No comment id to delete");
			return null;
		}

		try {
			UIComment selectedComment = commentMap.get(commentId);

			DocumentModel comment = selectedComment.getComment();

			final DocumentRef ref = comment.getRef();
			if (!documentManager.exists(ref)) {
				throw new ClientException("Comment Document does not exist: " + comment.getId());
			}

			// Pour chacune des étapes de premier niveau du dossier courant
			List<DocumentModel> firstLevelSteps = SSServiceLocator.getSSFeuilleRouteService().getSteps(documentManager,
					navigationContext.getCurrentDocument());

			exploreListAndRemoveLinks(commentId, firstLevelSteps);

			LOGGER.info(documentManager, STLogEnumImpl.DEL_COMMENT_TEC, comment);
			SSServiceLocator.getCommentManager().deleteComment(navigationContext.getCurrentDocument(), comment);

			cleanContextVariable();
			return null;
		} catch (Throwable t) {
			log.error("failed to delete comment", t);
			throw ClientException.wrap(t);
		}
	}
	
	private void exploreListAndRemoveLinks(String commentId, List<DocumentModel> stepList) throws ClientException {
		for (DocumentModel stepDocModel : stepList) {
			if (!STConstant.STEP_FOLDER_DOCUMENT_TYPE.equals(stepDocModel.getType())) {
				// Etape non imbriquée, cas classique
				deleteCommentLink(commentId, stepDocModel);
			} else {
				// Etape parallèle
				final DocumentRoutingService documentRoutingService = SSServiceLocator
						.getDocumentRoutingService();
				final DocumentModelList children = documentRoutingService.getOrderedRouteElement(stepDocModel.getId(),
						documentManager);
				exploreListAndRemoveLinks(commentId, children);
			}
		}
	}

	private void deleteCommentLink(String commentId, DocumentModel stepDocModel) throws ClientException {
		List<String> commentDocIds = PropertyUtil.getStringListProperty(stepDocModel,
				STSchemaConstant.ROUTING_TASK_SCHEMA, STSchemaConstant.ROUTING_TASK_COMMENTS_PROPERTY);

		if (commentDocIds.contains(commentId)) {
			commentDocIds.remove(commentId);
			stepDocModel.setProperty(STSchemaConstant.ROUTING_TASK_SCHEMA,
					STSchemaConstant.ROUTING_TASK_COMMENTS_PROPERTY, commentDocIds);
			stepDocModel.getAdapter(STRouteStep.class).save(documentManager);
			documentManager.save();
		}
	}

	/**
	 * On reste en mode showWidgetComment sur le même routeStep
	 */
	@Override
	public String cancelComment() {
		DocumentModel rsd = routeStepDoc;
		boolean scw = showCommentWidget;

		super.cancelComment();

		if (!isShared()) {
			// Si on est en mode création de note d'étape partagée, on ne reste
			// pas en mode édition de commentaire.
			routeStepDoc = rsd;
			showCommentWidget = scw;
		}

		return null;
	}

	protected boolean isVisible(DocumentModel commentModel) throws ClientException {
		STComment comment = commentModel.getAdapter(STComment.class);

		if (comment == null) {
			return false;
		}

		if (comment.getAuthor() != null && comment.getAuthor().equals(principal.getName())) {
			return true;
		}

		// FEV514 : si l'utilisateur courant est dans le poste de l'auteur de la
		// note, il peut voir la note
		if (isInAuthorPoste(comment)) {
			return true;
		}

		return true;

	}
	
	/**
	 * FEV514 : Une condition suffisante pour pouvoir supprimer une note est
	 * d'être dans un des postes de son auteur.
	 * 
	 * @return true si l'utilisateur courant est dans le même poste que l'auteur du commentaire
	 * @throws ClientException 
	 */
	public boolean isInAuthorPoste(STComment comment) throws ClientException {
		return comment != null && ((CommentManagerImpl) SSServiceLocator.getCommentManager())
				.isInAuthorPoste(comment, principal.getName());
	}
	
	public boolean isInAuthorPoste(DocumentModel commentDoc) throws ClientException {
		return commentDoc==null || isInAuthorPoste(commentDoc.getAdapter(STComment.class));
	}
    
	@Override
	public void cleanContextVariable() {
        super.cleanContextVariable();
        
        routeStepDoc = null;
        routeStepDocs = null;
        showCommentWidget = false;
        shared = false;
        
    }
	
	@Override
	protected CommentableDocument getCommentableDoc() {
		if(routeStepDoc !=null) {
			commentableDoc = routeStepDoc.getAdapter(CommentableDocument.class);
			return commentableDoc;
		}
		
        return super.getCommentableDoc();
    }
	
	@Override
	public List<ThreadEntry> getCommentsAsThread() throws ClientException {
        return getCommentsAsThread(getRouteStepDoc());
    }

	public DocumentModel getRouteStepDoc() {
		return routeStepDoc;
	}

	public void setRouteStepDoc(DocumentModel routeStepDoc) {
		this.routeStepDoc = routeStepDoc;
		
		showCommentWidget = true;
	}

	public void setShowCommentWidget(boolean showCommentWidget) {
		this.showCommentWidget = showCommentWidget;
	}

	public boolean isShowCommentWidget() {
		return showCommentWidget;
	}

	public List<DocumentModel> getRouteStepDocs() {
		return routeStepDocs;
	}

	public void setRouteStepDocs(List<DocumentModel> routeStepDocs) {
		this.routeStepDocs = routeStepDocs;
	}

	
	public boolean isShared() {
		return shared;
	}

	public void setShared(boolean shared) {
		this.shared = shared;
	}
}
