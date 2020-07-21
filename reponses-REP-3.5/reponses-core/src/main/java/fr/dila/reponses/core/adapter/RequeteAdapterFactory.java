package fr.dila.reponses.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.reponses.api.constant.RequeteConstants;
import fr.dila.reponses.core.recherche.RequeteImpl;

/**
 * @author jgomez
 */
public class RequeteAdapterFactory implements DocumentAdapterFactory {

    protected void checkDocument(DocumentModel doc) {
    	if (!(doc.hasSchema(RequeteConstants.REQUETE_SIMPLE_SCHEMA) && doc.hasSchema(RequeteConstants.REQUETE_COMPLEXE_SCHEMA)
    	     && doc.hasSchema(RequeteConstants.REQUETE_METADONNEES_SCHEMA) && doc.hasSchema(RequeteConstants.REQUETE_TEXTE_INTEGRAL_SCHEMA))){
            throw new CaseManagementRuntimeException("Document should contain schemas of Requete");
        }
    }

	@Override
	public Object getAdapter(DocumentModel doc,@SuppressWarnings("rawtypes") Class arg1) {
		checkDocument(doc);
		return new RequeteImpl(doc);
	}

}
