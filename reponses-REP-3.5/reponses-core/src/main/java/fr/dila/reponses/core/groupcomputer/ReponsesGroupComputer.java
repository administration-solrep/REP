package fr.dila.reponses.core.groupcomputer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.platform.computedgroups.AbstractGroupComputer;
import org.nuxeo.ecm.platform.usermanager.NuxeoPrincipalImpl;

import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.ss.core.security.principal.SSPrincipalImpl;
import fr.dila.st.api.constant.STAclConstant;

/**
 * Ce calculateur de groupe injecte dans le principal les groupes calculés pour Réponses.
 * 
 * @author jtremeaux
 */
public class ReponsesGroupComputer extends AbstractGroupComputer {

    private static final Log log = LogFactory.getLog(ReponsesGroupComputer.class);

    @Override
    public List<String> getGroupsForUser(NuxeoPrincipalImpl nuxeoPrincipal) throws Exception {
        if (nuxeoPrincipal == null) {
            return Collections.emptyList();
        }

        // Récupère les groupes actuels du principal
        final List<String> groupList = nuxeoPrincipal.getGroups();
        try {
            List<String> newGroupList = new ArrayList<String>(groupList);
            if (!(nuxeoPrincipal instanceof SSPrincipalImpl)) {
                throw new Exception("Le principal doit être du type SSPrincipalImpl");
            }
            SSPrincipalImpl ssPrincipal = (SSPrincipalImpl) nuxeoPrincipal;

            // Injecte les groupes donnant accès aux dossiers
            newGroupList.addAll(getDossierDistributionMinistereUpdaterGroupSet(ssPrincipal));

            return newGroupList;
        } catch (Exception e) {
            log.warn("Impossible d'associer les groupes de Réponses à l'utilisateur connecté.", e);
            return groupList;
        }
    }

    /**
     * Ajout les groupes donnant accès aux DossierLink via la distribution dans les ministères.
     * 
     * @param principal Principal
     * @return Groupes correspondant à cette fonction
     */
    private Set<String> getDossierDistributionMinistereUpdaterGroupSet(SSPrincipalImpl ssPrincipal) {
        // Ajoute les groupes uniquement si l'utilisateur possède la fonction unitaire
        final Set<String> baseFunctionSet = ssPrincipal.getBaseFunctionSet();
        final Set<String> newGroupSet = new HashSet<String>();
        if (!baseFunctionSet.contains(ReponsesBaseFunctionConstant.DOSSIER_RECHERCHE_MIN_UPDATER)) {
            return newGroupSet;
        }

        // Injecte un groupe pour chacun des ministères
        final Set<String> ministereIdSet = ssPrincipal.getMinistereIdSet();
        for (String ministereId : ministereIdSet) {
            String group = STAclConstant.DOSSIER_LINK_UPDATER_MIN_ACE_PREFIX + ministereId;
            newGroupSet.add(group);
        }

        return newGroupSet;
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
