package fr.dila.reponses.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.reponses.api.constant.ReponsesLoggingConstant;
import fr.dila.reponses.core.logging.ReponsesLoggingLineDetailImpl;

/**
 * Adapteur de DocumentModel vers Allotissement.
 * 
 * @author asatre
 */
public class ReponsesLoggingLineDetailAdapterFactory implements DocumentAdapterFactory {

    protected void checkDocument(DocumentModel doc) {
        if (!doc.hasSchema(ReponsesLoggingConstant.REPONSES_LOGGING_DETAIL_SHEMA)) {
            throw new CaseManagementRuntimeException("Document should contain schema " + ReponsesLoggingConstant.REPONSES_LOGGING_DETAIL_SHEMA);
        }
    }

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocument(doc);
        return new ReponsesLoggingLineDetailImpl(doc);
    }

}
