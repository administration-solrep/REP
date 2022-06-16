package fr.dila.reponses.core.adapter;

import fr.dila.reponses.api.constant.ReponsesSchemaConstant;
import fr.dila.reponses.core.notification.WsNotificationImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

public class WsNotificationAdapterFactory implements STDocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocumentSchemas(doc, ReponsesSchemaConstant.NOTIFICATION_SCHEMA);
        return new WsNotificationImpl(doc);
    }
}
