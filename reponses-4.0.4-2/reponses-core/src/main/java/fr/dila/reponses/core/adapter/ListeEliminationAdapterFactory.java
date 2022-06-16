package fr.dila.reponses.core.adapter;

import fr.dila.reponses.api.constant.ReponsesSchemaConstant;
import fr.dila.reponses.core.archive.ListeEliminationImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

public class ListeEliminationAdapterFactory implements STDocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocumentSchemas(doc, ReponsesSchemaConstant.LISTE_ELIMINATION_SCHEMA);
        return new ListeEliminationImpl(doc);
    }
}
