package fr.dila.reponses.core.fonddossier;

import fr.dila.reponses.api.fonddossier.FondDeDossierFolder;
import fr.dila.ss.core.fondDeDossier.SSFondDeDossierFolderImpl;
import org.nuxeo.ecm.core.api.DocumentModel;

public class FondDeDossierFolderImpl extends SSFondDeDossierFolderImpl implements FondDeDossierFolder {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public FondDeDossierFolderImpl(DocumentModel doc) {
        super(doc);
    }
}
