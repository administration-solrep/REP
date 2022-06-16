package fr.dila.reponses.core.adapter;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.core.recherche.IndexationImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

public class IndexationComplAdapterFactory implements STDocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocumentSchemas(doc, DossierConstants.INDEXATION_COMPLEMENTAIRE_DOCUMENT_SCHEMA);
        return new IndexationImpl(doc, DossierConstants.INDEXATION_COMPLEMENTAIRE_DOCUMENT_SCHEMA);
    }
}
