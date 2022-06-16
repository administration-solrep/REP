package fr.dila.reponses.core.event;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.reponses.api.service.AllotissementService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

/**
 * Gestionnaire d'évènements qui permet de traiter les évènements de
 * modification de la reponse
 *
 * @author asatre
 */
public class ReponseUpdateListener implements EventListener {

    /**
     * Constructeur de ReponseUpdateListener.
     */
    public ReponseUpdateListener() {}

    @Override
    public void handleEvent(Event event) {
        // Traite uniquement les évènements de modification de reponse
        EventContext ctx = event.getContext();
        DocumentEventContext context = (DocumentEventContext) ctx;
        if (
            !(
                event.getName().equals(ReponsesEventConstant.DOSSIER_REPONSE_UPDATE_EVENT) ||
                event.getName().equals(ReponsesEventConstant.DOSSIER_REPONSE_VERSION_UPDATE_EVENT)
            )
        ) {
            return;
        }

        // Traite uniquement les documents de type Reponse
        DocumentModel repDoc = context.getSourceDocument();
        String docType = repDoc.getType();
        if (!DossierConstants.REPONSE_DOCUMENT_TYPE.equals(docType)) {
            return;
        }

        AllotissementService allotissementService = ReponsesServiceLocator.getAllotissementService();
        if (event.getName().equals(ReponsesEventConstant.DOSSIER_REPONSE_UPDATE_EVENT)) {
            allotissementService.updateTexteLinkedReponses(ctx.getCoreSession(), repDoc);
        } else {
            allotissementService.updateVersionLinkedReponses(ctx.getCoreSession(), repDoc);
        }
    }
}
