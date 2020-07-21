package fr.dila.reponses.core.event;

import java.util.HashSet;
import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.PostCommitEventListener;

import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.core.util.SessionUtil;

public class UpdateMailboxStepCountListener implements PostCommitEventListener {

    @Override
    public void handleEvent(EventBundle eventBundle) throws ClientException {
        
        if (eventBundle.containsEventName(ReponsesEventConstant.EVT_UPDATE_MAILBOX_STEP_COUNT)) {
            CoreSession session = null;
            try {             
                
                final Set<String> mailboxIdsToUpdate = new HashSet<String>();
                // collect mailbox doc id to update
                for (Event event : eventBundle) {
                    if (ReponsesEventConstant.EVT_UPDATE_MAILBOX_STEP_COUNT.equals(event.getName())) {
                    	final EventContext evtCtx = event.getContext();
                        final String mailboxDocId = (String) evtCtx.getProperty(ReponsesEventConstant.EVT_CONTEXT_MAILBOX_DOC_ID);
                        mailboxIdsToUpdate.add(mailboxDocId);
                    }
                }                
                
                session = SessionUtil.getCoreSession();
                // update for the mailbox ids
                ReponsesServiceLocator.getMailboxService().updatePrecomptageMailboxes(mailboxIdsToUpdate, session);                    
                session.save();
                
            } finally {
                SessionUtil.close(session);
            }

        }
    }

}
