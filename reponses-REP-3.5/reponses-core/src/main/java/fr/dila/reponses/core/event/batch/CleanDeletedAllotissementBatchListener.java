package fr.dila.reponses.core.event.batch;

import java.util.LinkedList;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.api.constant.ReponsesEventConstant;
import fr.dila.st.core.event.batch.CleanDeletedDocumentBatchListener;

/**
 * Batch de nettoyage des allotissements à l'état deleted
 *
 */
public class CleanDeletedAllotissementBatchListener extends CleanDeletedDocumentBatchListener {	    
	/**
	 * Default constructor
	 */
	public CleanDeletedAllotissementBatchListener() {
		super(ReponsesEventConstant.CLEAN_DELETED_ALLOT_EVENT, getDocumentTypes());
	}

	protected static final LinkedList<String> getDocumentTypes() {
		LinkedList<String> documents = new LinkedList<String>();
		documents.add(DossierConstants.ALLOTISSEMENT_DOCUMENT_TYPE);
		return documents;
	}
}
