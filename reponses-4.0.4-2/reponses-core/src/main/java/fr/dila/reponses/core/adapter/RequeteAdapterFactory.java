package fr.dila.reponses.core.adapter;

import fr.dila.reponses.api.constant.RequeteConstants;
import fr.dila.reponses.core.recherche.RequeteImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * @author jgomez
 */
public class RequeteAdapterFactory implements STDocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocumentSchemas(
            doc,
            RequeteConstants.REQUETE_SIMPLE_SCHEMA,
            RequeteConstants.REQUETE_COMPLEXE_SCHEMA,
            RequeteConstants.REQUETE_METADONNEES_SCHEMA,
            RequeteConstants.REQUETE_TEXTE_INTEGRAL_SCHEMA
        );
        return new RequeteImpl(doc);
    }
}
