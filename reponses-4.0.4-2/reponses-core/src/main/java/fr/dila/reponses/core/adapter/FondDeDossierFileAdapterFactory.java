package fr.dila.reponses.core.adapter;

import fr.dila.reponses.core.fonddossier.FondDeDossierFileImpl;
import fr.dila.ss.api.constant.SSFondDeDossierConstants;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Adapteur de documents vers FondDeDossierFile.
 *
 * @author jtremeaux
 */
public class FondDeDossierFileAdapterFactory implements STDocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        checkDocumentType(doc, SSFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_DOCUMENT_TYPE);
        return new FondDeDossierFileImpl(doc);
    }
}
