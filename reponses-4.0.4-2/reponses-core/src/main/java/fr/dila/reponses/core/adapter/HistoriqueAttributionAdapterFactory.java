package fr.dila.reponses.core.adapter;

import fr.dila.reponses.api.constant.ReponsesSchemaConstant;
import fr.dila.reponses.core.historique.HistoriqueAttributionImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet métier HistoriqueAttribution de réponses.
 *
 * @author arolin
 */
public class HistoriqueAttributionAdapterFactory implements STDocumentAdapterFactory {

    /**
     * Default constructor
     */
    public HistoriqueAttributionAdapterFactory() {
        // do nothing
    }

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocumentSchemas(doc, ReponsesSchemaConstant.HISTORIQUE_ATTRIBUTION_DOCUMENT_SCHEMA);
        return new HistoriqueAttributionImpl(doc);
    }
}
