package fr.dila.ss.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.ss.api.constant.SSInfoUtilisateurConnectionConstants;
import fr.dila.ss.core.documentmodel.SSInfoUtilisateurConnectionImpl;


public class SSInfoUtilisateurConnectionAdapterFactory implements DocumentAdapterFactory {
    
    protected void checkDocument(DocumentModel doc) {
        if (!doc.hasSchema(SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_SCHEMA)) {
            throw new CaseManagementRuntimeException("Document should contain schema " + SSInfoUtilisateurConnectionConstants.INFO_UTILISATEUR_CONNECTION_SCHEMA);
        }
    }

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        checkDocument(doc);
        return new SSInfoUtilisateurConnectionImpl(doc);
    }

}