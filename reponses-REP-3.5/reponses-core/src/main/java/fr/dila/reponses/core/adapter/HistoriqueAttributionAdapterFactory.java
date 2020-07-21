package fr.dila.reponses.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.reponses.api.constant.ReponsesSchemaConstant;
import fr.dila.reponses.core.historique.HistoriqueAttributionImpl;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet métier HistoriqueAttribution de réponses.
 *
 * @author arolin
 */
public class HistoriqueAttributionAdapterFactory implements DocumentAdapterFactory {

	/**
	 * Default constructor
	 */
	public HistoriqueAttributionAdapterFactory(){
		// do nothing
	}
	
	protected void checkDocument(DocumentModel doc) {
		if (!doc.hasSchema(ReponsesSchemaConstant.HISTORIQUE_ATTRIBUTION_DOCUMENT_SCHEMA)) {
			throw new CaseManagementRuntimeException(
					"Document should contain schema " + ReponsesSchemaConstant.HISTORIQUE_ATTRIBUTION_DOCUMENT_SCHEMA);
		}
	}

	@Override
	public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
		checkDocument(doc);
		return new HistoriqueAttributionImpl(doc);
	}
}
