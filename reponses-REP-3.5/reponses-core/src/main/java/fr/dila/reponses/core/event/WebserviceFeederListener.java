package fr.dila.reponses.core.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

import fr.dila.reponses.api.caselink.DossierLink;
import fr.dila.reponses.api.service.DossierDistributionService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.constant.STDossierLinkConstant;
import fr.dila.st.core.schema.DublincoreSchemaUtils;

/**
 * Listener qui se place juste après que le dossierLink soit setté, afin de créer un jeton si le dossierLink concerne un ministère interfacé. 
 * 
 * @author sly
 */
public class WebserviceFeederListener implements EventListener {
    private static final Log log = LogFactory.getLog(WebserviceFeederListener.class);
    
    @Override
    public void handleEvent(Event event) throws ClientException {
        EventContext ctx = event.getContext();
        DocumentEventContext context = (DocumentEventContext) ctx;
        if (!(event.getName().equals(DocumentEventTypes.ABOUT_TO_CREATE))) {
            return;
        }
        
        // Traite uniquement les documents de type Dossier
        DocumentModel doc = context.getSourceDocument();
        String docType = doc.getType();
        if (!STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE.equals(docType)) {
            return;
        }

        if (DublincoreSchemaUtils.getTitle(doc) == null) {
            return;
        }
        
        CoreSession session = ctx.getCoreSession();
        DossierLink dlink = doc.getAdapter(DossierLink.class);
        DossierDistributionService dds = ReponsesServiceLocator.getDossierDistributionService();
        try {
            dds.specificStepsOperation(dlink, session);
        } catch (Exception e) {
            log.warn("Erreur lors du traitement du passage à l'étape "+dlink.getRoutingTaskLabel());
        }
        

    }
}
