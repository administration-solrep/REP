package fr.dila.ss.core.schema;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.ecm.platform.routing.api.DocumentRoutingConstants.ExecutionTypeValues;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.util.PropertyUtil;

/**
 * Manipulation des propriété du schema STEP_FOLDER
 * @author SPL
 *
 */
public final class StepFolderSchemaUtils {

	/**
	 * Utility class
	 */
	private StepFolderSchemaUtils(){
		// do nothing
	}
	
	public static String getExecution(final DocumentModel doc) {
		return PropertyUtil.getStringProperty(doc, STSchemaConstant.STEP_FOLDER_SCHEMA, STSchemaConstant.STEP_FOLDER_EXECUTION_PROPERTY);
	}
	
	public static  ExecutionTypeValues getExecutionType(final DocumentModel doc) {
		return ExecutionTypeValues.valueOf(getExecution(doc));
	}
	
	public static void setExecution(final DocumentModel doc, final String execution){
		PropertyUtil.setProperty(doc,
                STSchemaConstant.STEP_FOLDER_SCHEMA, STSchemaConstant.STEP_FOLDER_EXECUTION_PROPERTY,
                execution);
	}
	
}
