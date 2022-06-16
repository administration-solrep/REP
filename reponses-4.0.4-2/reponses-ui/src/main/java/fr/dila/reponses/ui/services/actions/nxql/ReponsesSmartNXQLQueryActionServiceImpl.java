package fr.dila.reponses.ui.services.actions.nxql;

import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.services.actions.impl.STSmartNXQLQueryActionServiceImpl;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Surcharge de l'action servuce SmartNXQLQueryActionService, initialement pour
 * écraser la méthode initCurrentSmartQuery(String existingQueryPart) et mettre
 * une nouvelle implémentation de IncrementalSmartNXQLQuery.
 *
 * @since 5.4
 * @author jgomez
 */
public class ReponsesSmartNXQLQueryActionServiceImpl
    extends STSmartNXQLQueryActionServiceImpl
    implements ReponsesSmartNXQLQueryActionService {

    @Override
    public String getFullQuery(CoreSession session, String queryPart) {
        return STServiceLocator.getRequeteurService().getPattern(session, queryPart);
    }
}
