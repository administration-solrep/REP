package fr.dila.reponses.core.adapter;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.core.flux.HasInfosFluxImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 *
 * L'adapteur pour un objet comportant des informations de flux.
 * @author jgomez
 *
 */
public class HasInfoFluxAdapterFactory implements STDocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocumentSchemas(doc, DossierConstants.QUESTION_DOCUMENT_SCHEMA);
        return new HasInfosFluxImpl(doc);
    }
}
