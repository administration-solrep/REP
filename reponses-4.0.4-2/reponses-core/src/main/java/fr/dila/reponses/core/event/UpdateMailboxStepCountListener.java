package fr.dila.reponses.core.event;

import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.sword.naiad.nuxeo.commons.core.util.SessionUtil;
import java.util.HashSet;
import java.util.Set;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.PostCommitEventListener;

public class UpdateMailboxStepCountListener implements PostCommitEventListener {

    @Override
    public void handleEvent(EventBundle eventBundle) {
        if (eventBundle.containsEventName(ReponsesEventConstant.EVT_UPDATE_MAILBOX_STEP_COUNT)) {
            final Set<String> mailboxIdsToUpdate = new HashSet<>();
            // collect mailbox doc id to update
            for (Event event : eventBundle) {
                if (ReponsesEventConstant.EVT_UPDATE_MAILBOX_STEP_COUNT.equals(event.getName())) {
                    final EventContext evtCtx = event.getContext();
                    final String mailboxDocId = (String) evtCtx.getProperty(
                        ReponsesEventConstant.EVT_CONTEXT_MAILBOX_DOC_ID
                    );
                    mailboxIdsToUpdate.add(mailboxDocId);
                }
            }

            try (CloseableCoreSession session = SessionUtil.openSession()) {
                // update for the mailbox ids
                ReponsesServiceLocator.getMailboxService().updatePrecomptageMailboxes(mailboxIdsToUpdate, session);
                session.save();
            }
        }
    }
}
