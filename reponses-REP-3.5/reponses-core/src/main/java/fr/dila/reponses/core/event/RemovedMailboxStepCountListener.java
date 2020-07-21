package fr.dila.reponses.core.event;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.PostCommitEventListener;
import org.nuxeo.runtime.api.Framework;

import fr.dila.cm.event.CaseManagementEventConstants;
import fr.dila.cm.event.CaseManagementEventConstants.EventNames;
import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.api.mailbox.ReponsesMailbox;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.SessionUtil;

public class RemovedMailboxStepCountListener implements PostCommitEventListener {

	private static final STLogger LOGGER = STLogFactory.getLog(RemovedMailboxStepCountListener.class);
	
	@Override
	public void handleEvent(EventBundle events) throws ClientException {
		if(!events.containsEventName(EventNames.beforeCaseLinkRemovedEvent.name())){
            return;
        }
        for (final Event event : events) {
            if (EventNames.beforeCaseLinkRemovedEvent.name().equals(event.getName())) {
                handleEvent(event);
            }
        }
	}
	
    private void handleEvent(Event event) throws ClientException {
        EventContext eventCtx = event.getContext();
        CoreSession session = null;
        LoginContext loginContext = null;
        try {
        	// on se connecte à l'application en tant que super utilisateur
            loginContext = Framework.login();
        	session = SessionUtil.getCoreSession();
	        
            final DocumentModel linkDoc = (DocumentModel) eventCtx.getProperty(CaseManagementEventConstants.EVENT_CONTEXT_CASE_LINK);
            if (!linkDoc.hasSchema(DossierConstants.DOSSIER_REPONSES_LINK_SCHEMA)) {
                return;
            }
        	
        	new UnrestrictedSessionRunner(session) {
	            @Override
	            public void run() throws ClientException {
	                DocumentModel mailbox = session.getParentDocument(linkDoc.getRef());
	                ReponsesMailbox reponsesMailbox = mailbox.getAdapter(ReponsesMailbox.class);
	                
	                DossierLink dossierLink = linkDoc.getAdapter(DossierLink.class);
	                String routingTaskType = dossierLink.getRoutingTaskType();
	                String ministereId = dossierLink.getIdMinistereAttributaire();
	                
	                reponsesMailbox.decrPreComptage(ministereId, routingTaskType);
	
	                session.saveDocument(mailbox);
	                session.save();
	                
	                final DossierDistributionService service = ReponsesServiceLocator.getDossierDistributionService();
	                service.notifyMailboxModification(session, mailbox.getId());
	            }
	        }.runUnrestricted();
        } catch (LoginException exc) {
        	LOGGER.error(STLogEnumImpl.FAIL_LOGIN_USER_TEC, exc);
		} finally {
        	SessionUtil.close(session);
        	if (loginContext != null) {
                try {
					loginContext.logout();
				} catch (LoginException exc) {
					LOGGER.error(STLogEnumImpl.FAIL_LOGOUT_USER_TEC, exc);
				}
            }
        }
    }

}
