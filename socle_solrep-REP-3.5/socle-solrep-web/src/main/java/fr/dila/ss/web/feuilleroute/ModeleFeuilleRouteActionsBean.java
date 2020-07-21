package fr.dila.ss.web.feuilleroute;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.api.WebActions;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.webapp.contentbrowser.DocumentActions;
import org.nuxeo.ecm.webapp.edit.lock.LockActions;
import org.nuxeo.ecm.webapp.helpers.EventNames;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.ecm.platform.routing.api.DocumentRoute;
import fr.dila.ecm.platform.routing.api.DocumentRouteElement;
import fr.dila.ecm.platform.routing.api.DocumentRouteElement.ElementLifeCycleState;
import fr.dila.ecm.platform.routing.api.exception.DocumentRouteAlredayLockedException;
import fr.dila.ecm.platform.routing.api.exception.DocumentRouteNotLockedException;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.api.service.FeuilleRouteModelService;
import fr.dila.ss.core.groupcomputer.MinistereGroupeHelper;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.web.admin.AdministrationActionsBean;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.constant.STViewConstant;
import fr.dila.st.api.feuilleroute.STFeuilleRoute;
import fr.dila.st.api.service.STLockService;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.web.lock.STLockActionsBean;

/**
 * Actions permettant de gérer un modèle de feuille de route dans le socle transverse.
 * 
 * @author jtremeaux
 */
@Name("modeleFeuilleRouteActions")
@Scope(CONVERSATION)
@Install(precedence = Install.FRAMEWORK)
public class ModeleFeuilleRouteActionsBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @In(required = true, create = true)
    protected NavigationContext navigationContext;

    @In(create = true, required = false)
    protected CoreSession documentManager;

    @In(required = true, create = true)
    protected SSPrincipal ssPrincipal;

    @In(create = true, required = false)
    protected FacesMessages facesMessages;

    @In(create = true)
    protected WebActions webActions;

    @In(create = true)
    protected LockActions lockActions;

    @In(create = true)
    protected DocumentActions documentActions;

    @In(create = true)
    protected ResourcesAccessor resourcesAccessor;

    @In(create = true)
    protected List<DocumentModel> relatedRoutes;

    @In(create = true, required = false)
    protected AdministrationActionsBean administrationActions;

    @In(required = false, create = true)
    protected transient DocumentRoutingWebActionsBean routingWebActions;

    @In(create = true)
    protected STLockActionsBean stLockActions;

    protected String relatedRouteModelDocumentId;

    /**
     * Default constructor
     */
    public ModeleFeuilleRouteActionsBean(){
    	// do nothing
    }
    
    @Observer(value = { EventNames.DOCUMENT_CHANGED })
    public void resetRelatedRouteDocumentId() {
        relatedRouteModelDocumentId = null;
    }

    /**
     * Détermine si l'utilisateur a le droit de lecture sur le modèle de feuille de route.
     * 
     * @param doc Modèle de feuille de route
     * @return Droit d'écriture
     * @throws ClientException
     */
    public boolean canUserReadRoute(DocumentModel doc) throws ClientException {
        // Les administrateurs fonctionnels ont le droit de lecture sur tous les modèles
        final List<String> groups = ssPrincipal.getGroups();
        if (groups.contains(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR)) {
            return true;
        }

        /*
         * Les administrateurs ministériels ont le droit de lecture sur les modèles de leur ministère, et sur les modèles communs (ministère non affecté)
         */
        final STFeuilleRoute feuilleRoute = doc.getAdapter(STFeuilleRoute.class);
        final String ministere = feuilleRoute.getMinistere();
        final String groupMinistere = MinistereGroupeHelper.ministereidToGroup(ministere);

        return StringUtils.isBlank(ministere) || groups.contains(groupMinistere);
    }

    /**
     * Détermine si l'utilisateur a le droit d'écriture sur le modèle de feuille de route.
     * 
     * @param doc Modèle de feuille de route
     * @return Droit d'écriture
     * @throws ClientException
     */
    public boolean canUserModifyRoute(DocumentModel doc) throws ClientException {
        // Les modèles en demande de validation ou validés sont non modifiables
        final STFeuilleRoute feuilleRoute = doc.getAdapter(STFeuilleRoute.class);
        if ((feuilleRoute.isDraft() && feuilleRoute.isDemandeValidation()) || feuilleRoute.isValidated() || feuilleRoute.isReady()) {
            return false;
        }

        // Les modèles verrouillés par un autre utilisateur sont non modifiables
        if (!stLockActions.canUserLockDoc(doc)) {
            return false;
        }

        // Les administrateurs fonctionnels ont le droit d'écriture sur tous les modèles
        final List<String> groups = ssPrincipal.getGroups();
        if (groups.contains(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR)) {
            return true;
        } else if (isFeuilleDeRouteCreeParAdminFonctionnel(doc)) {
            // Les administrateurs ministériels ont le droit d'écriture sur les modèles de leur ministère sauf sur ceux crées par un Administrateur fonctionnel
            return false;
        } else {
            final String ministere = feuilleRoute.getMinistere();
            final String groupMinistere = MinistereGroupeHelper.ministereidToGroup(ministere);

            if (StringUtils.isBlank(ministere) || !groups.contains(groupMinistere) || !groups.contains(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_UDPATER)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Détermine si l'utilisateur a le droit de suppression sur le modèle de feuille de route.
     * 
     * @param doc Modèle de feuille de route
     * @return Droit de suppression
     * @throws ClientException
     */
    public boolean canUserDeleteRoute(DocumentModel doc) throws ClientException {
        // Les modèles verrouillés par un autre utilisateur sont non supprimables

        if (!stLockActions.canUserLockDoc(doc)) {
            return false;
        }
        final STFeuilleRoute feuilleRoute = doc.getAdapter(STFeuilleRoute.class);
        final String ministere = feuilleRoute.getMinistere();
        final String groupMinistere = MinistereGroupeHelper.ministereidToGroup(ministere);

        // Les administrateurs fonctionnels ont le droit d'écriture sur tous les modèles
        final List<String> groups = ssPrincipal.getGroups();
        if (groups.contains(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR)) {
            return true;
        } else if (isAdminMinisteriel() && groups.contains(groupMinistere)) {
        	// Les administrateurs ministériels ont le droit d'écriture sur les modèles de leur ministère sauf sur ceux crées par un Administrateur fonctionnel
        	if(isFeuilleDeRouteCreeParAdminFonctionnel(doc)){
        		return false;
        	}
        	else{
        		return true;
        	}
        } else {
            if (StringUtils.isBlank(ministere) || !groups.contains(groupMinistere) || !groups.contains(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_UDPATER)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Détermine si l'utilisateur a le droit de libérer le verrou sur le document.
     * 
     * @return Droit de libérer le verrou
     * @throws ClientException
     */
    public boolean canUserLibererVerrou() throws ClientException {
        // Verifie si le document est verrouillé
        DocumentModel doc = navigationContext.getCurrentDocument();
        if (!stLockActions.isDocumentLockedByAnotherUser(doc)) {
            return false;
        }

        // Les administrateurs fonctionnels ont le droit de déverrouiller tous les documents
        final List<String> groups = ssPrincipal.getGroups();
        return groups.contains(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR);
    }

    /**
     * Retourne vrai si l'utilisateur courant peut créer des feuilles de route.
     * 
     * @return Condition
     * @throws ClientException
     */
    public boolean canCreateRoute() throws ClientException {
        return isAdminFonctionnel();
    }

    /**
     * Retourne vrai si l'utilisateur courant est administrateur fonctionnel.
     * 
     * @return Condition
     * @throws ClientException
     */
    public boolean isAdminFonctionnel() throws ClientException {
        final List<String> groups = ssPrincipal.getGroups();
        boolean isAdminFonctionnel = groups.contains(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR);

        return isAdminFonctionnel;
    }

    /**
     * Retourne vrai si le document est crée par l'adminitrateur fonctionnel.
     * 
     * @param doc
     * @return
     * @throws ClientException
     */
    public boolean isFeuilleDeRouteCreeParAdminFonctionnel(DocumentModel doc) throws ClientException {
        String feuilleDeRouteCreateur = DublincoreSchemaUtils.getCreator(doc);

        UserManager userManager = STServiceLocator.getUserManager();
        SSPrincipal principal = (SSPrincipal) userManager.getPrincipal(feuilleDeRouteCreateur);

        if (principal == null) {
            return false;
        } else {
            return principal.isMemberOf(STBaseFunctionConstant.ADMIN_FONCTIONNEL_GROUP_NAME);
        }
    }

    /**
     * Retourne vrai si l'utilisateur courant est administrateur ministériel.
     * 
     * @return Condition
     * @throws ClientException
     */
    public boolean isAdminMinisteriel() throws ClientException {
        final List<String> groups = ssPrincipal.getGroups();
        boolean isAdminMinisteriel = groups.contains(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_UDPATER);

        return isAdminMinisteriel;
    }

    /**
     * Retourne vrai si l'utilisateur courant peut demander la validation du modèle de feuille de route.
     * 
     * @return Condition
     * @throws ClientException
     */
    public boolean canRequestValidateRoute() throws ClientException {
        DocumentModel doc = navigationContext.getCurrentDocument();
        final STFeuilleRoute feuilleRoute = getRelatedRoute();

        // Les modèles verrouillés par un autre utilisateur sont non modifiables
        if (!stLockActions.isDocumentLockedByCurrentUser(doc)) {
            return false;
        }

        final String lifeCycleState = feuilleRoute.getDocument().getCurrentLifeCycleState();
        boolean isDraft = ElementLifeCycleState.draft.name().equals(lifeCycleState);
        boolean demandeValidation = feuilleRoute.isDemandeValidation();
        final List<String> groups = ssPrincipal.getGroups();
        boolean isAdminMinisteriel = groups.contains(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_UDPATER) && !groups.contains(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR);
        final String ministere = feuilleRoute.getMinistere();

        return isDraft && !demandeValidation && isAdminMinisteriel && !StringUtils.isBlank(ministere);
    }

    /**
     * Retourne vrai si l'utilisateur courant peut annuler la demande de validation du modèle de feuille de route.
     * 
     * @return Condition
     * @throws ClientException
     */
    public boolean canCancelRequestValidateRoute() throws ClientException {
        final STFeuilleRoute feuilleRoute = getRelatedRoute();
        DocumentModel doc = navigationContext.getCurrentDocument();

        // Les modèles verrouillés par un autre utilisateur sont non modifiables
        if (!stLockActions.canUserLockDoc(doc)) {
            return false;
        }

        final String lifeCycleState = feuilleRoute.getDocument().getCurrentLifeCycleState();
        boolean isDraft = ElementLifeCycleState.draft.name().equals(lifeCycleState);
        boolean demandeValidation = feuilleRoute.isDemandeValidation();
        final List<String> groups = ssPrincipal.getGroups();
        boolean isAdminMinisteriel = groups.contains(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_UDPATER) && !groups.contains(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR);

        return isDraft && demandeValidation && isAdminMinisteriel;
    }

    /**
     * Retourne vrai si l'utilisateur courant peut valider le modèle de feuille de route.
     * 
     * @return Condition
     * @throws ClientException
     */
    public boolean canValidateRoute() throws ClientException {
        final STFeuilleRoute feuilleRoute = getRelatedRoute();
        DocumentModel doc = feuilleRoute.getDocument();

        // Les modèles verrouillés par un autre utilisateur sont non modifiables
        if (!stLockActions.isDocumentLockedByCurrentUser(doc)) {
            return false;
        }

        final String lifeCycleState = doc.getCurrentLifeCycleState();
        boolean isDraft = ElementLifeCycleState.draft.name().equals(lifeCycleState);
        final List<String> groups = ssPrincipal.getGroups();
        boolean isAdminFonctionnel = groups.contains(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR);

        return isDraft && isAdminFonctionnel;
    }

    /**
     * Retourne vrai si l'utilisateur courant peut refuser la validation du modèle de feuille de route.
     * 
     * @return Condition
     * @throws ClientException
     */
    public boolean canRefuseValidateRoute() throws ClientException {
        final STFeuilleRoute feuilleRoute = getRelatedRoute();
        DocumentModel doc = feuilleRoute.getDocument();

        // Les modèles verrouillés par un autre utilisateur sont non modifiables
        if (!stLockActions.isDocumentLockedByCurrentUser(doc)) {
            return false;
        }

        final String lifeCycleState = doc.getCurrentLifeCycleState();
        boolean isDraft = ElementLifeCycleState.draft.name().equals(lifeCycleState);
        final List<String> groups = ssPrincipal.getGroups();
        boolean isAdminFonctionnel = groups.contains(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR);
        boolean isDemandeValidation = feuilleRoute.isDemandeValidation();

        return isDraft && isAdminFonctionnel && isDemandeValidation;
    }

    /**
     * Retourne vrai si l'utilisateur courant peut invalider le modèle de feuille de route.
     * 
     * @return Condition
     * @throws ClientException
     */
    public boolean canInvalidateRoute() throws ClientException {
        final STFeuilleRoute feuilleRoute = getRelatedRoute();
        DocumentModel doc = feuilleRoute.getDocument();

        // Les modèles verrouillés par un autre utilisateur sont non modifiables
        if (!stLockActions.isDocumentLockedByCurrentUser(doc)) {
            return false;
        }

        // On peut invalider un modèle de feuille de route uniquement à l'état valider
        final String lifeCycleState = feuilleRoute.getDocument().getCurrentLifeCycleState();
        boolean isValidated = ElementLifeCycleState.validated.name().equals(lifeCycleState);
        if (!isValidated) {
            return false;
        }

        // L'administrateur fonctionnel peut invalider un modèle
        final List<String> groups = ssPrincipal.getGroups();
        boolean isAdminFonctionnel = groups.contains(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR);
        if (isAdminFonctionnel) {
            return true;
        } else {
            // l'administrateur ministériel ne peut modifier que les feuilles de route affecté à ses ministères sauf ceux crées par un Administrateur fonctionnel
            if (isFeuilleDeRouteCreeParAdminFonctionnel(doc)) {
                return false;
            } else {
                final String ministere = feuilleRoute.getMinistere();
                final String groupMinistere = MinistereGroupeHelper.ministereidToGroup(ministere);
                if (!StringUtils.isBlank(ministere) && groups.contains(groupMinistere) && groups.contains(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_UDPATER)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Retourne la feuille de route chargée à partir du document courant.
     * 
     * @return Feuille de route
     */
    public STFeuilleRoute getRelatedRoute() {
        // Retourne le document chargé si c'est une feuille de route
        DocumentModel currentDocument = navigationContext.getCurrentDocument();
        STFeuilleRoute relatedRoute = currentDocument.getAdapter(STFeuilleRoute.class);
        if (relatedRoute != null) {
            return relatedRoute;
        }

        // Si l'elément chargé est un élément de feuille de route, remonte à la route
        DocumentRouteElement relatedRouteElement = currentDocument.getAdapter(DocumentRouteElement.class);
        if (relatedRouteElement != null) {
            DocumentRoute documentRoute = relatedRouteElement.getDocumentRoute(documentManager);
            if (documentRoute == null) {
                return null;
            }
            return documentRoute.getDocument().getAdapter(STFeuilleRoute.class);
        }

        return null;
    }

    /**
     * Libère le verrou et retourne à la liste.
     * 
     * @return Vue
     * @throws ClientException
     */
    public String libererVerrou() throws ClientException {
        final STLockService stLockService = STServiceLocator.getSTLockService();
        DocumentRoute currentRouteModel = getRelatedRoute();
        try {
            stLockService.unlockDocUnrestricted(documentManager, navigationContext.getCurrentDocument());
        } catch (ClientException e) {
            String errorMessage = resourcesAccessor.getMessages().get("st.lock.action.unlock.error");
            facesMessages.add(StatusMessage.Severity.WARN, errorMessage);
            return null;
        }
        Events.instance().raiseEvent(EventNames.DOCUMENT_CHILDREN_CHANGED, currentRouteModel.getDocument());

        // Affiche un message d'information
        String infoMessage = resourcesAccessor.getMessages().get("st.lock.action.unlock.success");
        facesMessages.add(StatusMessage.Severity.INFO, infoMessage);

        return administrationActions.navigateToModeleFeuilleRouteFolder();
    }

    /**
     * Demande la validation du modèle de feuille de route.
     * 
     * @return Vue
     * @throws ClientException
     */
    public String requestValidateRouteModel() throws ClientException {
        DocumentRoute currentRouteModel = getRelatedRoute();
        DocumentModel routeDoc = navigationContext.getCurrentDocument();

        // Vérifie si le document est verrouillé par l'utilisateur en cours
        if (!stLockActions.isDocumentLockedByCurrentUser(routeDoc)) {
            String errorMessage = resourcesAccessor.getMessages().get(STLockActionsBean.LOCK_LOST_ERROR_MSG);
            facesMessages.add(StatusMessage.Severity.WARN, errorMessage);
            return null;
        }

        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
        try {
            documentRoutingService.lockDocumentRoute(currentRouteModel, documentManager);
        } catch (DocumentRouteAlredayLockedException e) {
            String errorMessage = resourcesAccessor.getMessages().get("feedback.casemanagement.document.route.already.locked");
            facesMessages.add(StatusMessage.Severity.WARN, errorMessage);
            return null;
        }
        try {
            documentRoutingService.requestValidateRouteModel(currentRouteModel, documentManager, true);
        } catch (DocumentRouteNotLockedException e) {
            String errorMessage = resourcesAccessor.getMessages().get("feedback.casemanagement.document.route.not.locked");
            facesMessages.add(StatusMessage.Severity.WARN, errorMessage);
            return null;
        }

        try {
            documentRoutingService.sendValidationMail(documentManager, currentRouteModel);
        } catch (Exception e) {
            String errorMessage = resourcesAccessor.getMessages().get("st.feuilleRoute.action.requestValidation.mail.failed");
            facesMessages.add(StatusMessage.Severity.INFO, errorMessage);
        }

        Events.instance().raiseEvent(EventNames.DOCUMENT_CHILDREN_CHANGED, currentRouteModel.getDocument());
        documentRoutingService.unlockDocumentRoute(currentRouteModel, documentManager);

        // Affiche un message d'information
        String infoMessage = resourcesAccessor.getMessages().get("st.feuilleRoute.action.requestValidation.success");
        facesMessages.add(StatusMessage.Severity.INFO, infoMessage);

        return null;
    }

    /**
     * Annule la demande de validation du modèle de feuille de route en cours.
     * 
     * @return Vue
     * @throws ClientException
     */
    public String cancelRequestValidateRouteModel() throws ClientException {
        DocumentModel routeDoc = navigationContext.getCurrentDocument();
        DocumentRoute currentRouteModel = getRelatedRoute();

        // Vérifie si le document est verrouillé par l'utilisateur en cours
        if (!stLockActions.isDocumentLockedByCurrentUser(routeDoc)) {
            String errorMessage = resourcesAccessor.getMessages().get(STLockActionsBean.LOCK_LOST_ERROR_MSG);
            facesMessages.add(StatusMessage.Severity.WARN, errorMessage);
            return null;
        }

        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
        try {
            documentRoutingService.lockDocumentRoute(currentRouteModel, documentManager);
        } catch (DocumentRouteAlredayLockedException e) {
            String errorMessage = resourcesAccessor.getMessages().get("feedback.casemanagement.document.route.already.locked");
            facesMessages.add(StatusMessage.Severity.WARN, errorMessage);
            return null;
        }
        try {
            documentRoutingService.requestValidateRouteModel(currentRouteModel, documentManager, false);
        } catch (DocumentRouteNotLockedException e) {
            String errorMessage = resourcesAccessor.getMessages().get("feedback.casemanagement.document.route.not.locked");
            facesMessages.add(StatusMessage.Severity.WARN, errorMessage);
            return null;
        }
        Events.instance().raiseEvent(EventNames.DOCUMENT_CHILDREN_CHANGED, currentRouteModel.getDocument());
        documentRoutingService.unlockDocumentRoute(currentRouteModel, documentManager);

        // Affiche un message d'information
        String infoMessage = resourcesAccessor.getMessages().get("st.feuilleRoute.action.cancelRequestValidation.success");
        facesMessages.add(StatusMessage.Severity.INFO, infoMessage);

        return null;
    }

    /**
     * Valide la feuille de route.
     * 
     * @return Vue
     * @throws ClientException
     */
    public String validateRouteModel() throws ClientException {
        DocumentModel routeDoc = navigationContext.getCurrentDocument();
        DocumentRoute currentRouteModel = getRelatedRoute();

        // Vérifie si le document est verrouillé par l'utilisateur en cours
        if (!stLockActions.isDocumentLockedByCurrentUser(routeDoc)) {
            String errorMessage = resourcesAccessor.getMessages().get(STLockActionsBean.LOCK_LOST_ERROR_MSG);
            facesMessages.add(StatusMessage.Severity.WARN, errorMessage);
            return null;
        }

        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
        try {
            documentRoutingService.lockDocumentRoute(currentRouteModel, documentManager);
        } catch (DocumentRouteAlredayLockedException e) {
            String errorMessage = resourcesAccessor.getMessages().get("feedback.casemanagement.document.route.already.locked");
            facesMessages.add(StatusMessage.Severity.WARN, errorMessage);
            return null;
        }
        try {
            currentRouteModel = documentRoutingService.validateRouteModel(currentRouteModel, documentManager);
        } catch (DocumentRouteNotLockedException e) {
            String errorMessage = resourcesAccessor.getMessages().get("feedback.casemanagement.document.route.not.locked");
            facesMessages.add(StatusMessage.Severity.WARN, errorMessage);
            return null;
        }
        Events.instance().raiseEvent(EventNames.DOCUMENT_CHILDREN_CHANGED, currentRouteModel.getDocument());

        // Affiche un message d'information
        String infoMessage = resourcesAccessor.getMessages().get("st.feuilleRoute.action.validated.success");
        facesMessages.add(StatusMessage.Severity.INFO, infoMessage);

        return null;
    }

    /**
     * Refuse la validation du modèle de feuille de route.
     * 
     * @return Vue
     * @throws ClientException
     */
    public String refuseValidateRouteModel() throws ClientException {
        DocumentModel routeDoc = navigationContext.getCurrentDocument();
        DocumentRoute currentRouteModel = getRelatedRoute();

        // Vérifie si le document est verrouillé par l'utilisateur en cours
        if (!stLockActions.isDocumentLockedByCurrentUser(routeDoc)) {
            String errorMessage = resourcesAccessor.getMessages().get(STLockActionsBean.LOCK_LOST_ERROR_MSG);
            facesMessages.add(StatusMessage.Severity.WARN, errorMessage);
            return null;
        }

        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
        try {
            documentRoutingService.lockDocumentRoute(currentRouteModel, documentManager);
        } catch (DocumentRouteAlredayLockedException e) {
            String errorMessage = resourcesAccessor.getMessages().get("feedback.casemanagement.document.route.already.locked");
            facesMessages.add(StatusMessage.Severity.WARN, errorMessage);
            return null;
        }
        try {
            documentRoutingService.requestValidateRouteModel(currentRouteModel, documentManager, false);
        } catch (DocumentRouteNotLockedException e) {
            String errorMessage = resourcesAccessor.getMessages().get("feedback.casemanagement.document.route.not.locked");
            facesMessages.add(StatusMessage.Severity.WARN, errorMessage);
            return null;
        }
        Events.instance().raiseEvent(EventNames.DOCUMENT_CHILDREN_CHANGED, currentRouteModel.getDocument());

        // Affiche un message d'information
        String infoMessage = resourcesAccessor.getMessages().get("st.feuilleRoute.action.refuseValidation.success");
        facesMessages.add(StatusMessage.Severity.INFO, infoMessage);

        return null;
    }

    /**
     * Invalide le modèle de feuille de route en cours.
     * 
     * @return Vue
     * @throws ClientException
     */
    public String invalidateRouteModel() throws ClientException {
        DocumentModel routeDoc = navigationContext.getCurrentDocument();
        DocumentRoute currentRouteModel = getRelatedRoute();

        // Vérifie si le document est verrouillé par l'utilisateur en cours
        if (!stLockActions.isDocumentLockedByCurrentUser(routeDoc)) {
            String errorMessage = resourcesAccessor.getMessages().get(STLockActionsBean.LOCK_LOST_ERROR_MSG);
            facesMessages.add(StatusMessage.Severity.WARN, errorMessage);
            return null;
        }

        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
        try {
            documentRoutingService.lockDocumentRoute(currentRouteModel, documentManager);
        } catch (DocumentRouteAlredayLockedException e) {
            String errorMessage = resourcesAccessor.getMessages().get("feedback.casemanagement.document.route.already.locked");
            facesMessages.add(StatusMessage.Severity.WARN, errorMessage);
            return null;
        }
        try {
            documentRoutingService.invalidateRouteModel(currentRouteModel, documentManager);
        } catch (DocumentRouteNotLockedException e) {
            String errorMessage = resourcesAccessor.getMessages().get("feedback.casemanagement.document.route.not.locked");
            facesMessages.add(StatusMessage.Severity.WARN, errorMessage);
            return null;
        }
        Events.instance().raiseEvent(EventNames.DOCUMENT_CHILDREN_CHANGED, currentRouteModel.getDocument());

        // Affiche un message d'information
        String infoMessage = resourcesAccessor.getMessages().get("st.feuilleRoute.action.invalidated.success");
        facesMessages.add(StatusMessage.Severity.INFO, infoMessage);

        return null;
    }

    /**
     * Duplique un modèle de feuille de route.
     * 
     * @return vue
     * @throws ClientException
     */
    public String duplicateRouteModel(DocumentModel doc) throws ClientException {
        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();

        final List<String> groups = ssPrincipal.getGroups();
        boolean isAdminFonctionnel = groups.contains(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR);
        String ministere = null;
        if (!isAdminFonctionnel) {
            // Affecte la nouvelle feuille de route au premier ministère de cet administrateur ministériel
            Set<String> ministereSet = ssPrincipal.getMinistereIdSet();
            if (ministereSet == null || ministereSet.size() <= 0) {
                throw new ClientException("Aucun ministère défini pour cet administrateur ministériel");
            }
            ministere = ministereSet.iterator().next();
        }

        DocumentRoute newFeuilleRoute = documentRoutingService.duplicateRouteModel(documentManager, doc, ministere);
        DocumentModel newDoc = newFeuilleRoute.getDocument();

        // Recharge la liste des modèles de feuilles de route
        DocumentModel parent = documentManager.getDocument(newDoc.getParentRef());
        Events.instance().raiseEvent(EventNames.DOCUMENT_CHILDREN_CHANGED, parent);

        // Charge le nouveau document en édition
        navigationContext.setCurrentDocument(newDoc);

        // Affiche un message d'information
        String infoMessage = resourcesAccessor.getMessages().get("st.feuilleRoute.action.duplicated.success");
        facesMessages.add(StatusMessage.Severity.INFO, infoMessage);

        return documentActions.editDocument();
    }

    /**
     * Retourne le prédicat permettant de filtrer les feuilles de route par ministère.
     * 
     * @return Prédicat NXQL
     * @throws ClientException
     */
    public String getContentViewCriteria() throws ClientException {
        final StringBuilder sb = new StringBuilder(STSchemaConstant.FEUILLE_ROUTE_SCHEMA_PREFIX).append(":").append(STSchemaConstant.FEUILLE_ROUTE_MINISTERE_PROPERTY);

        final FeuilleRouteModelService feuilleRouteModelService = SSServiceLocator.getFeuilleRouteModelService();
        return feuilleRouteModelService.getMinistereCriteria(ssPrincipal, sb.toString());
    }

    /**
     * Crée un modèle de feuille de route.
     * 
     * @return Vue
     * @throws ClientException
     */
    public String saveDocument() throws ClientException {
        DocumentModel changeableDocument = navigationContext.getChangeableDocument();
        STFeuilleRoute route = changeableDocument.getAdapter(STFeuilleRoute.class);

        // Nettoyage des données en entrée
        if (StringUtils.isBlank(route.getMinistere())) {
            route.setMinistere(null);
        }

        // Vérifie l'unicité de l'intitulé de modèle de feuille de route par ministère
        final FeuilleRouteModelService feuilleRouteModelService = SSServiceLocator.getFeuilleRouteModelService();
        if (!feuilleRouteModelService.isIntituleUnique(documentManager, route)) {
            String errorMessage = resourcesAccessor.getMessages().get("st.feuilleRoute.action.unicite.intitule.error");
            facesMessages.add(StatusMessage.Severity.WARN, errorMessage);
            return STViewConstant.ERROR_VIEW;
        }

        return documentActions.saveDocument();
    }

    /**
     * Sauvegarde les modifications du modèle de feuille de route.
     * 
     * @return Vue
     * @throws ClientException
     */
    public String updateDocument() throws ClientException {
        DocumentModel changeableDocument = navigationContext.getChangeableDocument();
        STFeuilleRoute route = changeableDocument.getAdapter(STFeuilleRoute.class);

        // Vérifie si le document est verrouillé par l'utilisateur en cours
        if (!stLockActions.isDocumentLockedByCurrentUser(changeableDocument)) {
            String errorMessage = resourcesAccessor.getMessages().get(STLockActionsBean.LOCK_LOST_ERROR_MSG);
            facesMessages.add(StatusMessage.Severity.WARN, errorMessage);
            return null;
        }

        // Nettoyage des données en entrée
        if (StringUtils.isBlank(route.getMinistere())) {
            route.setMinistere(null);
        }

        // Vérifie l'unicité de l'intitulé de modèle de feuille de route par ministère
        final FeuilleRouteModelService feuilleRouteModelService = SSServiceLocator.getFeuilleRouteModelService();
        if (!feuilleRouteModelService.isIntituleUnique(documentManager, route)) {
            String errorMessage = resourcesAccessor.getMessages().get("st.feuilleRoute.action.unicite.intitule.error");
            facesMessages.add(StatusMessage.Severity.WARN, errorMessage);
            return STViewConstant.ERROR_VIEW;
        }

        documentActions.updateDocument();
        return routingWebActions.getFeuilleRouteView();
    }

    /**
     * Retourne l'id du repertoire des modeles de feuille de route
     */
    public String getFeuilleRouteModelFolderId() throws ClientException {
        final FeuilleRouteModelService feuilleRouteModelService = SSServiceLocator.getFeuilleRouteModelService();
        return feuilleRouteModelService.getFeuilleRouteModelFolder(documentManager).getId();
    }
    
    /**
     * Controle l'accès à la vue correspondante
     * 
     */
    public boolean isAccessAuthorized() {
    	SSPrincipal ssPrincipal = (SSPrincipal) documentManager.getPrincipal();
    	return (ssPrincipal.isAdministrator() || ssPrincipal.isMemberOf(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_UDPATER));
    }

}
