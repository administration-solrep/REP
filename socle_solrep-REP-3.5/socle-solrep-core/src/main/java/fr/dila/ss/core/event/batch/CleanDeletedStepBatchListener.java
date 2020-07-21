package fr.dila.ss.core.event.batch;

import java.util.LinkedList;

import fr.dila.ss.api.constant.SSEventConstant;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.core.event.batch.CleanDeletedDocumentBatchListener;

public class CleanDeletedStepBatchListener extends CleanDeletedDocumentBatchListener {
	    
    /**
     * Default constructor
     */
    public CleanDeletedStepBatchListener(){
    	super(SSEventConstant.CLEAN_DELETED_STEP_EVENT, getDocumentTypes());
    }
    
    protected static final LinkedList<String> getDocumentTypes() {
        LinkedList<String> documents = new LinkedList<String>();
        documents.add(STConstant.ROUTE_STEP_DOCUMENT_TYPE);
        documents.add(STConstant.STEP_FOLDER_DOCUMENT_TYPE);
        documents.add(STConstant.FEUILLE_ROUTE_DOCUMENT_TYPE);
        documents.add(STConstant.FEUILLE_ROUTE_MODEL_FOLDER_DOCUMENT_TYPE);
        return documents;
    }
}
