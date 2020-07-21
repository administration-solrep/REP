package fr.dila.reponses.web.utilisateur;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.faces.convert.Converter;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.ui.web.directory.VocabularyEntry;
import org.nuxeo.ecm.webapp.helpers.EventNames;

import fr.dila.reponses.api.constant.ProfilUtilisateurConstants;
import fr.dila.reponses.api.service.ProfilUtilisateurService;
import fr.dila.reponses.api.user.ProfilUtilisateur;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.web.converter.VocabularyEntryConverter;

@Name("profilUtilisateurActions")
@Scope(CONVERSATION)
public class ProfilUtilisateurActionsBean implements Serializable {
    
    /**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -2829355856749157105L;

	private boolean panelDisplayed;

    @In(create = true, required = false)
    protected CoreSession documentManager;

    private String parametreMail;

    private List<VocabularyEntry> sourceValueColumn;

    private List<VocabularyEntry> targetValueColumn;

    private Converter converter;

    private List<String> userColumnId;
    
    public boolean isPanelDisplayed() {
        return panelDisplayed;
    }

    public void setPanelDisplayed(boolean panelDisplayed) throws ClientException {
        this.panelDisplayed = panelDisplayed;
        
        final ProfilUtilisateurService profilUtilisateurService = ReponsesServiceLocator.getProfilUtilisateurService();
        
        ProfilUtilisateur profil = (ProfilUtilisateur) profilUtilisateurService.getProfilUtilisateurForCurrentUser(documentManager);
        parametreMail = profil.getParametreMail();
    }
    
    public String getParametreMail() {
        return parametreMail;
    }

    public void setParametreMail(String parametreMail) {
        if(ProfilUtilisateurConstants.PROFIL_UTILISATEUR_PARAMETRE_MAIL_VALUE_AUTO.equals(parametreMail) ||
                ProfilUtilisateurConstants.PROFIL_UTILISATEUR_PARAMETRE_MAIL_VALUE_JOURNALIER.equals(parametreMail) ||
                ProfilUtilisateurConstants.PROFIL_UTILISATEUR_PARAMETRE_MAIL_VALUE_AUCUN.equals(parametreMail))
        {
            this.parametreMail = parametreMail;
        }
    }
    
    public String save() throws ClientException {
        final ProfilUtilisateurService profilUtilisateurService = ReponsesServiceLocator.getProfilUtilisateurService();
        
        ProfilUtilisateur profil = (ProfilUtilisateur) profilUtilisateurService.getProfilUtilisateurForCurrentUser(documentManager);
        profil.setParametreMail(parametreMail);
        
        List<String> userColumns = new LinkedList<String>();
        for (VocabularyEntry vocabularyEntry : targetValueColumn) {
            userColumns.add(vocabularyEntry.getId());
        }
        profil.setUserColumns(userColumns);
        
        documentManager.saveDocument(profil.getDocument());
        documentManager.save();
        
        userColumnId = null;
        
        Events evtManager = Events.instance();
        evtManager.raiseEvent(EventNames.DOCUMENT_CHANGED); // refresh de la liste
        
        return null;
        
    }
    
    public void cancel() {
        //
    }
    
    public List<VocabularyEntry> getSourceValueColumn() throws ClientException {
        if (sourceValueColumn == null) {
            ProfilUtilisateurService profilUtilisateurService = ReponsesServiceLocator.getProfilUtilisateurService();
            sourceValueColumn = profilUtilisateurService.getVocEntryAllowedColumn(documentManager);
        }
        return sourceValueColumn;
    }

    public void setSourceValueColumn(List<VocabularyEntry> sourceValueColumn) {
        this.sourceValueColumn = sourceValueColumn;
    }

    public List<VocabularyEntry> getTargetValueColumn() throws ClientException {
        if (targetValueColumn == null) {
            ProfilUtilisateurService profilUtilisateurService = ReponsesServiceLocator.getProfilUtilisateurService();
            targetValueColumn = profilUtilisateurService.getVocEntryUserColumn(documentManager);
        }
        return targetValueColumn;
    }

    public void setTargetValueColumn(List<VocabularyEntry> targetValueColumn) {
        this.targetValueColumn = targetValueColumn;
    }
    
    public Converter getColumnConverter() throws ClientException {
        if (converter == null) {
            List<VocabularyEntry> columnVocabularyList = new ArrayList<VocabularyEntry>();
            columnVocabularyList.addAll(getTargetValueColumn());
            columnVocabularyList.addAll(getSourceValueColumn());
            converter = new VocabularyEntryConverter(columnVocabularyList);
        }
        return converter;
    }
    
    public List<String> getUserColumnId() throws ClientException {
        if (userColumnId == null) {
            ProfilUtilisateurService profilUtilisateurService = ReponsesServiceLocator.getProfilUtilisateurService();
            userColumnId = profilUtilisateurService.getUserColumn(documentManager);
        }
        return userColumnId;
    }
    
}
