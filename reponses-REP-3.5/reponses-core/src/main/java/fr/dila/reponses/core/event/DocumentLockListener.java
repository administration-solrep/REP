package fr.dila.reponses.core.event;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;

import fr.dila.ecm.platform.routing.api.DocumentRoutingConstants;
import fr.dila.reponses.api.feuilleroute.ReponsesFeuilleRoute;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.feuilleroute.STFeuilleRoute;
import fr.dila.st.api.service.STLockService;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Listener permettant de traiter les changement de document pour verrouiller / déverrouiller automatiquement les documents. TODO il faut aussi notifier le document parent que ses fils ont changé : cela doit etre fait dans la couche Web ex Events.instance().raiseEvent(EventNames.DOCUMENT_CHILDREN_CHANGED, currentRouteModel.getDocument());
 * 
 * @author jtremeaux
 */
public class DocumentLockListener implements EventListener {
    private static final Log log = LogFactory.getLog(DocumentLockListener.class);

    @Override
    public void handleEvent(Event event) throws ClientException {
        // Traite uniquement les évènements de changement document courant
        EventContext ctx = event.getContext();
        if (!(ctx instanceof InlineEventContext)) {
            return;
        }
        InlineEventContext context = (InlineEventContext) ctx;
        final String eventName = event.getName();
        if (!eventName.equals(STEventConstant.CURRENT_DOCUMENT_CHANGED_EVENT)) {
            return;
        }

        // Tente de déverrouiller l'ancien document
        final DocumentModel oldDoc = (DocumentModel) context.getProperty(STEventConstant.OLD_DOCUMENT_EVENT_PARAM);
        final DocumentModel newDoc = (DocumentModel) context.getProperty(STEventConstant.NEW_DOCUMENT_EVENT_PARAM);
        if (oldDoc != null) {
            unlockDocument(ctx.getCoreSession(), (NuxeoPrincipal) ctx.getPrincipal(), oldDoc, newDoc);
        }

        // Tente de vérrouiller le nouveau document
        if (newDoc != null) {
            lockDocument(ctx.getCoreSession(), (NuxeoPrincipal) ctx.getPrincipal(), oldDoc, newDoc);
        }
    }

    /**
     * Tente de verrouiller un document. Si le document n'est pas verrouillable (l'utilisateur n'a pas les droits, ou il est déja verrouillé par un autre utilisateur) ce n'est pas une erreur, mais le document sera consultable en lecture seule.
     * 
     * @param session Session
     * @param principal Principal
     * @param oldDoc Ancien document
     * @param newDoc Document à vérrouiller (remplace l'ancien document)
     */
    private void lockDocument(CoreSession session, NuxeoPrincipal principal, DocumentModel oldDoc, DocumentModel newDoc) {
        final STLockService stLockService = STServiceLocator.getSTLockService();

        try {
            String docType = newDoc.getType();
            if (STConstant.FEUILLE_ROUTE_DOCUMENT_TYPE.equals(docType)) {
                // Verrouille les modèles feuilles de route
                STFeuilleRoute feuilleRoute = newDoc.getAdapter(STFeuilleRoute.class);
                if (feuilleRoute.isModel()) {
                    if (stLockService.isLockActionnableByUser(session, newDoc, principal)) {
                        stLockService.lockDoc(session, newDoc);
                    }
                }
            }
        } catch (ClientException e) {
            log.warn("Erreur de verrouillage automatique du document [" + newDoc.getName() + "]", e);
        }
    }

    /**
     * Tente de déverrouiller un document. Si le document n'est pas déverrouillable cela signifie que le document était verrouillé par un autre utilisateur et il n'y a pas d'action à effectuer.
     * 
     * @param session Session
     * @param principal Principal
     * @param oldDoc Document à déverrouiller
     * @param newDoc Nouveau document (remplace le document déverrouillé)
     */
    private void unlockDocument(CoreSession session, NuxeoPrincipal principal, DocumentModel oldDoc, DocumentModel newDoc) {
        final STLockService stLockService = STServiceLocator.getSTLockService();

        final String oldDocType = oldDoc.getType();
        final Set<String> newDocFacet = new HashSet<String>();
        if (newDoc != null) {
            newDocFacet.addAll(newDoc.getFacets());
        }

        try {
            if (STConstant.FEUILLE_ROUTE_DOCUMENT_TYPE.equals(oldDocType)) {
                // Déverrouille les modèles feuilles de route
                ReponsesFeuilleRoute feuilleRoute = oldDoc.getAdapter(ReponsesFeuilleRoute.class);
                if (feuilleRoute.isModel()) {
                    // Ne déverrouille pas encore si on édite une étape de la feuille de route
                    if (!newDocFacet.contains(DocumentRoutingConstants.ROUTE_STEP_FACET)) {
                        if (stLockService.isLockActionnableByUser(session, oldDoc, principal)) {
                            stLockService.unlockDoc(session, oldDoc);
                        }
                    }
                }
            }
        } catch (ClientException e) {
            log.warn("Erreur de verrouillage automatique du document [" + oldDoc.getName() + "]", e);
        }
    }
}
