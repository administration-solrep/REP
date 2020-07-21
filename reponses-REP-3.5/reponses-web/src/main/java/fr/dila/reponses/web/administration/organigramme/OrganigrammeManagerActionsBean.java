package fr.dila.reponses.web.administration.organigramme;

import java.util.Collections;
import java.util.List;

import org.jboss.annotation.ejb.SerializedConcurrentAccess;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.st.api.constant.STViewConstant;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.core.organigramme.ProtocolarOrderComparator;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.web.administration.organigramme.OrganigrammeTreeBean;

/**
 * ActionBean de gestion de l'organigramme
 * 
 * @author FEO
 */
@Name("organigrammeManagerActions")
@SerializedConcurrentAccess
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.APPLICATION + 1)
public class OrganigrammeManagerActionsBean extends fr.dila.ss.web.admin.organigramme.OrganigrammeManagerActionsBean {

	private static final long	serialVersionUID	= 7758881641656108468L;

	/**
	 * Retourne la liste des ministères du gouvernement courant
	 * 
	 * @return une liste de noeuds organigramme
	 * @throws ClientException
	 */
	public List<EntiteNode> getCurrentMinisteres() throws ClientException {
		final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();

		return ministeresService.getCurrentMinisteres();
	}

	/**
	 * Retourne la liste des ministères du gouvernement courant, trié par ordre protocolaire
	 * 
	 * @return une liste de noeuds organigramme
	 * @throws ClientException
	 */
	public List<EntiteNode> getSortedCurrentMinisteres() throws ClientException {
		List<EntiteNode> currentMinisteres = getCurrentMinisteres();
		Collections.sort(currentMinisteres, new ProtocolarOrderComparator());
		return currentMinisteres;
	}
	
	/**
	 * Mise à jour de l'entité
	 * 
	 * @return la vue organigramme
	 * @throws ClientException
	 */
	@Override
	public String updateEntite() throws ClientException {
		final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
		organigrammeService = STServiceLocator.getOrganigrammeService();
		// vérifie qu'on a toujours le lock
		OrganigrammeNode nodeToUpdate = selectedNodeModel;
		OrganigrammeNode node = ministeresService.getEntiteNode(nodeToUpdate.getId());
		if (node == null || !isCurrentUserUnlocker(node.getLockUserName())) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("info.organigrammeManager.lost.lock"));
			return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
		}
		try {
			ministeresService.updateEntite((EntiteNode) selectedNodeModel);
		} finally {
			unlockOrganigrammeNode(selectedNodeModel);
		}
		selectedNodeModel = null;
		facesMessages.add(StatusMessage.Severity.INFO,
				resourcesAccessor.getMessages().get("info.organigrammeManager.entiteModified"));
		// event pour recharger l'organigramme
		Events.instance().raiseEvent(OrganigrammeTreeBean.ORGANIGRAMME_CHANGED_EVENT);
		nameAlreadyUsed = false;
		return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
	}

	public String getLabelCurrentGouvernement() throws ClientException {

		return STServiceLocator.getSTGouvernementService().getGouvernement(currentGouvernement).getLabel();

	}

	public String getLabelNextGouvernement() throws ClientException {

		return STServiceLocator.getSTGouvernementService().getGouvernement(nextGouvernement).getLabel();

	}

	@Override
	public String editNode() throws ClientException {

		String view = null;

		if (selectedNodeType == null) {
			view = STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("organigramme.group.notfound"));
		} else {

			loadSelectedNodeModel();

			if (!lockOrganigrammeNode(selectedNodeModel)) {
				return null;
			}

			OrganigrammeType nodeType = OrganigrammeType.getEnum(selectedNodeType);

			if (OrganigrammeType.MINISTERE.equals(nodeType)) {
				view = "edit_entite_organigramme";
			} else if (OrganigrammeType.DIRECTION.equals(nodeType)
					|| OrganigrammeType.UNITE_STRUCTURELLE.equals(nodeType)) {
				view = "edit_unite_structurelle_organigramme";
			} else if (OrganigrammeType.POSTE.equals(nodeType)) {
				PosteNode posteNode = (PosteNode) selectedNodeModel;
				if (posteNode.getWsUrl() == null || posteNode.getWsUrl().isEmpty()) {
					view = "edit_poste_organigramme";
				} else {
					view = "edit_poste_ws_organigramme";
				}
			} else if (OrganigrammeType.GOUVERNEMENT.equals(nodeType)) {
				view = "edit_gouvernement_organigramme";
			}
		}

		return view;
	}

	public String getMinistereLabel(String ministereLabel) {
		if (NEW_TIMBRE_UNCHANGED_ENTITY.equals(ministereLabel)) {
			return resourcesAccessor.getMessages().get("label.organigrammeManager.migration.ministere.inchange");
		} else if (NEW_TIMBRE_DEACTIVATE_ENTITY.equals(ministereLabel)) {
			return resourcesAccessor.getMessages().get("label.organigrammeManager.migration.ministere.desactive");
		} else {
			return ministereLabel;
		}
	}
}