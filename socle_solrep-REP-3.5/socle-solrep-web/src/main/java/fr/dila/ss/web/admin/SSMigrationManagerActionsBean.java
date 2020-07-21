package fr.dila.ss.web.admin;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.jboss.annotation.ejb.SerializedConcurrentAccess;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.ui.web.api.WebActions;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.web.admin.organigramme.OrganigrammeManagerActionsBean;
import fr.dila.st.api.constant.STViewConstant;
import fr.dila.st.api.organigramme.GouvernementNode;
import fr.dila.st.api.organigramme.LDAPSessionContainer;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STGouvernementService;
import fr.dila.st.core.organigramme.LDAPSessionContainerImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.web.administration.organigramme.OrganigrammeTreeBean;

/**
 * ActionBean de gestion des migrations
 */
@Name("migrationManagerActions")
@SerializedConcurrentAccess
@Scope(ScopeType.CONVERSATION)
public class SSMigrationManagerActionsBean implements Serializable {

	/**
	 * Serial version UID
	 */
	private static final long					serialVersionUID				= -2160907242992812866L;

	public static final String					NEW_TIMBRE_EMPTY_VALUE			= "empty_value";
	public static final String					NEW_TIMBRE_UNCHANGED_ENTITY		= "unchanged_entity";
	public static final String					NEW_TIMBRE_DEACTIVATE_ENTITY	= "deactivate_entity";

	protected transient OrganigrammeService		organigrammeService;

	@In(create = true, required = false)
	protected FacesMessages						facesMessages;

	@In(create = true)
	protected ResourcesAccessor					resourcesAccessor;

	@In(create = true, required = false)
	protected transient WebActions				webActions;

	@In(create = true, required = false)
	protected CoreSession						documentManager;

	@In(required = true, create = true)
	protected SSPrincipal						ssPrincipal;

	@In(create = true, required = false)
	protected OrganigrammeManagerActionsBean	organigrammeManagerActions;

	private GouvernementNode					selectedNodeModel;

	// Gestion du changement de timbre
	protected Map<String, String>				newTimbre;

	protected List<SelectItem>					newTimbreList;

	protected List<SelectItem>					gouvernementList;

	/**
	 * Default constructor
	 */
	public SSMigrationManagerActionsBean() {
		// do nothing
	}

	@Destroy
	public void destroy() {
		organigrammeService = null;
	}

	/**
	 * Mise à jour du gouvernement
	 * 
	 * @return la vue organigramme
	 * @throws ClientException
	 */
	public String updateGouvernement() throws ClientException {
		final STGouvernementService gouvService = STServiceLocator.getSTGouvernementService();

		// vérifie qu'on a toujours le lock
		final OrganigrammeNode nodeToUpdate = selectedNodeModel;
		final OrganigrammeNode node = gouvService.getGouvernement(nodeToUpdate.getId());
		if (node == null || !organigrammeManagerActions.isCurrentUserUnlocker(node.getLockUserName())) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("info.organigrammeManager.lost.lock"));
			return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
		}

		try {
			gouvService.updateGouvernement(selectedNodeModel);
		} finally {
			organigrammeManagerActions.unlockOrganigrammeNode(selectedNodeModel);
		}

		selectedNodeModel = null;
		facesMessages.add(StatusMessage.Severity.INFO,
				resourcesAccessor.getMessages().get("info.organigrammeManager.gouvernenementModified"));
		// event pour recharger l'organigramme
		Events.instance().raiseEvent(OrganigrammeTreeBean.ORGANIGRAMME_CHANGED_EVENT);

		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;

	}

	/**
	 * @return the newTimbre
	 */
	public Map<String, String> getNewTimbre() {
		if (newTimbre == null) {
			newTimbre = new HashMap<String, String>();
		}
		return newTimbre;
	}

	/**
	 * @param newTimbre
	 *            the newTimbre to set
	 */
	public void setNewTimbre(Map<String, String> newTimbre) {
		this.newTimbre = newTimbre;
	}

	public String updateTimbreView() throws ClientException {
		LDAPSessionContainer ldapSessionContainer = new LDAPSessionContainerImpl();
		try {
			List<GouvernementNode> gvtList = STServiceLocator.getSTGouvernementService().getActiveGouvernementList();

			GouvernementNode nextGNode = null;
			GouvernementNode currentGNode = null;
			if (gvtList.size() > 1) {
				nextGNode = gvtList.get(gvtList.size() - 1);
				currentGNode = gvtList.get(gvtList.size() - 2);
				organigrammeManagerActions.setCurrentGouvernement(currentGNode.getId());
				organigrammeManagerActions.setNextGouvernement(nextGNode.getId());
			}

			if (currentGNode != null
					&& !STServiceLocator.getSTPostesService().isPosteBdcInEachEntiteFromGouvernement(currentGNode)) {
				facesMessages.add(StatusMessage.Severity.WARN,
						resourcesAccessor.getMessages().get("info.organigrammeManager.previous.gvt.no.bdc"));
				return null;
			}

			// un nouveau gouvernement a été créé et chacune de ses entités contient un poste BDC
			// sinon message à l'utilisateur
			if (nextGNode != null) {
				if (!STServiceLocator.getSTPostesService().isPosteBdcInEachEntiteFromGouvernement(nextGNode)) {
					facesMessages.add(StatusMessage.Severity.WARN,
							resourcesAccessor.getMessages().get("info.organigrammeManager.next.gvt.no.bdc"));
					return null;
				}
			} else {
				facesMessages.add(StatusMessage.Severity.WARN,
						resourcesAccessor.getMessages().get("info.organigrammeManager.next.gvt.not.found"));
				return null;
			}

			return "update_timbre";
		} finally {
			ldapSessionContainer.closeAll();
		}
	}
}
