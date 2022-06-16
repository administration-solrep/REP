package fr.dila.reponses.core.event;

import static fr.dila.reponses.core.service.ReponsesServiceLocator.getLoginManager;
import static fr.dila.ss.core.service.SSServiceLocator.getMailboxPosteService;
import static fr.dila.st.core.service.STServiceLocator.getConfigService;

import fr.dila.reponses.api.constant.ReponsesConfigConstant;
import fr.dila.reponses.api.service.ReponsesMailboxService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.event.AbstractLogEventListener;
import fr.dila.st.core.service.STServiceLocator;
import java.util.Calendar;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.usermanager.UserManager;

/**
 * Gestionnaire d'évènements executé au login de l'utilisateur : - Crée si
 * nécessaire la mailbox personnelle de l'utilisateur ; - Crée si nécessaire les
 * mailbox des postes de l'utilisateur.
 *
 * @author jtremeaux
 */
public class LoginSuccessListener extends AbstractLogEventListener {
    private static final Log log = LogFactory.getLog(LoginSuccessListener.class);

    public LoginSuccessListener() {
        super(LOGGING_SUCCESS_EVENT_NAME);
    }

    @Override
    protected void processLogin(final CoreSession session, final Set<String> principals) {
        for (final String username : principals) {
            try {
                final UserManager userManager = STServiceLocator.getUserManager();
                NuxeoPrincipal principal = userManager.getPrincipal(username);
                if (principal == null || principal.isAdministrator()) {
                    continue;
                }
                final DocumentModel userDoc = principal.getModel();

                // Crée la mailbox personnelle de l'utilisateur
                createPersonalMailbox(session, userDoc);

                // Crée les mailbox des postes de l'utilisateur
                getMailboxPosteService().createPosteMailboxes(session, userDoc);

                // Met à jour la date de la dernière connexion
                getLoginManager().updateUserAfterLogin(session, username);

                if (userDoc != null) {
                    // monitor la date de derniere connexion
                    final STUser stUser = userDoc.getAdapter(STUser.class);
                    stUser.setLogout(false);
                    stUser.setDateDerniereConnexion(Calendar.getInstance());
                    userManager.updateUser(stUser.getDocument());
                }
            } catch (Exception e) {
                log.error("Impossible d'associer les groupes à l'utilisateur connecté.", e);
            }
        }
    }

    /**
     * Crée la mailbox personnelle de l'utilisateur.
     *
     * @param session Session
     * @param userDoc Document utilisateur
     * @throws CaseManagementException
     * @throws Exception
     */
    private void createPersonalMailbox(CoreSession session, DocumentModel userDoc) {
        // Seul l'utilisateur qui crée les dossiers Réponse a droit à une mailbox
        // personnelle
        String dossierOwner = getConfigService().getValue(ReponsesConfigConstant.REPONSES_DOSSIER_OWNER);
        if (userDoc == null || !userDoc.getId().equals(dossierOwner)) {
            return;
        }

        // Crée la mailbox personnelle
        final ReponsesMailboxService mailboxService = ReponsesServiceLocator.getMailboxService();
        if (!mailboxService.hasUserPersonalMailbox(session, userDoc)) {
            mailboxService.createPersonalMailboxes(session, userDoc);
        }
    }
}
