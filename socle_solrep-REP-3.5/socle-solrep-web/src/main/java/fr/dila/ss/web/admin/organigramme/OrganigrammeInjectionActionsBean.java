package fr.dila.ss.web.admin.organigramme;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import fr.dila.ss.api.client.InjectionGvtDTO;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.web.administration.organigramme.OrganigrammeTreeBean;

/**
 * Injection de gouvernement.
 * 
 * @author jbrunet
 */
@Name("organigrammeInjectionActions")
@Scope(ScopeType.CONVERSATION)
public class OrganigrammeInjectionActionsBean implements Serializable {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    private static final STLogger LOGGER = STLogFactory.getLog(OrganigrammeInjectionActionsBean.class);
    
    @In(create = true, required = false)
    protected CoreSession documentManager;
    
    @In(create = true, required = false)
    protected OrganigrammeManagerActionsBean organigrammeManagerActionsBean;
    
    @In(create = true, required = false)
    protected FacesMessages facesMessages;
    
    @In(create = true)
    protected ResourcesAccessor resourcesAccessor;
    
    @In(create = true)
    protected OrganigrammeTreeBean organigrammeTree;
    
    protected Boolean noPageError;

    protected String errorName;
    
    protected List<String> listErrorName;
    
    protected String removeFileName;
    
    protected File currentFile;
    
    protected List<InjectionGvtDTO> listInjection;
    
    public static final String ERROR_MSG_NO_FILE_SELECTED = "feedback.ss.document.tree.document.error.unselected.file";
    
    /**
     * Constructeur de OrganigrammeSuggestionActionsBean.
     */
    public OrganigrammeInjectionActionsBean() {
    	noPageError = true;
    }

    public void resetErrorProperties() {
        setNoPageError(false);
        setErrorName(null);
        setListErrorName(null);
    }
    
    /**
     * Reset the temp properties
     * 
     */
    public void resetProperties() {
    	setCurrentFile(null);
    }
    
    protected boolean checkUploadAvailable(UploadEvent event) {
        if (event == null || event.getUploadItem() == null || event.getUploadItem().getFileName() == null) {
            setErrorName("le fichier est vide !");
            setNoPageError(false);
            resetProperties();
            return false;
        }
        
        return true;
    }
    
    /**
     * Listener sur l'upload d'un fichier.
     * 
     */
    public void fileUploadListener(UploadEvent event) throws Exception {
        LOGGER.debug(documentManager, STLogEnumImpl.CREATE_FILE_FONC);
        resetErrorProperties();
        
        if (!checkUploadAvailable(event)) {
            return;
        }
                
        if (errorName == null || errorName.isEmpty()) {
            setNoPageError(true);
            setListErrorName(null);
            // get file info
            UploadItem item = event.getUploadItem();
            // get File
            currentFile = item.getFile();
        }
    }
    
    public void clearUploadData(ActionEvent event) {
    	resetProperties();
    }

	public File getCurrentFile() {
		return currentFile;
	}

	public void setCurrentFile(File currentFile) {
		this.currentFile = currentFile;
	}

	public Boolean getNoPageError() {
		return noPageError;
	}

	public void setNoPageError(Boolean noPageError) {
		this.noPageError = noPageError;
	}

	public String getErrorName() {
		return errorName;
	}

	public void setErrorName(String errorName) {
		this.errorName = errorName;
	}

	public List<String> getListErrorName() {
		return listErrorName;
	}

	public void setListErrorName(List<String> listErrorName) {
		this.listErrorName = listErrorName;
	}

	public String getRemoveFileName() {
		return removeFileName;
	}

	public void setRemoveFileName(String removeFileName) {
		this.removeFileName = removeFileName;
	}

	public List<InjectionGvtDTO> getListInjection() {
		return listInjection;
	}

	public void setListInjection(List<InjectionGvtDTO> listInjection) {
		this.listInjection = listInjection;
	}
}
