package fr.dila.reponses.web.suivi;

import static org.jboss.seam.ScopeType.EVENT;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.actions.ejb.ActionManager;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;
import org.nuxeo.ecm.platform.ui.web.api.WebActions;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManagerBean;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;
import org.nuxeo.runtime.api.Framework;

import fr.dila.cm.web.mailbox.CaseManagementMailboxTabsActionsBean;
import fr.dila.reponses.api.constant.ReponsesActionConstant;
import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.reponses.api.constant.ReponsesViewConstant;
import fr.dila.reponses.web.corbeille.CorbeilleActionsBean;
import fr.dila.ss.web.feuilleroute.DocumentRoutingWebActionsBean;
import fr.dila.st.api.constant.STAlertConstant;
import fr.dila.st.api.constant.STLifeCycleConstant;
import fr.dila.st.api.constant.STRequeteConstants;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.web.action.NavigationWebActionsBean;
import fr.dila.st.web.context.NavigationContextBean;

/**
 * WebBean permettant de gérer l'espace de suivi.
 * 
 * @author jtremeaux
 */
@Name("suiviActions")
@Scope(ScopeType.CONVERSATION)
public class SuiviActionsBean implements Serializable {

	private static final String									SUIVI_VIEW				= "suivi";

	private static final String									CREATE_REQUETEUR_ACTION	= "create_requeteur";

	private static final long									serialVersionUID		= 1L;

	private static final String									SUIVI_ACTION_LIST		= "SUIVI_ACTION_LIST";

	protected List<Action>										viewSuiviActionTabs;

	@In(create = true, required = false)
	protected transient WebActions								webActions;

	@In(create = true, required = true)
	protected transient CoreSession								documentManager;

	@In(create = true, required = false)
	protected transient CaseManagementMailboxTabsActionsBean	cmMailboxTabsActionsBean;

	@In(required = true, create = true)
	protected transient ActionManager							actionManager;

	@In(create = true, required = true)
	protected ContentViewActions								contentViewActions;

	@In(create = true, required = true)
	protected transient NavigationContextBean					navigationContext;

	@In(create = true)
	protected transient NavigationWebActionsBean				navigationWebActions;

	@In(create = true, required = true)
	protected transient CorbeilleActionsBean					corbeilleActions;

	@In(create = true, required = false)
	protected transient DocumentRoutingWebActionsBean			routingWebActions;

	@In(create = true)
	protected transient DocumentsListsManagerBean				documentsListsManager;

	@In(create = true, required = false)
	protected transient FacesMessages							facesMessages;

	@In(create = true)
	protected transient ResourcesAccessor						resourcesAccessor;

	/**
	 * Logger formalisé en surcouche du logger apache/log4j
	 */
	private static final STLogger								LOGGER					= STLogFactory
																								.getLog(SuiviActionsBean.class);

	/**
	 * Navigue vers des résulats de l'espace suvi.
	 * 
	 * @return Ecran d'accueil du suivi
	 * @throws ClientException
	 */
	public String navigateToEspaceSuivi() throws ClientException {
		navigationContext.resetCurrentDocument();

		// Renseigne le menu du haut
		Action mainMenuAction = actionManager.getAction(ReponsesActionConstant.ESPACE_SUIVI);
		navigationWebActions.setCurrentMainMenuAction(mainMenuAction);
		navigationWebActions.setLeftPanelIsOpened(true);
		// Renseigne le menu de gauche
		navigationWebActions.setCurrentLeftMenuAction(null);

		corbeilleActions.setCurrentView(ReponsesViewConstant.ESPACE_SUIVI);
		routingWebActions.setFeuilleRouteView(ReponsesViewConstant.ESPACE_SUIVI);

		documentsListsManager.resetWorkingList(ReponsesConstant.RECHERCHE_SELECTION);

		initializeSuivi();

		return ReponsesViewConstant.ESPACE_SUIVI;
	}

	/**
	 * Donne la liste des requêtes sauvegardé.
	 * 
	 * @return
	 * @throws ClientException
	 */
	public DocumentModelList getSavedRequetes() throws ClientException {
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM ").append(STRequeteConstants.SMART_FOLDER_DOCUMENT_TYPE).append("WHERE ")
				.append(STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH).append(" != 'deleted' AND ")
				.append(STSchemaConstant.ECM_ISPROXY_XPATH).append(" = 0 ");
		DocumentModelList docs = documentManager.query(query.toString());
		return docs;
	}

	/**
	 * @return Toutes les actions pour le suivi
	 */
	@Factory(value = "viewSuiviActionTabs", scope = EVENT)
	public List<Action> getViewSuiviActionTabs() {
		if (viewSuiviActionTabs == null) {
			viewSuiviActionTabs = webActions.getActionsList(SUIVI_ACTION_LIST);
		}
		return viewSuiviActionTabs;
	}

	/**
	 * Initialise l'espace de suivi et navigue vers celui-ci.
	 * 
	 * @return Vue
	 * @throws ClientException
	 */
	public String initializeSuivi() {
		setCurrentSuiviSubcontainerAction(CREATE_REQUETEUR_ACTION);
		return SUIVI_VIEW;
	}

	public String isEditingAlert() throws ClientException {
		if (navigationContext.getCurrentDocument() != null
				&& navigationContext.getCurrentDocument().getDocumentType() != null
				&& STAlertConstant.ALERT_DOCUMENT_TYPE.equals(navigationContext.getCurrentDocument().getDocumentType()
						.getName())) {
			return "";
		}
		return navigateToEspaceSuivi();
	}

	/**
	 * Place l'action du bas de l'écran de suivi (le requêteur et l'édition d'une alerte).
	 * 
	 * @param actionId
	 * @throws ClientException
	 */
	public void setCurrentSuiviSubcontainerAction(String actionId) {
		webActions.setCurrentTabAction(actionManager.getAction(actionId));
	}

	/**
	 * Supprime le document requête.
	 * 
	 * @param doc
	 * @return
	 * @throws ClientException
	 */
	public String delete(DocumentModel doc) throws ClientException {
		// Soft delete par défaut
		if ("true".equals(Framework.getProperty("socle.transverse.alert.soft.delete", "true"))) {
			try {
				documentManager.followTransition(doc.getRef(), STLifeCycleConstant.TO_DELETE_TRANSITION);
				documentManager.save();
			} catch (ClientException ce) {
				LOGGER.error(documentManager, STLogEnumImpl.FAIL_UPDATE_REQUETE_TEC, doc, ce);
				facesMessages.add(StatusMessage.Severity.INFO,
						resourcesAccessor.getMessages().get("suivi.error.requeteDeletion"));
			}
		} else {
			try {
				LOGGER.info(documentManager, STLogEnumImpl.DEL_REQ_EXP_TEC, doc);
				documentManager.removeDocument(doc.getRef());
				documentManager.save();
			} catch (ClientException ce) {
				LOGGER.error(documentManager, STLogEnumImpl.FAIL_DEL_REQ_EXP_TEC, doc, ce);
				facesMessages.add(StatusMessage.Severity.INFO,
						resourcesAccessor.getMessages().get("suivi.error.requeteDeletion"));
			}
		}
		contentViewActions.refresh("suivi_saved_requete_content");
		navigationContext.resetCurrentDocument();
		return null;
	}

}
