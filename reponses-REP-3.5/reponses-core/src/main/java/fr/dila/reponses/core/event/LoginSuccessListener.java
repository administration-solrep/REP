package fr.dila.reponses.core.event;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.usermanager.UserManager;

import fr.dila.cm.exception.CaseManagementException;
import fr.dila.reponses.api.constant.ReponsesConfigConstant;
import fr.dila.reponses.api.service.MailboxService;
import fr.dila.reponses.api.service.ReponsesLoginManager;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.api.service.SSUtilisateurConnectionMonitorService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.event.AbstractLogEventListener;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Gestionnaire d'évènements executé au login de l'utilisateur : - Crée si nécessaire la mailbox personnelle de
 * l'utilisateur ; - Crée si nécessaire les mailbox des postes de l'utilisateur.
 * 
 * @author jtremeaux
 */
public class LoginSuccessListener extends AbstractLogEventListener {

	private static final Log	log	= LogFactory.getLog(LoginSuccessListener.class);

	public LoginSuccessListener() {
		super(LOGGING_SUCCESS_EVENT_NAME);
	}

	@Override
	protected void processLogin(final CoreSession session, final Set<String> principals) throws ClientException {

		final SSUtilisateurConnectionMonitorService utilisateurConnectionMonitorService = SSServiceLocator
				.getUtilisateurConnectionMonitorService();
		for (final String username : principals) {
			try {
				final UserManager userManager = STServiceLocator.getUserManager();

				DocumentModel userDoc = userManager.getUserModel(username);
				// Crée la mailbox personnelle de l'utilisateur
				createPersonalMailbox(session, userDoc);

				// Crée les mailbox des postes de l'utilisateur
				createPosteMailbox(session, userDoc);

				// Met à jour la date de la dernière connexion
				updateLastConnectionDateUser(session, username);

				// monitor la date de derniere connexion
				final STUser stUser = userDoc.getAdapter(STUser.class);
				utilisateurConnectionMonitorService.createOrUpdateInfoUtilisateurConnection(session, stUser);
			} catch (Exception e) {
				log.error("Impossible d'associer les groupes à l'utilisateur connecté.", e);
			}
		}
	}

	/**
	 * Crée la mailbox personnelle de l'utilisateur.
	 * 
	 * @param session
	 *            Session
	 * @param userDoc
	 *            Document utilisateur
	 * @throws CaseManagementException
	 * @throws Exception
	 */
	private void createPersonalMailbox(CoreSession session, DocumentModel userDoc) throws Exception,
			CaseManagementException {
		// Seul l'utilisateur qui crée les dossiers Réponse a droit à une mailbox personnelle
		ConfigService configService = STServiceLocator.getConfigService();
		String dossierOwner = configService.getValue(ReponsesConfigConstant.REPONSES_DOSSIER_OWNER);
		if (!userDoc.getId().equals(dossierOwner)) {
			return;
		}

		// Crée la mailbox personnelle
		final MailboxService mailboxService = ReponsesServiceLocator.getMailboxService();
		if (!mailboxService.hasUserPersonalMailbox(session, userDoc)) {
			mailboxService.createPersonalMailboxes(session, userDoc);
		}
	}

	/**
	 * Crée les mailbox poste de l'utilisateur.
	 * 
	 * @param session
	 *            Session
	 * @param userDoc
	 *            Document utilisateur
	 * @throws CaseManagementException
	 * @throws Exception
	 */
	private void createPosteMailbox(CoreSession session, DocumentModel userDoc) throws CaseManagementException,
			Exception {
		final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
		mailboxPosteService.createPosteMailboxes(session, userDoc);
	}

	/**
	 * Met à jour la date dernière connexion de l'utilisateur.
	 * 
	 * @param session
	 *            Session
	 * @param userDoc
	 *            Document utilisateur
	 * @throws CaseManagementException
	 * @throws Exception
	 */
	private void updateLastConnectionDateUser(CoreSession session, String username) throws CaseManagementException,
			Exception {
		final ReponsesLoginManager reponsesLoginManager = ReponsesServiceLocator.getLoginManager();
		reponsesLoginManager.updateUserAfterLogin(session, username);
	}
}
