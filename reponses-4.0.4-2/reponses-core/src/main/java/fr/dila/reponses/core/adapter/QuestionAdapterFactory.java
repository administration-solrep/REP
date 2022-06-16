package fr.dila.reponses.core.adapter;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.core.cases.QuestionImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Adapteur de document vers Question.
 *
 * @author arolin
 */
public class QuestionAdapterFactory implements STDocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocumentSchemas(doc, DossierConstants.QUESTION_DOCUMENT_SCHEMA);
        return new QuestionImpl(doc);
    }
}
