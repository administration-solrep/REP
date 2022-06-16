package fr.dila.reponses.api.service;

import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Service de notification (vers les ministeres interfacés)
 * @author bgamard
 *
 */
public interface WsNotificationService {
    /**
     * Tente d'envoyer une notification aux webservice des ministères
     * @param docId
     * @param webservice
     * @param session
     *
     */
    void notifierEntite(String docId, String webservice, CoreSession session);

    /**
     * Rééssaie d'envoyer les notifications en echec
     * @param session
     *
     */
    void retryNotifications(CoreSession session);
}
