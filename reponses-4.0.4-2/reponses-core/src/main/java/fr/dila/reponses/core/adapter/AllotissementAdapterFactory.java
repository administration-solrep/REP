package fr.dila.reponses.core.adapter;

import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.core.cases.AllotissementImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Adapteur de DocumentModel vers Allotissement.
 *
 * @author asatre
 */
public class AllotissementAdapterFactory implements STDocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocumentSchemas(doc, DossierConstants.ALLOTISSEMENT_SCHEMA);
        return new AllotissementImpl(doc);
    }
}
