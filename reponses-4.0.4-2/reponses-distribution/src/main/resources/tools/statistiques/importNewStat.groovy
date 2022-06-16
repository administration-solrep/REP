import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.platform.reporting.api.Constants;
import org.nuxeo.ecm.platform.reporting.api.ReportModel;
import org.nuxeo.ecm.platform.ui.web.util.files.FileUtils;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.util.PropertyUtil;
import fr.dila.st.core.util.StringUtil;

class ImportUtils {
	
	// ** Propriétés du fichier properties utile à l'import
	private static final String ID_PROPERTY = "idStat";
	private static final String NAME_PROPERTY = "nameStat";
	private static final String TITLE_PROPERTY = "titleStat";
	private static final String DESCRIPTION_PROPERTY = "descriptionStat";
	private static final String RESTRICTION_SGG_PROPERTY = "droitVisibiliteRestraintSGGStat";
	private static final String PARAMETRE_ORGA_PROPERTY = "parametreOrganigrammeStat";
	private static final String REPORT_PATH_PROPERTY = "reportPathStat";
	
	// ** Constantes birt / nuxeo
	private static final String BIRT_MODEL_SCHEMA = "birtreportmodel";
	private static final String PARENT_PATH = "/case-management/birt-model-route";
	
	private static Properties getProperties(filePropertiesPath) {
		Properties prop = new Properties();
		File fileProperties = new File(filePropertiesPath);
		InputStream input = null;
		Reader reader = null;
		try {
			input = new FileInputStream(fileProperties);
			reader = new InputStreamReader(input, "UTF-8");
			prop.load(reader);
		} catch (IOException exc) {
			print "Exception apparue lors de la récupération des proprietés : " + exc.getMessage();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException exc) {
					print "Exception apparue lors de la fermeture du reader : " + exc.getMessage();
				}
			}
			if (input != null) {
				try {
					input.close();
				} catch (IOException exc) {
					print "Exception apparue lors de la fermeture de l'input : " + exc.getMessage();
				}
			}
		}
		return prop;
	}
	
	private static boolean checkProperties(properties) {
		if (properties.isEmpty()) {
        	print "Aucune proprieté n'a pu être récupérée";
        	return false;
        }

        if (properties.getProperty(ID_PROPERTY) == null) {
        	print "Propriété idStat obligatoire manquante";
        	return false;
        }
        
        if (properties.getProperty(NAME_PROPERTY) == null) {
        	print "Propriété nameStat obligatoire manquante";
        	return false;
        }
        
        if (properties.getProperty(TITLE_PROPERTY) == null) {
        	print "Propriété titleStat obligatoire manquante";
        	return false;
        }
        
        if (properties.getProperty(REPORT_PATH_PROPERTY) == null) {
        	print "Propriété reportPathStat obligatoire manquante";
        	return false;
        }
        
        return true;
	}
	
	private static DocumentModel createBirtModel(session, prop) throws IOException {
		// Init doc
		DocumentModel newChild = session.createDocumentModel("BirtReportModel");
		
		// Set nuxeo properties
        newChild.setPathInfo(PARENT_PATH, prop.getProperty(ID_PROPERTY));        
        DublincoreSchemaUtils.setTitle(newChild, prop.getProperty(TITLE_PROPERTY));
        DublincoreSchemaUtils.setDescription(newChild, prop.getProperty(DESCRIPTION_PROPERTY));
        PropertyUtil.setProperty(newChild, BIRT_MODEL_SCHEMA, "reportName", prop.getProperty(NAME_PROPERTY));
        if (prop.getProperty(RESTRICTION_SGG_PROPERTY) != null) {
        	PropertyUtil.setProperty(newChild, BIRT_MODEL_SCHEMA, "droitVisibiliteRestraintSGG", Boolean.valueOf(prop.getProperty(RESTRICTION_SGG_PROPERTY)));
        }
        if (prop.contains(PARAMETRE_ORGA_PROPERTY) != null) {
        	PropertyUtil.setProperty(newChild, BIRT_MODEL_SCHEMA, "parametreOrganigramme", prop.getProperty(PARAMETRE_ORGA_PROPERTY));
        }
        
        newChild.setProperty(STSchemaConstant.FILE_SCHEMA,STSchemaConstant.FILE_FILENAME_PROPERTY, prop.getProperty(NAME_PROPERTY));
        
        // set rptdesig file
        File rptDesignFile = new File(prop.getProperty(REPORT_PATH_PROPERTY));
		InputStream input = null;
		try {
			input = new FileInputStream(rptDesignFile);
			Blob  blob = FileUtils.createSerializableBlob(input, prop.get(NAME_PROPERTY), null);
			newChild.setProperty(STSchemaConstant.FILE_SCHEMA, STSchemaConstant.FILE_CONTENT_PROPERTY, blob);
		} catch (IOException exc) {
			print "Exception apparue lors de la récupération du fichier rptDesign : " + exc.getMessage();
			throw exc;
		} finally {
			try {
				input.close();
			} catch (IOException exc) {
				print "Exception apparue lors de la fermeture de l'input : " + exc.getMessage();
				throw exc;
			}
		}
		
		// creation
        newChild = session.createDocument(newChild);
        session.save();
        return newChild;
	}
	
	private static void createBirtInstance(session, prop, parentId) {
		DocumentModel instance = session.createDocumentModel(Constants.BIRT_REPORT_INSTANCE_TYPE);
        instance.setPathInfo(PARENT_PATH, "BirtReportInstance");
        DublincoreSchemaUtils.setTitle(instance, prop.getProperty(TITLE_PROPERTY));        
        instance.setPropertyValue("birtreport:modelRef", parentId);
        session.createDocument(instance);
        session.save();
	}
	
	public static boolean importStat(session, fileProperties) {
		// Récupération des propriétés
		Properties prop = getProperties(fileProperties);
        
		if (!checkProperties(prop)) {
			return false;
		}		
        
		try {
			// Creation du model birt
			DocumentModel statModelDoc = createBirtModel(session, prop);
			ReportModel model = statModelDoc.getAdapter(ReportModel.class);
			// Creation de l'instance
			createBirtInstance(session, prop, model.getId());
		} catch (IOException exc) {
			return false;
		}
		
        return true;
		
	}
	
}


print "Début script groovy d'import d'une nouvelle statistique";
print "-------------------------------------------------------------------------------";
String fileProperties = Context.get("fileProperties");

if (StringUtil.isBlank(fileProperties)) {
	print "Argument fileProperties non trouvé.";
	print "-------------------------------------------------------------------------------";
    print "Fin du script - Une erreur a provoqué son arrêt avant sa fin";
	return "Fin du script - Une erreur a provoqué son arrêt avant sa fin";
}
fileProperties = fileProperties.replace("'", "");


if (!ImportUtils.importStat (Session, fileProperties)) {
    print "-------------------------------------------------------------------------------";
    print "Fin du script - Une erreur a provoqué son arrêt avant sa fin";
    return "Fin du script - Une erreur a provoqué son arrêt avant sa fin";
}
 
print "-------------------------------------------------------------------------------";
print "Fin du script groovy d'import d'une nouvelle statistique";
return "Fin du script groovy ";
