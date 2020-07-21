package fr.dila.reponses.core.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.platform.usermanager.NuxeoPrincipalImpl;
import org.nuxeo.ecm.platform.usermanager.exceptions.UserAlreadyExistsException;
import org.nuxeo.runtime.api.Framework;

import fr.dila.reponses.api.Exception.ReponsesException;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.service.ProfilUtilisateurService;
import fr.dila.reponses.api.service.ReponsesUserManager;
import fr.dila.ss.core.security.principal.SSPrincipalImpl;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.domain.user.Delegation;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.exception.WorkspaceNotFoundException;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.service.STUserManagerImpl;
import fr.dila.st.core.util.SessionUtil;

/**
 * Gestionnaires d'utilisateur de l'application Réponses. Ce gestionnaire d'utilisateurs permet de gérer les
 * délégations.
 * 
 * @author jtremeaux
 */
public class ReponsesUserManagerImpl extends STUserManagerImpl implements ReponsesUserManager {

	/**
	 * Serial version UID
	 */
	private static final long		serialVersionUID	= -420330163185351332L;
	/**
	 * Logger.
	 */
	private static final STLogger	LOGGER				= STLogFactory.getLog(ReponsesUserManagerImpl.class);

	/**
	 * Default constructor
	 */
	public ReponsesUserManagerImpl() {
		super();
	}

	protected NuxeoPrincipal makeSSPrincipal(DocumentModel userEntry, boolean anonymous, List<String> groups)
			throws ClientException {
		boolean admin = false;
		String username = userEntry.getId();

		List<String> virtualGroups = new LinkedList<String>();
		if (!anonymous) {
			// Add preconfigured groups: useful for LDAP
			if (defaultGroup != null) {
				virtualGroups.add(defaultGroup);
			}
			// Add additional groups: useful for virtual users
			if (groups != null) {
				virtualGroups.addAll(groups);
			}
			// Create a default admin if needed
			if (administratorIds != null && administratorIds.contains(username)) {
				admin = true;
				if (administratorGroups != null) {
					virtualGroups.addAll(administratorGroups);
				}
			}
		}

		SSPrincipalImpl principal = new SSPrincipalImpl(username, anonymous, admin, false);
		principal.setConfig(userConfig);

		principal.setModel(userEntry, false);
		principal.setVirtualGroups(virtualGroups, true);

		List<String> roles = Arrays.asList("regular");
		principal.setRoles(roles);

		STUser stUser = userEntry.getAdapter(STUser.class);
		if (stUser == null) {
			throw new ReponsesException("Cannot cast user to STUser");
		}

		// Renseigne les fonctions unitaires de l'utilisateur
		Set<String> baseFunctionSet = STServiceLocator.getProfileService().getBaseFunctionFromProfil(
				principal.getAllGroups());
		principal.setBaseFunctionSet(baseFunctionSet);

		// Renseigne les postes de l'utilisateur
		List<String> posteListe = stUser.getPostes();
		Set<String> posteIdSet = new HashSet<String>(posteListe);
		principal.setPosteIdSet(posteIdSet);

		// Renseigne les ministères de l'utilisateur
		final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
		Set<String> ministereIdSet = new HashSet<String>();
		for (String posteId : posteIdSet) {
			List<EntiteNode> ministereNodeList = ministeresService.getMinistereParentFromPoste(posteId);
			for (OrganigrammeNode ministereNode : ministereNodeList) {
				final String ministereId = ministereNode.getId().toString();
				ministereIdSet.add(ministereId);
			}
		}
		principal.setMinistereIdSet(ministereIdSet);

		return principal;
	}

	@Override
	protected NuxeoPrincipal makePrincipal(DocumentModel userEntry, boolean anonymous, List<String> groups)
			throws ClientException {
		// Ajoute les délégations à l'utilisateur
		addDelegationToUser(userEntry);

		// Crée le principal
		NuxeoPrincipal principal = makeSSPrincipal(userEntry, anonymous, groups);

		if (activateComputedGroup() && principal instanceof NuxeoPrincipalImpl) {
			NuxeoPrincipalImpl nuxPrincipal = (NuxeoPrincipalImpl) principal;

			List<String> vGroups = getService().computeGroupsForUser(nuxPrincipal);

			if (vGroups == null) {
				vGroups = new ArrayList<String>();
			}

			List<String> origVGroups = nuxPrincipal.getVirtualGroups();
			if (origVGroups == null) {
				origVGroups = new ArrayList<String>();
			}

			// MERGE!
			origVGroups.addAll(vGroups);

			nuxPrincipal.setVirtualGroups(origVGroups);

			// This a hack to work around the problem of running tests
			if (!Framework.isTestModeSet()) {
				nuxPrincipal.updateAllGroups();
			} else {
				List<String> allGroups = nuxPrincipal.getGroups();
				for (String vGroup : vGroups) {
					if (!allGroups.contains(vGroup)) {
						allGroups.add(vGroup);
					}
				}
				nuxPrincipal.setGroups(allGroups);
			}
		}
		return principal;
	}

	@Override
	public boolean checkUsernamePassword(String username, String password) throws ClientException {
		if (super.checkUsernamePassword(username, password)) {
			// Test de la validité du mot de passe
			CoreSession session = null;
			LoginContext loginContext = null;
			try {
				String userId = username;
				loginContext = Framework.login();
				session = SessionUtil.getCoreSession();
				NuxeoPrincipal principal = STServiceLocator.getUserManager().getPrincipal(username);
				if (principal != null) {
					final ProfilUtilisateurService profilService = ReponsesServiceLocator.getProfilUtilisateurService();
					userId = principal.getName();
					profilService.getOrCreateUserProfilFromId(session, userId);

					// Vérifie que l'utilisateur n'a pas été supprimé logiquement
					if (profilService.isUserDeleted(userId)) {
						return false;
					}

					if (profilService.isUserPasswordOutdated(session, userId)) {
						LOGGER.info(session, STLogEnumImpl.NOTIFICATION_PASSWORD_FONC,
								"Mot de passe expiré pour l'utilisateur " + username);
					}
				}
			} catch (WorkspaceNotFoundException we) {
				LOGGER.debug(session, STLogEnumImpl.FAIL_GET_WORKSPACE_FONC, we.getMessage());
				LOGGER.debug(session, STLogEnumImpl.FAIL_GET_WORKSPACE_FONC, we);
			} catch (LoginException le) {
				LOGGER.warn(session, STLogEnumImpl.FAIL_GET_SESSION_TEC, le.getMessage());
				LOGGER.debug(session, STLogEnumImpl.FAIL_GET_SESSION_TEC, le);
			} finally {
				SessionUtil.close(session);
				if (loginContext != null) {
					try {
						loginContext.logout();
					} catch (LoginException exc) {
						LOGGER.warn(null, STLogEnumImpl.FAIL_GET_SESSION_TEC, "Echec du logout : " + exc.getMessage());
						LOGGER.debug(null, STLogEnumImpl.FAIL_GET_SESSION_TEC, exc);
					}
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Recherche les délégations de l'utilisateur et ajoute les profils et les postes correspondants au UserModel par
	 * effet de bord.
	 * 
	 * @param userEntry
	 *            Utilisateur cible
	 * @throws ClientException
	 */
	protected void addDelegationToUser(final DocumentModel userEntry) throws ClientException {
		// Les utilisateurs techniques ne peuvent pas recevoir de délégation
		if (isTechnicalUser(userEntry.getId())) {
			return;
		}

		// Ouvre une session unrestricted (la session n'existe pas encore car l'utilisateur n'est pas loggé)
		new UnrestrictedSessionRunner("default") {
			@Override
			public void run() throws ClientException {
				// Injecte les délégations dans le principal
				STUser destUser = userEntry.getAdapter(STUser.class);

				List<DocumentModel> delegationList = STServiceLocator.getDelegationService()
						.findActiveDelegationForUser(session, destUser.getUsername());
				final Set<String> posteSet = new HashSet<String>();
				List<String> destPosteList = destUser.getPostes();
				if (destPosteList != null) {
					posteSet.addAll(destPosteList);
				}
				final Set<String> profilSet = new HashSet<String>();
				List<String> destProfilList = destUser.getGroups();
				if (destProfilList != null) {
					profilSet.addAll(destProfilList);
				}
				for (DocumentModel delegationDoc : delegationList) {
					LOGGER.debug(session, STLogEnumImpl.UPDATE_USER_TEC,
							"Ajout de la délégation " + delegationDoc.getId());
					Delegation delegation = delegationDoc.getAdapter(Delegation.class);

					// Injecte tous les postes postes de l'utilisateur source
					DocumentModel sourceUserDoc = getUserModel(delegation.getSourceId());
					STUser sourceUser = sourceUserDoc.getAdapter(STUser.class);

					List<String> sourcePosteList = sourceUser.getPostes();
					if (sourcePosteList != null) {
						posteSet.addAll(sourcePosteList);
					}
					// Injecte les profils spécifiés dans la délégation
					List<String> delegationProfilList = delegation.getProfilListId();
					if (delegationProfilList != null) {
						profilSet.addAll(delegationProfilList);
					}
				}
				destUser.setPostes(new ArrayList<String>(posteSet));
				destUser.setGroups(new ArrayList<String>(profilSet));
			}
		}.runUnrestricted();
	}

	/**
	 * Création d'un utilisateur temporaire
	 * 
	 * @param session
	 * @param newUser
	 * @return
	 * @throws ClientException
	 */
	@Override
	public DocumentModel createUserOccasional(CoreSession session, DocumentModel newUser) throws ClientException {

		String userCreatorId = session.getPrincipal().getName();
		DocumentModel userCreatorModel = getUserModel(userCreatorId);
		STUser userCreator = userCreatorModel.getAdapter(STUser.class);

		createUser(newUser);
		// Récupération du user nouvellement créé - le documentModel retourné ne correspond pas, cela provoquait
		// impossibilité de la premiere connexion
		DocumentModel userModel = getUserModel(getUserId(newUser));
		STUser user = userModel.getAdapter(STUser.class);

		LOGGER.debug(session, STLogEnumImpl.CREATE_USER_TEC, "Creation d'un utilisateur temporaire - identifiant : "
				+ user.getUsername());

		// Ajout du profil par défaut
		Set<String> defaultProfiles = STServiceLocator.getProfileService().getProfilFromBaseFunction(
				ReponsesBaseFunctionConstant.DEFAULT_PROFILE_TEMP_USER);
		user.setGroups(new ArrayList<String>(defaultProfiles));

		LOGGER.debug(session, STLogEnumImpl.UPDATE_USER_TEC,
				"Ajout du profil par défaut - identifiant : " + user.getUsername());

		ArrayList<String> postes = new ArrayList<String>();
		// Récuperation des parents des postes du créateur
		final STPostesService posteService = STServiceLocator.getSTPostesService();
		List<OrganigrammeNode> parentList = new ArrayList<OrganigrammeNode>();
		List<String> creatorPosteList = userCreator.getPostes();

		List<PosteNode> listNode = posteService.getPostesNodes(creatorPosteList);
		final OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
		for (OrganigrammeNode posteNode : listNode) {
			for (OrganigrammeNode parentPoste : organigrammeService.getParentList(posteNode)) {
				if (!parentList.contains(parentPoste)) {
					parentList.add(parentPoste);
				}
			}
		}

		// Creation du poste temporaire
		PosteNode poste = posteService.getBarePosteModel();
		poste.setLabel("TMP-" + user.getFirstName() + user.getLastName());
		poste.setParentList(parentList);

		// Enregistre le poste
		posteService.createPoste(session, poste);

		// ajoute le poste à la liste des postes de l'utilisateur
		postes.add(poste.getId().toString());
		user.setPostes(postes);

		// enregistre les postes
		updateUser(user.getDocument());
		LOGGER.debug(
				session,
				STLogEnumImpl.UPDATE_USER_TEC,
				"Ajout du poste par défaut - identifiant poste : " + poste.getId() + " - identifiant : "
						+ user.getUsername());

		return userModel;
	}

	@Override
	public DocumentModel createUser(DocumentModel userModel) throws ClientException {
		Session userDir = null;
		CoreSession session = null;
		try {
			userDir = dirService.open(userDirectoryName);
			String userId = getUserId(userModel);

			// check the user does not exist
			if (userDir.hasEntry(userId)) {
				throw new UserAlreadyExistsException();
			}

			String schema = dirService.getDirectorySchema(userDirectoryName);
			String clearUsername = (String) userModel.getProperty(schema, userDir.getIdField());
			String clearPassword = (String) userModel.getProperty(schema, userDir.getPasswordField());

			userModel = userDir.createEntry(userModel);
			userDir.commit();

			syncDigestAuthPassword(clearUsername, clearPassword);

			notifyUserChanged(userId);
			notify(userId, USERCREATED_EVENT_ID);
			session = SessionUtil.getCoreSession();
			try {
				String password = STServiceLocator.getSTUserService().generateAndSaveNewUserPassword(userId);
				ReponsesServiceLocator.getReponsesMailService().sendMailUserPasswordCreation(session, userId, password);
			} catch (Exception exc) {
				LOGGER.error(
						session,
						STLogEnumImpl.FAIL_SEND_MAIL_TEC,
						"Error lors de l'envoi du mail de mot de passe pour l'utilisateur : " + userId + " - "
								+ exc.getMessage());
				LOGGER.debug(session, STLogEnumImpl.FAIL_SEND_MAIL_TEC,
						"Error lors de l'envoi du mail de mot de passe pour l'utilisateur : " + userId, exc);
				userModel.putContextData(STConstant.MAIL_SEND_ERROR, true);
			}

			// Event pour le journal
			String comment = "Création d'un utilisateur [" + userId + "]";
			STServiceLocator.getJournalService()
					.journaliserActionAdministration(session, USERCREATED_EVENT_ID, comment);

			return userModel;

		} finally {
			if (userDir != null) {
				userDir.close();
			}
			if (session != null) {
				SessionUtil.close(session);
			}
		}
	}
}
