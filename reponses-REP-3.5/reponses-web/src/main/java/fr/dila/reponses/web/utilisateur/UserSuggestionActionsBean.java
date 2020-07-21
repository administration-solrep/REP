package fr.dila.reponses.web.utilisateur;

import static org.jboss.seam.ScopeType.PAGE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jboss.annotation.ejb.SerializedConcurrentAccess;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoGroup;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.service.STServiceLocator;

/**
 * @author asatre
 * @see org.nuxeo.ecm.webapp.security.UserSuggestionActionsBean
 */
@Name("userSuggestionActions")
@SerializedConcurrentAccess
@Scope(PAGE)
@Install(precedence = Install.APPLICATION + 1)
public class UserSuggestionActionsBean extends org.nuxeo.ecm.webapp.security.UserSuggestionActionsBean {

	private static final long		serialVersionUID	= 2774501488951539686L;

	@In(required = true, create = true)
	protected transient SSPrincipal	ssPrincipal;

	public List<DocumentModel> getUserSuggestions(Object input) throws ClientException {
		String searchPattern = (String) input;
		// if (searchPattern != null && !searchPattern.isEmpty()) {
		// if (!searchPattern.endsWith("*")) {
		// searchPattern = searchPattern + "*";
		// }
		// }
		return super.getUserSuggestions(searchPattern);
	}

	@Override
	public Object getSuggestions(Object input) throws ClientException {
		if (equals(cachedUserSuggestionSearchType, userSuggestionSearchType)
				&& equals(cachedUserSuggestionMaxSearchResults, userSuggestionMaxSearchResults)
				&& equals(cachedInput, input)) {
			return cachedSuggestions;
		}

		List<DocumentModel> users = Collections.emptyList();
		if (USER_TYPE.equals(userSuggestionSearchType) || StringUtils.isEmpty(userSuggestionSearchType)) {
			users = getUserSuggestions(input);
		}

		List<DocumentModel> groups = Collections.emptyList();
		if (GROUP_TYPE.equals(userSuggestionSearchType) || StringUtils.isEmpty(userSuggestionSearchType)) {
			groups = getGroupsSuggestions(input);
		}

		int userSize = users.size();
		int groupSize = groups.size();
		int totalSize = userSize + groupSize;

		if (userSuggestionMaxSearchResults != null && userSuggestionMaxSearchResults > 0) {
			if (userSize > userSuggestionMaxSearchResults || groupSize > userSuggestionMaxSearchResults
					|| totalSize > userSuggestionMaxSearchResults) {
				addSearchOverflowMessage();
				return null;
			}
		}

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>(totalSize);

		for (DocumentModel user : users) {
			Map<String, Object> entry = new HashMap<String, Object>();
			entry.put(TYPE_KEY_NAME, USER_TYPE);
			entry.put(ENTRY_KEY_NAME, user);
			String userId = user.getId();
			entry.put(ID_KEY_NAME, userId);
			entry.put(PREFIXED_ID_KEY_NAME, NuxeoPrincipal.PREFIX + userId);
			result.add(entry);
		}

		for (DocumentModel group : groups) {
			Map<String, Object> entry = new HashMap<String, Object>();
			entry.put(TYPE_KEY_NAME, GROUP_TYPE);
			entry.put(ENTRY_KEY_NAME, group);
			String groupId = group.getId();
			entry.put(ID_KEY_NAME, groupId);
			entry.put(PREFIXED_ID_KEY_NAME, NuxeoGroup.PREFIX + groupId);
			if (ssPrincipal.getGroups().contains(STBaseFunctionConstant.ADMIN_FONCTIONNEL_GROUP_NAME)
					|| !STBaseFunctionConstant.ADMIN_FONCTIONNEL_GROUP_NAME.equals(groupId)) {

				// Les administrateurs ministériels ne peuvent pas ajouter les profils Superviseur SGG, Contributeur
				// SPM, Vigie du SGG
				if (ssPrincipal.getGroups().contains(STBaseFunctionConstant.ADMIN_MINISTERIEL_GROUP_NAME)
						&& ("Superviseur SGG".equals(groupId) || "Contributeur SPM".equals(groupId) || "Vigie du SGG"
								.equals(groupId))) {
					continue;
				}

				result.add(entry);
			}
		}

		cachedInput = input;
		cachedUserSuggestionSearchType = userSuggestionSearchType;
		cachedUserSuggestionMaxSearchResults = userSuggestionMaxSearchResults;
		cachedSuggestions = result;

		return result;
	}

	public Object getSuggestionSuiviBatchNotification(Object input) throws ClientException {
		List<DocumentModel> users = Collections.emptyList();
		if (USER_TYPE.equals(userSuggestionSearchType) || StringUtils.isEmpty(userSuggestionSearchType)) {
			users = getUserSuggestions(input);
		}
		final List<STUser> adminUsers = STServiceLocator.getProfileService().getUsersFromBaseFunction(
				STBaseFunctionConstant.BATCH_READER);
		if (users == null || users.size() == 0 || adminUsers == null) {
			return null;
		}
		List<String> result = new ArrayList<String>();
		for (DocumentModel user : users) {
			STUser STuser = user.getAdapter(STUser.class);
			if (adminUsers.contains(STuser)) {
				result.add(STuser.getUsername());
			}
		}
		return result;
	}

}
