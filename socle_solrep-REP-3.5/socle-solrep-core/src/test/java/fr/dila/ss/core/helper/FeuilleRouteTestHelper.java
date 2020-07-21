package fr.dila.ss.core.helper;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.ss.core.schema.RoutingTaskSchemaUtils;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.core.schema.DublincoreSchemaUtils;

/**
 * Class utilitaire pour manipuler les feuilles de routes
 * 
 * @author SPL
 *
 */
public final class FeuilleRouteTestHelper {

	/**
	 * utility class
	 */
	private FeuilleRouteTestHelper(){
		// do nothing
	}
	
	/**
	 * Crée une étape de feuille de route
	 * @param session
	 * @param parent
	 * @param userMailboxId
	 * @param stepTitle
	 * @param stepType
	 * @return
	 * @throws Exception
	 */
	public static DocumentModel createSerialStep(CoreSession session, DocumentModel parent, String userMailboxId, String stepTitle, String stepType) throws ClientException {
        DocumentModel step = createDocumentModel(session, stepTitle, STConstant.ROUTE_STEP_DOCUMENT_TYPE, parent.getPathAsString());
        RoutingTaskSchemaUtils.setDistributionMailboxId(step, userMailboxId);
        RoutingTaskSchemaUtils.setType(step, stepType);
        return session.saveDocument(step);
    }
		
	private static DocumentModel createDocumentModel(CoreSession session, String name, String type, String path) throws ClientException {
        DocumentModel doc = session.createDocumentModel(path, name, type);
        DublincoreSchemaUtils.setTitle(doc, name);
        return session.createDocument(doc);
    }

	
}
