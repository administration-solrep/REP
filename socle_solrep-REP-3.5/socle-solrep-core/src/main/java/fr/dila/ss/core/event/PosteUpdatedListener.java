package fr.dila.ss.core.event;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;

import fr.dila.cm.mailbox.Mailbox;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.core.event.AbstractFilterEventListener;

/**
 * Listener qui permet de mettre à jour le titre de la mailbox poste à la création des postes.
 * 
 * @author feo
 */
public class PosteUpdatedListener extends AbstractFilterEventListener<InlineEventContext> {

	public PosteUpdatedListener() {
		super(InlineEventContext.class);
	}

	@Override
	protected void doHandleEvent(final Event event, final InlineEventContext context) throws ClientException {
		final CoreSession session = context.getCoreSession();

		final String posteId = (String) context.getProperty(STEventConstant.ORGANIGRAMME_NODE_ID_EVENT_PARAM);
		final String posteName = (String) context.getProperty(STEventConstant.ORGANIGRAMME_NODE_LABEL_EVENT_PARAM);

		// Crée si nécessaire la Mailbox poste
		final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
		final Mailbox mlbx = mailboxPosteService.getOrCreateMailboxPoste(session, posteId);

		try {
			new UnrestrictedSessionRunner(session) {
				@Override
				public void run() throws ClientException {
					if (!mlbx.getTitle().equals(posteName)) {
						mlbx.setTitle(posteName);
						mlbx.save(session);
					}
				}
			}.runUnrestricted();
		} catch (ClientException e) {
			throw new RuntimeException(e);
		}
	}
}
