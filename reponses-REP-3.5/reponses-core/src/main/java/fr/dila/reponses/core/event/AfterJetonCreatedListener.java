package fr.dila.reponses.core.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.PostCommitEventListener;

import fr.dila.reponses.api.service.WsNotificationService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.constant.STEventConstant;

/**
 * Listener qui se place juste après que le dossierLink soit setté, afin de créer un jeton si le dossierLink concerne un ministère interfacé.
 * 
 * @author sly
 */
public class AfterJetonCreatedListener implements PostCommitEventListener {

    /**
     * Logger.
     */
    private static final Log log = LogFactory.getLog(AfterJetonCreatedListener.class);

    public void handleEvent(Event event) throws ClientException {
        EventContext ctx = event.getContext();
        if (!event.getName().equals(STEventConstant.AFTER_CREATION_JETON)) {
            return;
        }

        CoreSession session = ctx.getCoreSession();
        String webservice = (String) ctx.getProperty(STEventConstant.AFTER_CREATION_JETON_PARAM_WEBSERVICE);
        String docId = (String) ctx.getProperty(STEventConstant.AFTER_CREATION_JETON_PARAM_DOC_ID);

        if (log.isInfoEnabled()) {
            log.info("AfterJetonCreatedListener " + docId);
        }

        WsNotificationService wns = ReponsesServiceLocator.getWsNotificationService();
        wns.notifierEntite(docId, webservice, session);

        if (log.isInfoEnabled()) {
            log.info("AfterJetonCreatedListener " + docId + " END");
        }
    }

    @Override
    public void handleEvent(EventBundle events) throws ClientException {
    	if (events.containsEventName(STEventConstant.AFTER_CREATION_JETON)) {
            for (Event event : events) {
                handleEvent(event);
            }
        }
    }
}
