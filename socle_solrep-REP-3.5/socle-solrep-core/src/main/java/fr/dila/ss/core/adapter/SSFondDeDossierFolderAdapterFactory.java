package fr.dila.ss.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.ss.api.constant.SSTreeConstants;
import fr.dila.ss.core.fondDeDossier.SSFondDeDossierFolderImpl;

/**
 * Adapteur de DocumentModel vers SSTreeFolder.
 * 
 */
public class SSFondDeDossierFolderAdapterFactory implements DocumentAdapterFactory {

	/**
     * Default constructor
     */
    public SSFondDeDossierFolderAdapterFactory(){
    	// do nothing
    }
	
    protected void checkDocument(DocumentModel doc) {
        if (!doc.hasFacet(SSTreeConstants.FOLDERISH_FACET)) {
            throw new CaseManagementRuntimeException("Document should contain facet " + SSTreeConstants.FOLDERISH_FACET);
        }
    }

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        checkDocument(doc);
        return new SSFondDeDossierFolderImpl(doc);
    }


}
