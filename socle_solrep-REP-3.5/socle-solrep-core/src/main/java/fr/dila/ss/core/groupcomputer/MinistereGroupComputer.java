package fr.dila.ss.core.groupcomputer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.platform.computedgroups.AbstractGroupComputer;
import org.nuxeo.ecm.platform.usermanager.NuxeoPrincipalImpl;

import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.security.principal.STPrincipal;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Ce calculateur de groupe injecte dans le principal les groupes correspondant aux ministères.
 * 
 * @author jtremeaux
 */
public class MinistereGroupComputer extends AbstractGroupComputer {

	private static final Log	LOG	= LogFactory.getLog(MinistereGroupComputer.class);

	/**
	 * Default constructor
	 */
	public MinistereGroupComputer() {
		super();
	}

	@Override
	public List<String> getGroupsForUser(NuxeoPrincipalImpl nuxeoPrincipal) throws Exception {
		if (nuxeoPrincipal == null) {
			return Collections.emptyList();
		}

		// Récupère les groupes actuels du principal
		final List<String> groupList = nuxeoPrincipal.getGroups();

		try {
			final List<String> newGroupList = new ArrayList<String>(groupList);

			// Injecte les groupes correspondant aux ministères
			final Set<String> ministereGroupSet = getMinistereGroupSet((STPrincipal) nuxeoPrincipal);
			newGroupList.addAll(ministereGroupSet);

			return newGroupList;
		} catch (Exception e) {
			LOG.error("Impossible d'associer les groupes ministères à l'utilisateur connecté.", e);
			return groupList;
		}
	}

	/**
	 * Recherche l'ensemble des ministères d' un utilisateur, puis construit et retourne les groupes correspondant à ces
	 * ministères.
	 * 
	 * @param principal
	 *            Principal
	 * @return Ensemble des groupes
	 * @throws DirectoryException
	 *             DirectoryException
	 * @throws ClientException
	 *             ClientException
	 */
	private Set<String> getMinistereGroupSet(STPrincipal principal) throws ClientException {
		// Récupère la liste des postes de l'utilisateur
		final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
		final Set<String> posteIdSet = principal.getPosteIdSet();
		final Set<String> groupSet = new HashSet<String>();
		for (String posteId : posteIdSet) {

			// Recherche l'ensemble des ministères associés au poste
			final List<EntiteNode> ministereNodeList = ministeresService.getMinistereParentFromPoste(posteId);
			for (OrganigrammeNode ministereNode : ministereNodeList) {

				final String ministereId = ministereNode.getId();
				groupSet.add(MinistereGroupeHelper.ministereidToGroup(ministereId));
			}
		}

		return groupSet;
	}

	@Override
	public List<String> getParentsGroupNames(String groupName) throws Exception {
		return Collections.emptyList();
	}

	@Override
	public List<String> getSubGroupsNames(String groupName) throws Exception {
		return Collections.emptyList();
	}

	/**
	 * Retourne faux: aucune fonction unitaire ne doit être vue comme un groupe.
	 */
	@Override
	public boolean hasGroup(String name) throws Exception {
		return false;
	}

	/**
	 * Returns an empty list for efficiency
	 */
	@Override
	public List<String> getAllGroupIds() throws Exception {
		return Collections.emptyList();
	}

	/**
	 * Returns an empty list as mailboxes are not searchable
	 */
	@Override
	public List<String> searchGroups(Map<String, Serializable> filter, HashSet<String> fulltext) throws Exception {
		return Collections.emptyList();
	}

	@Override
	public List<String> getGroupMembers(String groupName) throws Exception {
		return Collections.emptyList();
	}
}
