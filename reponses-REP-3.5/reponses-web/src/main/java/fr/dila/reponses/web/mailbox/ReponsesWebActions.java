package fr.dila.reponses.web.mailbox;

import static org.jboss.seam.ScopeType.CONVERSATION;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.webapp.action.WebActionsBean;

@Name("webActions")
@Scope(CONVERSATION)
@Install(precedence = Install.FRAMEWORK + 1)
public class ReponsesWebActions extends WebActionsBean {
    public static final String TAB_DOSSIER_PARAPHEUR = "TAB_DOSSIER_PARAPHEUR";
    private static final long serialVersionUID = -8259398272182336442L;

    protected Action getDefaultTab() {
        if (getTabsList() == null) {
            return null;
        }
        try {
            //L'onglet par défaut est celui du parapheur (le second) pour l'affichage d'un dossier
            if(tabsActionsList.get(1).getId().equals(TAB_DOSSIER_PARAPHEUR)) {
                return tabsActionsList.get(1);
            } else {
                return tabsActionsList.get(0);
            }
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
    
}
