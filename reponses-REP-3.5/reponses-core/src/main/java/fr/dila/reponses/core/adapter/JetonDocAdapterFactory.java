package fr.dila.reponses.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.reponses.core.domain.JetonDocImpl;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet métier JetonDoc de réponses.
 *
 * @author arolin
 */
public class JetonDocAdapterFactory implements DocumentAdapterFactory {

	/**
	 * Default constructor
	 */
	public JetonDocAdapterFactory(){
		// do nothing
	}
	
	protected void checkDocument(DocumentModel doc) {
		if (!doc.hasSchema(STSchemaConstant.JETON_DOCUMENT_SCHEMA)) {
			throw new CaseManagementRuntimeException(
					"Document should contain schema " + STSchemaConstant.JETON_DOCUMENT_SCHEMA);
		}
	}

	@Override
	public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
		checkDocument(doc);
		return new JetonDocImpl(doc);
	}
}
