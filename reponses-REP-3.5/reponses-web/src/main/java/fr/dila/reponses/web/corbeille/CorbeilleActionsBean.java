package fr.dila.reponses.web.corbeille;

import static org.jboss.seam.ScopeType.EVENT;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.actions.ejb.ActionManager;
import org.nuxeo.ecm.platform.ui.web.api.UserAction;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager;

import fr.dila.cm.web.mailbox.CaseManagementMailboxActionsBean;
import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesActionConstant;
import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.reponses.api.constant.ReponsesViewConstant;
import fr.dila.reponses.web.contentview.ProviderBean;
import fr.dila.reponses.web.dossier.DossierDistributionActionsBean;
import fr.dila.reponses.web.mailbox.ReponsesWebActions;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.web.feuilleroute.DocumentRoutingWebActionsBean;
import fr.dila.st.web.action.NavigationWebActionsBean;
import fr.dila.st.web.context.NavigationContextBean;

/**
 * WebBean permettant de gérer les corbeilles de l'application.
 * 
 * @author admin
 */
@Name("corbeilleActions")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.FRAMEWORK + 1)
public class CorbeilleActionsBean extends fr.dila.st.web.corbeille.CorbeilleActionsBean implements Serializable {
	/**
	 * Serial UID.
	 */
	private static final long								serialVersionUID	= -6601690797613742328L;

	/**
	 * Logger.
	 */
	// private static final Log log = LogFactory.getLog(CorbeilleActionsBean.class);

	@In(create = true, required = true)
	protected transient CoreSession							documentManager;

	@In(create = true, required = false)
	protected transient NavigationContextBean				navigationContext;

	@In(create = true, required = false)
	protected transient DocumentRoutingWebActionsBean		routingWebActions;

	@In(create = true, required = false)
	protected transient CaseManagementMailboxActionsBean	cmMailboxActions;

	@In(create = true, required = false)
	protected transient ActionManager						actionManager;

	@In(create = true)
	protected transient NavigationWebActionsBean			navigationWebActions;

	@In(create = true, required = false)
	protected transient CorbeilleTreeBean					corbeilleTree;

	@In(create = true)
	protected transient ReponsesWebActions					webActions;

	@In(create = true)
	protected transient DocumentsListsManager				documentsListsManager;

	@In(required = true, create = true)
	protected transient SSPrincipal							ssPrincipal;
	
	@In(create = true, required = false)
	protected FacesMessages									facesMessages;

	/**
	 * DossierLink courant pour cet utilisateur. Les actions de distribution du dossier (donner avis favorable, etc.)
	 * sont basées sur la présence de ce DossierLink.
	 */
	private DossierLink										currentDossierLink;

	private String											currentView			= ReponsesViewConstant.ESPACE_CORBEILLE_VIEW;

	/**
	 * Navigue vers l'espace de travail.
	 * 
	 * @return Écran d'accueil de l'espace de travail
	 * @throws ClientException
	 */
	public String navigateToEspaceTravail() throws ClientException {
		navigationContext.resetCurrentDocument();

		// charge l'arbre des corbeilles
		corbeilleTree.forceRefresh();

		// Renseigne le menu du haut
		Action mainMenuAction = actionManager.getAction(ReponsesActionConstant.ESPACE_TRAVAIL);
		navigationWebActions.setCurrentMainMenuAction(mainMenuAction);
		navigationWebActions.setLeftPanelIsOpened(true);

		currentView = ReponsesViewConstant.ESPACE_CORBEILLE_VIEW;

		// Renseigne la vue de route des étapes de feuille de route vers les dossiers
		routingWebActions.setFeuilleRouteView(currentView);

		// Charge le menu
		Action action = actionManager.getAction(ReponsesConstant.LEFT_MENU_CORBEILLE_ACTION);
		navigationWebActions.setCurrentLeftMenuAction(action);

		return ReponsesViewConstant.ESPACE_CORBEILLE_VIEW;
	}

	@Override
	public boolean isDossierLoadedInCorbeille() {
		// Vérifie que le DossierLink est présent
		return currentDossierLink != null;
	}

	public String readDossierLink(String caseLinkId, String caseDocumentId, Boolean read) throws ClientException {
		return readDossierLink(caseLinkId, caseDocumentId, read, null);
	}

	public String readDossierLink(String caseLinkId, String caseDocumentId, Boolean read, String tabId)
			throws ClientException {
		DocumentModel caseLinkDoc = documentManager.getDocument(new IdRef(caseLinkId));

		if (!read) {
			DossierLink dossierLink = caseLinkDoc.getAdapter(DossierLink.class);
			dossierLink.setReadState(Boolean.TRUE);
			caseLinkDoc = documentManager.saveDocument(dossierLink.getDocument());

			Events.instance().raiseEvent(ProviderBean.REFRESH_CONTENT_VIEW_EVENT);
		}
		caseLinkDoc.refresh();
		currentDossierLink = caseLinkDoc.getAdapter(DossierLink.class);
		try {
			navigationContext.navigateToId(caseDocumentId);
		} catch (Exception e){
			facesMessages.add(StatusMessage.Severity.WARN, "Impossible de récupérer le dossier correspondant à ce Dossier Link.");
		}

		if (tabId != null) {
			webActions.setCurrentTabId(tabId);
		}

		return currentView;
	}

	@Factory(value = "currentDossierLink", scope = EVENT)
	public DossierLink getCurrentDossierLink() {
		return currentDossierLink;
	}

	/**
	 * Renseigne le dossierLink courant.
	 * 
	 * @param dossierLink
	 *            DossierLink
	 */
	public void setCurrentDossierLink(DossierLink dossierLink) {
		currentDossierLink = dossierLink;
	}

	public void forceRefresh(boolean keepCorbeille) throws ClientException {
		corbeilleTree.forceRefresh(keepCorbeille);
	}
	
	public void forceRefresh() throws ClientException {
		forceRefresh(false);
	}

	/**
	 * Permet de décharger le DossierLink et de raffraichir la corbeille.
	 * 
	 * @return Vue
	 * @throws ClientException
	 */
	public String dechargerDossierLink(boolean keepCorbeille) throws ClientException {
		DocumentModel doc = currentDossierLink == null ? null : currentDossierLink.getDocument();

		currentDossierLink = null;
		forceRefresh(keepCorbeille);
		Events.instance().raiseEvent(ProviderBean.REFRESH_CONTENT_VIEW_EVENT);

		if (doc != null) {
			documentsListsManager.removeFromWorkingList(
					DossierDistributionActionsBean.retrieveSelectionListAccordingView(getCurrentView()), doc);
		}
		Events.instance().raiseEvent(ReponsesConstant.CORBEILLE_SELECTION + "Updated");
		return getCurrentView();
	}
	
	public String dechargerDossierLink() throws ClientException {
		return dechargerDossierLink(false);
	}

	public String getCurrentView() {
		return currentView;
	}

	/**
	 * Setter de currentView.
	 * 
	 * @param currentView
	 *            currentView
	 */
	public void setCurrentView(String currentView) {
		this.currentView = currentView;
	}

	public String createDossier() throws ClientException {
		DocumentModel changeableDocument = documentManager.createDocumentModel(DossierConstants.QUESTION_DOCUMENT_TYPE);
		navigationContext.setChangeableDocument(changeableDocument);
		return navigationContext.getActionResult(changeableDocument, UserAction.CREATE);
	}

	/**
	 * Recharge le dossier link
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String refreshDossierLink() throws ClientException {
		DocumentModel doc = currentDossierLink == null ? null : currentDossierLink.getDocument();

		if (doc != null) {
			String dossierLinkId = currentDossierLink.getDocument().getId();
			currentDossierLink = documentManager.getDocument(new IdRef(dossierLinkId)).getAdapter(DossierLink.class);
			documentsListsManager.removeFromWorkingList(
					DossierDistributionActionsBean.retrieveSelectionListAccordingView(getCurrentView()), doc);
			Events.instance().raiseEvent(ReponsesConstant.CORBEILLE_SELECTION + "Updated");
			// try {
			// // ajouté pour le propblème de comptage d'une nouvelle corbeille
			// Events.instance().raiseEvent("corbeilleChanged");
			// } catch (IllegalArgumentException iae) {
			// // rien à faire
			// // il s'agit du cas de l'allotissement, on se passera de la correction dans ce cas là
			// }
		}

		return getCurrentView();
	}

	/**
	 * Recharge le dossier link sans lancer l'evenement corbeilleChanged
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String refreshDossierLinkSimple() throws ClientException {
		DocumentModel doc = currentDossierLink == null ? null : currentDossierLink.getDocument();

		if (doc != null) {
			String dossierLinkId = currentDossierLink.getDocument().getId();
			currentDossierLink = documentManager.getDocument(new IdRef(dossierLinkId)).getAdapter(DossierLink.class);
			documentsListsManager.removeFromWorkingList(
					DossierDistributionActionsBean.retrieveSelectionListAccordingView(getCurrentView()), doc);
			Events.instance().raiseEvent(ReponsesConstant.CORBEILLE_SELECTION + "Updated");
		}
		return getCurrentView();
	}
}
