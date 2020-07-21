package fr.dila.reponses.core.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.api.user.STUser;

public class MockPostesService implements STPostesService {

	@Override
	public PosteNode getPoste(String posteId) throws ClientException {
		OrganigrammeNode node = new MockPosteNode();
		node.setLabel("jhjhj");
		node.setId(posteId);
		return (PosteNode) node;
	}

	@Override
	public PosteNode getSGGPosteNode() throws ClientException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PosteNode getBarePosteModel() throws ClientException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updatePoste(CoreSession coreSession, PosteNode poste) throws ClientException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isPosteBdcInEachEntiteFromGouvernement(OrganigrammeNode gouvernementNode) throws ClientException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<STUser> getUserFromPoste(String posteId) throws ClientException {
		return new ArrayList<STUser>();
	}

	@Override
	public List<PosteNode> getPostesNodes(Collection<String> postesId) throws ClientException {
		return new ArrayList<PosteNode>();
	}

	@Override
	public void deactivateBdcPosteList(List<OrganigrammeNode> bdcList) throws ClientException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<PosteNode> getPosteBdcNodeList() throws ClientException {
		return new ArrayList<PosteNode>();
	}

	@Override
	public void addBdcPosteToNewPosteBdc(Map<String, List<OrganigrammeNode>> posteBdcToMigrate) throws ClientException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getPosteIdInSubNode(OrganigrammeNode node) throws ClientException {
		return new ArrayList<String>();
	}

	@Override
	public List<OrganigrammeNode> getPosteBdcListInEntite(String entiteId) throws ClientException {
		return new ArrayList<OrganigrammeNode>();
	}

	@Override
	public PosteNode getPosteBdcInEntite(String entiteId) throws ClientException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createPoste(CoreSession coreSession, PosteNode newPoste) throws ClientException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean userHasOnePosteOnly(PosteNode posteNode) throws ClientException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> getUserNamesFromPoste(String posteId) throws ClientException {
		return new ArrayList<String>();
	}

	@Override
	public List<EntiteNode> getEntitesParents(PosteNode poste) throws ClientException {
		return new ArrayList<EntiteNode>();
	}

	@Override
	public List<UniteStructurelleNode> getUniteStructurelleParentList(PosteNode poste) throws ClientException {
		return new ArrayList<UniteStructurelleNode>();
	}

	@Override
	public List<PosteNode> getAllPostesForUser(String userId) throws ClientException {
		return new ArrayList<PosteNode>();
	}

	@Override
	public List<String> getAllPosteNameForUser(String userId) throws ClientException {
		return new ArrayList<String>();
	}

	@Override
	public List<PosteNode> getPosteNodeEnfant(String nodeId, OrganigrammeType type) throws ClientException {
		return new ArrayList<PosteNode>();
	}

	@Override
	public void addUserToPostes(List<String> postes, String username) throws ClientException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getAllPosteIdsForUser(String userId) throws ClientException {
		return new ArrayList<String>();
	}

	@Override
	public void removeUserFromPoste(String poste, String userName) throws ClientException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<PosteNode> getAllPostes() {
		// TODO
		return new ArrayList<PosteNode>();
	}

	@Override
	public String deleteUserFromAllPostes(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

}
