package fr.dila.reponses.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.reponses.api.constant.ReponsesSchemaConstant;
import fr.dila.reponses.core.cases.flux.QuestionStateChangeImpl;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet métier HistoriqueAttribution de réponses.
 * 
 * @author arolin
 */
public class EtatQuestionAdapterFactory implements DocumentAdapterFactory {

	/**
	 * Default constructor
	 */
	public EtatQuestionAdapterFactory() {
		// do nothing
	}

	protected void checkDocument(DocumentModel doc) {
		if (!doc.hasSchema(ReponsesSchemaConstant.ETAT_QUESTION_DOCUMENT_SCHEMA)) {
			throw new CaseManagementRuntimeException("Document should contain schema "
					+ ReponsesSchemaConstant.ETAT_QUESTION_DOCUMENT_SCHEMA);
		}
	}

	@Override
	public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
		checkDocument(doc);
		return new QuestionStateChangeImpl(doc);
	}
}
