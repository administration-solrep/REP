package fr.dila.reponses.ui.services.actions;

import fr.dila.ss.ui.services.actions.ModeleFeuilleRouteActionService;
import fr.dila.ss.ui.th.bean.ModeleFdrForm;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface ReponsesModeleFeuilleRouteActionService extends ModeleFeuilleRouteActionService {
    @Override
    DocumentModel initFeuilleRoute(CoreSession session, ModeleFdrForm form, String creatorName);
}
