package fr.dila.reponses.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.reponses.api.constant.DossierConstants;
import fr.dila.reponses.core.cases.DossierImpl;

/**
 * Adapteur de DocumentModel vers Dossier.
 * 
 * @author arolin
 */
public class DossierAdapterFactory implements DocumentAdapterFactory {

    protected void checkDocument(DocumentModel doc) {
        if (!doc.hasSchema(DossierConstants.DOSSIER_SCHEMA)) {
            throw new CaseManagementRuntimeException("Document should contain schema " + DossierConstants.DOSSIER_SCHEMA);
        }
    }

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        checkDocument(doc);
        return new DossierImpl(doc);
    }

}
