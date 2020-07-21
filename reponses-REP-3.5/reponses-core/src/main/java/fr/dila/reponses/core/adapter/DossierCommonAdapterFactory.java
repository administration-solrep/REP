package fr.dila.reponses.core.adapter;

import org.nuxeo.ecm.core.api.ClientRuntimeException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.reponses.api.cases.DossierCommon;
import fr.dila.reponses.api.constant.ReponsesSchemaConstant;

public class DossierCommonAdapterFactory implements DocumentAdapterFactory {
    
    @Override
    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        Class<? extends DossierCommon> dcclass = ReponsesSchemaConstant.documentTypeMap.get(doc.getType());
        if(dcclass == null){
            throw new ClientRuntimeException("Type of document ["+doc.getType() + "] not supported for DossierCommon");
        }
        
        return doc.getAdapter(dcclass);
    }
}
