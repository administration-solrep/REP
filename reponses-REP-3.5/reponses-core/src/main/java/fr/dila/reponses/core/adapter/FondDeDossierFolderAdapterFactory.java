package fr.dila.reponses.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.reponses.api.constant.ReponsesFondDeDossierConstants;
import fr.dila.reponses.core.fonddossier.FondDeDossierFolderImpl;

/**
 * Adapteur de documents vers FondDeDossierFile. 
 * 
 * @author jtremeaux
 */
public class FondDeDossierFolderAdapterFactory implements DocumentAdapterFactory {

    protected void checkDocument(DocumentModel doc) {
        if (!ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_DOCUMENT_TYPE.equalsIgnoreCase(doc.getType())) {
            throw new CaseManagementRuntimeException(
                    "Document should contain schema " + ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_DOCUMENT_TYPE);
        }
    }

	@Override
	public Object getAdapter(DocumentModel doc, Class<?> arg1) {
		checkDocument(doc);
		return new FondDeDossierFolderImpl(doc);
	}

}
