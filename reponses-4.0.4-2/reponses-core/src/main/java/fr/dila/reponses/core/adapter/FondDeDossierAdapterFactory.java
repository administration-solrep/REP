package fr.dila.reponses.core.adapter;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.core.cases.FondDeDossierImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * @author arolin
 */
public class FondDeDossierAdapterFactory implements STDocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocumentSchemas(doc, DossierConstants.FOND_DE_DOSSIER_DOCUMENT_SCHEMA);
        return new FondDeDossierImpl(doc);
    }
}
