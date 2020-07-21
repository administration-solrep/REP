package fr.dila.reponses.core.operation.nxshell;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.EntityManager;

import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.persistence.PersistenceProvider.RunCallback;
import org.nuxeo.ecm.directory.ldap.LDAPSession;

import com.google.common.collect.Lists;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STOrganigrammeConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.GouvernementNode;
import fr.dila.st.api.organigramme.LDAPSessionContainer;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.core.operation.document.RepriseLDAP;
import fr.dila.st.core.organigramme.EntiteNodeImpl;
import fr.dila.st.core.organigramme.GouvernementNodeImpl;
import fr.dila.st.core.organigramme.LDAPSessionContainerImpl;
import fr.dila.st.core.organigramme.PosteNodeImpl;
import fr.dila.st.core.organigramme.UniteStructurelleNodeImpl;
import fr.dila.st.core.util.PropertyUtil;

@Operation(id = RepriseOrganigrammeOperation.ID, label = "RepriseOrganigramme")
public class RepriseOrganigrammeOperation extends RepriseLDAP {
	public static final String	ID	= "Reponses.Organigramme.Reprise";

	@OperationMethod
	public void run() throws Exception {
		LOG.info("-------------------------------------------------------------------------------");
		LOG.info("Début opération " + ID);

		try {
			getOrCreatePersistenceProvider().run(true, new RunCallback<Void>() {

				@Override
				public Void runWith(EntityManager manager) throws ClientException {
					reprise(manager);
					return null;
				}
			});

		} catch (Exception e) {
			LOG.error("Une erreur est survenue lors de la reprise " + e.getMessage(), e);
		}

		LOG.info("Fin opération " + ID);

	}

	/**
	 * Permet de récupérer l'intégralité des gouvernements sans les liens enfants
	 * 
	 * @return
	 */
	protected List<GouvernementNode> getAllGouvernements() {
		List<GouvernementNode> lstGouvs = new ArrayList<GouvernementNode>();
		LDAPSessionContainer ldapSessionContainer = new LDAPSessionContainerImpl();
		try {
			LDAPSession session = ldapSessionContainer.getSessionGouvernement();

			Map<String, Serializable> filter = new HashMap<String, Serializable>();
			Map<String, String> orderBy = new HashMap<String, String>();
			orderBy.put("groupName", "asc");
			List<DocumentModel> entries = session.query(filter, null, orderBy);

			for (DocumentModel group : entries) {
				GouvernementNode node = new GouvernementNodeImpl();
				DocumentModel loadedGroup = session.getEntry((String) group.getProperty(session.getDirectory()
						.getSchema(), STConstant.LDAP_GROUP_NAME_PROPERTY));

				mapOrganigrammeData(node, STSchemaConstant.ORGANIGRAMME_GOUVERNEMENT_SCHEMA, loadedGroup);

				List<String> children = PropertyUtil.getStringListProperty(loadedGroup,
						STSchemaConstant.ORGANIGRAMME_GOUVERNEMENT_SCHEMA, "subEntites");

				for (String child : children) {

					if (!child.equals("cn=emptyRef")) {
						mapEnfantEntParentGvt.put(child, node.getId());
					}
				}

				lstGouvs.add(node);
			}

		} catch (Exception e) {
			LOG.error("Erreur lors de la récupération des gouvernements", e);

		} finally {
			try {
				ldapSessionContainer.closeAll();
			} catch (ClientException e) {
				LOG.error("Erreur lors de la fermeture de la connection au LDAP", e);
			}
		}

		return lstGouvs;
	}

	/**
	 * Permet de récupérer l'intégralité des ministères sans le lien vers les parents
	 * 
	 * @return
	 */
	protected List<EntiteNode> getAllEntites() {
		List<EntiteNode> lstEntites = new ArrayList<EntiteNode>();

		LDAPSessionContainer ldapSessionContainer = new LDAPSessionContainerImpl();
		try {
			LDAPSession session = ldapSessionContainer.getSessionEntite();

			Map<String, Serializable> filter = new HashMap<String, Serializable>();
			Map<String, String> orderBy = new HashMap<String, String>();
			orderBy.put("groupName", "asc");

			List<DocumentModel> entries = session.query(filter, null, orderBy);

			for (DocumentModel group : entries) {

				DocumentModel loadedGroup = session.getEntry((String) group.getProperty(session.getDirectory()
						.getSchema(), STConstant.LDAP_GROUP_NAME_PROPERTY));
				EntiteNode node = new EntiteNodeImpl();

				mapOrganigrammeData(node, STSchemaConstant.ORGANIGRAMME_ENTITE_SCHEMA, loadedGroup);

				node.setEdition(PropertyUtil.getStringProperty(loadedGroup,
						STSchemaConstant.ORGANIGRAMME_ENTITE_SCHEMA, "edition"));
				node.setFormule(PropertyUtil.getStringProperty(loadedGroup,
						STSchemaConstant.ORGANIGRAMME_ENTITE_SCHEMA, "formule"));
				node.setOrdre(PropertyUtil.getLongProperty(loadedGroup, STSchemaConstant.ORGANIGRAMME_ENTITE_SCHEMA,
						"ordre"));
				node.setMembreGouvernementCivilite(PropertyUtil.getStringProperty(loadedGroup,
						STSchemaConstant.ORGANIGRAMME_ENTITE_SCHEMA, "membreGouvernementCivilite"));
				node.setMembreGouvernementNom(PropertyUtil.getStringProperty(loadedGroup,
						STSchemaConstant.ORGANIGRAMME_ENTITE_SCHEMA, "membreGouvernementNom"));
				node.setMembreGouvernementPrenom(PropertyUtil.getStringProperty(loadedGroup,
						STSchemaConstant.ORGANIGRAMME_ENTITE_SCHEMA, "membreGouvernementPrenom"));

				List<String> childrenPoste = PropertyUtil.getStringListProperty(loadedGroup,
						STSchemaConstant.ORGANIGRAMME_ENTITE_SCHEMA, "subPostes");
				List<String> childrenUnite = PropertyUtil.getStringListProperty(loadedGroup,
						STSchemaConstant.ORGANIGRAMME_ENTITE_SCHEMA, "subUnitesStructurelles");

				for (String child : childrenUnite) {

					if (!child.equals("cn=emptyRef")) {
						if (!mapEnfantUstParentEnt.containsKey(child)) {
							mapEnfantUstParentEnt.put(child, Lists.newArrayList(node.getId()));
						} else {
							mapEnfantUstParentEnt.get(child).add(node.getId());
						}
					}
				}

				for (String child : childrenPoste) {

					if (!child.equals("cn=emptyRef")) {
						if (!mapEnfantPstParentEnt.containsKey(child)) {
							mapEnfantPstParentEnt.put(child, Lists.newArrayList(node.getId()));
						} else {
							mapEnfantPstParentEnt.get(child).add(node.getId());
						}
					}
				}

				lstEntites.add(node);
			}

		} catch (Exception e) {
			LOG.error("Erreur lors de la récupération des ministères", e);

		} finally {
			try {
				ldapSessionContainer.closeAll();
			} catch (ClientException e) {
				LOG.error("Erreur lors de la fermeture de la connection au LDAP", e);
			}
		}

		return lstEntites;
	}

	/**
	 * Permet de récupérer la liste de l'intégralité des Unités et des directions sans les liens des parents
	 * 
	 * @return
	 */
	protected Map<String, UniteStructurelleNode> getAllUnitesAndDirections() {
		Map<String, UniteStructurelleNode> mapUnites = new TreeMap<String, UniteStructurelleNode>();

		LDAPSessionContainer ldapSessionContainer = new LDAPSessionContainerImpl();
		try {
			LDAPSession session = ldapSessionContainer.getSessionUniteStructurelle();

			Map<String, Serializable> filter = new HashMap<String, Serializable>();
			Map<String, String> orderBy = new HashMap<String, String>();
			orderBy.put("groupName", "asc");
			List<DocumentModel> entries = session.query(filter, null, orderBy);

			for (DocumentModel group : entries) {
				DocumentModel loadedGroup = session.getEntry((String) group.getProperty(session.getDirectory()
						.getSchema(), STConstant.LDAP_GROUP_NAME_PROPERTY));
				UniteStructurelleNode node = new UniteStructurelleNodeImpl();

				mapOrganigrammeData(node, STSchemaConstant.ORGANIGRAMME_UNITE_STRUCTURELLE_SCHEMA, loadedGroup);

				String type = PropertyUtil.getStringProperty(loadedGroup,
						STSchemaConstant.ORGANIGRAMME_UNITE_STRUCTURELLE_SCHEMA, "type");

				node.setTypeValue(type);

				List<String> childrenPoste = PropertyUtil.getStringListProperty(loadedGroup,
						STSchemaConstant.ORGANIGRAMME_UNITE_STRUCTURELLE_SCHEMA, "subPostes");
				List<String> childrenUnite = PropertyUtil.getStringListProperty(loadedGroup,
						STSchemaConstant.ORGANIGRAMME_UNITE_STRUCTURELLE_SCHEMA, "subUnitesStructurelles");

				for (String child : childrenUnite) {
					if (!child.equals("cn=emptyRef")) {
						if (!mapEnfantUstParentUst.containsKey(child)) {
							mapEnfantUstParentUst.put(child, Lists.newArrayList(node.getId()));
						} else {
							mapEnfantUstParentUst.get(child).add(node.getId());
						}
					}
				}

				for (String child : childrenPoste) {
					if (!child.equals("cn=emptyRef")) {
						if (!mapEnfantPstParentUst.containsKey(child)) {
							mapEnfantPstParentUst.put(child, Lists.newArrayList(node.getId()));
						} else {
							mapEnfantPstParentUst.get(child).add(node.getId());
						}
					}
				}

				mapUnites.put(node.getId(), node);
			}

		} catch (Exception e) {
			LOG.error("Erreur lors de la récupération des unités", e);

		} finally {
			try {
				ldapSessionContainer.closeAll();
			} catch (ClientException e) {
				LOG.error("Erreur lors de la fermeture de la connection au LDAP", e);
			}
		}

		return mapUnites;
	}

	/**
	 * Permet de récupérer les postes mais on ne mappe pas encore avec les parents
	 * 
	 * @return
	 */
	protected List<PosteNode> getAllPostes() {
		List<PosteNode> lstPostes = new ArrayList<PosteNode>();
		LDAPSessionContainer ldapSessionContainer = new LDAPSessionContainerImpl();
		try {
			LDAPSession session = ldapSessionContainer.getSessionPoste();

			Map<String, Serializable> filter = new HashMap<String, Serializable>();
			Map<String, String> orderBy = new HashMap<String, String>();
			orderBy.put("groupName", "asc");
			List<DocumentModel> entries = session.query(filter, null, orderBy);

			for (DocumentModel group : entries) {
				DocumentModel loadedGroup = session.getEntry((String) group.getProperty(session.getDirectory()
						.getSchema(), STConstant.LDAP_GROUP_NAME_PROPERTY));
				PosteNode node = new PosteNodeImpl();

				mapOrganigrammeData(node, STSchemaConstant.ORGANIGRAMME_POSTE_SCHEMA, loadedGroup);
				String chargeMissionSGG = PropertyUtil.getStringProperty(loadedGroup,
						STSchemaConstant.ORGANIGRAMME_POSTE_SCHEMA, STOrganigrammeConstant.CHARGE_MISSION_SGG_PROPERTY);

				node.setChargeMissionSGG(Boolean.parseBoolean(chargeMissionSGG));

				String conseillerPM = PropertyUtil.getStringProperty(loadedGroup,
						STSchemaConstant.ORGANIGRAMME_POSTE_SCHEMA, STOrganigrammeConstant.CONSEILLER_PM_PROPERTY);

				node.setConseillerPM(Boolean.parseBoolean(conseillerPM));

				String posteBdc = PropertyUtil.getStringProperty(loadedGroup,
						STSchemaConstant.ORGANIGRAMME_POSTE_SCHEMA, STOrganigrammeConstant.POSTE_BDC_PROPERTY);

				node.setPosteBdc(Boolean.parseBoolean(posteBdc));

				String superviseurSGG = PropertyUtil.getStringProperty(loadedGroup,
						STSchemaConstant.ORGANIGRAMME_POSTE_SCHEMA,
						STOrganigrammeConstant.POSTE_SUPERVISEUR_SGG_PROPERTY);

				node.setSuperviseurSGG(Boolean.parseBoolean(superviseurSGG));

				String posteWs = PropertyUtil.getStringProperty(loadedGroup,
						STSchemaConstant.ORGANIGRAMME_POSTE_SCHEMA, STOrganigrammeConstant.POSTE_WS_PROPERTY);

				node.setPosteWs(Boolean.parseBoolean(posteWs));

				node.setWsKeyAlias(PropertyUtil.getStringProperty(loadedGroup,
						STSchemaConstant.ORGANIGRAMME_POSTE_SCHEMA, STOrganigrammeConstant.WS_KEY_ALIAS_PROPERTY));

				node.setWsPassword(PropertyUtil.getStringProperty(loadedGroup,
						STSchemaConstant.ORGANIGRAMME_POSTE_SCHEMA, STOrganigrammeConstant.WS_PWD_PROPERTY));

				node.setWsUrl(PropertyUtil.getStringProperty(loadedGroup, STSchemaConstant.ORGANIGRAMME_POSTE_SCHEMA,
						STOrganigrammeConstant.WS_URL_PROPERTY));

				node.setWsUser(PropertyUtil.getStringProperty(loadedGroup, STSchemaConstant.ORGANIGRAMME_POSTE_SCHEMA,
						STOrganigrammeConstant.WS_USR_PROPERTY));

				List<String> children = PropertyUtil.getStringListProperty(loadedGroup,
						STSchemaConstant.ORGANIGRAMME_POSTE_SCHEMA, STOrganigrammeConstant.MEMBERS_PROPERTY);

				if (children != null && !children.isEmpty()) {
					node.setMembers(children);
				}

				lstPostes.add(node);
			}

		} catch (Exception e) {
			LOG.error("Erreur lors de la récupération des postes", e);

		} finally {
			try {
				ldapSessionContainer.closeAll();
			} catch (ClientException e) {
				LOG.error("Erreur lors de la fermeture de la connection au LDAP", e);
			}
		}

		return lstPostes;
	}

}
