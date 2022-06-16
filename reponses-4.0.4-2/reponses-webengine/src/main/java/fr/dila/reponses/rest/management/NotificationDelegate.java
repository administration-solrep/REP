package fr.dila.reponses.rest.management;

import fr.sword.xsd.reponses.EnvoyerNotificationRequest;
import fr.sword.xsd.reponses.EnvoyerNotificationResponse;
import fr.sword.xsd.reponses.TraitementStatut;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Permet de gerer toutes les operations sur les questions
 */
public class NotificationDelegate extends AbstractDelegate {

    public NotificationDelegate(CoreSession documentManager) {
        super(documentManager);
    }

    public EnvoyerNotificationResponse notify(EnvoyerNotificationRequest request) {
        EnvoyerNotificationResponse response = new EnvoyerNotificationResponse();

        Map<String, Object> resultsAccess = checkWebserviceAccess();
        if (!resultsAccess.isEmpty()) {
            response.setStatutTraitement((TraitementStatut) resultsAccess.get(MAP_TRAITEMENT_STATUT));
            return response;
        }

        response.setStatutTraitement(TraitementStatut.OK);
        return response;
    }
}
