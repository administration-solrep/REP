package fr.dila.reponses.web.helper;

import static org.jboss.seam.ScopeType.SESSION;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.platform.actions.ejb.ActionManager;
import org.nuxeo.ecm.webapp.helpers.StartupHelper;

import fr.dila.cm.web.mailbox.CaseManagementMailboxActionsBean;
import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.reponses.api.constant.ReponsesViewConstant;
import fr.dila.reponses.api.service.ProfilUtilisateurService;
import fr.dila.reponses.core.service.ReponsesServiceLocator;
import fr.dila.reponses.web.corbeille.CorbeilleActionsBean;
import fr.dila.ss.web.admin.utilisateur.UserManagerActionsBean;
import fr.dila.st.api.administration.EtatApplication;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.service.EtatApplicationService;
import fr.dila.st.api.service.STUserService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.web.action.NavigationWebActionsBean;

/**
 * Overwrite CaseManagementStartupHelper to provide custom startup page for casemanagement.
 * 
 * @author FEO
 */
@Name("startupHelper")
@Scope(SESSION)
@Install(precedence = Install.DEPLOYMENT + 1)
public class ReponsesStartupHelper extends StartupHelper {

	private static final long								serialVersionUID	= -3606085944027894437L;

	private static final Log								log					= LogFactory
																						.getLog(ReponsesStartupHelper.class);

	@In(create = true)
	protected transient CaseManagementMailboxActionsBean	cmMailboxActions;

	@In(create = true)
	protected transient NuxeoPrincipal						currentNuxeoPrincipal;

	@In(create = true, required = false)
	protected transient ActionManager						actionManager;

	@In(create = true)
	protected transient NavigationWebActionsBean			navigationWebActions;

	@In(create = true)
	protected transient UserManagerActionsBean				userManagerActions;

	@In(create = true)
	protected transient CorbeilleActionsBean				corbeilleActions;

	@Override
	public String initServerAndFindStartupPage() throws ClientException {
		super.initServerAndFindStartupPage();
		initCurrentDomain();
		try {
			final String user = currentNuxeoPrincipal.getName();
			final STUserService userService = STServiceLocator.getSTUserService();
			final ProfilUtilisateurService profilUtilisateurService = ReponsesServiceLocator
					.getProfilUtilisateurService();

			// Vérification de la date de dernier changement de mot de passe par rapport au paramètre
			if (profilUtilisateurService.isUserPasswordOutdated(documentManager, user)) {
				userService.forceChangeOutdatedPassword(user);
			}

			// Navigue vers l'écran permettant à l'utilisateur de changer son mot de passe
			if (userService.isUserPasswordResetNeeded(user)) {
				return userManagerActions.resetCurrentUserPassword(user);
			}

			if (!currentNuxeoPrincipal.isMemberOf(STBaseFunctionConstant.INTERFACE_ACCESS)) {
				return navigationWebActions.logout();
			}

			EtatApplicationService etatApplicationService = STServiceLocator.getEtatApplicationService();
			EtatApplication etatApplication = etatApplicationService.getEtatApplicationDocument(documentManager);

			if (etatApplication.getRestrictionAcces()) {
				if (!currentNuxeoPrincipal.isMemberOf(ReponsesBaseFunctionConstant.ADMIN_ACCESS_UNRESTRICTED)) {
					try {
						return navigationWebActions.logout();
					} catch (Exception e) {
						throw new ClientException(e);
					}
				}
			}

			// Navigue vers l'espace de travail
			corbeilleActions.navigateToEspaceTravail();
		} catch (Exception e) {
			log.error("Could not redirect to user mailbox", e);
		}
		return ReponsesViewConstant.ESPACE_CORBEILLE_VIEW;
	}

	protected void initCurrentDomain() throws ClientException {
		// initialize framework context
		if (documentManager == null) {
			documentManager = navigationContext.getOrCreateDocumentManager();
		}
		// get the domains from selected server
		DocumentModel rootDocument = documentManager.getRootDocument();

		if (documentManager.hasPermission(rootDocument.getRef(), SecurityConstants.READ_CHILDREN)) {
			DocumentModelList domains = documentManager.getChildren(rootDocument.getRef());

			if (domains.size() > 0) {
				navigationContext.setCurrentDocument(domains.get(0));
			} else {
				log.warn("No domain found: cannot set current document");
			}
		}
	}
}
