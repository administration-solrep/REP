package fr.dila.reponses.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.reponses.api.constant.ReponsesLoggingConstant;
import fr.dila.reponses.core.logging.ReponsesLoggingLineImpl;

/**
 * Adapteur de DocumentModel vers Allotissement.
 * 
 * @author asatre
 */
public class ReponsesLoggingLineAdapterFactory implements DocumentAdapterFactory {

    protected void checkDocument(DocumentModel doc) {
        if (!doc.hasSchema(ReponsesLoggingConstant.REPONSES_LOGGING_LINE_SHEMA)) {
            throw new CaseManagementRuntimeException("Document should contain schema " + ReponsesLoggingConstant.REPONSES_LOGGING_LINE_SHEMA);
        }
    }

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocument(doc);
        return new ReponsesLoggingLineImpl(doc);
    }

}
