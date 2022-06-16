package fr.dila.reponses.ui.services.actions.nxql;

import fr.dila.st.ui.services.actions.STSmartNXQLQueryActionService;
import org.nuxeo.ecm.core.api.CoreSession;

public interface ReponsesSmartNXQLQueryActionService extends STSmartNXQLQueryActionService {
    /**
     * Retourne la requête complète du requêteur.
     *
     * @return la requête complête.
     */
    String getFullQuery(CoreSession session, String queryPart);
}
