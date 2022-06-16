package fr.dila.reponses.core.adapter;

import fr.dila.reponses.api.constant.ReponsesLoggingConstant;
import fr.dila.reponses.core.logging.ReponsesLoggingLineDetailImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Adapteur de DocumentModel vers Allotissement.
 *
 * @author asatre
 */
public class ReponsesLoggingLineDetailAdapterFactory implements STDocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocumentSchemas(doc, ReponsesLoggingConstant.REPONSES_LOGGING_DETAIL_SHEMA);
        return new ReponsesLoggingLineDetailImpl(doc);
    }
}
