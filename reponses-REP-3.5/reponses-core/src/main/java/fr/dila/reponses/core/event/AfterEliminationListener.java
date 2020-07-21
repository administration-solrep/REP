package fr.dila.reponses.core.event;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.PostCommitEventListener;

import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.logging.enumerationCodes.ReponsesLogEnumImpl;
import fr.dila.reponses.api.service.ArchiveService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;

/**
 * Gestionnaire d'évènements qui permet l'envoi d'un mail en html aux membres du poste lors de la distribution du dossier. postCommit et async
 * 
 * @author asatre
 */
public class AfterEliminationListener implements PostCommitEventListener {

    private static final STLogger LOGGER = STLogFactory.getLog(AfterEliminationListener.class);

    @Override
    public void handleEvent(EventBundle events) throws ClientException {
        for (Event event : events) {
            if (event.getName().equals(ReponsesEventConstant.AFTER_ELIMINATION_LISTE) || 
            		event.getName().equals(ReponsesEventConstant.AFTER_ABANDON_LISTE) ) {
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
            final EventContext ctx = event.getContext();
            final CoreSession session = ctx.getCoreSession();
            final DocumentModel eliminationList = (DocumentModel) ctx.getProperty(ReponsesEventConstant.DOSSIER_EVENT_PARAM);
            final ArchiveService archiveService = ReponsesServiceLocator.getArchiveService();
            
            if (event.getName().equals(ReponsesEventConstant.AFTER_ELIMINATION_LISTE)) {
            	try {
					archiveService.suppressionListeElimination(session, eliminationList);
				} catch (ClientException e) {
					LOGGER.error(session, ReponsesLogEnumImpl.FAIL_DEL_LISTE_ELIMINATION_TEC, eliminationList, e);
				}
            } else if (event.getName().equals(ReponsesEventConstant.AFTER_ABANDON_LISTE)) {
            	try {
					archiveService.abandonListeElimination(session, eliminationList);
				} catch (ClientException e) {
					LOGGER.error(session, ReponsesLogEnumImpl.FAIL_DEL_LISTE_ELIMINATION_TEC, eliminationList, e);
				}
            }
    }
}
