package fr.dila.reponses.core.event;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;

import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STEventConstant;

/**
 * Listener qui permet de créer les mailbox poste à la création des postes.
 * 
 * @author jtremeaux
 */
public class PosteCreatedListener implements EventListener {

	@Override
	public void handleEvent(Event event) throws ClientException {
		EventContext ctx = event.getContext();
		if (!(ctx instanceof InlineEventContext)) {
			return;
		}
		InlineEventContext context = (InlineEventContext) ctx;
		CoreSession session = context.getCoreSession();

		String posteId = (String) context.getProperty(STEventConstant.ORGANIGRAMME_NODE_ID_EVENT_PARAM);
		String posteName = (String) context.getProperty(STEventConstant.ORGANIGRAMME_NODE_LABEL_EVENT_PARAM);

		// Crée si nécessaire la Mailbox poste
		final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
		mailboxPosteService.createPosteMailbox(session, posteId, posteName);
	}
}
