package fr.dila.reponses.core.event;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.cases.Dossier;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.constant.STDossierLinkConstant;
import fr.dila.st.core.event.RollbackEventListener;

/**
 * Listener exécuté après la création d'un DossierLink.
 * 
 * @author jtremeaux
 */
public class AfterDossierLinkCreatedListener extends RollbackEventListener {
    @Override
    public void handleDocumentEvent(Event event, DocumentEventContext ctx) throws ClientException {
        // Traite uniquement les évènements de document sur le point d'être créé
        if (!event.getName().equals(DocumentEventTypes.DOCUMENT_CREATED)) {
            return;
        }

        // Traite uniquement les documents de type DossierLink
        final DocumentModel dossierLinkDoc = ctx.getSourceDocument();

        String docType = dossierLinkDoc.getType();
        if (!STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE.equals(docType)) {
            return;
        }

        CoreSession session = ctx.getCoreSession();

        new UnrestrictedSessionRunner(session) {
            @Override
            public void run() throws ClientException {
                // Récupère le dossier TODO on pourrait éviter une requete en utilisant un evenement custom et en passant le Dossier en paramètre
                DossierLink dossierLink = dossierLinkDoc.getAdapter(DossierLink.class);
                Dossier dossier = dossierLink.getDossier(session);

                // Initialise les droits du DossierLink
                final DossierDistributionService dossierDistributionService = ReponsesServiceLocator.getDossierDistributionService();
                dossierDistributionService.initDossierLinkAcl(session, dossier.getDocument(), dossierLinkDoc);
            }
        }.runUnrestricted();
    }
}
