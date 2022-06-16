package fr.dila.reponses.core.adapter;

import fr.dila.reponses.api.constant.ReponsesLoggingConstant;
import fr.dila.reponses.core.logging.ReponsesLoggingLineImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Adapteur de DocumentModel vers Allotissement.
 *
 * @author asatre
 */
public class ReponsesLoggingLineAdapterFactory implements STDocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocumentSchemas(doc, ReponsesLoggingConstant.REPONSES_LOGGING_LINE_SHEMA);
        return new ReponsesLoggingLineImpl(doc);
    }
}
