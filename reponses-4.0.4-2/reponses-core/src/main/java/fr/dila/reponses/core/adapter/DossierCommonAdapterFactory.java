package fr.dila.reponses.core.adapter;

import fr.dila.reponses.api.cases.DossierCommon;
import fr.dila.reponses.api.constant.ReponsesSchemaConstant;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

public class DossierCommonAdapterFactory implements DocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        Class<? extends DossierCommon> dcclass = ReponsesSchemaConstant.documentTypeMap.get(doc.getType());
        if (dcclass == null) {
            throw new NuxeoException("Type of document [" + doc.getType() + "] not supported for DossierCommon");
        }

        return doc.getAdapter(dcclass);
    }
}
