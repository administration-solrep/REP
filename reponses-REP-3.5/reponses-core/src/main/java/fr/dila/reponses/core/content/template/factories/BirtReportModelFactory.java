package fr.dila.reponses.core.content.template.factories;

import java.io.InputStream;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.content.template.factories.SimpleTemplateBasedFactory;
import org.nuxeo.ecm.platform.content.template.service.TemplateItemDescriptor;
import org.nuxeo.ecm.platform.reporting.api.Constants;
import org.nuxeo.ecm.platform.reporting.api.ReportModel;
import org.nuxeo.ecm.platform.ui.web.util.files.FileUtils;
import org.nuxeo.runtime.api.Framework;

import fr.dila.reponses.api.constant.ReponsesConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.schema.DublincoreSchemaUtils;

public class BirtReportModelFactory extends SimpleTemplateBasedFactory {

    @Override
    public void createContentStructure(DocumentModel doc) throws ClientException {
        initSession(doc);

        if (doc.isVersion() || !isTargetEmpty(doc)) {
            return;
        }

        setAcl(acl, doc.getRef());

        for (TemplateItemDescriptor item : template) {
            String itemPath = doc.getPathAsString();
            if (item.getPath() != null) {
                itemPath += "/" + item.getPath();
            }
            
            DocumentModel newChild = session.createDocumentModel(itemPath, item.getId(), item.getTypeName());
            
            DublincoreSchemaUtils.setTitle(newChild, item.getTitle());
            DublincoreSchemaUtils.setDescription(newChild, item.getDescription());
            setProperties(item.getProperties(), newChild);
            String reportName = (String)newChild.getProperty("birtreportmodel","reportName");
            newChild.setProperty(STSchemaConstant.FILE_SCHEMA,STSchemaConstant.FILE_FILENAME_PROPERTY, reportName);

            
    		// read the report file
    		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(
    				ReponsesConstant.BIRT_REPORT_DIR+"/"+reportName);
    		if (is == null) {
    			is = Framework.getResourceLoader().getResourceAsStream(
        				ReponsesConstant.BIRT_REPORT_DIR+"/"+reportName);
    		}

            Blob  blob = FileUtils.createSerializableBlob(is, reportName, null);
			newChild.setProperty(STSchemaConstant.FILE_SCHEMA, STSchemaConstant.FILE_CONTENT_PROPERTY, blob);
			
            newChild = session.createDocument(newChild);
            setAcl(item.getAcl(), newChild.getRef());
            
            DocumentModel instance = session.createDocumentModel(itemPath, "BirtReportInstance",Constants.BIRT_REPORT_INSTANCE_TYPE);
            DublincoreSchemaUtils.setTitle(instance, item.getTitle());
            ReportModel model = newChild.getAdapter(ReportModel.class);
            instance.setPropertyValue("birtreport:modelRef", model.getId());
            session.createDocument(instance);

        }
    }
}