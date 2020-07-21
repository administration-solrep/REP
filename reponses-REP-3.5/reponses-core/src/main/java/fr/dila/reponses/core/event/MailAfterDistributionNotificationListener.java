package fr.dila.reponses.core.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.PostCommitEventListener;

import fr.dila.reponses.api.service.FeuilleRouteService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.feuilleroute.STRouteStep;

/**
 * Gestionnaire d'évènements qui permet l'envoi d'un mail en html aux membres du poste lors de la distribution du dossier. postCommit et async
 * 
 * @author asatre
 */
public class MailAfterDistributionNotificationListener implements PostCommitEventListener {

    private static final Log log = LogFactory.getLog(MailAfterDistributionNotificationListener.class);

    @Override
    public void handleEvent(EventBundle events) throws ClientException {
        for (Event event : events) {
            if (event.getName().equals(STEventConstant.SEND_MAIL_AFTER_DISTRIBUTION_NOTIFICATION)) {
            	// Traite uniquement les evenement de type
                // sendMailAfterDistributionNotification
                handleEvent(event);
            }
        }
    }

    /**
     * Traite uniquement les documents de type sendMailAfterDistributionNotification
     * @param event
     * @throws ClientException
     */
    private final void handleEvent(Event event) {
        try {
            final EventContext ctx = event.getContext();
            final CoreSession session = ctx.getCoreSession();

            final FeuilleRouteService feuilleRouteService = ReponsesServiceLocator.getFeuilleRouteService();
            final DocumentModel routeStepDoc = (DocumentModel) ctx.getProperty(STEventConstant.NEW_DOCUMENT_EVENT_PARAM);
            STRouteStep routeStep = routeStepDoc.getAdapter(STRouteStep.class);

            feuilleRouteService.sendMailAfterDistribution(session, routeStep);

        } catch (ClientException e) {
            log.error("MailNotificationListener Error : ", e);
        }
    }
}
