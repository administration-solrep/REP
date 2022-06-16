package fr.dila.reponses.core.event;

import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.st.api.dossier.STDossier.DossierTransition;
import fr.dila.st.core.event.RollbackEventListener;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteEvent;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

/**
 * Listener exécuté après la terminaison des feuilles de routes.
 *
 * @author jtremeaux
 */
public class AfterRouteFinishListener extends RollbackEventListener {

    @Override
    public void handleDocumentEvent(Event event, DocumentEventContext ctx) {
        // Traite uniquement les évènements de terminaison des feuilles de routes
        if (!(event.getName().equals(FeuilleRouteEvent.afterRouteFinish.name()))) {
            return;
        }
        final CoreSession session = ctx.getCoreSession();

        // Passe les dossiers attachés à l'état done
        final DocumentModel routeInstanceDoc = ctx.getSourceDocument();
        SSFeuilleRoute feuilleroute = routeInstanceDoc.getAdapter(SSFeuilleRoute.class);
        List<String> caseDocIdList = feuilleroute.getAttachedDocuments();
        for (String caseDocId : caseDocIdList) {
            DocumentModel caseDoc = session.getDocument(new IdRef(caseDocId));
            caseDoc.followTransition(DossierTransition.toDone.name());
        }
    }
}
