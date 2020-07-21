package fr.dila.ss.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.ss.api.constant.SSTreeConstants;
import fr.dila.ss.core.tree.SSTreeFileImpl;

/**
 * Adapteur de DocumentModel vers StepFolder.
 * 
 * @author jtremeaux
 */
public class SSTreeFileAdapterFactory implements DocumentAdapterFactory {

	/**
     * Default constructor
     */
    public SSTreeFileAdapterFactory(){
    	// do nothing
    }
	
    protected void checkDocument(DocumentModel doc) {
        if (!doc.hasSchema(SSTreeConstants.FILE_SCHEMA)) {
            throw new CaseManagementRuntimeException("Document should contain schema " + SSTreeConstants.FILE_SCHEMA);
        }
    }

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        checkDocument(doc);
        return new SSTreeFileImpl(doc);
    }


}
