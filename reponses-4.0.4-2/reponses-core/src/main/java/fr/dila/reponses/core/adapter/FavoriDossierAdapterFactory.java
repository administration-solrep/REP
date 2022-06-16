package fr.dila.reponses.core.adapter;

import fr.dila.reponses.api.constant.ReponsesSchemaConstant;
import fr.dila.reponses.core.favoris.FavoriDossierImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

public class FavoriDossierAdapterFactory implements STDocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocumentSchemas(doc, ReponsesSchemaConstant.FAVORI_DOSSIER_SCHEMA);
        return new FavoriDossierImpl(doc);
    }
}
