package fr.dila.reponses.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.core.recherche.IndexationImpl;

public class IndexationAdapterFactory implements DocumentAdapterFactory {
    protected void checkDocument(DocumentModel doc) {
      if (!doc.hasSchema(DossierConstants.INDEXATION_DOCUMENT_SCHEMA)) {
            throw new CaseManagementRuntimeException(
                    "Document should contain schema " + DossierConstants.INDEXATION_DOCUMENT_SCHEMA +" ,your type :  :" + doc.getType());
        }
    }

    @Override
    public Object getAdapter(DocumentModel doc,@SuppressWarnings("rawtypes") Class arg1) {
        checkDocument(doc);
        return new IndexationImpl(doc);
    }
}
