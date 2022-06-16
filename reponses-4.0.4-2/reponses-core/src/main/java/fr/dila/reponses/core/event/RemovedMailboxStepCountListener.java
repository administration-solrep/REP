package fr.dila.reponses.core.event;

import static fr.dila.cm.event.CaseManagementEventConstants.EVENT_CONTEXT_CASE_LINK;
import static fr.dila.reponses.api.constant.ReponsesEventConstant.COMPUTE_MAILBOX_PRECOMPTAGE_EVENT;
import static fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil.getRequiredService;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.sword.naiad.nuxeo.commons.core.listener.AbstractDocumentEventListener;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.core.event.impl.EventContextImpl;

/**
 * Listener synchrone qui lève un événement post commit pour mettre à jour le
 * précomtage du mailbox d'un dossier link qui sera supprimé
 *
 */
public class RemovedMailboxStepCountListener extends AbstractDocumentEventListener {
    public static final String MAILBOX_REF_PROP = "mailboxRef";
    public static final String MINISTERE_ID_PROP = "ministereId";
    public static final String ROUTING_TASK_TYPE_PROP = "routingTaskType";

    @Override
    public void handleDocumentEvent(Event event, DocumentEventContext context) {
        final DocumentModel linkDoc = (DocumentModel) context.getProperty(EVENT_CONTEXT_CASE_LINK);
        if (!linkDoc.hasSchema(DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA)) {
            return;
        }
        CoreSession session = context.getCoreSession();
        DossierLink dossierLink = linkDoc.getAdapter(DossierLink.class);

        EventContext evtCtx = new EventContextImpl(session, session.getPrincipal());
        Map<String, Serializable> props = new HashMap<>();
        props.put(MAILBOX_REF_PROP, linkDoc.getParentRef());
        props.put(ROUTING_TASK_TYPE_PROP, dossierLink.getRoutingTaskType());
        props.put(MINISTERE_ID_PROP, dossierLink.getIdMinistereAttributaire());
        evtCtx.setProperties(props);
        getRequiredService(EventService.class).fireEvent(COMPUTE_MAILBOX_PRECOMPTAGE_EVENT, evtCtx);
    }
}
