package fr.dila.ss.core.tree;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.ss.api.tree.SSTreeFolder;

public class SSTreeFolderImpl extends SSTreeNodeImpl implements SSTreeFolder {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private boolean isEmpty = true;

    public SSTreeFolderImpl(DocumentModel doc) {
        super(doc);
    }
    
    @Override
    public boolean isEmpty() {
        return isEmpty;
    }
    
    @Override
    public void setIsEmpty(boolean isEmpty) {
        this.isEmpty = isEmpty;
    }
}
