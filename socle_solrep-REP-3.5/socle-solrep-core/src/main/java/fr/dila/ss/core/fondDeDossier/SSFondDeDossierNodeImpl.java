package fr.dila.ss.core.fondDeDossier;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.ss.api.fondDeDossier.SSFondDeDossierNode;
import fr.dila.ss.core.tree.SSTreeNodeImpl;

public class SSFondDeDossierNodeImpl extends SSTreeNodeImpl implements SSFondDeDossierNode {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public SSFondDeDossierNodeImpl(DocumentModel doc) {
        super(doc);
    }

}
