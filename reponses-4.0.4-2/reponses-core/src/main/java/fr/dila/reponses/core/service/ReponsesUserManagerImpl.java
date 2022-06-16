package fr.dila.reponses.core.service;

import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.domain.user.Delegation;
import fr.dila.reponses.api.exception.ReponsesException;
import fr.dila.reponses.api.service.ProfilUtilisateurService;
import fr.dila.reponses.api.service.ReponsesUserManager;
import fr.dila.ss.core.security.principal.SSPrincipalImpl;
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
import fr.dila.st.core.util.ResourceHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.ecm.platform.usermanager.NuxeoPrincipalImpl;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.transaction.TransactionHelper;

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
    private static final long serialVersionUID = -420330163185351332L;
    /**
     * Logger.
     */
    private static final STLogger LOGGER = STLogFactory.getLog(ReponsesUserManagerImpl.class);

    /**
     * Default constructor
     */
    public ReponsesUserManagerImpl() {
        super();
    }

    protected NuxeoPrincipal makeSSPrincipal(DocumentModel userEntry, boolean anonymous, List<String> groups) {
        boolean admin = false;
        String username = userEntry.getId();

        List<String> virtualGroups = new LinkedList<>();
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
        Set<String> baseFunctionSet = STServiceLocator
            .getProfileService()
            .getBaseFunctionFromProfil(principal.getAllGroups());
        principal.setBaseFunctionSet(baseFunctionSet);

        // Renseigne les postes de l'utilisateur
        List<String> posteListe = stUser.getPostes();
        Set<String> posteIdSet = new HashSet<>(posteListe);
        principal.setPosteIdSet(posteIdSet);

        // Renseigne les ministères de l'utilisateur
        final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
        Set<String> ministereIdSet = new HashSet<>();
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
    protected NuxeoPrincipal makePrincipal(DocumentModel userEntry, boolean anonymous, List<String> groups) {
        // Ajoute les délégations à l'utilisateur
        addDelegationToUser(userEntry);

        // Crée le principal
        NuxeoPrincipal principal = makeSSPrincipal(userEntry, anonymous, groups);

        if (activateComputedGroup() && principal instanceof NuxeoPrincipalImpl) {
            NuxeoPrincipalImpl nuxPrincipal = (NuxeoPrincipalImpl) principal;

            List<String> vGroups = getService().computeGroupsForUser(nuxPrincipal);

            if (vGroups == null) {
                vGroups = new ArrayList<>();
            }

            List<String> origVGroups = nuxPrincipal.getVirtualGroups();
            if (origVGroups == null) {
                origVGroups = new ArrayList<>();
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
    public boolean checkUsernamePassword(String username, String password) {
        if (super.checkUsernamePassword(username, password)) {
            Framework.doPrivileged(
                () -> {
                    TransactionHelper.runInNewTransaction(
                        () -> {
                            RepositoryManager repoManager = STServiceLocator.getRepositoryManager();
                            CoreInstance.doPrivileged(
                                repoManager.getDefaultRepositoryName(),
                                session -> {
                                    // Test de la validité du mot de passe
                                    try {
                                        NuxeoPrincipal principal = STServiceLocator
                                            .getUserManager()
                                            .getPrincipal(username);
                                        if (principal != null) {
                                            final ProfilUtilisateurService profilService = ReponsesServiceLocator.getProfilUtilisateurService();
                                            final String userId = principal.getName();
                                            profilService.getOrCreateUserProfilFromId(session, userId);

                                            if (profilService.isUserPasswordOutdated(session, userId)) {
                                                LOGGER.info(
                                                    session,
                                                    STLogEnumImpl.NOTIFICATION_PASSWORD_FONC,
                                                    ResourceHelper.getString("login.journal.password.expire", username)
                                                );
                                            }
                                        }
                                    } catch (WorkspaceNotFoundException we) {
                                        LOGGER.debug(session, STLogEnumImpl.FAIL_GET_WORKSPACE_FONC, we.getMessage());
                                        LOGGER.debug(session, STLogEnumImpl.FAIL_GET_WORKSPACE_FONC, we);
                                    }
                                    return true;
                                }
                            );
                        }
                    );
                }
            );

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
     *
     */
    protected void addDelegationToUser(final DocumentModel userEntry) {
        // Les utilisateurs techniques ne peuvent pas recevoir de délégation
        if (isTechnicalUser(userEntry.getId())) {
            return;
        }

        // Ouvre une session unrestricted (la session n'existe pas encore car l'utilisateur n'est pas loggé)
        TransactionHelper.runInNewTransaction(
            () -> {
                RepositoryManager repositoryManager = STServiceLocator.getRepositoryManager();
                CoreInstance.doPrivileged(
                    repositoryManager.getDefaultRepositoryName(),
                    session -> {
                        // Injecte les délégations dans le principal
                        STUser destUser = userEntry.getAdapter(STUser.class);

                        List<DocumentModel> delegationList = ReponsesServiceLocator
                            .getDelegationService()
                            .findActiveDelegationForUser(session, destUser.getUsername());
                        final Set<String> posteSet = new HashSet<>();
                        List<String> destPosteList = destUser.getPostes();
                        if (destPosteList != null) {
                            posteSet.addAll(destPosteList);
                        }
                        final Set<String> profilSet = new HashSet<>();
                        List<String> destProfilList = destUser.getGroups();
                        if (destProfilList != null) {
                            profilSet.addAll(destProfilList);
                        }
                        for (DocumentModel delegationDoc : delegationList) {
                            LOGGER.debug(
                                session,
                                STLogEnumImpl.UPDATE_USER_TEC,
                                "Ajout de la délégation " + delegationDoc.getId()
                            );
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
                        destUser.setPostes(new ArrayList<>(posteSet));
                        destUser.setGroups(new ArrayList<>(profilSet));
                    }
                );
            }
        );
    }

    /**
     * Création d'un utilisateur temporaire
     *
     * @param session
     * @param newUser
     * @return
     *
     */
    @Override
    public DocumentModel createUserOccasional(CoreSession session, DocumentModel newUser) {
        String userCreatorId = session.getPrincipal().getName();
        DocumentModel userCreatorModel = getUserModel(userCreatorId);
        STUser userCreator = userCreatorModel.getAdapter(STUser.class);

        createUser(newUser);
        // Récupération du user nouvellement créé - le documentModel retourné ne correspond pas, cela provoquait
        // impossibilité de la premiere connexion
        DocumentModel userModel = getUserModel(getUserId(newUser));
        STUser user = userModel.getAdapter(STUser.class);

        LOGGER.debug(
            session,
            STLogEnumImpl.CREATE_USER_TEC,
            "Creation d'un utilisateur temporaire - identifiant : " + user.getUsername()
        );

        // Ajout du profil par défaut
        Set<String> defaultProfiles = STServiceLocator
            .getProfileService()
            .getProfilFromBaseFunction(ReponsesBaseFunctionConstant.DEFAULT_PROFILE_TEMP_USER);
        user.setGroups(new ArrayList<>(defaultProfiles));

        LOGGER.debug(
            session,
            STLogEnumImpl.UPDATE_USER_TEC,
            "Ajout du profil par défaut - identifiant : " + user.getUsername()
        );

        ArrayList<String> postes = new ArrayList<>();
        // Récuperation des parents des postes du créateur
        final STPostesService posteService = STServiceLocator.getSTPostesService();
        List<OrganigrammeNode> parentList = new ArrayList<>();
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
            "Ajout du poste par défaut - identifiant poste : " +
            poste.getId() +
            " - identifiant : " +
            user.getUsername()
        );

        return userModel;
    }

    @Override
    protected NuxeoPrincipal getPrincipalUsingCache(String username) {
        NuxeoPrincipal ret = (NuxeoPrincipal) principalCache.get(username);
        if (ret == null) {
            ret = getPrincipal(username, null);
            if (ret == null) {
                return ret;
            }
            principalCache.put(username, ret);
        }
        return ((SSPrincipalImpl) ret).cloneTransferable(); // should not return cached principal
    }
}
