package fr.dila.reponses.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.core.cases.QuestionImpl;

/**
 * Adapteur de document vers Question.
 * 
 * @author arolin
 */
public class QuestionAdapterFactory implements DocumentAdapterFactory {

    protected void checkDocument(DocumentModel doc) {
        if (!doc.hasSchema(DossierConstants.QUESTION_DOCUMENT_SCHEMA)) {
            throw new CaseManagementRuntimeException("Document should contain schema " + DossierConstants.QUESTION_DOCUMENT_SCHEMA);
        }
    }

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocument(doc);
        return new QuestionImpl(doc);
    }

}
