package fr.dila.reponses.core.fonddossier;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.reponses.api.fonddossier.FondDeDossierFolder;
import fr.dila.ss.core.fondDeDossier.SSFondDeDossierFolderImpl;

public class FondDeDossierFolderImpl extends SSFondDeDossierFolderImpl implements FondDeDossierFolder {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public FondDeDossierFolderImpl(DocumentModel doc) {
        super(doc);
    }

}
