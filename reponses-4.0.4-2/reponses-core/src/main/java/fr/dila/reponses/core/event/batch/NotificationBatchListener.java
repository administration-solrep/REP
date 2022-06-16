package fr.dila.reponses.core.event.batch;

import fr.dila.reponses.api.service.WsNotificationService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.core.event.batch.AbstractNotificationBatchListener;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Batch de ré-essai d'envoi des notifications aux webservices
 *
 * @author bgamard
 */
public class NotificationBatchListener extends AbstractNotificationBatchListener {

    public NotificationBatchListener() {
        super();
    }

    @Override
    protected void doNotifications(final CoreSession session) throws Exception {
        final WsNotificationService wsNotificationService = ReponsesServiceLocator.getWsNotificationService();
        wsNotificationService.retryNotifications(session);
    }
}
