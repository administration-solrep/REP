package fr.dila.reponses.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.reponses.api.constant.ReponsesSchemaConstant;
import fr.dila.reponses.core.notification.WsNotificationImpl;

public class WsNotificationAdapterFactory implements DocumentAdapterFactory {
    protected void checkDocument(DocumentModel doc) {
        if (!doc.hasSchema(ReponsesSchemaConstant.NOTIFICATION_SCHEMA)) {
            throw new CaseManagementRuntimeException("Document should contain schema " + ReponsesSchemaConstant.NOTIFICATION_SCHEMA);
        }
    }

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocument(doc);
        return new WsNotificationImpl(doc);
    }
}
