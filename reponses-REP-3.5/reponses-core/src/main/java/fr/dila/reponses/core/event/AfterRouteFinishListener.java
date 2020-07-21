package fr.dila.reponses.core.event;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

import fr.dila.ecm.platform.routing.api.DocumentRoutingConstants;
import fr.dila.st.api.dossier.STDossier.DossierTransition;
import fr.dila.st.api.feuilleroute.STFeuilleRoute;
import fr.dila.st.core.event.RollbackEventListener;

/**
 * Listener exécuté après la terminaison des feuilles de routes.
 * 
 * @author jtremeaux
 */
public class AfterRouteFinishListener extends RollbackEventListener {
    @Override
    public void handleDocumentEvent(Event event, DocumentEventContext ctx) throws ClientException {
        // Traite uniquement les évènements de terminaison des feuilles de routes
        if (!(event.getName().equals(DocumentRoutingConstants.Events.afterRouteFinish.name()))) {
            return;
        }
        final CoreSession session = ctx.getCoreSession();
        
        // Passe les dossiers attachés à l'état done
        final DocumentModel routeInstanceDoc = ctx.getSourceDocument();
        STFeuilleRoute feuilleroute = routeInstanceDoc.getAdapter(STFeuilleRoute.class);
        List<String> caseDocIdList = feuilleroute.getAttachedDocuments();
        for (String caseDocId : caseDocIdList) {
            DocumentModel caseDoc = session.getDocument(new IdRef(caseDocId));
            caseDoc.followTransition(DossierTransition.toDone.name());
        }
     }
}
