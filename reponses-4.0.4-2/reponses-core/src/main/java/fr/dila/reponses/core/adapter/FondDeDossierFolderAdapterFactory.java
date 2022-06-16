package fr.dila.reponses.core.adapter;

import fr.dila.reponses.api.constant.ReponsesFondDeDossierConstants;
import fr.dila.reponses.core.fonddossier.FondDeDossierFolderImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Adapteur de documents vers FondDeDossierFile.
 *
 * @author jtremeaux
 */
public class FondDeDossierFolderAdapterFactory implements STDocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        checkDocumentType(doc, ReponsesFondDeDossierConstants.FOND_DE_DOSSIER_REPERTOIRE_DOCUMENT_TYPE);
        return new FondDeDossierFolderImpl(doc);
    }
}
