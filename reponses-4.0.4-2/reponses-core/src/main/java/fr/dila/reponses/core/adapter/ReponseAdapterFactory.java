package fr.dila.reponses.core.adapter;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.core.cases.ReponseImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * @author arolin
 */
public class ReponseAdapterFactory implements STDocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocumentSchemas(doc, DossierConstants.REPONSE_DOCUMENT_SCHEMA);
        return new ReponseImpl(doc);
    }
}
