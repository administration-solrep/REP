package fr.dila.ss.web.admin.organigramme;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.webapp.security.UserSuggestionActionsBean;

import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Auto-complétion de l'organigramme.
 * 
 * @author bgamard
 */
@Name("organigrammeSuggestionActions")
@Scope(ScopeType.SESSION)
public class OrganigrammeSuggestionActionsBean implements Serializable {
	/**
	 * Serial UID.
	 */
	private static final long					serialVersionUID	= 1L;

	@In(create = true, required = false)
	protected CoreSession						documentManager;

	@In(create = true, required = false)
	protected OrganigrammeManagerActionsBean	organigrammeManagerActions;

	@In(create = true, required = false)
	protected UserSuggestionActionsBean			userSuggestionActions;

	@RequestParameter
	protected String							selectionType;

	@RequestParameter
	protected String							activatePosteFilter;

	/**
	 * Constructeur de OrganigrammeSuggestionActionsBean.
	 */
	public OrganigrammeSuggestionActionsBean() {
		// do nothing
	}

	/**
	 * Retourne une suggestion d'élément du LDAP selon le type en paramètre.
	 * 
	 * @param input
	 * @return
	 * @throws ClientException
	 */
	public List<OrganigrammeSuggestionDto> getSuggestions(Object input) throws ClientException {
		List<OrganigrammeSuggestionDto> out = new ArrayList<OrganigrammeSuggestionDto>();
		if (input == null || selectionType == null) {
			return out;
		}
		OrganigrammeType directoryName = null;

		if (selectionType.equals("POSTE_TYPE") || selectionType.equals("MAILBOX_TYPE")) {
			directoryName = OrganigrammeType.POSTE;
		} else if (selectionType.equals("GVT_TYPE")) {
			directoryName = OrganigrammeType.GOUVERNEMENT;
		} else if (selectionType.equals("MIN_TYPE")) {
			directoryName = OrganigrammeType.MINISTERE;
		} else if (selectionType.equals("DIR_TYPE") || selectionType.equals("UST_TYPE")
				|| selectionType.equals("DIR_AND_UST_TYPE")) {
			directoryName = OrganigrammeType.UNITE_STRUCTURELLE;
		} else if (selectionType.equals("USER_TYPE")) {

			// Cas spécial pour les utilisateurs
			List<DocumentModel> usersDoc = userSuggestionActions.getUserSuggestions(input);
			for (DocumentModel userDoc : usersDoc) {
				STUser user = userDoc.getAdapter(STUser.class);
				if (!user.isActive()) {
					continue;
				}

				OrganigrammeSuggestionDto suggestion = new OrganigrammeSuggestionDto();
				suggestion.setId(userDoc.getId());
				suggestion.setLabel(StringUtils.stripToEmpty(user.getFirstName()) + " "
						+ StringUtils.stripToEmpty(user.getLastName()));
				out.add(suggestion);
			}
			return out;
		} else {
			return out;
		}

		if (directoryName != null) {
			String pattern = (String) input;
			List<OrganigrammeNode> results = STServiceLocator.getOrganigrammeService().getOrganigrameLikeLabel(pattern,
					directoryName);
			final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
			for (OrganigrammeNode node : results) {

				if (node.getDeleted() || !node.isActive()) {
					continue;
				}

				// Cas des postes, une vérification est faite
				if (selectionType.equals("POSTE_TYPE") || selectionType.equals("MAILBOX_TYPE")) {
					if (activatePosteFilter == null || activatePosteFilter.equals("true")) {
						List<EntiteNode> entiteNodes = ministeresService.getMinistereParentFromPoste(node.getId());
						boolean allowed = false;
						for (EntiteNode entiteNode : entiteNodes) {
							if (entiteNode != null && organigrammeManagerActions.allowAddPoste(entiteNode.getId())) {
								allowed = true;
							}
						}
						if (!allowed) {
							continue;
						}
					}
				}

				// Ajout du node aux suggestions
				OrganigrammeSuggestionDto suggestion = new OrganigrammeSuggestionDto();
				if (selectionType.equals("MAILBOX_TYPE")) {
					suggestion.setId(organigrammeManagerActions.getMailboxIdFromPosteId(node.getId()));
				} else {
					suggestion.setId(node.getId().toString());
				}
				suggestion.setLabel(node.getLabel());
				out.add(suggestion);
			}
		}

		return out;
	}

	/**
	 * Résultat de la suggestion.
	 * 
	 * @author bgamard
	 * 
	 */
	public static class OrganigrammeSuggestionDto {

		/** Identifiant dans le LDAP */
		private String	id;

		/** Label dans le LDAP */
		private String	label;

		/**
		 * Default constructor
		 */
		public OrganigrammeSuggestionDto() {
			// do nothing
		}

		/**
		 * @return the id
		 */
		public String getId() {
			return id;
		}

		/**
		 * @param id
		 *            the id to set
		 */
		public void setId(String id) {
			this.id = id;
		}

		/**
		 * @return the label
		 */
		public String getLabel() {
			return label;
		}

		/**
		 * @param label
		 *            the label to set
		 */
		public void setLabel(String label) {
			this.label = label;
		}
	}
}
