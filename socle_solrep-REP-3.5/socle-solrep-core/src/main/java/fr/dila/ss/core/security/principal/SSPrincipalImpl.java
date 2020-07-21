package fr.dila.ss.core.security.principal;

import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.core.user.STPrincipalImpl;

/**
 * Implémentation du principal du socle Solrep.
 * 
 * @author jtremeaux
 */
public class SSPrincipalImpl extends STPrincipalImpl implements SSPrincipal {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Ensemble des identifiants techniques des ministères de l'utilisateur.
     */
    private Set<String> ministereIdSet;

    /**
     * Ensemble des identifiants techniques des directions de l'utilisateur.
     */
    private Set<String> directionIdSet;
    
    /**
     * Constructeur de SSPrincipalImpl.
     * 
     * @param name name
     * @param isAnonymous isAnonymous
     * @param isAdministrator isAdministrator
     * @param updateAllGroups updateAllGroups
     * @throws ClientException
     */
    public SSPrincipalImpl(String name, boolean isAnonymous, boolean isAdministrator, boolean updateAllGroups) throws ClientException {
        super(name, isAnonymous, isAdministrator, updateAllGroups);
    }

    @Override
    public Set<String> getMinistereIdSet() {
        return ministereIdSet;
    }

    @Override
    public void setMinistereIdSet(Set<String> ministereIdSet) {
        this.ministereIdSet = ministereIdSet;
    }

    @Override
    public Set<String> getDirectionIdSet() {
        return directionIdSet;
    }

    @Override
    public void setDirectionIdSet(Set<String> directionIdSet) {
        this.directionIdSet = directionIdSet;
    }
}
