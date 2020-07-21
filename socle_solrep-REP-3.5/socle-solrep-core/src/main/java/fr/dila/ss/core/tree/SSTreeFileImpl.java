package fr.dila.ss.core.tree;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.ss.api.tree.SSTreeFile;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.util.PropertyUtil;

public class SSTreeFileImpl extends SSTreeNodeImpl implements SSTreeFile {

    /**
     * Serial UID
     */
    private static final long serialVersionUID = -8231018847656537811L;

    public SSTreeFileImpl(DocumentModel doc) {
        super(doc);
    }

    @Override
    public String getFilename() {
        return PropertyUtil.getStringProperty(document,
                STSchemaConstant.FILE_SCHEMA,
                STSchemaConstant.FILE_FILENAME_PROPERTY);
    }

    @Override
    public void setFilename(String filename) {
        PropertyUtil.setProperty(document,
                STSchemaConstant.FILE_SCHEMA,
                STSchemaConstant.FILE_FILENAME_PROPERTY,
                filename);
    }

    @Override
    public Blob getContent() {
        return getBlobProperty(STSchemaConstant.FILE_SCHEMA, 
                STSchemaConstant.FILE_CONTENT_PROPERTY);
    }

    @Override
    public void setContent(Blob content) {
        setProperty(STSchemaConstant.FILE_SCHEMA, 
                STSchemaConstant.FILE_CONTENT_PROPERTY, 
                content);

    }

    @Override
    public Long getMajorVersion() {
        return getLongProperty(STSchemaConstant.UID_SCHEMA, STSchemaConstant.UID_MAJOR_VERSION_PROPERTY);
    }

    @Override
    public String getFileMimeType() {
        return getStringProperty(STSchemaConstant.FILE_SCHEMA, STSchemaConstant.FILE_CONTENT_MIME_TYPE_PROPERTY);
    }
}
