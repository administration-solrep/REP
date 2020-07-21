package fr.dila.reponses.core.event;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;

/**
 * Gestionnaire d'évènements qui permet de traiter les évènements de création des documents
 * de type Dossier Réponses. 
 * 
 * @author jtremeaux
 */
public class DossierReponsesCreationListener implements EventListener {
//    private static final Log log = LogFactory.getLog(DossierReponsesCreationListener.class);

    @Override
    public void handleEvent(Event event) throws ClientException {
        // Traite uniquement les évènements de création de document
        EventContext ctx = event.getContext();
        DocumentEventContext context = (DocumentEventContext) ctx;
        if (!(event.getName().equals(DocumentEventTypes.DOCUMENT_CREATED))) {
            return;
        }

        // Traite uniquement les documents de type Dossier
        DocumentModel dossierDoc = context.getSourceDocument();
        String docType = dossierDoc.getType();
        if (!DossierConstants.DOSSIER_DOCUMENT_TYPE.equals(docType)) {
            return;
        }
        
        // Démarre la feuille de route associée au dossier
        DossierDistributionService dossierDistributionService = ReponsesServiceLocator.getDossierDistributionService();
        Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        dossierDistributionService.startDefaultRoute(ctx.getCoreSession(), dossier);
     }
}
