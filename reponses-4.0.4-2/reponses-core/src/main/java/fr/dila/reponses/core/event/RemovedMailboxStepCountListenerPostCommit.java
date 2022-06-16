package fr.dila.reponses.core.event;

import static com.google.common.collect.Streams.stream;
import static fr.dila.reponses.api.constant.ReponsesEventConstant.COMPUTE_MAILBOX_PRECOMPTAGE_EVENT;
import static fr.dila.reponses.core.event.RemovedMailboxStepCountListener.MAILBOX_REF_PROP;
import static fr.dila.reponses.core.event.RemovedMailboxStepCountListener.MINISTERE_ID_PROP;
import static fr.dila.reponses.core.event.RemovedMailboxStepCountListener.ROUTING_TASK_TYPE_PROP;
import static java.lang.String.format;

import fr.dila.reponses.api.mailbox.ReponsesMailbox;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import java.io.Serializable;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.PostCommitEventListener;

/**
 * Listener post commit qui met à jour le précomptage d'un mailbox
 *
 */
public class RemovedMailboxStepCountListenerPostCommit implements PostCommitEventListener {
    private static final STLogger LOG = STLogFactory.getLog(RemovedMailboxStepCountListenerPostCommit.class);

    @Override
    public void handleEvent(EventBundle events) {
        stream(events).filter(e -> COMPUTE_MAILBOX_PRECOMPTAGE_EVENT.equals(e.getName())).forEach(this::handleEvent);
    }

    private void handleEvent(Event event) {
        EventContext eventCtx = event.getContext();
        CoreSession session = eventCtx.getCoreSession();

        CoreInstance.doPrivileged(
            session,
            systemSession -> {
                Map<String, Serializable> props = eventCtx.getProperties();
                DocumentRef mailboxRef = (DocumentRef) props.get(MAILBOX_REF_PROP);
                String routingTaskType = (String) props.get(ROUTING_TASK_TYPE_PROP);
                String ministereId = (String) props.get(MINISTERE_ID_PROP);

                LOG.info(
                    STLogEnumImpl.DEFAULT,
                    format(
                        "Mis à jour du précomptage pour le mailbox [%s] avec le routingTaskType [%s] et ministereId [%s]",
                        mailboxRef,
                        routingTaskType,
                        ministereId
                    )
                );

                DocumentModel mailbox = systemSession.getDocument(mailboxRef);
                if (routingTaskType != null && ministereId != null) {
                    ReponsesMailbox reponsesMailbox = mailbox.getAdapter(ReponsesMailbox.class);
                    reponsesMailbox.decrPreComptage(ministereId, routingTaskType);
                    systemSession.saveDocument(mailbox);

                    final DossierDistributionService service = ReponsesServiceLocator.getDossierDistributionService();
                    service.notifyMailboxModification(session, mailbox.getId());
                }
            }
        );
    }
}
