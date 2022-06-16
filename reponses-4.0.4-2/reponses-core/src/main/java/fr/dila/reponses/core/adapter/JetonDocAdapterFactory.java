package fr.dila.reponses.core.adapter;

import fr.dila.reponses.core.domain.JetonDocImpl;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet métier JetonDoc de réponses.
 *
 * @author arolin
 */
public class JetonDocAdapterFactory implements STDocumentAdapterFactory {

    /**
     * Default constructor
     */
    public JetonDocAdapterFactory() {
        // do nothing
    }

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        checkDocumentSchemas(doc, STSchemaConstant.JETON_DOCUMENT_SCHEMA);
        return new JetonDocImpl(doc);
    }
}
