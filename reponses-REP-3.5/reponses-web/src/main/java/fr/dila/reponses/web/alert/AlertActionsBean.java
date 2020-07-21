package fr.dila.reponses.web.alert;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.actions.ejb.ActionManager;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;
import org.nuxeo.ecm.platform.ui.web.api.WebActions;
import org.nuxeo.ecm.platform.userworkspace.web.ejb.UserWorkspaceManagerActionsBean;
import org.nuxeo.ecm.webapp.contentbrowser.DocumentActions;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.reponses.api.service.ReponsesAlertService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.web.suivi.SuiviActionsBean;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.alert.Alert;
import fr.dila.st.api.constant.STAlertConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.SecurityService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.web.alert.STAlertActionsBean;
import fr.dila.st.web.context.NavigationContextBean;

/**
 * 
 * Bean d'alertes.
 * 
 */
@Name("alertActions")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.APPLICATION + 2)
public class AlertActionsBean extends STAlertActionsBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String ACTION_ALERT_ACTIVATED = "alert_activated";

    private static final String ACTION_ALERT_DESACTIVATED = "alert_desactivated";

    @In(create = true, required = true)
    protected DocumentActions documentActions;

    @In(create = true, required = true)
    protected transient NavigationContextBean navigationContext;

    @In(create = true, required = true)
    protected transient SuiviActionsBean suiviActions;

    @In(create = true, required = false)
    protected transient FacesMessages facesMessages;

    @In(create = true)
    protected transient ResourcesAccessor resourcesAccessor;

    @In(create = true, required = true)
    protected transient CoreSession documentManager;

    @In(create = true, required = true)
    protected ContentViewActions contentViewActions;

    @In(create = true, required = true)
    protected transient ActionManager actionManager;

    @In(create = true, required = false)
    protected transient WebActions webActions;

    @In(required = true, create = true)
    protected transient SSPrincipal ssPrincipal;
    
    @In(create = true, required = false)
    protected transient UserWorkspaceManagerActionsBean userWorkspaceManagerActions;

    /**
     * Logger formalisé en surcouche du logger apache/log4j 
     */
    private static final STLogger LOGGER = STLogFactory.getLog(AlertActionsBean.class);

    /**
     * Créé une nouvelle alerte non persistée à partir d'une requete.
     * 
     * @param
     * @return la nouvelle alerte
     * @throws ClientException
     * 
     */
    public String newAlertFromRequeteExperte(DocumentModel requeteExperte) {
        final ReponsesAlertService alertService = ReponsesServiceLocator.getAlertService();
        suiviActions.setCurrentSuiviSubcontainerAction("new_alert");
        LOGGER.debug(documentManager, STLogEnumImpl.CREATE_ALERT_TEC);
        Alert alert = alertService.initAlertFromRequete(documentManager, requeteExperte);
        if (alert == null) {
            LOGGER.error(documentManager, STLogEnumImpl.FAIL_CREATE_ALERT_TEC);
            facesMessages.add(StatusMessage.Severity.ERROR, resourcesAccessor.getMessages().get("alert.messages.error.alertCreatedFailed"));
            return null;
        }
        navigationContext.setChangeableDocument(alert.getDocument());
        return "suivi";
    }

    public String editAlert(DocumentModel alerteDoc) throws ClientException {
        navigationContext.navigateToDocument(alerteDoc);
        suiviActions.setCurrentSuiviSubcontainerAction("new_alert");
        return "suivi";
    }

    /**
     * Sauvegarde l'alerte et l'enregistre dans le service d'alerte
     * 
     * @return
     * @throws Exception
     */
    public String saveAlert() throws Exception {
        DocumentModel docBefore = navigationContext.getCurrentDocument();
        String docType = "";
        String alertAction = null;
        if (docBefore != null) {
        	docType = docBefore.getType();
        }
        if ( STAlertConstant.ALERT_DOCUMENT_TYPE.equals(docType) ) {
        	alertAction = documentActions.updateCurrentDocument();
        } else {
        	alertAction = documentActions.saveDocument();
        
        	DocumentModel docAfter = navigationContext.getCurrentDocument();
        	SecurityService service = STServiceLocator.getSecurityService();
        	service.addAceToAcl(docAfter, ACL.LOCAL_ACL, ssPrincipal.getName(), SecurityConstants.EVERYTHING);
        }
        
        FacesMessages.afterPhase();
        facesMessages.clear();
        facesMessages.add(StatusMessage.Severity.INFO, resourcesAccessor.getMessages().get("alert.messages.info.alertSaved"));
        contentViewActions.refresh("suivi_alert_content");
        suiviActions.setCurrentSuiviSubcontainerAction("new_alert");
        return alertAction;
    }

    /**
     * Met l'alerte en pause
     * 
     * @return
     * @throws ClientException
     */
    @Override
    public String suspend() throws ClientException {
        final ReponsesAlertService alertService = ReponsesServiceLocator.getAlertService();
        alertService.suspendAlert(documentManager, getCurrentAlert());
        facesMessages.add(StatusMessage.Severity.INFO, resourcesAccessor.getMessages().get("alert.messages.info.alertSuspended"));
        contentViewActions.refresh("suivi_alert_content");
        suiviActions.setCurrentSuiviSubcontainerAction("new_alert");
        return ACTION_ALERT_DESACTIVATED;
    }

    /**
     * renvoie vrai si l'alerte est suspendue
     * 
     * @return
     */
    @Override
    public Boolean isSuspended() {
        Alert alert = getCurrentAlert();
        if (alert == null || alert.isActivated() == null) {
            return true;
        }
        return !alert.isActivated();
    }

    /**
     * renvoie vrai si l'alerte est activée
     * 
     * @return
     */
    @Override
    public Boolean isActivated() {
        Alert alert = getCurrentAlert();
        if (alert == null || alert.isActivated() == null) {
            return false;
        }
        return alert.isActivated();
    }

    /**
     * Rend l'alerte active.
     * 
     * @return
     * @throws ClientException
     */
    @Override
    public String activate() throws ClientException {
        final ReponsesAlertService alertService = ReponsesServiceLocator.getAlertService();
        alertService.activateAlert(documentManager, getCurrentAlert());
        facesMessages.add(StatusMessage.Severity.INFO, resourcesAccessor.getMessages().get("alert.messages.info.alertActivated"));
        contentViewActions.refresh("suivi_alert_content");
        suiviActions.setCurrentSuiviSubcontainerAction("new_alert");
        return ACTION_ALERT_ACTIVATED;
    }

    /**
     * Retourne la requête courante.
     * 
     * @return
     */
    @Override
    public Alert getCurrentAlert() {
        DocumentModel alertDoc = navigationContext.getCurrentDocument();
        if (alertDoc == null || !STAlertConstant.ALERT_DOCUMENT_TYPE.equals(alertDoc.getType())) {
            return null;
        }
        Alert alert = alertDoc.getAdapter(Alert.class);
        return alert;
    }

    /**
     * Supprime le document alert
     * 
     * @return
     * @throws ClientException
     */
    @Override
    public String delete(DocumentModel doc) {
        final ReponsesAlertService alertService = ReponsesServiceLocator.getAlertService();
        boolean deleted = alertService.deleteAlert(documentManager, doc);
        if (deleted) {
            contentViewActions.refresh("suivi_alert_content");
            try {
                navigationContext.resetCurrentDocument();
            } catch (ClientException ce) {
                LOGGER.error(documentManager, STLogEnumImpl.FAIL_UPDATE_NAV_CONTEXT_FONC, ce);
            }
        } else {
            LOGGER.error(documentManager, STLogEnumImpl.FAIL_DEL_ALERT_FONC);
            facesMessages.add(StatusMessage.Severity.INFO, resourcesAccessor.getMessages().get("alert.error.alertDeletion"));
        }
        return null;
    }

    /**
     * Retourne l'action qui correspond à l'état de l'alerte.
     * 
     * @return
     * @throws ClientException
     */
    public Action getAlertEtatAction(DocumentRef ref) throws ClientException {
        DocumentModel doc = documentManager.getDocument(ref);
        if (doc == null) {
            return null;
        }
        Alert alert = doc.getAdapter(Alert.class);
        if (alert == null || alert.isActivated()) {
            return actionManager.getAction("etat_alert_isActivated");
        } else {
            return actionManager.getAction("etat_alert_isSuspended");
        }
    }

}
