package fr.dila.reponses.web.requeteur;

import java.io.Serializable;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.reponses.core.requeteur.ReponsesIncrementalSmartNXQLQuery;
import fr.dila.st.api.service.RequeteurService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.web.context.NavigationContextBean;
import fr.dila.st.web.requeteur.STSmartNXQLQueryActions;

/**
 * Surcharge du bean seam SmartNXQLQueryActions, initialement pour écraser la méthode initCurrentSmartQuery(String existingQueryPart) et mettre une nouvelle implémentation de IncrementalSmartNXQLQuery.
 * 
 * @since 5.4
 * @author jgomez
 **/

@Name("smartNXQLQueryActions")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.APPLICATION + 2)
public class ReponsesSmartNXQLQueryActions extends STSmartNXQLQueryActions implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Map<String, String> userInfo;

    @In(create = true)
    protected transient ResourcesAccessor resourcesAccessor;

    @In(create = true, required = false)
    protected transient NavigationContextBean navigationContext;

    @In(create = true, required = true)
    protected transient CoreSession documentManager;

    @Override
    public void initCurrentSmartQuery(String existingQueryPart, boolean resetHistory) {
        super.initCurrentSmartQuery(existingQueryPart, resetHistory);
        currentSmartQuery = new ReponsesIncrementalSmartNXQLQuery(existingQueryPart);
    }

    /**
     * Retourne la requête complête du requêteur.
     * 
     * @return la requête complête.
     */
    public String getFullQuery() {
        RequeteurService requeteurService = STServiceLocator.getRequeteurService();
        return requeteurService.getPattern(documentManager, getQueryPart());
    }

}
