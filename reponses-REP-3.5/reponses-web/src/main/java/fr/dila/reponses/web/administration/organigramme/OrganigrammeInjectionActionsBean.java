package fr.dila.reponses.web.administration.organigramme;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.core.util.ExcelUtil;
import fr.dila.ss.api.client.InjectionGvtDTO;
import fr.dila.st.api.constant.STViewConstant;
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
@Install(precedence = Install.APPLICATION + 1)
public class OrganigrammeInjectionActionsBean extends fr.dila.ss.web.admin.organigramme.OrganigrammeInjectionActionsBean {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    private static final STLogger LOGGER = STLogFactory.getLog(OrganigrammeInjectionActionsBean.class);
    
    /**
     * Action préliminaire à l'injection : création du DTO
     * 
     * @return
     */
    public String preparerInjection() {
        LOGGER.info(documentManager, STLogEnumImpl.PROCESS_INJECTION_GOUVERNEMENT_TEC);
        resetErrorProperties();
        if (getCurrentFile() == null) {
            setErrorName(resourcesAccessor.getMessages().get(ERROR_MSG_NO_FILE_SELECTED));
            facesMessages.add(StatusMessage.Severity.ERROR, resourcesAccessor.getMessages().get(ERROR_MSG_NO_FILE_SELECTED));
            setNoPageError(false);
        } else {
            try {
                // Récupération du DTO d'injection
            	listInjection = ReponsesServiceLocator.getReponsesInjectionGouvernementService().prepareInjection(documentManager, currentFile);
                
            	// on réinitialise les variables de l'édition de fichier
                resetProperties();
                if (listInjection == null) {
                	throw new ClientException("Aucun gouvernement n'a été récupéré : erreur lors de la lecture du fichier");
                }
                return "view_injection";
            } catch (final Exception exc) {
                LOGGER.error(documentManager, STLogEnumImpl.FAIL_PROCESS_INJECTION_GOUVERNEMENT_TEC, exc.getMessage());
                setNoPageError(false);
                setErrorName(exc.getMessage());
                facesMessages.add(StatusMessage.Severity.ERROR, resourcesAccessor.getMessages().get("ss.organigramme.injection.ajout.echec"));
            } 
        }
        return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
    }
    
    protected static HttpServletResponse getHttpServletResponse() {
        ServletResponse response = null;
        final FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            response = (ServletResponse) facesContext.getExternalContext().getResponse();
        }
        if (response != null && response instanceof HttpServletResponse) {
            return (HttpServletResponse) response;
        }
        return null;
    }
    
    /**
     * Exporte le gouvernement dans un fichier Excel .xls
     */
    public void exportGouvernement() {
		HttpServletResponse response = getHttpServletResponse();
        if (response == null) {
            return;
        }

        response.reset();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");

        response.addHeader("Content-Disposition", "attachment; filename=\"gouvernement.xls\"");
        
        OutputStream outputStream;
        try {
	        // récupération réponse
	        outputStream = response.getOutputStream();
	        InputStream inputStream = ExcelUtil.createExportGvt(documentManager).getInputStream();
	        BufferedInputStream fif = new BufferedInputStream(inputStream);
	        // copie le fichier dans le flux de sortie
	        int data;
	        while ((data = fif.read()) != -1) {
	            outputStream.write(data);
	        }
	        outputStream.flush();
	        outputStream.close();
	        FacesContext.getCurrentInstance().responseComplete();
    	} catch (IOException e) {
    		LOGGER.error(documentManager, STLogEnumImpl.FAIL_CREATE_EXCEL_TEC, "Erreur lors de la réponse",e);
    	}
    }
    
    public InjectionGvtDTO getNewGovernment() {
    	return ReponsesServiceLocator.getReponsesInjectionGouvernementService().getNewGovernment(listInjection);
    }
    
    public List<InjectionGvtDTO> getAllNewEntities() {
    	return ReponsesServiceLocator.getReponsesInjectionGouvernementService().getAllNewEntities(listInjection);
    }
    
    public String executeInjection() {
    	try {
    		ReponsesServiceLocator.getReponsesInjectionGouvernementService().executeInjection(documentManager,listInjection);
    		facesMessages.add(StatusMessage.Severity.INFO, resourcesAccessor.getMessages().get("ss.organigramme.injection.ajout.succes"));
        	Events.instance().raiseEvent(OrganigrammeTreeBean.ORGANIGRAMME_CHANGED_EVENT);
    	} catch (ClientException e) {
    		LOGGER.error(documentManager, STLogEnumImpl.FAIL_PROCESS_INJECTION_GOUVERNEMENT_TEC, e);
    		facesMessages.add(StatusMessage.Severity.ERROR, resourcesAccessor.getMessages().get("ss.organigramme.injection.ajout.echec"));
    	}
    	return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
    }
    
    public String cancelInjection() {
    	resetProperties();
    	resetErrorProperties();
    	setListInjection(null);
    	return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
    }
}
