package fr.dila.reponses.core.adapter;

import fr.dila.reponses.api.constant.ReponsesSchemaConstant;
import fr.dila.reponses.core.cases.flux.QuestionStateChangeImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet métier HistoriqueAttribution de réponses.
 *
 * @author arolin
 */
public class EtatQuestionAdapterFactory implements STDocumentAdapterFactory {

    /**
     * Default constructor
     */
    public EtatQuestionAdapterFactory() {
        // do nothing
    }

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocumentSchemas(doc, ReponsesSchemaConstant.ETAT_QUESTION_DOCUMENT_SCHEMA);
        return new QuestionStateChangeImpl(doc);
    }
}
