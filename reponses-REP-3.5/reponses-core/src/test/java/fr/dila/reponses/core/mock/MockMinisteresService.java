package fr.dila.reponses.core.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.user.STUser;

public class MockMinisteresService implements STMinisteresService {

	@Override
	public EntiteNode getEntiteNode(String entiteId) throws ClientException {
		OrganigrammeNode node = new MockEntiteNode();
		node.setLabel("jhjhj");
		node.setId("1562");
		return (EntiteNode) node;
	}

	@Override
	public List<EntiteNode> getMinistereParentFromPoste(String posteId) throws ClientException {
		return new ArrayList<EntiteNode>();
	}

	@Override
	public List<EntiteNode> getMinistereParentFromPostes(Set<String> posteIds) throws ClientException {
		return new ArrayList<EntiteNode>();
	}

	@Override
	public List<EntiteNode> getMinistereParentFromUniteStructurelle(String ustId) throws ClientException {
		return new ArrayList<EntiteNode>();
	}

	@Override
	public List<STUser> getUserFromMinistere(String ministereId) throws ClientException {
		return new ArrayList<STUser>();
	}

	@Override
	public List<EntiteNode> getMinisteresParents(OrganigrammeNode node) throws ClientException {
		return new ArrayList<EntiteNode>();
	}

	@Override
	public void migrateEntiteToNewGouvernement(String currentMin, String newMin) throws ClientException {

	}

	@Override
	public void migrateUnchangedEntiteToNewGouvernement(String entiteId, String newGouvernementId)
			throws ClientException {

	}

	@Override
	public List<String> findUserFromMinistere(String ministereId) throws ClientException {
		return new ArrayList<String>();
	}

	@Override
	public EntiteNode createEntite(EntiteNode newEntite) throws ClientException {
		return null;
	}

	@Override
	public void updateEntite(EntiteNode entite) throws ClientException {

	}

	@Override
	public void getMinistereParent(OrganigrammeNode node, List<EntiteNode> resultList) throws ClientException {

	}

	@Override
	public List<EntiteNode> getEntiteNodeEnfant(String nodeID) throws ClientException {
		return new ArrayList<EntiteNode>();
	}

	@Override
	public List<EntiteNode> getCurrentMinisteres() throws ClientException {
		return new ArrayList<EntiteNode>();
	}

	@Override
	public List<EntiteNode> getAllMinisteres() throws ClientException {
		return new ArrayList<EntiteNode>();
	}

	@Override
	public List<EntiteNode> getMinisteres(boolean active) throws ClientException {
		return new ArrayList<EntiteNode>();
	}

	@Override
	public List<EntiteNode> getEntiteNodes(Collection<String> entiteIds) throws ClientException {
		return new ArrayList<EntiteNode>();
	}

	@Override
	public EntiteNode getBareEntiteModel() throws ClientException {
		return null;
	}
}
