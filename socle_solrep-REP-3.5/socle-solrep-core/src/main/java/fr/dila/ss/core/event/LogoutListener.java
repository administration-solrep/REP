package fr.dila.ss.core.event;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.usermanager.UserManager;

import fr.dila.ss.api.service.SSUtilisateurConnectionMonitorService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.event.AbstractLogEventListener;
import fr.dila.st.core.service.STServiceLocator;


/**
 * Gestionnaire d'évènements executé à la déconnexion de l'utilisateur :
 * - Déverrouille les documents vérrouillés automatiquement par l'utilisateur.
 *  
 * @author jtremeaux
 */
public class LogoutListener extends AbstractLogEventListener {

    private static final Log log = LogFactory.getLog(LogoutListener.class);

    public LogoutListener(){
		super(LOGOUT_EVENT_NAME);
	}
    
    @Override
    protected void processLogin(final CoreSession session,  final Set<String> principals) throws ClientException {
        try {
        	final SSUtilisateurConnectionMonitorService utilisateurConnectionMonitorService = SSServiceLocator.getUtilisateurConnectionMonitorService();        	
        	final UserManager userManager = STServiceLocator.getUserManager();
        	for(String username : principals){
        		final DocumentModel userDoc = userManager.getUserModel(username);
        		final STUser stUser = userDoc.getAdapter(STUser.class);        
        		utilisateurConnectionMonitorService.updateInfoUtilisateurConnection(session, stUser, true);
        	}
        } catch (Exception e) {
            log.error("Impossible d'associer les groupes à l'utilisateur connecté.", e);
        }
    }
}
