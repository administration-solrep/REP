package fr.dila.ss.core.schema;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.util.PropertyUtil;

/**
 * Manipulation du schema routing_task
 * @author SPL
 *
 */
public final class RoutingTaskSchemaUtils {

	/**
	 * utility class
	 */
	private RoutingTaskSchemaUtils(){
		// do nothing
	}
	
	public static void setDocumentRouteId(final DocumentModel doc, final String value){
		PropertyUtil.setProperty(doc, STSchemaConstant.ROUTING_TASK_SCHEMA, STSchemaConstant.ROUTING_TASK_DOCUMENT_ROUTE_ID_PROPERTY, value);
	}
	
	public static String getMailboxId(final DocumentModel doc){
		return PropertyUtil.getStringProperty(doc, STSchemaConstant.ROUTING_TASK_SCHEMA, STSchemaConstant.ROUTING_TASK_MAILBOX_ID_PROPERTY);
	}
	
	public static String getType(final DocumentModel doc) {
		return PropertyUtil.getStringProperty(doc, STSchemaConstant.ROUTING_TASK_SCHEMA, STSchemaConstant.ROUTING_TASK_TYPE_PROPERTY);
	}
	
	public static void setType(final DocumentModel doc, final String stepType) {
		PropertyUtil.setProperty(doc, STSchemaConstant.ROUTING_TASK_SCHEMA, STSchemaConstant.ROUTING_TASK_TYPE_PROPERTY, stepType);
	}
	
	public static void setDistributionMailboxId(final DocumentModel doc, final String distributionMailboxId){
		PropertyUtil.setProperty(doc, STSchemaConstant.ROUTING_TASK_SCHEMA, STSchemaConstant.ROUTING_TASK_MAILBOX_ID_PROPERTY, distributionMailboxId);
	}
	
	public static String getDistributionMailboxId(final DocumentModel doc){
		return PropertyUtil.getStringProperty(doc, STSchemaConstant.ROUTING_TASK_SCHEMA, STSchemaConstant.ROUTING_TASK_MAILBOX_ID_PROPERTY);
	}
}
