package fr.dila.reponses.rest.management;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.reponses.api.constant.ReponsesBaseFunctionConstant;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.administration.EtatApplication;
import fr.dila.st.api.service.EtatApplicationService;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.xsd.reponses.EnvoyerNotificationRequest;
import fr.sword.xsd.reponses.EnvoyerNotificationResponse;
import fr.sword.xsd.reponses.TraitementStatut;

/**
 * Permet de gerer toutes les operations sur les questions
 */
public class NotificationDelegate extends AbstractDelegate{

    private static Log LOGGER = LogFactory.getLog(NotificationDelegate.class);
	    
	public NotificationDelegate(CoreSession documentManager) {
		super(documentManager);
	}
	
	public EnvoyerNotificationResponse notify(EnvoyerNotificationRequest request) {
	    EnvoyerNotificationResponse response = new EnvoyerNotificationResponse();
	    // Verification de l'accès à l'application
        final EtatApplicationService etatApplicationService = STServiceLocator.getEtatApplicationService();
        SSPrincipal principal = (SSPrincipal) session.getPrincipal();

        try {
            EtatApplication etatApplication = etatApplicationService.getEtatApplicationDocument(session);
            if (etatApplication.getRestrictionAcces() && !principal.isMemberOf(ReponsesBaseFunctionConstant.ADMIN_ACCESS_UNRESTRICTED)) {
                response.setStatutTraitement(TraitementStatut.KO);
                return response;
            }
        } catch (ClientException e) {
            response.setStatutTraitement(TraitementStatut.KO);
            LOGGER.error(INFO_ACCES_APPLI_KO, e);
            return response;
        }
	    response.setStatutTraitement(TraitementStatut.OK);
	    return response;
	}

	
}
