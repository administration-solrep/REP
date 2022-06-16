package fr.dila.reponses.core.event;

import fr.dila.reponses.api.service.DelegationService;
import fr.dila.reponses.core.constant.DelegationConstant;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.PostCommitEventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

/**
 * Listener qui permet d'envoyer un mail après la création / modification d'une délégation.
 *
 * @author jtremeaux
 */
public class DelegationCreatedListener implements PostCommitEventListener {

    private void handleEvent(Event event) {
        // Traite uniquement les évènements de création / modification de documents
        EventContext ctx = event.getContext();
        if (!(ctx instanceof DocumentEventContext)) {
            return;
        }

        if (
            !(
                event.getName().equals(DocumentEventTypes.DOCUMENT_UPDATED) ||
                event.getName().equals(DocumentEventTypes.DOCUMENT_CREATED)
            )
        ) {
            return;
        }

        // Traite uniquement les documents de type délégation
        DocumentEventContext context = (DocumentEventContext) ctx;
        DocumentModel delegationDoc = context.getSourceDocument();
        if (!DelegationConstant.DELEGATION_DOCUMENT_TYPE.equals(delegationDoc.getType())) {
            return;
        }
        CoreSession session = context.getCoreSession();

        // Envoie un email de notification
        final DelegationService delegationService = ReponsesServiceLocator.getDelegationService();
        delegationService.sendDelegationEmail(session, delegationDoc);
    }

    @Override
    public void handleEvent(EventBundle events) {
        for (Event event : events) {
            handleEvent(event);
        }
    }
}
