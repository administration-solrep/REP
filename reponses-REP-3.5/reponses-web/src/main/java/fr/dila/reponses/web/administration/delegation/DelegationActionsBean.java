package fr.dila.reponses.web.administration.delegation;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.CoreEventConstants;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.webapp.contentbrowser.DocumentActions;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.reponses.api.constant.ReponsesViewConstant;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STViewConstant;
import fr.dila.st.api.domain.user.Delegation;
import fr.dila.st.api.service.DelegationService;
import fr.dila.st.core.service.STServiceLocator;

/**
 * WebBean permettant de gérer la délégation des droits.
 * 
 * @author jtremeaux
 */
@Name("delegationActions")
@Scope(ScopeType.CONVERSATION)
public class DelegationActionsBean implements Serializable {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 7758881641656108468L;

    @In(create = true, required = true)
    protected transient CoreSession documentManager;

    @In(required = true, create = true)
    protected transient SSPrincipal ssPrincipal;

    @In(create = true, required = false)
    protected transient NavigationContext navigationContext;

    @In(create = true)
    protected DocumentActions documentActions;

    @In(create = true, required = false)
    protected FacesMessages facesMessages;

    @In(create = true)
    protected ResourcesAccessor resourcesAccessor;

    /**
     * Répertoire racine des délégations.
     */
    protected DocumentModel delegationRoot;

    /**
     * Profil sélectionné pour l'ajout d'un profil à une liste de valeurs.
     */
    protected String currentProfile;

    /**
     * Retourne le répertoire racine des délégations.
     * 
     * @return Répertoire racine des délégations
     * @throws ClientException
     */
    public DocumentModel getDelegationRoot() throws ClientException {
        if (delegationRoot == null) {
            final DelegationService delegationService = STServiceLocator.getDelegationService();
            delegationRoot = delegationService.getDelegationRoot(documentManager);
        }

        return delegationRoot;
    }

    /**
     * Retourne l'utilisateur qui délègue ses droits, i.e. l'utilisateur connecté.
     * 
     * @return Utilisateur
     * @throws ClientException
     */
    public DocumentModel getSourceUser() throws ClientException {
        UserManager userManager = STServiceLocator.getUserManager();
        return userManager.getUserModel(ssPrincipal.getName());
    }

    /**
     * Retourne la vue de création d'une délégation.
     */
    public String navigateToCreateDelegation() throws ClientException {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put(CoreEventConstants.PARENT_PATH, navigationContext.getCurrentDocument().getPathAsString());
        DocumentModel delegationDoc = documentManager.createDocumentModel(STConstant.DELEGATION_DOCUMENT_TYPE, context);
        Delegation delegation = delegationDoc.getAdapter(Delegation.class);
        delegation.setSourceId(ssPrincipal.getName());
        navigationContext.setChangeableDocument(delegationDoc);

        return ReponsesViewConstant.CREATE_DELEGATION_VIEW;
    }

    /**
     * Crée une délégation.
     * 
     * @return Vue
     * @throws ClientException
     */
    public String createDelegation() throws ClientException {
        DocumentModel changeableDocument = navigationContext.getChangeableDocument();
        Delegation delegation = changeableDocument.getAdapter(Delegation.class);

        if (!validateDate(delegation.getDateDebut().getTime(), delegation.getDateFin().getTime())) {
            return STViewConstant.ERROR_VIEW;
        }
        // Renseigne le titre du document
        changeableDocument.setPathInfo((String) changeableDocument.getContextData(CoreEventConstants.PARENT_PATH), delegation.getDestinataireId());

        return documentActions.saveDocument();
    }

    /**
     * Sauvegarde les modifications du modèle de feuille de route.
     * 
     * @return Vue
     * @throws ClientException
     */
    public String updateDelegation() throws ClientException {
        DocumentModel changeableDocument = navigationContext.getChangeableDocument();
        Delegation delegation = changeableDocument.getAdapter(Delegation.class);

        if (!validateDate(delegation.getDateDebut().getTime(), delegation.getDateFin().getTime())) {
            return STViewConstant.ERROR_VIEW;
        }
        return documentActions.updateDocument();
    }

    /**
     * Validation des dates
     * 
     * @param startDate
     * @param endDate
     */
    public boolean validateDate(Date startDate, Date endDate) {
        if (!checkDateNotPast(startDate)) {
            facesMessages.add(StatusMessage.Severity.ERROR, resourcesAccessor.getMessages().get("reponses.delegation.start.date.not.valid"));
            return false;
        }
        if (!checkDateStartDateBeforeEndDate(startDate, endDate)) {
            facesMessages.add(StatusMessage.Severity.ERROR, resourcesAccessor.getMessages().get("reponses.delegation.dates.not.valid"));
            return false;
        }
        return true;
    }

    public boolean checkDateNotPast(Date date) {
        Date today = new Date();
        today = DateUtils.truncate(today, Calendar.DAY_OF_MONTH);
        if (date.compareTo(today) < 0) {
            return false;
        }
        return true;
    }

    public boolean checkDateStartDateBeforeEndDate(Date startDate, Date endDate) {
        if (startDate.compareTo(endDate) >= 0) {
            return false;
        }
        return true;
    }

    /**
     * Ajout d'un profil à la liste des profils de la délégation.
     * 
     * @param delegationDoc Document délégation
     */
    public void addProfile(DocumentModel delegationDoc) {
        Delegation delegation = delegationDoc.getAdapter(Delegation.class);
        List<String> profilIdList = delegation.getProfilListId();
        if (!profilIdList.contains(currentProfile)) {
            profilIdList.add(currentProfile);
        }
        delegation.setProfilListId(profilIdList);
    }

    /**
     * Retrait d'un profil à la liste des profils de la délégation.
     * 
     * @param delegationDoc Document délégation
     * @param profile Profil à retirer
     */
    public void removeProfile(DocumentModel delegationDoc, String profile) {
        Delegation delegation = delegationDoc.getAdapter(Delegation.class);
        List<String> profilIdList = delegation.getProfilListId();
        profilIdList.remove(profile);
        delegation.setProfilListId(profilIdList);
    }

    /**
     * Getter de currentProfile.
     * 
     * @return currentProfile
     */
    public String getCurrentProfile() {
        return currentProfile;
    }

    /**
     * Setter de currentProfile.
     * 
     * @param currentProfile currentProfile
     */
    public void setCurrentProfile(String currentProfile) {
        this.currentProfile = currentProfile;
    }
}