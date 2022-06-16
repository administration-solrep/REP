package fr.dila.reponses.core.event;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

/**
 * Gestionnaire d'évènements qui permet de traiter le nom du document de type Dossier.
 *
 * @author jtremeaux
 */
public class DossierPathCreationListener implements EventListener {

    @Override
    public void handleEvent(Event event) {
        // Traite uniquement les évènements de document sur le point d'être créé
        EventContext ctx = event.getContext();
        DocumentEventContext context = (DocumentEventContext) ctx;
        if (!(event.getName().equals(DocumentEventTypes.ABOUT_TO_CREATE))) {
            return;
        }

        // Traite uniquement les documents de type Dossier
        DocumentModel doc = context.getSourceDocument();
        String docType = doc.getType();
        if (!DossierConstants.DOSSIER_DOCUMENT_TYPE.equals(docType)) {
            return;
        }

        // Renseigne le nom du document dossier à partir du numero de la question
        final DossierDistributionService dossierDistributionService = ReponsesServiceLocator.getDossierDistributionService();
        dossierDistributionService.setDossierPathName(doc);
    }
}
