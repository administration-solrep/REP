package fr.dila.ss.core.event;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.core.event.AbstractFilterEventListener;

/**
 * Gestionnaire d'évènements qui permet de renseigner le nom des documents RouteStep. 
 * 
 * @author jtremeaux
 */
public class RouteStepAboutToCreateListener extends AbstractFilterEventListener<DocumentEventContext> {
	
	/**
	 * Default constructor
	 */
	public RouteStepAboutToCreateListener(){
		super(DocumentEventTypes.ABOUT_TO_CREATE, DocumentEventContext.class);
	}
	
    @Override
    protected void doHandleEvent(final Event event, final DocumentEventContext context) throws ClientException {

        // Traite uniquement les documents de type RouteStep
        final DocumentModel doc = context.getSourceDocument();
        final String docType = doc.getType();
        if (!STConstant.ROUTE_STEP_DOCUMENT_TYPE.equals(docType)) {
            return;
        }

        // Renseigne le nom du document à partir de son type d'étape
        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
        documentRoutingService.setRouteStepDocName(doc);
    }
}
