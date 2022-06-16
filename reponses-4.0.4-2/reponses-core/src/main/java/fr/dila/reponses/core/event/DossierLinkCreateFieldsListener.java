package fr.dila.reponses.core.event;

import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.constant.STDossierLinkConstant;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

/**
 * Gestionnaire d'évènements qui permet de mettre en forme les dossiers links lors de l'affichage de la liste des dossiers : ajout d'un champ de tri au dossierLink, du champ date de réception et du champ auteur.
 *
 * @author arolin
 */
public class DossierLinkCreateFieldsListener implements EventListener {

    @Override
    public void handleEvent(Event event) {
        // Traite uniquement les évènements de document sur le point d'être créé
        EventContext ctx = event.getContext();
        DocumentEventContext context = (DocumentEventContext) ctx;
        if (!(event.getName().equals(DocumentEventTypes.ABOUT_TO_CREATE))) {
            return;
        }

        // Traite uniquement les documents de type Dossier
        CoreSession session = ctx.getCoreSession();
        DocumentModel dossierLinkDoc = context.getSourceDocument();
        String docType = dossierLinkDoc.getType();
        if (!STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE.equals(docType)) {
            return;
        }

        // on crée les champs du caseLink
        final DossierDistributionService dossierDistributionService = ReponsesServiceLocator.getDossierDistributionService();
        dossierDistributionService.setDossierLinksFields(session, dossierLinkDoc);
    }
}
