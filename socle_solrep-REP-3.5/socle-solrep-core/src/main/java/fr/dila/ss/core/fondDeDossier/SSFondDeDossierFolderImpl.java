package fr.dila.ss.core.fondDeDossier;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.ss.api.fondDeDossier.SSFondDeDossierFolder;
import fr.dila.ss.core.tree.SSTreeFolderImpl;

public class SSFondDeDossierFolderImpl extends SSTreeFolderImpl implements SSFondDeDossierFolder {

    /**
     * 
     */
    private static final long serialVersionUID = -6499443072170078721L;
    
    public SSFondDeDossierFolderImpl(DocumentModel doc) {
        super(doc);
    }

}
