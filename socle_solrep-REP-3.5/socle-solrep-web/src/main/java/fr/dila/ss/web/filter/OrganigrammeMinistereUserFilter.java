package fr.dila.ss.web.filter;

import java.io.Serializable;
import java.util.Set;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.Filter;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.GouvernementNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;

/**
 * WebBean qui conserve uniquement les noeuds ministères auquel l'utilisateur appartient.
 *
 * @author jtremeaux
 */
@Name("organigrammeMinistereUserFilter")
@Scope(ScopeType.EVENT)
public class OrganigrammeMinistereUserFilter implements Filter, Serializable {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    @In(required = true, create = true)
    protected NuxeoPrincipal currentUser;

    /**
     * Default constructor
     */
    public OrganigrammeMinistereUserFilter(){
    	// do nothing
    }
    
    @Override
    public boolean accept(DocumentModel doc) {
        SSPrincipal principal = (SSPrincipal) currentUser;
        final Set<String> ministereIdSet = principal.getMinistereIdSet();
        
        OrganigrammeNode node = doc.getAdapter(OrganigrammeNode.class);
        if (node instanceof GouvernementNode) {
            return true;
        } else if (node instanceof EntiteNode) {
            if (principal.isMemberOf(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR)) {
                return true;
            } else {
                return ministereIdSet.contains(node.getId());
            }
        } else {
            return false;
        }
    }
}
