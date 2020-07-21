package fr.dila.ss.core.event;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;

import fr.dila.cm.cases.Case;
import fr.dila.cm.event.CaseManagementEventConstants;
import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.cm.security.CaseManagementSecurityConstants;
import fr.dila.st.core.event.AbstractEventListener;

/**
 * Surcharge de l'évenement Nuxeo pour ne mettre les droits à la distribution que sur le Case
 *
 * @author bgamard
 */
public class DistributionListener extends AbstractEventListener {

	/**
	 * Default constructor
	 */
	public DistributionListener(){
		super();
	}
	
    @SuppressWarnings("unchecked")
    public void doHandleEvent(Event event) throws ClientException {
        EventContext eventCtx = event.getContext();
        // set all rights to mailbox users

        Object envelopeObject = eventCtx.getProperty(CaseManagementEventConstants.EVENT_CONTEXT_CASE);
        if (!(envelopeObject instanceof Case)) {
            return;
        }
        Case envelope = (Case) envelopeObject;
        
        Map<String, List<String>> recipients = (Map<String, List<String>>) eventCtx.getProperty(CaseManagementEventConstants.EVENT_CONTEXT_INTERNAL_PARTICIPANTS);
        if (recipients == null) {
            return;
        }
        try {
            SetEnvelopeAclUnrestricted session = new SetEnvelopeAclUnrestricted(
                    eventCtx.getCoreSession(), envelope, recipients);
            session.runUnrestricted();
        } catch (Exception e) {
            throw new CaseManagementRuntimeException(e.getMessage(), e);
        }

    }

    public static class SetEnvelopeAclUnrestricted extends
            UnrestrictedSessionRunner {

        protected final Case envelope;

        protected final Map<String, List<String>> recipients;

        protected Set<String> allMailboxIds = new HashSet<String>();

        public SetEnvelopeAclUnrestricted(CoreSession session, Case envelope,
                Map<String, List<String>> recipients) {
            super(session);
            this.envelope = envelope;
            this.recipients = recipients;
        }

        @Override
        public void run() throws ClientException {
            for (Map.Entry<String, List<String>> recipient : recipients.entrySet()) {
                allMailboxIds.addAll(recipient.getValue());
            }
            if (!allMailboxIds.isEmpty()) {
                DocumentModel envelopeDoc = envelope.getDocument();
                if (envelopeDoc != null) {
                    setRightsOnCaseItems(envelopeDoc.getRef());
                }
            }
        }

        protected void setRightsOnCaseItems(DocumentRef docRef)
                throws ClientException {
            final ACP acp = session.getACP(docRef);
            final ACL mailboxACL = acp.getOrCreateACL(CaseManagementSecurityConstants.ACL_MAILBOX_PREFIX);
            final List<ACE> newACE = getNewACEs();
            mailboxACL.addAll(newACE);
            acp.removeACL(CaseManagementSecurityConstants.ACL_MAILBOX_PREFIX);
            acp.addACL(mailboxACL);
            session.setACP(docRef, acp, true);
        }

        protected List<ACE> getNewACEs() {
        	final List<ACE> newACEs = new LinkedList<ACE>();
            // compute private ace
            for (String mailboxId : allMailboxIds) {
                    newACEs.add(new ACE(
                            CaseManagementSecurityConstants.MAILBOX_PREFIX
                                    + mailboxId, SecurityConstants.READ_WRITE,
                            true));
            }
            return newACEs;
        }
    }

}
