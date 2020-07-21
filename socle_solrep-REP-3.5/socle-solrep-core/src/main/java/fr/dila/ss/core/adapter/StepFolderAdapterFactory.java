package fr.dila.ss.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.ecm.platform.routing.api.DocumentRoutingConstants;
import fr.dila.ecm.platform.routing.api.DocumentRoutingConstants.ExecutionTypeValues;
import fr.dila.ecm.platform.routing.core.impl.ParallelRunner;
import fr.dila.ecm.platform.routing.core.impl.SerialRunner;
import fr.dila.ss.core.domain.feuilleroute.StepFolderImpl;
import fr.dila.ss.core.schema.StepFolderSchemaUtils;
import fr.dila.st.api.constant.STConstant;

/**
 * Adapteur de DocumentModel vers StepFolder.
 * 
 * @author jtremeaux
 */
public class StepFolderAdapterFactory implements DocumentAdapterFactory {

	/**
     * Default constructor
     */
    public StepFolderAdapterFactory(){
    	// do nothing
    }
	
    @Override
    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        if (DocumentRoutingConstants.STEP_FOLDER_DOCUMENT_TYPE.equalsIgnoreCase(doc.getType())) {
            ExecutionTypeValues executionType = StepFolderSchemaUtils.getExecutionType(doc);
            switch (executionType) {
            case serial:
                return new StepFolderImpl(doc, new SerialRunner());
            case parallel:
                return new StepFolderImpl(doc, new ParallelRunner());
            default:
                throw new CaseManagementRuntimeException("Unknown execution type");
            }
        } else {
            throw new CaseManagementRuntimeException("Document should be of type " + STConstant.STEP_FOLDER_DOCUMENT_TYPE);
        }
    }

}
