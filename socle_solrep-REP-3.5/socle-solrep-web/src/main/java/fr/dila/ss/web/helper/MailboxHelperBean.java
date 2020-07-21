package fr.dila.ss.web.helper;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.ui.web.util.ComponentUtils;
import org.nuxeo.ecm.platform.ui.web.util.SuggestionActionsBean;

import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.service.MailboxService;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Bean Seam permettant de gérer les noms des postes / ministères associés aux Mailbox.
 * 
 * @author FEO
 */
@Name("mailboxHelper")
@Scope(ScopeType.EVENT)
public class MailboxHelperBean extends SuggestionActionsBean {

    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog(MailboxHelperBean.class);

    private static final long serialVersionUID = 1L;

    @In(create = true, required = false)
    protected transient CoreSession documentManager;

    /**
     * Defautl constructor
     */
    public MailboxHelperBean(){
    	super();
    }
    
    /**
     * Retourne le titre de la mailbox poste en fonction de son identifiant technique.
     * 
     * @param mailboxId Identifiant technique de la mailbox (ex: "poste-1234")
     * @return Titre de la mailbox (ex: "Agents BDC")
     */
    public String getMailboxTitleFromId(String mailboxId) {
        // Étapes sans destinataire
        if (StringUtils.isEmpty(mailboxId)) {
            return "";
        }

        // Recherche le titre de la Mailbox
        final MailboxService mailboxService = STServiceLocator.getMailboxService();
        try {
            return mailboxService.getMailboxTitle(documentManager, mailboxId);
        } catch (Exception e) {
            LOG.warn(e);
            return "**nom du poste inconnu**";
        }
    }

    /**
     * Retourne les noms des ministères en fonction de l'identifiant technique de la mailbox.
     * 
     * @param mailboxId Identifiant technique de la mailbox (ex: "poste-1234")
     * @return Noms des ministères (ex. "Mnistère de l'économie, Ministère de l'agriculture").
     */
    public String getMinisteresFromMailboxId(String mailboxId) {
        final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
        return mailboxPosteService.getMinisteresFromMailboxId(mailboxId);
    }

    /**
     * Retourne l'edition des ministères en fonction de l'identifiant technique de la mailbox.
     * 
     * @param mailboxId Identifiant technique de la mailbox (ex: "poste-1234")
     * @return Edition des ministères
     */
    public String getMinisteresEditionFromMailboxId(String mailboxId) {
        final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
        return mailboxPosteService.getMinisteresEditionFromMailboxId(mailboxId);
    }

    @Override
    public void addSingleBoundSelection(ActionEvent event) {
        UIComponent component = event.getComponent();
        if (component == null) {
            return;
        }
        UIComponent base = ComponentUtils.getBase(component);
        EditableValueHolder hiddenSelector = ComponentUtils.getComponent(base, suggestionSelectionHiddenId, EditableValueHolder.class);
        ValueHolder output = ComponentUtils.getComponent(base, suggestionSelectionOutputId, ValueHolder.class);

        if (hiddenSelector != null && output != null) {
            String selectedValue = getSelectedValue();
            output.setValue(getMailboxTitleFromId(selectedValue));
            hiddenSelector.setSubmittedValue(selectedValue);

            // display delete component if needed
            if (suggestionSelectionDeleteId != null) {
                UIComponent deleteComponent = ComponentUtils.getComponent(base, suggestionSelectionDeleteId, UIComponent.class);
                if (deleteComponent != null) {
                    deleteComponent.setRendered(true);
                }
            }
        }
    }
}
